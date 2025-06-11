// ===== Gelişmiş TestBuilder.java - OpenAI Entegrasyonu ile =====

package org.example.openapi;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import io.github.cdimascio.dotenv.Dotenv;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * Gelişmiş TestBuilder - Eski kodun tüm gücüyle!
 * - OpenAI entegrasyonu
 * - Çoklu test stratejileri
 * - Asenkron işleme
 * - Akıllı fallback
 */
public class TestBuilder {

    private static final Logger logger = Logger.getLogger(TestBuilder.class.getName());

    private final Configuration config;
    private final OpenAiService openAiService;
    private final ExecutorService executorService;
    private final TestStrategyManager strategyManager;

    public TestBuilder(Configuration config) {
        this.config = config;
        this.openAiService = initializeOpenAI();
        this.executorService = Executors.newFixedThreadPool(config.getThreadPoolSize());
        this.strategyManager = new TestStrategyManager(config);
    }

    /**
     * Ana test üretim metodu - Asenkron ve akıllı!
     */
    public List<String> buildTests(List<EndpointInfo> endpoints) {
        logger.info("Gelişmiş test üretimi başlıyor - " + endpoints.size() + " endpoint");

        try {
            // Asenkron olarak tüm testleri oluştur
            List<CompletableFuture<String>> futures = new ArrayList<>();

            for (EndpointInfo endpoint : endpoints) {
                CompletableFuture<String> future = CompletableFuture.supplyAsync(() ->
                        createAdvancedTestForEndpoint(endpoint), executorService);
                futures.add(future);
            }

            // Tüm sonuçları bekle
            List<String> testCases = new ArrayList<>();
            for (CompletableFuture<String> future : futures) {
                try {
                    String testCase = future.get(30, TimeUnit.SECONDS);
                    if (testCase != null) {
                        testCases.add(testCase);
                    }
                } catch (Exception e) {
                    logger.warning("Test oluşturulamadı: " + e.getMessage());
                }
            }

            logger.info("Toplam " + testCases.size() + " gelişmiş test oluşturuldu");
            return testCases;

        } finally {
            executorService.shutdown();
        }
    }

    /**
     * Tek endpoint için test oluşturur - Ana sınıftan çağrılır
     */
    public String createTestForEndpoint(EndpointInfo endpoint) {
        return createAdvancedTestForEndpoint(endpoint);
    }

    /**
     * Tek endpoint için gelişmiş test oluşturur
     */
    private String createAdvancedTestForEndpoint(EndpointInfo endpoint) {
        try {
            // 1. Önce OpenAI ile akıllı test oluşturmaya çalış
            String aiGeneratedTest = generateWithOpenAI(endpoint);
            if (aiGeneratedTest != null) {
                return aiGeneratedTest;
            }

            // 2. AI başarısız olursa, stratejileri kullan
            return generateWithStrategies(endpoint);

        } catch (Exception e) {
            logger.warning("Gelişmiş test oluşturulamadı: " + endpoint.getPath() + " - " + e.getMessage());
            // 3. Her şey başarısız olursa basit fallback
            return createSimpleFallback(endpoint);
        }
    }

    /**
     * OpenAI ile akıllı test üretimi
     */
    private String generateWithOpenAI(EndpointInfo endpoint) {
        if (openAiService == null) {
            logger.info("OpenAI servisi mevcut değil, stratejiler kullanılacak");
            return null;
        }

        try {
            String prompt = createIntelligentPrompt(endpoint);

            List<ChatMessage> messages = Arrays.asList(
                    new ChatMessage(ChatMessageRole.SYSTEM.value(), createSystemPrompt()),
                    new ChatMessage(ChatMessageRole.USER.value(), prompt)
            );

            ChatCompletionRequest request = ChatCompletionRequest.builder()
                    .model("gpt-4")
                    .messages(messages)
                    .maxTokens(2000)
                    .temperature(0.1)
                    .build();

            String response = openAiService.createChatCompletion(request)
                    .getChoices().get(0).getMessage().getContent();

            return formatAIResponse(endpoint, response);

        } catch (Exception e) {
            logger.warning("OpenAI test üretimi başarısız: " + e.getMessage());
            return null;
        }
    }

