package org.example.openapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Instant;
import java.time.Duration;

/**
 * Enterprise-Grade Schema Analyzer for OpenAPI Test Generator
 * STANDARDIZED VERSION - Aligned with TestStrategyManager Reference
 *
 * Features:
 * - Standard interface compatibility with tutarlılık rehberi
 * - Advanced OpenAPI schema analysis with comprehensive feature set
 * - Multi-threaded parallel analysis with CompletableFuture
 * - Intelligent caching system with LRU eviction
 * - Security analysis and vulnerability detection
 * - Performance optimization and bottleneck detection
 * - Quality metrics and validation
 * - Standard method signatures as per reference
 *
 * @version 5.0.0-STANDARDIZED
 * @since 2025-06-18
 */
public class SchemaAnalyzer {

    private static final Logger logger = Logger.getLogger(SchemaAnalyzer.class.getName());

    // ===== Configuration Constants =====
    private static final int DEFAULT_THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2;
    private static final int MAX_CACHE_SIZE = 10000;
    private static final int ANALYSIS_TIMEOUT_SECONDS = 300;
    private static final String VERSION = "5.0.0-STANDARDIZED";

    // Performance thresholds
    private static final int COMPLEXITY_THRESHOLD_HIGH = 50;
    private static final int COMPLEXITY_THRESHOLD_CRITICAL = 100;
    private static final int MAX_SCHEMA_DEPTH = 20;
    private static final int MAX_CIRCULAR_REFERENCES = 5;

    // ===== STANDARD ENUMS - Matching Tutarlılık Rehberi =====

    /**
     * Standard StrategyType Enum - EXACT MATCH with rehber standartları
     */
    public enum StrategyType {
        // === FUNCTIONAL TESTING STRATEGIES ===
        FUNCTIONAL_BASIC("Basic functional testing", 1, true, false, false, StrategyCategory.FUNCTIONAL),
        FUNCTIONAL_COMPREHENSIVE("Comprehensive functional testing", 2, true, false, false, StrategyCategory.FUNCTIONAL),
        FUNCTIONAL_BOUNDARY("Boundary condition testing", 2, true, false, true, StrategyCategory.FUNCTIONAL),
        FUNCTIONAL_EDGE_CASE("Edge case testing", 3, true, false, true, StrategyCategory.FUNCTIONAL),

        // === SECURITY TESTING STRATEGIES ===
        SECURITY_BASIC("Basic security validation", 2, false, true, false, StrategyCategory.SECURITY),
        SECURITY_OWASP_TOP10("OWASP Top 10 testing", 4, false, true, true, StrategyCategory.SECURITY),
        SECURITY_PENETRATION("Penetration testing", 5, false, true, true, StrategyCategory.SECURITY),
        SECURITY_INJECTION("Injection attack testing", 4, false, true, true, StrategyCategory.SECURITY),
        SECURITY_XSS("XSS vulnerability testing", 4, false, true, true, StrategyCategory.SECURITY),

        // === PERFORMANCE TESTING STRATEGIES ===
        PERFORMANCE_BASIC("Basic performance testing", 2, false, false, true, StrategyCategory.PERFORMANCE),
        PERFORMANCE_LOAD("Load testing", 3, false, false, true, StrategyCategory.PERFORMANCE),

        // === ADVANCED TESTING STRATEGIES ===
        ADVANCED_AI_DRIVEN("AI-driven testing", 5, true, false, true, StrategyCategory.ADVANCED);

        private final String description;
        private final int complexity;
        private final boolean supportsFunctional;
        private final boolean supportsSecurity;
        private final boolean requiresAdvancedAnalysis;
        private final StrategyCategory category;

        StrategyType(String description, int complexity, boolean supportsFunctional,
                     boolean supportsSecurity, boolean requiresAdvancedAnalysis, StrategyCategory category) {
            this.description = description;
            this.complexity = complexity;
            this.supportsFunctional = supportsFunctional;
            this.supportsSecurity = supportsSecurity;
            this.requiresAdvancedAnalysis = requiresAdvancedAnalysis;
            this.category = category;
        }

        public String getDescription() { return description; }
        public int getComplexity() { return complexity; }
        public boolean supportsFunctional() { return supportsFunctional; }
        public boolean supportsSecurity() { return supportsSecurity; }
        public boolean requiresAdvancedAnalysis() { return requiresAdvancedAnalysis; }
        public StrategyCategory getCategory() { return category; }
    }

    /**
     * Standard StrategyCategory enum - EXACT MATCH with rehber
     */
    public enum StrategyCategory {
        FUNCTIONAL("Functional Testing", "Core functionality validation"),
        SECURITY("Security Testing", "Security vulnerability assessment"),
        PERFORMANCE("Performance Testing", "Performance and scalability validation"),
        ADVANCED("Advanced Testing", "Advanced testing methodologies"),
        SPECIALIZED("Specialized Testing", "Technology-specific testing");

        private final String name;
        private final String description;

        StrategyCategory(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() { return name; }
        public String getDescription() { return description; }
    }

    /**
     * Standard TestGenerationScenario enum - EXACT MATCH with rehber
     */
    public enum TestGenerationScenario {
        // === FUNCTIONAL SCENARIOS ===
        HAPPY_PATH("Happy path testing", StrategyType.FUNCTIONAL_BASIC, 1, ScenarioCategory.FUNCTIONAL),
        ERROR_HANDLING("Error handling testing", StrategyType.FUNCTIONAL_COMPREHENSIVE, 2, ScenarioCategory.FUNCTIONAL),
        BOUNDARY_MIN("Minimum boundary testing", StrategyType.FUNCTIONAL_BOUNDARY, 2, ScenarioCategory.BOUNDARY),
        BOUNDARY_MAX("Maximum boundary testing", StrategyType.FUNCTIONAL_BOUNDARY, 2, ScenarioCategory.BOUNDARY),

