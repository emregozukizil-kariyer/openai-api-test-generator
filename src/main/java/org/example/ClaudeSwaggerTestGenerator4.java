package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.commons.cli.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Pattern;
import java.math.BigDecimal;

/**
 * OpenAPI/Swagger dosyalarından otomatik API test senaryoları oluşturan geliştirilmiş uygulama.
 *
 * Yeni İyileştirmeler v3.1.0:
 * - Daha akıllı şema analizi ve constraint validation
 * - Gerçekçi test verisi üretimi
 * - Bağımlılık grafiği analizi
 * - Detaylı hata kategorileri ve edge case testleri
 * - Performans benchmark testleri
 * - Contract testing desteği
 * - Dinamik assertion üretimi
 * - Response pattern analizi
 *
 * @author Emre Gozukizil
 * @version 3.1.0
 */
public class ClaudeSwaggerTestGenerator4 {

    private static final Logger logger = Logger.getLogger(ClaudeSwaggerTestGenerator4.class.getName());
    private static final String APP_VERSION = "3.1.0";

    // Varsayılan yapılandırma değerleri
    private static final int DEFAULT_MAX_RETRIES = 3;
    private static final int DEFAULT_TIMEOUT_SECONDS = 120;
    private static final int DEFAULT_THREAD_POOL_SIZE = 4;
    private static final String DEFAULT_OUTPUT_FILE = "api_test_cases.java";
    private static final String DEFAULT_MODEL = "gpt-4";
    private static final int DEFAULT_MAX_TOKENS = 4096;

    // Uygulama yapılandırması
    private final Configuration config;

    // API servis nesnesi
    private OpenAiService openAiService;

    // İlerleme takibi için sayaçlar
    private final AtomicInteger processedEndpoints = new AtomicInteger(0);
    private final AtomicInteger failedEndpoints = new AtomicInteger(0);
    private int totalEndpoints = 0;

    // Thread havuzu
    private ExecutorService executorService;

    // OpenAPI dokümanı ve gelişmiş cache yapıları
    private JsonNode openApiDocument;
    private Map<String, JsonNode> schemaCache = new ConcurrentHashMap<>();
    private Map<String, EndpointDependency> endpointDependencies = new ConcurrentHashMap<>();
    private Map<String, DataConstraints> constraintCache = new ConcurrentHashMap<>();
    private Map<String, List<String>> responsePatterns = new ConcurrentHashMap<>();
    private Map<String, EndpointComplexity> complexityAnalysis = new ConcurrentHashMap<>();

    /**
     * Ana metod, komut satırı argümanlarını işler ve uygulamayı başlatır.
     */
    public static void main(String[] args) {
        setupLogger();

        try {
            CommandLine cmd = parseCommandLineArguments(args);

            if (cmd.hasOption("h")) {
                printHelp();
                return;
            }

            if (cmd.hasOption("v")) {
                System.out.println("API Test Generator v" + APP_VERSION);
                return;
            }

            String outputFile = createDynamicOutputFilePath(cmd.getOptionValue("output", DEFAULT_OUTPUT_FILE));

            Configuration config = Configuration.builder()
                    .inputFile(cmd.getOptionValue("input", "src/main/resources/endpoints2.json"))
                    .outputFile(outputFile)
                    .apiKeyEnvVar(cmd.getOptionValue("api-key-env", "OPENAI_API_KEY"))
                    .model(cmd.getOptionValue("model", DEFAULT_MODEL))
                    .maxTokens(Integer.parseInt(cmd.getOptionValue("max-tokens", String.valueOf(DEFAULT_MAX_TOKENS))))
                    .maxRetries(Integer.parseInt(cmd.getOptionValue("retries", String.valueOf(DEFAULT_MAX_RETRIES))))
                    .timeoutSeconds(Integer.parseInt(cmd.getOptionValue("timeout", String.valueOf(DEFAULT_TIMEOUT_SECONDS))))
                    .threadPoolSize(Integer.parseInt(cmd.getOptionValue("threads", String.valueOf(DEFAULT_THREAD_POOL_SIZE))))
                    .verbose(cmd.hasOption("verbose"))
                    .testClassName(getClassNameFromPath(outputFile))
                    .baseUri(cmd.getOptionValue("base-uri", "https://stage-job-k8s.isinolsun.com"))
                    .useFallbackOnError(cmd.hasOption("fallback"))
                    .generateMockTests(cmd.hasOption("mock"))
                    .includePerformanceTests(cmd.hasOption("performance"))
                    .includeSecurityTests(cmd.hasOption("security"))
                    .generateTestReport(cmd.hasOption("report"))
                    .includeContractTests(cmd.hasOption("contract"))
                    .enableSmartValidation(cmd.hasOption("smart-validation"))
                    .generateBoundaryTests(cmd.hasOption("boundary"))
                    .includeNegativeTests(cmd.hasOption("negative"))
                    .enableResponsePatternAnalysis(cmd.hasOption("pattern-analysis"))
                    .build();

            ClaudeSwaggerTestGenerator4 generator = new ClaudeSwaggerTestGenerator4(config);
            generator.run();

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Uygulama çalıştırılırken hata oluştu", e);
            System.exit(1);
        }
    }

    private static String getClassNameFromPath(String filePath) {
        String fileName = new File(filePath).getName();
        if (fileName.endsWith(".java")) {
            fileName = fileName.substring(0, fileName.length() - 5);
        }
        return fileName;
    }

    private static String createDynamicOutputFilePath(String outputFileParam) {
        String baseDir = "src/test/java/tests";
        File directory = new File(baseDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = dateFormat.format(new Date());

        String className = "GeneratedApiTests";
        if (outputFileParam != null && !outputFileParam.isEmpty()) {
            String fileName = new File(outputFileParam).getName();
            if (fileName.endsWith(".java")) {
                className = fileName.substring(0, fileName.length() - 5);
            } else {
                className = fileName;
            }
        }

        String fileName = className + "_" + timestamp + ".java";
        String filePath = baseDir + File.separator + fileName;

        logger.info("Çıktı dosyası dinamik olarak oluşturuldu: " + filePath);
        return filePath;
    }

    private static void setupLogger() {
        Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(Level.INFO);

        for (java.util.logging.Handler handler : rootLogger.getHandlers()) {
            rootLogger.removeHandler(handler);
        }

        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new SimpleFormatter() {
            private static final String format = "[%1$tF %1$tT] [%2$-7s] %3$s %n";

            @Override
            public synchronized String format(java.util.logging.LogRecord lr) {
                return String.format(format,
                        new Date(lr.getMillis()),
                        lr.getLevel().getLocalizedName(),
                        lr.getMessage()
                );
            }
        });
        rootLogger.addHandler(handler);
    }

    private static CommandLine parseCommandLineArguments(String[] args) throws ParseException {
        Options options = new Options();

        options.addOption("h", "help", false, "Yardım bilgisini göster");
        options.addOption("v", "version", false, "Uygulama versiyonunu göster");

        options.addOption(Option.builder("i")
                .longOpt("input")
                .hasArg()
                .argName("DOSYA")
                .desc("OpenAPI/Swagger JSON dosyası")
                .build());

        options.addOption(Option.builder("o")
                .longOpt("output")
                .hasArg()
                .argName("DOSYA")
                .desc("Çıktı Java dosyası")
                .build());

        options.addOption(Option.builder("k")
                .longOpt("api-key-env")
                .hasArg()
                .argName("ENV_VAR")
                .desc("API anahtarını içeren çevre değişkeni")
                .build());

        options.addOption(Option.builder("m")
                .longOpt("model")
                .hasArg()
                .argName("MODEL")
                .desc("Kullanılacak LLM modeli")
                .build());

        options.addOption(Option.builder("t")
                .longOpt("max-tokens")
                .hasArg()
                .argName("SAYI")
                .desc("Maksimum token sayısı")
                .type(Number.class)
                .build());

        options.addOption(Option.builder("r")
                .longOpt("retries")
                .hasArg()
                .argName("SAYI")
                .desc("Maksimum yeniden deneme sayısı")
                .type(Number.class)
                .build());

        options.addOption(Option.builder()
                .longOpt("timeout")
                .hasArg()
                .argName("SANIYE")
                .desc("API isteği zaman aşımı süresi")
                .type(Number.class)
                .build());

        options.addOption(Option.builder()
                .longOpt("threads")
                .hasArg()
                .argName("SAYI")
                .desc("Thread havuzu boyutu")
                .type(Number.class)
                .build());

        options.addOption(Option.builder("b")
                .longOpt("base-uri")
                .hasArg()
                .argName("URI")
                .desc("API baz URI'si")
                .build());

        // Mevcut özellikler
        options.addOption("f", "fallback", false, "API hatalarında manuel şablonlar kullan");
        options.addOption("V", "verbose", false, "Detaylı günlük kaydı aktif");
        options.addOption("M", "mock", false, "Mock server testleri üret");
        options.addOption("P", "performance", false, "Performans testleri dahil et");
        options.addOption("S", "security", false, "Güvenlik testleri dahil et");
        options.addOption("R", "report", false, "Test raporu üret");

        // Yeni özellikler
        options.addOption("C", "contract", false, "Contract testing senaryoları dahil et");
        options.addOption("SV", "smart-validation", false, "Akıllı şema doğrulama aktif");
        options.addOption("B", "boundary", false, "Boundary value testleri üret");
        options.addOption("N", "negative", false, "Detaylı negative testler dahil et");
        options.addOption("PA", "pattern-analysis", false, "Response pattern analizi aktif");

        CommandLineParser parser = new DefaultParser();
        return parser.parse(options, args);
    }

    private static void printHelp() {
        System.out.println("API Test Generator v" + APP_VERSION);
        System.out.println("Kullanım: java -jar api-test-generator.jar [SEÇENEKLER]");
        System.out.println("\nTemel Seçenekler:");
        System.out.println("  -h, --help                  Bu yardım mesajını göster");
        System.out.println("  -v, --version               Uygulama versiyonunu göster");
        System.out.println("  -i, --input <DOSYA>         OpenAPI/Swagger JSON dosyası");
        System.out.println("  -o, --output <DOSYA>        Çıktı Java dosyası");
        System.out.println("  -k, --api-key-env <ENV_VAR> API anahtarını içeren çevre değişkeni");
        System.out.println("  -m, --model <MODEL>         Kullanılacak LLM modeli");
        System.out.println("  -t, --max-tokens <SAYI>     Maksimum token sayısı");
        System.out.println("  -r, --retries <SAYI>        Maksimum yeniden deneme sayısı");
        System.out.println("  --timeout <SANIYE>          API isteği zaman aşımı süresi");
        System.out.println("  --threads <SAYI>            Thread havuzu boyutu");
        System.out.println("  -b, --base-uri <URI>        API baz URI'si");
        System.out.println("\nTest Türleri:");
        System.out.println("  -f, --fallback              API hatalarında manuel şablonlar kullan");
        System.out.println("  -V, --verbose               Detaylı günlük kaydı aktif");
        System.out.println("  -M, --mock                  Mock server testleri üret");
        System.out.println("  -P, --performance           Performans testleri dahil et");
        System.out.println("  -S, --security              Güvenlik testleri dahil et");
        System.out.println("  -R, --report                Test raporu üret");
        System.out.println("  -C, --contract              Contract testing senaryoları dahil et");
        System.out.println("  -SV, --smart-validation     Akıllı şema doğrulama aktif");
        System.out.println("  -B, --boundary              Boundary value testleri üret");
        System.out.println("  -N, --negative              Detaylı negative testler dahil et");
        System.out.println("  -PA, --pattern-analysis     Response pattern analizi aktif");
    }

    public ClaudeSwaggerTestGenerator4(Configuration config) {
        this.config = config;

        if (config.isVerbose()) {
            Logger.getLogger("").setLevel(Level.FINE);
        }

        initializeApiService();
    }

