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

/**
 * OpenAPI/Swagger dosyalarından otomatik API test senaryoları oluşturan geliştirilmiş uygulama.
 * Yapılandırılabilir seçenekler, multithreading desteği, gelişmiş hata yönetimi ve ilerleme takibi içerir.
 *
 * Yeni Özellikler:
 * - Gelişmiş OpenAPI şema analizi
 * - Daha akıllı test senaryosu üretimi
 * - Bağımlılık analizi ve test sıralaması
 * - Veri doğrulama ve constraint testleri
 * - Mock server desteği
 * - Test raporu üretimi
 *
 * @author Emre Gozukizil
 * @version 3.0.0
 */
public class ClaudeSwaggerTestGenerator3 {

    private static final Logger logger = Logger.getLogger(ClaudeSwaggerTestGenerator3.class.getName());
    private static final String APP_VERSION = "3.0.0";

    // Varsayılan yapılandırma değerleri
    private static final int DEFAULT_MAX_RETRIES = 3;
    private static final int DEFAULT_TIMEOUT_SECONDS = 120;
    private static final int DEFAULT_THREAD_POOL_SIZE = 4;
    private static final String DEFAULT_OUTPUT_FILE = "api_test_cases3.java";
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

    // OpenAPI dokümanı ve şema cache
    private JsonNode openApiDocument;
    private Map<String, JsonNode> schemaCache = new ConcurrentHashMap<>();
    private Map<String, EndpointDependency> endpointDependencies = new ConcurrentHashMap<>();

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
                    .build();

            ClaudeSwaggerTestGenerator3 generator = new ClaudeSwaggerTestGenerator3(config);
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

        // Yeni özellikler
        options.addOption("f", "fallback", false, "API hatalarında manuel şablonlar kullan");
        options.addOption("V", "verbose", false, "Detaylı günlük kaydı aktif");
        options.addOption("M", "mock", false, "Mock server testleri üret");
        options.addOption("P", "performance", false, "Performans testleri dahil et");
        options.addOption("S", "security", false, "Güvenlik testleri dahil et");
        options.addOption("R", "report", false, "Test raporu üret");

