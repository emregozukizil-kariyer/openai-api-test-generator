// ===== Gelişmiş SchemaAnalyzer.java - Detaylı Analiz =====

package org.example.openapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Gelişmiş SchemaAnalyzer - Eski kodun tüm analiz gücüyle!
 * - Detaylı şema analizi
 * - Parameter constraint analizi
 * - Response pattern analizi
 * - Dependency analizi
 * - Complexity hesaplama
 */
public class SchemaAnalyzer {

    private static final Logger logger = Logger.getLogger(SchemaAnalyzer.class.getName());
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Gelişmiş cache yapıları
    private final Map<String, JsonNode> schemaCache = new ConcurrentHashMap<>();
    private final Map<String, DataConstraints> constraintCache = new ConcurrentHashMap<>();
    private final Map<String, List<String>> responsePatterns = new ConcurrentHashMap<>();
    private final Map<String, EndpointComplexity> complexityCache = new ConcurrentHashMap<>();

    /**
     * Gelişmiş endpoint analizi - Eski kodun tüm gücüyle!
     */
    public List<EndpointInfo> analyzeEndpoints(String jsonContent) throws Exception {
        logger.info("Gelişmiş endpoint analizi başlıyor...");

        JsonNode rootNode = objectMapper.readTree(jsonContent);

        // 1. Önce şemaları analiz et
        performAdvancedSchemaAnalysis(rootNode);

        // 2. Endpoint'leri topla ve detaylı analiz et
        List<EndpointInfo> endpoints = collectAndAnalyzeEndpoints(rootNode);

        // 3. Bağımlılık analizi yap
        performDependencyAnalysis(endpoints);

        // 4. Complexity analizi yap
        performComplexityAnalysis(endpoints);

        // 5. Endpoint'leri önceliğe göre sırala
        endpoints = sortEndpointsByPriority(endpoints);

        logger.info("Gelişmiş analiz tamamlandı - " + endpoints.size() + " endpoint, " +
                schemaCache.size() + " şema analiz edildi");

        return endpoints;
    }

    /**
     * Gelişmiş şema analizi - Eski koddan
     */
    private void performAdvancedSchemaAnalysis(JsonNode rootNode) {
        logger.info("Gelişmiş şema analizi başlatılıyor...");

        if (!hasSchemas(rootNode)) {
            logger.warning("OpenAPI dokümanında şema bulunamadı");
            return;
        }

        JsonNode schemas = rootNode.get("components").get("schemas");
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

            // Response pattern analizi
            List<String> patterns = analyzeResponsePatterns(schema, schemaName);
            responsePatterns.put(schemaName, patterns);
        }

