package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class OpenAIEndpointTestGenerator {

    private static final String OPENAI_API_KEY = Dotenv.load().get("OPENAI_API_KEY");
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String ENDPOINTS_JSON_FILE = Objects.requireNonNull(OpenAIEndpointTestGenerator.class.getClassLoader().getResource("endpoints2.json")).getPath();

    public static void main(String[] args) {
        if (OPENAI_API_KEY == null || OPENAI_API_KEY.isEmpty()) {
            System.err.println("❌ ERROR: OpenAI API key is missing! Set the OPENAI_API_KEY environment variable.");
            return;
        }

        try {
            List<Endpoint> endpoints = readEndpoints();
            for (Endpoint endpoint : endpoints) {
                for (String method : endpoint.methods) {
                    System.out.println("📡 Processing Endpoint: " + method + " " + endpoint.url);
                    String detailedPrompt = createDetailedPrompt(endpoint.url, method);
                    String generatedTestCode = generateApiTestsWithOpenAI(detailedPrompt);
                    saveGeneratedCode(generatedTestCode, endpoint.url, method);
                }
            }
            System.out.println("✅ All API test cases have been successfully generated.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * JSON dosyasından endpoint listesini okur.
     */
    private static List<Endpoint> readEndpoints() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(new File(ENDPOINTS_JSON_FILE));

        List<Endpoint> endpoints = new ArrayList<>();
        for (JsonNode endpointNode : rootNode.path("endpoints")) {
            String url = endpointNode.path("url").asText();
            List<String> methods = new ArrayList<>();
            for (JsonNode methodNode : endpointNode.path("methods")) {
                methods.add(methodNode.asText());
            }
            endpoints.add(new Endpoint(url, methods));
        }
        return endpoints;
    }

    /**
     * Endpoint ve HTTP metoduna özel detaylı bir test senaryosu prompt'u oluşturur.
     */
    private static String createDetailedPrompt(String url, String method) {
        return "Generate comprehensive API tests for the following endpoint:\n"
                + "- URL: " + url + "\n"
                + "- HTTP Method: " + method + "\n"
                + "- Include different test cases such as:\n"
                + "  - Valid requests with expected responses\n"
                + "  - Invalid requests (e.g., missing required fields, wrong data types, unauthorized access)\n"
                + "  - Boundary and edge case testing\n"
                + "  - Performance considerations (e.g., large payloads)\n"
                + "  - Security aspects (e.g., SQL injection, XSS, authorization bypass)\n"
                + "  - Chained tests (e.g., create -> fetch -> delete)\n"
                + "  - Handle authentication if required.\n";
    }

    /**
     * OpenAI API ile API test kodlarını üretir.
     */
    private static String generateApiTestsWithOpenAI(String prompt) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(OPENAI_API_URL);
            request.setHeader("Authorization", "Bearer " + OPENAI_API_KEY);
            request.setHeader("Content-Type", "application/json");

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> requestPayload = new HashMap<>();
            requestPayload.put("model", "gpt-4o");

            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);
            messages.add(userMessage);

            requestPayload.put("messages", messages);
            requestPayload.put("temperature", 0.5);

            String requestBody = objectMapper.writeValueAsString(requestPayload);
            request.setEntity(new StringEntity(requestBody, StandardCharsets.UTF_8));

            try (var response = httpClient.execute(request)) {
                return objectMapper.readTree(response.getEntity().getContent())
                        .path("choices").get(0).path("message").path("content").asText();
            }
        }
    }

    /**
     * Üretilen API test kodlarını dosyaya kaydeder.
     */
    private static void saveGeneratedCode(String code, String url, String method) throws IOException {
        String fileName = "GeneratedTest_" + method + "_" + url.replaceAll("[^a-zA-Z0-9]", "_") + ".java";
        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(code);
        }
        System.out.println("✅ Test code saved to: " + fileName);
    }

    /**
     * Endpoint bilgisini tutan sınıf.
     */
    private static class Endpoint {
        String url;
        List<String> methods;

        public Endpoint(String url, List<String> methods) {
            this.url = url;
            this.methods = methods;
        }
    }
}