    /**
     * Akıllı prompt oluşturur
     */
    private String createIntelligentPrompt(EndpointInfo endpoint) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("API Endpoint Analizi:\n");
        prompt.append("- Endpoint: ").append(endpoint.getMethod().toUpperCase())
                .append(" ").append(endpoint.getPath()).append("\n");
        prompt.append("- Operasyon: ").append(endpoint.getOperationId()).append("\n");

        if (endpoint.isHasParameters()) {
            prompt.append("- Parametreler: VAR\n");
        }

        if (endpoint.isHasRequestBody()) {
            prompt.append("- Request Body: VAR\n");
        }

        // Endpoint türüne göre özel talimatlar
        if (endpoint.getPath().contains("{")) {
            prompt.append("- Tip: Parametreli endpoint (ID gerekli)\n");
        }

        prompt.append("\nLütfen bu endpoint için kapsamlı REST Assured testleri oluştur:\n");
        prompt.append("1. Happy path (başarılı senaryo)\n");
        prompt.append("2. Negative test (hata senaryoları)\n");
        prompt.append("3. Boundary test (sınır değerler)\n");
        prompt.append("4. Security test (güvenlik kontrolleri)\n");

        return prompt.toString();
    }

    /**
     * Sistem prompt'u
     */
    private String createSystemPrompt() {
        return """
            Sen bir uzman API test geliştiricisisin. REST Assured ve JUnit 5 kullanarak 
            kapsamlı test senaryoları oluştur.
            
            Kurallar:
            1. Sadece metod gövdesi döndür (import yok, sınıf tanımı yok)
            2. Gerçekçi test verileri kullan
            3. Tüm HTTP status kodlarını kontrol et
            4. Response time assertions ekle
            5. Türkçe yorumlar ekle
            6. Comprehensive validation yap
            """;
    }

    /**
     * Strateji tabanlı test üretimi
     */
    private String generateWithStrategies(EndpointInfo endpoint) {
        StringBuilder testCode = new StringBuilder();

        // Test metodu başlangıcı
        testCode.append("    @Test\n");
        testCode.append("    @DisplayName(\"Comprehensive Test: ").append(endpoint.getOperationId()).append("\")\n");
        testCode.append("    public void testComprehensive").append(sanitizeMethodName(endpoint.getOperationId())).append("() {\n");
        testCode.append("        logger.info(\"=== Comprehensive Test: ")
                .append(endpoint.getMethod().toUpperCase()).append(" ").append(endpoint.getPath()).append(" ===\");\n\n");

        // 1. Happy Path Test
        testCode.append("        // 1. HAPPY PATH TEST\n");
        testCode.append(strategyManager.generateHappyPathTest(endpoint));
        testCode.append("\n");

        // 2. Negative Tests
        testCode.append("        // 2. NEGATIVE TESTS\n");
        testCode.append(strategyManager.generateNegativeTests(endpoint));
        testCode.append("\n");

        // 3. Security Tests
        testCode.append("        // 3. SECURITY TESTS\n");
        testCode.append(strategyManager.generateSecurityTests(endpoint));
        testCode.append("\n");

        // 4. Performance Test
        testCode.append("        // 4. PERFORMANCE TEST\n");
        testCode.append(strategyManager.generatePerformanceTest(endpoint));

        testCode.append("        logger.info(\"=== Test Completed: ").append(endpoint.getOperationId()).append(" ===\");\n");
        testCode.append("    }\n");

        return testCode.toString();
    }

    /**
     * AI yanıtını formatlar
     */
    private String formatAIResponse(EndpointInfo endpoint, String response) {
        String cleanResponse = response.replaceAll("```java", "").replaceAll("```", "").trim();

        StringBuilder formatted = new StringBuilder();
        formatted.append("    /**\n");
        formatted.append("     * AI Generated Test: ").append(endpoint.getOperationId()).append("\n");
        formatted.append("     * Endpoint: ").append(endpoint.getMethod().toUpperCase())
                .append(" ").append(endpoint.getPath()).append("\n");
        formatted.append("     */\n");
        formatted.append("    @Test\n");
        formatted.append("    @DisplayName(\"AI Generated: ").append(endpoint.getOperationId()).append("\")\n");
        formatted.append("    public void testAI").append(sanitizeMethodName(endpoint.getOperationId())).append("() {\n");
        formatted.append("        logger.info(\"=== AI Generated Test: ")
                .append(endpoint.getMethod().toUpperCase()).append(" ").append(endpoint.getPath()).append(" ===\");\n\n");

        // AI yanıtını temizle ve ekle
        String[] lines = cleanResponse.split("\n");
        for (String line : lines) {
            formatted.append("        ").append(line).append("\n");
        }

        formatted.append("        logger.info(\"=== AI Test Completed: ").append(endpoint.getOperationId()).append(" ===\");\n");
        formatted.append("    }\n");

        return formatted.toString();
    }

    /**
     * Basit fallback test
     */
    private String createSimpleFallback(EndpointInfo endpoint) {
        return "    @Test\n" +
                "    public void testFallback" + sanitizeMethodName(endpoint.getOperationId()) + "() {\n" +
                "        // Fallback test for " + endpoint.getMethod().toUpperCase() + " " + endpoint.getPath() + "\n" +
                "        logger.info(\"Fallback test executed for: " + endpoint.getPath() + "\");\n" +
                "        // TODO: Implement proper test logic\n" +
                "    }\n";
    }

    /**
     * OpenAI servisini başlatır
     */
    private OpenAiService initializeOpenAI() {
        try {
            Dotenv dotenv = Dotenv.load();
            String apiKey = dotenv.get("OPENAI_API_KEY");

            if (apiKey == null || apiKey.trim().isEmpty()) {
                logger.warning("OpenAI API key bulunamadı, basit strateji kullanılacak");
                return null;
            }

            OpenAiService service = new OpenAiService(apiKey, Duration.ofSeconds(30));
            logger.info("OpenAI servisi başarıyla başlatıldı");
            return service;

        } catch (Exception e) {
            logger.warning("OpenAI servisi başlatılamadı: " + e.getMessage());
            return null;
        }
    }

    /**
     * Metod ismini temizler
     */
    private String sanitizeMethodName(String input) {
        if (input == null || input.isEmpty()) {
            return "Unknown";
        }

        String result = input.replaceAll("[^a-zA-Z0-9_]", "_");
        if (Character.isDigit(result.charAt(0))) {
            result = "_" + result;
        }

        return result;
    }
}

