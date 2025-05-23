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

/**
 * OpenAPI/Swagger dosyalarından otomatik API test senaryoları oluşturan profesyonel uygulama.
 * Yapılandırılabilir seçenekler, multithreading desteği, gelişmiş hata yönetimi ve ilerleme takibi içerir.
 *
 * @author Emre Gozukizil
 * @version 2.2.0
 */
public class ClaudeSwaggerTestGenerator2 {

    private static final Logger logger = Logger.getLogger(ClaudeSwaggerTestGenerator2.class.getName());
    private static final String APP_VERSION = "2.2.0";

    // Varsayılan yapılandırma değerleri
    private static final int DEFAULT_MAX_RETRIES = 3;
    private static final int DEFAULT_TIMEOUT_SECONDS = 120;
    private static final int DEFAULT_THREAD_POOL_SIZE = 4;
    private static final String DEFAULT_OUTPUT_FILE = "api_test_cases2.java";
    private static final String DEFAULT_MODEL = "gpt-3.5-turbo";
    private static final int DEFAULT_MAX_TOKENS = 2048;

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

    /**
     * Ana metod, komut satırı argümanlarını işler ve uygulamayı başlatır.
     *
     * @param args Komut satırı argümanları
     */
    public static void main(String[] args) {
        setupLogger();

        try {
            // Komut satırı argümanlarını işle
            CommandLine cmd = parseCommandLineArguments(args);

            // Yardım bayrağını kontrol et
            if (cmd.hasOption("h")) {
                printHelp();
                return;
            }

            // Versiyon bayrağını kontrol et
            if (cmd.hasOption("v")) {
                System.out.println("API Test Generator v" + APP_VERSION);
                return;
            }

            // Çıktı dosyası için dinamik bir yol oluştur
            String outputFile = createDynamicOutputFilePath(cmd.getOptionValue("output", DEFAULT_OUTPUT_FILE));

            // Yapılandırma oluştur
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
                    .baseUri(cmd.getOptionValue("base-uri", "https://stage-job-k8s.isinolsun.com/swagger/v1/swagger.json"))
                    .useFallbackOnError(cmd.hasOption("fallback"))
                    .build();

            // Uygulamayı başlat
            ClaudeSwaggerTestGenerator2 generator = new ClaudeSwaggerTestGenerator2(config);
            generator.run();

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Uygulama çalıştırılırken hata oluştu", e);
            String errorDetail = e.getMessage();
            if (e.getCause() != null) {
                errorDetail += " Sebep: " + e.getCause().getMessage();
            }
            logger.severe("Hata detayı: " + errorDetail);
            System.exit(1);
        }
    }

    /**
     * Dosya yolundan sınıf adını çıkartır
     *
     * @param filePath Dosya yolu
     * @return Sınıf adı
     */
    private static String getClassNameFromPath(String filePath) {
        String fileName = new File(filePath).getName();
        // .java uzantısını kaldır
        if (fileName.endsWith(".java")) {
            fileName = fileName.substring(0, fileName.length() - 5);
        }
        return fileName;
    }