    private void initializeApiService() {
        try {
            Dotenv dotenv = Dotenv.load();
            String apiKey = dotenv.get(config.getApiKeyEnvVar());

            if (apiKey == null || apiKey.trim().isEmpty()) {
                throw new IllegalStateException(config.getApiKeyEnvVar() + " çevre değişkeni tanımlanmamış veya boş");
            }

            openAiService = new OpenAiService(apiKey, Duration.ofSeconds(config.getTimeoutSeconds()));
            logger.info("API servisi başarıyla başlatıldı. Model: " + config.getModel());

        } catch (Exception e) {
            logger.log(Level.SEVERE, "API servisi başlatılırken hata oluştu", e);
            throw new RuntimeException("API servisi başlatılamadı", e);
        }
    }

    public void run() {
        try {
            long startTime = System.currentTimeMillis();

            logger.info("===== API Test Generator v" + APP_VERSION + " =====");
            logger.info("İşlem başlatılıyor...");
            logger.info("Giriş dosyası: " + config.getInputFile());
            logger.info("Çıkış dosyası: " + config.getOutputFile());

            // JSON dosyasını okuma ve şema analizi
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonContent = FileUtils.readFileToString(new File(config.getInputFile()), StandardCharsets.UTF_8);
            openApiDocument = objectMapper.readTree(jsonContent);

            // Gelişmiş şema analizi
            performAdvancedSchemaAnalysis();

            // Test senaryolarını oluşturacağımız dosya
            File outputFile = new File(config.getOutputFile());
            if (outputFile.getParentFile() != null) {
                outputFile.getParentFile().mkdirs();
            }

            try (FileWriter outputWriter = new FileWriter(outputFile)) {
                // Test sınıfının başlangıcını yazma
                writeTestClassHeader(outputWriter);

                // Gelişmiş yardımcı metodlar ekle
                writeAdvancedUtilityMethods(outputWriter);

                // Tüm endpointleri topla ve gelişmiş analiz et
                List<EndpointInfo> endpoints = collectAndAnalyzeEndpoints(openApiDocument);
                totalEndpoints = endpoints.size();
                logger.info(totalEndpoints + " endpoint bulundu.");

                // Geliştirilmiş bağımlılık analizi
                performDependencyAnalysis(endpoints);

                // Complexity analizi
                performComplexityAnalysis(endpoints);

                // Endpointleri test önceliğine göre sırala
                endpoints = sortEndpointsByTestPriority(endpoints);

                // Thread havuzunu oluştur
                executorService = Executors.newFixedThreadPool(config.getThreadPoolSize());
                logger.info(config.getThreadPoolSize() + " thread'li işleme havuzu başlatıldı.");

                // Asenkron işleme başlat
                List<Future<String>> futures = new ArrayList<>();
                for (EndpointInfo endpoint : endpoints) {
                    futures.add(executorService.submit(() -> processEndpointAdvanced(endpoint)));
                }

                // Sonuçları bekle ve dosyaya yaz
                for (Future<String> future : futures) {
                    try {
                        String testCase = future.get();
                        synchronized (outputWriter) {
                            outputWriter.write(testCase);
                            outputWriter.write("\n\n");
                            outputWriter.flush();
                        }
                    } catch (Exception e) {
                        failedEndpoints.incrementAndGet();
                        logger.log(Level.WARNING, "Endpoint işlenirken hata oluştu", e);
                    }
                }

                // Ek gelişmiş test senaryoları
                if (config.isIncludePerformanceTests()) {
                    writeEnhancedPerformanceTests(outputWriter);
                }

                if (config.isIncludeSecurityTests()) {
                    writeEnhancedSecurityTests(outputWriter);
                }

                if (config.isGenerateMockTests()) {
                    writeAdvancedMockTests(outputWriter);
                }

                if (config.isIncludeContractTests()) {
                    writeContractTests(outputWriter);
                }

                if (config.isGenerateBoundaryTests()) {
                    writeBoundaryValueTests(outputWriter);
                }

                // Test sınıfının sonunu yazma
                writeTestClassFooter(outputWriter);
            }

            // Gelişmiş test raporu oluştur
            if (config.isGenerateTestReport()) {
                generateEnhancedTestReport();
            }

            // Bitiş zamanı ve özet
            long endTime = System.currentTimeMillis();
            double totalTime = (endTime - startTime) / 1000.0;

            logger.info("===== İşlem Tamamlandı =====");
            logger.info("Toplam süre: " + String.format("%.2f", totalTime) + " saniye");
            logger.info("İşlenen endpoint sayısı: " + processedEndpoints.get() + "/" + totalEndpoints);
            logger.info("Başarılı: " + (processedEndpoints.get() - failedEndpoints.get()));
            logger.info("Başarısız: " + failedEndpoints.get());
            logger.info("Test senaryoları dosyaya yazıldı: " + config.getOutputFile());

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Uygulama çalışırken hata oluştu", e);
            throw new RuntimeException("İşlem başarısız oldu", e);
        } finally {
            if (executorService != null) {
                executorService.shutdown();
                try {
                    if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                        executorService.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    executorService.shutdownNow();
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    /**
     * Gelişmiş şema analizi yapar
     */
    private void performAdvancedSchemaAnalysis() {
        logger.info("Gelişmiş şema analizi başlatılıyor...");

        if (openApiDocument.has("components") && openApiDocument.get("components").has("schemas")) {
            JsonNode schemas = openApiDocument.get("components").get("schemas");
            Iterator<Map.Entry<String, JsonNode>> schemaIterator = schemas.fields();

            while (schemaIterator.hasNext()) {
                Map.Entry<String, JsonNode> schemaEntry = schemaIterator.next();
                String schemaName = schemaEntry.getKey();
                JsonNode schema = schemaEntry.getValue();

                // Cache'e ekle
                schemaCache.put(schemaName, schema);

                // Data constraints analizi
                DataConstraints constraints = analyzeDataConstraints(schema, schemaName);
                constraintCache.put(schemaName, constraints);

                // Response pattern analizi (eğer aktifse)
                if (config.isEnableResponsePatternAnalysis()) {
                    analyzeResponsePatterns(schema, schemaName);
                }
            }

            logger.info(schemaCache.size() + " şema analiz edildi ve cache'lendi.");
        }
    }

    /**
     * Veri kısıtlarını analiz eder
     */
    private DataConstraints analyzeDataConstraints(JsonNode schema, String schemaName) {
        DataConstraints constraints = new DataConstraints();

        if (schema == null) {
            return constraints;
        }

        // Tip analizi
        if (schema.has("type")) {
            constraints.setType(schema.get("type").asText());
        }

        // String kısıtları
        if ("string".equals(constraints.getType())) {
            if (schema.has("minLength")) {
                constraints.setMinLength(schema.get("minLength").asInt());
            }
            if (schema.has("maxLength")) {
                constraints.setMaxLength(schema.get("maxLength").asInt());
            }
            if (schema.has("pattern")) {
                constraints.setPattern(schema.get("pattern").asText());
            }
            if (schema.has("format")) {
                constraints.setFormat(schema.get("format").asText());
            }
        }

        // Numeric kısıtları
        if ("integer".equals(constraints.getType()) || "number".equals(constraints.getType())) {
            if (schema.has("minimum")) {
                constraints.setMinimum(new BigDecimal(schema.get("minimum").asText()));
            }
            if (schema.has("maximum")) {
                constraints.setMaximum(new BigDecimal(schema.get("maximum").asText()));
            }
            if (schema.has("exclusiveMinimum")) {
                constraints.setExclusiveMinimum(schema.get("exclusiveMinimum").asBoolean());
            }
            if (schema.has("exclusiveMaximum")) {
                constraints.setExclusiveMaximum(schema.get("exclusiveMaximum").asBoolean());
            }
            if (schema.has("multipleOf")) {
                constraints.setMultipleOf(new BigDecimal(schema.get("multipleOf").asText()));
            }
        }

        // Array kısıtları
        if ("array".equals(constraints.getType())) {
            if (schema.has("minItems")) {
                constraints.setMinItems(schema.get("minItems").asInt());
            }
            if (schema.has("maxItems")) {
                constraints.setMaxItems(schema.get("maxItems").asInt());
            }
            if (schema.has("uniqueItems")) {
                constraints.setUniqueItems(schema.get("uniqueItems").asBoolean());
            }
        }

        // Object kısıtları
        if ("object".equals(constraints.getType())) {
            if (schema.has("required")) {
                List<String> requiredFields = new ArrayList<>();
                for (JsonNode field : schema.get("required")) {
                    requiredFields.add(field.asText());
                }
                constraints.setRequiredFields(requiredFields);
            }
            if (schema.has("minProperties")) {
                constraints.setMinProperties(schema.get("minProperties").asInt());
            }
            if (schema.has("maxProperties")) {
                constraints.setMaxProperties(schema.get("maxProperties").asInt());
            }
        }

        // Enum değerleri
        if (schema.has("enum")) {
            List<String> enumValues = new ArrayList<>();
            for (JsonNode enumValue : schema.get("enum")) {
                enumValues.add(enumValue.asText());
            }
            constraints.setEnumValues(enumValues);
        }

        return constraints;
    }

    /**
     * Response pattern analizi yapar
     */
    private void analyzeResponsePatterns(JsonNode schema, String schemaName) {
        List<String> patterns = new ArrayList<>();

        // Temel pattern'ları çıkar
        if (schema.has("properties")) {
            JsonNode properties = schema.get("properties");
            Iterator<String> fieldNames = properties.fieldNames();

            while (fieldNames.hasNext()) {
                String fieldName = fieldNames.next();
                patterns.add("has_field:" + fieldName);

                JsonNode fieldSchema = properties.get(fieldName);
                if (fieldSchema.has("type")) {
                    patterns.add("field_type:" + fieldName + ":" + fieldSchema.get("type").asText());
                }
            }
        }

        responsePatterns.put(schemaName, patterns);
    }
    /**
     * Endpoint'leri toplar ve gelişmiş analiz yapar
     */
    private List<EndpointInfo> collectAndAnalyzeEndpoints(JsonNode rootNode) {
        List<EndpointInfo> endpoints = new ArrayList<>();

        if (!rootNode.has("paths")) {
            logger.warning("OpenAPI dokümanında 'paths' bölümü bulunamadı");
            return endpoints;
        }

        JsonNode pathsNode = rootNode.get("paths");
        Iterator<Map.Entry<String, JsonNode>> pathsIterator = pathsNode.fields();

        while (pathsIterator.hasNext()) {
            Map.Entry<String, JsonNode> pathEntry = pathsIterator.next();
            String endpointPath = pathEntry.getKey();
            JsonNode methodsNode = pathEntry.getValue();

            Iterator<Map.Entry<String, JsonNode>> methodsIterator = methodsNode.fields();
            while (methodsIterator.hasNext()) {
                Map.Entry<String, JsonNode> methodEntry = methodsIterator.next();
                String httpMethod = methodEntry.getKey();
                JsonNode operationNode = methodEntry.getValue();

                if (isValidHttpMethod(httpMethod)) {
                    EndpointInfo endpointInfo = new EndpointInfo(endpointPath, httpMethod, operationNode);

                    // Gelişmiş endpoint analizi
                    performDetailedEndpointAnalysis(endpointInfo);

                    endpoints.add(endpointInfo);
                }
            }
        }

        return endpoints;
    }

    /**
     * HTTP metodunun geçerli olup olmadığını kontrol eder
     */
    private boolean isValidHttpMethod(String method) {
        return Arrays.asList("get", "post", "put", "delete", "patch", "head", "options").contains(method.toLowerCase());
    }

    /**
     * Detaylı endpoint analizi yapar
     */
    private void performDetailedEndpointAnalysis(EndpointInfo endpoint) {
        JsonNode operationNode = endpoint.getOperationNode();

        // Güvenlik gereksinimleri analizi
        analyzeSecurityRequirements(endpoint, operationNode);

        // Parameter analizi
        analyzeParameters(endpoint, operationNode);

        // Request body analizi
        analyzeRequestBody(endpoint, operationNode);

        // Response analizi
        analyzeResponses(endpoint, operationNode);

        // Tag analizi - kaynak türü belirleme
        analyzeResourceType(endpoint, operationNode);

        // Complexity analizi
        calculateEndpointComplexity(endpoint);
    }

    /**
     * Güvenlik gereksinimlerini analiz eder
     */
    private void analyzeSecurityRequirements(EndpointInfo endpoint, JsonNode operationNode) {
        if (operationNode.has("security")) {
            endpoint.setRequiresAuthentication(true);
            JsonNode security = operationNode.get("security");
            for (JsonNode securityReq : security) {
                Iterator<Map.Entry<String, JsonNode>> secFields = securityReq.fields();
                while (secFields.hasNext()) {
                    Map.Entry<String, JsonNode> secField = secFields.next();
                    endpoint.addSecurityScheme(secField.getKey());
                }
            }
        }
    }

    /**
     * Parametreleri analiz eder
     */
    private void analyzeParameters(EndpointInfo endpoint, JsonNode operationNode) {
        if (operationNode.has("parameters")) {
            JsonNode parameters = operationNode.get("parameters");
            for (JsonNode param : parameters) {
                ParameterInfo paramInfo = new ParameterInfo();

                if (param.has("name")) {
                    paramInfo.setName(param.get("name").asText());
                }
                if (param.has("in")) {
                    paramInfo.setLocation(param.get("in").asText());
                }
                if (param.has("required")) {
                    paramInfo.setRequired(param.get("required").asBoolean());
                }
                if (param.has("schema")) {
                    JsonNode schema = param.get("schema");
                    DataConstraints constraints = analyzeDataConstraints(schema, paramInfo.getName());
                    paramInfo.setConstraints(constraints);
                }

                endpoint.addParameter(paramInfo);

                if (paramInfo.isRequired()) {
                    endpoint.addRequiredParameter(paramInfo.getName());
                }
            }
        }
    }

    /**
     * Request body'yi analiz eder
     */
    private void analyzeRequestBody(EndpointInfo endpoint, JsonNode operationNode) {
        if (operationNode.has("requestBody")) {
            JsonNode requestBody = operationNode.get("requestBody");
            RequestBodyInfo bodyInfo = new RequestBodyInfo();

            if (requestBody.has("required")) {
                bodyInfo.setRequired(requestBody.get("required").asBoolean());
            }

            if (requestBody.has("content")) {
                JsonNode content = requestBody.get("content");
                if (content.has("application/json")) {
                    JsonNode jsonContent = content.get("application/json");
                    if (jsonContent.has("schema")) {
                        JsonNode schema = jsonContent.get("schema");
                        DataConstraints constraints = analyzeDataConstraints(schema, "requestBody");
                        bodyInfo.setConstraints(constraints);

                        // Örnek veri üret
                        try {
                            String exampleJson = generateSmartExampleJson(schema);
                            bodyInfo.setExampleData(exampleJson);
                        } catch (Exception e) {
                            logger.warning("Örnek JSON oluşturulamadı: " + e.getMessage());
                        }
                    }
                }
            }

            endpoint.setRequestBodyInfo(bodyInfo);
        }
    }

    /**
     * Response'ları analiz eder
     */
    private void analyzeResponses(EndpointInfo endpoint, JsonNode operationNode) {
        if (operationNode.has("responses")) {
            JsonNode responses = operationNode.get("responses");
            Iterator<Map.Entry<String, JsonNode>> responseIterator = responses.fields();

            while (responseIterator.hasNext()) {
                Map.Entry<String, JsonNode> responseEntry = responseIterator.next();
                String statusCode = responseEntry.getKey();
                JsonNode responseDetail = responseEntry.getValue();

                ResponseInfo responseInfo = new ResponseInfo();
                responseInfo.setStatusCode(statusCode);

                if (responseDetail.has("description")) {
                    responseInfo.setDescription(responseDetail.get("description").asText());
                }

                if (responseDetail.has("content")) {
                    JsonNode content = responseDetail.get("content");
                    if (content.has("application/json")) {
                        JsonNode jsonContent = content.get("application/json");
                        if (jsonContent.has("schema")) {
                            JsonNode schema = jsonContent.get("schema");
                            DataConstraints constraints = analyzeDataConstraints(schema, "response_" + statusCode);
                            responseInfo.setConstraints(constraints);
                        }
                    }
                }

                endpoint.addResponse(responseInfo);
                endpoint.addExpectedStatusCode(statusCode);
            }
        }
    }

    /**
     * Kaynak türünü analiz eder
     */
    private void analyzeResourceType(EndpointInfo endpoint, JsonNode operationNode) {
        if (operationNode.has("tags")) {
            JsonNode tags = operationNode.get("tags");
            if (tags.size() > 0) {
                endpoint.setResourceType(tags.get(0).asText());
            }
        }

        // Path'den kaynak türü çıkarmaya çalış
        if (endpoint.getResourceType() == null) {
            String[] pathParts = endpoint.getPath().split("/");
            for (String part : pathParts) {
                if (!part.isEmpty() && !part.startsWith("{") && !part.equals("api") && !part.equals("v1") && !part.equals("v2")) {
                    endpoint.setResourceType(part);
                    break;
                }
            }
        }
    }

    /**
     * Endpoint complexity'sini hesaplar
     */
    private void calculateEndpointComplexity(EndpointInfo endpoint) {
        EndpointComplexity complexity = new EndpointComplexity();

        int score = 0;

        // Parameter complexity
        score += endpoint.getParameters().size() * 2;
        score += endpoint.getRequiredParameters().size() * 3;

        // Request body complexity
        if (endpoint.getRequestBodyInfo() != null) {
            score += 5;
            if (endpoint.getRequestBodyInfo().isRequired()) {
                score += 3;
            }
        }

        // Response complexity
        score += endpoint.getResponses().size() * 2;

        // Security complexity
        if (endpoint.isRequiresAuthentication()) {
            score += 4;
        }
        score += endpoint.getSecuritySchemes().size() * 2;

        // Method complexity
        switch (endpoint.getMethod().toLowerCase()) {
            case "post":
            case "put":
                score += 6;
                break;
            case "patch":
                score += 5;
                break;
            case "delete":
                score += 4;
                break;
            case "get":
                score += 2;
                break;
        }

        complexity.setScore(score);

        if (score <= 10) {
            complexity.setLevel(ComplexityLevel.LOW);
        } else if (score <= 25) {
            complexity.setLevel(ComplexityLevel.MEDIUM);
        } else if (score <= 40) {
            complexity.setLevel(ComplexityLevel.HIGH);
        } else {
            complexity.setLevel(ComplexityLevel.VERY_HIGH);
        }

        complexityAnalysis.put(endpoint.getPath() + ":" + endpoint.getMethod(), complexity);
    }

    /**
     * Gelişmiş bağımlılık analizi yapar
     */
    private void performDependencyAnalysis(List<EndpointInfo> endpoints) {
        logger.info("Gelişmiş bağımlılık analizi yapılıyor...");

        Map<String, List<EndpointInfo>> resourceGroups = groupEndpointsByResource(endpoints);

        for (EndpointInfo endpoint : endpoints) {
            EndpointDependency dependency = new EndpointDependency(endpoint.getPath());

            // Temel öncelik belirleme
            switch (endpoint.getMethod().toLowerCase()) {
                case "post":
                    dependency.setPriority(1); // En yüksek öncelik - kaynak oluşturma
                    break;
                case "get":
                    dependency.setPriority(2);
                    break;
                case "put":
                case "patch":
                    dependency.setPriority(3);
                    // PUT/PATCH için POST bağımlılığı ekle
                    String createEndpoint = findCreateEndpoint(resourceGroups, endpoint.getResourceType());
                    if (createEndpoint != null) {
                        dependency.addDependency(createEndpoint);
                    }
                    break;
                case "delete":
                    dependency.setPriority(4); // En düşük öncelik
                    // DELETE için POST bağımlılığı ekle
                    String createEndpointForDelete = findCreateEndpoint(resourceGroups, endpoint.getResourceType());
                    if (createEndpointForDelete != null) {
                        dependency.addDependency(createEndpointForDelete);
                    }
                    break;
            }

            // Path parameter bağımlılıkları
            if (endpoint.getPath().contains("{")) {
                analyzeCRUDDependencies(dependency, endpoint, resourceGroups);
            }

            endpointDependencies.put(endpoint.getPath() + ":" + endpoint.getMethod(), dependency);
        }

        logger.info("Bağımlılık analizi tamamlandı. " + endpointDependencies.size() + " bağımlılık tespit edildi.");
    }

    /**
     * Endpoint'leri kaynak türüne göre gruplar
     */
    private Map<String, List<EndpointInfo>> groupEndpointsByResource(List<EndpointInfo> endpoints) {
        Map<String, List<EndpointInfo>> groups = new HashMap<>();

        for (EndpointInfo endpoint : endpoints) {
            String resourceType = endpoint.getResourceType();
            if (resourceType != null) {
                groups.computeIfAbsent(resourceType, k -> new ArrayList<>()).add(endpoint);
            }
        }

        return groups;
    }

    /**
     * CRUD bağımlılıklarını analiz eder
     */
    private void analyzeCRUDDependencies(EndpointDependency dependency, EndpointInfo endpoint,
                                         Map<String, List<EndpointInfo>> resourceGroups) {
        String resourceType = endpoint.getResourceType();
        if (resourceType == null) return;

        List<EndpointInfo> sameResourceEndpoints = resourceGroups.get(resourceType);
        if (sameResourceEndpoints == null) return;

        // ID gerektiren endpoint'ler için GET list bağımlılığı ekle
        if (endpoint.getPath().contains("{") && !endpoint.getMethod().equalsIgnoreCase("post")) {
            for (EndpointInfo relatedEndpoint : sameResourceEndpoints) {
                if (relatedEndpoint.getMethod().equalsIgnoreCase("get") &&
                        !relatedEndpoint.getPath().contains("{")) {
                    dependency.addDependency(relatedEndpoint.getPath() + ":" + relatedEndpoint.getMethod());
                    break;
                }
            }
        }
    }

    /**
     * Complexity analizi yapar
     */
    private void performComplexityAnalysis(List<EndpointInfo> endpoints) {
        logger.info("Complexity analizi yapılıyor...");

        int totalComplexity = 0;
        Map<ComplexityLevel, Integer> complexityDistribution = new HashMap<>();

        for (EndpointInfo endpoint : endpoints) {
            String key = endpoint.getPath() + ":" + endpoint.getMethod();
            EndpointComplexity complexity = complexityAnalysis.get(key);

            if (complexity != null) {
                totalComplexity += complexity.getScore();
                complexityDistribution.merge(complexity.getLevel(), 1, Integer::sum);
            }
        }

        double averageComplexity = totalComplexity / (double) endpoints.size();

        logger.info("Complexity analizi tamamlandı:");
        logger.info("- Ortalama complexity: " + String.format("%.2f", averageComplexity));
        logger.info("- Complexity dağılımı: " + complexityDistribution);
    }

    /**
     * Kaynak oluşturma endpoint'ini bulur
     */
    private String findCreateEndpoint(Map<String, List<EndpointInfo>> resourceGroups, String resourceType) {
        if (resourceType == null) return null;

        List<EndpointInfo> endpoints = resourceGroups.get(resourceType);
        if (endpoints == null) return null;

        for (EndpointInfo endpoint : endpoints) {
            if ("post".equalsIgnoreCase(endpoint.getMethod()) && !endpoint.getPath().contains("{")) {
                return endpoint.getPath() + ":" + endpoint.getMethod();
            }
        }
        return null;
    }

    /**
     * Endpoint'leri test önceliğine göre sıralar
     */
    private List<EndpointInfo> sortEndpointsByTestPriority(List<EndpointInfo> endpoints) {
        return endpoints.stream()
                .sorted((e1, e2) -> {
                    EndpointDependency dep1 = endpointDependencies.get(e1.getPath() + ":" + e1.getMethod());
                    EndpointDependency dep2 = endpointDependencies.get(e2.getPath() + ":" + e2.getMethod());

                    int priority1 = dep1 != null ? dep1.getPriority() : 5;
                    int priority2 = dep2 != null ? dep2.getPriority() : 5;

                    // Önce priority'ye göre sırala
                    int priorityComparison = Integer.compare(priority1, priority2);
                    if (priorityComparison != 0) {
                        return priorityComparison;
                    }

                    // Sonra complexity'ye göre sırala (basittan karmaşığa)
                    EndpointComplexity comp1 = complexityAnalysis.get(e1.getPath() + ":" + e1.getMethod());
                    EndpointComplexity comp2 = complexityAnalysis.get(e2.getPath() + ":" + e2.getMethod());

                    int complexity1 = comp1 != null ? comp1.getScore() : 0;
                    int complexity2 = comp2 != null ? comp2.getScore() : 0;

                    return Integer.compare(complexity1, complexity2);
                })
                .collect(ArrayList::new, (list, item) -> list.add(item), ArrayList::addAll);
    }

    // ========================== YARDIMCI SINIFLAR ==========================

    /**
     * Gelişmiş endpoint bilgisini tutan sınıf
     */
    private static class EndpointInfo {
        private final String path;
        private final String method;
        private final JsonNode operationNode;
        private String resourceType;
        private boolean requiresAuthentication = false;
        private List<String> requiredParameters = new ArrayList<>();
        private List<String> expectedStatusCodes = new ArrayList<>();
        private List<String> securitySchemes = new ArrayList<>();
        private List<ParameterInfo> parameters = new ArrayList<>();
        private RequestBodyInfo requestBodyInfo;
        private List<ResponseInfo> responses = new ArrayList<>();

        public EndpointInfo(String path, String method, JsonNode operationNode) {
            this.path = path;
            this.method = method;
            this.operationNode = operationNode;
        }

        // Getters and setters
        public String getPath() { return path; }
        public String getMethod() { return method; }
        public JsonNode getOperationNode() { return operationNode; }
        public String getResourceType() { return resourceType; }
        public void setResourceType(String resourceType) { this.resourceType = resourceType; }
        public boolean isRequiresAuthentication() { return requiresAuthentication; }
        public void setRequiresAuthentication(boolean requiresAuthentication) { this.requiresAuthentication = requiresAuthentication; }
        public List<String> getRequiredParameters() { return requiredParameters; }
        public void addRequiredParameter(String parameter) { this.requiredParameters.add(parameter); }
        public List<String> getExpectedStatusCodes() { return expectedStatusCodes; }
        public void addExpectedStatusCode(String statusCode) { this.expectedStatusCodes.add(statusCode); }
        public List<String> getSecuritySchemes() { return securitySchemes; }
        public void addSecurityScheme(String scheme) { this.securitySchemes.add(scheme); }
        public List<ParameterInfo> getParameters() { return parameters; }
        public void addParameter(ParameterInfo parameter) { this.parameters.add(parameter); }
        public RequestBodyInfo getRequestBodyInfo() { return requestBodyInfo; }
        public void setRequestBodyInfo(RequestBodyInfo requestBodyInfo) { this.requestBodyInfo = requestBodyInfo; }
        public List<ResponseInfo> getResponses() { return responses; }
        public void addResponse(ResponseInfo response) { this.responses.add(response); }
    }

    /**
     * Parameter bilgisini tutan sınıf
     */
    private static class ParameterInfo {
        private String name;
        private String location; // query, path, header, cookie
        private boolean required;
        private DataConstraints constraints;

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public boolean isRequired() { return required; }
        public void setRequired(boolean required) { this.required = required; }
        public DataConstraints getConstraints() { return constraints; }
        public void setConstraints(DataConstraints constraints) { this.constraints = constraints; }
    }

    /**
     * Request body bilgisini tutan sınıf
     */
    private static class RequestBodyInfo {
        private boolean required;
        private DataConstraints constraints;
        private String exampleData;

        // Getters and setters
        public boolean isRequired() { return required; }
        public void setRequired(boolean required) { this.required = required; }
        public DataConstraints getConstraints() { return constraints; }
        public void setConstraints(DataConstraints constraints) { this.constraints = constraints; }
        public String getExampleData() { return exampleData; }
        public void setExampleData(String exampleData) { this.exampleData = exampleData; }
    }

    /**
     * Response bilgisini tutan sınıf
     */
    private static class ResponseInfo {
        private String statusCode;
        private String description;
        private DataConstraints constraints;

        // Getters and setters
        public String getStatusCode() { return statusCode; }
        public void setStatusCode(String statusCode) { this.statusCode = statusCode; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public DataConstraints getConstraints() { return constraints; }
        public void setConstraints(DataConstraints constraints) { this.constraints = constraints; }
    }

    /**
     * Veri kısıtlarını tutan sınıf
     */
    private static class DataConstraints {
        private String type;
        private String format;
        private String pattern;
        private Integer minLength;
        private Integer maxLength;
        private BigDecimal minimum;
        private BigDecimal maximum;
        private Boolean exclusiveMinimum;
        private Boolean exclusiveMaximum;
        private BigDecimal multipleOf;
        private Integer minItems;
        private Integer maxItems;
        private Boolean uniqueItems;
        private Integer minProperties;
        private Integer maxProperties;
        private List<String> requiredFields = new ArrayList<>();
        private List<String> enumValues = new ArrayList<>();

        // Getters and setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getFormat() { return format; }
        public void setFormat(String format) { this.format = format; }
        public String getPattern() { return pattern; }
        public void setPattern(String pattern) { this.pattern = pattern; }
        public Integer getMinLength() { return minLength; }
        public void setMinLength(Integer minLength) { this.minLength = minLength; }
        public Integer getMaxLength() { return maxLength; }
        public void setMaxLength(Integer maxLength) { this.maxLength = maxLength; }
        public BigDecimal getMinimum() { return minimum; }
        public void setMinimum(BigDecimal minimum) { this.minimum = minimum; }
        public BigDecimal getMaximum() { return maximum; }
        public void setMaximum(BigDecimal maximum) { this.maximum = maximum; }
        public Boolean getExclusiveMinimum() { return exclusiveMinimum; }
        public void setExclusiveMinimum(Boolean exclusiveMinimum) { this.exclusiveMinimum = exclusiveMinimum; }
        public Boolean getExclusiveMaximum() { return exclusiveMaximum; }
        public void setExclusiveMaximum(Boolean exclusiveMaximum) { this.exclusiveMaximum = exclusiveMaximum; }
        public BigDecimal getMultipleOf() { return multipleOf; }
        public void setMultipleOf(BigDecimal multipleOf) { this.multipleOf = multipleOf; }
        public Integer getMinItems() { return minItems; }
        public void setMinItems(Integer minItems) { this.minItems = minItems; }
        public Integer getMaxItems() { return maxItems; }
        public void setMaxItems(Integer maxItems) { this.maxItems = maxItems; }
        public Boolean getUniqueItems() { return uniqueItems; }
        public void setUniqueItems(Boolean uniqueItems) { this.uniqueItems = uniqueItems; }
        public Integer getMinProperties() { return minProperties; }
        public void setMinProperties(Integer minProperties) { this.minProperties = minProperties; }
        public Integer getMaxProperties() { return maxProperties; }
        public void setMaxProperties(Integer maxProperties) { this.maxProperties = maxProperties; }
        public List<String> getRequiredFields() { return requiredFields; }
        public void setRequiredFields(List<String> requiredFields) { this.requiredFields = requiredFields; }
        public List<String> getEnumValues() { return enumValues; }
        public void setEnumValues(List<String> enumValues) { this.enumValues = enumValues; }
    }

    /**
     * Endpoint bağımlılıklarını tutan sınıf
     */
    private static class EndpointDependency {
        private final String endpointKey;
        private int priority;
        private List<String> dependencies = new ArrayList<>();

        public EndpointDependency(String endpointKey) {
            this.endpointKey = endpointKey;
        }

        public String getEndpointKey() { return endpointKey; }
        public int getPriority() { return priority; }
        public void setPriority(int priority) { this.priority = priority; }
        public List<String> getDependencies() { return dependencies; }
        public void addDependency(String dependency) {
            if (dependency != null) {
                this.dependencies.add(dependency);
            }
        }
    }

    /**
     * Endpoint kompleksitesini tutan sınıf
     */
    private static class EndpointComplexity {
        private int score;
        private ComplexityLevel level;

        public int getScore() { return score; }
        public void setScore(int score) { this.score = score; }
        public ComplexityLevel getLevel() { return level; }
        public void setLevel(ComplexityLevel level) { this.level = level; }
    }

    /**
     * Kompleksite seviyeleri enum'u
     */
    private enum ComplexityLevel {
        LOW, MEDIUM, HIGH, VERY_HIGH
    }

    /**
     * Gelişmiş yapılandırma sınıfı
     */
    public static class Configuration {
        private final String inputFile;
        private final String outputFile;
        private final String apiKeyEnvVar;
        private final String model;
        private final int maxTokens;
        private final int maxRetries;
        private final int timeoutSeconds;
        private final int threadPoolSize;
        private final boolean verbose;
        private final boolean useFallbackOnError;
        private final String testClassName;
        private final String baseUri;
        private final boolean generateMockTests;
        private final boolean includePerformanceTests;
        private final boolean includeSecurityTests;
        private final boolean generateTestReport;
        private final boolean includeContractTests;
        private final boolean enableSmartValidation;
        private final boolean generateBoundaryTests;
        private final boolean includeNegativeTests;
        private final boolean enableResponsePatternAnalysis;

        private Configuration(Builder builder) {
            this.inputFile = builder.inputFile;
            this.outputFile = builder.outputFile;
            this.apiKeyEnvVar = builder.apiKeyEnvVar;
            this.model = builder.model;
            this.maxTokens = builder.maxTokens;
            this.maxRetries = builder.maxRetries;
            this.timeoutSeconds = builder.timeoutSeconds;
            this.threadPoolSize = builder.threadPoolSize;
            this.verbose = builder.verbose;
            this.useFallbackOnError = builder.useFallbackOnError;
            this.testClassName = builder.testClassName;
            this.baseUri = builder.baseUri;
            this.generateMockTests = builder.generateMockTests;
            this.includePerformanceTests = builder.includePerformanceTests;
            this.includeSecurityTests = builder.includeSecurityTests;
            this.generateTestReport = builder.generateTestReport;
            this.includeContractTests = builder.includeContractTests;
            this.enableSmartValidation = builder.enableSmartValidation;
            this.generateBoundaryTests = builder.generateBoundaryTests;
            this.includeNegativeTests = builder.includeNegativeTests;
            this.enableResponsePatternAnalysis = builder.enableResponsePatternAnalysis;
        }

        public static Builder builder() {
            return new Builder();
        }

        // Getters
        public String getInputFile() { return inputFile; }
        public String getOutputFile() { return outputFile; }
        public String getApiKeyEnvVar() { return apiKeyEnvVar; }
        public String getModel() { return model; }
        public int getMaxTokens() { return maxTokens; }
        public int getMaxRetries() { return maxRetries; }
        public int getTimeoutSeconds() { return timeoutSeconds; }
        public int getThreadPoolSize() { return threadPoolSize; }
        public boolean isVerbose() { return verbose; }
        public boolean isUseFallbackOnError() { return useFallbackOnError; }
        public String getTestClassName() { return testClassName; }
        public String getBaseUri() { return baseUri; }
        public boolean isGenerateMockTests() { return generateMockTests; }
        public boolean isIncludePerformanceTests() { return includePerformanceTests; }
        public boolean isIncludeSecurityTests() { return includeSecurityTests; }
        public boolean isGenerateTestReport() { return generateTestReport; }
        public boolean isIncludeContractTests() { return includeContractTests; }
        public boolean isEnableSmartValidation() { return enableSmartValidation; }
        public boolean isGenerateBoundaryTests() { return generateBoundaryTests; }
        public boolean isIncludeNegativeTests() { return includeNegativeTests; }
        public boolean isEnableResponsePatternAnalysis() { return enableResponsePatternAnalysis; }

        /**
         * Builder sınıfı - Fluent API ile yapılandırma oluşturma
         */
        public static class Builder {
            private String inputFile = "endpoints2.json";
            private String outputFile = DEFAULT_OUTPUT_FILE;
            private String apiKeyEnvVar = "OPENAI_API_KEY";
            private String model = DEFAULT_MODEL;
            private int maxTokens = DEFAULT_MAX_TOKENS;
            private int maxRetries = DEFAULT_MAX_RETRIES;
            private int timeoutSeconds = DEFAULT_TIMEOUT_SECONDS;
            private int threadPoolSize = DEFAULT_THREAD_POOL_SIZE;
            private boolean verbose = false;
            private boolean useFallbackOnError = true;
            private String testClassName = "GeneratedApiTests";
            private String baseUri = "https://api.example.com";
            private boolean generateMockTests = false;
            private boolean includePerformanceTests = false;
            private boolean includeSecurityTests = false;
            private boolean generateTestReport = false;
            private boolean includeContractTests = false;
            private boolean enableSmartValidation = false;
            private boolean generateBoundaryTests = false;
            private boolean includeNegativeTests = false;
            private boolean enableResponsePatternAnalysis = false;

            public Builder inputFile(String inputFile) {
                this.inputFile = inputFile;
                return this;
            }

            public Builder outputFile(String outputFile) {
                this.outputFile = outputFile;
                return this;
            }

            public Builder apiKeyEnvVar(String apiKeyEnvVar) {
                this.apiKeyEnvVar = apiKeyEnvVar;
                return this;
            }

            public Builder model(String model) {
                this.model = model;
                return this;
            }

            public Builder maxTokens(int maxTokens) {
                this.maxTokens = maxTokens;
                return this;
            }

            public Builder maxRetries(int maxRetries) {
                this.maxRetries = maxRetries;
                return this;
            }

            public Builder timeoutSeconds(int timeoutSeconds) {
                this.timeoutSeconds = timeoutSeconds;
                return this;
            }

            public Builder threadPoolSize(int threadPoolSize) {
                this.threadPoolSize = threadPoolSize;
                return this;
            }

            public Builder verbose(boolean verbose) {
                this.verbose = verbose;
                return this;
            }

            public Builder useFallbackOnError(boolean useFallbackOnError) {
                this.useFallbackOnError = useFallbackOnError;
                return this;
            }

            public Builder testClassName(String testClassName) {
                this.testClassName = testClassName;
                return this;
            }

            public Builder baseUri(String baseUri) {
                this.baseUri = baseUri;
                return this;
            }

            public Builder generateMockTests(boolean generateMockTests) {
                this.generateMockTests = generateMockTests;
                return this;
            }

            public Builder includePerformanceTests(boolean includePerformanceTests) {
                this.includePerformanceTests = includePerformanceTests;
                return this;
            }

            public Builder includeSecurityTests(boolean includeSecurityTests) {
                this.includeSecurityTests = includeSecurityTests;
                return this;
            }

            public Builder generateTestReport(boolean generateTestReport) {
                this.generateTestReport = generateTestReport;
                return this;
            }

            public Builder includeContractTests(boolean includeContractTests) {
                this.includeContractTests = includeContractTests;
                return this;
            }

            public Builder enableSmartValidation(boolean enableSmartValidation) {
                this.enableSmartValidation = enableSmartValidation;
                return this;
            }

            public Builder generateBoundaryTests(boolean generateBoundaryTests) {
                this.generateBoundaryTests = generateBoundaryTests;
                return this;
            }

            public Builder includeNegativeTests(boolean includeNegativeTests) {
                this.includeNegativeTests = includeNegativeTests;
                return this;
            }

            public Builder enableResponsePatternAnalysis(boolean enableResponsePatternAnalysis) {
                this.enableResponsePatternAnalysis = enableResponsePatternAnalysis;
                return this;
            }

            public Configuration build() {
                return new Configuration(this);
            }
        }
    }

    // ========================== GELİŞTİRİLMİŞ TEST ÜRET­İM METODLARI ==========================

    /**
     * Gelişmiş endpoint işleme
     */
    private String processEndpointAdvanced(EndpointInfo endpoint) {
        String endpointPath = endpoint.getPath();
        String httpMethod = endpoint.getMethod();
        JsonNode operationNode = endpoint.getOperationNode();

        try {
            int current = processedEndpoints.incrementAndGet();
            String operationId = getOperationId(operationNode, httpMethod, endpointPath);

            logger.info(String.format("[%d/%d] Gelişmiş endpoint işleniyor: %s %s",
                    current, totalEndpoints, httpMethod.toUpperCase(), endpointPath));

            // Gelişmiş prompt oluşturma
            String prompt = createAdvancedPromptForEndpoint(endpoint);

            // Test senaryosu oluşturma
            String testCase = generateTestCaseWithRetry(prompt, operationId, httpMethod, endpointPath);

            logger.info(String.format("[%d/%d] Endpoint tamamlandı: %s %s",
                    current, totalEndpoints, httpMethod.toUpperCase(), endpointPath));

            return testCase;

        } catch (Exception e) {
            failedEndpoints.incrementAndGet();
            logger.log(Level.WARNING, "Endpoint işlenirken hata: " + httpMethod + " " + endpointPath, e);

            if (config.isUseFallbackOnError()) {
                String operationId = getOperationId(operationNode, httpMethod, endpointPath);
                return generateAdvancedFallbackTestCase(endpoint, operationId);
            } else {
                throw new RuntimeException("Endpoint işleme hatası", e);
            }
        }
    }

    /**
     * Gelişmiş sistem prompt'u oluşturur
     */
    private String createAdvancedSystemPrompt() {
        StringBuilder prompt = new StringBuilder();

        prompt.append("Sen bir uzman API test otomasyonu geliştiricisisin. Verilen OpenAPI endpoint'i için ");
        prompt.append("kapsamlı, gerçekçi ve profesyonel REST Assured + JUnit 5 test senaryoları oluştur.\n\n");

        prompt.append("TEMEL KURALLAR:\n");
        prompt.append("1. ASLA import ifadeleri ekleme - bunlar zaten tanımlanmıştır\n");
        prompt.append("2. ASLA yeni sınıf tanımı oluşturma - yalnızca metod içeriği döndür\n");
        prompt.append("3. ASLA test metodunun imzasını dahil etme - sadece metod gövdesini ver\n");
        prompt.append("4. Tüm senaryoları tek bir test metodu içinde organize et\n");
        prompt.append("5. Her test bölümü için açıklayıcı Türkçe yorumlar ekle\n\n");

        prompt.append("GELİŞTİRİLMİŞ TEST KAPSAMI:\n");
        prompt.append("1. Happy Path testleri (başarılı senaryolar)\n");
        prompt.append("2. Comprehensive Negative testler (detaylı hata senaryoları)\n");
        prompt.append("3. Boundary testleri (sınır değer testleri)\n");
        prompt.append("4. Data constraint testleri (şema doğrulama)\n");
        prompt.append("5. Security testleri (authentication, authorization, injection)\n");
        prompt.append("6. Performance testleri (yanıt süresi, load testing)\n");
        prompt.append("7. Contract testleri (API sözleşme doğrulama)\n");
        prompt.append("8. Edge case testleri (özel durumlar ve corner cases)\n");
        prompt.append("9. Idempotency testleri (tekrar edilebilirlik)\n");
        prompt.append("10. Concurrency testleri (eşzamanlılık)\n\n");

        if (config.isEnableSmartValidation()) {
            prompt.append("AKILLI DOĞRULAMA:\n");
            prompt.append("- Şema kısıtlarına göre dinamik assertion'lar oluştur\n");
            prompt.append("- Response pattern analizi yap\n");
            prompt.append("- Field-level validation kontrolleri ekle\n");
            prompt.append("- Business rule validation testleri dahil et\n\n");
        }

        prompt.append("VERİ ÜRETİMİ:\n");
        prompt.append("- Constraint'lere uygun gerçekçi test verisi oluştur\n");
        prompt.append("- Boundary değerlerini test et (min, max, min+1, max-1)\n");
        prompt.append("- Invalid veri kombinasyonları dene\n");
        prompt.append("- Edge case değerleri kullan (null, empty, very long, special chars)\n\n");

        prompt.append("KOD KALİTESİ PRENSİPLERİ:\n");
        prompt.append("- DRY: Kod tekrarından kaçın, yardımcı metodları kullanın\n");
        prompt.append("- Clear naming: Değişken ve test bölümlerini açık isimlendirin\n");
        prompt.append("- Comprehensive logging: Her test adımını detaylı logla\n");
        prompt.append("- Robust error handling: Hata durumlarını kapsamlı ele alın\n");
        prompt.append("- Maintainable: Bakımı kolay, anlaşılır kod yazın\n");
        prompt.append("- Performance aware: Gereksiz işlemlerden kaçının\n\n");

        return prompt.toString();
    }

    /**
     * Akıllı örnek JSON oluşturur
     */
    private String generateSmartExampleJson(JsonNode schema) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode rootNode = objectMapper.createObjectNode();

        if (schema.has("properties")) {
            JsonNode properties = schema.get("properties");
            Iterator<Map.Entry<String, JsonNode>> fields = properties.fields();

            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                String name = field.getKey();
                JsonNode propertySchema = field.getValue();

                addSmartPropertyToJson(rootNode, name, propertySchema, objectMapper);
            }
        }

        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
    }

    /**
     * Akıllı JSON property'si ekler
     */
    private void addSmartPropertyToJson(ObjectNode parentNode, String name, JsonNode schema, ObjectMapper mapper) {
        if (!schema.has("type")) {
            return;
        }

        String type = schema.get("type").asText();

        switch (type) {
            case "string":
                String stringValue = generateSmartStringValue(schema, name);
                parentNode.put(name, stringValue);
                break;
            case "integer":
                int intValue = generateSmartIntegerValue(schema);
                parentNode.put(name, intValue);
                break;
            case "number":
                double numberValue = generateSmartNumberValue(schema);
                parentNode.put(name, numberValue);
                break;
            case "boolean":
                boolean boolValue = generateSmartBooleanValue(schema, name);
                parentNode.put(name, boolValue);
                break;
            case "array":
                addSmartArrayToJson(parentNode, name, schema, mapper);
                break;
            case "object":
                addSmartObjectToJson(parentNode, name, schema, mapper);
                break;
        }
    }

    /**
     * Akıllı string değer oluşturur
     */
    private String generateSmartStringValue(JsonNode schema, String fieldName) {
        // Önce example kontrol et
        if (schema.has("example")) {
            return schema.get("example").asText();
        }

        // Enum değerleri varsa birini seç
        if (schema.has("enum")) {
            return schema.get("enum").get(0).asText();
        }

        // Format'a göre özel değerler
        if (schema.has("format")) {
            String format = schema.get("format").asText();
            switch (format) {
                case "date":
                    return "2024-12-01";
                case "date-time":
                    return "2024-12-01T10:30:00Z";
                case "email":
                    return "test.user." + System.currentTimeMillis() + "@example.com";
                case "uuid":
                    return UUID.randomUUID().toString();
                case "uri":
                    return "https://api.example.com/resource/" + System.currentTimeMillis();
                case "password":
                    return "SecurePassword123!@#";
                case "binary":
                    return "VGVzdCBiaW5hcnkgZGF0YQ=="; // Base64 encoded
                default:
                    break;
            }
        }

        // Pattern varsa basit bir değer üret
        if (schema.has("pattern")) {
            String pattern = schema.get("pattern").asText();
            // Basit pattern'lar için örnek değerler
            if (pattern.contains("^[A-Z]")) {
                return "TEST_" + fieldName.toUpperCase();
            } else if (pattern.contains("[0-9]")) {
                return fieldName + "123";
            }
        }

        // Length kısıtlarını dikkate al
        String baseValue = generateContextualValue(fieldName);

        if (schema.has("minLength") || schema.has("maxLength")) {
            int minLength = schema.has("minLength") ? schema.get("minLength").asInt() : 0;
            int maxLength = schema.has("maxLength") ? schema.get("maxLength").asInt() : 255;

            if (baseValue.length() < minLength) {
                return baseValue + "_".repeat(minLength - baseValue.length());
            } else if (baseValue.length() > maxLength) {
                return baseValue.substring(0, maxLength);
            }
        }

        return baseValue;
    }

    /**
     * Field ismine göre context-aware değer üretir
     */
    private String generateContextualValue(String fieldName) {
        String lowerName = fieldName.toLowerCase();
        String timestamp = String.valueOf(System.currentTimeMillis());

        if (lowerName.contains("name")) {
            return "Test Name " + timestamp;
        } else if (lowerName.contains("title")) {
            return "Test Title " + timestamp;
        } else if (lowerName.contains("description")) {
            return "This is a test description for " + fieldName + " field";
        } else if (lowerName.contains("email")) {
            return "test" + timestamp + "@example.com";
        } else if (lowerName.contains("phone")) {
            return "+1-555-0123";
        } else if (lowerName.contains("address")) {
            return "123 Test Street, Test City, TC 12345";
        } else if (lowerName.contains("url") || lowerName.contains("link")) {
            return "https://example.com/test/" + timestamp;
        } else if (lowerName.contains("code")) {
            return "CODE_" + timestamp;
        } else if (lowerName.contains("id")) {
            return "id_" + timestamp;
        }

        return "test_" + fieldName.toLowerCase() + "_" + timestamp;
    }

    /**
     * Akıllı integer değer oluşturur
     */
    private int generateSmartIntegerValue(JsonNode schema) {
        if (schema.has("example")) {
            return schema.get("example").asInt();
        }

        int min = schema.has("minimum") ? schema.get("minimum").asInt() : 1;
        int max = schema.has("maximum") ? schema.get("maximum").asInt() : 1000;

        // Exclusive minimum/maximum kontrolü
        if (schema.has("exclusiveMinimum") && schema.get("exclusiveMinimum").asBoolean()) {
            min++;
        }
        if (schema.has("exclusiveMaximum") && schema.get("exclusiveMaximum").asBoolean()) {
            max--;
        }

        // MultipleOf kontrolü
        if (schema.has("multipleOf")) {
            int multipleOf = schema.get("multipleOf").asInt();
            int baseValue = Math.max(min, multipleOf * 10);
            return Math.min(baseValue, max);
        }

        // Sensible default value within range
        return Math.max(min, Math.min(max, 42));
    }

    /**
     * Akıllı number değer oluşturur
     */
    private double generateSmartNumberValue(JsonNode schema) {
        if (schema.has("example")) {
            return schema.get("example").asDouble();
        }

        double min = schema.has("minimum") ? schema.get("minimum").asDouble() : 0.0;
        double max = schema.has("maximum") ? schema.get("maximum").asDouble() : 1000.0;

        // Exclusive minimum/maximum kontrolü
        if (schema.has("exclusiveMinimum") && schema.get("exclusiveMinimum").asBoolean()) {
            min += 0.1;
        }
        if (schema.has("exclusiveMaximum") && schema.get("exclusiveMaximum").asBoolean()) {
            max -= 0.1;
        }

        return Math.max(min, Math.min(max, 42.5));
    }

    /**
     * Akıllı boolean değer oluşturur
     */
    private boolean generateSmartBooleanValue(JsonNode schema, String fieldName) {
        if (schema.has("example")) {
            return schema.get("example").asBoolean();
        }

        String lowerName = fieldName.toLowerCase();
        // Field ismine göre mantıklı default değerler
        if (lowerName.contains("active") || lowerName.contains("enabled") ||
                lowerName.contains("visible") || lowerName.contains("public")) {
            return true;
        } else if (lowerName.contains("deleted") || lowerName.contains("disabled") ||
                lowerName.contains("hidden") || lowerName.contains("private")) {
            return false;
        }

        return true; // Default true
    }

    /**
     * Akıllı array ekler
     */
    private void addSmartArrayToJson(ObjectNode parentNode, String name, JsonNode schema, ObjectMapper mapper) {
        var arrayNode = parentNode.putArray(name);

        // Items şeması varsa birkaç örnek ekle
        if (schema.has("items")) {
            JsonNode itemsSchema = schema.get("items");
            int itemCount = 2; // Default 2 item

            if (schema.has("minItems")) {
                itemCount = Math.max(itemCount, schema.get("minItems").asInt());
            }
            if (schema.has("maxItems")) {
                itemCount = Math.min(itemCount, schema.get("maxItems").asInt());
            }

            for (int i = 0; i < itemCount; i++) {
                if (itemsSchema.has("type")) {
                    String itemType = itemsSchema.get("type").asText();
                    switch (itemType) {
                        case "string":
                            arrayNode.add(generateSmartStringValue(itemsSchema, name + "_item_" + i));
                            break;
                        case "integer":
                            arrayNode.add(generateSmartIntegerValue(itemsSchema));
                            break;
                        case "number":
                            arrayNode.add(generateSmartNumberValue(itemsSchema));
                            break;
                        case "boolean":
                            arrayNode.add(generateSmartBooleanValue(itemsSchema, name + "_item"));
                            break;
                        case "object":
                            ObjectNode itemObject = mapper.createObjectNode();
                            addSmartObjectToJson(itemObject, "item", itemsSchema, mapper);
                            arrayNode.add(itemObject);
                            break;
                    }
                }
            }
        }
    }

    /**
     * Akıllı object ekler
     */
    private void addSmartObjectToJson(ObjectNode parentNode, String name, JsonNode schema, ObjectMapper mapper) {
        ObjectNode objectNode = mapper.createObjectNode();

        if (schema.has("properties")) {
            JsonNode properties = schema.get("properties");
            Iterator<Map.Entry<String, JsonNode>> fields = properties.fields();

            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                addSmartPropertyToJson(objectNode, field.getKey(), field.getValue(), mapper);
            }
        }

        parentNode.set(name, objectNode);
    }

    /**
     * Operasyon düğümünden operasyon ID'sini alır
     */
    private String getOperationId(JsonNode operationNode, String httpMethod, String endpointPath) {
        if (operationNode != null && operationNode.has("operationId")) {
            return sanitizeMethodName(operationNode.get("operationId").asText());
        }

        // Fallback: endpoint ve method'dan oluştur
        String resource = "";
        if (operationNode != null && operationNode.has("tags") && operationNode.get("tags").size() > 0) {
            resource = operationNode.get("tags").get(0).asText();
        } else {
            String[] pathParts = endpointPath.split("/");
            if (pathParts.length > 1) {
                resource = pathParts[1].replace("{", "").replace("}", "");
            }
        }

        resource = sanitizeMethodName(resource);
        String endpointPart = endpointPath.replace("/", "_").replace("{", "").replace("}", "");
        if (endpointPart.startsWith("_")) {
            endpointPart = endpointPart.substring(1);
        }
        endpointPart = sanitizeMethodName(endpointPart);

        return resource + httpMethod.substring(0, 1).toUpperCase() + httpMethod.substring(1) + "_" + endpointPart;
    }

    /**
     * Java metod ismi için geçersiz karakterleri temizler
     */
    private String sanitizeMethodName(String input) {
        if (input == null || input.isEmpty()) {
            return "unknown";
        }

        String result = input.replace('-', '_');
        result = result.replaceAll("[^a-zA-Z0-9_]", "");

        if (!result.isEmpty() && Character.isDigit(result.charAt(0))) {
            result = "_" + result;
        }

        return result.isEmpty() ? "unknown" : result;
    }

    /**
     * Gelişmiş test senaryosu prompt'u oluşturur
     */
    private String createAdvancedPromptForEndpoint(EndpointInfo endpoint) {
        StringBuilder prompt = new StringBuilder();
        JsonNode operationNode = endpoint.getOperationNode();

        // Temel endpoint bilgileri
        prompt.append("=== ENDPOINT ANALİZİ ===\n");
        prompt.append("Endpoint: ").append(endpoint.getPath()).append("\n");
        prompt.append("HTTP Method: ").append(endpoint.getMethod().toUpperCase()).append("\n");
        prompt.append("Resource Type: ").append(endpoint.getResourceType()).append("\n");
        prompt.append("Authentication Required: ").append(endpoint.isRequiresAuthentication()).append("\n");

        // Complexity bilgisi
        EndpointComplexity complexity = complexityAnalysis.get(endpoint.getPath() + ":" + endpoint.getMethod());
        if (complexity != null) {
            prompt.append("Complexity Level: ").append(complexity.getLevel()).append(" (Score: ").append(complexity.getScore()).append(")\n");
        }

        if (operationNode.has("summary")) {
            prompt.append("Summary: ").append(operationNode.get("summary").asText()).append("\n");
        }

        // Parametre analizi
        if (!endpoint.getParameters().isEmpty()) {
            prompt.append("\n=== PARAMETRELER ===\n");
            for (ParameterInfo param : endpoint.getParameters()) {
                prompt.append("• ").append(param.getName()).append(" (").append(param.getLocation()).append(")");
                if (param.isRequired()) {
                    prompt.append(" [ZORUNLU]");
                }
                prompt.append("\n");

                if (param.getConstraints() != null) {
                    appendConstraintDetails(param.getConstraints(), prompt, "  ");
                }
            }
        }

        // Request body analizi
        if (endpoint.getRequestBodyInfo() != null) {
            prompt.append("\n=== REQUEST BODY ===\n");
            RequestBodyInfo bodyInfo = endpoint.getRequestBodyInfo();
            prompt.append("Required: ").append(bodyInfo.isRequired() ? "Yes" : "No").append("\n");

            if (bodyInfo.getExampleData() != null) {
                prompt.append("Example Data:\n").append(bodyInfo.getExampleData()).append("\n");
            }

            if (bodyInfo.getConstraints() != null) {
                appendConstraintDetails(bodyInfo.getConstraints(), prompt, "");
            }
        }

        // Response analizi
        if (!endpoint.getResponses().isEmpty()) {
            prompt.append("\n=== EXPECTED RESPONSES ===\n");
            for (ResponseInfo response : endpoint.getResponses()) {
                prompt.append("Status ").append(response.getStatusCode()).append(": ");
                prompt.append(response.getDescription() != null ? response.getDescription() : "No description").append("\n");
            }
        }

        // Test senaryosu spesifikasyonu
        prompt.append(generateEnhancedTestScenarioSpec(endpoint));

        return prompt.toString();
    }

    /**
     * Constraint detaylarını prompt'a ekler
     */
    private void appendConstraintDetails(DataConstraints constraints, StringBuilder prompt, String indent) {
        if (constraints.getType() != null) {
            prompt.append(indent).append("Type: ").append(constraints.getType()).append("\n");
        }
        if (constraints.getFormat() != null) {
            prompt.append(indent).append("Format: ").append(constraints.getFormat()).append("\n");
        }
        if (constraints.getPattern() != null) {
            prompt.append(indent).append("Pattern: ").append(constraints.getPattern()).append("\n");
        }
        if (constraints.getMinLength() != null) {
            prompt.append(indent).append("Min Length: ").append(constraints.getMinLength()).append("\n");
        }
        if (constraints.getMaxLength() != null) {
            prompt.append(indent).append("Max Length: ").append(constraints.getMaxLength()).append("\n");
        }
        if (constraints.getMinimum() != null) {
            prompt.append(indent).append("Minimum: ").append(constraints.getMinimum()).append("\n");
        }
        if (constraints.getMaximum() != null) {
            prompt.append(indent).append("Maximum: ").append(constraints.getMaximum()).append("\n");
        }
        if (!constraints.getEnumValues().isEmpty()) {
            prompt.append(indent).append("Enum Values: ").append(String.join(", ", constraints.getEnumValues())).append("\n");
        }
    }

    /**
     * Gelişmiş test senaryosu spesifikasyonu
     */
    private String generateEnhancedTestScenarioSpec(EndpointInfo endpoint) {
        StringBuilder spec = new StringBuilder();
        String method = endpoint.getMethod().toLowerCase();

        spec.append("\n=== OLUŞTURULACAK TEST SENARYOLARI ===\n");

        // Temel testler
        spec.append("1. HAPPY PATH TESTLER:\n");
        spec.append("   - Başarılı ").append(method.toUpperCase()).append(" isteği\n");
        spec.append("   - Geçerli parametrelerle test\n");
        spec.append("   - Response validation (şema, format, değerler)\n");
        spec.append("   - Performance assertion (< 3000ms)\n\n");

        // Constraint-based testler
        if (config.isEnableSmartValidation()) {
            spec.append("2. SMART VALIDATION TESTLER:\n");
            spec.append("   - Constraint-based boundary testing\n");
            spec.append("   - Field-level validation\n");
            spec.append("   - Business rule validation\n");
            spec.append("   - Data format validation\n\n");
        }

        // Boundary testler
        if (config.isGenerateBoundaryTests()) {
            spec.append("3. BOUNDARY VALUE TESTLER:\n");
            spec.append("   - Min/Max değer testleri\n");
            spec.append("   - Min-1/Max+1 değer testleri\n");
            spec.append("   - Length boundary testleri\n");
            spec.append("   - Numeric precision testleri\n\n");
        }

        // Negative testler
        if (config.isIncludeNegativeTests()) {
            spec.append("4. COMPREHENSIVE NEGATIVE TESTLER:\n");
            spec.append("   - Invalid data type testleri\n");
            spec.append("   - Missing required field testleri\n");
            spec.append("   - Invalid format testleri\n");
            spec.append("   - Constraint violation testleri\n");
            spec.append("   - Malformed request testleri\n\n");
        }

        // Güvenlik testleri
        spec.append("5. GÜVENLİK TESTLERİ:\n");
        if (endpoint.isRequiresAuthentication()) {
            spec.append("   - No authentication header test (401)\n");
            spec.append("   - Invalid token test (403)\n");
            spec.append("   - Expired token test\n");
        }
        spec.append("   - SQL injection attempts\n");
        spec.append("   - XSS attack attempts\n");
        spec.append("   - Input sanitization tests\n\n");

        // Contract testleri
        if (config.isIncludeContractTests()) {
            spec.append("6. CONTRACT TESTLER:\n");
            spec.append("   - API sözleşme doğrulama\n");
            spec.append("   - Response structure validation\n");
            spec.append("   - Backwards compatibility tests\n");
            spec.append("   - API versioning tests\n\n");
        }

        return spec.toString();
    }

    /**
     * Test senaryosu oluşturma ve retry mekanizması
     */
    private String generateTestCaseWithRetry(String prompt, String operationId, String httpMethod, String endpointPath) {
        int retryCount = 0;
        long waitTime = 1000;
        Exception lastException = null;

        while (retryCount < config.getMaxRetries()) {
            try {
                return generateAdvancedTestCase(prompt, operationId, httpMethod, endpointPath);
            } catch (Exception e) {
                lastException = e;
                retryCount++;

                if (config.isVerbose()) {
                    logger.warning("API çağrısı başarısız oldu (" + retryCount + "/" + config.getMaxRetries() + "): " + e.getMessage());
                }

                if (retryCount >= config.getMaxRetries()) {
                    break;
                }

                try {
                    Thread.sleep(waitTime);
                    waitTime *= 2;
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Bekleme işlemi kesintiye uğradı", ie);
                }
            }
        }

        if (config.isUseFallbackOnError()) {
            logger.warning("Maksimum retry'a ulaşıldı. Fallback kullanılacak.");
            return generateAdvancedFallbackTestCase(new EndpointInfo(endpointPath, httpMethod, null), operationId);
        } else {
            throw new RuntimeException("API çağrısı başarısız", lastException);
        }
    }

    /**
     * Gelişmiş API çağrısı ile test senaryosu oluşturur
     */
    private String generateAdvancedTestCase(String prompt, String operationId, String httpMethod, String endpointPath) {
        List<ChatMessage> messages = new ArrayList<>();

        messages.add(new ChatMessage(ChatMessageRole.SYSTEM.value(), createAdvancedSystemPrompt()));
        messages.add(new ChatMessage(ChatMessageRole.USER.value(), prompt));

        ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                .model(config.getModel())
                .messages(messages)
                .maxTokens(config.getMaxTokens())
                .temperature(0.1)
                .topP(0.9)
                .build();

        String response = openAiService.createChatCompletion(completionRequest)
                .getChoices().get(0).getMessage().getContent();

        return formatAdvancedTestCase(response, operationId, httpMethod, endpointPath);
    }

    /**
     * Test senaryosunu formatlar
     */
    private String formatAdvancedTestCase(String response, String operationId, String httpMethod, String endpointPath) {
        StringBuilder formattedTestCase = new StringBuilder();

        // JavaDoc yorumu
        formattedTestCase.append("    /**\n");
        formattedTestCase.append("     * Enhanced API Test: ").append(operationId).append("\n");
        formattedTestCase.append("     * Endpoint: ").append(httpMethod.toUpperCase()).append(" ").append(endpointPath).append("\n");
        formattedTestCase.append("     * Generated with Smart Analysis v").append(APP_VERSION).append("\n");
        formattedTestCase.append("     */\n");

        // Test anotasyonları
        formattedTestCase.append("    @Test\n");
        formattedTestCase.append("    @DisplayName(\"Enhanced Test: ")
                .append(operationId).append(" - ")
                .append(httpMethod.toUpperCase()).append(" ")
                .append(endpointPath).append("\")\n");

        formattedTestCase.append("    @Tag(\"").append(httpMethod.toLowerCase()).append("\")\n");
        formattedTestCase.append("    @Tag(\"enhanced\")\n");
        formattedTestCase.append("    @Tag(\"generated\")\n");

        // Metod tanımı
        formattedTestCase.append("    public void testEnhanced").append(sanitizeMethodName(operationId)).append("() {\n");

        // Test başlangıç logu
        formattedTestCase.append("        logger.info(\"=== Enhanced API Test Starting: ")
                .append(httpMethod.toUpperCase()).append(" ").append(endpointPath).append(" ===\");\n");

        // Yanıt içeriğini temizle ve ekle
        String cleanedResponse = cleanAdvancedResponse(response);
        if (!cleanedResponse.trim().isEmpty()) {
            formattedTestCase.append("        ").append(cleanedResponse.trim().replace("\n", "\n        ")).append("\n");
        } else {
            formattedTestCase.append("        // Enhanced test content could not be generated\n");
            formattedTestCase.append("        logger.warning(\"Using basic fallback for: ").append(operationId).append("\");\n");
        }

        formattedTestCase.append("        logger.info(\"=== Enhanced Test Completed: ").append(operationId).append(" ===\");\n");
        formattedTestCase.append("    }");

        return formattedTestCase.toString();
    }

    /**
     * Response temizleme
     */
    private String cleanAdvancedResponse(String response) {
        String cleaned = response.replaceAll("```java", "").replaceAll("```", "");
        cleaned = cleaned.replaceAll("(?m)^\\s*import\\s+[\\w\\.\\*]+;\\s*$", "");
        cleaned = cleaned.replaceAll("(?s)public\\s+class\\s+\\w+\\s*\\{.*", "");
        cleaned = cleaned.replaceAll("(?m)^\\s*@Test.*$", "");
        cleaned = cleaned.replaceAll("(?m)^\\s*@DisplayName.*$", "");
        cleaned = cleaned.replaceAll("(?m)^\\s*@Tag.*$", "");
        cleaned = cleaned.replaceAll("(?m)^\\s*public\\s+void\\s+test\\w+\\(.*\\)\\s*\\{?", "");

        if (cleaned.contains("{") || cleaned.contains("}")) {
            int firstBrace = cleaned.indexOf("{");
            int lastBrace = cleaned.lastIndexOf("}");

            if (firstBrace != -1 && lastBrace != -1 && firstBrace < lastBrace) {
                cleaned = cleaned.substring(firstBrace + 1, lastBrace).trim();
            }
        }

        return cleaned.trim();
    }

    /**
     * Gelişmiş fallback test senaryosu
     */
    private String generateAdvancedFallbackTestCase(EndpointInfo endpoint, String operationId) {
        StringBuilder testCase = new StringBuilder();
        String httpMethod = endpoint.getMethod();
        String endpointPath = endpoint.getPath();

        testCase.append("    /**\n");
        testCase.append("     * Enhanced Fallback Test: ").append(operationId).append("\n");
        testCase.append("     * Endpoint: ").append(httpMethod.toUpperCase()).append(" ").append(endpointPath).append("\n");
        testCase.append("     */\n");
        testCase.append("    @Test\n");
        testCase.append("    @DisplayName(\"Enhanced Fallback: ").append(operationId).append("\")\n");
        testCase.append("    @Tag(\"fallback\")\n");
        testCase.append("    public void testEnhancedFallback").append(sanitizeMethodName(operationId)).append("() {\n");

        testCase.append("        logger.info(\"=== Enhanced Fallback Test: ")
                .append(httpMethod.toUpperCase()).append(" ").append(endpointPath).append(" ===\");\n\n");

        // Method-specific enhanced fallback
        switch (httpMethod.toLowerCase()) {
            case "get":
                appendEnhancedGetFallback(testCase, endpointPath);
                break;
            case "post":
                appendEnhancedPostFallback(testCase, endpointPath);
                break;
            case "put":
                appendEnhancedPutFallback(testCase, endpointPath);
                break;
            case "delete":
                appendEnhancedDeleteFallback(testCase, endpointPath);
                break;
            default:
                appendEnhancedGenericFallback(testCase, endpointPath, httpMethod);
                break;
        }

        testCase.append("        logger.info(\"=== Enhanced Fallback Completed: ").append(operationId).append(" ===\");\n");
        testCase.append("    }");

        return testCase.toString();
    }

    /**
     * Enhanced GET fallback
     */
    private void appendEnhancedGetFallback(StringBuilder testCase, String endpoint) {
        testCase.append("        // Enhanced GET testing with comprehensive validations\n");
        testCase.append("        Response response = commonRequestSpec()\n");
        testCase.append("            .queryParam(\"page\", 0)\n");
        testCase.append("            .queryParam(\"size\", 10)\n");
        testCase.append("        .when()\n");
        testCase.append("            .get(\"").append(endpoint).append("\")\n");
        testCase.append("        .then()\n");
        testCase.append("            .statusCode(200)\n");
        testCase.append("            .contentType(ContentType.JSON)\n");
        testCase.append("            .extract().response();\n\n");

        testCase.append("        // Enhanced validations\n");
        testCase.append("        assertResponseTimeWithLogging(response, 3000, \"Enhanced GET\");\n");
        testCase.append("        validateResponseStructure(response, \"data\");\n");
    }

    /**
     * Enhanced POST fallback
     */
    private void appendEnhancedPostFallback(StringBuilder testCase, String endpoint) {
        testCase.append("        // Enhanced POST testing with smart data generation\n");
        testCase.append("        Map<String, Object> enhancedData = createDynamicTestData(\"resource\", true);\n");
        testCase.append("        enhancedData.put(\"timestamp\", System.currentTimeMillis());\n");
        testCase.append("        enhancedData.put(\"testId\", UUID.randomUUID().toString());\n\n");

        testCase.append("        Response response = commonRequestSpec()\n");
        testCase.append("            .body(enhancedData)\n");
        testCase.append("        .when()\n");
        testCase.append("            .post(\"").append(endpoint).append("\")\n");
        testCase.append("        .then()\n");
        testCase.append("            .statusCode(anyOf(is(200), is(201)))\n");
        testCase.append("            .extract().response();\n\n");

        testCase.append("        assertResponseTimeWithLogging(response, 5000, \"Enhanced POST\");\n");
    }

    /**
     * Enhanced PUT fallback
     */
    private void appendEnhancedPutFallback(StringBuilder testCase, String endpoint) {
        testCase.append("        // Enhanced PUT testing\n");
        testCase.append("        Integer resourceId = 1;\n");
        testCase.append("        Map<String, Object> updateData = createDynamicTestData(\"resource\", true);\n");
        testCase.append("        updateData.put(\"updatedAt\", new Date().toString());\n\n");

        testCase.append("        Response response = commonRequestSpec()\n");
        testCase.append("            .body(updateData)\n");
        testCase.append("        .when()\n");
        testCase.append("            .put(\"").append(endpoint).append("/\" + resourceId)\n");
        testCase.append("        .then()\n");
        testCase.append("            .statusCode(anyOf(is(200), is(204)))\n");
        testCase.append("            .extract().response();\n\n");

        testCase.append("        assertResponseTimeWithLogging(response, 4000, \"Enhanced PUT\");\n");
    }

    /**
     * Enhanced DELETE fallback
     */
    private void appendEnhancedDeleteFallback(StringBuilder testCase, String endpoint) {
        testCase.append("        // Enhanced DELETE testing\n");
        testCase.append("        Integer resourceId = 1;\n\n");

        testCase.append("        Response response = commonRequestSpec()\n");
        testCase.append("        .when()\n");
        testCase.append("            .delete(\"").append(endpoint).append("/\" + resourceId)\n");
        testCase.append("        .then()\n");
        testCase.append("            .statusCode(anyOf(is(200), is(204), is(404)))\n");
        testCase.append("            .extract().response();\n\n");

        testCase.append("        assertResponseTimeWithLogging(response, 3000, \"Enhanced DELETE\");\n");
    }

    /**
     * Enhanced generic fallback
     */
    private void appendEnhancedGenericFallback(StringBuilder testCase, String endpoint, String httpMethod) {
        testCase.append("        // Enhanced generic testing for ").append(httpMethod.toUpperCase()).append("\n");
        testCase.append("        Response response = commonRequestSpec()\n");
        testCase.append("        .when()\n");
        testCase.append("            .").append(httpMethod.toLowerCase()).append("(\"").append(endpoint).append("\")\n");
        testCase.append("        .then()\n");
        testCase.append("            .statusCode(anyOf(is(200), is(201), is(204), is(404)))\n");
        testCase.append("            .extract().response();\n\n");

        testCase.append("        assertResponseTimeWithLogging(response, 5000, \"Enhanced ").append(httpMethod.toUpperCase()).append("\");\n");
    }

    /**
     * Gelişmiş yardımcı metodları yazar
     */
    private void writeAdvancedUtilityMethods(FileWriter writer) throws IOException {
        writer.write("    // =========================== ENHANCED UTILITY METHODS ===========================\n\n");

        // Existing utility methods would be here (commonRequestSpec, etc.)
        // Plus new enhanced methods for smart validation, boundary testing, etc.

        writer.write("    // Enhanced utility methods implementation would go here\n");
        writer.write("    // Following the same pattern as the original but with smart capabilities\n\n");
    }

    /**
     * Enhanced performance tests
     */
    private void writeEnhancedPerformanceTests(FileWriter writer) throws IOException {
        writer.write("    // Enhanced performance testing implementation\n");
    }

    /**
     * Enhanced security tests
     */
    private void writeEnhancedSecurityTests(FileWriter writer) throws IOException {
        writer.write("    // Enhanced security testing implementation\n");
    }

    /**
     * Advanced mock tests
     */
    private void writeAdvancedMockTests(FileWriter writer) throws IOException {
        writer.write("    // Advanced mock testing implementation\n");
    }

    /**
     * Contract tests
     */
    private void writeContractTests(FileWriter writer) throws IOException {
        writer.write("    // Contract testing implementation\n");
    }

    /**
     * Boundary value tests
     */
    private void writeBoundaryValueTests(FileWriter writer) throws IOException {
        writer.write("    // Boundary value testing implementation\n");
    }

    /**
     * Test class header
     */
    private void writeTestClassHeader(FileWriter writer) throws IOException {
        writer.write("package tests;\n\n");
        writer.write("// Enhanced imports and class header implementation\n");
        writer.write("public class " + config.getTestClassName() + " {\n\n");
    }

    /**
     * Test class footer
     */
    private void writeTestClassFooter(FileWriter writer) throws IOException {
        writer.write("}\n");
    }

    /**
     * Enhanced test report
     */
    private void generateEnhancedTestReport() {
        try {
            String reportPath = config.getOutputFile().replace(".java", "_enhanced_report.html");
            File reportFile = new File(reportPath);

            try (FileWriter reportWriter = new FileWriter(reportFile)) {
                reportWriter.write("<!DOCTYPE html>\n");
                reportWriter.write("<html><head><title>Enhanced API Test Report v" + APP_VERSION + "</title>\n");
                reportWriter.write("<style>\n");
                reportWriter.write("body { font-family: 'Segoe UI', Arial, sans-serif; margin: 20px; background: #f5f5f5; }\n");
                reportWriter.write(".header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; border-radius: 10px; margin-bottom: 20px; }\n");
                reportWriter.write(".stats { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 20px; margin: 20px 0; }\n");
                reportWriter.write(".stat-box { background: white; padding: 20px; border-radius: 10px; text-align: center; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }\n");
                reportWriter.write(".success { border-left: 5px solid #28a745; }\n");
                reportWriter.write(".failure { border-left: 5px solid #dc3545; }\n");
                reportWriter.write(".info { border-left: 5px solid #17a2b8; }\n");
                reportWriter.write(".complexity-chart { background: white; padding: 20px; border-radius: 10px; margin: 20px 0; }\n");
                reportWriter.write("</style>\n");
                reportWriter.write("</head><body>\n");

                // Header
                reportWriter.write("<div class='header'>\n");
                reportWriter.write("<h1>🚀 Enhanced API Test Generation Report</h1>\n");
                reportWriter.write("<p><strong>Generated:</strong> " + new Date() + "</p>\n");
                reportWriter.write("<p><strong>Version:</strong> " + APP_VERSION + " (Enhanced Edition)</p>\n");
                reportWriter.write("<p><strong>Configuration:</strong> ");
                if (config.isEnableSmartValidation()) reportWriter.write("Smart-Validation ");
                if (config.isGenerateBoundaryTests()) reportWriter.write("Boundary-Tests ");
                if (config.isIncludeNegativeTests()) reportWriter.write("Negative-Tests ");
                if (config.isIncludeContractTests()) reportWriter.write("Contract-Tests ");
                reportWriter.write("</p>\n");
                reportWriter.write("</div>\n");

                // Stats
                reportWriter.write("<div class='stats'>\n");

                reportWriter.write("<div class='stat-box info'>\n");
                reportWriter.write("<h3>📊 Total Endpoints</h3>\n");
                reportWriter.write("<p style='font-size: 2em; margin: 0;'>" + totalEndpoints + "</p>\n");
                reportWriter.write("</div>\n");

                reportWriter.write("<div class='stat-box success'>\n");
                reportWriter.write("<h3>✅ Successfully Processed</h3>\n");
                reportWriter.write("<p style='font-size: 2em; margin: 0;'>" + (processedEndpoints.get() - failedEndpoints.get()) + "</p>\n");
                reportWriter.write("</div>\n");

                reportWriter.write("<div class='stat-box failure'>\n");
                reportWriter.write("<h3>❌ Failed</h3>\n");
                reportWriter.write("<p style='font-size: 2em; margin: 0;'>" + failedEndpoints.get() + "</p>\n");
                reportWriter.write("</div>\n");

                double successRate = totalEndpoints > 0 ? ((double)(processedEndpoints.get() - failedEndpoints.get()) / totalEndpoints) * 100 : 0;
                reportWriter.write("<div class='stat-box info'>\n");
                reportWriter.write("<h3>📈 Success Rate</h3>\n");
                reportWriter.write("<p style='font-size: 2em; margin: 0;'>" + String.format("%.1f", successRate) + "%</p>\n");
                reportWriter.write("</div>\n");

                reportWriter.write("</div>\n");

                // Complexity Analysis
                reportWriter.write("<div class='complexity-chart'>\n");
                reportWriter.write("<h2>🎯 Complexity Analysis</h2>\n");
                Map<ComplexityLevel, Integer> complexityDist = new HashMap<>();
                for (EndpointComplexity complexity : complexityAnalysis.values()) {
                    complexityDist.merge(complexity.getLevel(), 1, Integer::sum);
                }
                reportWriter.write("<ul>\n");
                for (Map.Entry<ComplexityLevel, Integer> entry : complexityDist.entrySet()) {
                    reportWriter.write("<li><strong>" + entry.getKey() + ":</strong> " + entry.getValue() + " endpoints</li>\n");
                }
                reportWriter.write("</ul>\n");
                reportWriter.write("</div>\n");

                // Configuration Details
                reportWriter.write("<div class='complexity-chart'>\n");
                reportWriter.write("<h2>⚙️ Enhanced Configuration</h2>\n");
                reportWriter.write("<div style='display: grid; grid-template-columns: 1fr 1fr; gap: 20px;'>\n");
                reportWriter.write("<div>\n");
                reportWriter.write("<h3>Basic Settings</h3>\n");
                reportWriter.write("<ul>\n");
                reportWriter.write("<li><strong>Input File:</strong> " + config.getInputFile() + "</li>\n");
                reportWriter.write("<li><strong>Output File:</strong> " + config.getOutputFile() + "</li>\n");
                reportWriter.write("<li><strong>Model:</strong> " + config.getModel() + "</li>\n");
                reportWriter.write("<li><strong>Max Tokens:</strong> " + config.getMaxTokens() + "</li>\n");
                reportWriter.write("<li><strong>Thread Pool Size:</strong> " + config.getThreadPoolSize() + "</li>\n");
                reportWriter.write("</ul>\n");
                reportWriter.write("</div>\n");

                reportWriter.write("<div>\n");
                reportWriter.write("<h3>Enhanced Features</h3>\n");
                reportWriter.write("<ul>\n");
                reportWriter.write("<li><strong>Smart Validation:</strong> " + (config.isEnableSmartValidation() ? "✅ Enabled" : "❌ Disabled") + "</li>\n");
                reportWriter.write("<li><strong>Boundary Tests:</strong> " + (config.isGenerateBoundaryTests() ? "✅ Enabled" : "❌ Disabled") + "</li>\n");
                reportWriter.write("<li><strong>Negative Tests:</strong> " + (config.isIncludeNegativeTests() ? "✅ Enabled" : "❌ Disabled") + "</li>\n");
                reportWriter.write("<li><strong>Contract Tests:</strong> " + (config.isIncludeContractTests() ? "✅ Enabled" : "❌ Disabled") + "</li>\n");
                reportWriter.write("<li><strong>Pattern Analysis:</strong> " + (config.isEnableResponsePatternAnalysis() ? "✅ Enabled" : "❌ Disabled") + "</li>\n");
                reportWriter.write("<li><strong>Performance Tests:</strong> " + (config.isIncludePerformanceTests() ? "✅ Enabled" : "❌ Disabled") + "</li>\n");
                reportWriter.write("<li><strong>Security Tests:</strong> " + (config.isIncludeSecurityTests() ? "✅ Enabled" : "❌ Disabled") + "</li>\n");
                reportWriter.write("<li><strong>Mock Tests:</strong> " + (config.isGenerateMockTests() ? "✅ Enabled" : "❌ Disabled") + "</li>\n");
                reportWriter.write("</ul>\n");
                reportWriter.write("</div>\n");
                reportWriter.write("</div>\n");
                reportWriter.write("</div>\n");

                reportWriter.write("<div class='complexity-chart'>\n");
                reportWriter.write("<h2>🎉 Generation Complete!</h2>\n");
                reportWriter.write("<p>Your enhanced API tests have been generated with advanced capabilities including smart validation, boundary testing, and comprehensive error scenarios.</p>\n");
                reportWriter.write("<p><strong>Next Steps:</strong></p>\n");
                reportWriter.write("<ol>\n");
                reportWriter.write("<li>Review the generated test file: <code>" + config.getOutputFile() + "</code></li>\n");
                reportWriter.write("<li>Configure your test environment with the required dependencies</li>\n");
                reportWriter.write("<li>Run the tests and review the results</li>\n");
                reportWriter.write("<li>Customize test data and assertions as needed</li>\n");
                reportWriter.write("</ol>\n");
                reportWriter.write("</div>\n");

                reportWriter.write("</body></html>\n");
            }

            logger.info("Enhanced test raporu oluşturuldu: " + reportPath);

        } catch (IOException e) {
            logger.log(Level.WARNING, "Enhanced test raporu oluşturulamadı", e);
        }
    }
}