// ===== Test Strateji Yöneticisi =====

/**
 * TestStrategyManager - Farklı test türlerini yönetir
 */
class TestStrategyManager {

    private final Configuration config;

    public TestStrategyManager(Configuration config) {
        this.config = config;
    }

    /**
     * Happy path test oluşturur
     */
    public String generateHappyPathTest(EndpointInfo endpoint) {
        StringBuilder test = new StringBuilder();

        test.append("        Response response = given()\n");
        test.append("            .contentType(ContentType.JSON)\n");
        test.append("            .accept(ContentType.JSON)\n");

        // Parametreler varsa ekle
        if (endpoint.isHasParameters()) {
            test.append("            .queryParam(\"page\", 0)\n");
            test.append("            .queryParam(\"size\", 10)\n");
        }

        // Request body varsa ekle
        if (endpoint.isHasRequestBody()) {
            test.append("            .body(createSmartTestData(\"").append(endpoint.getResourceType()).append("\"))\n");
        }

        test.append("        .when()\n");
        test.append("            .").append(endpoint.getMethod().toLowerCase()).append("(\"").append(endpoint.getPath()).append("\")\n");
        test.append("        .then()\n");
        test.append("            .statusCode(").append(getExpectedSuccessStatus(endpoint.getMethod())).append(")\n");
        test.append("            .time(lessThan(5000L))\n");
        test.append("            .extract().response();\n\n");

        test.append("        logger.info(\"Happy path test passed - Status: \" + response.getStatusCode());\n");

        return test.toString();
    }