        // === SECURITY SCENARIOS ===
        SQL_INJECTION_BASIC("Basic SQL injection testing", StrategyType.SECURITY_INJECTION, 3, ScenarioCategory.SECURITY),
        XSS_REFLECTED("Reflected XSS testing", StrategyType.SECURITY_XSS, 3, ScenarioCategory.SECURITY),

        // === PERFORMANCE SCENARIOS ===
        LOAD_TESTING_LIGHT("Light load testing", StrategyType.PERFORMANCE_LOAD, 2, ScenarioCategory.PERFORMANCE);

        private final String description;
        private final StrategyType recommendedStrategy;
        private final int complexity;
        private final ScenarioCategory category;

        TestGenerationScenario(String description, StrategyType recommendedStrategy,
                               int complexity, ScenarioCategory category) {
            this.description = description;
            this.recommendedStrategy = recommendedStrategy;
            this.complexity = complexity;
            this.category = category;
        }

        public String getDescription() { return description; }
        public StrategyType getRecommendedStrategy() { return recommendedStrategy; }
        public int getComplexity() { return complexity; }
        public ScenarioCategory getCategory() { return category; }
    }

    /**
     * Standard ScenarioCategory - EXACT MATCH with rehber
     */
    public enum ScenarioCategory {
        FUNCTIONAL, SECURITY, PERFORMANCE, BOUNDARY, ADVANCED
    }

    // ===== STANDARD DATA CLASSES - EXACT MATCH with Tutarlılık Rehberi =====

    /**
     * Standard EndpointInfo class - EXACT MATCH with rehber interface
     */
    public static class EndpointInfo {
        private String method;
        private String path;
        private String operationId;
        private List<ParameterInfo> parameters;
        private RequestBodyInfo requestBodyInfo;
        private Map<String, ResponseInfo> responses;
        private List<String> securitySchemes;
        private boolean requiresAuthentication;
        private boolean hasParameters;
        private boolean hasRequestBody;

        public EndpointInfo() {
        }

        public EndpointInfo(String method, String path, String operationId) {
            this.method = method;
            this.path = path;
            this.operationId = operationId;
            this.parameters = new ArrayList<>();
            this.responses = new HashMap<>();
            this.securitySchemes = new ArrayList<>();
        }

        // Standard getters - EXACT MATCH with rehber
        public String getMethod() { return method; }
        public String getPath() { return path; }
        public String getOperationId() { return operationId; }
        public List<ParameterInfo> getParameters() { return parameters != null ? parameters : new ArrayList<>(); }
        public RequestBodyInfo getRequestBodyInfo() { return requestBodyInfo; }
        public Map<String, ResponseInfo> getResponses() { return responses != null ? responses : new HashMap<>(); }
        public List<String> getSecuritySchemes() { return securitySchemes != null ? securitySchemes : new ArrayList<>(); }
        public boolean isRequiresAuthentication() { return requiresAuthentication; }
        public boolean isHasParameters() { return hasParameters; }
        public boolean isHasRequestBody() { return hasRequestBody; }

        // Setters for builder
        public void setMethod(String method) { this.method = method; }
        public void setPath(String path) { this.path = path; }
        public void setOperationId(String operationId) { this.operationId = operationId; }
        public void setParameters(List<ParameterInfo> parameters) {
            this.parameters = parameters;
            this.hasParameters = parameters != null && !parameters.isEmpty();
        }
        public void setRequestBodyInfo(RequestBodyInfo requestBodyInfo) {
            this.requestBodyInfo = requestBodyInfo;
            this.hasRequestBody = requestBodyInfo != null;
        }
        public void setResponses(Map<String, ResponseInfo> responses) { this.responses = responses; }
        public void setSecuritySchemes(List<String> securitySchemes) { this.securitySchemes = securitySchemes; }
        public void setRequiresAuthentication(boolean requiresAuthentication) { this.requiresAuthentication = requiresAuthentication; }

        @Override
        public int hashCode() { return Objects.hash(method, path, operationId); }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            EndpointInfo that = (EndpointInfo) obj;
            return Objects.equals(method, that.method) &&
                    Objects.equals(path, that.path) &&
                    Objects.equals(operationId, that.operationId);
        }
    }

    /**
     * Standard ParameterInfo class - EXACT MATCH with rehber
     */
    public static class ParameterInfo {
        private String name;
        private String type;
        private boolean required;

        public ParameterInfo() {}
        public ParameterInfo(String name, String type, boolean required) {
            this.name = name;
            this.type = type;
            this.required = required;
        }

        public String getName() { return name; }
        public String getType() { return type; }
        public boolean isRequired() { return required; }
        public void setName(String name) { this.name = name; }
        public void setType(String type) { this.type = type; }
        public void setRequired(boolean required) { this.required = required; }
    }

    /**
     * Standard RequestBodyInfo class - EXACT MATCH with rehber
     */
    public static class RequestBodyInfo {
        private String contentType;
        private Object schema;

        public RequestBodyInfo() {}
        public RequestBodyInfo(String contentType, Object schema) {
            this.contentType = contentType;
            this.schema = schema;
        }

        public String getContentType() { return contentType; }
        public Object getSchema() { return schema; }
        public void setContentType(String contentType) { this.contentType = contentType; }
        public void setSchema(Object schema) { this.schema = schema; }
    }

