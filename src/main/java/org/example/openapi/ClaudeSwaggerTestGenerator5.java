// ===== TAM ÖZELLİKLİ ANA SINIF - CLI ve Tüm Özellikler =====

package org.example.openapi;

import io.github.cdimascio.dotenv.Dotenv;
import org.apache.commons.cli.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Tam Özellikli ClaudeSwaggerTestGenerator
 * Eski kodun TÜM özelliklerini içerir:
 * - Apache Commons CLI desteği
 * - Dotenv (.env) dosyası desteği
 * - Gelişmiş logging
 * - Progress tracking
 * - Dinamik dosya adları
 * - Retry mekanizması
 * - Kapsamlı hata yönetimi
 */
public class ClaudeSwaggerTestGenerator5 {

    private static final Logger logger = Logger.getLogger(ClaudeSwaggerTestGenerator5.class.getName());
    private static final String APP_VERSION = "4.0.0";

    // Varsayılan değerler
    private static final int DEFAULT_MAX_RETRIES = 3;
    private static final int DEFAULT_TIMEOUT_SECONDS = 120;
    private static final int DEFAULT_THREAD_POOL_SIZE = 4;
    private static final String DEFAULT_OUTPUT_FILE = "api_test_cases.java";
    private static final String DEFAULT_MODEL = "gpt-4";
    private static final int DEFAULT_MAX_TOKENS = 4096;

    // Configuration ve servisler
    private final Configuration config;
    private final FileManager fileManager;
    private final SchemaAnalyzer schemaAnalyzer;
    private final TestBuilder testBuilder;
    private final ReportWriter reportWriter;
    private final ProgressTracker progressTracker;

    // Progress tracking
    private final AtomicInteger processedEndpoints = new AtomicInteger(0);
    private final AtomicInteger failedEndpoints = new AtomicInteger(0);
    private int totalEndpoints = 0;

    public ClaudeSwaggerTestGenerator5(Configuration config) {
        this.config = config;
        this.fileManager = new FileManager();
        this.schemaAnalyzer = new SchemaAnalyzer();
        this.testBuilder = new TestBuilder(config);
        this.reportWriter = new ReportWriter(config);
        this.progressTracker = new ProgressTracker();

        if (config.isVerbose()) {
            setupDetailedLogging();
        }
    }

