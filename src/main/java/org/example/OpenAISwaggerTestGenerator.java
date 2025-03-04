package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * OpenAI kullanarak Swagger dokümantasyonundan Java + RestAssured test kodları üretir.
 * Bu program, Swagger API dokümantasyonunu alır, OpenAI API'si aracılığıyla Java test kodlarını üretir
 * ve bu kodu bir dosyaya kaydeder.
 */
public class OpenAISwaggerTestGenerator {

    // OpenAI API anahtarını çevresel değişkenden al
    private static final String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY");

    // OpenAI API URL'si
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    // Swagger dokümantasyonunun URL'si (Örnek: GitHub API)
    private static final String SWAGGER_URL = "https://raw.githubusercontent.com/github/rest-api-description/main/descriptions/api.github.com/api.github.com.json";

    public static void main(String[] args) {
        // API anahtarı tanımlı mı kontrol et
        if (OPENAI_API_KEY == null || OPENAI_API_KEY.isEmpty()) {
            System.err.println("❌ ERROR: OpenAI API key is missing! Set the OPENAI_API_KEY environment variable.");
            return;
        }

        try {
            // Swagger JSON dokümantasyonunu indir
            String swaggerJson = downloadSwaggerJson(SWAGGER_URL);
            // OpenAI ile API test kodlarını oluştur
            String generatedCode = generateApiTestsWithOpenAI(swaggerJson);
            // Oluşan kodu dosyaya kaydet
            saveGeneratedCode(generatedCode);
            // Başarı mesajı yazdır
            System.out.println("✅ API test code successfully generated.");
        } catch (Exception e) {
            // Hata oluşursa yazdır
            e.printStackTrace();
        }
    }

    /**
     * Swagger JSON dokümantasyonunu verilen URL'den indirir.
     *
     * @param urlString Swagger JSON dosyasının URL'si
     * @return Swagger JSON dokümantasyonu
     * @throws IOException Eğer JSON dosyasını indirme sırasında bir hata oluşursa
     */
    private static String downloadSwaggerJson(String urlString) throws IOException {
        try {
            return new String(java.net.http.HttpClient.newHttpClient()
                    .send(java.net.http.HttpRequest.newBuilder()
                                    .uri(java.net.URI.create(urlString))
                                    .GET()
                                    .build(),
                            java.net.http.HttpResponse.BodyHandlers.ofString())
                    .body().getBytes(), StandardCharsets.UTF_8);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Swagger JSON download interrupted.", e);
        }
    }

    /**
     * OpenAI API'sini kullanarak Swagger dokümantasyonuna dayalı Java API test kodlarını oluşturur.
     *
     * @param swaggerJson Swagger dokümantasyonu
     * @return OpenAI tarafından üretilen test kodu
     * @throws IOException Eğer OpenAI API ile iletişim sırasında bir hata oluşursa
     */
    private static String generateApiTestsWithOpenAI(String swaggerJson) throws IOException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // HTTP POST isteğini hazırla
            HttpPost request = getHttpPost(swaggerJson);

            // OpenAI API'ye isteği gönder ve yanıtı al
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                // Yanıtın içeriğini oku
                String responseBody = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
                System.out.println("🔍 OpenAI API Response: " + responseBody);

                // JSON yanıtını işle
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonResponse = objectMapper.readTree(responseBody);

                // Yanıtın beklenen formatta olup olmadığını kontrol et
                if (!jsonResponse.has("choices") || jsonResponse.get("choices").isEmpty()) {
                    throw new IOException("❌ ERROR: Unexpected response format from OpenAI API.");
                }

                JsonNode messageNode = jsonResponse.get("choices").get(0).get("message");
                if (messageNode == null || !messageNode.has("content")) {
                    throw new IOException("❌ ERROR: Missing 'content' field in OpenAI response.");
                }

                // Test kodlarını döndür
                return messageNode.get("content").asText();
            }
        }
    }

    /**
     * OpenAI API'ye gönderilecek HTTP POST isteğini oluşturur.
     *
     * @param swaggerJson Swagger dokümantasyonu
     * @return HTTP POST isteği
     * @throws IOException Eğer istek hazırlanırken bir hata oluşursa
     */
    private static HttpPost getHttpPost(String swaggerJson) throws IOException {
        // Yeni bir POST isteği oluştur
        HttpPost request = new HttpPost(OPENAI_API_URL);
        request.setHeader("Authorization", "Bearer " + OPENAI_API_KEY);
        request.setHeader("Content-Type", "application/json");

        // JSON payload'ını hazırla
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> requestPayload = new HashMap<>();

        requestPayload.put("model", "gpt-4o");

        // Kullanıcı mesajını oluştur (Swagger dokümantasyonuna göre test kodu üretmesi için)
        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", "Generate Java RestAssured API tests based on the following Swagger documentation: " + swaggerJson);
        messages.add(userMessage);

        requestPayload.put("messages", messages);
        requestPayload.put("temperature", 0.5);

        // JSON olarak payload'ı hazırla ve isteğe ekle
        String requestBody = objectMapper.writeValueAsString(requestPayload);
        request.setEntity(new StringEntity(requestBody, StandardCharsets.UTF_8));

        return request;
    }

    /**
     * Üretilen API test kodunu dosyaya kaydeder.
     *
     * @param code Üretilen test kodu
     * @throws IOException Eğer dosyaya yazma sırasında bir hata oluşursa
     */
    private static void saveGeneratedCode(String code) throws IOException {
        // Dosya adını dinamik olarak oluştur (timestamp ekleyerek çakışmayı önler)
        String fileName = "GeneratedApiTests_" + System.currentTimeMillis() + ".java";

        // Test kodunu dosyaya yaz
        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(code);
        }

        // Başarı mesajını yazdır
        System.out.println("✅ Test code saved to: " + fileName);
    }
}