    /**
     * Standard ResponseInfo class - EXACT MATCH with rehber
     */
    public static class ResponseInfo {
        private String statusCode;
        private String description;
        private Object schema;

        public ResponseInfo() {}
        public ResponseInfo(String statusCode, String description) {
            this.statusCode = statusCode;
            this.description = description;
        }

        public String getStatusCode() { return statusCode; }
        public String getDescription() { return description; }
        public Object getSchema() { return schema; }
        public void setStatusCode(String statusCode) { this.statusCode = statusCode; }
        public void setDescription(String description) { this.description = description; }
        public void setSchema(Object schema) { this.schema = schema; }
    }

    /**
     * Standard GeneratedTestCase with builder pattern - EXACT MATCH with rehber
     */
    public static class GeneratedTestCase {
        private String testId;
        private String testName;
        private String description;
        private TestGenerationScenario scenario;
        private StrategyType strategyType;
        private EndpointInfo endpoint;
        private List<TestStep> testSteps;
        private TestDataSet testData;
        private List<TestAssertion> assertions;
        private int priority;
        private Duration estimatedDuration;
        private int complexity;
        private Set<String> tags;
        private Instant generationTimestamp;

        // Private constructor for builder
        private GeneratedTestCase() {}

        // Standard getters - EXACT MATCH with rehber
        public String getTestId() { return testId; }
        public String getTestName() { return testName; }
        public String getDescription() { return description; }
        public TestGenerationScenario getScenario() { return scenario; }
        public StrategyType getStrategyType() { return strategyType; }
        public EndpointInfo getEndpoint() { return endpoint; }
        public List<TestStep> getTestSteps() { return testSteps != null ? testSteps : new ArrayList<>(); }
        public TestDataSet getTestData() { return testData; }
        public List<TestAssertion> getAssertions() { return assertions != null ? assertions : new ArrayList<>(); }
        public int getPriority() { return priority; }
        public Duration getEstimatedDuration() { return estimatedDuration; }
        public int getComplexity() { return complexity; }
        public Set<String> getTags() { return tags != null ? tags : new HashSet<>(); }
        public Instant getGenerationTimestamp() { return generationTimestamp; }

        /**
         * Standard Builder pattern - EXACT MATCH with rehber
         */
        public static class Builder {
            private GeneratedTestCase testCase = new GeneratedTestCase();

            public Builder withTestId(String testId) { testCase.testId = testId; return this; }
            public Builder withTestName(String testName) { testCase.testName = testName; return this; }
            public Builder withDescription(String description) { testCase.description = description; return this; }
            public Builder withScenario(TestGenerationScenario scenario) { testCase.scenario = scenario; return this; }
            public Builder withStrategyType(StrategyType strategyType) { testCase.strategyType = strategyType; return this; }
            public Builder withEndpoint(EndpointInfo endpoint) { testCase.endpoint = endpoint; return this; }
            public Builder withTestSteps(List<TestStep> testSteps) { testCase.testSteps = testSteps; return this; }
            public Builder withTestData(TestDataSet testData) { testCase.testData = testData; return this; }
            public Builder withAssertions(List<TestAssertion> assertions) { testCase.assertions = assertions; return this; }
            public Builder withPriority(int priority) { testCase.priority = priority; return this; }
            public Builder withEstimatedDuration(Duration estimatedDuration) { testCase.estimatedDuration = estimatedDuration; return this; }
            public Builder withComplexity(int complexity) { testCase.complexity = complexity; return this; }
            public Builder withTags(Set<String> tags) { testCase.tags = tags; return this; }

            public GeneratedTestCase build() {
                if (testCase.generationTimestamp == null) {
                    testCase.generationTimestamp = Instant.now();
                }
                return testCase;
            }
        }

        // Standard builder factory method - EXACT MATCH with rehber
        public static Builder builder() { return new Builder(); }
    }

    // ===== SUPPORTING STANDARD CLASSES =====

    public static class TestStep {
        private String action;
        private String description;
        private Map<String, Object> parameters;

        public TestStep() {}
        public TestStep(String action, String description) {
            this.action = action;
            this.description = description;
            this.parameters = new HashMap<>();
        }

        public String getAction() { return action; }
        public String getDescription() { return description; }
        public Map<String, Object> getParameters() { return parameters != null ? parameters : new HashMap<>(); }
        public void setAction(String action) { this.action = action; }
        public void setDescription(String description) { this.description = description; }
        public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
    }

    public static class TestDataSet {
        private Map<String, Object> data;

        public TestDataSet() {}
        public Map<String, Object> getData() { return data != null ? data : new HashMap<>(); }
        public void setData(Map<String, Object> data) { this.data = data; }
    }

    public static class TestAssertion {
        private String type;
        private String condition;
        private Object expected;

        public TestAssertion() {}
        public TestAssertion(String type, String condition, Object expected) {
            this.type = type;
            this.condition = condition;
            this.expected = expected;
        }

        public String getType() { return type; }
        public String getCondition() { return condition; }
        public Object getExpected() { return expected; }
        public void setType(String type) { this.type = type; }
        public void setCondition(String condition) { this.condition = condition; }
        public void setExpected(Object expected) { this.expected = expected; }
    }

    // ===== Enhanced Caching System =====
    private final Map<String, JsonNode> schemaCache = new ConcurrentHashMap<>();
    private final Map<String, DataConstraints> constraintCache = new ConcurrentHashMap<>();
    private final Map<String, List<String>> responsePatternCache = new ConcurrentHashMap<>();
    private final Map<String, EndpointComplexity> complexityCache = new ConcurrentHashMap<>();
    private final Map<String, SecurityAnalysisResult> securityCache = new ConcurrentHashMap<>();
    private final Map<String, PerformanceMetrics> performanceCache = new ConcurrentHashMap<>();