    /**
     * Negative testler oluşturur
     */
    public String generateNegativeTests(EndpointInfo endpoint) {
        StringBuilder test = new StringBuilder();

        // Invalid request body test
        if (endpoint.isHasRequestBody()) {
            test.append("        // Invalid request body test\n");
            test.append("        given()\n");
            test.append("            .contentType(ContentType.JSON)\n");
            test.append("            .body(\"{\\\"invalid\\\": \\\"data\\\"}\")\n");
            test.append("        .when()\n");
            test.append("            .").append(endpoint.getMethod().toLowerCase()).append("(\"").append(endpoint.getPath()).append("\")\n");
            test.append("        .then()\n");
            test.append("            .statusCode(anyOf(is(400), is(422)));\n\n");
        }

        // Invalid path test
        test.append("        // Invalid path test\n");
        test.append("        given()\n");
        test.append("        .when()\n");
        test.append("            .").append(endpoint.getMethod().toLowerCase()).append("(\"").append(endpoint.getPath()).append("/invalid\")\n");
        test.append("        .then()\n");
        test.append("            .statusCode(404);\n\n");

        test.append("        logger.info(\"Negative tests completed\");\n");

        return test.toString();
    }

    /**
     * Güvenlik testleri oluşturur
     */
    public String generateSecurityTests(EndpointInfo endpoint) {
        StringBuilder test = new StringBuilder();

        // SQL Injection test
        test.append("        // SQL Injection test\n");
        test.append("        given()\n");
        test.append("            .queryParam(\"id\", \"1' OR '1'='1\")\n");
        test.append("        .when()\n");
        test.append("            .").append(endpoint.getMethod().toLowerCase()).append("(\"").append(endpoint.getPath()).append("\")\n");
        test.append("        .then()\n");
        test.append("            .statusCode(not(200));\n\n");

        // XSS test
        test.append("        // XSS test\n");
        test.append("        given()\n");
        test.append("            .queryParam(\"search\", \"<script>alert('xss')</script>\")\n");
        test.append("        .when()\n");
        test.append("            .").append(endpoint.getMethod().toLowerCase()).append("(\"").append(endpoint.getPath()).append("\")\n");
        test.append("        .then()\n");
        test.append("            .statusCode(anyOf(is(400), is(403)));\n\n");

        test.append("        logger.info(\"Security tests completed\");\n");

        return test.toString();
    }

    /**
     * Performance test oluşturur
     */
    public String generatePerformanceTest(EndpointInfo endpoint) {
        StringBuilder test = new StringBuilder();

        test.append("        // Performance test - Response time should be < 3 seconds\n");
        test.append("        given()\n");
        test.append("        .when()\n");
        test.append("            .").append(endpoint.getMethod().toLowerCase()).append("(\"").append(endpoint.getPath()).append("\")\n");
        test.append("        .then()\n");
        test.append("            .time(lessThan(3000L));\n\n");

        test.append("        logger.info(\"Performance test completed\");\n");

        return test.toString();
    }

    /**
     * HTTP metoduna göre beklenen başarı status kodunu döndürür
     */
    private String getExpectedSuccessStatus(String method) {
        switch (method.toLowerCase()) {
            case "post":
                return "anyOf(is(200), is(201))";
            case "put":
            case "patch":
                return "anyOf(is(200), is(204))";
            case "delete":
                return "anyOf(is(200), is(204), is(404))";
            default:
                return "200";
        }
    }
}