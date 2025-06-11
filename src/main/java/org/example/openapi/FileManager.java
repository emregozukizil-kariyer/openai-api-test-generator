package org.example.openapi;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 * FileManager - Dosya işlemleri için tek sınıf
 * Hem basit hem de gelişmiş tüm özellikleri içerir
 */
public class FileManager {

    private static final Logger logger = Logger.getLogger(FileManager.class.getName());

    /**
     * Dosya okur - Basit versiyon
     */
    public String readFile(String filePath) throws IOException {
        logger.info("Dosya okunuyor: " + filePath);

        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("Dosya bulunamadı: " + filePath);
        }

        if (!file.canRead()) {
            throw new IOException("Dosya okunamıyor: " + filePath);
        }

        try {
            String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            logger.info("Dosya başarıyla okundu: " + content.length() + " karakter");
            return content;
        } catch (IOException e) {
            throw new IOException("Dosya okuma hatası: " + filePath, e);
        }
    }

    /**
     * Basit test dosyası yazar - Geriye uyumluluk için
     */
    public void writeFile(String outputPath, List<String> testCases) throws IOException {
        logger.info("Basit test dosyası yazılıyor: " + outputPath);

        createDirectoriesIfNeeded(outputPath);

        try (FileWriter writer = new FileWriter(outputPath, StandardCharsets.UTF_8)) {
            writeSimpleTestClass(writer, testCases);
            writer.flush();
        }

        logger.info("Basit test dosyası yazıldı: " + outputPath);
    }

    /**
     * Gelişmiş test dosyası yazar - Tam özellikli
     */
    public String writeTestFile(List<String> testCases, Configuration config) throws IOException {
        String outputPath = config.getOutputFile();
        logger.info("Gelişmiş test dosyası yazılıyor: " + outputPath);

        createDirectoriesIfNeeded(outputPath);

        try (FileWriter writer = new FileWriter(outputPath, StandardCharsets.UTF_8)) {
            // 1. Test sınıfının header'ını yaz
            writeTestClassHeader(writer, config);

            // 2. Gelişmiş utility metodları yaz
            writeAdvancedUtilityMethods(writer, config);

            // 3. Test metodlarını yaz
            writeTestMethods(writer, testCases);

            // 4. Test sınıfının footer'ını yaz
            writeTestClassFooter(writer);

            writer.flush();
        }

        logger.info("Gelişmiş test dosyası yazıldı: " + outputPath + " (" + testCases.size() + " test)");
        return outputPath;
    }

    /**
     * Basit test sınıfı yazar
     */
    private void writeSimpleTestClass(FileWriter writer, List<String> testCases) throws IOException {
        // Basit header
        writer.write("package tests;\n\n");
        writer.write("import io.restassured.RestAssured;\n");
        writer.write("import io.restassured.response.Response;\n");
        writer.write("import org.junit.jupiter.api.Test;\n");
        writer.write("import static io.restassured.RestAssured.*;\n");
        writer.write("import static org.hamcrest.Matchers.*;\n\n");
        writer.write("public class GeneratedApiTests {\n\n");

        // Test metodları
        for (String testCase : testCases) {
            writer.write(testCase);
            writer.write("\n\n");
        }

        // Basit footer
        writer.write("}\n");
    }

    /**
     * Gelişmiş test sınıfının header'ını yazar
     */
    private void writeTestClassHeader(FileWriter writer, Configuration config) throws IOException {
        writer.write("package generated.tests;\n\n");

        // Comprehensive imports
        writer.write("// === REST Assured Imports ===\n");
        writer.write("import io.restassured.RestAssured;\n");
        writer.write("import io.restassured.http.ContentType;\n");
        writer.write("import io.restassured.response.Response;\n");
        writer.write("import io.restassured.specification.RequestSpecification;\n");
        writer.write("import static io.restassured.RestAssured.*;\n");
        writer.write("import static org.hamcrest.Matchers.*;\n\n");

        writer.write("// === JUnit 5 Imports ===\n");
        writer.write("import org.junit.jupiter.api.*;\n");
        writer.write("import org.junit.jupiter.api.parallel.Execution;\n");
        writer.write("import org.junit.jupiter.api.parallel.ExecutionMode;\n");
        writer.write("import static org.junit.jupiter.api.Assertions.*;\n\n");

        writer.write("// === Java Standard Imports ===\n");
        writer.write("import java.util.*;\n");
        writer.write("import java.util.concurrent.TimeUnit;\n");
        writer.write("import java.time.Duration;\n");
        writer.write("import java.time.Instant;\n");
        writer.write("import java.util.logging.Logger;\n");
        writer.write("import java.math.BigDecimal;\n\n");

        // Class documentation
        writer.write("/**\n");
        writer.write(" * Enhanced Generated API Test Suite\n");
        writer.write(" * \n");
        writer.write(" * Auto-generated with ClaudeSwaggerTestGenerator v4.0.0\n");
        writer.write(" * Generation time: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "\n");
        writer.write(" * \n");
        writer.write(" * Configuration:\n");
        writer.write(" * - Base URI: " + config.getBaseUri() + "\n");
        writer.write(" * - Thread Pool: " + config.getThreadPoolSize() + "\n");
        writer.write(" * - Max Retries: " + config.getMaxRetries() + "\n");
        writer.write(" */\n");

        // Class annotations
        writer.write("@TestInstance(TestInstance.Lifecycle.PER_CLASS)\n");
        writer.write("@TestMethodOrder(MethodOrderer.OrderAnnotation.class)\n");
        writer.write("@Execution(ExecutionMode.CONCURRENT)\n");
        writer.write("@DisplayName(\"Enhanced API Test Suite\")\n");
        writer.write("public class " + config.getTestClassName() + " {\n\n");

        // Class-level fields
        writer.write("    private static final Logger logger = Logger.getLogger(" + config.getTestClassName() + ".class.getName());\n");
        writer.write("    private static final String BASE_URI = \"" + config.getBaseUri() + "\";\n");
        writer.write("    private final Map<String, Object> testDataStore = new HashMap<>();\n\n");

        // Setup methods
        writer.write("    @BeforeAll\n");
        writer.write("    void setUp() {\n");
        writer.write("        RestAssured.baseURI = BASE_URI;\n");
        writer.write("        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();\n");
        writer.write("        logger.info(\"=== Enhanced API Test Suite Started ===\");\n");
        writer.write("    }\n\n");

        writer.write("    @AfterAll\n");
        writer.write("    void tearDown() {\n");
        writer.write("        logger.info(\"=== Enhanced API Test Suite Completed ===\");\n");
        writer.write("    }\n\n");
    }

    /**
     * Gelişmiş utility metodları yazar
     */
    private void writeAdvancedUtilityMethods(FileWriter writer, Configuration config) throws IOException {
        writer.write("    // ===== UTILITY METHODS =====\n\n");

        // Common request specification
        writer.write("    protected RequestSpecification createCommonRequestSpec() {\n");
        writer.write("        return given()\n");
        writer.write("            .contentType(ContentType.JSON)\n");
        writer.write("            .accept(ContentType.JSON)\n");
        writer.write("            .header(\"User-Agent\", \"Enhanced-API-Test-Suite/4.0.0\")\n");
        writer.write("            .log().ifValidationFails();\n");
        writer.write("    }\n\n");

        // Smart test data generation
        writer.write("    protected Map<String, Object> createSmartTestData(String resourceType) {\n");
        writer.write("        Map<String, Object> data = new HashMap<>();\n");
        writer.write("        String timestamp = String.valueOf(System.currentTimeMillis());\n");
        writer.write("        \n");
        writer.write("        data.put(\"id\", \"test_\" + timestamp);\n");
        writer.write("        data.put(\"name\", \"Test \" + (resourceType != null ? resourceType : \"Object\"));\n");
        writer.write("        data.put(\"createdAt\", new Date().toString());\n");
        writer.write("        data.put(\"active\", true);\n");
        writer.write("        \n");
        writer.write("        if (resourceType != null) {\n");
        writer.write("            switch (resourceType.toLowerCase()) {\n");
        writer.write("                case \"user\":\n");
        writer.write("                    data.put(\"email\", \"test\" + timestamp + \"@example.com\");\n");
        writer.write("                    data.put(\"username\", \"testuser\" + timestamp);\n");
        writer.write("                    break;\n");
        writer.write("                case \"product\":\n");
        writer.write("                    data.put(\"price\", new BigDecimal(\"99.99\"));\n");
        writer.write("                    data.put(\"category\", \"test-category\");\n");
        writer.write("                    break;\n");
        writer.write("            }\n");
        writer.write("        }\n");
        writer.write("        \n");
        writer.write("        return data;\n");
        writer.write("    }\n\n");

        // Response time assertion
        writer.write("    protected void assertResponseTimeWithLogging(Response response, long maxTimeMs, String operation) {\n");
        writer.write("        long responseTime = response.getTime();\n");
        writer.write("        logger.info(operation + \" response time: \" + responseTime + \"ms\");\n");
        writer.write("        \n");
        writer.write("        assertTrue(responseTime <= maxTimeMs, \n");
        writer.write("            operation + \" response time (\" + responseTime + \"ms) exceeded maximum (\" + maxTimeMs + \"ms)\");\n");
        writer.write("    }\n\n");
    }

    /**
     * Test metodlarını yazar
     */
    private void writeTestMethods(FileWriter writer, List<String> testCases) throws IOException {
        writer.write("    // ===== GENERATED TEST METHODS =====\n\n");

        for (int i = 0; i < testCases.size(); i++) {
            writer.write("    @Order(" + (i + 1) + ")\n");
            writer.write(testCases.get(i));
            writer.write("\n");
        }
    }

    /**
     * Test sınıfının footer'ını yazar
     */
    private void writeTestClassFooter(FileWriter writer) throws IOException {
        writer.write("}\n");
    }

    /**
     * Gerekli dizinleri oluşturur
     */
    private void createDirectoriesIfNeeded(String filePath) throws IOException {
        File file = new File(filePath);
        File parentDir = file.getParentFile();

        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                throw new IOException("Dizin oluşturulamadı: " + parentDir.getAbsolutePath());
            }
            logger.info("Dizin oluşturuldu: " + parentDir.getAbsolutePath());
        }
    }
}