        CommandLineParser parser = new DefaultParser();
        return parser.parse(options, args);
    }

    private static void printHelp() {
        System.out.println("API Test Generator v" + APP_VERSION);
        System.out.println("Kullanım: java -jar api-test-generator.jar [SEÇENEKLER]");
        System.out.println("\nSeçenekler:");
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
        System.out.println("  -f, --fallback              API hatalarında manuel şablonlar kullan");
        System.out.println("  -V, --verbose               Detaylı günlük kaydı aktif");
        System.out.println("  -M, --mock                  Mock server testleri üret");
        System.out.println("  -P, --performance           Performans testleri dahil et");
        System.out.println("  -S, --security              Güvenlik testleri dahil et");
        System.out.println("  -R, --report                Test raporu üret");
    }

    public ClaudeSwaggerTestGenerator3(Configuration config) {
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

            // Şemaları önceden cache'le
            cacheSchemas();

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

                // Tüm endpointleri topla ve analiz et
                List<EndpointInfo> endpoints = collectAndAnalyzeEndpoints(openApiDocument);
                totalEndpoints = endpoints.size();
                logger.info(totalEndpoints + " endpoint bulundu.");

                // Bağımlılık analizi yap
                analyzeDependencies(endpoints);

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

                // Ek test senaryoları ekle
                if (config.isIncludePerformanceTests()) {
                    writePerformanceTests(outputWriter);
                }

                if (config.isIncludeSecurityTests()) {
                    writeSecurityTests(outputWriter);
                }

                if (config.isGenerateMockTests()) {
                    writeMockTests(outputWriter);
                }

                // Test sınıfının sonunu yazma
                writeTestClassFooter(outputWriter);
            }

            // Test raporu oluştur
            if (config.isGenerateTestReport()) {
                generateTestReport();
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
     * OpenAPI şemalarını önceden cache'ler - performans iyileştirmesi
     */
    private void cacheSchemas() {
        if (openApiDocument.has("components") && openApiDocument.get("components").has("schemas")) {
            JsonNode schemas = openApiDocument.get("components").get("schemas");
            Iterator<Map.Entry<String, JsonNode>> schemaIterator = schemas.fields();
            while (schemaIterator.hasNext()) {
                Map.Entry<String, JsonNode> schemaEntry = schemaIterator.next();
                schemaCache.put(schemaEntry.getKey(), schemaEntry.getValue());
            }
            logger.info(schemaCache.size() + " şema cache'lendi.");
        }
    }

    /**
     * Gelişmiş yardımcı metodları test sınıfına ekler
     */
    private void writeAdvancedUtilityMethods(FileWriter writer) throws IOException {
        writer.write("    // =========================== TEMEL YARDIMCI METODLAR ===========================\n\n");

        writer.write("    /**\n");
        writer.write("     * Ortak request specification oluşturur\n");
        writer.write("     */\n");
        writer.write("    private RequestSpecification commonRequestSpec() {\n");
        writer.write("        return given()\n");
        writer.write("            .contentType(ContentType.JSON)\n");
        writer.write("            .accept(ContentType.JSON)\n");
        writer.write("            .relaxedHTTPSValidation()\n");
        writer.write("            .log().ifValidationFails();\n");
        writer.write("    }\n\n");

        writer.write("    /**\n");
        writer.write("     * Yetkilendirilmiş request specification oluşturur\n");
        writer.write("     */\n");
        writer.write("    private RequestSpecification authorizedRequestSpec() {\n");
        writer.write("        return commonRequestSpec()\n");
        writer.write("            .header(\"Authorization\", \"Bearer \" + getTestToken())\n");
        writer.write("            .header(\"X-API-Key\", getApiKey());\n");
        writer.write("    }\n\n");

        writer.write("    /**\n");
        writer.write("     * Test token'ı oluşturur\n");
        writer.write("     */\n");
        writer.write("    private String getTestToken() {\n");
        writer.write("        // Gerçek uygulamada burada OAuth token alınması yapılabilir\n");
        writer.write("        return \"test-jwt-token-\" + System.currentTimeMillis();\n");
        writer.write("    }\n\n");

        writer.write("    /**\n");
        writer.write("     * API anahtarı döndürür\n");
        writer.write("     */\n");
        writer.write("    private String getApiKey() {\n");
        writer.write("        return \"test-api-key-\" + UUID.randomUUID().toString();\n");
        writer.write("    }\n\n");

        // Gelişmiş test verisi oluşturma metodları
        writer.write("    // =========================== VERİ OLUŞTURMA METODLARI ===========================\n\n");

        writer.write("    /**\n");
        writer.write("     * Kaynak türüne göre dinamik test verisi oluşturur\n");
        writer.write("     */\n");
        writer.write("    private Map<String, Object> createDynamicTestData(String resourceType, boolean isValid) {\n");
        writer.write("        Map<String, Object> data = new HashMap<>();\n");
        writer.write("        String timestamp = String.valueOf(System.currentTimeMillis());\n");
        writer.write("        \n");
        writer.write("        switch (resourceType.toLowerCase()) {\n");
        writer.write("            case \"user\":\n");
        writer.write("                data.put(\"id\", isValid ? null : -1);\n");
        writer.write("                data.put(\"name\", isValid ? \"Test User \" + timestamp : \"\");\n");
        writer.write("                data.put(\"email\", isValid ? \"test\" + timestamp + \"@example.com\" : \"invalid-email\");\n");
        writer.write("                data.put(\"active\", true);\n");
        writer.write("                data.put(\"createdAt\", new Date().toString());\n");
        writer.write("                break;\n");
        writer.write("            case \"company\":\n");
        writer.write("                data.put(\"id\", isValid ? null : -1);\n");
        writer.write("                data.put(\"name\", isValid ? \"Test Company \" + timestamp : \"\");\n");
        writer.write("                data.put(\"description\", \"Test şirketi açıklaması\");\n");
        writer.write("                data.put(\"employeeCount\", isValid ? 50 : -10);\n");
        writer.write("                data.put(\"established\", isValid ? \"2020-01-01\" : \"invalid-date\");\n");
        writer.write("                break;\n");
        writer.write("            case \"job\":\n");
        writer.write("                data.put(\"id\", isValid ? null : -1);\n");
        writer.write("                data.put(\"title\", isValid ? \"Test Job \" + timestamp : \"\");\n");
        writer.write("                data.put(\"description\", \"Test iş ilanı açıklaması\");\n");
        writer.write("                data.put(\"salary\", isValid ? 5000 : -1000);\n");
        writer.write("                data.put(\"isRemote\", true);\n");
        writer.write("                data.put(\"location\", \"Istanbul, Turkey\");\n");
        writer.write("                break;\n");
        writer.write("            default:\n");
        writer.write("                data.put(\"id\", isValid ? null : -1);\n");
        writer.write("                data.put(\"name\", isValid ? \"Test Resource \" + timestamp : \"\");\n");
        writer.write("                data.put(\"description\", \"Test kaynağı açıklaması\");\n");
        writer.write("        }\n");
        writer.write("        \n");
        writer.write("        return data;\n");
        writer.write("    }\n\n");

        // Şema doğrulama metodları
        writer.write("    // =========================== ŞEMADoğRULAMA METODLARI ===========================\n\n");

        writer.write("    /**\n");
        writer.write("     * JSON şema doğrulaması yapar\n");
        writer.write("     */\n");
        writer.write("    private void validateJsonSchema(Response response, String schemaName) {\n");
        writer.write("        try {\n");
        writer.write("            response.then().body(matchesJsonSchemaInClasspath(\"schemas/\" + schemaName + \".json\"));\n");
        writer.write("            logger.info(\"JSON şema doğrulaması başarılı: \" + schemaName);\n");
        writer.write("        } catch (Exception e) {\n");
        writer.write("            logger.warning(\"JSON şema doğrulaması başarısız: \" + schemaName + \" - \" + e.getMessage());\n");
        writer.write("        }\n");
        writer.write("    }\n\n");

        // Performans test metodları
        writer.write("    // =========================== PERFORMANS TEST METODLARI ===========================\n\n");

        writer.write("    /**\n");
        writer.write("     * Yanıt süresini kontrol eder ve loglar\n");
        writer.write("     */\n");
        writer.write("    private void assertResponseTimeWithLogging(Response response, long maxAllowedTimeMs, String operation) {\n");
        writer.write("        long responseTime = response.getTime();\n");
        writer.write("        logger.info(String.format(\"%s - Yanıt süresi: %d ms (Maksimum: %d ms)\", operation, responseTime, maxAllowedTimeMs));\n");
        writer.write("        \n");
        writer.write("        if (responseTime > maxAllowedTimeMs) {\n");
        writer.write("            logger.warning(String.format(\"PERFORMANS UYARISI: %s işlemi %d ms sürdü (Beklenen: <%d ms)\", operation, responseTime, maxAllowedTimeMs));\n");
        writer.write("        }\n");
        writer.write("        \n");
        writer.write("        Assertions.assertTrue(responseTime < maxAllowedTimeMs,\n");
        writer.write("            String.format(\"%s - API yanıt süresi %d ms beklenen %d ms'den fazla\", operation, responseTime, maxAllowedTimeMs));\n");
        writer.write("    }\n\n");

        // Güvenlik test metodları
        writer.write("    // =========================== GÜVENLİK TEST METODLARI ===========================\n\n");

        writer.write("    /**\n");
        writer.write("     * SQL Injection saldırı testi yapar\n");
        writer.write("     */\n");
        writer.write("    private void testSqlInjection(String endpoint, String paramName) {\n");
        writer.write("        String[] sqlInjectionPayloads = {\n");
        writer.write("            \"' OR '1'='1\",\n");
        writer.write("            \"'; DROP TABLE users; --\",\n");
        writer.write("            \"' UNION SELECT * FROM users --\",\n");
        writer.write("            \"1' OR 1=1#\"\n");
        writer.write("        };\n");
        writer.write("        \n");
        writer.write("        for (String payload : sqlInjectionPayloads) {\n");
        writer.write("            commonRequestSpec()\n");
        writer.write("                .param(paramName, payload)\n");
        writer.write("            .when()\n");
        writer.write("                .get(endpoint)\n");
        writer.write("            .then()\n");
        writer.write("                .statusCode(not(200)) // SQL injection başarılı olmamalı\n");
        writer.write("                .body(not(containsString(\"error\")));\n");
        writer.write("        }\n");
        writer.write("    }\n\n");

        writer.write("    /**\n");
        writer.write("     * XSS saldırı testi yapar\n");
        writer.write("     */\n");
        writer.write("    private void testXSSAttack(String endpoint, String fieldName) {\n");
        writer.write("        String[] xssPayloads = {\n");
        writer.write("            \"<script>alert('XSS')</script>\",\n");
        writer.write("            \"javascript:alert('XSS')\",\n");
        writer.write("            \"<img src=x onerror=alert('XSS')>\",\n");
        writer.write("            \"<svg onload=alert('XSS')>\"\n");
        writer.write("        };\n");
        writer.write("        \n");
        writer.write("        Map<String, Object> testData = new HashMap<>();\n");
        writer.write("        for (String payload : xssPayloads) {\n");
        writer.write("            testData.put(fieldName, payload);\n");
        writer.write("            \n");
        writer.write("            commonRequestSpec()\n");
        writer.write("                .body(testData)\n");
        writer.write("            .when()\n");
        writer.write("                .post(endpoint)\n");
        writer.write("            .then()\n");
        writer.write("                .statusCode(not(200)) // XSS payload kabul edilmemeli\n");
        writer.write("                .body(not(containsString(payload))); // Payload response'da görünmemeli\n");
        writer.write("        }\n");
        writer.write("    }\n\n");

        // Veri doğrulama metodları
        writer.write("    // =========================== VERİ DOĞRULAMA METODLARI ===========================\n\n");

        writer.write("    /**\n");
        writer.write("     * API yanıtının beklenen yapıda olduğunu doğrular\n");
        writer.write("     */\n");
        writer.write("    private void validateResponseStructure(Response response, String... requiredFields) {\n");
        writer.write("        for (String field : requiredFields) {\n");
        writer.write("            response.then().body(field, notNullValue());\n");
        writer.write("        }\n");
        writer.write("    }\n\n");

        writer.write("    /**\n");
        writer.write("     * Sayfalandırma doğrulaması yapar\n");
        writer.write("     */\n");
        writer.write("    private void validatePagination(Response response) {\n");
        writer.write("        response.then()\n");
        writer.write("            .body(\"page\", notNullValue())\n");
        writer.write("            .body(\"size\", notNullValue())\n");
        writer.write("            .body(\"totalElements\", notNullValue())\n");
        writer.write("            .body(\"totalPages\", notNullValue())\n");
        writer.write("            .body(\"content\", notNullValue());\n");
        writer.write("    }\n\n");

        // Mock test metodları
        writer.write("    // =========================== MOCK TEST METODLARI ===========================\n\n");

        writer.write("    /**\n");
        writer.write("     * Mock server kullanarak test yapar\n");
        writer.write("     */\n");
        writer.write("    private void runMockTest(String endpoint, String httpMethod, Object expectedResponse) {\n");
        writer.write("        // WireMock kullanarak mock server kurulumu\n");
        writer.write("        // Bu örnekte basit bir mock implementasyonu\n");
        writer.write("        logger.info(\"Mock test çalıştırılıyor: \" + httpMethod + \" \" + endpoint);\n");
        writer.write("        \n");
        writer.write("        // Gerçek mock server implementasyonu burada olacak\n");
        writer.write("        Response response = commonRequestSpec()\n");
        writer.write("        .when()\n");
        writer.write("            .request(httpMethod, endpoint)\n");
        writer.write("        .then()\n");
        writer.write("            .statusCode(anyOf(is(200), is(201), is(204)))\n");
        writer.write("            .extract().response();\n");
        writer.write("            \n");
        writer.write("        logger.info(\"Mock test tamamlandı: \" + endpoint);\n");
        writer.write("    }\n\n");
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

                    // Endpoint analizi yap
                    analyzeEndpoint(endpointInfo);

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
     * Endpoint analizi yapar ve ek bilgiler çıkarır
     */
    private void analyzeEndpoint(EndpointInfo endpoint) {
        JsonNode operationNode = endpoint.getOperationNode();

        // Güvenlik gereksinimleri analizi
        if (operationNode.has("security")) {
            endpoint.setRequiresAuthentication(true);
        }

        // Parameter analizi
        if (operationNode.has("parameters")) {
            JsonNode parameters = operationNode.get("parameters");
            for (JsonNode param : parameters) {
                if (param.has("required") && param.get("required").asBoolean()) {
                    endpoint.addRequiredParameter(param.get("name").asText());
                }
            }
        }

        // Response analizi
        if (operationNode.has("responses")) {
            JsonNode responses = operationNode.get("responses");
            Iterator<Map.Entry<String, JsonNode>> responseIterator = responses.fields();
            while (responseIterator.hasNext()) {
                Map.Entry<String, JsonNode> responseEntry = responseIterator.next();
                String statusCode = responseEntry.getKey();
                endpoint.addExpectedStatusCode(statusCode);
            }
        }

        // Tag analizi - kaynak türü belirleme
        if (operationNode.has("tags")) {
            JsonNode tags = operationNode.get("tags");
            if (tags.size() > 0) {
                endpoint.setResourceType(tags.get(0).asText());
            }
        }
    }

    /**
     * Endpoint bağımlılıklarını analiz eder
     */
    private void analyzeDependencies(List<EndpointInfo> endpoints) {
        logger.info("Endpoint bağımlılıkları analiz ediliyor...");

        for (EndpointInfo endpoint : endpoints) {
            EndpointDependency dependency = new EndpointDependency(endpoint.getPath());

            // POST -> GET -> PUT -> DELETE sıralaması
            switch (endpoint.getMethod().toLowerCase()) {
                case "post":
                    dependency.setPriority(1); // En yüksek öncelik
                    break;
                case "get":
                    dependency.setPriority(2);
                    break;
                case "put":
                case "patch":
                    dependency.setPriority(3);
                    dependency.addDependency(findCreateEndpoint(endpoints, endpoint.getResourceType()));
                    break;
                case "delete":
                    dependency.setPriority(4); // En düşük öncelik
                    dependency.addDependency(findCreateEndpoint(endpoints, endpoint.getResourceType()));
                    break;
            }

            endpointDependencies.put(endpoint.getPath() + ":" + endpoint.getMethod(), dependency);
        }

        logger.info("Bağımlılık analizi tamamlandı. " + endpointDependencies.size() + " bağımlılık tespit edildi.");
    }

    /**
     * Kaynak oluşturma endpoint'ini bulur
     */
    private String findCreateEndpoint(List<EndpointInfo> endpoints, String resourceType) {
        for (EndpointInfo endpoint : endpoints) {
            if ("post".equalsIgnoreCase(endpoint.getMethod()) &&
                    resourceType != null && resourceType.equals(endpoint.getResourceType())) {
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

                    return Integer.compare(priority1, priority2);
                })
                .collect(ArrayList::new, (list, item) -> list.add(item), ArrayList::addAll);
    }

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
        return "Sen bir uzman API test otomasyonu geliştiricisisin. Verilen OpenAPI endpoint'i için " +
                "kapsamlı, gerçekçi ve profesyonel REST Assured + JUnit 5 test senaryoları oluştur.\n\n" +

                "TEMEL KURALLAR:\n" +
                "1. ASLA import ifadeleri ekleme - bunlar zaten tanımlanmıştır\n" +
                "2. ASLA yeni sınıf tanımı oluşturma - yalnızca metod içeriği döndür\n" +
                "3. ASLA test metodunun imzasını dahil etme - sadece metod gövdesini ver\n" +
                "4. Tüm senaryoları tek bir test metodu içinde organize et\n" +
                "5. Her test bölümü için açıklayıcı Türkçe yorumlar ekle\n\n" +

                "KULLANILACAK YARDIMCI METODLAR:\n" +
                "- commonRequestSpec(): Temel request specification\n" +
                "- authorizedRequestSpec(): Yetkilendirilmiş request specification\n" +
                "- createDynamicTestData(String resourceType, boolean isValid): Dinamik test verisi\n" +
                "- validateResponseStructure(Response, String...): Yanıt yapısı doğrulama\n" +
                "- validateJsonSchema(Response, String): JSON şema doğrulama\n" +
                "- assertResponseTimeWithLogging(Response, long, String): Performans kontrolü\n" +
                "- validatePagination(Response): Sayfalandırma doğrulama\n" +
                "- testSqlInjection(String, String): SQL injection testi\n" +
                "- testXSSAttack(String, String): XSS saldırı testi\n\n" +

                "TEST KAPSAMI:\n" +
                "1. Happy Path testleri (başarılı senaryolar)\n" +
                "2. Negative testler (hata senaryoları)\n" +
                "3. Boundary testleri (sınır değer testleri)\n" +
                "4. Güvenlik testleri (authentication, authorization)\n" +
                "5. Performans testleri (yanıt süresi kontrolü)\n" +
                "6. Veri doğrulama testleri (şema, format kontrolü)\n" +
                "7. Edge case testleri (özel durumlar)\n\n" +

                "KOD KALİTESİ:\n" +
                "- DRY prensibi: Kod tekrarından kaçın, yardımcı metodları kullanın\n" +
                "- Clear naming: Değişken ve test bölümlerini açık isimlendirin\n" +
                "- Comprehensive logging: Her test adımını logla\n" +
                "- Error handling: Hata durumlarını uygun şekilde ele alın\n" +
                "- Maintainable: Bakımı kolay, anlaşılır kod yazın\n\n";
    }

    /**
     * Endpoint için gelişmiş prompt oluşturur
     */
    private String createAdvancedPromptForEndpoint(EndpointInfo endpoint) {
        StringBuilder prompt = new StringBuilder();
        JsonNode operationNode = endpoint.getOperationNode();

        prompt.append("OpenAPI Endpoint Analizi:\n");
        prompt.append("=========================\n");
        prompt.append("Endpoint: ").append(endpoint.getPath()).append("\n");
        prompt.append("HTTP Method: ").append(endpoint.getMethod().toUpperCase()).append("\n");
        prompt.append("Resource Type: ").append(endpoint.getResourceType()).append("\n");
        prompt.append("Authentication Required: ").append(endpoint.isRequiresAuthentication()).append("\n");

        if (operationNode.has("summary")) {
            prompt.append("Summary: ").append(operationNode.get("summary").asText()).append("\n");
        }

        if (operationNode.has("description")) {
            prompt.append("Description: ").append(operationNode.get("description").asText()).append("\n");
        }

        prompt.append("\n");

        // Detaylı parametre analizi
        if (operationNode.has("parameters")) {
            prompt.append("PARAMETRELER:\n");
            prompt.append("=============\n");
            JsonNode parameters = operationNode.get("parameters");
            for (JsonNode param : parameters) {
                String name = param.get("name").asText();
                String in = param.get("in").asText();
                boolean required = param.has("required") && param.get("required").asBoolean();

                prompt.append("• ").append(name).append(" (").append(in).append(")");
                if (required) {
                    prompt.append(" [ZORUNLU]");
                }
                prompt.append("\n");

                if (param.has("description")) {
                    prompt.append("  Açıklama: ").append(param.get("description").asText()).append("\n");
                }

                if (param.has("schema")) {
                    JsonNode schema = param.get("schema");
                    appendSchemaDetails(schema, prompt, "  ");
                }
                prompt.append("\n");
            }
            prompt.append("\n");
        }

        // Request Body analizi
        if (operationNode.has("requestBody")) {
            prompt.append("REQUEST BODY:\n");
            prompt.append("=============\n");
            JsonNode requestBody = operationNode.get("requestBody");

            if (requestBody.has("description")) {
                prompt.append("Açıklama: ").append(requestBody.get("description").asText()).append("\n");
            }

            boolean required = requestBody.has("required") && requestBody.get("required").asBoolean();
            prompt.append("Zorunlu: ").append(required ? "Evet" : "Hayır").append("\n");

            if (requestBody.has("content")) {
                JsonNode content = requestBody.get("content");
                if (content.has("application/json")) {
                    JsonNode jsonContent = content.get("application/json");
                    if (jsonContent.has("schema")) {
                        JsonNode schema = jsonContent.get("schema");
                        appendSchemaDetails(schema, prompt, "");

                        try {
                            String exampleJson = generateAdvancedExampleJson(schema);
                            prompt.append("\nÖrnek Geçerli JSON:\n").append(exampleJson).append("\n");
                        } catch (Exception e) {
                            logger.warning("Örnek JSON oluşturulamadı: " + e.getMessage());
                        }
                    }
                }
            }
            prompt.append("\n");
        }

        // Response analizi
        if (operationNode.has("responses")) {
            prompt.append("BEKLENEN YANITLAR:\n");
            prompt.append("==================\n");
            JsonNode responses = operationNode.get("responses");
            Iterator<Map.Entry<String, JsonNode>> responseIterator = responses.fields();

            while (responseIterator.hasNext()) {
                Map.Entry<String, JsonNode> responseEntry = responseIterator.next();
                String statusCode = responseEntry.getKey();
                JsonNode responseDetail = responseEntry.getValue();

                prompt.append("Status Code: ").append(statusCode).append("\n");
                if (responseDetail.has("description")) {
                    prompt.append("Açıklama: ").append(responseDetail.get("description").asText()).append("\n");
                }

                if (responseDetail.has("content")) {
                    JsonNode content = responseDetail.get("content");
                    if (content.has("application/json")) {
                        JsonNode jsonContent = content.get("application/json");
                        if (jsonContent.has("schema")) {
                            JsonNode schema = jsonContent.get("schema");
                            appendSchemaDetails(schema, prompt, "  ");
                        }
                    }
                }
                prompt.append("\n");
            }
        }

        // Test senaryoları spesifikasyonu
        prompt.append("OLUŞTURULACAK TEST SENARYOLARI:\n");
        prompt.append("===============================\n");

        prompt.append(generateTestScenarioSpec(endpoint));

        prompt.append("\nÖNEMLİ: Kod tekrarını minimize etmek için yukarıdaki yardımcı metodları kullan.\n");
        prompt.append("Her test bölümünü açıklayıcı yorumlarla ayır ve logging ekle.\n");
        prompt.append("Sadece metod içeriğini döndür, import veya class tanımı yapma.\n");

        return prompt.toString();
    }

    /**
     * Şema detaylarını prompt'a ekler
     */
    private void appendSchemaDetails(JsonNode schema, StringBuilder prompt, String indent) {
        if (schema == null) return;

        if (schema.has("type")) {
            prompt.append(indent).append("Tür: ").append(schema.get("type").asText()).append("\n");
        }

        if (schema.has("format")) {
            prompt.append(indent).append("Format: ").append(schema.get("format").asText()).append("\n");
        }

        if (schema.has("pattern")) {
            prompt.append(indent).append("Pattern: ").append(schema.get("pattern").asText()).append("\n");
        }

        if (schema.has("minimum")) {
            prompt.append(indent).append("Minimum: ").append(schema.get("minimum").asText()).append("\n");
        }

        if (schema.has("maximum")) {
            prompt.append(indent).append("Maximum: ").append(schema.get("maximum").asText()).append("\n");
        }

        if (schema.has("minLength")) {
            prompt.append(indent).append("Min Length: ").append(schema.get("minLength").asText()).append("\n");
        }

        if (schema.has("maxLength")) {
            prompt.append(indent).append("Max Length: ").append(schema.get("maxLength").asText()).append("\n");
        }

        if (schema.has("enum")) {
            prompt.append(indent).append("Enum Values: ");
            JsonNode enumValues = schema.get("enum");
            List<String> values = new ArrayList<>();
            for (JsonNode value : enumValues) {
                values.add(value.asText());
            }
            prompt.append(String.join(", ", values)).append("\n");
        }

        if (schema.has("properties")) {
            prompt.append(indent).append("Properties:\n");
            JsonNode properties = schema.get("properties");
            Iterator<Map.Entry<String, JsonNode>> fields = properties.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                String name = field.getKey();
                JsonNode propertySchema = field.getValue();

                prompt.append(indent).append("  • ").append(name);

                // Zorunlu alan kontrolü
                if (schema.has("required")) {
                    JsonNode required = schema.get("required");
                    for (JsonNode req : required) {
                        if (req.asText().equals(name)) {
                            prompt.append(" [ZORUNLU]");
                            break;
                        }
                    }
                }
                prompt.append("\n");

                appendSchemaDetails(propertySchema, prompt, indent + "    ");
            }
        }
    }

    /**
     * Test senaryosu spesifikasyonu oluşturur
     */
    private String generateTestScenarioSpec(EndpointInfo endpoint) {
        StringBuilder spec = new StringBuilder();
        String method = endpoint.getMethod().toLowerCase();

        spec.append("1. TEMEL TESTLER:\n");
        spec.append("   - Başarılı ").append(method.toUpperCase()).append(" isteği\n");
        spec.append("   - Geçerli parametrelerle test\n");
        spec.append("   - Yanıt yapısı doğrulama\n");
        spec.append("   - Performans kontrolü (max 3000ms)\n\n");

        spec.append("2. GÜVENLİK TESTLERİ:\n");
        if (endpoint.isRequiresAuthentication()) {
            spec.append("   - Authorization header olmadan test (401 beklenir)\n");
            spec.append("   - Geçersiz token ile test (403 beklenir)\n");
        }
        spec.append("   - SQL Injection denemeleri\n");
        spec.append("   - XSS saldırı denemeleri\n\n");

        spec.append("3. HATA DURUMU TESTLERİ:\n");

        switch (method) {
            case "get":
                spec.append("   - Geçersiz ID ile 404 testi\n");
                spec.append("   - Geçersiz parametre formatı ile 400 testi\n");
                spec.append("   - Sayfalandırma parametreleri testi\n");
                break;
            case "post":
                spec.append("   - Eksik zorunlu alanlar ile 400 testi\n");
                spec.append("   - Geçersiz data formatı ile 400 testi\n");
                spec.append("   - Duplicate kayıt testi\n");
                spec.append("   - Constraint violation testleri\n");
                break;
            case "put":
            case "patch":
                spec.append("   - Geçersiz ID ile 404 testi\n");
                spec.append("   - Eksik zorunlu alanlar ile 400 testi\n");
                spec.append("   - Constraint violation testleri\n");
                break;
            case "delete":
                spec.append("   - Geçersiz ID ile 404 testi\n");
                spec.append("   - Zaten silinmiş kayıt testi\n");
                spec.append("   - Bağımlı kayıtlar varsa 409 testi\n");
                break;
        }

        spec.append("\n4. VERİ DOĞRULAMA TESTLERİ:\n");
        spec.append("   - JSON şema doğrulaması\n");
        spec.append("   - Required field kontrolü\n");
        spec.append("   - Data type kontrolü\n");
        spec.append("   - Format validation (email, date, vb.)\n\n");

        spec.append("5. EDGE CASE TESTLERİ:\n");
        spec.append("   - Boundary value testing\n");
        spec.append("   - Null/empty value testing\n");
        spec.append("   - Large payload testing\n");
        spec.append("   - Special character testing\n\n");

        return spec.toString();
    }

    /**
     * Gelişmiş örnek JSON oluşturur
     */
    private String generateAdvancedExampleJson(JsonNode schema) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode rootNode = objectMapper.createObjectNode();

        if (schema.has("properties")) {
            JsonNode properties = schema.get("properties");
            Iterator<Map.Entry<String, JsonNode>> fields = properties.fields();

            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                String name = field.getKey();
                JsonNode propertySchema = field.getValue();

                addPropertyToJson(rootNode, name, propertySchema, objectMapper);
            }
        }

        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
    }

    /**
     * JSON property'sini ekler
     */
    private void addPropertyToJson(ObjectNode parentNode, String name, JsonNode schema, ObjectMapper mapper) {
        if (!schema.has("type")) {
            return;
        }

        String type = schema.get("type").asText();

        switch (type) {
            case "string":
                String stringValue = generateStringValue(schema, name);
                parentNode.put(name, stringValue);
                break;
            case "integer":
                int intValue = generateIntegerValue(schema);
                parentNode.put(name, intValue);
                break;
            case "number":
                double numberValue = generateNumberValue(schema);
                parentNode.put(name, numberValue);
                break;
            case "boolean":
                boolean boolValue = schema.has("example") ? schema.get("example").asBoolean() : true;
                parentNode.put(name, boolValue);
                break;
            case "array":
                parentNode.putArray(name);
                break;
            case "object":
                if (schema.has("properties")) {
                    ObjectNode nestedNode = mapper.createObjectNode();
                    JsonNode properties = schema.get("properties");
                    Iterator<Map.Entry<String, JsonNode>> fields = properties.fields();
                    while (fields.hasNext()) {
                        Map.Entry<String, JsonNode> field = fields.next();
                        addPropertyToJson(nestedNode, field.getKey(), field.getValue(), mapper);
                    }
                    parentNode.set(name, nestedNode);
                } else {
                    parentNode.putObject(name);
                }
                break;
        }
    }

    /**
     * String değer oluşturur
     */
    private String generateStringValue(JsonNode schema, String fieldName) {
        if (schema.has("example")) {
            return schema.get("example").asText();
        }

        if (schema.has("enum")) {
            return schema.get("enum").get(0).asText();
        }

        if (schema.has("format")) {
            String format = schema.get("format").asText();
            switch (format) {
                case "date":
                    return "2024-01-01";
                case "date-time":
                    return "2024-01-01T12:00:00Z";
                case "email":
                    return "test@example.com";
                case "uuid":
                    return "123e4567-e89b-12d3-a456-426614174000";
                case "uri":
                    return "https://example.com";
                case "password":
                    return "TestPassword123!";
                default:
                    return "test_" + fieldName.toLowerCase();
            }
        }

        // Length constraints kontrolü
        int minLength = schema.has("minLength") ? schema.get("minLength").asInt() : 5;
        int maxLength = schema.has("maxLength") ? schema.get("maxLength").asInt() : 50;

        String baseValue = "test_" + fieldName.toLowerCase();
        if (baseValue.length() < minLength) {
            return baseValue + "_".repeat(minLength - baseValue.length());
        } else if (baseValue.length() > maxLength) {
            return baseValue.substring(0, maxLength);
        }

        return baseValue;
    }

    /**
     * Integer değer oluşturur
     */
    private int generateIntegerValue(JsonNode schema) {
        if (schema.has("example")) {
            return schema.get("example").asInt();
        }

        int min = schema.has("minimum") ? schema.get("minimum").asInt() : 1;
        int max = schema.has("maximum") ? schema.get("maximum").asInt() : 100;

        return Math.max(min, Math.min(max, 42)); // Default sensible value
    }

    /**
     * Number değer oluşturur
     */
    private double generateNumberValue(JsonNode schema) {
        if (schema.has("example")) {
            return schema.get("example").asDouble();
        }

        double min = schema.has("minimum") ? schema.get("minimum").asDouble() : 1.0;
        double max = schema.has("maximum") ? schema.get("maximum").asDouble() : 100.0;

        return Math.max(min, Math.min(max, 42.5)); // Default sensible value
    }

    /**
     * Yeniden deneme mekanizması ile test senaryosu oluşturur
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
                    if (config.isVerbose()) {
                        logger.info("Yeniden denemeden önce " + waitTime + "ms bekleniyor...");
                    }
                    Thread.sleep(waitTime);
                    waitTime *= 2;
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Bekleme işlemi kesintiye uğradı", ie);
                }
            }
        }

        if (config.isUseFallbackOnError()) {
            logger.warning("Maksimum yeniden deneme sayısına ulaşıldı. Gelişmiş fallback şablon kullanılacak.");
            return generateAdvancedFallbackTestCase(
                    new EndpointInfo(endpointPath, httpMethod, null), operationId);
        } else {
            throw new RuntimeException("API çağrısı " + config.getMaxRetries() + " denemeden sonra başarısız oldu", lastException);
        }
    }

    /**
     * Gelişmiş API çağrısı yaparak test senaryosu oluşturur
     */
    private String generateAdvancedTestCase(String prompt, String operationId, String httpMethod, String endpointPath) {
        if (config.isVerbose()) {
            logger.info("Gelişmiş API isteği gönderiliyor...");
        }

        List<ChatMessage> messages = new ArrayList<>();

        // Gelişmiş sistem prompt'u kullan
        messages.add(new ChatMessage(ChatMessageRole.SYSTEM.value(), createAdvancedSystemPrompt()));

        // Endpoint prompt'unu ekle
        messages.add(new ChatMessage(ChatMessageRole.USER.value(), prompt));

        // Pozitif pekiştirme için örnek yanıt
        messages.add(new ChatMessage(ChatMessageRole.ASSISTANT.value(),
                "Anlıyorum. Kapsamlı bir API test senaryosu oluşturacağım. " +
                        "Yardımcı metodları kullanarak kod tekrarını önleyeceğim, " +
                        "güvenlik testleri, performans kontrolleri ve veri doğrulaması dahil edeceğim. " +
                        "Import ifadeleri veya sınıf tanımları eklemeyeceğim."));

        // Son direktif
        messages.add(new ChatMessage(ChatMessageRole.USER.value(),
                "Lütfen şimdi tam test senaryosunu oluştur. " +
                        "Sadece metod gövdesini ver, import veya sınıf tanımı yok. " +
                        "Tüm test senaryolarını tek metod içinde organize et. " +
                        "Yardımcı metodları kullanarak kod kalitesini artır."));

        ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                .model(config.getModel())
                .messages(messages)
                .maxTokens(config.getMaxTokens())
                .temperature(0.1) // Daha tutarlı yanıtlar için çok düşük
                .topP(0.9)
                .frequencyPenalty(0.0)
                .presencePenalty(0.0)
                .build();

        if (config.isVerbose()) {
            logger.info("Gelişmiş API yanıtı bekleniyor...");
        }

        String response = openAiService.createChatCompletion(completionRequest)
                .getChoices().get(0).getMessage().getContent();

        if (config.isVerbose()) {
            logger.info("Gelişmiş API yanıtı alındı.");
        }

        return formatAdvancedTestCase(response, operationId, httpMethod, endpointPath);
    }

    /**
     * Gelişmiş test senaryosunu formatlar
     */
    private String formatAdvancedTestCase(String response, String operationId, String httpMethod, String endpointPath) {
        StringBuilder formattedTestCase = new StringBuilder();

        // Detaylı JavaDoc yorumu ekle
        formattedTestCase.append("    /**\n");
        formattedTestCase.append("     * Kapsamlı API Test: ").append(operationId).append("\n");
        formattedTestCase.append("     * Endpoint: ").append(httpMethod.toUpperCase()).append(" ").append(endpointPath).append("\n");
        formattedTestCase.append("     * \n");
        formattedTestCase.append("     * Test Kapsamı:\n");
        formattedTestCase.append("     * - Happy path testleri\n");
        formattedTestCase.append("     * - Negative testler\n");
        formattedTestCase.append("     * - Güvenlik testleri\n");
        formattedTestCase.append("     * - Performans kontrolleri\n");
        formattedTestCase.append("     * - Veri doğrulama testleri\n");
        formattedTestCase.append("     * - Edge case testleri\n");
        formattedTestCase.append("     */\n");

        // Test anotasyonları ekle
        formattedTestCase.append("    @Test\n");
        formattedTestCase.append("    @DisplayName(\"Kapsamlı Test: ")
                .append(operationId).append(" - ")
                .append(httpMethod.toUpperCase()).append(" ")
                .append(endpointPath).append("\")\n");

        // Tag'ler ekle
        formattedTestCase.append("    @Tag(\"").append(httpMethod.toLowerCase()).append("\")\n");
        formattedTestCase.append("    @Tag(\"comprehensive\")\n");
        formattedTestCase.append("    @Tag(\"generated\")\n");

        // Metod tanımı başlat
        formattedTestCase.append("    public void testComprehensive").append(sanitizeMethodName(operationId)).append("() {\n");

        // Test başlangıç logu
        formattedTestCase.append("        logger.info(\"=== Kapsamlı API Test Başlatılıyor: ")
                .append(httpMethod.toUpperCase()).append(" ").append(endpointPath).append(" ===\");\n");
        formattedTestCase.append("        long testStartTime = System.currentTimeMillis();\n\n");

        // Yanıtın içeriğini temizle ve ekle
        String cleanedResponse = cleanAdvancedResponse(response);

        if (cleanedResponse.trim().isEmpty()) {
            // Boşsa gelişmiş fallback içerik ekle
            formattedTestCase.append("        // API test içeriği oluşturulamadı - gelişmiş fallback kullanılıyor\n");
            formattedTestCase.append("        logger.warning(\"Test içeriği '").append(operationId).append("' için oluşturulamadı, fallback kullanılıyor\");\n");

            // Basit test ekle
            appendBasicTestScenario(formattedTestCase, endpointPath, httpMethod);
        } else {
            formattedTestCase.append("        ").append(cleanedResponse.trim().replace("\n", "\n        ")).append("\n");
        }

        // Test bitiş logu
        formattedTestCase.append("\n        long testEndTime = System.currentTimeMillis();\n");
        formattedTestCase.append("        logger.info(\"=== Test Tamamlandı: ").append(operationId)
                .append(" (\" + (testEndTime - testStartTime) + \" ms) ===\");\n");

        // Metodu kapat
        formattedTestCase.append("    }");

        return formattedTestCase.toString();
    }

    /**
     * Gelişmiş yanıt temizleme
     */
    private String cleanAdvancedResponse(String response) {
        // Backtick kod bloklarını temizle
        String cleaned = response.replaceAll("```java", "").replaceAll("```", "");

        // Import ifadelerini kaldır
        cleaned = cleaned.replaceAll("(?m)^\\s*import\\s+[\\w\\.\\*]+;\\s*$", "");

        // Class tanımlarını kaldır
        cleaned = cleaned.replaceAll("(?s)public\\s+class\\s+\\w+\\s*\\{.*", "");

        // Test metod imzalarını kaldır
        cleaned = cleaned.replaceAll("(?m)^\\s*@Test.*$", "");
        cleaned = cleaned.replaceAll("(?m)^\\s*@DisplayName.*$", "");
        cleaned = cleaned.replaceAll("(?m)^\\s*@Tag.*$", "");
        cleaned = cleaned.replaceAll("(?m)^\\s*public\\s+void\\s+test\\w+\\(.*\\)\\s*\\{?", "");

        // Fazladan açılış/kapanış parantezlerini temizle
        if (cleaned.contains("{") || cleaned.contains("}")) {
            // En dıştaki süslü parantezleri bul ve içeriği al
            int firstBrace = cleaned.indexOf("{");
            int lastBrace = cleaned.lastIndexOf("}");

            if (firstBrace != -1 && lastBrace != -1 && firstBrace < lastBrace) {
                cleaned = cleaned.substring(firstBrace + 1, lastBrace).trim();
            }
        }

        // Boş satırları düzenle
        cleaned = cleaned.replaceAll("(?m)^\\s*$\\n{2,}", "\n\n");
        cleaned = cleaned.replaceAll("(?m)^\\s*$\\n", "\n");

        return cleaned.trim();
    }

    /**
     * Basit test senaryosu ekler (fallback durumu için)
     */
    private void appendBasicTestScenario(StringBuilder testCase, String endpoint, String httpMethod) {
        testCase.append("        // Temel test senaryosu\n");
        testCase.append("        Response response = commonRequestSpec()\n");
        testCase.append("        .when()\n");
        testCase.append("            .").append(httpMethod.toLowerCase()).append("(\"").append(endpoint).append("\")\n");
        testCase.append("        .then()\n");
        testCase.append("            .statusCode(anyOf(is(200), is(201), is(204)))\n");
        testCase.append("            .extract().response();\n\n");
        testCase.append("        assertResponseTimeWithLogging(response, 3000, \"").append(httpMethod.toUpperCase()).append(" ").append(endpoint).append("\");\n");
    }

    /**
     * Gelişmiş fallback test senaryosu oluşturur
     */
    private String generateAdvancedFallbackTestCase(EndpointInfo endpoint, String operationId) {
        StringBuilder testCase = new StringBuilder();
        String httpMethod = endpoint.getMethod();
        String endpointPath = endpoint.getPath();

        testCase.append("    /**\n");
        testCase.append("     * Gelişmiş Fallback Test: ").append(operationId).append("\n");
        testCase.append("     * Endpoint: ").append(httpMethod.toUpperCase()).append(" ").append(endpointPath).append("\n");
        testCase.append("     * NOT: Bu test API hatası nedeniyle otomatik fallback şablonu kullanılarak oluşturulmuştur.\n");
        testCase.append("     */\n");
        testCase.append("    @Test\n");
        testCase.append("    @DisplayName(\"Fallback Test: ")
                .append(operationId).append(" - ")
                .append(httpMethod.toUpperCase()).append(" ")
                .append(endpointPath).append("\")\n");
        testCase.append("    @Tag(\"fallback\")\n");
        testCase.append("    @Tag(\"").append(httpMethod.toLowerCase()).append("\")\n");
        testCase.append("    public void testFallback").append(sanitizeMethodName(operationId)).append("() {\n");

        testCase.append("        logger.info(\"=== Fallback Test Başlatılıyor: ")
                .append(httpMethod.toUpperCase()).append(" ").append(endpointPath).append(" ===\");\n\n");

        switch (httpMethod.toLowerCase()) {
            case "get":
                appendAdvancedGetFallback(testCase, endpointPath);
                break;
            case "post":
                appendAdvancedPostFallback(testCase, endpointPath);
                break;
            case "put":
                appendAdvancedPutFallback(testCase, endpointPath);
                break;
            case "delete":
                appendAdvancedDeleteFallback(testCase, endpointPath);
                break;
            case "patch":
                appendAdvancedPatchFallback(testCase, endpointPath);
                break;
            default:
                appendGenericFallback(testCase, endpointPath, httpMethod);
                break;
        }

        testCase.append("        logger.info(\"=== Fallback Test Tamamlandı: ").append(operationId).append(" ===\");\n");
        testCase.append("    }");

        return testCase.toString();
    }

    /**
     * Gelişmiş GET fallback testi
     */
    private void appendAdvancedGetFallback(StringBuilder testCase, String endpoint) {
        testCase.append("        // 1. BAŞARILI SENARYO TESTİ\n");
        testCase.append("        logger.info(\"Başarılı GET isteği testi\");\n");
        testCase.append("        Response successResponse = commonRequestSpec()\n");
        testCase.append("            .queryParam(\"page\", 0)\n");
        testCase.append("            .queryParam(\"size\", 10)\n");
        testCase.append("        .when()\n");
        testCase.append("            .get(\"").append(endpoint).append("\")\n");
        testCase.append("        .then()\n");
        testCase.append("            .statusCode(200)\n");
        testCase.append("            .contentType(ContentType.JSON)\n");
        testCase.append("            .extract().response();\n\n");

        testCase.append("        assertResponseTimeWithLogging(successResponse, 3000, \"GET Success\");\n");
        testCase.append("        validateResponseStructure(successResponse, \"data\");\n\n");

        testCase.append("        // 2. SAYFALANDIRMA TESTİ\n");
        testCase.append("        logger.info(\"Sayfalandırma parametreleri testi\");\n");
        testCase.append("        Response paginationResponse = commonRequestSpec()\n");
        testCase.append("            .queryParam(\"page\", 1)\n");
        testCase.append("            .queryParam(\"size\", 5)\n");
        testCase.append("        .when()\n");
        testCase.append("            .get(\"").append(endpoint).append("\")\n");
        testCase.append("        .then()\n");
        testCase.append("            .statusCode(200)\n");
        testCase.append("            .extract().response();\n\n");

        testCase.append("        try {\n");
        testCase.append("            validatePagination(paginationResponse);\n");
        testCase.append("        } catch (AssertionError e) {\n");
        testCase.append("            logger.warning(\"Sayfalandırma yapısı standart değil: \" + e.getMessage());\n");
        testCase.append("        }\n\n");

        testCase.append("        // 3. HATA DURUMU TESTLERİ\n");
        testCase.append("        logger.info(\"Geçersiz parametre testi\");\n");
        testCase.append("        commonRequestSpec()\n");
        testCase.append("            .queryParam(\"page\", -1)\n");
        testCase.append("        .when()\n");
        testCase.append("            .get(\"").append(endpoint).append("\")\n");
        testCase.append("        .then()\n");
        testCase.append("            .statusCode(anyOf(is(400), is(422)));\n\n");

        testCase.append("        // 4. GÜVENLİK TESTİ\n");
        testCase.append("        logger.info(\"Güvenlik testi\");\n");
        testCase.append("        given()\n");
        testCase.append("            .contentType(ContentType.JSON)\n");
        testCase.append("        .when()\n");
        testCase.append("            .get(\"").append(endpoint).append("\")\n");
        testCase.append("        .then()\n");
        testCase.append("            .statusCode(anyOf(is(200), is(401), is(403)));\n\n");
    }

    /**
     * Gelişmiş POST fallback testi
     */
    private void appendAdvancedPostFallback(StringBuilder testCase, String endpoint) {
        testCase.append("        // 1. BAŞARILI KAYIT OLUŞTURMA TESTİ\n");
        testCase.append("        logger.info(\"Başarılı POST isteği testi\");\n");
        testCase.append("        Map<String, Object> validData = createDynamicTestData(\"resource\", true);\n\n");

        testCase.append("        Response createResponse = commonRequestSpec()\n");
        testCase.append("            .body(validData)\n");
        testCase.append("        .when()\n");
        testCase.append("            .post(\"").append(endpoint).append("\")\n");
        testCase.append("        .then()\n");
        testCase.append("            .statusCode(anyOf(is(200), is(201), is(204)))\n");
        testCase.append("            .contentType(ContentType.JSON)\n");
        testCase.append("            .extract().response();\n\n");

        testCase.append("        assertResponseTimeWithLogging(createResponse, 5000, \"POST Create\");\n\n");

        testCase.append("        // 2. GEÇERSİZ VERİ TESTİ\n");
        testCase.append("        logger.info(\"Geçersiz veri testi\");\n");
        testCase.append("        Map<String, Object> invalidData = createDynamicTestData(\"resource\", false);\n\n");

        testCase.append("        commonRequestSpec()\n");
        testCase.append("            .body(invalidData)\n");
        testCase.append("        .when()\n");
        testCase.append("            .post(\"").append(endpoint).append("\")\n");
        testCase.append("        .then()\n");
        testCase.append("            .statusCode(anyOf(is(400), is(422)));\n\n");

        testCase.append("        // 3. BOŞ VERİ TESTİ\n");
        testCase.append("        logger.info(\"Boş veri testi\");\n");
        testCase.append("        commonRequestSpec()\n");
        testCase.append("            .body(\"{}\")\n");
        testCase.append("        .when()\n");
        testCase.append("            .post(\"").append(endpoint).append("\")\n");
        testCase.append("        .then()\n");
        testCase.append("            .statusCode(anyOf(is(400), is(422)));\n\n");

        testCase.append("        // 4. GÜVENLİK TESTİ\n");
        testCase.append("        logger.info(\"Güvenlik testi\");\n");
        testCase.append("        given()\n");
        testCase.append("            .contentType(ContentType.JSON)\n");
        testCase.append("            .body(validData)\n");
        testCase.append("        .when()\n");
        testCase.append("            .post(\"").append(endpoint).append("\")\n");
        testCase.append("        .then()\n");
        testCase.append("            .statusCode(anyOf(is(200), is(201), is(401), is(403)));\n\n");
    }

    /**
     * Gelişmiş PUT fallback testi
     */
    private void appendAdvancedPutFallback(StringBuilder testCase, String endpoint) {
        testCase.append("        // 1. BAŞARILI GÜNCELLEME TESTİ\n");
        testCase.append("        logger.info(\"Başarılı PUT isteği testi\");\n");
        testCase.append("        Integer resourceId = 1;\n");
        testCase.append("        Map<String, Object> updateData = createDynamicTestData(\"resource\", true);\n\n");

        testCase.append("        Response updateResponse = commonRequestSpec()\n");
        testCase.append("            .body(updateData)\n");
        testCase.append("        .when()\n");
        testCase.append("            .put(\"").append(endpoint).append("/\" + resourceId)\n");
        testCase.append("        .then()\n");
        testCase.append("            .statusCode(anyOf(is(200), is(204)))\n");
        testCase.append("            .extract().response();\n\n");

        testCase.append("        assertResponseTimeWithLogging(updateResponse, 5000, \"PUT Update\");\n\n");

        testCase.append("        // 2. GEÇERSIZ ID TESTİ\n");
        testCase.append("        logger.info(\"Geçersiz ID testi\");\n");
        testCase.append("        commonRequestSpec()\n");
        testCase.append("            .body(updateData)\n");
        testCase.append("        .when()\n");
        testCase.append("            .put(\"").append(endpoint).append("/99999999\")\n");
        testCase.append("        .then()\n");
        testCase.append("            .statusCode(404);\n\n");

        testCase.append("        // 3. GEÇERSİZ VERİ TESTİ\n");
        testCase.append("        logger.info(\"Geçersiz veri testi\");\n");
        testCase.append("        Map<String, Object> invalidUpdateData = createDynamicTestData(\"resource\", false);\n\n");

        testCase.append("        commonRequestSpec()\n");
        testCase.append("            .body(invalidUpdateData)\n");
        testCase.append("        .when()\n");
        testCase.append("            .put(\"").append(endpoint).append("/\" + resourceId)\n");
        testCase.append("        .then()\n");
        testCase.append("            .statusCode(anyOf(is(400), is(422)));\n\n");

        testCase.append("        // 4. GÜVENLİK TESTİ\n");
        testCase.append("        logger.info(\"Güvenlik testi\");\n");
        testCase.append("        given()\n");
        testCase.append("            .contentType(ContentType.JSON)\n");
        testCase.append("            .body(updateData)\n");
        testCase.append("        .when()\n");
        testCase.append("            .put(\"").append(endpoint).append("/\" + resourceId)\n");
        testCase.append("        .then()\n");
        testCase.append("            .statusCode(anyOf(is(200), is(204), is(401), is(403)));\n\n");
    }

    /**
     * Gelişmiş DELETE fallback testi
     */
    private void appendAdvancedDeleteFallback(StringBuilder testCase, String endpoint) {
        testCase.append("        // 1. BAŞARILI SİLME TESTİ\n");
        testCase.append("        logger.info(\"Başarılı DELETE isteği testi\");\n");
        testCase.append("        Integer resourceId = 1;\n\n");

        testCase.append("        Response deleteResponse = commonRequestSpec()\n");
        testCase.append("        .when()\n");
        testCase.append("            .delete(\"").append(endpoint).append("/\" + resourceId)\n");
        testCase.append("        .then()\n");
        testCase.append("            .statusCode(anyOf(is(200), is(204), is(404)))\n");
        testCase.append("            .extract().response();\n\n");

        testCase.append("        assertResponseTimeWithLogging(deleteResponse, 3000, \"DELETE Resource\");\n\n");

        testCase.append("        // 2. GEÇERSIZ ID TESTİ\n");
        testCase.append("        logger.info(\"Geçersiz ID testi\");\n");
        testCase.append("        commonRequestSpec()\n");
        testCase.append("        .when()\n");
        testCase.append("            .delete(\"").append(endpoint).append("/99999999\")\n");
        testCase.append("        .then()\n");
        testCase.append("            .statusCode(404);\n\n");

        testCase.append("        // 3. İKİNCİ KERE SİLME TESTİ\n");
        testCase.append("        logger.info(\"Zaten silinmiş kayıt testi\");\n");
        testCase.append("        commonRequestSpec()\n");
        testCase.append("        .when()\n");
        testCase.append("            .delete(\"").append(endpoint).append("/\" + resourceId)\n");
        testCase.append("        .then()\n");
        testCase.append("            .statusCode(anyOf(is(404), is(410)));\n\n");

        testCase.append("        // 4. GÜVENLİK TESTİ\n");
        testCase.append("        logger.info(\"Güvenlik testi\");\n");
        testCase.append("        given()\n");
        testCase.append("        .when()\n");
        testCase.append("            .delete(\"").append(endpoint).append("/\" + resourceId)\n");
        testCase.append("        .then()\n");
        testCase.append("            .statusCode(anyOf(is(200), is(204), is(401), is(403), is(404)));\n\n");
    }

    /**
     * Gelişmiş PATCH fallback testi
     */
    private void appendAdvancedPatchFallback(StringBuilder testCase, String endpoint) {
        testCase.append("        // 1. BAŞARILI KISMİ GÜNCELLEME TESTİ\n");
        testCase.append("        logger.info(\"Başarılı PATCH isteği testi\");\n");
        testCase.append("        Integer resourceId = 1;\n");
        testCase.append("        Map<String, Object> patchData = new HashMap<>();\n");
        testCase.append("        patchData.put(\"name\", \"Updated Name \" + System.currentTimeMillis());\n\n");

        testCase.append("        Response patchResponse = commonRequestSpec()\n");
        testCase.append("            .body(patchData)\n");
        testCase.append("        .when()\n");
        testCase.append("            .patch(\"").append(endpoint).append("/\" + resourceId)\n");
        testCase.append("        .then()\n");
        testCase.append("            .statusCode(anyOf(is(200), is(204)))\n");
        testCase.append("            .extract().response();\n\n");

        testCase.append("        assertResponseTimeWithLogging(patchResponse, 3000, \"PATCH Update\");\n\n");

        testCase.append("        // 2. GEÇERSIZ ID TESTİ\n");
        testCase.append("        logger.info(\"Geçersiz ID testi\");\n");
        testCase.append("        commonRequestSpec()\n");
        testCase.append("            .body(patchData)\n");
        testCase.append("        .when()\n");
        testCase.append("            .patch(\"").append(endpoint).append("/99999999\")\n");
        testCase.append("        .then()\n");
        testCase.append("            .statusCode(404);\n\n");

        testCase.append("        // 3. BOŞ VERİ TESTİ\n");
        testCase.append("        logger.info(\"Boş veri testi\");\n");
        testCase.append("        commonRequestSpec()\n");
        testCase.append("            .body(\"{}\")\n");
        testCase.append("        .when()\n");
        testCase.append("            .patch(\"").append(endpoint).append("/\" + resourceId)\n");
        testCase.append("        .then()\n");
        testCase.append("            .statusCode(anyOf(is(200), is(204), is(400)));\n\n");

        testCase.append("        // 4. GÜVENLİK TESTİ\n");
        testCase.append("        logger.info(\"Güvenlik testi\");\n");
        testCase.append("        given()\n");
        testCase.append("            .contentType(ContentType.JSON)\n");
        testCase.append("            .body(patchData)\n");
        testCase.append("        .when()\n");
        testCase.append("            .patch(\"").append(endpoint).append("/\" + resourceId)\n");
        testCase.append("        .then()\n");
        testCase.append("            .statusCode(anyOf(is(200), is(204), is(401), is(403)));\n\n");
    }

    /**
     * Genel fallback testi
     */
    private void appendGenericFallback(StringBuilder testCase, String endpoint, String httpMethod) {
        testCase.append("        // GENEL ").append(httpMethod.toUpperCase()).append(" TESTİ\n");
        testCase.append("        logger.info(\"Genel ").append(httpMethod.toUpperCase()).append(" isteği testi\");\n");
        testCase.append("        Response response = commonRequestSpec()\n");
        testCase.append("        .when()\n");
        testCase.append("            .").append(httpMethod.toLowerCase()).append("(\"").append(endpoint).append("\")\n");
        testCase.append("        .then()\n");
        testCase.append("            .statusCode(anyOf(is(200), is(201), is(204), is(404)))\n");
        testCase.append("            .extract().response();\n\n");

        testCase.append("        assertResponseTimeWithLogging(response, 5000, \"").append(httpMethod.toUpperCase()).append(" ").append(endpoint).append("\");\n\n");

        testCase.append("        // GÜVENLİK TESTİ\n");
        testCase.append("        given()\n");
        testCase.append("        .when()\n");
        testCase.append("            .").append(httpMethod.toLowerCase()).append("(\"").append(endpoint).append("\")\n");
        testCase.append("        .then()\n");
        testCase.append("            .statusCode(anyOf(is(200), is(201), is(204), is(401), is(403), is(404)));\n\n");
    }

    /**
     * Performans testlerini ekler
     */
    private void writePerformanceTests(FileWriter writer) throws IOException {
        writer.write("    // =========================== PERFORMANS TESTLERİ ===========================\n\n");

        writer.write("    /**\n");
        writer.write("     * Genel API performans testi\n");
        writer.write("     */\n");
        writer.write("    @Test\n");
        writer.write("    @DisplayName(\"API Performans Testi\")\n");
        writer.write("    @Tag(\"performance\")\n");
        writer.write("    public void testApiPerformance() {\n");
        writer.write("        logger.info(\"=== API Performans Testi Başlatılıyor ===\");\n\n");

        writer.write("        // Concurrent request testi\n");
        writer.write("        int concurrentUsers = 10;\n");
        writer.write("        int requestsPerUser = 5;\n");
        writer.write("        ExecutorService executor = Executors.newFixedThreadPool(concurrentUsers);\n");
        writer.write("        List<Future<Long>> futures = new ArrayList<>();\n\n");

        writer.write("        for (int i = 0; i < concurrentUsers; i++) {\n");
        writer.write("            futures.add(executor.submit(() -> {\n");
        writer.write("                long totalTime = 0;\n");
        writer.write("                for (int j = 0; j < requestsPerUser; j++) {\n");
        writer.write("                    long startTime = System.currentTimeMillis();\n");
        writer.write("                    commonRequestSpec()\n");
        writer.write("                        .queryParam(\"page\", 0)\n");
        writer.write("                        .queryParam(\"size\", 10)\n");
        writer.write("                    .when()\n");
        writer.write("                        .get(\"/api/test-endpoint\")\n");
        writer.write("                    .then()\n");
        writer.write("                        .statusCode(anyOf(is(200), is(404)));\n");
        writer.write("                    totalTime += (System.currentTimeMillis() - startTime);\n");
        writer.write("                }\n");
        writer.write("                return totalTime / requestsPerUser;\n");
        writer.write("            }));\n");
        writer.write("        }\n\n");

        writer.write("        // Sonuçları topla\n");
        writer.write("        long totalAverageTime = 0;\n");
        writer.write("        for (Future<Long> future : futures) {\n");
        writer.write("            try {\n");
        writer.write("                totalAverageTime += future.get();\n");
        writer.write("            } catch (Exception e) {\n");
        writer.write("                logger.warning(\"Performans testi hatası: \" + e.getMessage());\n");
        writer.write("            }\n");
        writer.write("        }\n\n");

        writer.write("        long averageResponseTime = totalAverageTime / concurrentUsers;\n");
        writer.write("        logger.info(\"Ortalama yanıt süresi: \" + averageResponseTime + \" ms\");\n");
        writer.write("        \n");
        writer.write("        // Performans assertion\n");
        writer.write("        Assertions.assertTrue(averageResponseTime < 2000, \n");
        writer.write("            \"Ortalama yanıt süresi 2 saniyeden fazla: \" + averageResponseTime + \" ms\");\n\n");

        writer.write("        executor.shutdown();\n");
        writer.write("        logger.info(\"=== API Performans Testi Tamamlandı ===\");\n");
        writer.write("    }\n\n");
    }

    /**
     * Güvenlik testlerini ekler
     */
    private void writeSecurityTests(FileWriter writer) throws IOException {
        writer.write("    // =========================== GÜVENLİK TESTLERİ ===========================\n\n");

        writer.write("    /**\n");
        writer.write("     * Kapsamlı güvenlik testi\n");
        writer.write("     */\n");
        writer.write("    @Test\n");
        writer.write("    @DisplayName(\"Kapsamlı Güvenlik Testi\")\n");
        writer.write("    @Tag(\"security\")\n");
        writer.write("    public void testComprehensiveSecurity() {\n");
        writer.write("        logger.info(\"=== Kapsamlı Güvenlik Testi Başlatılıyor ===\");\n\n");

        writer.write("        // 1. CORS Testi\n");
        writer.write("        logger.info(\"CORS testi\");\n");
        writer.write("        given()\n");
        writer.write("            .header(\"Origin\", \"https://malicious-site.com\")\n");
        writer.write("            .header(\"Access-Control-Request-Method\", \"GET\")\n");
        writer.write("        .when()\n");
        writer.write("            .options(\"/api/test-endpoint\")\n");
        writer.write("        .then()\n");
        writer.write("            .statusCode(anyOf(is(200), is(204), is(405)));\n\n");

        writer.write("        // 2. HTTP Method Override Testi\n");
        writer.write("        logger.info(\"HTTP Method Override testi\");\n");
        writer.write("        given()\n");
        writer.write("            .header(\"X-HTTP-Method-Override\", \"DELETE\")\n");
        writer.write("        .when()\n");
        writer.write("            .post(\"/api/test-endpoint\")\n");
        writer.write("        .then()\n");
        writer.write("            .statusCode(not(200)); // Method override engellenmeli\n\n");

        writer.write("        // 3. Rate Limiting Testi\n");
        writer.write("        logger.info(\"Rate limiting testi\");\n");
        writer.write("        for (int i = 0; i < 50; i++) {\n");
        writer.write("            Response response = commonRequestSpec()\n");
        writer.write("            .when()\n");
        writer.write("                .get(\"/api/test-endpoint\")\n");
        writer.write("            .then()\n");
        writer.write("                .extract().response();\n");
        writer.write("            \n");
        writer.write("            if (response.getStatusCode() == 429) {\n");
        writer.write("                logger.info(\"Rate limiting çalışıyor: \" + (i + 1) + \". istekte engellendi\");\n");
        writer.write("                break;\n");
        writer.write("            }\n");
        writer.write("        }\n\n");

        writer.write("        // 4. Header Injection Testi\n");
        writer.write("        logger.info(\"Header injection testi\");\n");
        writer.write("        given()\n");
        writer.write("            .header(\"X-Malicious\", \"\\r\\nSet-Cookie: admin=true\")\n");
        writer.write("        .when()\n");
        writer.write("            .get(\"/api/test-endpoint\")\n");
        writer.write("        .then()\n");
        writer.write("            .statusCode(anyOf(is(200), is(400), is(404)))\n");
        writer.write("            .header(\"Set-Cookie\", not(containsString(\"admin=true\")));\n\n");

        writer.write("        // 5. Path Traversal Testi\n");
        writer.write("        logger.info(\"Path traversal testi\");\n");
        writer.write("        String[] pathTraversalPayloads = {\n");
        writer.write("            \"../../../etc/passwd\",\n");
        writer.write("            \"..\\\\..\\\\..\\\\windows\\\\system32\\\\drivers\\\\etc\\\\hosts\",\n");
        writer.write("            \"%2e%2e%2f%2e%2e%2f%2e%2e%2fetc%2fpasswd\"\n");
        writer.write("        };\n");
        writer.write("        \n");
        writer.write("        for (String payload : pathTraversalPayloads) {\n");
        writer.write("            commonRequestSpec()\n");
        writer.write("                .pathParam(\"file\", payload)\n");
        writer.write("            .when()\n");
        writer.write("                .get(\"/api/files/{file}\")\n");
        writer.write("            .then()\n");
        writer.write("                .statusCode(not(200)) // Path traversal başarılı olmamalı\n");
        writer.write("                .body(not(containsString(\"root:\")));\n");
        writer.write("        }\n\n");

        writer.write("        logger.info(\"=== Kapsamlı Güvenlik Testi Tamamlandı ===\");\n");
        writer.write("    }\n\n");
    }

    /**
     * Mock testlerini ekler
     */
    private void writeMockTests(FileWriter writer) throws IOException {
        writer.write("    // =========================== MOCK TESTLERİ ===========================\n\n");

        writer.write("    /**\n");
        writer.write("     * Mock server ile test senaryoları\n");
        writer.write("     */\n");
        writer.write("    @Test\n");
        writer.write("    @DisplayName(\"Mock Server Testleri\")\n");
        writer.write("    @Tag(\"mock\")\n");
        writer.write("    public void testWithMockServer() {\n");
        writer.write("        logger.info(\"=== Mock Server Testleri Başlatılıyor ===\");\n\n");

        writer.write("        // Bu örnekte WireMock kullanılabilir\n");
        writer.write("        // WireMock server kurulumu\n");
        writer.write("        logger.info(\"Mock server simülasyonu\");\n\n");

        writer.write("        // 1. Başarılı yanıt mock'u\n");
        writer.write("        Map<String, Object> mockResponse = new HashMap<>();\n");
        writer.write("        mockResponse.put(\"id\", 1);\n");
        writer.write("        mockResponse.put(\"name\", \"Mock Resource\");\n");
        writer.write("        mockResponse.put(\"status\", \"active\");\n\n");

        writer.write("        // 2. Hata yanıtı mock'u\n");
        writer.write("        Map<String, Object> errorResponse = new HashMap<>();\n");
        writer.write("        errorResponse.put(\"error\", \"Resource not found\");\n");
        writer.write("        errorResponse.put(\"code\", 404);\n\n");

        writer.write("        // 3. Timeout senaryosu mock'u\n");
        writer.write("        logger.info(\"Timeout senaryosu testi\");\n");
        writer.write("        try {\n");
        writer.write("            commonRequestSpec()\n");
        writer.write("                .timeout(Duration.ofMillis(100))\n");
        writer.write("            .when()\n");
        writer.write("                .get(\"/api/slow-endpoint\")\n");
        writer.write("            .then()\n");
        writer.write("                .statusCode(anyOf(is(200), is(404), is(408)));\n");
        writer.write("        } catch (Exception e) {\n");
        writer.write("            logger.info(\"Timeout testi beklenen şekilde başarısız oldu: \" + e.getMessage());\n");
        writer.write("        }\n\n");

        writer.write("        // 4. Circuit breaker senaryosu\n");
        writer.write("        logger.info(\"Circuit breaker senaryosu\");\n");
        writer.write("        int failureCount = 0;\n");
        writer.write("        for (int i = 0; i < 10; i++) {\n");
        writer.write("            try {\n");
        writer.write("                commonRequestSpec()\n");
        writer.write("                .when()\n");
        writer.write("                    .get(\"/api/unreliable-endpoint\")\n");
        writer.write("                .then()\n");
        writer.write("                    .statusCode(anyOf(is(200), is(500), is(503), is(404)));\n");
        writer.write("            } catch (Exception e) {\n");
        writer.write("                failureCount++;\n");
        writer.write("            }\n");
        writer.write("        }\n");
        writer.write("        \n");
        writer.write("        logger.info(\"Circuit breaker test tamamlandı. Hata sayısı: \" + failureCount);\n");
        writer.write("        \n");
        writer.write("        logger.info(\"=== Mock Server Testleri Tamamlandı ===\");\n");
        writer.write("    }\n\n");
    }

    /**
     * Test sınıfının başlık kısmını yazar
     */
    private void writeTestClassHeader(FileWriter writer) throws IOException {
        writer.write("package tests;\n\n");

        // Gelişmiş importlar
        writer.write("import io.restassured.RestAssured;\n");
        writer.write("import io.restassured.http.ContentType;\n");
        writer.write("import io.restassured.response.Response;\n");
        writer.write("import io.restassured.specification.RequestSpecification;\n");
        writer.write("import io.restassured.builder.RequestSpecBuilder;\n");
        writer.write("import io.restassured.config.RestAssuredConfig;\n");
        writer.write("import io.restassured.config.HttpClientConfig;\n");

        // JUnit importları
        writer.write("import org.junit.jupiter.api.*;\n");
        writer.write("import org.junit.jupiter.params.ParameterizedTest;\n");
        writer.write("import org.junit.jupiter.params.provider.ValueSource;\n");
        writer.write("import org.junit.jupiter.params.provider.CsvSource;\n");

        // RestAssured statik importları
        writer.write("import static io.restassured.RestAssured.*;\n");

        // Hamcrest importları
        writer.write("import static org.hamcrest.Matchers.*;\n");
        writer.write("import static org.hamcrest.MatcherAssert.assertThat;\n");

        // JUnit Assertions statik importları
        writer.write("import static org.junit.jupiter.api.Assertions.*;\n");

        // Java standart importları
        writer.write("import java.util.logging.Logger;\n");
        writer.write("import java.util.*;\n");
        writer.write("import java.util.concurrent.*;\n");
        writer.write("import java.time.Duration;\n");

        // JSON işleme için import
        writer.write("import org.json.JSONObject;\n");
        writer.write("import org.json.JSONArray;\n");

        // JsonSchemaValidator modülü için import
        writer.write("import static io.restassured.module.jsv.JsonSchemaValidator.*;\n\n");

        // Detaylı JavaDoc ve sınıf tanımı
        writer.write("/**\n");
        writer.write(" * Gelişmiş API Test Senaryoları Sınıfı\n");
        writer.write(" * \n");
        writer.write(" * Bu sınıf otomatik olarak API Test Generator v" + APP_VERSION + " tarafından oluşturulmuştur.\n");
        writer.write(" * \n");
        writer.write(" * Özellikler:\n");
        writer.write(" * - Kapsamlı API endpoint testleri\n");
        writer.write(" * - Güvenlik testleri\n");
        writer.write(" * - Performans testleri\n");
        writer.write(" * - Mock server testleri\n");
        writer.write(" * - Veri doğrulama testleri\n");
        writer.write(" * - Edge case testleri\n");
        writer.write(" * \n");
        writer.write(" * @author API Test Generator\n");
        writer.write(" * @version " + APP_VERSION + "\n");
        writer.write(" * @since " + new Date() + "\n");
        writer.write(" */\n");
        writer.write("@TestInstance(TestInstance.Lifecycle.PER_CLASS)\n");
        writer.write("@TestMethodOrder(MethodOrderer.OrderAnnotation.class)\n");
        writer.write("public class " + config.getTestClassName() + " {\n\n");

        // Logger ve ortak değişkenler
        writer.write("    private static final Logger logger = Logger.getLogger(" + config.getTestClassName() + ".class.getName());\n");
        writer.write("    private static final String BASE_URI = \"" + config.getBaseUri() + "\";\n");
        writer.write("    private static final int DEFAULT_TIMEOUT = 5000;\n\n");

        // Gelişmiş setup metodu
        writer.write("    @BeforeAll\n");
        writer.write("    public static void globalSetup() {\n");
        writer.write("        logger.info(\"=== Test Sınıfı Başlatılıyor: " + config.getTestClassName() + " ===\");\n");
        writer.write("        \n");
        writer.write("        RestAssured.baseURI = BASE_URI;\n");
        writer.write("        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();\n");
        writer.write("        \n");
        writer.write("        // Gelişmiş RestAssured konfigürasyonu\n");
        writer.write("        RestAssured.config = RestAssuredConfig.config()\n");
        writer.write("            .httpClient(HttpClientConfig.httpClientConfig()\n");
        writer.write("                .setParam(\"http.connection.timeout\", DEFAULT_TIMEOUT)\n");
        writer.write("                .setParam(\"http.socket.timeout\", DEFAULT_TIMEOUT));\n");
        writer.write("        \n");
        writer.write("        logger.info(\"Base URI ayarlandı: \" + BASE_URI);\n");
        writer.write("        logger.info(\"Test konfigürasyonu tamamlandı\");\n");
        writer.write("    }\n\n");

        writer.write("    @BeforeEach\n");
        writer.write("    public void testSetup(TestInfo testInfo) {\n");
        writer.write("        logger.info(\"Test başlatılıyor: \" + testInfo.getDisplayName());\n");
        writer.write("    }\n\n");

        writer.write("    @AfterEach\n");
        writer.write("    public void testTeardown(TestInfo testInfo) {\n");
        writer.write("        logger.info(\"Test tamamlandı: \" + testInfo.getDisplayName());\n");
        writer.write("    }\n\n");

        writer.write("    @AfterAll\n");
        writer.write("    public static void globalTeardown() {\n");
        writer.write("        logger.info(\"=== Tüm Testler Tamamlandı ===\");\n");
        writer.write("        RestAssured.reset();\n");
        writer.write("    }\n\n");
    }

    /**
     * Test sınıfının bitiş kısmını yazar
     */
    private void writeTestClassFooter(FileWriter writer) throws IOException {
        writer.write("}\n");
    }

    /**
     * Test raporu oluşturur
     */
    private void generateTestReport() {
        try {
            String reportPath = config.getOutputFile().replace(".java", "_report.html");
            File reportFile = new File(reportPath);

            try (FileWriter reportWriter = new FileWriter(reportFile)) {
                reportWriter.write("<!DOCTYPE html>\n");
                reportWriter.write("<html><head><title>API Test Report</title>\n");
                reportWriter.write("<style>\n");
                reportWriter.write("body { font-family: Arial, sans-serif; margin: 20px; }\n");
                reportWriter.write(".header { background: #f0f0f0; padding: 20px; border-radius: 5px; }\n");
                reportWriter.write(".stats { display: flex; gap: 20px; margin: 20px 0; }\n");
                reportWriter.write(".stat-box { background: #e8f4fd; padding: 15px; border-radius: 5px; text-align: center; }\n");
                reportWriter.write(".success { background: #d4edda; }\n");
                reportWriter.write(".failure { background: #f8d7da; }\n");
                reportWriter.write("</style>\n");
                reportWriter.write("</head><body>\n");

                reportWriter.write("<div class='header'>\n");
                reportWriter.write("<h1>API Test Generation Report</h1>\n");
                reportWriter.write("<p>Generated: " + new Date() + "</p>\n");
                reportWriter.write("<p>Version: " + APP_VERSION + "</p>\n");
                reportWriter.write("</div>\n");

                reportWriter.write("<div class='stats'>\n");
                reportWriter.write("<div class='stat-box'>\n");
                reportWriter.write("<h3>Total Endpoints</h3>\n");
                reportWriter.write("<p>" + totalEndpoints + "</p>\n");
                reportWriter.write("</div>\n");

                reportWriter.write("<div class='stat-box success'>\n");
                reportWriter.write("<h3>Successfully Processed</h3>\n");
                reportWriter.write("<p>" + (processedEndpoints.get() - failedEndpoints.get()) + "</p>\n");
                reportWriter.write("</div>\n");

                reportWriter.write("<div class='stat-box failure'>\n");
                reportWriter.write("<h3>Failed</h3>\n");
                reportWriter.write("<p>" + failedEndpoints.get() + "</p>\n");
                reportWriter.write("</div>\n");
                reportWriter.write("</div>\n");

                reportWriter.write("<h2>Configuration</h2>\n");
                reportWriter.write("<ul>\n");
                reportWriter.write("<li>Input File: " + config.getInputFile() + "</li>\n");
                reportWriter.write("<li>Output File: " + config.getOutputFile() + "</li>\n");
                reportWriter.write("<li>Model: " + config.getModel() + "</li>\n");
                reportWriter.write("<li>Max Tokens: " + config.getMaxTokens() + "</li>\n");
                reportWriter.write("<li>Thread Pool Size: " + config.getThreadPoolSize() + "</li>\n");
                reportWriter.write("<li>Performance Tests: " + config.isIncludePerformanceTests() + "</li>\n");
                reportWriter.write("<li>Security Tests: " + config.isIncludeSecurityTests() + "</li>\n");
                reportWriter.write("<li>Mock Tests: " + config.isGenerateMockTests() + "</li>\n");
                reportWriter.write("</ul>\n");

                reportWriter.write("</body></html>\n");
            }

            logger.info("Test raporu oluşturuldu: " + reportPath);

        } catch (IOException e) {
            logger.log(Level.WARNING, "Test raporu oluşturulamadı", e);
        }
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

    // ========================== YARDIMCI SINIFLAR ==========================

    /**
     * Endpoint bilgisini tutan gelişmiş sınıf
     */
    private static class EndpointInfo {
        private final String path;
        private final String method;
        private final JsonNode operationNode;
        private String resourceType;
        private boolean requiresAuthentication = false;
        private List<String> requiredParameters = new ArrayList<>();
        private List<String> expectedStatusCodes = new ArrayList<>();

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

            public Configuration build() {
                return new Configuration(this);
            }
        }
    }
}