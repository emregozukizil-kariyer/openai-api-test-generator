package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import io.github.cdimascio.dotenv.Dotenv;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class OpenAIEndpointTestGenerator2 {
    private static final String OPENAI_API_KEY = Dotenv.load().get("OPENAI_API_KEY");
    private static final String SWAGGER_JSON_PATH = "src/main/resources/endpoints2.json";  // Buraya Swagger JSON'unun yolunu gir

    public static void main(String[] args) {
        try {
            OpenAIEndpointTestGenerator2 generator = new OpenAIEndpointTestGenerator2();
            generator.generateTestsForEndpoints(SWAGGER_JSON_PATH);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void generateTestsForEndpoints(String swaggerJsonPath) throws IOException {
        // Swagger JSON'unu oku ve parse et
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(new File(swaggerJsonPath));

        JsonNode pathsNode = rootNode.get("paths");
        if (pathsNode == null || !pathsNode.isObject()) {
            throw new IOException("❌ ERROR: 'paths' objesi bulunamadı.");
        }

        OpenAiService service = new OpenAiService(OPENAI_API_KEY);

        // Tüm endpointleri al ve OpenAI'ye ayrı ayrı gönder
        Iterator<Map.Entry<String, JsonNode>> fields = pathsNode.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            String endpoint = entry.getKey();
            JsonNode methodsNode = entry.getValue();

            System.out.println("🔹 İşlenen Endpoint: " + endpoint);

            // OpenAI için prompt hazırla
            String prompt = createPrompt(endpoint, methodsNode);

            // OpenAI'ye gönder ve test kodunu al
            String testCode = generateApiTestsWithOpenAI(service, prompt);

            // Sonucu yazdır
            System.out.println("✅ Generated Tests for " + endpoint + ":\n" + testCode);
            System.out.println("--------------------------------------------------\n");
        }
    }

    private String createPrompt(String endpoint, JsonNode methodsNode) {
        return "Generate API tests using JUnit 5 and RestAssured for the following Swagger API endpoint:\n\n" +
                "Endpoint: " + endpoint + "\n" +
                "Methods and Details: " + methodsNode.toPrettyString() + "\n\n" +
                "Ensure the tests cover status codes, request parameters, and response validation.";
    }

    private String generateApiTestsWithOpenAI(OpenAiService service, String prompt) {
        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model("gpt-4")  // veya "gpt-4o"
                .messages(Collections.singletonList(new ChatMessage("user", prompt)))
                .maxTokens(1024)  // Küçük parçalar halinde yanıt almak için
                .temperature(0.7)
                .build();

        ChatCompletionResult response = service.createChatCompletion(request);
        return response.getChoices().get(0).getMessage().getContent();
    }
}