    /**
     * Ana metod - Komut satırı argümanlarını işler
     */
    public static void main(String[] args) {
        setupBasicLogging();

        try {
            CommandLine cmd = parseCommandLineArguments(args);

            if (cmd.hasOption("h")) {
                printHelp();
                return;
            }

            if (cmd.hasOption("v")) {
                System.out.println("Enhanced API Test Generator v" + APP_VERSION);
                return;
            }

            // Configuration oluştur
            Configuration config = buildConfigurationFromCLI(cmd);

            // Uygulamayı çalıştır
            ClaudeSwaggerTestGenerator5 generator = new ClaudeSwaggerTestGenerator5(config);
            generator.run();

        } catch (ParseException e) {
            logger.severe("Komut satırı argümanları hatalı: " + e.getMessage());
            printHelp();
            System.exit(1);
        } catch (Exception e) {
            logger.severe("Uygulama çalıştırılırken hata: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Ana çalıştırma metodu - Progress tracking ile
     */
    public void run() {
        Instant startTime = Instant.now();

        try {
            logger.info("===== Enhanced API Test Generator v" + APP_VERSION + " =====");
            logger.info("İşlem başlatılıyor...");
            logger.info("Giriş dosyası: " + config.getInputFile());
            logger.info("Çıkış dosyası: " + config.getOutputFile());
            logger.info("Thread havuzu boyutu: " + config.getThreadPoolSize());

            // 1. Dosyayı oku
            progressTracker.updateStatus("Dosya okunuyor...");
            String jsonContent = fileManager.readFile(config.getInputFile());

            // 2. Gelişmiş şema analizi
            progressTracker.updateStatus("Gelişmiş şema analizi yapılıyor...");
            List<EndpointInfo> endpoints = schemaAnalyzer.analyzeEndpoints(jsonContent);
            totalEndpoints = endpoints.size();

            logger.info("Toplam " + totalEndpoints + " endpoint bulundu");

            // 3. Progress tracking ile test üretimi
            progressTracker.updateStatus("Testler oluşturuluyor...");
            List<String> testCases = generateTestsWithProgress(endpoints);

            // 4. Dosyaya yazma
            progressTracker.updateStatus("Test dosyası yazılıyor...");
            String actualOutputPath = fileManager.writeTestFile(testCases, config);

            // 5. Rapor oluşturma
            if (config.isGenerateTestReport()) {
                progressTracker.updateStatus("Rapor oluşturuluyor...");
                reportWriter.generateReport(endpoints, testCases);
            }

            // Sonuç özeti
            Duration totalTime = Duration.between(startTime, Instant.now());
            logCompletionSummary(totalTime, actualOutputPath);

        } catch (Exception e) {
            failedEndpoints.incrementAndGet();
            logger.severe("İşlem başarısız: " + e.getMessage());
            throw new RuntimeException("Test üretimi başarısız", e);
        }
    }

    /**
     * Progress tracking ile test üretimi
     */
    private List<String> generateTestsWithProgress(List<EndpointInfo> endpoints) {
        logger.info("Gelişmiş test üretimi başlıyor - " + endpoints.size() + " endpoint");

        List<String> testCases = new ArrayList<>();

        for (int i = 0; i < endpoints.size(); i++) {
            EndpointInfo endpoint = endpoints.get(i);
            try {
                // Progress update
                processedEndpoints.set(i + 1);
                double percentage = (double) (i + 1) / endpoints.size() * 100;
                logger.info(String.format("[%d/%d] (%.1f%%) İşleniyor: %s %s",
                        i + 1, endpoints.size(), percentage, endpoint.getMethod().toUpperCase(), endpoint.getPath()));

                // Test oluştur
                String testCase = testBuilder.createTestForEndpoint(endpoint);
                if (testCase != null) {
                    testCases.add(testCase);
                }

            } catch (Exception e) {
                failedEndpoints.incrementAndGet();
                logger.warning("Hata: " + endpoint.getPath() + " - " + e.getMessage());
            }
        }

        return testCases;
    }

    /**
     * Komut satırı argümanlarını ayrıştırır
     */
    private static CommandLine parseCommandLineArguments(String[] args) throws ParseException {
        Options options = new Options();

        // Temel seçenekler
        options.addOption("h", "help", false, "Yardım bilgisini göster");
        options.addOption("v", "version", false, "Uygulama versiyonunu göster");

        // Dosya seçenekleri
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

        // API seçenekleri
        options.addOption(Option.builder("k")
                .longOpt("api-key-env")
                .hasArg()
                .argName("ENV_VAR")
                .desc("OpenAI API anahtarı çevre değişkeni")
                .build());

        options.addOption(Option.builder("m")
                .longOpt("model")
                .hasArg()
                .argName("MODEL")
                .desc("OpenAI modeli (varsayılan: gpt-4)")
                .build());

        options.addOption(Option.builder("t")
                .longOpt("max-tokens")
                .hasArg()
                .argName("SAYI")
                .desc("Maksimum token sayısı")
                .type(Number.class)
                .build());

        // Performance seçenekleri
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
                .desc("API isteği zaman aşımı")
                .type(Number.class)
                .build());

        options.addOption(Option.builder()
                .longOpt("threads")
                .hasArg()
                .argName("SAYI")
                .desc("Thread havuzu boyutu")
                .type(Number.class)
                .build());

        // Test seçenekleri
        options.addOption(Option.builder("b")
                .longOpt("base-uri")
                .hasArg()
                .argName("URI")
                .desc("API baz URI'si")
                .build());

        // Boolean seçenekler
        options.addOption("V", "verbose", false, "Detaylı günlük çıktısı");
        options.addOption("f", "fallback", false, "Hata durumunda basit testler üret");
        options.addOption("M", "mock", false, "Mock server testleri dahil et");
        options.addOption("P", "performance", false, "Performance testleri dahil et");
        options.addOption("S", "security", false, "Security testleri dahil et");
        options.addOption("R", "report", false, "HTML raporu oluştur");
        options.addOption("C", "contract", false, "Contract testleri dahil et");
        options.addOption("SV", "smart-validation", false, "Akıllı doğrulama aktif");
        options.addOption("B", "boundary", false, "Boundary testleri üret");
        options.addOption("N", "negative", false, "Negative testleri dahil et");
        options.addOption("PA", "pattern-analysis", false, "Response pattern analizi");

        CommandLineParser parser = new DefaultParser();
        return parser.parse(options, args);
    }

    /**
     * CLI'dan Configuration oluşturur
     */
    private static Configuration buildConfigurationFromCLI(CommandLine cmd) {
        // Dotenv yükle
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        // Dinamik dosya yolu oluştur
        String outputFile = createDynamicOutputPath(cmd.getOptionValue("output", DEFAULT_OUTPUT_FILE));

        return Configuration.create()
                .inputFile(cmd.getOptionValue("input", "endpoints.json"))
                .outputFile(outputFile)
                .apiKeyEnvVar(cmd.getOptionValue("api-key-env", "OPENAI_API_KEY"))
                .apiKey(getApiKeyFromEnv(dotenv, cmd.getOptionValue("api-key-env", "OPENAI_API_KEY")))
                .model(cmd.getOptionValue("model", DEFAULT_MODEL))
                .maxTokens(parseIntOption(cmd, "max-tokens", DEFAULT_MAX_TOKENS))
                .maxRetries(parseIntOption(cmd, "retries", DEFAULT_MAX_RETRIES))
                .timeoutSeconds(parseIntOption(cmd, "timeout", DEFAULT_TIMEOUT_SECONDS))
                .threadPoolSize(parseIntOption(cmd, "threads", DEFAULT_THREAD_POOL_SIZE))
                .baseUri(cmd.getOptionValue("base-uri", "https://api.example.com"))
                .testClassName(getClassNameFromPath(outputFile))
                .verbose(cmd.hasOption("verbose"))
                .useFallbackOnError(cmd.hasOption("fallback"))
                .generateMockTests(cmd.hasOption("mock"))
                .includePerformanceTests(cmd.hasOption("performance"))
                .includeSecurityTests(cmd.hasOption("security"))
                .generateTestReport(cmd.hasOption("report"))
                .includeContractTests(cmd.hasOption("contract"))
                .enableSmartValidation(cmd.hasOption("smart-validation"))
                .generateBoundaryTests(cmd.hasOption("boundary"))
                .includeNegativeTests(cmd.hasOption("negative"))
                .enableResponsePatternAnalysis(cmd.hasOption("pattern-analysis"));
    }

    /**
     * Dinamik çıktı dosya yolu oluşturur
     */
    private static String createDynamicOutputPath(String outputParam) {
        String baseDir = "src/test/java/generated";
        File directory = new File(baseDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = dateFormat.format(new Date());

        String className = "GeneratedApiTests";
        if (outputParam != null && !outputParam.isEmpty()) {
            String fileName = new File(outputParam).getName();
            if (fileName.endsWith(".java")) {
                className = fileName.substring(0, fileName.length() - 5);
            } else {
                className = fileName;
            }
        }

        String fileName = className + "_" + timestamp + ".java";
        return baseDir + File.separator + fileName;
    }

    /**
     * Environment'tan API anahtarını alır
     */
    private static String getApiKeyFromEnv(Dotenv dotenv, String envVarName) {
        String apiKey = dotenv.get(envVarName);
        if (apiKey == null || apiKey.trim().isEmpty()) {
            // System environment'tan dene
            apiKey = System.getenv(envVarName);
        }

        if (apiKey == null || apiKey.trim().isEmpty()) {
            logger.warning(envVarName + " çevre değişkeni bulunamadı. OpenAI özellikleri devre dışı olacak.");
            return null;
        }

        return apiKey;
    }

    /**
     * Gelişmiş logging kurulumu
     */
    private static void setupBasicLogging() {
        Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(Level.INFO);

        // Mevcut handler'ları temizle
        for (java.util.logging.Handler handler : rootLogger.getHandlers()) {
            rootLogger.removeHandler(handler);
        }

        // Özel formatter ile console handler ekle
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
     * Detaylı logging kurulumu
     */
    private void setupDetailedLogging() {
        Logger.getLogger("").setLevel(Level.FINE);
        logger.info("Detaylı logging aktif edildi");
    }

    /**
     * Tamamlanma özetini loglar
     */
    private void logCompletionSummary(Duration totalTime, String outputPath) {
        logger.info("===== İşlem Tamamlandı =====");
        logger.info("Toplam süre: " + String.format("%.2f", totalTime.toSeconds()) + " saniye");
        logger.info("İşlenen endpoint: " + processedEndpoints.get() + "/" + totalEndpoints);
        logger.info("Başarılı: " + (processedEndpoints.get() - failedEndpoints.get()));
        logger.info("Başarısız: " + failedEndpoints.get());

        if (totalEndpoints > 0) {
            double successRate = (double)(processedEndpoints.get() - failedEndpoints.get()) / totalEndpoints * 100;
            logger.info("Başarı oranı: " + String.format("%.1f%%", successRate));
        }

        logger.info("Test dosyası: " + outputPath);

        if (config.isGenerateTestReport()) {
            String reportPath = outputPath.replace(".java", "_report.html");
            logger.info("HTML raporu: " + reportPath);
        }
    }

    /**
     * Yardım mesajını gösterir
     */
    private static void printHelp() {
        System.out.println("Enhanced API Test Generator v" + APP_VERSION);
        System.out.println("OpenAPI/Swagger dosyalarından otomatik test üretimi\n");

        System.out.println("Kullanım:");
        System.out.println("  java -jar api-test-generator.jar [SEÇENEKLER]\n");

        System.out.println("Temel Seçenekler:");
        System.out.println("  -h, --help                     Bu yardım mesajını göster");
        System.out.println("  -v, --version                  Versiyon bilgisini göster");
        System.out.println("  -i, --input <DOSYA>            OpenAPI JSON dosyası");
        System.out.println("  -o, --output <DOSYA>           Çıktı Java dosyası");
        System.out.println("  -V, --verbose                  Detaylı log çıktısı\n");

        System.out.println("OpenAI Seçenekleri:");
        System.out.println("  -k, --api-key-env <ENV>        API key çevre değişkeni");
        System.out.println("  -m, --model <MODEL>            OpenAI modeli (varsayılan: gpt-4)");
        System.out.println("  -t, --max-tokens <SAYI>        Max token sayısı");
        System.out.println("  -r, --retries <SAYI>           Max retry sayısı\n");

        System.out.println("Performance Seçenekleri:");
        System.out.println("  --timeout <SANIYE>             API timeout süresi");
        System.out.println("  --threads <SAYI>               Thread havuzu boyutu");
        System.out.println("  -b, --base-uri <URI>           Test API base URI\n");

        System.out.println("Test Türleri:");
        System.out.println("  -f, --fallback                 Fallback testleri aktif");
        System.out.println("  -M, --mock                     Mock testleri dahil et");
        System.out.println("  -P, --performance              Performance testleri");
        System.out.println("  -S, --security                 Security testleri");
        System.out.println("  -C, --contract                 Contract testleri");
        System.out.println("  -B, --boundary                 Boundary testleri");
        System.out.println("  -N, --negative                 Negative testleri");
        System.out.println("  -R, --report                   HTML raporu oluştur\n");

        System.out.println("Gelişmiş Özellikler:");
        System.out.println("  -SV, --smart-validation        Akıllı doğrulama");
        System.out.println("  -PA, --pattern-analysis        Response pattern analizi\n");

        System.out.println("Örnekler:");
        System.out.println("  # Basit kullanım");
        System.out.println("  java -jar generator.jar -i api.json -o tests.java\n");
        System.out.println("  # Tüm özelliklerle");
        System.out.println("  java -jar generator.jar -i api.json -o tests.java \\");
        System.out.println("       -V -P -S -B -N -R --threads 8\n");
        System.out.println("  # OpenAI ile");
        System.out.println("  java -jar generator.jar -i api.json -k OPENAI_API_KEY \\");
        System.out.println("       -m gpt-4 -t 4000 --smart-validation\n");
    }

    // Yardımcı metodlar
    private static int parseIntOption(CommandLine cmd, String option, int defaultValue) {
        try {
            return Integer.parseInt(cmd.getOptionValue(option, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            logger.warning("Geçersiz sayı değeri için " + option + ", varsayılan kullanılıyor: " + defaultValue);
            return defaultValue;
        }
    }

    private static String getClassNameFromPath(String filePath) {
        String fileName = new File(filePath).getName();
        if (fileName.endsWith(".java")) {
            fileName = fileName.substring(0, fileName.length() - 5);
        }
        return fileName;
    }

    // Progress callback interface
    public interface ProgressCallback {
        void onProgress(int current, int total, String currentEndpoint);
        void onError(String endpoint, Exception error);
    }
}

// ===== Progress Tracker Sınıfı =====

/**
 * Progress tracking için yardımcı sınıf
 */
class ProgressTracker {
    private static final Logger logger = Logger.getLogger(ProgressTracker.class.getName());
    private String currentStatus = "";

    public void updateStatus(String status) {
        this.currentStatus = status;
        logger.info("Status: " + status);
    }

    public String getCurrentStatus() {
        return currentStatus;
    }
}