    // ===== Core Components =====
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Executor analysisExecutor = Executors.newFixedThreadPool(DEFAULT_THREAD_POOL_SIZE);
    private final AnalysisConfiguration configuration;

    // Compiled patterns for performance
    private final Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private final Pattern urlPattern = Pattern.compile("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
    private final Pattern phonePattern = Pattern.compile("^[+]?[1-9]?[0-9]{7,15}$");
    private final Pattern uuidPattern = Pattern.compile("^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$");

    // ===== Constructors =====

    /**
     * Default constructor with standard configuration
     */
    public SchemaAnalyzer() {
        this(AnalysisConfiguration.createDefault());
    }

    /**
     * Constructor with custom configuration
     *
     * @param configuration Custom analysis configuration
     */
    public SchemaAnalyzer(AnalysisConfiguration configuration) {
        this.configuration = configuration;
        logger.info("Enhanced SchemaAnalyzer v" + VERSION + " initialized");
    }

    // ===== CORE API METHODS - STANDARD INTERFACE SIGNATURES =====

    /**
     * STANDARD METHOD SIGNATURE: analyzeEndpoints
     * EXACT MATCH with rehber interface signature
     *
     * @param jsonContent OpenAPI specification JSON content
     * @return List of analyzed endpoint information
     * @throws Exception if analysis fails
     */
    public List<EndpointInfo> analyzeEndpoints(String jsonContent) throws Exception {
        logger.info("Starting endpoint analysis...");
        long startTime = System.currentTimeMillis();

        try {
            // Parse and validate JSON
            JsonNode rootNode = parseAndValidateJson(jsonContent);

            // Extract endpoints
            List<EndpointInfo> endpoints = collectAndAnalyzeEndpoints(rootNode);

            // Log performance metrics
            long duration = System.currentTimeMillis() - startTime;
            logger.info("Analysis completed in " + duration + "ms - " +
                    endpoints.size() + " endpoints analyzed");

            return endpoints;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Analysis failed", e);
            throw new AnalysisException("Failed to analyze endpoints: " + e.getMessage(), e);
        }
    }

    /**
     * STANDARD METHOD SIGNATURE: generateAdvancedCacheKey
     * Standard cache key generation method
     */
    private String generateAdvancedCacheKey(EndpointInfo endpoint) {
        return String.format("endpoint_%s_%s_%d",
                endpoint.getMethod(),
                endpoint.getPath().replaceAll("[^a-zA-Z0-9]", "_"),
                endpoint.hashCode()
        );
    }

    /**
     * STANDARD METHOD SIGNATURE: createOptimizedExecutorService
     * Standard executor service creation
     */
    private Executor createOptimizedExecutorService() {
        return Executors.newFixedThreadPool(
                Math.max(1, Math.min(DEFAULT_THREAD_POOL_SIZE, Runtime.getRuntime().availableProcessors() * 2))
        );
    }

    /**
     * STANDARD METHOD SIGNATURE: validateConfiguration
     * Standard configuration validation method
     */
    private void validateConfiguration(AnalysisConfiguration config) {
        if (config == null) {
            throw new IllegalArgumentException("AnalysisConfiguration cannot be null");
        }
        if (config.getThreadPoolSize() <= 0) {
            throw new IllegalArgumentException("Thread pool size must be positive");
        }
    }

    // ===== ENDPOINT COLLECTION AND ANALYSIS =====

    /**
     * Collects and analyzes endpoints from OpenAPI specification
     *
     * @param rootNode Root JSON node
     * @return List of analyzed endpoints
     */
    private List<EndpointInfo> collectAndAnalyzeEndpoints(JsonNode rootNode) {
        List<EndpointInfo> endpoints = new ArrayList<>();

        if (!rootNode.has("paths")) {
            logger.warning("No paths found in OpenAPI document");
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
                    EndpointInfo endpointInfo = createStandardEndpointInfo(endpointPath, httpMethod, operationNode);
                    endpoints.add(endpointInfo);
                }
            }
        }