    /**
     * Dinamik çıktı dosyası yolu oluşturur
     * Her çalıştırmada benzersiz bir dosya adı oluşturur
     *
     * @param outputFileParam Komut satırından gelen çıktı dosyası parametresi
     * @return Oluşturulan dosya yolu
     */
    private static String createDynamicOutputFilePath(String outputFileParam) {
        // src/test/java/tests klasörü altında oluştur
        String baseDir = "src/test/java/tests";

        // Klasörün var olduğundan emin ol
        File directory = new File(baseDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Tarih ve zaman tabanlı benzersiz dosya adı oluştur
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = dateFormat.format(new Date());

        // Dosya adını belirle: TestClassName_Timestamp.java
        String className = "GeneratedApiTests";
        if (outputFileParam != null && !outputFileParam.isEmpty()) {
            // Gelen parametreden sınıf adını çıkart
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

    /**
     * Logger kurulumu
     */
    private static void setupLogger() {
        Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(Level.INFO);

        // Mevcut handlers'ları temizle
        for (java.util.logging.Handler handler : rootLogger.getHandlers()) {
            rootLogger.removeHandler(handler);
        }

        // Yeni handler ekle
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

    /**
     * Komut satırı argümanlarını işler
     *
     * @param args Komut satırı argümanları
     * @return İşlenmiş komut nesnesi
     * @throws ParseException Argüman ayrıştırma hatası
     */
    private static CommandLine parseCommandLineArguments(String[] args) throws ParseException {
        Options options = new Options();

        // Yardım ve versiyon bilgisi
        options.addOption("h", "help", false, "Yardım bilgisini göster");
        options.addOption("v", "version", false, "Uygulama versiyonunu göster");

        // Dosya seçenekleri
        options.addOption(Option.builder("i")
                .longOpt("input")
                .hasArg()
                .argName("DOSYA")
                .desc("OpenAPI/Swagger JSON dosyası [varsayılan: endpoints2.json]")
                .build());

        options.addOption(Option.builder("o")
                .longOpt("output")
                .hasArg()
                .argName("DOSYA")
                .desc("Çıktı Java dosyası [varsayılan: " + DEFAULT_OUTPUT_FILE + "]")
                .build());

        // API seçenekleri
        options.addOption(Option.builder("k")
                .longOpt("api-key-env")
                .hasArg()
                .argName("ENV_VAR")
                .desc("API anahtarını içeren çevre değişkeni [varsayılan: OPENAI_API_KEY]")
                .build());

        options.addOption(Option.builder("m")
                .longOpt("model")
                .hasArg()
                .argName("MODEL")
                .desc("Kullanılacak LLM modeli [varsayılan: " + DEFAULT_MODEL + "]")
                .build());

        options.addOption(Option.builder("t")
                .longOpt("max-tokens")
                .hasArg()
                .argName("SAYI")
                .desc("Maksimum token sayısı [varsayılan: " + DEFAULT_MAX_TOKENS + "]")
                .type(Number.class)
                .build());

        // Performans seçenekleri
        options.addOption(Option.builder("r")
                .longOpt("retries")
                .hasArg()
                .argName("SAYI")
                .desc("Maksimum yeniden deneme sayısı [varsayılan: " + DEFAULT_MAX_RETRIES + "]")
                .type(Number.class)
                .build());

        options.addOption(Option.builder()
                .longOpt("timeout")
                .hasArg()
                .argName("SANIYE")
                .desc("API isteği zaman aşımı süresi (saniye) [varsayılan: " + DEFAULT_TIMEOUT_SECONDS + "]")
                .type(Number.class)
                .build());

        options.addOption(Option.builder()
                .longOpt("threads")
                .hasArg()
                .argName("SAYI")
                .desc("Thread havuzu boyutu [varsayılan: " + DEFAULT_THREAD_POOL_SIZE + "]")
                .type(Number.class)
                .build());

        // Test oluşturma seçenekleri
        options.addOption(Option.builder("c")
                .longOpt("class-name")
                .hasArg()
                .argName("SINIF")
                .desc("Oluşturulacak test sınıfının adı [varsayılan: ApiAutomationTests]")
                .build());

        options.addOption(Option.builder("b")
                .longOpt("base-uri")
                .hasArg()
                .argName("URI")
                .desc("API baz URI'si [varsayılan: https://your-api-base-url.com]")
                .build());

        // Diğer seçenekler
        options.addOption("f", "fallback", false, "API hatalarında manuel şablonlar kullan");
        options.addOption("V", "verbose", false, "Detaylı günlük kaydı aktif");

        // Komut satırı argümanlarını işle
        CommandLineParser parser = new DefaultParser();
        return parser.parse(options, args);
    }

    /**
     * Yardım bilgisini ekrana yazdırır
     */
    private static void printHelp() {
        System.out.println("API Test Generator v" + APP_VERSION);
        System.out.println("Kullanım: java -jar api-test-generator.jar [SEÇENEKLER]");
        System.out.println("\nSeçenekler:");
        System.out.println("  -h, --help                  Bu yardım mesajını göster");
        System.out.println("  -v, --version               Uygulama versiyonunu göster");
        System.out.println("  -i, --input <DOSYA>         OpenAPI/Swagger JSON dosyası [varsayılan: endpoints2.json]");
        System.out.println("  -o, --output <DOSYA>        Çıktı Java dosyası [varsayılan: " + DEFAULT_OUTPUT_FILE + "]");
        System.out.println("  -k, --api-key-env <ENV_VAR> API anahtarını içeren çevre değişkeni [varsayılan: OPENAI_API_KEY]");
        System.out.println("  -m, --model <MODEL>         Kullanılacak LLM modeli [varsayılan: " + DEFAULT_MODEL + "]");
        System.out.println("  -t, --max-tokens <SAYI>     Maksimum token sayısı [varsayılan: " + DEFAULT_MAX_TOKENS + "]");
        System.out.println("  -r, --retries <SAYI>        Maksimum yeniden deneme sayısı [varsayılan: " + DEFAULT_MAX_RETRIES + "]");
        System.out.println("  --timeout <SANIYE>          API isteği zaman aşımı süresi [varsayılan: " + DEFAULT_TIMEOUT_SECONDS + "]");
        System.out.println("  --threads <SAYI>            Thread havuzu boyutu [varsayılan: " + DEFAULT_THREAD_POOL_SIZE + "]");
        System.out.println("  -c, --class-name <SINIF>    Oluşturulacak test sınıfının adı [varsayılan: ApiAutomationTests]");
        System.out.println("  -b, --base-uri <URI>        API baz URI'si [varsayılan: https://your-api-base-url.com]");
        System.out.println("  -f, --fallback              API hatalarında manuel şablonlar kullan");
        System.out.println("  -V, --verbose               Detaylı günlük kaydı aktif");
    }

    /**
     * Yapılandırma ile oluşturucu
     *
     * @param config Uygulama yapılandırması
     */
    public ClaudeSwaggerTestGenerator2(Configuration config) {
        this.config = config;

        // Log seviyesini ayarla
        if (config.isVerbose()) {
            Logger.getLogger("").setLevel(Level.FINE);
        }

        // API servisini başlat
        initializeApiService();
    }

    /**
     * API servisini başlatır
     */
    private void initializeApiService() {
        try {
            // .env dosyasından API anahtarını al
            Dotenv dotenv = Dotenv.load();
            String apiKey = dotenv.get(config.getApiKeyEnvVar());

            if (apiKey == null || apiKey.trim().isEmpty()) {
                throw new IllegalStateException(config.getApiKeyEnvVar() + " çevre değişkeni tanımlanmamış veya boş");
            }

            // OpenAI servisini oluştur (burada Duration ile çalışan constructor'ı kullanıyoruz)
            openAiService = new OpenAiService(apiKey, Duration.ofSeconds(config.getTimeoutSeconds()));
            logger.info("API servisi başarıyla başlatıldı. Model: " + config.getModel());

        } catch (Exception e) {
            logger.log(Level.SEVERE, "API servisi başlatılırken hata oluştu", e);
            throw new RuntimeException("API servisi başlatılamadı", e);
        }
    }

    /**
     * Uygulamayı çalıştırır
     */
    public void run() {
        try {
            // Başlangıç zamanı
            long startTime = System.currentTimeMillis();

            logger.info("===== API Test Generator v" + APP_VERSION + " =====");
            logger.info("İşlem başlatılıyor...");
            logger.info("Giriş dosyası: " + config.getInputFile());
            logger.info("Çıkış dosyası: " + config.getOutputFile());

            // JSON dosyasını okuma
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonContent = FileUtils.readFileToString(new File(config.getInputFile()), StandardCharsets.UTF_8);
            JsonNode rootNode = objectMapper.readTree(jsonContent);

            // Test senaryolarını oluşturacağımız dosya
            File outputFile = new File(config.getOutputFile());
            if (outputFile.getParentFile() != null) {
                outputFile.getParentFile().mkdirs();
            }

            try (FileWriter outputWriter = new FileWriter(outputFile)) {
                // Test sınıfının başlangıcını yazma
                writeTestClassHeader(outputWriter);

                // Tekrar kullanılabilir yardımcı metodlar ekle
                writeUtilityMethods(outputWriter);

                // Tüm endpointleri topla
                List<EndpointInfo> endpoints = collectEndpoints(rootNode);
                totalEndpoints = endpoints.size();
                logger.info(totalEndpoints + " endpoint bulundu.");

                // Thread havuzunu oluştur
                executorService = Executors.newFixedThreadPool(config.getThreadPoolSize());
                logger.info(config.getThreadPoolSize() + " thread'li işleme havuzu başlatıldı.");

                // Asenkron işleme başlat
                List<Future<String>> futures = new ArrayList<>();
                for (EndpointInfo endpoint : endpoints) {
                    futures.add(executorService.submit(() -> processEndpoint(endpoint)));
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

                // Test sınıfının sonunu yazma
                writeTestClassFooter(outputWriter);
            }

            // Bitiş zamanı
            long endTime = System.currentTimeMillis();
            double totalTime = (endTime - startTime) / 1000.0;

            // Özet raporu
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
            // Thread havuzunu kapat
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
     * Tekrar kullanılabilir yardımcı metodları test sınıfına ekler
     * Bu metodlar kodda tekrarı azaltmak için kullanılır
     *
     * @param writer Dosya yazıcısı
     * @throws IOException I/O hatası
     */
    private void writeUtilityMethods(FileWriter writer) throws IOException {
        writer.write("    /**\n");
        writer.write("     * Ortak request specification oluşturur\n");
        writer.write("     * Temel headerlar ve parametreler için kullanılır\n");
        writer.write("     */\n");
        writer.write("    private RequestSpecification commonRequestSpec() {\n");
        writer.write("        return given()\n");
        writer.write("            .contentType(ContentType.JSON)\n");
        writer.write("            .accept(ContentType.JSON)\n");
        writer.write("            .log().ifValidationFails();\n");
        writer.write("    }\n\n");

        writer.write("    /**\n");
        writer.write("     * Oturum gerektiren istekler için request specification oluşturur\n");
        writer.write("     */\n");
        writer.write("    private RequestSpecification authorizedRequestSpec() {\n");
        writer.write("        return commonRequestSpec()\n");
        writer.write("            .header(\"Authorization\", \"Bearer \" + getTestToken());\n");
        writer.write("    }\n\n");

        writer.write("    /**\n");
        writer.write("     * Test için token oluşturur veya döndürür\n");
        writer.write("     * Gerçek uygulamada burada token alınması için gerekli API çağrısı yapılabilir\n");
        writer.write("     */\n");
        writer.write("    private String getTestToken() {\n");
        writer.write("        // Test amaçlı sabit bir token\n");
        writer.write("        return \"test-token-for-automation\";\n");
        writer.write("    }\n\n");

        writer.write("    /**\n");
        writer.write("     * Test verisi oluşturan yardımcı metod\n");
        writer.write("     * Çeşitli kaynak türleri için test verisi oluşturur\n");
        writer.write("     */\n");
        writer.write("    private String createTestData(String resourceType) {\n");
        writer.write("        String timestamp = String.valueOf(System.currentTimeMillis());\n");
        writer.write("        switch (resourceType.toLowerCase()) {\n");
        writer.write("            case \"user\":\n");
        writer.write("                return \"{\\n\" +\n");
        writer.write("                    \"    \\\"name\\\": \\\"Test User \" + timestamp + \"\\\",\\n\" +\n");
        writer.write("                    \"    \\\"email\\\": \\\"test\" + timestamp + \"@example.com\\\",\\n\" +\n");
        writer.write("                    \"    \\\"active\\\": true\\n\" +\n");
        writer.write("                    \"}\";\n");
        writer.write("            case \"company\":\n");
        writer.write("                return \"{\\n\" +\n");
        writer.write("                    \"    \\\"name\\\": \\\"Test Company \" + timestamp + \"\\\",\\n\" +\n");
        writer.write("                    \"    \\\"description\\\": \\\"Test şirketi\\\",\\n\" +\n");
        writer.write("                    \"    \\\"employeeCount\\\": 50\\n\" +\n");
        writer.write("                    \"}\";\n");
        writer.write("            case \"job\":\n");
        writer.write("                return \"{\\n\" +\n");
        writer.write("                    \"    \\\"title\\\": \\\"Test Job \" + timestamp + \"\\\",\\n\" +\n");
        writer.write("                    \"    \\\"description\\\": \\\"Test iş ilanı\\\",\\n\" +\n");
        writer.write("                    \"    \\\"salary\\\": 5000,\\n\" +\n");
        writer.write("                    \"    \\\"isRemote\\\": true\\n\" +\n");
        writer.write("                    \"}\";\n");
        writer.write("            default:\n");
        writer.write("                return \"{\\n\" +\n");
        writer.write("                    \"    \\\"name\\\": \\\"Test Resource \" + timestamp + \"\\\",\\n\" +\n");
        writer.write("                    \"    \\\"description\\\": \\\"Test kaynağı\"\\n\" +\n");
        writer.write("                    \"}\";\n");
        writer.write("        }\n");
        writer.write("    }\n\n");

        writer.write("    /**\n");
        writer.write("     * Geçersiz test verisi oluşturan yardımcı metod\n");
        writer.write("     * Eksik veya hatalı veri için kullanılır\n");
        writer.write("     */\n");
        writer.write("    private String createInvalidTestData(String resourceType) {\n");
        writer.write("        // Eksik zorunlu alanlar içeren geçersiz veri\n");
        writer.write("        switch (resourceType.toLowerCase()) {\n");
        writer.write("            case \"user\":\n");
        writer.write("                return \"{\\n\" +\n");
        writer.write("                    \"    \\\"email\\\": \\\"invalid-email\\\"\\n\" +\n");
        writer.write("                    \"}\";\n");
        writer.write("            case \"company\":\n");
        writer.write("                return \"{\\n\" +\n");
        writer.write("                    \"    \\\"employeeCount\\\": -10\\n\" +\n");
        writer.write("                    \"}\";\n");
        writer.write("            default:\n");
        writer.write("                return \"{\\n\" +\n");
        writer.write("                    \"    \\\"id\\\": 0\\n\" +\n");
        writer.write("                    \"}\";\n");
        writer.write("        }\n");
        writer.write("    }\n\n");

        writer.write("    /**\n");
        writer.write("     * REST assured yanıt nesnesi için performans kontrolü yapar\n");
        writer.write("     * Uzun süren API çağrıları için uyarı verir\n");
        writer.write("     */\n");
        writer.write("    private void assertResponseTime(Response response, long maxAllowedTimeMs) {\n");
        writer.write("        long responseTime = response.getTime();\n");
        writer.write("        logger.info(\"Yanıt süresi: \" + responseTime + \" ms\");\n");
        writer.write("        Assertions.assertTrue(responseTime < maxAllowedTimeMs,\n");
        writer.write("            \"API yanıt süresi \" + maxAllowedTimeMs + \" ms'den fazla: \" + responseTime + \" ms\");\n");
        writer.write("    }\n\n");
    }

    /**
     * Endpoint'i işler ve test senaryosunu oluşturur
     *
     * @param endpoint Endpoint bilgisi
     * @return Oluşturulan test senaryosu
     */
    private String processEndpoint(EndpointInfo endpoint) {
        String endpointPath = endpoint.getPath();
        String httpMethod = endpoint.getMethod();
        JsonNode operationNode = endpoint.getOperationNode();

        try {
            // İlerleme bilgisi
            int current = processedEndpoints.incrementAndGet();
            String operationId = getOperationId(operationNode, httpMethod, endpointPath);

            logger.info(String.format("[%d/%d] Endpoint işleniyor: %s %s",
                    current, totalEndpoints, httpMethod.toUpperCase(), endpointPath));

            // LLM için prompt oluşturma
            String prompt = createPromptForEndpoint(endpointPath, httpMethod, operationNode);

            // Test senaryosu oluşturma
            String testCase = generateTestCaseWithRetry(prompt, operationId, httpMethod, endpointPath);

            logger.info(String.format("[%d/%d] Endpoint tamamlandı: %s %s",
                    current, totalEndpoints, httpMethod.toUpperCase(), endpointPath));

            return testCase;

        } catch (Exception e) {
            failedEndpoints.incrementAndGet();
            logger.log(Level.WARNING, "Endpoint işlenirken hata: " + httpMethod + " " + endpointPath, e);

            if (config.isUseFallbackOnError()) {
                // Hata durumunda fallback şablon kullan
                String operationId = getOperationId(operationNode, httpMethod, endpointPath);
                return generateFallbackTestCase(endpointPath, httpMethod, operationId);
            } else {
                // Hatayı yukarı fırlat
                throw new RuntimeException("Endpoint işleme hatası", e);
            }
        }
    }

    /**
     * LLM için sistem prompt'u oluşturur - Tekrar kullanım dikkate alınmış versiyon
     *
     * @return Sistem prompt'u
     */
    private String createSystemPrompt() {
        return "Sen bir API test otomasyonu uzmanısın. Verilen endpoint için kapsamlı, gerçekçi " +
                "ve çalıştırılabilir REST Assured ve JUnit 5 test senaryoları oluştur.\n\n" +
                "Önemli Kurallar:\n" +
                "1. ASLA import ifadeleri ekleme - bunlar zaten sınıf başında tanımlanmıştır\n" +
                "2. ASLA yeni sınıf tanımı oluşturma - yalnızca metod içeriği döndür\n" +
                "3. ASLA test metodunun imzasını dahil etme - sadece metod içeriğini ver\n" +
                "4. Her senaryoyu açıklayan Türkçe yorumlar ekle\n" +
                "5. Tüm senaryoları tek bir test metodu içinde oluştur, ayrı metodlar oluşturma\n" +
                "6. Rest Assured kütüphanesinin given(), when(), then() zincirini kullan\n" +
                "7. JUnit 5 Assertions kullan (assertEquals, assertTrue vb.)\n" +
                "8. Kod organizasyonu için bölüm yorumları ekle\n" +
                "9. Tekrarlayan kodları azaltmak için aşağıdaki hazır yardımcı metodları kullan:\n" +
                "   - commonRequestSpec(): Ortak request specification oluşturur (ContentType.JSON ve loglar aktif)\n" +
                "   - authorizedRequestSpec(): Yetkilendirme başlığı eklenmiş request specification\n" +
                "   - createTestData(String resourceType): 'user', 'company', 'job' gibi kaynak türleri için test verisi oluşturur\n" +
                "   - createInvalidTestData(String resourceType): Geçersiz test verisi oluşturur\n" +
                "   - assertResponseTime(Response response, long maxTimeMs): Yanıt süresini kontrol eder\n" +
                "10. Alternatif durum kodlarını kontrol ederken 'or()' metodunu KULLANMA, bunun yerine 'statusCode(anyOf(is(401), is(403)))' gibi Hamcrest matcher'larını kullan\n" +
                "11. JSON şema doğrulaması için bu formatı kullan: .body(matchesJsonSchemaInClasspath(\"schemaFile.json\"))\n";
    }

    /**
     * Belirli bir endpoint için test senaryosu oluşturma istemcisi oluşturur - Geliştirilmiş versiyon
     * Kod tekrarını azaltacak şekilde güncellenmiş
     *
     * @param endpointPath Endpoint yolu
     * @param httpMethod HTTP metodu
     * @param operationNode Operasyon düğümü
     * @return Oluşturulan prompt
     */
    private String createPromptForEndpoint(String endpointPath, String httpMethod, JsonNode operationNode) {
        StringBuilder prompt = new StringBuilder();

        // Temel bilgiler
        prompt.append("Verilen endpoint için Rest Assured ve JUnit 5 kullanarak kapsamlı bir test senaryosu oluştur:\n\n");
        prompt.append("Endpoint: ").append(endpointPath).append("\n");
        prompt.append("HTTP Metodu: ").append(httpMethod.toUpperCase()).append("\n");

        // Endpoint açıklaması
        if (operationNode.has("summary")) {
            prompt.append("Açıklama: ").append(operationNode.get("summary").asText()).append("\n");
        }

        prompt.append("\n");

        // Parametreleri ekleme
        if (operationNode.has("parameters")) {
            prompt.append("Parametreler:\n");
            JsonNode parameters = operationNode.get("parameters");
            for (JsonNode param : parameters) {
                String name = param.get("name").asText();
                String in = param.get("in").asText();
                boolean required = param.has("required") && param.get("required").asBoolean();

                prompt.append("- ").append(name).append(" (").append(in).append(")");
                if (required) {
                    prompt.append(" [zorunlu]");
                }
                prompt.append("\n");

                // Parametre açıklaması
                if (param.has("description")) {
                    prompt.append("  Açıklama: ").append(param.get("description").asText()).append("\n");
                }

                // Şema bilgileri
                if (param.has("schema")) {
                    JsonNode schema = param.get("schema");
                    describeSchema(schema, prompt, "  ");
                }
            }
            prompt.append("\n");
        }

        // Request body ekleme
        if (operationNode.has("requestBody")) {
            prompt.append("Request Body:\n");
            JsonNode requestBody = operationNode.get("requestBody");

            if (requestBody.has("description")) {
                prompt.append("Açıklama: ").append(requestBody.get("description").asText()).append("\n");
            }

            if (requestBody.has("content")) {
                JsonNode content = requestBody.get("content");
                if (content.has("application/json")) {
                    JsonNode jsonContent = content.get("application/json");
                    if (jsonContent.has("schema")) {
                        JsonNode schema = jsonContent.get("schema");
                        describeSchema(schema, prompt, "");

                        // Örnek JSON oluştur
                        try {
                            String exampleJson = generateExampleJson(schema);
                            prompt.append("\nÖrnek JSON Body:\n").append(exampleJson).append("\n");
                        } catch (Exception e) {
                            prompt.append("\n(Örnek JSON oluşturulamadı)\n");
                        }
                    }
                }
            }
            prompt.append("\n");
        }

        // Response ekleme
        if (operationNode.has("responses")) {
            prompt.append("Beklenen Yanıtlar:\n");
            JsonNode responses = operationNode.get("responses");
            Iterator<Map.Entry<String, JsonNode>> responseIterator = responses.fields();
            while (responseIterator.hasNext()) {
                Map.Entry<String, JsonNode> responseEntry = responseIterator.next();
                String statusCode = responseEntry.getKey();
                JsonNode responseDetail = responseEntry.getValue();

                prompt.append("- Status Code: ").append(statusCode).append("\n");
                if (responseDetail.has("description")) {
                    prompt.append("  Açıklama: ").append(responseDetail.get("description").asText()).append("\n");
                }

                if (responseDetail.has("content")) {
                    JsonNode content = responseDetail.get("content");
                    if (content.has("application/json")) {
                        JsonNode jsonContent = content.get("application/json");
                        if (jsonContent.has("schema")) {
                            JsonNode schema = jsonContent.get("schema");
                            prompt.append("  Yanıt Şeması:\n");
                            describeSchema(schema, prompt, "  ");
                        }
                    }
                }
            }
            prompt.append("\n");
        }

        // HTTP metoduna göre özelleştirilmiş test senaryoları
        prompt.append("\nAşağıdaki durumları test eden bir kod oluştur (import ifadeleri veya class tanımı olmadan SADECE metod içeriği olarak):\n");

        // GET metodları için test senaryoları
        if ("get".equalsIgnoreCase(httpMethod)) {
            prompt.append("- Geçerli parametrelerle başarılı veri çekme\n");
            prompt.append("- Sayfalandırma parametreleriyle çalışma (varsa)\n");
            prompt.append("- Filtre parametreleriyle veri filtreleme (varsa)\n");
            prompt.append("- Geçersiz ID ile 404 yanıtı doğrulama\n");
            prompt.append("- Yetkisiz erişim testi (401/403)\n");
            prompt.append("- Yanıt verilerinin şema ile uyumluluğunu doğrulama\n");
            prompt.append("- Performans kontrolü için yanıt süresinin ölçümü (assertResponseTime yardımcı metodunu kullan)\n");
        }
        // POST metodları için test senaryoları
        else if ("post".equalsIgnoreCase(httpMethod)) {
            prompt.append("- Geçerli verilerle yeni kayıt oluşturma\n");
            prompt.append("- Eksik zorunlu alanlarla 400 yanıtı doğrulama\n");
            prompt.append("- Geçersiz formatta verilerle 400 yanıtı doğrulama\n");
            prompt.append("- Oluşturulan kaynağı GET ile doğrulama (zincirleme)\n");
            prompt.append("- Yetkilendirme kontrolü\n");
            prompt.append("- Veri tekrarı kontrolü (aynı veriyle tekrar gönderim)\n");
            prompt.append("- Performans kontrolü için yanıt süresinin ölçümü (assertResponseTime yardımcı metodunu kullan)\n");
        }
        // PUT metodları için test senaryoları
        else if ("put".equalsIgnoreCase(httpMethod)) {
            prompt.append("- Geçerli güncelleştirme verisiyle kayıt güncelleme\n");
            prompt.append("- Eksik zorunlu alanlarla 400 yanıtı doğrulama\n");
            prompt.append("- Geçersiz ID ile 404 yanıtı doğrulama\n");
            prompt.append("- Güncellenmiş kaynağı GET ile doğrulama (zincirleme)\n");
            prompt.append("- Yetkilendirme kontrolü\n");
            prompt.append("- Tüm ve kısmi güncelleştirme senaryoları\n");
            prompt.append("- Performans kontrolü için yanıt süresinin ölçümü (assertResponseTime yardımcı metodunu kullan)\n");
        }
        // DELETE metodları için test senaryoları
        else if ("delete".equalsIgnoreCase(httpMethod)) {
            prompt.append("- Geçerli ID ile kayıt silme\n");
            prompt.append("- Geçersiz ID ile 404 yanıtı doğrulama\n");
            prompt.append("- Silinen kaynağa GET ile erişim denemesi (404 beklenir)\n");
            prompt.append("- Yetkilendirme kontrolü\n");
            prompt.append("- Bağlantılı kaynaklara etkisini doğrulama\n");
            prompt.append("- Performans kontrolü için yanıt süresinin ölçümü (assertResponseTime yardımcı metodunu kullan)\n");
        }
        // PATCH metodları için test senaryoları
        else if ("patch".equalsIgnoreCase(httpMethod)) {
            prompt.append("- Geçerli kısmi güncelleştirme verisiyle kayıt güncelleme\n");
            prompt.append("- Geçersiz formatta verilerle 400 yanıtı doğrulama\n");
            prompt.append("- Geçersiz ID ile 404 yanıtı doğrulama\n");
            prompt.append("- Güncellenmiş kaynağı GET ile doğrulama (zincirleme)\n");
            prompt.append("- Yetkilendirme kontrolü\n");
            prompt.append("- Performans kontrolü için yanıt süresinin ölçümü (assertResponseTime yardımcı metodunu kullan)\n");
        }
        // Diğer HTTP metodları için genel test senaryoları
        else {
            prompt.append("- Temel işlevsellik testi\n");
            prompt.append("- Geçersiz parametrelerle negatif test\n");
            prompt.append("- Yetkilendirme kontrolü\n");
            prompt.append("- Yanıt formatı doğrulama\n");
            prompt.append("- Performans kontrolü için yanıt süresinin ölçümü (assertResponseTime yardımcı metodunu kullan)\n");
        }

        prompt.append("\nÖnemli Not: Var olmayan bir endpoint testi tüm endpointler için ayrı ayrı yapılmamalıdır. Bu test senaryosunu sadece gerekli görülen yerlerde ekle.");

        prompt.append("\n\nÖnemli Kurallar:\n");
        prompt.append("- İmport ifadeleri EKLEME - bunlar zaten sınıf başında tanımlanmıştır\n");
        prompt.append("- Rest Assured için given(), when(), then() zinciri kullan\n");
        prompt.append("- Türkçe açıklama yorumları ekle\n");
        prompt.append("- Test metodu içerisinde yeni metodlar tanımlama\n");
        prompt.append("- Sınıf tanımları YAPMA, sadece metod içeriği döndür\n");
        prompt.append("- Tüm testleri tek bir metod içinde organize et, açıklayıcı yorum bloklarıyla ayır\n");
        prompt.append("- Her endpoint için farklı senaryolar oluştur, kopyala-yapıştır kullanma\n");

        // Kod tekrarını azaltma açıklaması
        prompt.append("\nKod tekrarını azaltmak için:\n");
        prompt.append("- Aynı istek için RequestSpecification tekrar tekrar oluşturmak yerine commonRequestSpec() veya authorizedRequestSpec() kullan\n");
        prompt.append("- Test verisi oluşturmak için createTestData() metodu kullan\n");
        prompt.append("- Yetkilendirme testlerinde authorizedRequestSpec() kullan\n");
        prompt.append("- Performans kontrolü için assertResponseTime() metodunu kullan\n");

        return prompt.toString();
    }

    /**
     * Test sınıfının başlık kısmını yazar
     *
     * @param writer Dosya yazıcısı
     * @throws IOException I/O hatası
     */
    private void writeTestClassHeader(FileWriter writer) throws IOException {
        writer.write("package tests;\n\n");

        // Temel importlar
        writer.write("import io.restassured.RestAssured;\n");
        writer.write("import io.restassured.http.ContentType;\n");
        writer.write("import io.restassured.response.Response;\n");
        writer.write("import io.restassured.specification.RequestSpecification;\n");
        writer.write("import io.restassured.builder.RequestSpecBuilder;\n");

        // JUnit importları
        writer.write("import org.junit.jupiter.api.BeforeAll;\n");
        writer.write("import org.junit.jupiter.api.BeforeEach;\n");
        writer.write("import org.junit.jupiter.api.Test;\n");
        writer.write("import org.junit.jupiter.api.DisplayName;\n");
        writer.write("import org.junit.jupiter.api.TestInfo;\n");
        writer.write("import org.junit.jupiter.api.TestInstance;\n");
        writer.write("import org.junit.jupiter.api.Nested;\n");
        writer.write("import org.junit.jupiter.api.Tag;\n");
        writer.write("import org.junit.jupiter.api.Assertions;\n");
        writer.write("import org.junit.jupiter.params.ParameterizedTest;\n");
        writer.write("import org.junit.jupiter.params.provider.ValueSource;\n");

        // RestAssured statik importları
        writer.write("import static io.restassured.RestAssured.given;\n");
        writer.write("import static io.restassured.RestAssured.when;\n");

        // Hamcrest importları
        writer.write("import static org.hamcrest.Matchers.*;\n");
        writer.write("import static org.hamcrest.MatcherAssert.assertThat;\n");

        // JUnit Assertions statik importları
        writer.write("import static org.junit.jupiter.api.Assertions.*;\n");

        // Java standart importları
        writer.write("import java.util.logging.Logger;\n");
        writer.write("import java.util.Map;\n");
        writer.write("import java.util.HashMap;\n");
        writer.write("import java.time.Duration;\n");

        // JSON işleme için import
        writer.write("import org.json.JSONObject;\n");
        writer.write("import org.json.JSONArray;\n");
        writer.write("import org.json.JSONException;\n");

        // JsonSchemaValidator modülü için import
        writer.write("import static io.restassured.module.jsv.JsonSchemaValidator.*;\n\n");

        // JavaDoc ve sınıf tanımı
        writer.write("/**\n");
        writer.write(" * API test senaryoları sınıfı\n");
        writer.write(" * Bu sınıf otomatik olarak API Test Generator tarafından oluşturulmuştur.\n");
        writer.write(" * Sürüm " + APP_VERSION + "\n");
        writer.write(" */\n");
        writer.write("@TestInstance(TestInstance.Lifecycle.PER_CLASS)\n");
        writer.write("public class " + config.getTestClassName() + " {\n\n");

        // Logger ve ortak değişkenler
        writer.write("    private static final Logger logger = Logger.getLogger(" + config.getTestClassName() + ".class.getName());\n\n");

        // Setup metodu
        writer.write("    @BeforeAll\n");
        writer.write("    public static void setup() {\n");
        writer.write("        RestAssured.baseURI = \"" + config.getBaseUri() + "\";\n");
        writer.write("        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();\n");
        writer.write("    }\n\n");
    }

    /**
     * Test sınıfının bitiş kısmını yazar
     *
     * @param writer Dosya yazıcısı
     * @throws IOException I/O hatası
     */
    private void writeTestClassFooter(FileWriter writer) throws IOException {
        writer.write("}\n");
    }

    /**
     * Root JSON objesinden tüm endpointleri toplar
     *
     * @param rootNode JSON kök düğümü
     * @return Endpoint bilgileri listesi
     */
    private List<EndpointInfo> collectEndpoints(JsonNode rootNode) {
        List<EndpointInfo> endpoints = new ArrayList<>();

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

                // Standart olmayan alanları filtrele
                if (!httpMethod.equals("parameters") && !httpMethod.equals("summary") && !httpMethod.equals("description")) {
                    endpoints.add(new EndpointInfo(endpointPath, httpMethod, operationNode));
                }
            }
        }

        return endpoints;
    }

    /**
     * JSON şemasını açıklar
     *
     * @param schema JSON şema düğümü
     * @param builder String builder
     * @param indent Girinti
     */
    private void describeSchema(JsonNode schema, StringBuilder builder, String indent) {
        if (schema == null) {
            return;
        }

        if (schema.has("type")) {
            builder.append(indent).append("Tür: ").append(schema.get("type").asText()).append("\n");
        }

        if (schema.has("format")) {
            builder.append(indent).append("Format: ").append(schema.get("format").asText()).append("\n");
        }

        if (schema.has("description")) {
            builder.append(indent).append("Açıklama: ").append(schema.get("description").asText()).append("\n");
        }

        if (schema.has("enum")) {
            builder.append(indent).append("Enum Değerleri: ");
            JsonNode enumValues = schema.get("enum");
            List<String> values = new ArrayList<>();
            for (JsonNode value : enumValues) {
                values.add(value.asText());
            }
            builder.append(String.join(", ", values)).append("\n");
        }

        if (schema.has("properties")) {
            builder.append(indent).append("Özellikler:\n");
            JsonNode properties = schema.get("properties");
            Iterator<Map.Entry<String, JsonNode>> fields = properties.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                String name = field.getKey();
                JsonNode propertySchema = field.getValue();

                builder.append(indent).append("  - ").append(name);

                // Zorunlu alan kontrolü
                if (schema.has("required")) {
                    JsonNode required = schema.get("required");
                    for (JsonNode req : required) {
                        if (req.asText().equals(name)) {
                            builder.append(" [zorunlu]");
                            break;
                        }
                    }
                }
                builder.append("\n");

                describeSchema(propertySchema, builder, indent + "    ");
            }
        }

        if (schema.has("items") && schema.get("type").asText().equals("array")) {
            builder.append(indent).append("Dizi Öğeleri:\n");
            JsonNode items = schema.get("items");
            describeSchema(items, builder, indent + "  ");
        }
    }

    /**
     * JSON şemasından örnek JSON oluşturur
     *
     * @param schema JSON şema düğümü
     * @return Oluşturulan örnek JSON string
     * @throws JsonProcessingException JSON oluşturma hatası
     */
    private String generateExampleJson(JsonNode schema) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode rootNode = objectMapper.createObjectNode();

        if (schema.has("properties")) {
            JsonNode properties = schema.get("properties");
            Iterator<Map.Entry<String, JsonNode>> fields = properties.fields();

            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                String name = field.getKey();
                JsonNode propertySchema = field.getValue();

                if (propertySchema.has("type")) {
                    String type = propertySchema.get("type").asText();

                    switch (type) {
                        case "string":
                            if (propertySchema.has("example")) {
                                rootNode.put(name, propertySchema.get("example").asText());
                            } else if (propertySchema.has("enum")) {
                                rootNode.put(name, propertySchema.get("enum").get(0).asText());
                            } else if (propertySchema.has("format")) {
                                String format = propertySchema.get("format").asText();
                                if (format.equals("date")) {
                                    rootNode.put(name, "2023-01-01");
                                } else if (format.equals("date-time")) {
                                    rootNode.put(name, "2023-01-01T12:00:00Z");
                                } else if (format.equals("email")) {
                                    rootNode.put(name, "ornek@example.com");
                                } else {
                                    rootNode.put(name, "string_" + name);
                                }
                            } else {
                                rootNode.put(name, "string_" + name);
                            }
                            break;
                        case "integer":
                        case "number":
                            if (propertySchema.has("example")) {
                                if (propertySchema.get("example").isInt()) {
                                    rootNode.put(name, propertySchema.get("example").asInt());
                                } else {
                                    rootNode.put(name, propertySchema.get("example").asDouble());
                                }
                            } else {
                                rootNode.put(name, 123);
                            }
                            break;
                        case "boolean":
                            if (propertySchema.has("example")) {
                                rootNode.put(name, propertySchema.get("example").asBoolean());
                            } else {
                                rootNode.put(name, true);
                            }
                            break;
                        case "array":
                            // Basit dizi örneği
                            rootNode.putArray(name);
                            break;
                        case "object":
                            // İç içe nesne için özyinelemeli çağrı
                            if (propertySchema.has("properties")) {
                                rootNode.set(name, objectMapper.readTree(generateExampleJson(propertySchema)));
                            } else {
                                rootNode.putObject(name);
                            }
                            break;
                    }
                }
            }
        }

        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
    }

    /**
     * Yeniden deneme mekanizması ile test senaryosu oluşturur
     *
     * @param prompt İstek metni
     * @param operationId Operasyon ID
     * @param httpMethod HTTP metodu
     * @param endpointPath Endpoint yolu
     * @return Oluşturulan test senaryosu
     */
    private String generateTestCaseWithRetry(String prompt, String operationId, String httpMethod, String endpointPath) {
        int retryCount = 0;
        long waitTime = 1000; // Başlangıç bekleme süresi (ms)
        Exception lastException = null;

        while (retryCount < config.getMaxRetries()) {
            try {
                return generateTestCase(prompt, operationId, httpMethod, endpointPath);
            } catch (Exception e) {
                lastException = e;
                retryCount++;

                if (config.isVerbose()) {
                    logger.warning("API çağrısı başarısız oldu (" + retryCount + "/" + config.getMaxRetries() + "): " + e.getMessage());
                }

                if (retryCount >= config.getMaxRetries()) {
                    break;
                }

                // Exponential backoff ile bekleme
                try {
                    if (config.isVerbose()) {
                        logger.info("Yeniden denemeden önce " + waitTime + "ms bekleniyor...");
                    }
                    Thread.sleep(waitTime);
                    waitTime *= 2; // Her denemede bekleme süresini iki katına çıkar
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Bekleme işlemi kesintiye uğradı", ie);
                }
            }
        }

        // Tüm denemeler başarısız oldu
        if (config.isUseFallbackOnError()) {
            logger.warning("Maksimum yeniden deneme sayısına ulaşıldı. Manuel şablon kullanılacak.");
            return generateFallbackTestCase(endpointPath, httpMethod, operationId);
        } else {
            throw new RuntimeException("API çağrısı " + config.getMaxRetries() + " denemeden sonra başarısız oldu", lastException);
        }
    }

    /**
     * API çağrısı yaparak test senaryosu oluşturur
     *
     * @param prompt İstek metni
     * @param operationId Operasyon ID
     * @param httpMethod HTTP metodu
     * @param endpointPath Endpoint yolu
     * @return Oluşturulan test senaryosu
     */
    private String generateTestCase(String prompt, String operationId, String httpMethod, String endpointPath) {
        if (config.isVerbose()) {
            logger.info("API'ye istek gönderiliyor...");
        }

        List<ChatMessage> messages = new ArrayList<>();

        // İyileştirilmiş sistem prompt'u kullan
        messages.add(new ChatMessage(ChatMessageRole.SYSTEM.value(), createSystemPrompt()));

        // Endpoint prompt'unu ekle
        messages.add(new ChatMessage(ChatMessageRole.USER.value(), prompt));

        // Hiçbir import kullanmaması ve class tanımlamaması için örnek olumlu pekiştirme
        messages.add(new ChatMessage(ChatMessageRole.ASSISTANT.value(),
                "Anladım, REST Assured ve JUnit 5 ile test senaryosu oluşturacağım. " +
                        "Import ifadeleri eklemeyeceğim ve sadece test metodu içeriğini döndüreceğim. " +
                        "Kod tekrarlarını azaltmak için commonRequestSpec(), createTestData() gibi hazır yardımcı metodlar kullanacağım."));

        // Son kullanıcı direktifi ekle
        messages.add(new ChatMessage(ChatMessageRole.USER.value(),
                "Lütfen test senaryosunu oluştur. Import ifadeleri, sınıf tanımları veya metod imzaları OLMADAN " +
                        "SADECE metod içeriğini ver. Tüm testleri tek bir metod içinde oluştur. Kod tekrarını azaltmak için yardımcı metodları kullan."));

        ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                .model(config.getModel())
                .messages(messages)
                .maxTokens(config.getMaxTokens())
                .temperature(0.2) // Daha tutarlı yanıtlar için düşük sıcaklık
                .build();

        if (config.isVerbose()) {
            logger.info("API yanıtı bekleniyor...");
        }

        String response = openAiService.createChatCompletion(completionRequest)
                .getChoices().get(0).getMessage().getContent();

        if (config.isVerbose()) {
            logger.info("API yanıtı alındı.");
        }

        // Yanıtı formatlayarak test metodu oluştur
        return formatTestCase(response, operationId, httpMethod, endpointPath);
    }

    /**
     * API yanıtını formatlayarak test senaryosu oluşturur
     *
     * @param response API yanıtı
     * @param operationId Operasyon ID
     * @param httpMethod HTTP metodu
     * @param endpointPath Endpoint yolu
     * @return Oluşturulan test senaryosu
     */
    private String formatTestCase(String response, String operationId, String httpMethod, String endpointPath) {
        StringBuilder formattedTestCase = new StringBuilder();

        // JavaDoc yorumu ekle
        formattedTestCase.append("    /**\n");
        formattedTestCase.append("     * ").append(operationId).append(" - ")
                .append(httpMethod.toUpperCase()).append(" ").append(endpointPath).append("\n");
        formattedTestCase.append("     */\n");

        // Test anotasyonları ekle
        formattedTestCase.append("    @Test\n");
        formattedTestCase.append("    @DisplayName(\"Test ")
                .append(operationId).append(" - ")
                .append(httpMethod.toUpperCase()).append(" ")
                .append(endpointPath).append("\")\n");

        // HTTP metodu için tag ekle
        formattedTestCase.append("    @Tag(\"").append(httpMethod.toLowerCase()).append("\")\n");

        // Metod tanımı başlat - sanitize edilmiş operationId kullan
        formattedTestCase.append("    public void test").append(sanitizeMethodName(operationId)).append("() {\n");

        // Yanıtın içeriğini temizle ve ekle
        String cleanedResponse = cleanResponse(response);

        // Temizlenmiş içeriği doğru girintiyle ekle
        if (cleanedResponse.trim().isEmpty()) {
            // Boşsa fallback içerik ekle
            formattedTestCase.append("        // API test içeriği oluşturulamadı - lütfen manuel olarak implement edin\n");
            formattedTestCase.append("        logger.warning(\"Test içeriği '").append(operationId).append("' için oluşturulamadı\");\n");
        } else {
            formattedTestCase.append("        ").append(cleanedResponse.trim().replace("\n", "\n        ")).append("\n");
        }

        // Metodu kapat
        formattedTestCase.append("    }");

        return formattedTestCase.toString();
    }

    /**
     * API yanıtını temizler
     *
     * @param response API yanıtı
     * @return Temizlenmiş yanıt
     */
    private String cleanResponse(String response) {
        // İlk olarak, backtick kod bloklarını temizle
        String cleaned = response.replaceAll("```java", "").replaceAll("```", "");

        // Test metodu içindeki import ifadelerini kaldır
        cleaned = cleaned.replaceAll("(?m)^\\s*import\\s+[\\w\\.\\*]+;\\s*$", "");

        // İçiçe sınıf tanımlarını kaldır
        cleaned = cleaned.replaceAll("(?s)public\\s+class\\s+\\w+\\s*\\{.*", "");

        // Fazladan olan class ve method tanımlarını temizle
        if (cleaned.contains("@Test") || cleaned.contains("public void test") || cleaned.contains("public void ")) {
            int startIndex = cleaned.indexOf("{");
            int endIndex = cleaned.lastIndexOf("}");

            if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
                cleaned = cleaned.substring(startIndex + 1, endIndex).trim();
            }
        }

        // Boş satırlar varsa her birini tek bir boş satırla değiştir
        cleaned = cleaned.replaceAll("(?m)^\\s*$\\n{2,}", "\n");

        return cleaned;
    }

    /**
     * API hatası durumunda manuel test şablonu oluşturur - Geliştirilmiş versiyon
     * Her HTTP metodu için daha kapsamlı test şablonları içerir
     *
     * @param endpoint Endpoint yolu
     * @param httpMethod HTTP metodu
     * @param operationId Operasyon ID
     * @return Oluşturulan test şablonu
     */
    private String generateFallbackTestCase(String endpoint, String httpMethod, String operationId) {
        StringBuilder testCase = new StringBuilder();
        testCase.append("    /**\n");
        testCase.append("     * ").append(operationId).append(" - ")
                .append(httpMethod.toUpperCase()).append(" ").append(endpoint).append("\n");
        testCase.append("     * NOT: Bu test senaryosu API hatası nedeniyle otomatik oluşturulmuştur.\n");
        testCase.append("     */\n");
        testCase.append("    @Test\n");
        testCase.append("    @DisplayName(\"Test ")
                .append(operationId).append(" - ")
                .append(httpMethod.toUpperCase()).append(" ")
                .append(endpoint).append(" (Manuel oluşturulmuş şablon)\")\n");
        testCase.append("    @Tag(\"fallback\")\n");
        testCase.append("    @Tag(\"").append(httpMethod.toLowerCase()).append("\")\n");
        testCase.append("    public void test").append(formatOperationId(operationId)).append("(TestInfo testInfo) {\n");
        testCase.append("        // Test bilgisini logla\n");
        testCase.append("        logger.info(\"Test çalıştırılıyor: \" + testInfo.getDisplayName());\n\n");

        // HTTP metodu için temel test şablonu ekleme
        switch (httpMethod.toLowerCase()) {
            case "get":
                appendImprovedGetTestTemplate(testCase, endpoint);
                break;
            case "post":
                appendImprovedPostTestTemplate(testCase, endpoint);
                break;
            case "put":
                appendImprovedPutTestTemplate(testCase, endpoint);
                break;
            case "delete":
                appendImprovedDeleteTestTemplate(testCase, endpoint);
                break;
            case "patch":
                appendImprovedPatchTestTemplate(testCase, endpoint);
                break;
            default:
                appendGenericTestTemplate(testCase, endpoint, httpMethod);
                break;
        }

        testCase.append("    }");

        return testCase.toString();
    }

    /**
     * GET metodu için geliştirilmiş test şablonu ekler
     *
     * @param testCase Şablon oluşturucu
     * @param endpoint Endpoint yolu
     */
    private void appendImprovedGetTestTemplate(StringBuilder testCase, String endpoint) {
        // 1. Başarılı senaryo
        testCase.append("        // 1. Başarılı veri çekme senaryosu\n");
        testCase.append("        logger.info(\"Başarılı veri çekme testi başlatılıyor\");\n");
        testCase.append("        Response response = commonRequestSpec()\n");
        testCase.append("            .param(\"page\", 1)\n");
        testCase.append("            .param(\"size\", 10)\n");
        testCase.append("        .when()\n");
        testCase.append("            .get(\"").append(endpoint).append("\")\n");
        testCase.append("        .then()\n");
        testCase.append("            .statusCode(200) // Beklenen durum kodu\n");
        testCase.append("            .body(\"$\", not(empty()))\n");
        testCase.append("            .extract().response();\n\n");

        // 2. Performans kontrolü
        testCase.append("        // 2. Performans kontrolü\n");
        testCase.append("        assertResponseTime(response, 5000); // 5 saniye zaman aşımı\n\n");

        // 3. Geçersiz ID testi
        testCase.append("        // 3. Geçersiz ID ile 404 hatası kontrolü\n");
        testCase.append("        commonRequestSpec()\n");
        testCase.append("        .when()\n");
        testCase.append("            .get(\"").append(endpoint).append("/99999999\") // Geçersiz ID\n");
        testCase.append("        .then()\n");
        testCase.append("            .statusCode(404); // Not Found beklenir\n\n");

        // 4. Yetkilendirme testi
        testCase.append("        // 4. Yetkilendirme testi\n");
        testCase.append("        // Authorization header olmadan deneme\n");
        testCase.append("        given()\n");
        testCase.append("            .contentType(ContentType.JSON)\n");
        testCase.append("            // Yetkilendirme başlığı olmadan\n");
        testCase.append("        .when()\n");
        testCase.append("            .get(\"").append(endpoint).append("\")\n");
        testCase.append("        .then()\n");
        testCase.append("            .statusCode(anyOf(is(401), is(403))); // Unauthorized veya Forbidden beklenir\n");
    }

    /**
     * POST metodu için geliştirilmiş test şablonu ekler
     *
     * @param testCase Şablon oluşturucu
     * @param endpoint Endpoint yolu
     */
    private void appendImprovedPostTestTemplate(StringBuilder testCase, String endpoint) {
        // 1. Başarılı oluşturma senaryosu
        testCase.append("        // 1. Başarılı kayıt oluşturma senaryosu\n");
        testCase.append("        logger.info(\"Başarılı kayıt oluşturma testi başlatılıyor\");\n");
        testCase.append("        // Kaynak türüne uygun test verisi oluştur\n");
        testCase.append("        String requestBody = createTestData(\"resource\");\n\n");

        testCase.append("        Response response = commonRequestSpec()\n");
        testCase.append("            .contentType(ContentType.JSON)\n");
        testCase.append("            .body(requestBody)\n");
        testCase.append("        .when()\n");
        testCase.append("            .post(\"").append(endpoint).append("\")\n");
        testCase.append("        .then()\n");
        testCase.append("            .statusCode(anyOf(is(200), is(201), is(204))) // Beklenen başarı kodları\n");
        testCase.append("            .body(\"id\", notNullValue())\n");
        testCase.append("            .extract().response();\n\n");

        // 2. Performans kontrolü
        testCase.append("        // 2. Performans kontrolü\n");
        testCase.append("        assertResponseTime(response, 5000); // 5 saniye zaman aşımı\n\n");

        // 3. Eksik zorunlu alan testi
        testCase.append("        // 3. Eksik zorunlu alanlar testi\n");
        testCase.append("        String invalidBody = createInvalidTestData(\"resource\");\n\n");

        testCase.append("        commonRequestSpec()\n");
        testCase.append("            .contentType(ContentType.JSON)\n");
        testCase.append("            .body(invalidBody)\n");
        testCase.append("        .when()\n");
        testCase.append("            .post(\"").append(endpoint).append("\")\n");
        testCase.append("        .then()\n");
        testCase.append("            .statusCode(400); // Bad Request beklenir\n\n");

        // 4. Yetkilendirme testi
        testCase.append("        // 4. Yetkilendirme testi\n");
        testCase.append("        given()\n");
        testCase.append("            .contentType(ContentType.JSON)\n");
        testCase.append("            .body(requestBody)\n");
        testCase.append("            // Yetkilendirme başlığı olmadan\n");
        testCase.append("        .when()\n");
        testCase.append("            .post(\"").append(endpoint).append("\")\n");
        testCase.append("        .then()\n");
        testCase.append("            .statusCode(anyOf(is(401), is(403))); // Unauthorized veya Forbidden beklenir\n");
    }

    /**
     * PUT metodu için geliştirilmiş test şablonu ekler
     *
     * @param testCase Şablon oluşturucu
     * @param endpoint Endpoint yolu
     */
    private void appendImprovedPutTestTemplate(StringBuilder testCase, String endpoint) {
        // 1. Başarılı güncelleme senaryosu
        testCase.append("        // 1. Başarılı güncelleme senaryosu\n");
        testCase.append("        logger.info(\"Başarılı güncelleme testi başlatılıyor\");\n");
        testCase.append("        // Önce var olan bir kaynağı varsayalım\n");
        testCase.append("        Integer resourceId = 1; // Test için varsayılan ID\n\n");

        testCase.append("        // Güncelleme için test verisi oluştur\n");
        testCase.append("        String requestBody = createTestData(\"resource\");\n\n");

        testCase.append("        Response response = commonRequestSpec()\n");
        testCase.append("            .contentType(ContentType.JSON)\n");
        testCase.append("            .body(requestBody)\n");
        testCase.append("        .when()\n");
        testCase.append("            .put(\"").append(endpoint).append("/\" + resourceId)\n");
        testCase.append("        .then()\n");
        testCase.append("            .statusCode(200) // OK beklenir\n");
        testCase.append("            .extract().response();\n\n");

        // 2. Performans kontrolü
        testCase.append("        // 2. Performans kontrolü\n");
        testCase.append("        assertResponseTime(response, 5000); // 5 saniye zaman aşımı\n\n");

        // 3. Geçersiz ID ile güncelleme
        testCase.append("        // 3. Geçersiz ID ile güncelleme testi\n");
        testCase.append("        commonRequestSpec()\n");
        testCase.append("            .contentType(ContentType.JSON)\n");
        testCase.append("            .body(requestBody)\n");
        testCase.append("        .when()\n");
        testCase.append("            .put(\"").append(endpoint).append("/99999999\")\n");
        testCase.append("        .then()\n");
        testCase.append("            .statusCode(404); // Not Found beklenir\n\n");

        // 4. Yetkilendirme testi
        testCase.append("        // 4. Yetkilendirme testi\n");
        testCase.append("        given()\n");
        testCase.append("            .contentType(ContentType.JSON)\n");
        testCase.append("            .body(requestBody)\n");
        testCase.append("            // Yetkilendirme başlığı olmadan\n");
        testCase.append("        .when()\n");
        testCase.append("            .put(\"").append(endpoint).append("/\" + resourceId)\n");
        testCase.append("        .then()\n");
        testCase.append("            .statusCode(anyOf(is(401), is(403))); // Unauthorized veya Forbidden beklenir\n");
    }

    /**
     * DELETE metodu için geliştirilmiş test şablonu ekler
     *
     * @param testCase Şablon oluşturucu
     * @param endpoint Endpoint yolu
     */
    private void appendImprovedDeleteTestTemplate(StringBuilder testCase, String endpoint) {
        // 1. Başarılı silme senaryosu
        testCase.append("        // 1. Başarılı silme senaryosu\n");
        testCase.append("        logger.info(\"Başarılı silme testi başlatılıyor\");\n");
        testCase.append("        // Silinecek bir kaynak ID'si\n");
        testCase.append("        Integer resourceId = 1; // Test için varsayılan ID\n\n");

        testCase.append("        Response response = commonRequestSpec()\n");
        testCase.append("        .when()\n");
        testCase.append("            .delete(\"").append(endpoint).append("/\" + resourceId)\n");
        testCase.append("        .then()\n");
        testCase.append("            .statusCode(anyOf(is(200), is(202), is(204))) // Beklenen başarı kodları\n");
        testCase.append("            .extract().response();\n\n");

        // 2. Performans kontrolü
        testCase.append("        // 2. Performans kontrolü\n");
        testCase.append("        assertResponseTime(response, 5000); // 5 saniye zaman aşımı\n\n");

        // 3. Geçersiz ID ile silme denemesi
        testCase.append("        // 3. Geçersiz ID ile silme testi\n");
        testCase.append("        commonRequestSpec()\n");
        testCase.append("        .when()\n");
        testCase.append("            .delete(\"").append(endpoint).append("/99999999\")\n");
        testCase.append("        .then()\n");
        testCase.append("            .statusCode(404); // Not Found beklenir\n\n");

        // 4. Yetkilendirme testi
        testCase.append("        // 4. Yetkilendirme testi\n");
        testCase.append("        given()\n");
        testCase.append("            // Yetkilendirme başlığı olmadan\n");
        testCase.append("        .when()\n");
        testCase.append("            .delete(\"").append(endpoint).append("/\" + resourceId)\n");
        testCase.append("        .then()\n");
        testCase.append("            .statusCode(anyOf(is(401), is(403))); // Unauthorized veya Forbidden beklenir\n");
    }

    /**
     * PATCH metodu için geliştirilmiş test şablonu ekler
     *
     * @param testCase Şablon oluşturucu
     * @param endpoint Endpoint yolu
     */
    private void appendImprovedPatchTestTemplate(StringBuilder testCase, String endpoint) {
        // 1. Başarılı kısmi güncelleme senaryosu
        testCase.append("        // 1. Başarılı kısmi güncelleme senaryosu\n");
        testCase.append("        logger.info(\"Başarılı kısmi güncelleme testi başlatılıyor\");\n");
        testCase.append("        // Güncellenecek bir kaynak ID'si\n");
        testCase.append("        Integer resourceId = 1; // Test için varsayılan ID\n\n");

        testCase.append("        // Sadece bazı alanlar içeren güncellenmiş veri\n");
        testCase.append("        String patchBody = \"{\\n\" +\n");
        testCase.append("            \"    \\\"name\\\": \\\"Patched Resource Name\\\"\\n\" +\n");
        testCase.append("            \"}\";\n\n");

        testCase.append("        Response response = commonRequestSpec()\n");
        testCase.append("            .contentType(ContentType.JSON)\n");
        testCase.append("            .body(patchBody)\n");
        testCase.append("        .when()\n");
        testCase.append("            .patch(\"").append(endpoint).append("/\" + resourceId)\n");
        testCase.append("        .then()\n");
        testCase.append("            .statusCode(200) // OK beklenir\n");
        testCase.append("            .extract().response();\n\n");

        // 2. Performans kontrolü
        testCase.append("        // 2. Performans kontrolü\n");
        testCase.append("        assertResponseTime(response, 5000); // 5 saniye zaman aşımı\n\n");

        // 3. Geçersiz ID ile güncelleme
        testCase.append("        // 3. Geçersiz ID ile güncelleme testi\n");
        testCase.append("        commonRequestSpec()\n");
        testCase.append("            .contentType(ContentType.JSON)\n");
        testCase.append("            .body(patchBody)\n");
        testCase.append("        .when()\n");
        testCase.append("            .patch(\"").append(endpoint).append("/99999999\")\n");
        testCase.append("        .then()\n");
        testCase.append("            .statusCode(404); // Not Found beklenir\n\n");

        // 4. Yetkilendirme testi
        testCase.append("        // 4. Yetkilendirme testi\n");
        testCase.append("        given()\n");
        testCase.append("            .contentType(ContentType.JSON)\n");
        testCase.append("            .body(patchBody)\n");
        testCase.append("            // Yetkilendirme başlığı olmadan\n");
        testCase.append("        .when()\n");
        testCase.append("            .patch(\"").append(endpoint).append("/\" + resourceId)\n");
        testCase.append("        .then()\n");
        testCase.append("            .statusCode(anyOf(is(401), is(403))); // Unauthorized veya Forbidden beklenir\n");
    }

    /**
     * Genel bir HTTP metodu için test şablonu ekler
     *
     * @param testCase Şablon oluşturucu
     * @param endpoint Endpoint yolu
     * @param httpMethod HTTP metodu
     */
    private void appendGenericTestTemplate(StringBuilder testCase, String endpoint, String httpMethod) {
        testCase.append("        // ").append(httpMethod.toUpperCase()).append(" metodu için temel test\n");
        testCase.append("        Response response = commonRequestSpec()\n");
        testCase.append("        .when()\n");
        testCase.append("            .").append(httpMethod.toLowerCase()).append("(\"").append(endpoint).append("\")\n");
        testCase.append("        .then()\n");
        testCase.append("            .statusCode(200) // Beklenen durum kodu\n");
        testCase.append("            .extract().response();\n\n");

        testCase.append("        // Performans kontrolü\n");
        testCase.append("        assertResponseTime(response, 5000); // 5 saniye zaman aşımı\n\n");

        testCase.append("        // Yetkilendirme testi\n");
        testCase.append("        given()\n");
        testCase.append("            // Yetkilendirme başlığı olmadan\n");
        testCase.append("        .when()\n");
        testCase.append("            .").append(httpMethod.toLowerCase()).append("(\"").append(endpoint).append("\")\n");
        testCase.append("        .then()\n");
        testCase.append("            .statusCode(anyOf(is(401), is(403))); // Unauthorized veya Forbidden beklenir\n");
    }

    /**
     * OperationID'yi CamelCase formatına dönüştürür
     *
     * @param operationId Ham operasyon ID
     * @return Formatlı operasyon ID
     */
    private static String formatOperationId(String operationId) {
        // ID boşsa veya null ise güvenli bir değer döndür
        if (operationId == null || operationId.trim().isEmpty()) {
            return "UnknownOperation";
        }

        // Özel karakterleri ve boşlukları temizle
        String cleaned = operationId.replaceAll("[^a-zA-Z0-9_\\-.]", "");

        // Ayırıcıları kullanarak parçalara böl
        String[] parts = cleaned.split("[_\\-.]");

        if (parts.length == 0) {
            return "UnknownOperation";
        }

        // CamelCase formatına dönüştür
        StringBuilder camelCase = new StringBuilder(parts[0].toLowerCase());

        for (int i = 1; i < parts.length; i++) {
            String part = parts[i];
            if (!part.isEmpty()) {
                camelCase.append(Character.toUpperCase(part.charAt(0)));
                if (part.length() > 1) {
                    camelCase.append(part.substring(1).toLowerCase());
                }
            }
        }

        // İlk harfi büyük yap
        if (camelCase.length() > 0) {
            return Character.toUpperCase(camelCase.charAt(0)) + camelCase.substring(1);
        }

        return "UnknownOperation";
    }

    /**
     * Operasyon düğümünden operasyon ID'sini alır ve geçerli bir Java metod ismine dönüştürür
     *
     * @param operationNode Operasyon düğümü
     * @param httpMethod HTTP metodu
     * @param endpoint Endpoint yolu
     * @return Operasyon ID
     */
    private String getOperationId(JsonNode operationNode, String httpMethod, String endpointPath) {
        if (operationNode.has("operationId")) {
            // Var olan operationId'deki geçersiz karakterleri temizle
            return sanitizeMethodName(operationNode.get("operationId").asText());
        }

        // Önerdiğiniz formatta isim oluştur: ResourceMethodType_Endpoint
        String resource = "";
        if (operationNode.has("tags") && operationNode.get("tags").size() > 0) {
            resource = operationNode.get("tags").get(0).asText();
        } else {
            // Temel kaynağı endpoint'ten çıkar
            String[] pathParts = endpointPath.split("/");
            if (pathParts.length > 1) {
                resource = pathParts[1].replace("{", "").replace("}", "");
            }
        }

        // Tire işaretlerini ve diğer geçersiz karakterleri kaldır
        resource = sanitizeMethodName(resource);

        // Endpoint'i temizleyip formatlı ad haline getir
        String endpointPart = endpointPath.replace("/", "_").replace("{", "").replace("}", "");
        if (endpointPart.startsWith("_")) {
            endpointPart = endpointPart.substring(1);
        }

        // Endpoint kısmındaki tire işaretlerini ve diğer geçersiz karakterleri kaldır
        endpointPart = sanitizeMethodName(endpointPart);

        return resource + httpMethod.substring(0, 1).toUpperCase() + httpMethod.substring(1) + "_" + endpointPart;
    }

    /**
     * Java metod ismi için geçersiz karakterleri temizler
     *
     * @param input Temizlenecek girdi
     * @return Geçerli Java metod ismine dönüştürülmüş string
     */
    private String sanitizeMethodName(String input) {
        if (input == null || input.isEmpty()) {
            return "unknown";
        }

        // Tire işaretlerini alt çizgi ile değiştir
        String result = input.replace('-', '_');

        // Diğer geçersiz karakterleri kaldır (sadece harf, rakam ve alt çizgiye izin ver)
        result = result.replaceAll("[^a-zA-Z0-9_]", "");

        // Eğer ilk karakter rakamsa önüne alt çizgi ekle
        if (!result.isEmpty() && Character.isDigit(result.charAt(0))) {
            result = "_" + result;
        }

        return result;
    }

    /**
     * Endpoint bilgisini tutan sınıf
     */
    private static class EndpointInfo {
        private final String path;
        private final String method;
        private final JsonNode operationNode;

        public EndpointInfo(String path, String method, JsonNode operationNode) {
            this.path = path;
            this.method = method;
            this.operationNode = operationNode;
        }

        public String getPath() {
            return path;
        }

        public String getMethod() {
            return method;
        }

        public JsonNode getOperationNode() {
            return operationNode;
        }
    }

    /**
     * Yapılandırma sınıfı, tüm uygulama ayarlarını içerir
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
        }

        public static Builder builder() {
            return new Builder();
        }

        public String getInputFile() {
            return inputFile;
        }

        public String getOutputFile() {
            return outputFile;
        }

        public String getApiKeyEnvVar() {
            return apiKeyEnvVar;
        }

        public String getModel() {
            return model;
        }

        public int getMaxTokens() {
            return maxTokens;
        }

        public int getMaxRetries() {
            return maxRetries;
        }

        public int getTimeoutSeconds() {
            return timeoutSeconds;
        }

        public int getThreadPoolSize() {
            return threadPoolSize;
        }

        public boolean isVerbose() {
            return verbose;
        }

        public boolean isUseFallbackOnError() {
            return useFallbackOnError;
        }

        public String getTestClassName() {
            return testClassName;
        }

        public String getBaseUri() {
            return baseUri;
        }

        /**
         * Builder sınıfı, yapılandırma oluşturmak için fluent API sunar
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
            private String testClassName = "ApiAutomationTests";
            private String baseUri = "https://your-api-base-url.com";

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

            public Configuration build() {
                return new Configuration(this);
            }
        }
    }
}