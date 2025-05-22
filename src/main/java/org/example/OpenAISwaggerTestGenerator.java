package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
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
    private static final String OPENAI_API_KEY = Dotenv.load().get("OPENAI_API_KEY");

    // OpenAI API URL'si
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    // Swagger dokümantasyonunun URL'si - UI yerine JSON URL'si kullanın
    private static final String SWAGGER_URL = "https://stage-job-k8s.isinolsun.com/swagger/v1/swagger.json";
    
    // Minimum test kodu satır sayısı
    private static final int MIN_CODE_LINES = 200;
    
    // Minimum test metod sayısı - aradığımız test metod sayısı
    private static final int MIN_TEST_METHODS = 8;

    public static void main(String[] args) {
        // API anahtarı tanımlı mı kontrol et
        if (OPENAI_API_KEY == null || OPENAI_API_KEY.isEmpty()) {
            System.err.println("❌ ERROR: OpenAI API key is missing! Set the OPENAI_API_KEY environment variable.");
            return;
        }

        String swaggerUrl = args.length > 0 ? args[0] : SWAGGER_URL;
        System.out.println("📌 Using Swagger URL: " + swaggerUrl);

        try {
            String swaggerJson = downloadSwaggerJson(swaggerUrl);
            String optimizedPrompt = createDetailedPrompt(swaggerJson);
            
            // İlk denemeyi yap
            String generatedCode = generateApiTestsWithOpenAI(optimizedPrompt);
            
            // Kod kalite kontrolü yap
            if (!isGeneratedCodeQualityAcceptable(generatedCode)) {
                System.out.println("⚠️ İlk üretilen kod kalitesi düşük, daha detaylı bir örnek ile tekrar deneniyor...");
                
                // Örnek test kodu içeren geliştirilmiş prompt oluştur
                String enhancedPrompt = optimizedPrompt + 
                    "\n\nYukarıdaki talimatlar doğrultusunda çalışan tam ve detaylı test kodları üretin. " +
                    "Her endpoint için en az 8 test senaryosu içermeli ve en az 300 satır kod üretmelisiniz. " + 
                    "Önceki yanıtınız yetersiz kaldı, lütfen daha kapsamlı bir çözüm sunun.\n\n" +
                    "Her endpoint için aşağıdaki test örneklerini içeren kapsamlı testle yazın:\n" +
                    generateDetailedTestExamplePrompt();
                
                // Tekrar dene
                generatedCode = generateApiTestsWithOpenAI(enhancedPrompt);
                
                // Hala kalite düşükse, son bir deneme daha yap
                if (!isGeneratedCodeQualityAcceptable(generatedCode)) {
                    System.out.println("⚠️ Üretilen kod hala yetersiz, son bir deneme yapılıyor...");
                    
                    // Son bir deneme - daha fazla zorlayıcı bir prompt
                    String finalPrompt = enhancedPrompt + 
                        "\n\nÜRETTİĞİNİZ KOD YETERSİZ KALDI! Test kod satır sayısı en az 300 ve her endpoint için en az 8 farklı test" +
                        " metodu olmalıdır. Lütfen her test durumu için detaylı açıklamalar ekleyin ve daha karmaşık test senaryoları" +
                        " içeren, profesyonel kalitede kod üretin. SENİ TEST EDİYORUM, BU PROMPT'A CEVAP OLARAK YETERLİ UZUNLUKTA" +
                        " VE KAPSAMDA GERÇEK BİR TEST KODU ÜRETMEZSENİZ, İŞLEVSEL BİR SONUÇ OLARAK KABUL EDİLMEYECEKTİR.";
                        
                    generatedCode = generateApiTestsWithOpenAI(finalPrompt);
                }
            }
            
            saveGeneratedCode(generatedCode);
            System.out.println("✅ API test code successfully generated.");
        } catch (Exception e) {
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
        // ...existing code...
        try {
            return java.net.http.HttpClient.newHttpClient()
                    .send(java.net.http.HttpRequest.newBuilder()
                                    .uri(java.net.URI.create(urlString))
                                    .GET()
                                    .build(),
                            java.net.http.HttpResponse.BodyHandlers.ofString())
                    .body();
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
    private static String createDetailedPrompt(String swaggerJson) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(swaggerJson);

        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("Sen deneyimli bir API test uzmanısın. Verilen Swagger dokümantasyonunu kullanarak kapsamlı ve ayrıntılı Java RestAssured test kodları oluştur.\n\n")
                .append("# TEMEL KURALLAR\n")
                .append("- Her endpoint için en az 6-8 test senaryosu geliştir\n")
                .append("- Test kod satır sayısı endpoint başına en az 150 satır, toplam en az 300 satır olmalı\n")
                .append("- Testler basit değil, kapsamlı, zengin ve ayrıntılı olmalı\n")
                .append("- Her test durumu için farklı test verileriyle çalışan ayrı testler oluştur\n")
                .append("- Her test için açık ve detaylı açıklamalar ekle\n\n")
                
                .append("# TEST SENARYOLARI (her endpoint için bunları üret):\n")
                .append("1. **Başarılı İşlem Testleri**: Tüm zorunlu parametrelerle geçerli istekler\n")
                .append("2. **Olumsuz Durumlar**: Eksik parametreler, geçersiz format, geçersiz değerler\n")
                .append("3. **Yetkilendirme**: JWT token ile kimlik doğrulama, yetkisiz erişim denemeleri, farklı kullanıcı rolleri\n")
                .append("4. **Sınır Değer Analizi**: Minimum/maksimum değerler, uzun metinler, büyük sayılar, özel karakterler\n")
                .append("5. **İş Akışı Testleri**: Birden fazla API'yi zincirleme çağırarak gerçek kullanım senaryolarını simüle etme\n")
                .append("6. **Performans Durumları**: Timeout, yeniden deneme mantığı, eşzamanlı istekler\n")
                .append("7. **Veri Doğrulama**: Yanıt şeması doğrulama, alan validasyonları, veri bütünlüğü kontrolleri\n")
                .append("8. **İdempotent Testler**: Aynı isteğin tekrar edilmesi durumunda davranış\n\n")
                
                .append("# KOD YAPISI\n")
                .append("- JUnit 5 kullan\n")
                .append("- Statik import kullanarak RestAssured kodunu daha okunabilir yap\n")
                .append("- Test verilerini sabit değişkenlerde veya helper metodlarda tanımla\n")
                .append("- POJO sınıfları kullanarak API yanıtlarını seri/deseri hale getir\n")
                .append("- Test öncesi (@BeforeEach) ve sonrası (@AfterEach) için hazırlık ve temizlik kodları ekle\n")
                .append("- Gerektiğinde parametre ile çalışan testler için @ParameterizedTest kullan\n")
                .append("- Okunabilirlik için yardımcı metodlar ekle\n")
                .append("- Hatalar için açıklayıcı assert mesajları kullan\n")
                .append("- Kapsamlı yanıt doğrulamaları için JsonPath/XmlPath kullan\n\n");
                
        // Örnek test kodunu (tüm içeriğini) ekle
        promptBuilder.append(generateDetailedTestExamplePrompt());
        
        promptBuilder.append("# SWAGGER DOKÜMANTASYON ANALİZİ\n");

        // API bilgilerini ekle
        if (rootNode.has("info")) {
            JsonNode infoNode = rootNode.path("info");
            String title = infoNode.path("title").asText("API");
            String version = infoNode.path("version").asText("v1");
            String description = infoNode.path("description").asText("No description");
            
            promptBuilder.append("\n## API Bilgileri\n")
                .append("- Başlık: ").append(title).append("\n")
                .append("- Versiyon: ").append(version).append("\n")
                .append("- Açıklama: ").append(description).append("\n\n");
        }

        // Global parametreleri ekle
        if (rootNode.has("security")) {
            JsonNode securityNode = rootNode.path("security");
            if (securityNode.isArray() && securityNode.size() > 0) {
                promptBuilder.append("## Güvenlik Şemaları\n");
                for (JsonNode securityItem : securityNode) {
                    securityItem.fieldNames().forEachRemaining(name -> 
                        promptBuilder.append("- ").append(name).append("\n")
                    );
                }
                promptBuilder.append("\n");
            }
        }

        JsonNode pathsNode = rootNode.path("paths");
        if (!pathsNode.isObject()) {
            throw new IOException("❌ ERROR: Swagger JSON does not contain a valid 'paths' object. Please check the API documentation.");
        }

        // Tüm endpoint'leri işle ve daha detaylı bilgi topla
        promptBuilder.append("## API Endpointleri\n");
        processEndpointsInDetail(pathsNode, rootNode.path("components"), promptBuilder);

        // Test senaryoları için daha detaylı talimatlara yer ver
        promptBuilder.append("\n# TEST GEREKSİNİMLERİ\n")
                .append("Yukarıda listelenen her API endpoint'i için kapsamlı ve detaylı test senaryoları üret. Test kodu:\n\n")
                .append("1. En az 5-6 test sınıfı ve her sınıfta en az 8-10 test metodu içermeli\n")
                .append("2. Her metot için açıklayıcı isimler ve @DisplayName kullanılmalı\n")
                .append("3. Geçerli kullanım örnekleri ile birlikte hataya dayanıklılık testleri içermeli\n")
                .append("4. API'nin davranışını tam olarak doğrulamak için farklı senaryo ve veri kombinasyonları içermeli\n")
                .append("5. Test verilerini hazırlamak ve yanıtları doğrulamak için yardımcı metodlar içermeli\n\n")
                
                .append("# ÖNEMLİ BİLGİLER\n")
                .append("- TOPLAM TEST SATIR SAYISI EN AZ 300 SATIR OLMALI\n")
                .append("- TÜM TESTLERİ BİR SINIFTA TOPLAMAK YERİNE ENDPOINTLERE GÖRE AYRI SINIFLAR KULLAN\n")
                .append("- EN AZ BİR TANE @ParameterizedTest İÇERMELİ\n")
                .append("- GETTER/SETTER SADELEŞTİRİLMİŞ LOMBOK ANNOTATION'LARI KULLANIN\n")
                .append("- HER SINIF İÇİN HELPER METOTLARINI EKLEYİN\n\n")
                
                .append("# SONUÇ ÇIKTISI\n")
                .append("Tek bir dosyada tüm test sınıflarını içeren ÇALIŞAN VE EKSİKSİZ bir kod oluşturun. Tüm gerekli import ifadelerini, sınıf tanımlarını, yardımcı sınıfları ve metotları içermelidir. Kod yüksek kaliteli ve profesyonel seviyede olmalıdır.\n")
                .append("TAMAMEN ÇALIŞIR DURUMDA VE KAPSAMLI BİR TEST KOD PAKETİ OLUŞTURUN.\n");

        return promptBuilder.toString();
    }
    
    /**
     * Detaylı örnek test kodu promptu oluşturur.
     */
    private static String generateDetailedTestExamplePrompt() {
        return "# ÖRNEK TEST YAPISI (BU YAPIYA BENZER AMA DAHA KAPSAMLI VE FAZLA TEST SENARYOSU İÇEREN KODLAR ÜRET)\n\n"
            + "```java\n"
            + "import io.restassured.http.ContentType;\n"
            + "import io.restassured.response.Response;\n"
            + "import io.restassured.specification.RequestSpecification;\n"
            + "import org.junit.jupiter.api.*;\n"
            + "import org.junit.jupiter.params.ParameterizedTest;\n"
            + "import org.junit.jupiter.params.provider.ValueSource;\n"
            + "import org.junit.jupiter.params.provider.CsvSource;\n"
            + "import org.junit.jupiter.params.provider.MethodSource;\n"
            + "import java.util.*;\n"
            + "import java.time.LocalDateTime;\n"
            + "import java.time.format.DateTimeFormatter;\n"
            + "import java.util.concurrent.TimeUnit;\n"
            + "import java.util.stream.Stream;\n"
            + "\n"
            + "import static io.restassured.RestAssured.*;\n"
            + "import static org.hamcrest.Matchers.*;\n"
            + "import static org.junit.jupiter.api.Assertions.*;\n"
            + "\n"
            + "@TestMethodOrder(MethodOrderer.OrderAnnotation.class)\n"
            + "public class JobsApiTests {\n"
            + "\n"
            + "    private static final String BASE_URL = \"https://api.example.com\";\n"
            + "    private static final String VALID_TOKEN = \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\";\n"
            + "    private static final String INVALID_TOKEN = \"invalid.token.value\";\n"
            + "    private static String createdJobId;\n"
            + "\n"
            + "    @BeforeAll\n"
            + "    public static void setup() {\n"
            + "        baseURI = BASE_URL;\n"
            + "        // Global configurations\n"
            + "        enableLoggingOfRequestAndResponseIfValidationFails();\n"
            + "    }\n"
            + "\n"
            + "    @BeforeEach\n"
            + "    public void beforeEachTest() {\n"
            + "        // Pre-test preparations\n"
            + "        // Reset request specifications, etc.\n"
            + "    }\n"
            + "\n"
            + "    // Helper method for authentication\n"
            + "    private RequestSpecification givenAuthenticatedRequest() {\n"
            + "        return given()\n"
            + "                .header(\"Authorization\", \"Bearer \" + VALID_TOKEN)\n"
            + "                .header(\"Content-Type\", ContentType.JSON)\n"
            + "                .header(\"Accept\", ContentType.JSON);\n"
            + "    }\n"
            + "\n"
            + "    @Test\n"
            + "    @Order(1)\n"
            + "    @DisplayName(\"Get jobs with valid parameters should return 200 and job list\")\n"
            + "    public void testGetJobsWithValidParameters() {\n"
            + "        Response response = givenAuthenticatedRequest()\n"
            + "            .queryParam(\"PageNumber\", 1)\n"
            + "            .queryParam(\"PageSize\", 10)\n"
            + "            .queryParam(\"SortBy\", \"createdAt\")\n"
            + "            .queryParam(\"SortDirection\", \"desc\")\n"
            + "        .when()\n"
            + "            .get(\"/jobs\")\n"
            + "        .then()\n"
            + "            .statusCode(200)\n"
            + "            .contentType(ContentType.JSON)\n"
            + "            .body(\"jobs\", notNullValue())\n"
            + "            .body(\"jobs.size()\", greaterThanOrEqualTo(0))\n"
            + "            .body(\"totalCount\", notNullValue())\n"
            + "            .body(\"pageNumber\", equalTo(1))\n"
            + "            .body(\"pageSize\", equalTo(10))\n"
            + "            .extract().response();\n"
            + "        \n"
            + "        // Additional assertions\n"
            + "        int totalCount = response.path(\"totalCount\");\n"
            + "        List<Map<String, Object>> jobs = response.path(\"jobs\");\n"
            + "        \n"
            + "        // Verify job structure if jobs exist\n"
            + "        if (jobs != null && !jobs.isEmpty()) {\n"
            + "            Map<String, Object> firstJob = jobs.get(0);\n"
            + "            assertTrue(firstJob.containsKey(\"id\"), \"Job should have an id\");\n"
            + "            assertTrue(firstJob.containsKey(\"title\"), \"Job should have a title\");\n"
            + "            assertTrue(firstJob.containsKey(\"description\"), \"Job should have a description\");\n"
            + "            assertTrue(firstJob.containsKey(\"createdAt\"), \"Job should have creation date\");\n"
            + "            \n"
            + "            // Verify sorted order if multiple jobs\n"
            + "            if (jobs.size() > 1) {\n"
            + "                String firstDate = (String) jobs.get(0).get(\"createdAt\");\n"
            + "                String secondDate = (String) jobs.get(1).get(\"createdAt\");\n"
            + "                \n"
            + "                // Parse dates and compare (assuming ISO format)\n"
            + "                DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;\n"
            + "                LocalDateTime firstDateTime = LocalDateTime.parse(firstDate, formatter);\n"
            + "                LocalDateTime secondDateTime = LocalDateTime.parse(secondDate, formatter);\n"
            + "                \n"
            + "                assertTrue(firstDateTime.isAfter(secondDateTime) || firstDateTime.isEqual(secondDateTime),\n"
            + "                    \"Jobs should be sorted by creation date in descending order\");\n"
            + "            }\n"
            + "        }\n"
            + "    }\n"
            + "\n"
            + "    @Test\n"
            + "    @Order(2)\n"
            + "    @DisplayName(\"Get jobs with unauthorized token should return 401\")\n"
            + "    public void testGetJobsWithUnauthorizedToken() {\n"
            + "        given()\n"
            + "            .header(\"Authorization\", \"Bearer \" + INVALID_TOKEN)\n"
            + "            .contentType(ContentType.JSON)\n"
            + "        .when()\n"
            + "            .get(\"/jobs\")\n"
            + "        .then()\n"
            + "            .statusCode(401)\n"
            + "            .body(\"message\", containsString(\"Unauthorized\"));\n"
            + "    }\n"
            + "\n"
            + "    @Test\n"
            + "    @Order(3)\n"
            + "    @DisplayName(\"Get jobs without token should return 401\")\n"
            + "    public void testGetJobsWithoutToken() {\n"
            + "        given()\n"
            + "            .contentType(ContentType.JSON)\n"
            + "        .when()\n"
            + "            .get(\"/jobs\")\n"
            + "        .then()\n"
            + "            .statusCode(401);\n"
            + "    }\n"
            + "\n"
            + "    @ParameterizedTest\n"
            + "    @ValueSource(ints = {0, -1, 1001})\n"
            + "    @Order(4)\n"
            + "    @DisplayName(\"Get jobs with invalid page numbers should handle gracefully\")\n"
            + "    public void testGetJobsWithInvalidPageNumbers(int pageNumber) {\n"
            + "        Response response = givenAuthenticatedRequest()\n"
            + "            .queryParam(\"PageNumber\", pageNumber)\n"
            + "            .queryParam(\"PageSize\", 10)\n"
            + "        .when()\n"
            + "            .get(\"/jobs\")\n"
            + "        .then()\n"
            + "            .extract().response();\n"
            + "            \n"
            + "        // Either returns a valid response with potentially adjusted page number\n"
            + "        // or returns a 400 bad request (implementation dependent)\n"
            + "        if (response.statusCode() == 200) {\n"
            + "            // API auto-corrected the page number\n"
            + "            int returnedPageNumber = response.path(\"pageNumber\");\n"
            + "            assertTrue(returnedPageNumber > 0, \"Returned page number should be positive\");\n"
            + "        } else {\n"
            + "            assertEquals(400, response.statusCode(), \"API should return 400 for invalid page number\");\n"
            + "        }\n"
            + "    }\n"
            + "\n"
            + "    @ParameterizedTest\n"
            + "    @CsvSource({\n"
            + "        \"createdAt,asc,Creation date ascending\",\n"
            + "        \"createdAt,desc,Creation date descending\",\n"
            + "        \"title,asc,Title ascending\",\n"
            + "        \"title,desc,Title descending\"\n"
            + "    })\n"
            + "    @Order(5)\n"
            + "    @DisplayName(\"Get jobs with different sort options should return properly sorted results\")\n"
            + "    public void testGetJobsWithDifferentSortOptions(String sortBy, String sortDirection, String testDescription) {\n"
            + "        Response response = givenAuthenticatedRequest()\n"
            + "            .queryParam(\"PageNumber\", 1)\n"
            + "            .queryParam(\"PageSize\", 20) // Get more results to check sorting\n"
            + "            .queryParam(\"SortBy\", sortBy)\n"
            + "            .queryParam(\"SortDirection\", sortDirection)\n"
            + "        .when()\n"
            + "            .get(\"/jobs\")\n"
            + "        .then()\n"
            + "            .statusCode(200)\n"
            + "            .extract().response();\n"
            + "            \n"
            + "        // Get jobs from response\n"
            + "        List<Map<String, Object>> jobs = response.path(\"jobs\");\n"
            + "        \n"
            + "        // If we have enough jobs to compare, verify the sort order\n"
            + "        if (jobs != null && jobs.size() > 1) {\n"
            + "            verifySorting(jobs, sortBy, sortDirection, testDescription);\n"
            + "        }\n"
            + "    }\n"
            + "\n"
            + "    @Test\n"
            + "    @Order(6)\n"
            + "    @DisplayName(\"Get jobs with filter parameters should return filtered results\")\n"
            + "    public void testGetJobsWithFilterParameters() {\n"
            + "        String searchKeyword = \"engineer\";\n"
            + "        \n"
            + "        Response response = givenAuthenticatedRequest()\n"
            + "            .queryParam(\"PageNumber\", 1)\n"
            + "            .queryParam(\"PageSize\", 10)\n"
            + "            .queryParam(\"SearchTerm\", searchKeyword)\n"
            + "            // Add other filter parameters based on API capabilities\n"
            + "        .when()\n"
            + "            .get(\"/jobs\")\n"
            + "        .then()\n"
            + "            .statusCode(200)\n"
            + "            .extract().response();\n"
            + "            \n"
            + "        // Verify search results contain the search keyword\n"
            + "        List<Map<String, Object>> jobs = response.path(\"jobs\");\n"
            + "        if (jobs != null && !jobs.isEmpty()) {\n"
            + "            for (Map<String, Object> job : jobs) {\n"
            + "                String title = (String) job.get(\"title\");\n"
            + "                String description = (String) job.get(\"description\");\n"
            + "                \n"
            + "                // Check if either title or description contains the search keyword (case insensitive)\n"
            + "                boolean matchesSearch = \n"
            + "                    (title != null && title.toLowerCase().contains(searchKeyword.toLowerCase())) ||\n"
            + "                    (description != null && description.toLowerCase().contains(searchKeyword.toLowerCase()));\n"
            + "                    \n"
            + "                assertTrue(matchesSearch, \n"
            + "                    \"Job with title '\" + title + \"' should match search term '\" + searchKeyword + \"'\");\n"
            + "            }\n"
            + "        }\n"
            + "    }\n"
            + "\n"
            + "    @Test\n"
            + "    @Order(7)\n"
            + "    @DisplayName(\"Get job details by ID should return detailed job information\")\n"
            + "    public void testGetJobDetailsById() {\n"
            + "        // First, get a list of jobs to pick an ID\n"
            + "        Response listResponse = givenAuthenticatedRequest()\n"
            + "            .queryParam(\"PageNumber\", 1)\n"
            + "            .queryParam(\"PageSize\", 1)\n"
            + "        .when()\n"
            + "            .get(\"/jobs\")\n"
            + "        .then()\n"
            + "            .statusCode(200)\n"
            + "            .extract().response();\n"
            + "            \n"
            + "        List<Map<String, Object>> jobs = listResponse.path(\"jobs\");\n"
            + "        \n"
            + "        // Skip test if no jobs available\n"
            + "        Assumptions.assumeTrue(jobs != null && !jobs.isEmpty(), \"Need at least one job for this test\");\n"
            + "        \n"
            + "        String jobId = jobs.get(0).get(\"id\").toString();\n"
            + "        \n"
            + "        // Get job details by ID\n"
            + "        givenAuthenticatedRequest()\n"
            + "            .pathParam(\"jobId\", jobId)\n"
            + "        .when()\n"
            + "            .get(\"/jobs/{jobId}\")\n"
            + "        .then()\n"
            + "            .statusCode(200)\n"
            + "            .body(\"id\", equalTo(jobId))\n"
            + "            .body(\"title\", notNullValue())\n"
            + "            .body(\"description\", notNullValue())\n"
            + "            .body(\"createdAt\", notNullValue());\n"
            + "    }\n"
            + "\n"
            + "    @Test\n"
            + "    @Order(8)\n"
            + "    @DisplayName(\"Create job with valid data should return 201 and job details\")\n"
            + "    public void testCreateJobWithValidData() {\n"
            + "        Map<String, Object> requestBody = new HashMap<>();\n"
            + "        requestBody.put(\"title\", \"Software Engineer\");\n"
            + "        requestBody.put(\"description\", \"Develop and maintain software applications.\");\n"
            + "        requestBody.put(\"location\", \"Remote\");\n"
            + "        requestBody.put(\"salary\", 120000);\n"
            + "        \n"
            + "        Response response = givenAuthenticatedRequest()\n"
            + "            .body(requestBody)\n"
            + "        .when()\n"
            + "            .post(\"/jobs\")\n"
            + "        .then()\n"
            + "            .statusCode(201)\n"
            + "            .body(\"id\", notNullValue())\n"
            + "            .body(\"title\", equalTo(\"Software Engineer\"))\n"
            + "            .body(\"description\", equalTo(\"Develop and maintain software applications.\"))\n"
            + "            .body(\"location\", equalTo(\"Remote\"))\n"
            + "            .body(\"salary\", equalTo(120000))\n"
            + "            .extract().response();\n"
            + "        \n"
            + "        createdJobId = response.path(\"id\");\n"
            + "        assertNotNull(createdJobId, \"Created job ID should not be null\");\n"
            + "    }\n"
            + "\n"
            + "    @Test\n"
            + "    @Order(9)\n"
            + "    @DisplayName(\"Create job with missing required fields should return 400\")\n"
            + "    public void testCreateJobWithMissingRequiredFields() {\n"
            + "        Map<String, Object> requestBody = new HashMap<>();\n"
            + "        requestBody.put(\"title\", \"Software Engineer\");\n"
            + "        // Missing description, location, and salary\n"
            + "        \n"
            + "        givenAuthenticatedRequest()\n"
            + "            .body(requestBody)\n"
            + "        .when()\n"
            + "            .post(\"/jobs\")\n"
            + "        .then()\n"
            + "            .statusCode(400)\n"
            + "            .body(\"message\", containsString(\"Missing required fields\"));\n"
            + "    }\n"
            + "\n"
            + "    @Test\n"
            + "    @Order(10)\n"
            + "    @DisplayName(\"Update job with valid data should return 200 and updated job details\")\n"
            + "    public void testUpdateJobWithValidData() {\n"
            + "        Assumptions.assumeTrue(createdJobId != null, \"Need a created job ID for this test\");\n"
            + "        \n"
            + "        Map<String, Object> requestBody = new HashMap<>();\n"
            + "        requestBody.put(\"title\", \"Senior Software Engineer\");\n"
            + "        requestBody.put(\"description\", \"Lead and develop software applications.\");\n"
            + "        requestBody.put(\"location\", \"Remote\");\n"
            + "        requestBody.put(\"salary\", 140000);\n"
            + "        \n"
            + "        givenAuthenticatedRequest()\n"
            + "            .pathParam(\"jobId\", createdJobId)\n"
            + "            .body(requestBody)\n"
            + "        .when()\n"
            + "            .put(\"/jobs/{jobId}\")\n"
            + "        .then()\n"
            + "            .statusCode(200)\n"
            + "            .body(\"id\", equalTo(createdJobId))\n"
            + "            .body(\"title\", equalTo(\"Senior Software Engineer\"))\n"
            + "            .body(\"description\", equalTo(\"Lead and develop software applications.\"))\n"
            + "            .body(\"location\", equalTo(\"Remote\"))\n"
            + "            .body(\"salary\", equalTo(140000));\n"
            + "    }\n"
            + "\n"
            + "    @Test\n"
            + "    @Order(11)\n"
            + "    @DisplayName(\"Delete job with valid ID should return 204\")\n"
            + "    public void testDeleteJobWithValidId() {\n"
            + "        Assumptions.assumeTrue(createdJobId != null, \"Need a created job ID for this test\");\n"
            + "        \n"
            + "        givenAuthenticatedRequest()\n"
            + "            .pathParam(\"jobId\", createdJobId)\n"
            + "        .when()\n"
            + "            .delete(\"/jobs/{jobId}\")\n"
            + "        .then()\n"
            + "            .statusCode(204);\n"
            + "        \n"
            + "        // Verify job is deleted\n"
            + "        givenAuthenticatedRequest()\n"
            + "            .pathParam(\"jobId\", createdJobId)\n"
            + "        .when()\n"
            + "            .get(\"/jobs/{jobId}\")\n"
            + "        .then()\n"
            + "            .statusCode(404);\n"
            + "    }\n"
            + "\n"
            + "    // Helper method to verify sorting\n"
            + "    private void verifySorting(List<Map<String, Object>> jobs, String sortBy, String sortDirection, String testDescription) {\n"
            + "        for (int i = 0; i < jobs.size() - 1; i++) {\n"
            + "            Map<String, Object> currentJob = jobs.get(i);\n"
            + "            Map<String, Object> nextJob = jobs.get(i + 1);\n"
            + "            \n"
            + "            Comparable currentValue = (Comparable) currentJob.get(sortBy);\n"
            + "            Comparable nextValue = (Comparable) nextJob.get(sortBy);\n"
            + "            \n"
            + "            if (\"asc\".equals(sortDirection)) {\n"
            + "                assertTrue(currentValue.compareTo(nextValue) <= 0, testDescription);\n"
            + "            } else {\n"
            + "                assertTrue(currentValue.compareTo(nextValue) >= 0, testDescription);\n"
            + "            }\n"
            + "        }\n"
            + "    }\n"
            + "}\n"
            + "```\n\n";
    }
    
    /**
     * Swagger dokümantasyonundaki endpoint'leri daha detaylı işleyerek prompt'a ekler.
     *
     * @param pathsNode Swagger dokümantasyonundaki paths nodu
     * @param componentsNode Components nodu (şemalar için)
     * @param promptBuilder Prompt oluşturucu
     */
    private static void processEndpointsInDetail(JsonNode pathsNode, JsonNode componentsNode, StringBuilder promptBuilder) {
        // Endpoint sayısını kontrol et
        int totalEndpoints = 0;
        Iterator<String> pathNames = pathsNode.fieldNames();
        while (pathNames.hasNext()) {
            pathNames.next();
            totalEndpoints++;
        }
        
        // Çok fazla endpoint varsa, sınırlayalım
        int endpointLimit = Math.min(totalEndpoints, 10);
        int currentCount = 0;
        
        // Önce ana kaynakları tanımla (GET/POST/PUT/DELETE)
        Map<String, List<EndpointInfo>> resourceGroups = groupEndpointsByResource(pathsNode);
        
        for (Map.Entry<String, List<EndpointInfo>> entry : resourceGroups.entrySet()) {
            if (currentCount >= endpointLimit) break;
            
            String resource = entry.getKey();
            List<EndpointInfo> endpoints = entry.getValue();
            
            promptBuilder.append("\n### Kaynak: ").append(resource).append("\n");
            
            for (EndpointInfo endpoint : endpoints) {
                promptBuilder.append("\n#### ").append(endpoint.method.toUpperCase()).append(" ").append(endpoint.path).append("\n");
                
                if (!endpoint.summary.isEmpty()) {
                    promptBuilder.append("- **Özet**: ").append(endpoint.summary).append("\n");
                }
                
                if (!endpoint.description.isEmpty()) {
                    promptBuilder.append("- **Açıklama**: ").append(endpoint.description).append("\n");
                }
                
                // Parametreleri işle
                if (endpoint.parameters.size() > 0) {
                    promptBuilder.append("- **Parametreler**:\n");
                    for (ParameterInfo param : endpoint.parameters) {
                        promptBuilder.append("  - `").append(param.name).append("` (")
                                .append(param.type).append(", ").append(param.in).append(")")
                                .append(param.required ? " [Zorunlu]" : " [İsteğe bağlı]");
                                
                        if (!param.description.isEmpty()) {
                            promptBuilder.append(": ").append(param.description);
                        }
                        promptBuilder.append("\n");
                        
                        // Enum değerleri varsa
                        if (param.enumValues != null && !param.enumValues.isEmpty()) {
                            promptBuilder.append("    - Değerler: ").append(String.join(", ", param.enumValues)).append("\n");
                        }
                    }
                }
                
                // Request body
                if (endpoint.hasRequestBody) {
                    promptBuilder.append("- **Request Body**: ").append(endpoint.requestContentType).append("\n");
                    if (!endpoint.requestBodyExample.isEmpty()) {
                        promptBuilder.append("  ```json\n  ").append(endpoint.requestBodyExample).append("\n  ```\n");
                    }
                }
                
                // Yanıt kodları
                if (endpoint.responses.size() > 0) {
                    promptBuilder.append("- **Yanıt Kodları**:\n");
                    for (Map.Entry<String, String> response : endpoint.responses.entrySet()) {
                        promptBuilder.append("  - `").append(response.getKey()).append("`: ")
                                .append(response.getValue()).append("\n");
                    }
                }
                
                // Test önerileri ekle
                promptBuilder.append("- **Test Önerileri**:\n");
                generateTestSuggestions(endpoint, promptBuilder);
            }
            
            currentCount++;
        }
    }
    
    /**
     * Endpoint bilgilerine göre test önerileri oluşturur.
     */
    private static void generateTestSuggestions(EndpointInfo endpoint, StringBuilder promptBuilder) {
        String method = endpoint.method.toUpperCase();
        
        // GET istekleri için
        if ("GET".equals(method)) {
            promptBuilder.append("  - Geçerli parametrelerle başarılı veri çekme\n")
                    .append("  - Sayfalandırma parametreleriyle çalışma (varsa)\n")
                    .append("  - Filtre parametreleriyle veri filtreleme (varsa)\n")
                    .append("  - Geçersiz ID ile 404 yanıtı doğrulama\n")
                    .append("  - Yetkisiz erişim testi (401/403)\n")
                    .append("  - Yanıt verilerinin şema ile uyumluluğunu doğrulama\n");
        } 
        // POST istekleri için
        else if ("POST".equals(method)) {
            promptBuilder.append("  - Geçerli verilerle yeni kayıt oluşturma\n")
                    .append("  - Eksik zorunlu alanlarla 400 yanıtı doğrulama\n")
                    .append("  - Geçersiz formatta verilerle 400 yanıtı doğrulama\n")
                    .append("  - Oluşturulan kaynağı GET ile doğrulama (zincirleme)\n")
                    .append("  - Yetkilendirme kontrolü\n")
                    .append("  - Veri tekrarı kontrolü (aynı veriyle tekrar gönderim)\n");
        } 
        // PUT istekleri için
        else if ("PUT".equals(method)) {
            promptBuilder.append("  - Geçerli güncelleştirme verisiyle kayıt güncelleme\n")
                    .append("  - Eksik zorunlu alanlarla 400 yanıtı doğrulama\n")
                    .append("  - Geçersiz ID ile 404 yanıtı doğrulama\n")
                    .append("  - Güncellenmiş kaynağı GET ile doğrulama (zincirleme)\n")
                    .append("  - Yetkilendirme kontrolü\n")
                    .append("  - Tüm ve kısmi güncelleştirme senaryoları\n");
        } 
        // DELETE istekleri için
        else if ("DELETE".equals(method)) {
            promptBuilder.append("  - Geçerli ID ile kayıt silme\n")
                    .append("  - Geçersiz ID ile 404 yanıtı doğrulama\n")
                    .append("  - Silinen kaynağa GET ile erişim denemesi (404 beklenir)\n")
                    .append("  - Yetkilendirme kontrolü\n")
                    .append("  - Bağlantılı kaynaklara etkisini doğrulama\n");
        } 
        // Diğer HTTP metodları için
        else {
            promptBuilder.append("  - Temel işlevsellik testi\n")
                    .append("  - Geçersiz parametrelerle negatif test\n")
                    .append("  - Yetkilendirme kontrolü\n")
                    .append("  - Yanıt formatı doğrulama\n");
        }
    }
    
    /**
     * Endpoint'leri kaynaklarına göre gruplar.
     */
    private static Map<String, List<EndpointInfo>> groupEndpointsByResource(JsonNode pathsNode) {
        Map<String, List<EndpointInfo>> resourceGroups = new HashMap<>();
        
        pathsNode.fields().forEachRemaining(entry -> {
            String path = entry.getKey();
            JsonNode methodsNode = entry.getValue();
            
            // Kaynağı belirle (ilk / ve ikinci / arasındaki metin)
            String resource = extractResourceName(path);
            
            methodsNode.fields().forEachRemaining(methodEntry -> {
                String httpMethod = methodEntry.getKey();
                JsonNode methodDetails = methodEntry.getValue();
                
                EndpointInfo info = new EndpointInfo();
                info.path = path;
                info.method = httpMethod;
                info.summary = methodDetails.path("summary").asText("");
                info.description = methodDetails.path("description").asText("");
                
                // Parametreleri ekle
                JsonNode parameters = methodDetails.path("parameters");
                if (parameters.isArray()) {
                    for (JsonNode param : parameters) {
                        ParameterInfo paramInfo = new ParameterInfo();
                        paramInfo.name = param.path("name").asText("");
                        paramInfo.in = param.path("in").asText("query");
                        paramInfo.required = param.path("required").asBoolean(false);
                        paramInfo.description = param.path("description").asText("");
                        
                        JsonNode schema = param.path("schema");
                        paramInfo.type = schema.path("type").asText("string");
                        
                        // Enum değerleri
                        JsonNode enumValues = schema.path("enum");
                        if (enumValues.isArray() && enumValues.size() > 0) {
                            List<String> values = new ArrayList<>();
                            for (JsonNode enumVal : enumValues) {
                                values.add(enumVal.asText());
                            }
                            paramInfo.enumValues = values;
                        }
                        
                        info.parameters.add(paramInfo);
                    }
                }
                
                // Request body kontrolü
                JsonNode requestBody = methodDetails.path("requestBody");
                if (!requestBody.isMissingNode()) {
                    info.hasRequestBody = true;
                    
                    JsonNode content = requestBody.path("content");
                    if (content.has("application/json")) {
                        info.requestContentType = "application/json";
                        
                        // Şema ve örnek değerler için detayları çek
                        JsonNode schema = content.path("application/json").path("schema");
                        info.requestBodyExample = extractExampleFromSchema(schema);
                    }
                }
                
                // Yanıtları ekle
                JsonNode responses = methodDetails.path("responses");
                if (responses.isObject()) {
                    responses.fields().forEachRemaining(responseEntry -> {
                        String statusCode = responseEntry.getKey();
                        String description = responseEntry.getValue().path("description").asText("No description");
                        info.responses.put(statusCode, description);
                    });
                }
                
                // Grubu güncelle
                resourceGroups.computeIfAbsent(resource, k -> new ArrayList<>()).add(info);
            });
        });
        
        return resourceGroups;
    }
    
    /**
     * Path değerinden kaynak adını çıkarır.
     */
    private static String extractResourceName(String path) {
        // "/users/{id}/posts" -> "users"
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        
        int nextSlash = path.indexOf('/');
        if (nextSlash > 0) {
            return path.substring(0, nextSlash);
        }
        
        return path;
    }
    
    /**
     * Şemadan örnek JSON çıkarır.
     */
    private static String extractExampleFromSchema(JsonNode schema) {
        // Basit bir örnek oluşturma
        if (schema.path("type").asText("").equals("object")) {
            Map<String, Object> exampleObj = new HashMap<>();
            JsonNode properties = schema.path("properties");
            
            if (properties.isObject()) {
                properties.fields().forEachRemaining(prop -> {
                    String propName = prop.getKey();
                    JsonNode propDetails = prop.getValue();
                    String type = propDetails.path("type").asText("string");
                    
                    // Türe göre örnek değer oluştur
                    if ("string".equals(type)) {
                        String format = propDetails.path("format").asText("");
                        if ("date-time".equals(format)) {
                            exampleObj.put(propName, "2023-01-15T14:30:00Z");
                        } else if ("email".equals(format)) {
                            exampleObj.put(propName, "user@example.com");
                        } else {
                            exampleObj.put(propName, "example_" + propName);
                        }
                    } else if ("integer".equals(type) || "number".equals(type)) {
                        exampleObj.put(propName, 123);
                    } else if ("boolean".equals(type)) {
                        exampleObj.put(propName, true);
                    } else if ("array".equals(type)) {
                        exampleObj.put(propName, List.of("item1", "item2"));
                    } else {
                        exampleObj.put(propName, "example_value");
                    }
                });
            }
            
            try {
                return new ObjectMapper().writeValueAsString(exampleObj);
            } catch (Exception e) {
                return "{}";
            }
        }
        
        return "{}";
    }

    /**
     * OpenAI API'si aracılığıyla API test kodları üretir.
     *
     * @param prompt OpenAI'ye gönderilecek istek metni
     * @return OpenAI tarafından üretilen test kodu
     * @throws IOException Eğer API ile iletişim sırasında bir hata oluşursa
     */
    private static String generateApiTestsWithOpenAI(String prompt) throws IOException {
        // OpenAI API için JSON istek gövdesi oluştur
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4-turbo"); // OpenAI model adı
        requestBody.put("temperature", 0.2); // Daha düşük sıcaklık değeri daha belirleyici yanıtlar verir
        requestBody.put("max_tokens", 8000); // Daha uzun yanıtlar için token limitini artır
        requestBody.put("top_p", 0.95); // Biraz daha belirleyici
        requestBody.put("frequency_penalty", 0.0); // Tekrarları cezalandırma
        requestBody.put("presence_penalty", 0.0); // Yeni kavramlar eklemek için cezalandırma
        
        // Mesaj formatında istek
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", "Sen üst düzey bir Java test uzmanısın. Restassured ve JUnit kullanarak API testleri yazıyorsun. " +
                         "Kesinlikle açıklama yapmadan veya mazeret sunmadan SADECE istenen Java test kodunu üretmelisin. " +
                         "Kod son derece kapsamlı ve ayrıntılı olmalı, istenen tüm senaryoları test etmeli ve en az 300 satır uzunluğunda " +
                         "ve 10'dan fazla test metodu içermeli. Bu akademik bir çalışma ve gerçek bir proje için değil.");
        
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", "SADECE KOD ÜRETMENİ İSTİYORUM, HİÇBİR AÇIKLAMA YAPMA. SWAGGER DOKÜMANTASYONUNDAKİ HER ENDPOINT İÇİN " +
                       "TESTLERİ İÇEREN TEK BİR JAVA DOSYASI OLUŞTUR: \n\n" + prompt);
        
        requestBody.put("messages", Arrays.asList(systemMessage, userMessage));
        
        // HTTP istek gövdesini JSON'a dönüştür
        ObjectMapper mapper = new ObjectMapper();
        String jsonBody = mapper.writeValueAsString(requestBody);
        
        // OpenAI API'ye HTTP POST isteği gönder
        HttpPost request = new HttpPost(OPENAI_API_URL);
        request.setHeader("Authorization", "Bearer " + OPENAI_API_KEY);
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity(jsonBody, StandardCharsets.UTF_8));
        
        System.out.println("📤 Sending request to OpenAI API...");
        
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                // Yanıtı işle
                String responseBody = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
                
                // JSON yanıtını ayrıştır
                JsonNode responseJson = mapper.readTree(responseBody);
                
                if (response.getCode() != 200) {
                    String errorMessage = responseJson.path("error").path("message").asText("Unknown error");
                    throw new IOException("OpenAI API error: " + errorMessage);
                }
                
                // Yanıtın içeriğini çıkar
                JsonNode choices = responseJson.path("choices");
                if (choices.isArray() && choices.size() > 0) {
                    String generatedCode = choices.get(0).path("message").path("content").asText("");
                    System.out.println("📥 Received response from OpenAI API.");
                    
                    // Kod bloğunu çıkar
                    return extractCodeFromMarkdown(generatedCode);
                } else {
                    throw new IOException("OpenAI API returned no content.");
                }
            }
        }
    }
    
    /**
     * Markdown formatındaki metinden kod bloğunu çıkarır.
     * Geliştirilmiş versiyon - daha karmaşık kod blokları için çalışır.
     */
    private static String extractCodeFromMarkdown(String markdown) {
        if (markdown == null || markdown.trim().isEmpty()) {
            return "";
        }

        // Eğer yanıt zaten kod bloğu içermiyor gibiyse, olduğu gibi döndür
        if (!markdown.contains("```java") && !markdown.contains("```") && markdown.contains("public class")) {
            return markdown;
        }
        
        StringBuilder codeBuilder = new StringBuilder();
        boolean inCodeBlock = false;
        int codeBlockCount = 0;
        
        for (String line : markdown.split("\n")) {
            if (line.trim().startsWith("```java") || line.trim().startsWith("``` java") || 
                (line.trim().startsWith("```") && !inCodeBlock)) {
                inCodeBlock = true;
                codeBlockCount++;
                continue;
            } else if (line.trim().startsWith("```") && inCodeBlock) {
                inCodeBlock = false;
                continue;
            }
            
            if (inCodeBlock) {
                codeBuilder.append(line).append("\n");
            }
        }
        
        // Kod bloğu yoksa veya çok az kod çıkarıldıysa, orijinal metni döndür
        if (codeBuilder.length() < 100 && markdown.contains("public class")) {
            return markdown;
        }
        
        return codeBuilder.toString();
    }

    /**
     * Üretilen kodun kalitesini kontrol eder.
     */
    private static boolean isGeneratedCodeQualityAcceptable(String generatedCode) {
        if (generatedCode == null || generatedCode.trim().isEmpty()) {
            return false;
        }
        
        // Kod satır sayısı kontrolü
        String[] lines = generatedCode.split("\n");
        int nonEmptyLines = 0;
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                nonEmptyLines++;
            }
        }
        
        if (nonEmptyLines < MIN_CODE_LINES) {
            System.out.println("⚠️ Generated code has only " + nonEmptyLines + 
                " lines, which is less than minimum " + MIN_CODE_LINES + " lines.");
            return false;
        }
        
        // Test metod sayısı kontrolü - @Test veya @ParameterizedTest anotasyonlarını say
        int testMethodCount = 0;
        for (String line : lines) {
            if (line.contains("@Test") || line.contains("@ParameterizedTest")) {
                testMethodCount++;
            }
        }
        
        if (testMethodCount < MIN_TEST_METHODS) {
            System.out.println("⚠️ Generated code has only " + testMethodCount + 
                " test methods, which is less than minimum " + MIN_TEST_METHODS + " methods.");
            return false;
        }
        
        return true;
    }
    
    /**
     * Üretilen kodu bir dosyaya kaydeder.
     */
    private static void saveGeneratedCode(String code) throws IOException {
        String fileName = "ApiTests_" + System.currentTimeMillis() + ".java";
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(code);
        }
        System.out.println("💾 Generated code saved to: " + fileName);
    }
    
    /**
     * Endpoint bilgilerini saklayan iç sınıf.
     */
    private static class EndpointInfo {
        String path;
        String method;
        String summary = "";
        String description = "";
        List<ParameterInfo> parameters = new ArrayList<>();
        boolean hasRequestBody = false;
        String requestContentType = "";
        String requestBodyExample = "";
        Map<String, String> responses = new HashMap<>();
    }
    
    /**
     * Parametre bilgilerini saklayan iç sınıf.
     */
    private static class ParameterInfo {
        String name;
        String in;
        boolean required;
        String description = "";
        String type = "string";
        List<String> enumValues = null;
    }
}