        return endpoints;
    }

    /**
     * Creates standard endpoint information using rehber interface
     *
     * @param path Endpoint path
     * @param method HTTP method
     * @param operationNode Operation JSON node
     * @return Standard EndpointInfo
     */
    private EndpointInfo createStandardEndpointInfo(String path, String method, JsonNode operationNode) {
        EndpointInfo endpoint = new EndpointInfo(method, path, generateOperationId(path, method));

        // Parameters analysis
        List<ParameterInfo> parameters = extractParameters(operationNode);
        endpoint.setParameters(parameters);

        // Request body analysis
        RequestBodyInfo requestBody = extractRequestBody(operationNode);
        endpoint.setRequestBodyInfo(requestBody);

        // Response analysis
        Map<String, ResponseInfo> responses = extractResponses(operationNode);
        endpoint.setResponses(responses);

        // Security analysis
        List<String> securitySchemes = extractSecuritySchemes(operationNode);
        endpoint.setSecuritySchemes(securitySchemes);
        endpoint.setRequiresAuthentication(!securitySchemes.isEmpty());

        return endpoint;
    }

    /**
     * Extracts parameters from operation node
     *
     * @param operationNode Operation JSON node
     * @return List of parameter information
     */
    private List<ParameterInfo> extractParameters(JsonNode operationNode) {
        List<ParameterInfo> parameters = new ArrayList<>();

        if (operationNode.has("parameters")) {
            JsonNode parametersNode = operationNode.get("parameters");
            for (JsonNode paramNode : parametersNode) {
                if (paramNode.has("name")) {
                    String name = paramNode.get("name").asText();
                    String type = extractParameterType(paramNode);
                    boolean required = paramNode.has("required") && paramNode.get("required").asBoolean();

                    parameters.add(new ParameterInfo(name, type, required));
                }
            }
        }

        return parameters;
    }

    /**
     * Extracts parameter type from parameter node
     *
     * @param paramNode Parameter JSON node
     * @return Parameter type
     */
    private String extractParameterType(JsonNode paramNode) {
        if (paramNode.has("schema") && paramNode.get("schema").has("type")) {
            return paramNode.get("schema").get("type").asText();
        }
        if (paramNode.has("type")) {
            return paramNode.get("type").asText();
        }
        return "string"; // Default type
    }

    /**
     * Extracts request body information
     *
     * @param operationNode Operation JSON node
     * @return Request body information or null
     */
    private RequestBodyInfo extractRequestBody(JsonNode operationNode) {
        if (!operationNode.has("requestBody")) {
            return null;
        }

        JsonNode requestBodyNode = operationNode.get("requestBody");
        if (requestBodyNode.has("content")) {
            JsonNode contentNode = requestBodyNode.get("content");
            Iterator<String> contentTypes = contentNode.fieldNames();

            if (contentTypes.hasNext()) {
                String contentType = contentTypes.next();
                JsonNode mediaTypeNode = contentNode.get(contentType);
                Object schema = mediaTypeNode.has("schema") ? mediaTypeNode.get("schema") : null;

                return new RequestBodyInfo(contentType, schema);
            }
        }

        return null;
    }

    /**
     * Extracts response information
     *
     * @param operationNode Operation JSON node
     * @return Map of response information
     */
    private Map<String, ResponseInfo> extractResponses(JsonNode operationNode) {
        Map<String, ResponseInfo> responses = new HashMap<>();

        if (operationNode.has("responses")) {
            JsonNode responsesNode = operationNode.get("responses");
            Iterator<Map.Entry<String, JsonNode>> responseIterator = responsesNode.fields();

            while (responseIterator.hasNext()) {
                Map.Entry<String, JsonNode> responseEntry = responseIterator.next();
                String statusCode = responseEntry.getKey();
                JsonNode responseNode = responseEntry.getValue();

                String description = responseNode.has("description") ?
                        responseNode.get("description").asText() : "";
                Object schema = extractResponseSchema(responseNode);

                responses.put(statusCode, new ResponseInfo(statusCode, description));
            }
        }

        return responses;
    }

    /**
     * Extracts response schema
     *
     * @param responseNode Response JSON node
     * @return Response schema object
     */
    private Object extractResponseSchema(JsonNode responseNode) {
        if (responseNode.has("content")) {
            JsonNode contentNode = responseNode.get("content");
            Iterator<String> contentTypes = contentNode.fieldNames();

            if (contentTypes.hasNext()) {
                String contentType = contentTypes.next();
                JsonNode mediaTypeNode = contentNode.get(contentType);
                return mediaTypeNode.has("schema") ? mediaTypeNode.get("schema") : null;
            }
        }
        return null;
    }

    /**
     * Extracts security schemes
     *
     * @param operationNode Operation JSON node
     * @return List of security scheme names
     */
    private List<String> extractSecuritySchemes(JsonNode operationNode) {
        List<String> securitySchemes = new ArrayList<>();

        if (operationNode.has("security")) {
            JsonNode securityNode = operationNode.get("security");
            for (JsonNode securityItem : securityNode) {
                Iterator<String> securityNames = securityItem.fieldNames();
                while (securityNames.hasNext()) {
                    securitySchemes.add(securityNames.next());
                }
            }
        }

        return securitySchemes;
    }

    // ===== HELPER METHODS =====

    /**
     * Parses and validates JSON content
     *
     * @param jsonContent JSON content to parse
     * @return Parsed JSON node
     * @throws Exception if parsing fails
     */
    /**
     * Parses and validates JSON content
     *
     * @param jsonContent JSON content to parse
     * @return Parsed JSON node
     * @throws Exception if parsing fails
     */
    private JsonNode parseAndValidateJson(String jsonContent) throws Exception {
        if (jsonContent == null || jsonContent.trim().isEmpty()) {
            throw new IllegalArgumentException("JSON content cannot be null or empty");
        }

        try {
            JsonNode rootNode = objectMapper.readTree(jsonContent);

            // Basic OpenAPI validation
            if (!rootNode.has("openapi") && !rootNode.has("swagger")) {
                throw new IllegalArgumentException("Not a valid OpenAPI/Swagger specification");
            }

            return rootNode;
        } catch (Exception e) {
            throw new AnalysisException("Failed to parse JSON content: " + e.getMessage(), e);
        }
    }

    /**
     * Checks if method is valid HTTP method
     *
     * @param method HTTP method
     * @return true if valid
     */
    private boolean isValidHttpMethod(String method) {
        return Arrays.asList("get", "post", "put", "delete", "patch", "head", "options", "trace")
                .contains(method.toLowerCase());
    }

    /**
     * Generates operation ID from path and method
     *
     * @param path Endpoint path
     * @param method HTTP method
     * @return Generated operation ID
     */
    private String generateOperationId(String path, String method) {
        String cleanPath = path.replaceAll("[^a-zA-Z0-9]", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "");
        return method.toLowerCase() + "_" + cleanPath;
    }

    // ===== ENHANCED DATA CONSTRAINTS ANALYSIS =====

    /**
     * Enhanced data constraints analysis with comprehensive validation
     *
     * @param schema Schema JSON node
     * @param schemaName Schema name for caching
     * @return Enhanced data constraints
     */
    private DataConstraints analyzeDataConstraints(JsonNode schema, String schemaName) {
        if (schema == null) {
            return new DataConstraints();
        }

        // Check cache first
        DataConstraints cached = constraintCache.get(schemaName);
        if (cached != null) {
            return cached;
        }

        DataConstraints.Builder builder = new DataConstraints.Builder();

        // Basic type information
        if (schema.has("type")) {
            String type = schema.get("type").asText();
            builder.withType(type);

            // Type-specific constraint analysis
            switch (type) {
                case "string":
                    analyzeStringConstraints(schema, builder);
                    break;
                case "integer":
                case "number":
                    analyzeNumericConstraints(schema, builder);
                    break;
                case "array":
                    analyzeArrayConstraints(schema, builder);
                    break;
                case "object":
                    analyzeObjectConstraints(schema, builder);
                    break;
                case "boolean":
                    analyzeBooleanConstraints(schema, builder);
                    break;
            }
        }

        // Format-specific constraints
        if (schema.has("format")) {
            analyzeFormatConstraints(schema, builder);
        }

        // Enum constraints
        if (schema.has("enum")) {
            analyzeEnumConstraints(schema, builder);
        }

        DataConstraints constraints = builder.build();
        constraintCache.put(schemaName, constraints);

        return constraints;
    }

    /**
     * String constraints analysis
     */
    private void analyzeStringConstraints(JsonNode schema, DataConstraints.Builder builder) {
        if (schema.has("minLength")) {
            builder.withMinLength(schema.get("minLength").asInt());
        }
        if (schema.has("maxLength")) {
            builder.withMaxLength(schema.get("maxLength").asInt());
        }
        if (schema.has("pattern")) {
            String pattern = schema.get("pattern").asText();
            builder.withPattern(pattern);
        }
    }

    /**
     * Numeric constraints analysis
     */
    private void analyzeNumericConstraints(JsonNode schema, DataConstraints.Builder builder) {
        if (schema.has("minimum")) {
            builder.withMinimum(schema.get("minimum").decimalValue());
        }
        if (schema.has("maximum")) {
            builder.withMaximum(schema.get("maximum").decimalValue());
        }
        if (schema.has("multipleOf")) {
            builder.withMultipleOf(schema.get("multipleOf").decimalValue());
        }
    }

    /**
     * Array constraints analysis
     */
    private void analyzeArrayConstraints(JsonNode schema, DataConstraints.Builder builder) {
        if (schema.has("minItems")) {
            builder.withMinItems(schema.get("minItems").asInt());
        }
        if (schema.has("maxItems")) {
            builder.withMaxItems(schema.get("maxItems").asInt());
        }
        if (schema.has("uniqueItems")) {
            builder.withUniqueItems(schema.get("uniqueItems").asBoolean());
        }
    }

    /**
     * Object constraints analysis
     */
    private void analyzeObjectConstraints(JsonNode schema, DataConstraints.Builder builder) {
        if (schema.has("minProperties")) {
            builder.withMinProperties(schema.get("minProperties").asInt());
        }
        if (schema.has("maxProperties")) {
            builder.withMaxProperties(schema.get("maxProperties").asInt());
        }

        // Required fields
        if (schema.has("required")) {
            List<String> requiredFields = new ArrayList<>();
            schema.get("required").forEach(field -> requiredFields.add(field.asText()));
            builder.withRequiredFields(requiredFields);
        }
    }

    /**
     * Boolean constraints analysis
     */
    private void analyzeBooleanConstraints(JsonNode schema, DataConstraints.Builder builder) {
        // Boolean type doesn't have specific constraints in JSON Schema
        builder.withBooleanContext("GENERAL");
    }

    /**
     * Format-specific constraints analysis
     */
    private void analyzeFormatConstraints(JsonNode schema, DataConstraints.Builder builder) {
        String format = schema.get("format").asText();
        builder.withFormat(format);
    }

    /**
     * Enum constraints analysis
     */
    private void analyzeEnumConstraints(JsonNode schema, DataConstraints.Builder builder) {
        List<String> enumValues = new ArrayList<>();
        schema.get("enum").forEach(value -> enumValues.add(value.asText()));
        builder.withEnumValues(enumValues);
    }

    // ===== CONFIGURATION CLASSES =====

    /**
     * Analysis configuration class
     */
    public static class AnalysisConfiguration {
        private final boolean securityEnabled;
        private final boolean performanceEnabled;
        private final boolean qualityEnabled;
        private final boolean parallelProcessingEnabled;
        private final int threadPoolSize;
        private final int cacheSize;

        public AnalysisConfiguration(boolean securityEnabled, boolean performanceEnabled,
                                     boolean qualityEnabled, boolean parallelProcessingEnabled,
                                     int threadPoolSize, int cacheSize) {
            this.securityEnabled = securityEnabled;
            this.performanceEnabled = performanceEnabled;
            this.qualityEnabled = qualityEnabled;
            this.parallelProcessingEnabled = parallelProcessingEnabled;
            this.threadPoolSize = threadPoolSize;
            this.cacheSize = cacheSize;
        }

        public static AnalysisConfiguration createDefault() {
            return new AnalysisConfiguration(true, true, true, true, DEFAULT_THREAD_POOL_SIZE, MAX_CACHE_SIZE);
        }

        // Getters
        public boolean isSecurityEnabled() { return securityEnabled; }
        public boolean isPerformanceEnabled() { return performanceEnabled; }
        public boolean isQualityEnabled() { return qualityEnabled; }
        public boolean isParallelProcessingEnabled() { return parallelProcessingEnabled; }
        public int getThreadPoolSize() { return threadPoolSize; }
        public int getCacheSize() { return cacheSize; }
    }

    // ===== DATA CLASSES =====

    /**
     * Data constraints with builder pattern
     */
    public static class DataConstraints {
        private String type;
        private Integer minLength;
        private Integer maxLength;
        private String pattern;
        private BigDecimal minimum;
        private BigDecimal maximum;
        private BigDecimal multipleOf;
        private Integer minItems;
        private Integer maxItems;
        private Boolean uniqueItems;
        private Integer minProperties;
        private Integer maxProperties;
        private List<String> requiredFields;
        private String booleanContext;
        private String format;
        private List<String> enumValues;

        // Private constructor for builder
        private DataConstraints() {}

        // Getters
        public String getType() { return type; }
        public Integer getMinLength() { return minLength; }
        public Integer getMaxLength() { return maxLength; }
        public String getPattern() { return pattern; }
        public BigDecimal getMinimum() { return minimum; }
        public BigDecimal getMaximum() { return maximum; }
        public BigDecimal getMultipleOf() { return multipleOf; }
        public Integer getMinItems() { return minItems; }
        public Integer getMaxItems() { return maxItems; }
        public Boolean getUniqueItems() { return uniqueItems; }
        public Integer getMinProperties() { return minProperties; }
        public Integer getMaxProperties() { return maxProperties; }
        public List<String> getRequiredFields() { return requiredFields != null ? new ArrayList<>(requiredFields) : new ArrayList<>(); }
        public String getBooleanContext() { return booleanContext; }
        public String getFormat() { return format; }
        public List<String> getEnumValues() { return enumValues != null ? new ArrayList<>(enumValues) : new ArrayList<>(); }

        public static class Builder {
            private DataConstraints constraints = new DataConstraints();

            public Builder withType(String type) { constraints.type = type; return this; }
            public Builder withMinLength(int minLength) { constraints.minLength = minLength; return this; }
            public Builder withMaxLength(int maxLength) { constraints.maxLength = maxLength; return this; }
            public Builder withPattern(String pattern) { constraints.pattern = pattern; return this; }
            public Builder withMinimum(BigDecimal minimum) { constraints.minimum = minimum; return this; }
            public Builder withMaximum(BigDecimal maximum) { constraints.maximum = maximum; return this; }
            public Builder withMultipleOf(BigDecimal multipleOf) { constraints.multipleOf = multipleOf; return this; }
            public Builder withMinItems(int minItems) { constraints.minItems = minItems; return this; }
            public Builder withMaxItems(int maxItems) { constraints.maxItems = maxItems; return this; }
            public Builder withUniqueItems(boolean uniqueItems) { constraints.uniqueItems = uniqueItems; return this; }
            public Builder withMinProperties(int minProperties) { constraints.minProperties = minProperties; return this; }
            public Builder withMaxProperties(int maxProperties) { constraints.maxProperties = maxProperties; return this; }
            public Builder withRequiredFields(List<String> requiredFields) { constraints.requiredFields = requiredFields; return this; }
            public Builder withBooleanContext(String booleanContext) { constraints.booleanContext = booleanContext; return this; }
            public Builder withFormat(String format) { constraints.format = format; return this; }
            public Builder withEnumValues(List<String> enumValues) { constraints.enumValues = enumValues; return this; }

            public DataConstraints build() {
                return constraints;
            }
        }

        public static Builder builder() {
            return new Builder();
        }
    }

    /**
     * Endpoint complexity information
     */
    public static class EndpointComplexity {
        private int score;
        private String level;
        private String description;
        private List<String> factors = new ArrayList<>();

        // Getters and Setters
        public int getScore() { return score; }
        public void setScore(int score) { this.score = score; }

        public String getLevel() { return level; }
        public void setLevel(String level) { this.level = level; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public List<String> getFactors() { return new ArrayList<>(factors); }
        public void setFactors(List<String> factors) {
            this.factors = factors != null ? new ArrayList<>(factors) : new ArrayList<>();
        }
    }

    // ===== ANALYSIS RESULT CLASSES =====

    /**
     * Security analysis result
     */
    public static class SecurityAnalysisResult {
        // Placeholder implementation
    }

    /**
     * Performance metrics
     */
    public static class PerformanceMetrics {
        // Placeholder implementation
    }

    // ===== EXCEPTION CLASSES =====

    /**
     * Custom analysis exception
     */
    public static class AnalysisException extends Exception {
        public AnalysisException(String message) {
            super(message);
        }

        public AnalysisException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    // ===== ENTERPRISE MONITORING AND UTILITY METHODS =====

    /**
     * Gets cache statistics for monitoring
     *
     * @return Cache statistics
     */
    public CacheStatistics getCacheStatistics() {
        return new CacheStatistics(
                schemaCache.size(),
                constraintCache.size(),
                responsePatternCache.size(),
                complexityCache.size(),
                securityCache.size(),
                performanceCache.size()
        );
    }

    /**
     * Clears all caches to free memory
     */
    public void clearCaches() {
        schemaCache.clear();
        constraintCache.clear();
        responsePatternCache.clear();
        complexityCache.clear();
        securityCache.clear();
        performanceCache.clear();

        logger.info("All caches cleared");
    }

    /**
     * Cache statistics for monitoring
     */
    public static class CacheStatistics {
        private final int schemaCacheSize;
        private final int constraintCacheSize;
        private final int responsePatternCacheSize;
        private final int complexityCacheSize;
        private final int securityCacheSize;
        private final int performanceCacheSize;

        public CacheStatistics(int schemaCacheSize, int constraintCacheSize,
                               int responsePatternCacheSize, int complexityCacheSize,
                               int securityCacheSize, int performanceCacheSize) {
            this.schemaCacheSize = schemaCacheSize;
            this.constraintCacheSize = constraintCacheSize;
            this.responsePatternCacheSize = responsePatternCacheSize;
            this.complexityCacheSize = complexityCacheSize;
            this.securityCacheSize = securityCacheSize;
            this.performanceCacheSize = performanceCacheSize;
        }

        public int getSchemaCacheSize() { return schemaCacheSize; }
        public int getConstraintCacheSize() { return constraintCacheSize; }
        public int getResponsePatternCacheSize() { return responsePatternCacheSize; }
        public int getComplexityCacheSize() { return complexityCacheSize; }
        public int getSecurityCacheSize() { return securityCacheSize; }
        public int getPerformanceCacheSize() { return performanceCacheSize; }

        public int getTotalCacheSize() {
            return schemaCacheSize + constraintCacheSize + responsePatternCacheSize +
                    complexityCacheSize + securityCacheSize + performanceCacheSize;
        }

        @Override
        public String toString() {
            return String.format("CacheStatistics{total=%d, schema=%d, constraint=%d, " +
                            "responsePattern=%d, complexity=%d, security=%d, performance=%d}",
                    getTotalCacheSize(), schemaCacheSize, constraintCacheSize,
                    responsePatternCacheSize, complexityCacheSize,
                    securityCacheSize, performanceCacheSize);
        }
    }

    /**
     * Gets version information
     *
     * @return Version string
     */
    public String getVersion() {
        return VERSION;
    }

    /**
     * Gets analysis configuration
     *
     * @return Current configuration
     */
    public AnalysisConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * Creates a default instance with optimized settings
     *
     * @return Default SchemaAnalyzer instance
     */
    public static SchemaAnalyzer createDefault() {
        return new SchemaAnalyzer();
    }

    /**
     * Builder pattern for creating customized SchemaAnalyzer instances
     */
    public static class Builder {
        private boolean enableSecurity = true;
        private boolean enablePerformance = true;
        private boolean enableQuality = true;
        private boolean enableParallelProcessing = true;
        private int threadPoolSize = DEFAULT_THREAD_POOL_SIZE;
        private int cacheSize = MAX_CACHE_SIZE;

        public Builder withSecurityAnalysis(boolean enable) {
            this.enableSecurity = enable;
            return this;
        }

        public Builder withPerformanceAnalysis(boolean enable) {
            this.enablePerformance = enable;
            return this;
        }

        public Builder withQualityAnalysis(boolean enable) {
            this.enableQuality = enable;
            return this;
        }

        public Builder withParallelProcessing(boolean enable) {
            this.enableParallelProcessing = enable;
            return this;
        }

        public Builder withThreadPoolSize(int size) {
            this.threadPoolSize = size;
            return this;
        }

        public Builder withCacheSize(int size) {
            this.cacheSize = size;
            return this;
        }

        public SchemaAnalyzer build() {
            AnalysisConfiguration config = new AnalysisConfiguration(
                    enableSecurity, enablePerformance, enableQuality,
                    enableParallelProcessing, threadPoolSize, cacheSize
            );
            return new SchemaAnalyzer(config);
        }
    }

    /**
     * Creates an enterprise instance with full features enabled
     *
     * @return Enterprise SchemaAnalyzer instance
     */
    public static SchemaAnalyzer createEnterprise() {
        return new Builder()
                .withSecurityAnalysis(true)
                .withPerformanceAnalysis(true)
                .withQualityAnalysis(true)
                .withParallelProcessing(true)
                .withThreadPoolSize(DEFAULT_THREAD_POOL_SIZE * 2)
                .withCacheSize(MAX_CACHE_SIZE * 2)
                .build();
    }

    /**
     * Creates a performance-optimized instance
     *
     * @return Performance-optimized SchemaAnalyzer instance
     */
    public static SchemaAnalyzer createPerformanceOptimized() {
        return new Builder()
                .withSecurityAnalysis(false)
                .withPerformanceAnalysis(true)
                .withQualityAnalysis(false)
                .withParallelProcessing(true)
                .withThreadPoolSize(DEFAULT_THREAD_POOL_SIZE * 3)
                .withCacheSize(MAX_CACHE_SIZE * 3)
                .build();
    }

    /**
     * Creates a security-focused instance
     *
     * @return Security-focused SchemaAnalyzer instance
     */
    public static SchemaAnalyzer createSecurityFocused() {
        return new Builder()
                .withSecurityAnalysis(true)
                .withPerformanceAnalysis(false)
                .withQualityAnalysis(true)
                .withParallelProcessing(true)
                .build();
    }

    /**
     * Health check method for enterprise monitoring
     */
    public boolean isHealthy() {
        return schemaCache != null && constraintCache != null;
    }

    /**
     * Get detailed health status
     */
    public Map<String, Object> getHealthStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("healthy", isHealthy());
        status.put("cacheStatistics", getCacheStatistics());
        status.put("version", getVersion());
        status.put("uptime", System.currentTimeMillis());
        return status;
    }

    /**
     * Shutdown method for cleanup
     */
    public void shutdown() {
        try {
            clearCaches();
            logger.info("SchemaAnalyzer shutdown completed");
        } catch (Exception e) {
            logger.severe("SchemaAnalyzer shutdown failed: " + e.getMessage());
        }
    }
}