        logger.info(schemaCache.size() + " şema detaylı analiz edildi");
    }

    /**
     * Endpoint'leri toplar ve detaylı analiz eder
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
                    EndpointInfo endpointInfo = createDetailedEndpointInfo(endpointPath, httpMethod, operationNode);
                    endpoints.add(endpointInfo);
                }
            }
        }

        return endpoints;
    }

    /**
     * Detaylı endpoint bilgisi oluşturur
     */
    private EndpointInfo createDetailedEndpointInfo(String path, String method, JsonNode operationNode) {
        EndpointInfo endpoint = new EndpointInfo();
        endpoint.setPath(path);
        endpoint.setMethod(method);

        // Operasyon ID
        if (operationNode.has("operationId")) {
            endpoint.setOperationId(operationNode.get("operationId").asText());
        } else {
            endpoint.setOperationId(generateOperationId(path, method));
        }

        // Summary ve description
        if (operationNode.has("summary")) {
            endpoint.setSummary(operationNode.get("summary").asText());
        }
        if (operationNode.has("description")) {
            endpoint.setDescription(operationNode.get("description").asText());
        }

        // Tags - Resource type belirleme
        if (operationNode.has("tags")) {
            JsonNode tags = operationNode.get("tags");
            if (tags.size() > 0) {
                endpoint.setResourceType(tags.get(0).asText());
            }
        }

        // Security requirements
        analyzeSecurityRequirements(endpoint, operationNode);

        // Parameters analizi
        analyzeParameters(endpoint, operationNode);

        // Request body analizi
        analyzeRequestBody(endpoint, operationNode);

        // Response analizi
        analyzeResponses(endpoint, operationNode);

        return endpoint;
    }

    /**
     * Güvenlik gereksinimlerini analiz eder
     */
    private void analyzeSecurityRequirements(EndpointInfo endpoint, JsonNode operationNode) {
        if (operationNode.has("security")) {
            endpoint.setRequiresAuthentication(true);
            JsonNode security = operationNode.get("security");

            List<String> securitySchemes = new ArrayList<>();
            for (JsonNode securityReq : security) {
                Iterator<Map.Entry<String, JsonNode>> secFields = securityReq.fields();
                while (secFields.hasNext()) {
                    Map.Entry<String, JsonNode> secField = secFields.next();
                    securitySchemes.add(secField.getKey());
                }
            }
            endpoint.setSecuritySchemes(securitySchemes);
        }
    }

    /**
     * Parametreleri detaylı analiz eder
     */
    private void analyzeParameters(EndpointInfo endpoint, JsonNode operationNode) {
        if (!operationNode.has("parameters")) {
            return;
        }

        JsonNode parameters = operationNode.get("parameters");
        List<ParameterInfo> paramList = new ArrayList<>();
        List<String> requiredParams = new ArrayList<>();

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
                if (paramInfo.isRequired()) {
                    requiredParams.add(paramInfo.getName());
                }
            }
            if (param.has("description")) {
                paramInfo.setDescription(param.get("description").asText());
            }

            // Schema analizi
            if (param.has("schema")) {
                JsonNode schema = param.get("schema");
                DataConstraints constraints = analyzeDataConstraints(schema, paramInfo.getName());
                paramInfo.setConstraints(constraints);
            }

            paramList.add(paramInfo);
        }

        endpoint.setParameters(paramList);
        endpoint.setRequiredParameters(requiredParams);
        endpoint.setHasParameters(!paramList.isEmpty());
    }

    /**
     * Request body'yi detaylı analiz eder
     */
    private void analyzeRequestBody(EndpointInfo endpoint, JsonNode operationNode) {
        if (!operationNode.has("requestBody")) {
            return;
        }

        JsonNode requestBody = operationNode.get("requestBody");
        RequestBodyInfo bodyInfo = new RequestBodyInfo();

        if (requestBody.has("required")) {
            bodyInfo.setRequired(requestBody.get("required").asBoolean());
        }

        if (requestBody.has("description")) {
            bodyInfo.setDescription(requestBody.get("description").asText());
        }

        // Content analizi
        if (requestBody.has("content")) {
            JsonNode content = requestBody.get("content");
            if (content.has("application/json")) {
                JsonNode jsonContent = content.get("application/json");
                if (jsonContent.has("schema")) {
                    JsonNode schema = jsonContent.get("schema");
                    DataConstraints constraints = analyzeDataConstraints(schema, "requestBody");
                    bodyInfo.setConstraints(constraints);

                    // Akıllı örnek veri üret
                    String exampleData = generateSmartExampleData(schema, endpoint.getResourceType());
                    bodyInfo.setExampleData(exampleData);
                }
            }
        }

        endpoint.setRequestBodyInfo(bodyInfo);
        endpoint.setHasRequestBody(true);
    }

    /**
     * Response'ları detaylı analiz eder
     */
    private void analyzeResponses(EndpointInfo endpoint, JsonNode operationNode) {
        if (!operationNode.has("responses")) {
            return;
        }

        JsonNode responses = operationNode.get("responses");
        List<ResponseInfo> responseList = new ArrayList<>();
        List<String> statusCodes = new ArrayList<>();

        Iterator<Map.Entry<String, JsonNode>> responseIterator = responses.fields();

        while (responseIterator.hasNext()) {
            Map.Entry<String, JsonNode> responseEntry = responseIterator.next();
            String statusCode = responseEntry.getKey();
            JsonNode responseDetail = responseEntry.getValue();

            ResponseInfo responseInfo = new ResponseInfo();
            responseInfo.setStatusCode(statusCode);
            statusCodes.add(statusCode);

            if (responseDetail.has("description")) {
                responseInfo.setDescription(responseDetail.get("description").asText());
            }

            // Response content analizi
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

            responseList.add(responseInfo);
        }

        endpoint.setResponses(responseList);
        endpoint.setExpectedStatusCodes(statusCodes);
    }

    /**
     * Veri kısıtlarını detaylı analiz eder
     */
    private DataConstraints analyzeDataConstraints(JsonNode schema, String schemaName) {
        if (schema == null) {
            return new DataConstraints();
        }

        // Cache kontrolü
        DataConstraints cached = constraintCache.get(schemaName);
        if (cached != null) {
            return cached;
        }

        DataConstraints constraints = new DataConstraints();

        // Tip analizi
        if (schema.has("type")) {
            constraints.setType(schema.get("type").asText());
        }

        // String kısıtları
        if ("string".equals(constraints.getType())) {
            analyzeStringConstraints(schema, constraints);
        }

        // Numeric kısıtları
        if ("integer".equals(constraints.getType()) || "number".equals(constraints.getType())) {
            analyzeNumericConstraints(schema, constraints);
        }

        // Array kısıtları
        if ("array".equals(constraints.getType())) {
            analyzeArrayConstraints(schema, constraints);
        }

        // Object kısıtları
        if ("object".equals(constraints.getType())) {
            analyzeObjectConstraints(schema, constraints);
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
     * String kısıtlarını analiz eder
     */
    private void analyzeStringConstraints(JsonNode schema, DataConstraints constraints) {
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

    /**
     * Numeric kısıtlarını analiz eder
     */
    private void analyzeNumericConstraints(JsonNode schema, DataConstraints constraints) {
        if (schema.has("minimum")) {
            constraints.setMinimum(schema.get("minimum").decimalValue());
        }
        if (schema.has("maximum")) {
            constraints.setMaximum(schema.get("maximum").decimalValue());
        }
        if (schema.has("exclusiveMinimum")) {
            constraints.setExclusiveMinimum(schema.get("exclusiveMinimum").asBoolean());
        }
        if (schema.has("exclusiveMaximum")) {
            constraints.setExclusiveMaximum(schema.get("exclusiveMaximum").asBoolean());
        }
        if (schema.has("multipleOf")) {
            constraints.setMultipleOf(schema.get("multipleOf").decimalValue());
        }
    }

    /**
     * Array kısıtlarını analiz eder
     */
    private void analyzeArrayConstraints(JsonNode schema, DataConstraints constraints) {
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

    /**
     * Object kısıtlarını analiz eder
     */
    private void analyzeObjectConstraints(JsonNode schema, DataConstraints constraints) {
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

    /**
     * Response pattern'larını analiz eder
     */
    private List<String> analyzeResponsePatterns(JsonNode schema, String schemaName) {
        List<String> patterns = new ArrayList<>();

        if (schema == null) {
            return patterns;
        }

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

        return patterns;
    }

    /**
     * Bağımlılık analizi yapar
     */
    private void performDependencyAnalysis(List<EndpointInfo> endpoints) {
        logger.info("Bağımlılık analizi yapılıyor...");

        Map<String, List<EndpointInfo>> resourceGroups = groupEndpointsByResource(endpoints);

        for (EndpointInfo endpoint : endpoints) {
            List<String> dependencies = new ArrayList<>();

            // Temel bağımlılık kuralları
            switch (endpoint.getMethod().toLowerCase()) {
                case "put":
                case "patch":
                case "delete":
                    // Bu metodlar için POST bağımlılığı ekle
                    String createEndpoint = findCreateEndpoint(resourceGroups, endpoint.getResourceType());
                    if (createEndpoint != null) {
                        dependencies.add(createEndpoint);
                    }
                    break;
            }

            // Path parameter bağımlılıkları
            if (endpoint.getPath().contains("{") && !endpoint.getMethod().equalsIgnoreCase("post")) {
                String listEndpoint = findListEndpoint(resourceGroups, endpoint.getResourceType());
                if (listEndpoint != null) {
                    dependencies.add(listEndpoint);
                }
            }

            endpoint.setDependencies(dependencies);
        }

        logger.info("Bağımlılık analizi tamamlandı");
    }

    /**
     * Complexity analizi yapar
     */
    private void performComplexityAnalysis(List<EndpointInfo> endpoints) {
        logger.info("Complexity analizi yapılıyor...");

        for (EndpointInfo endpoint : endpoints) {
            EndpointComplexity complexity = calculateEndpointComplexity(endpoint);
            complexityCache.put(endpoint.getPath() + ":" + endpoint.getMethod(), complexity);
            endpoint.setComplexity(complexity);
        }

        logger.info("Complexity analizi tamamlandı");
    }

    /**
     * Endpoint complexity'sini hesaplar
     */
    private EndpointComplexity calculateEndpointComplexity(EndpointInfo endpoint) {
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

        EndpointComplexity complexity = new EndpointComplexity();
        complexity.setScore(score);

        if (score <= 10) {
            complexity.setLevel("LOW");
        } else if (score <= 25) {
            complexity.setLevel("MEDIUM");
        } else if (score <= 40) {
            complexity.setLevel("HIGH");
        } else {
            complexity.setLevel("VERY_HIGH");
        }

        return complexity;
    }

    /**
     * Akıllı örnek veri üretir
     */
    private String generateSmartExampleData(JsonNode schema, String resourceType) {
        try {
            Map<String, Object> exampleData = new HashMap<>();
            String timestamp = String.valueOf(System.currentTimeMillis());

            // Temel alanlar
            exampleData.put("id", "test_" + timestamp);
            exampleData.put("name", "Test " + (resourceType != null ? resourceType : "Object"));
            exampleData.put("createdAt", new Date().toString());

            // Resource tipine göre özel alanlar
            if (resourceType != null) {
                switch (resourceType.toLowerCase()) {
                    case "user":
                        exampleData.put("email", "test" + timestamp + "@example.com");
                        exampleData.put("username", "testuser" + timestamp);
                        break;
                    case "product":
                        exampleData.put("price", 99.99);
                        exampleData.put("category", "test-category");
                        break;
                    case "order":
                        exampleData.put("total", 149.99);
                        exampleData.put("status", "pending");
                        break;
                }
            }

            // Schema properties'e göre alanlar ekle
            if (schema.has("properties")) {
                JsonNode properties = schema.get("properties");
                Iterator<Map.Entry<String, JsonNode>> fields = properties.fields();

                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> field = fields.next();
                    String fieldName = field.getKey();
                    JsonNode fieldSchema = field.getValue();

                    if (!exampleData.containsKey(fieldName)) {
                        Object value = generateValueForField(fieldName, fieldSchema);
                        if (value != null) {
                            exampleData.put(fieldName, value);
                        }
                    }
                }
            }

            return objectMapper.writeValueAsString(exampleData);

        } catch (Exception e) {
            logger.warning("Örnek veri oluşturulamadı: " + e.getMessage());
            return "{\"test\": \"data\"}";
        }
    }

    /**
     * Alan için değer üretir
     */
    private Object generateValueForField(String fieldName, JsonNode fieldSchema) {
        if (!fieldSchema.has("type")) {
            return null;
        }

        String type = fieldSchema.get("type").asText();
        String lowerFieldName = fieldName.toLowerCase();

        switch (type) {
            case "string":
                if (lowerFieldName.contains("email")) {
                    return "test@example.com";
                } else if (lowerFieldName.contains("phone")) {
                    return "+1-555-0123";
                } else if (lowerFieldName.contains("url")) {
                    return "https://example.com";
                } else {
                    return "test_" + fieldName;
                }
            case "integer":
                return lowerFieldName.contains("id") ? 1 : 42;
            case "number":
                return lowerFieldName.contains("price") ? 99.99 : 42.5;
            case "boolean":
                return !lowerFieldName.contains("deleted") && !lowerFieldName.contains("disabled");
            default:
                return null;
        }
    }

    // Yardımcı metodlar
    private boolean hasSchemas(JsonNode rootNode) {
        return rootNode.has("components") && rootNode.get("components").has("schemas");
    }

    private boolean isValidHttpMethod(String method) {
        return Arrays.asList("get", "post", "put", "delete", "patch", "head", "options").contains(method.toLowerCase());
    }

    private String generateOperationId(String path, String method) {
        String cleanPath = path.replaceAll("[^a-zA-Z0-9]", "_");
        return method + "_" + cleanPath;
    }

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

    private String findListEndpoint(Map<String, List<EndpointInfo>> resourceGroups, String resourceType) {
        if (resourceType == null) return null;

        List<EndpointInfo> endpoints = resourceGroups.get(resourceType);
        if (endpoints == null) return null;

        for (EndpointInfo endpoint : endpoints) {
            if ("get".equalsIgnoreCase(endpoint.getMethod()) && !endpoint.getPath().contains("{")) {
                return endpoint.getPath() + ":" + endpoint.getMethod();
            }
        }
        return null;
    }

    private List<EndpointInfo> sortEndpointsByPriority(List<EndpointInfo> endpoints) {
        return endpoints.stream()
                .sorted((e1, e2) -> {
                    // POST metodları önce (kaynak oluşturma)
                    if ("post".equals(e1.getMethod()) && !"post".equals(e2.getMethod())) {
                        return -1;
                    }
                    if (!"post".equals(e1.getMethod()) && "post".equals(e2.getMethod())) {
                        return 1;
                    }

                    // Complexity'ye göre sırala
                    int complexity1 = e1.getComplexity() != null ? e1.getComplexity().getScore() : 0;
                    int complexity2 = e2.getComplexity() != null ? e2.getComplexity().getScore() : 0;

                    return Integer.compare(complexity1, complexity2);
                })
                .collect(ArrayList::new, (list, item) -> list.add(item), ArrayList::addAll);
    }
}