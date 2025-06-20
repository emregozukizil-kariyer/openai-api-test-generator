package org.example.openapi;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * ENTERPRISE RequestBodyInfo - Standard Interface Implementation
 *
 * Fully compliant with TestStrategyManager.java standards:
 * - Standard field names and types aligned with EndpointInfo
 * - Builder pattern implementation
 * - Enterprise monitoring and logging
 * - Advanced test generation capabilities
 * - Security-aware payload generation
 * - Performance optimization features
 *
 * @version 2.0
 * @since 1.0
 */
public class RequestBodyInfo {

    // ===== STATIC FIELDS =====
    static final Logger logger = Logger.getLogger(RequestBodyInfo.class.getName());
    private static final Map<String, AnalysisCache> analysisCache = new ConcurrentHashMap<>();

    // ===== STANDARD INTERFACE FIELDS (Aligned with TestStrategyManager) =====

    // Core Request Body Information
    private boolean required = false;
    private String description;
    private String exampleData;
    private Object defaultValue;

    // Standard Content Type Configuration
    private Set<String> supportedContentTypes = new HashSet<>();
    private String primaryContentType = "application/json";
    private Map<String, MediaTypeInfo> mediaTypeDetails = new HashMap<>();

    // Standard Schema and Constraints (Aligned with EndpointInfo pattern)
    private DataConstraints dataConstraints;
    private Map<String, DataConstraints> propertyConstraints = new HashMap<>();
    private RequestBodySchema schema;
    private List<String> requiredProperties = new ArrayList<>();

    // Standard Test Generation Settings (Aligned with GeneratedTestCase)
    private TestComplexity testComplexity = TestComplexity.STANDARD;
    private Set<TestGenerationScenario> enabledTestScenarios = new HashSet<>();
    private int maxTestVariations = 20;
    private boolean generateNegativeTests = true;
    private int complexity = 1;
    private int priority = 3;
    private Set<String> tags = new HashSet<>();

    // Standard Security Configuration (Aligned with SecurityProfile)
    private SecuritySensitivity securitySensitivity = SecuritySensitivity.NORMAL;
    private Set<SecurityRisk> securityRisks = new HashSet<>();
    private List<ValidationRule> customValidationRules = new ArrayList<>();

    // Standard File Upload Support
    private boolean supportsFileUpload = false;
    private Set<String> allowedFileTypes = new HashSet<>();
    private long maxFileSize = 10 * 1024 * 1024; // 10MB default
    private boolean allowMultipleFiles = false;

    // Standard Enterprise Features
    private boolean supportsBulkOperations = false;
    private boolean supportsPartialUpdates = false;
    private boolean requiresIdempotency = false;
    private Map<String, Object> metadata = new HashMap<>();

    // Standard Tracking & Monitoring
    private Instant creationTimestamp;
    private Instant lastModified;
    private String version = "1.0";
    private String executionId;

    // ===== STANDARD ENUMS (Aligned with TestStrategyManager) =====

    public enum TestComplexity {
        MINIMAL(5, "Basic validation tests only", StrategyType.FUNCTIONAL_BASIC),
        STANDARD(15, "Standard test coverage", StrategyType.FUNCTIONAL_COMPREHENSIVE),
        COMPREHENSIVE(30, "Comprehensive test coverage", StrategyType.FUNCTIONAL_COMPREHENSIVE),
        EXHAUSTIVE(50, "Maximum test coverage", StrategyType.ADVANCED_AI_DRIVEN);

        private final int maxTestCount;
        private final String description;
        private final StrategyType recommendedStrategy;

        TestComplexity(int maxTestCount, String description, StrategyType recommendedStrategy) {
            this.maxTestCount = maxTestCount;
            this.description = description;
            this.recommendedStrategy = recommendedStrategy;
        }

        public int getMaxTestCount() { return maxTestCount; }
        public String getDescription() { return description; }
        public StrategyType getRecommendedStrategy() { return recommendedStrategy; }
    }

    public enum SecuritySensitivity {
        NONE("No security concerns", StrategyType.FUNCTIONAL_BASIC),
        LOW("Low security impact", StrategyType.FUNCTIONAL_BASIC),
        NORMAL("Standard security testing", StrategyType.SECURITY_BASIC),
        HIGH("High security impact", StrategyType.SECURITY_OWASP_TOP10),
        CRITICAL("Critical security testing required", StrategyType.SECURITY_PENETRATION);

        private final String description;
        private final StrategyType recommendedStrategy;

        SecuritySensitivity(String description, StrategyType recommendedStrategy) {
            this.description = description;
            this.recommendedStrategy = recommendedStrategy;
        }

        public String getDescription() { return description; }
        public StrategyType getRecommendedStrategy() { return recommendedStrategy; }
    }

    public enum SecurityRisk {
        SQL_INJECTION("SQL injection vulnerability", TestGenerationScenario.SQL_INJECTION_BASIC),
        XSS_ATTACK("Cross-site scripting", TestGenerationScenario.XSS_REFLECTED),
        XXE_ATTACK("XML external entity", TestGenerationScenario.XML_EXTERNAL_ENTITY),
        DESERIALIZATION("Unsafe deserialization", TestGenerationScenario.DESERIALIZATION_ATTACK),
        FILE_UPLOAD_ABUSE("File upload abuse", TestGenerationScenario.FILE_UPLOAD_MALICIOUS),
        BUFFER_OVERFLOW("Buffer overflow", TestGenerationScenario.BUFFER_OVERFLOW_TEST),
        DATA_EXPOSURE("Sensitive data exposure", TestGenerationScenario.DATA_EXPOSURE_TEST),
        PRIVILEGE_ESCALATION("Privilege escalation", TestGenerationScenario.PRIVILEGE_ESCALATION);

        private final String description;
        private final TestGenerationScenario associatedScenario;

        SecurityRisk(String description, TestGenerationScenario associatedScenario) {
            this.description = description;
            this.associatedScenario = associatedScenario;
        }

        public String getDescription() { return description; }
        public TestGenerationScenario getAssociatedScenario() { return associatedScenario; }
    }

    // ===== LEGACY TEST SCENARIO ENUM =====
    public enum TestScenario {
        VALID_MINIMAL, VALID_COMPLETE, VALID_BOUNDARY, INVALID_REQUIRED_MISSING,
        INVALID_TYPE_MISMATCH, INVALID_FORMAT, INVALID_RANGE, INVALID_EMPTY,
        INVALID_NULL, INVALID_MALFORMED, SECURITY_INJECTION, SECURITY_XSS,
        SECURITY_XXE, PERFORMANCE_LARGE, UNICODE_HANDLING, SPECIAL_CHARACTERS,
        NESTED_VALIDATION, ARRAY_VALIDATION, FILE_UPLOAD
    }

    // ===== STANDARD CONSTRUCTORS =====

    public RequestBodyInfo() {
        initializeDefaults();
        logCreation();
    }

    public RequestBodyInfo(boolean required, String description) {
        this();
        this.required = required;
        this.description = description;
        this.lastModified = Instant.now();

        performAdvancedAnalysis();
        validateConfiguration();
    }

    private void initializeDefaults() {
        this.creationTimestamp = Instant.now();
        this.lastModified = Instant.now();
        this.executionId = generateAdvancedExecutionId();

        // Default content types
        supportedContentTypes.add("application/json");
        supportedContentTypes.add("application/xml");
        supportedContentTypes.add("application/x-www-form-urlencoded");

        // Standard test scenarios (aligned with TestGenerationScenario)
        enabledTestScenarios.add(TestGenerationScenario.HAPPY_PATH);
        enabledTestScenarios.add(TestGenerationScenario.ERROR_HANDLING);

        // Default media type details
        mediaTypeDetails.put("application/json",
                new MediaTypeInfo("application/json", "JSON", true, true));
        mediaTypeDetails.put("application/xml",
                new MediaTypeInfo("application/xml", "XML", true, false));
        mediaTypeDetails.put("application/x-www-form-urlencoded",
                new MediaTypeInfo("application/x-www-form-urlencoded", "Form Data", false, false));

        // Initialize enterprise tracking
        metadata.put("analysisVersion", "2.0");
        metadata.put("generationMethod", "ADVANCED");
    }

    private void logCreation() {
        logger.info(String.format("Creating RequestBodyInfo: required=%s, executionId=%s",
                required, executionId));
    }

    private String generateAdvancedExecutionId() {
        return "RBI_" + System.currentTimeMillis() + "_" +
                Integer.toHexString(System.identityHashCode(this)).toUpperCase();
    }

    // ===== STANDARD BUILDER PATTERN =====

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final RequestBodyInfo requestBody;

        private Builder() {
            this.requestBody = new RequestBodyInfo();
        }

        public Builder withRequired(boolean required) {
            requestBody.required = required;
            return this;
        }

        public Builder withDescription(String description) {
            requestBody.description = description;
            return this;
        }

        public Builder withDataConstraints(DataConstraints constraints) {
            requestBody.dataConstraints = constraints;
            return this;
        }

        public Builder withExample(String exampleData) {
            requestBody.exampleData = exampleData;
            return this;
        }

        public Builder withContentType(String contentType) {
            requestBody.supportedContentTypes.add(contentType);
            requestBody.primaryContentType = contentType;
            return this;
        }

        public Builder withSchema(RequestBodySchema schema) {
            requestBody.schema = schema;
            return this;
        }

        public Builder withTestComplexity(TestComplexity complexity) {
            requestBody.testComplexity = complexity;
            requestBody.maxTestVariations = complexity.getMaxTestCount();
            return this;
        }

        public Builder withSecuritySensitivity(SecuritySensitivity sensitivity) {
            requestBody.securitySensitivity = sensitivity;
            return this;
        }

        public Builder withFileUpload(String... allowedTypes) {
            requestBody.supportsFileUpload = true;
            requestBody.allowedFileTypes.addAll(Arrays.asList(allowedTypes));
            requestBody.securitySensitivity = SecuritySensitivity.HIGH;
            requestBody.securityRisks.add(SecurityRisk.FILE_UPLOAD_ABUSE);
            return this;
        }

        public Builder withBulkOperations(boolean enabled) {
            requestBody.supportsBulkOperations = enabled;
            if (enabled) {
                requestBody.securityRisks.add(SecurityRisk.BUFFER_OVERFLOW);
                requestBody.enabledTestScenarios.add(TestGenerationScenario.LOAD_TESTING_LIGHT);
            }
            return this;
        }

        public Builder withComplexity(int complexity) {
            requestBody.complexity = complexity;
            return this;
        }

        public Builder withPriority(int priority) {
            requestBody.priority = priority;
            return this;
        }

        public Builder withTags(Set<String> tags) {
            requestBody.tags = new HashSet<>(tags);
            return this;
        }

        public Builder withMetadata(Map<String, Object> metadata) {
            requestBody.metadata.putAll(metadata);
            return this;
        }

        public RequestBodyInfo build() {
            validateConfiguration(requestBody);
            requestBody.performAdvancedAnalysis();
            requestBody.lastModified = Instant.now();

            logger.info(String.format("Built RequestBodyInfo: %s with %d scenarios, complexity=%d",
                    requestBody.executionId, requestBody.enabledTestScenarios.size(), requestBody.complexity));

            return requestBody;
        }

        private void validateConfiguration(RequestBodyInfo requestBody) {
            List<String> errors = new ArrayList<>();

            if (requestBody.supportedContentTypes.isEmpty()) {
                errors.add("At least one content type must be supported");
            }

            if (requestBody.maxTestVariations <= 0) {
                errors.add("Max test variations must be positive");
            }

            if (requestBody.maxFileSize <= 0 && requestBody.supportsFileUpload) {
                errors.add("Max file size must be positive for file upload support");
            }

            if (!errors.isEmpty()) {
                String errorMessage = "RequestBodyInfo validation failed: " + String.join(", ", errors);
                logger.severe(errorMessage);
                throw new IllegalArgumentException(errorMessage);
            }
        }
    }

    // ===== STANDARD ANALYSIS METHODS =====

    private void performAdvancedAnalysis() {
        try {
            analyzeSecurityImplications();
            updateTestScenariosFromConfiguration();
            updateComplexityAndPriority();
            generateTags();

            logger.fine(String.format("Advanced analysis completed for RequestBodyInfo: %s", executionId));
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error during request body analysis", e);
        }
    }

    private void analyzeSecurityImplications() {
        // Content type security analysis
        for (String contentType : supportedContentTypes) {
            analyzeContentTypeSecurity(contentType);
        }

        // Feature-based security analysis
        if (supportsFileUpload) {
            securityRisks.add(SecurityRisk.FILE_UPLOAD_ABUSE);
            securitySensitivity = SecuritySensitivity.HIGH;
            tags.add("file-upload");
        }

        if (supportsBulkOperations) {
            securityRisks.add(SecurityRisk.BUFFER_OVERFLOW);
            tags.add("bulk-operations");
        }

        // Update scenarios based on security risks
        for (SecurityRisk risk : securityRisks) {
            enabledTestScenarios.add(risk.getAssociatedScenario());
        }
    }

    private void analyzeContentTypeSecurity(String contentType) {
        switch (contentType.toLowerCase()) {
            case "application/xml":
            case "text/xml":
                securityRisks.add(SecurityRisk.XXE_ATTACK);
                enabledTestScenarios.add(TestGenerationScenario.XML_EXTERNAL_ENTITY);
                tags.add("xml-support");
                break;
            case "application/json":
                securityRisks.add(SecurityRisk.DESERIALIZATION);
                tags.add("json-support");
                break;
            case "multipart/form-data":
                securityRisks.add(SecurityRisk.FILE_UPLOAD_ABUSE);
                supportsFileUpload = true;
                tags.add("multipart-support");
                break;
            case "application/x-www-form-urlencoded":
                tags.add("form-support");
                break;
        }
    }

    private void updateTestScenariosFromConfiguration() {
        // Data constraint-based scenarios
        if (dataConstraints != null) {
            if (dataConstraints.getMinimum() != null || dataConstraints.getMaximum() != null) {
                enabledTestScenarios.add(TestGenerationScenario.BOUNDARY_VALUE_ANALYSIS);
            }

            if (dataConstraints.getPattern() != null) {
                enabledTestScenarios.add(TestGenerationScenario.REGEX_PATTERN_TESTING);
            }

            if (!dataConstraints.getAllowNull()) {
                enabledTestScenarios.add(TestGenerationScenario.NULL_VALUE_TESTING);
            }
        }

        // Security-based scenarios
        switch (securitySensitivity) {
            case CRITICAL:
            case HIGH:
                enabledTestScenarios.add(TestGenerationScenario.SQL_INJECTION_BASIC);
                enabledTestScenarios.add(TestGenerationScenario.XSS_REFLECTED);
                if (supportedContentTypes.contains("application/xml")) {
                    enabledTestScenarios.add(TestGenerationScenario.XML_EXTERNAL_ENTITY);
                }
                break;
            case NORMAL:
                enabledTestScenarios.add(TestGenerationScenario.INPUT_VALIDATION_BASIC);
                break;
        }

        // Schema-based scenarios
        if (schema != null) {
            if (schema.hasNestedObjects()) {
                enabledTestScenarios.add(TestGenerationScenario.NESTED_OBJECT_TESTING);
            }
            if (schema.hasArrayProperties()) {
                enabledTestScenarios.add(TestGenerationScenario.ARRAY_BOUNDARY_TESTING);
            }
        }
    }

    private void updateComplexityAndPriority() {
        complexity = calculateComplexityScore();
        priority = determinePriority();
    }

    private void generateTags() {
        // Content type tags
        if (supportedContentTypes.contains("application/json")) tags.add("json");
        if (supportedContentTypes.contains("application/xml")) tags.add("xml");
        if (supportedContentTypes.contains("multipart/form-data")) tags.add("multipart");

        // Feature tags
        if (required) tags.add("required");
        else tags.add("optional");

        if (supportsFileUpload) tags.add("file-upload");
        if (supportsBulkOperations) tags.add("bulk-operations");
        if (supportsPartialUpdates) tags.add("partial-updates");

        // Security tags
        tags.add("security-" + securitySensitivity.name().toLowerCase());

        // Complexity tags
        if (complexity > 30) tags.add("high-complexity");
        else if (complexity > 15) tags.add("medium-complexity");
        else tags.add("low-complexity");
    }

    // ===== STANDARD VALIDATION METHODS =====

    private void validateConfiguration() {
        validateConfiguration(this);
    }

    private static void validateConfiguration(RequestBodyInfo requestBody) {
        if (requestBody == null) {
            throw new IllegalArgumentException("RequestBodyInfo cannot be null");
        }

        List<String> errors = new ArrayList<>();

        if (requestBody.supportedContentTypes.isEmpty()) {
            errors.add("At least one content type must be supported");
        }

        if (requestBody.maxTestVariations <= 0) {
            errors.add("Max test variations must be positive");
        }

        if (requestBody.supportsFileUpload && requestBody.maxFileSize <= 0) {
            errors.add("Max file size must be positive for file upload support");
        }

        if (!errors.isEmpty()) {
            String errorMessage = "RequestBodyInfo validation failed: " + String.join(", ", errors);
            logger.severe(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
    }

    // ===== STANDARD TEST GENERATION METHODS =====

    public List<GeneratedTestCase> generateTestCases(EndpointInfo endpoint) {
        return generateTestCases(endpoint, null);
    }

    public List<GeneratedTestCase> generateTestCases(EndpointInfo endpoint,
                                                     AdvancedStrategyRecommendation recommendation) {
        List<GeneratedTestCase> testCases = new ArrayList<>();

        try {
            for (TestGenerationScenario scenario : enabledTestScenarios) {
                testCases.addAll(generateTestCasesForScenario(scenario, endpoint, recommendation));
            }

            // Limit and prioritize test cases
            if (testCases.size() > maxTestVariations) {
                testCases = prioritizeAndLimitTestCases(testCases);
            }

            logger.info(String.format("Generated %d test cases for RequestBodyInfo: %s",
                    testCases.size(), executionId));

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error generating test cases for RequestBodyInfo: " + executionId, e);
        }

        return testCases;
    }

    private List<GeneratedTestCase> generateTestCasesForScenario(TestGenerationScenario scenario,
                                                                 EndpointInfo endpoint,
                                                                 AdvancedStrategyRecommendation recommendation) {
        List<GeneratedTestCase> testCases = new ArrayList<>();

        try {
            StrategyType strategyType = recommendation != null ?
                    recommendation.getPrimaryStrategy() :
                    determineStrategyFromScenario(scenario);

            List<RequestBodyTestCase> requestBodyTests = generateRequestBodyTestCasesForScenario(scenario);

            for (RequestBodyTestCase rbTest : requestBodyTests) {
                GeneratedTestCase testCase = GeneratedTestCase.builder()
                        .withTestId(generateAdvancedTestId(scenario, rbTest))
                        .withTestName(generateTestName(scenario, rbTest))
                        .withDescription(generateTestDescription(scenario, rbTest))
                        .withScenario(scenario)
                        .withStrategyType(strategyType)
                        .withEndpoint(endpoint)
                        .withTestSteps(generateTestSteps(rbTest))
                        .withTestData(generateTestDataSet(rbTest))
                        .withAssertions(generateTestAssertions(rbTest))
                        .withPriority(priority)
                        .withEstimatedDuration(estimateTestDuration(rbTest))
                        .withComplexity(complexity)
                        .withTags(new HashSet<>(tags))
                        .build();

                testCases.add(testCase);
            }

        } catch (Exception e) {
            logger.log(Level.WARNING,
                    String.format("Error generating test cases for scenario %s", scenario), e);
        }

        return testCases;
    }

    // ===== TEST CASE GENERATION METHODS =====

    private List<RequestBodyTestCase> generateXssTestCases() {
        List<RequestBodyTestCase> cases = new ArrayList<>();

        String[] xssPayloads = {
                "<script>alert('XSS')</script>",
                "<img src=x onerror=alert('XSS')>",
                "javascript:alert('XSS')",
                "<svg onload=alert('XSS')>"
        };

        for (String payload : xssPayloads) {
            String maliciousJson = String.format("{ \"description\": \"%s\" }",
                    payload.replace("\"", "\\\""));
            cases.add(new RequestBodyTestCase(
                    "XSS attack attempt",
                    maliciousJson,
                    "application/json",
                    TestScenario.SECURITY_XSS,
                    false,
                    "XSS payload in request body"
            ));
        }

        return cases;
    }

    private List<RequestBodyTestCase> generateXxeTestCases() {
        List<RequestBodyTestCase> cases = new ArrayList<>();

        if (supportedContentTypes.contains("application/xml")) {
            String xxePayload = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                    "<!DOCTYPE root [<!ENTITY xxe SYSTEM \"file:///etc/passwd\">]>" +
                    "<root><name>&xxe;</name></root>";

            cases.add(new RequestBodyTestCase(
                    "XXE attack attempt",
                    xxePayload,
                    "application/xml",
                    TestScenario.SECURITY_XXE,
                    false,
                    "XML External Entity attack payload"
            ));
        }

        return cases;
    }

    private List<RequestBodyTestCase> generatePerformanceTestCases() {
        List<RequestBodyTestCase> cases = new ArrayList<>();

        // Large JSON payload
        StringBuilder largeJson = new StringBuilder("{");
        for (int i = 0; i < 1000; i++) {
            if (i > 0) largeJson.append(",");
            largeJson.append("\"field").append(i).append("\": \"")
                    .append("A".repeat(100)).append("\"");
        }
        largeJson.append("}");

        cases.add(new RequestBodyTestCase(
                "Large JSON payload",
                largeJson.toString(),
                "application/json",
                TestScenario.PERFORMANCE_LARGE,
                true,
                "Large JSON payload for performance testing"
        ));

        return cases;
    }

    private List<GeneratedTestCase> prioritizeAndLimitTestCases(List<GeneratedTestCase> testCases) {
        // Priority order: Security > Invalid > Valid
        Map<TestGenerationScenario, Integer> priorities = new HashMap<>();
        priorities.put(TestGenerationScenario.SQL_INJECTION_BASIC, 1);
        priorities.put(TestGenerationScenario.XSS_REFLECTED, 1);
        priorities.put(TestGenerationScenario.XML_EXTERNAL_ENTITY, 1);
        priorities.put(TestGenerationScenario.ERROR_HANDLING, 2);
        priorities.put(TestGenerationScenario.HAPPY_PATH, 3);

        return testCases.stream()
                .sorted((a, b) -> {
                    int priorityA = priorities.getOrDefault(a.getScenario(), 4);
                    int priorityB = priorities.getOrDefault(b.getScenario(), 4);
                    int priorityCompare = Integer.compare(priorityA, priorityB);

                    if (priorityCompare != 0) return priorityCompare;
                    return Integer.compare(b.getPriority(), a.getPriority());
                })
                .limit(maxTestVariations)
                .collect(Collectors.toList());
    }

    // ===== STANDARD PAYLOAD GENERATION METHODS =====

    private String generateValidPayload(String contentType) {
        switch (contentType.toLowerCase()) {
            case "application/json":
                return generateValidJsonPayload();
            case "application/xml":
                return generateValidXmlPayload();
            case "application/x-www-form-urlencoded":
                return generateValidFormPayload();
            default:
                return "{}";
        }
    }

    private String generateValidJsonPayload() {
        if (exampleData != null && !exampleData.trim().isEmpty()) {
            return exampleData;
        }

        StringBuilder json = new StringBuilder("{");
        boolean first = true;

        // Add required properties
        for (String reqProp : requiredProperties) {
            if (!first) json.append(",");
            json.append("\"").append(reqProp).append("\":");

            DataConstraints propConstraints = propertyConstraints.get(reqProp);
            if (propConstraints != null) {
                json.append(generateValueForConstraints(propConstraints));
            } else {
                json.append("\"test-value\"");
            }
            first = false;
        }

        // Add some default properties if none defined
        if (requiredProperties.isEmpty()) {
            json.append("\"id\":1,");
            json.append("\"name\":\"Test Object\",");
            json.append("\"active\":true,");
            json.append("\"timestamp\":\"").append(Instant.now()).append("\"");
        }

        json.append("}");
        return json.toString();
    }

    private String generateValidXmlPayload() {
        StringBuilder xml = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?><root>");

        for (String reqProp : requiredProperties) {
            xml.append("<").append(reqProp).append(">");

            DataConstraints propConstraints = propertyConstraints.get(reqProp);
            if (propConstraints != null) {
                String value = generateValueForConstraints(propConstraints);
                // Remove JSON quotes for XML
                value = value.replaceAll("^\"|\"$", "");
                xml.append(value);
            } else {
                xml.append("test-value");
            }
            xml.append("</").append(reqProp).append(">");
        }

        if (requiredProperties.isEmpty()) {
            xml.append("<id>1</id>");
            xml.append("<name>Test Object</name>");
            xml.append("<active>true</active>");
        }

        xml.append("</root>");
        return xml.toString();
    }

    private String generateValidFormPayload() {
        StringBuilder form = new StringBuilder();
        boolean first = true;

        for (String reqProp : requiredProperties) {
            if (!first) form.append("&");
            form.append(reqProp).append("=");

            DataConstraints propConstraints = propertyConstraints.get(reqProp);
            if (propConstraints != null) {
                String value = generateValueForConstraints(propConstraints);
                // Remove JSON quotes for form data
                value = value.replaceAll("^\"|\"$", "");
                form.append(value);
            } else {
                form.append("test-value");
            }
            first = false;
        }

        if (requiredProperties.isEmpty()) {
            form.append("id=1&name=Test+Object&active=true");
        }

        return form.toString();
    }

    private String generateValueForConstraints(DataConstraints constraints) {
        if (constraints == null) return "\"test-value\"";

        String type = constraints.getType();
        if (type == null) return "\"test-value\"";

        switch (type.toLowerCase()) {
            case "string":
                return generateStringValue(constraints);
            case "integer":
                return generateIntegerValue(constraints);
            case "number":
                return generateNumberValue(constraints);
            case "boolean":
                return "true";
            case "array":
                return "[\"item1\",\"item2\",\"item3\"]";
            case "object":
                return "{\"nested\":\"value\"}";
            default:
                return "\"test-value\"";
        }
    }

    private String generateStringValue(DataConstraints constraints) {
        if (constraints.getFormat() != null) {
            switch (constraints.getFormat().toLowerCase()) {
                case "email": return "\"test@example.com\"";
                case "uri": return "\"https://example.com\"";
                case "uuid": return "\"123e4567-e89b-12d3-a456-426614174000\"";
                case "date": return "\"2023-01-01\"";
                case "date-time": return "\"2023-01-01T00:00:00Z\"";
                default: return "\"test-string\"";
            }
        }

        if (constraints.getMaxLength() != null) {
            int length = Math.min(constraints.getMaxLength(), 50);
            return "\"" + "A".repeat(Math.max(1, length)) + "\"";
        }

        return "\"test-string\"";
    }

    private String generateIntegerValue(DataConstraints constraints) {
        if (constraints.getMaximum() != null) {
            return constraints.getMaximum().toString();
        }
        if (constraints.getMinimum() != null) {
            return constraints.getMinimum().toString();
        }
        return "42";
    }

    private String generateNumberValue(DataConstraints constraints) {
        if (constraints.getMaximum() != null) {
            return constraints.getMaximum().toString();
        }
        if (constraints.getMinimum() != null) {
            return constraints.getMinimum().toString();
        }
        return "42.5";
    }

    private String getContentTypeDescription(String contentType) {
        switch (contentType.toLowerCase()) {
            case "application/json": return "JSON";
            case "application/xml": return "XML";
            case "application/x-www-form-urlencoded": return "Form Data";
            case "multipart/form-data": return "Multipart Form";
            default: return contentType;
        }
    }

    // ===== STANDARD ANALYSIS METHODS =====

    public AdvancedRequestBodyAnalysis analyzeComprehensively() {
        return new AdvancedRequestBodyAnalysis(this);
    }

    public AdvancedStrategyRecommendation recommendAdvancedStrategy(EndpointInfo endpoint) {
        return new AdvancedStrategyRecommendation(this, endpoint);
    }

    public int calculateComplexityScore() {
        int score = 1; // Base score

        // Content type complexity
        score += supportedContentTypes.size() * 5;

        // Property complexity
        score += requiredProperties.size() * 3;
        score += propertyConstraints.size() * 2;

        // Security complexity
        score += securityRisks.size() * 10;
        if (securitySensitivity.ordinal() >= SecuritySensitivity.HIGH.ordinal()) {
            score += 20;
        }

        // Feature complexity
        if (supportsFileUpload) score += 15;
        if (supportsBulkOperations) score += 10;
        if (schema != null && schema.hasNestedObjects()) score += 15;
        if (schema != null && schema.hasArrayProperties()) score += 10;

        // Test scenario complexity
        score += enabledTestScenarios.size() * 2;

        return Math.min(score, 100);
    }

    private int determinePriority() {
        int calculatedPriority = 3; // Default priority

        // Adjust for security sensitivity
        if (securitySensitivity.ordinal() >= SecuritySensitivity.HIGH.ordinal()) {
            calculatedPriority = Math.max(calculatedPriority, 4);
        }

        // Adjust for required status
        if (required) {
            calculatedPriority = Math.max(calculatedPriority, 4);
        }

        // Adjust for critical features
        if (supportsFileUpload || supportsBulkOperations) {
            calculatedPriority = 5;
        }

        return calculatedPriority;
    }

    public int estimateTestCaseCount() {
        int estimatedCount = 0;

        for (TestGenerationScenario scenario : enabledTestScenarios) {
            switch (scenario) {
                case HAPPY_PATH:
                case ERROR_HANDLING:
                    estimatedCount += supportedContentTypes.size();
                    break;
                case SQL_INJECTION_BASIC:
                case XSS_REFLECTED:
                    estimatedCount += 4; // Fixed number of security payloads
                    break;
                case XML_EXTERNAL_ENTITY:
                    estimatedCount += supportedContentTypes.contains("application/xml") ? 2 : 0;
                    break;
                case LOAD_TESTING_LIGHT:
                    estimatedCount += 2;
                    break;
                default:
                    estimatedCount += 2; // Default estimate
            }
        }

        return Math.min(estimatedCount, maxTestVariations);
    }

    // ===== STANDARD UTILITY METHODS =====

    public boolean hasSecurityRisks() {
        return !securityRisks.isEmpty();
    }

    public boolean requiresSecurityTesting() {
        return securitySensitivity.ordinal() >= SecuritySensitivity.HIGH.ordinal() || hasSecurityRisks();
    }

    public boolean isHighComplexity() {
        return calculateComplexityScore() > 50;
    }

    public boolean supportsContentType(String contentType) {
        return supportedContentTypes.contains(contentType);
    }

    public int getPropertyCount() {
        return propertyConstraints.size();
    }

    public int getRequiredPropertyCount() {
        return requiredProperties.size();
    }

    // ===== STANDARD GETTERS (Aligned with interface) =====

    public boolean isRequired() { return required; }
    public String getDescription() { return description; }
    public String getExampleData() { return exampleData; }
    public Object getDefaultValue() { return defaultValue; }
    public Set<String> getSupportedContentTypes() { return new HashSet<>(supportedContentTypes); }
    public String getPrimaryContentType() { return primaryContentType; }
    public Map<String, MediaTypeInfo> getMediaTypeDetails() { return new HashMap<>(mediaTypeDetails); }
    public DataConstraints getDataConstraints() { return dataConstraints; }
    public Map<String, DataConstraints> getPropertyConstraints() { return new HashMap<>(propertyConstraints); }
    public RequestBodySchema getSchema() { return schema; }
    public List<String> getRequiredProperties() { return new ArrayList<>(requiredProperties); }
    public TestComplexity getTestComplexity() { return testComplexity; }
    public Set<TestGenerationScenario> getEnabledTestScenarios() { return new HashSet<>(enabledTestScenarios); }
    public int getMaxTestVariations() { return maxTestVariations; }
    public boolean isGenerateNegativeTests() { return generateNegativeTests; }
    public int getComplexity() { return complexity; }
    public int getPriority() { return priority; }
    public Set<String> getTags() { return new HashSet<>(tags); }
    public SecuritySensitivity getSecuritySensitivity() { return securitySensitivity; }
    public Set<SecurityRisk> getSecurityRisks() { return new HashSet<>(securityRisks); }
    public List<ValidationRule> getCustomValidationRules() { return new ArrayList<>(customValidationRules); }
    public boolean isSupportsFileUpload() { return supportsFileUpload; }
    public Set<String> getAllowedFileTypes() { return new HashSet<>(allowedFileTypes); }
    public long getMaxFileSize() { return maxFileSize; }
    public boolean isAllowMultipleFiles() { return allowMultipleFiles; }
    public boolean isSupportsBulkOperations() { return supportsBulkOperations; }
    public boolean isSupportsPartialUpdates() { return supportsPartialUpdates; }
    public boolean isRequiresIdempotency() { return requiresIdempotency; }
    public Map<String, Object> getMetadata() { return new HashMap<>(metadata); }
    public Instant getCreationTimestamp() { return creationTimestamp; }
    public Instant getLastModified() { return lastModified; }
    public String getVersion() { return version; }
    public String getExecutionId() { return executionId; }

    // ===== STANDARD SETTERS =====

    public void setRequired(boolean required) {
        this.required = required;
        this.lastModified = Instant.now();
    }

    public void setDescription(String description) {
        this.description = description;
        this.lastModified = Instant.now();
    }

    public void setDataConstraints(DataConstraints dataConstraints) {
        this.dataConstraints = dataConstraints;
        this.lastModified = Instant.now();
        updateTestScenariosFromConfiguration();
    }

    // ===== STANDARD HELPER METHODS =====

    private String generateAdvancedTestId(TestGenerationScenario scenario, RequestBodyTestCase rbTest) {
        return String.format("rb_%s_%s_%s_%d",
                executionId.substring(executionId.length() - 8),
                scenario.name().toLowerCase(),
                rbTest.getContentType().replace("/", "_"),
                System.nanoTime() % 100000);
    }

    private String generateTestName(TestGenerationScenario scenario, RequestBodyTestCase rbTest) {
        return String.format("Test %s request body with %s scenario",
                rbTest.getContentType(), scenario.name().toLowerCase().replace("_", " "));
    }

    private String generateTestDescription(TestGenerationScenario scenario, RequestBodyTestCase rbTest) {
        return String.format("Validates request body (%s) with %s scenario. Expected: %s. %s",
                rbTest.getContentType(), scenario.name().toLowerCase().replace("_", " "),
                rbTest.shouldSucceed() ? "SUCCESS" : "FAILURE",
                rbTest.getDescription());
    }

    private List<TestStep> generateTestSteps(RequestBodyTestCase rbTest) {
        List<TestStep> steps = new ArrayList<>();

        steps.add(new TestStep("SETUP", "Prepare request body validation test environment", 1));
        steps.add(new TestStep("PREPARE_PAYLOAD",
                String.format("Prepare %s payload: %s", rbTest.getContentType(),
                        rbTest.getPayload() != null ? rbTest.getPayload().substring(0, Math.min(50, rbTest.getPayload().length())) + "..." : "null"), 2));
        steps.add(new TestStep("SEND_REQUEST", "Send request with prepared payload", 3));
        steps.add(new TestStep("VALIDATE_RESPONSE", "Validate response matches expected outcome", 4));
        steps.add(new TestStep("CLEANUP", "Clean up test artifacts", 5));

        return steps;
    }

    private TestDataSet generateTestDataSet(RequestBodyTestCase rbTest) {
        TestDataSet dataSet = new TestDataSet();
        dataSet.addRequestBody(rbTest.getContentType(), rbTest.getPayload());

        // Add metadata
        dataSet.addMetadata("scenario", rbTest.getScenario().name());
        dataSet.addMetadata("shouldSucceed", rbTest.shouldSucceed());
        dataSet.addMetadata("payloadSize", rbTest.getPayloadSize());

        return dataSet;
    }

    private List<TestAssertion> generateTestAssertions(RequestBodyTestCase rbTest) {
        List<TestAssertion> assertions = new ArrayList<>();

        if (rbTest.shouldSucceed()) {
            assertions.add(new TestAssertion("RESPONSE_STATUS", "Response status should be 2xx", "status >= 200 && status < 300"));
            assertions.add(new TestAssertion("RESPONSE_TIME", "Response time should be reasonable", "responseTime < 5000"));
            assertions.add(new TestAssertion("CONTENT_TYPE", "Response should have proper content type", "response.contentType != null"));
        } else {
            assertions.add(new TestAssertion("RESPONSE_STATUS", "Response status should indicate error", "status >= 400"));
            assertions.add(new TestAssertion("ERROR_MESSAGE", "Error message should be present", "response.error != null"));
            assertions.add(new TestAssertion("ERROR_DETAILS", "Error details should be informative", "response.error.message != null"));
        }

        // Security-specific assertions
        if (rbTest.isSecurityTest()) {
            assertions.add(new TestAssertion("SECURITY_HEADERS", "Security headers should be present",
                    "response.headers['X-Content-Type-Options'] != null"));
            assertions.add(new TestAssertion("NO_SENSITIVE_DATA_LEAK", "No sensitive data in error response",
                    "!response.body.contains('password') && !response.body.contains('token')"));
        }

        return assertions;
    }

    private Duration estimateTestDuration(RequestBodyTestCase rbTest) {
        Duration baseDuration = Duration.ofMillis(800);

        // Add time for complexity
        baseDuration = baseDuration.plusMillis(complexity * 50L);

        // Add time for security tests
        if (rbTest.isSecurityTest()) {
            baseDuration = baseDuration.plusSeconds(3);
        }

        // Add time for large payloads
        if (rbTest.isPerformanceTest()) {
            baseDuration = baseDuration.plusSeconds(10);
        }

        // Add time for file upload tests
        if (supportsFileUpload && rbTest.getContentType().contains("multipart")) {
            baseDuration = baseDuration.plusSeconds(5);
        }

        return baseDuration;
    }

    private StrategyType determineStrategyFromScenario(TestGenerationScenario scenario) {
        switch (scenario) {
            case HAPPY_PATH:
                return StrategyType.FUNCTIONAL_BASIC;
            case ERROR_HANDLING:
                return StrategyType.FUNCTIONAL_COMPREHENSIVE;
            case SQL_INJECTION_BASIC:
            case XSS_REFLECTED:
                return StrategyType.SECURITY_OWASP_TOP10;
            case XML_EXTERNAL_ENTITY:
                return StrategyType.SECURITY_PENETRATION;
            case LOAD_TESTING_LIGHT:
                return StrategyType.PERFORMANCE_LOAD;
            default:
                return testComplexity.getRecommendedStrategy();
        }
    }

    private List<RequestBodyTestCase> generateRequestBodyTestCasesForScenario(TestGenerationScenario scenario) {
        List<RequestBodyTestCase> cases = new ArrayList<>();

        switch (scenario) {
            case HAPPY_PATH:
                cases.addAll(generateValidTestCases());
                break;
            case ERROR_HANDLING:
                cases.addAll(generateInvalidTestCases());
                break;
            case SQL_INJECTION_BASIC:
                cases.addAll(generateSqlInjectionCases());
                break;
            case XSS_REFLECTED:
                cases.addAll(generateXssTestCases());
                break;
            case XML_EXTERNAL_ENTITY:
                cases.addAll(generateXxeTestCases());
                break;
            case LOAD_TESTING_LIGHT:
                cases.addAll(generatePerformanceTestCases());
                break;
            default:
                cases.addAll(generateValidTestCases());
                break;
        }

        return cases;
    }

    private List<RequestBodyTestCase> generateValidTestCases() {
        List<RequestBodyTestCase> cases = new ArrayList<>();

        for (String contentType : supportedContentTypes) {
            String validPayload = generateValidPayload(contentType);
            cases.add(new RequestBodyTestCase(
                    "Valid " + getContentTypeDescription(contentType),
                    validPayload,
                    contentType,
                    TestScenario.VALID_COMPLETE,
                    true,
                    "Valid request body with proper structure"
            ));
        }

        return cases;
    }

    private List<RequestBodyTestCase> generateInvalidTestCases() {
        List<RequestBodyTestCase> cases = new ArrayList<>();

        // Null body test
        cases.add(new RequestBodyTestCase(
                "Null request body",
                null,
                primaryContentType,
                TestScenario.INVALID_NULL,
                !required,
                "Null request body test"
        ));

        // Empty body test
        cases.add(new RequestBodyTestCase(
                "Empty request body",
                "",
                primaryContentType,
                TestScenario.INVALID_EMPTY,
                !required,
                "Empty request body test"
        ));

        // Malformed JSON test
        if (supportedContentTypes.contains("application/json")) {
            cases.add(new RequestBodyTestCase(
                    "Malformed JSON",
                    "{ \"name\": \"test\"",
                    "application/json",
                    TestScenario.INVALID_MALFORMED,
                    false,
                    "JSON with missing closing brace"
            ));
        }

        return cases;
    }

    private List<RequestBodyTestCase> generateSqlInjectionCases() {
        List<RequestBodyTestCase> cases = new ArrayList<>();

        String[] injectionPayloads = {
                "'; DROP TABLE users; --",
                "' OR '1'='1",
                "admin'--",
                "'; INSERT INTO users VALUES ('hacker', 'password'); --"
        };

        for (String payload : injectionPayloads) {
            String maliciousJson = String.format("{ \"name\": \"%s\" }", payload);
            cases.add(new RequestBodyTestCase(
                    "SQL Injection attempt",
                    maliciousJson,
                    "application/json",
                    TestScenario.SECURITY_INJECTION,
                    false,
                    "SQL injection payload in request body"
            ));
        }

        return cases;
    }

    // ===== STANDARD EQUALS, HASHCODE, TOSTRING =====

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestBodyInfo that = (RequestBodyInfo) o;
        return required == that.required &&
                Objects.equals(description, that.description) &&
                Objects.equals(primaryContentType, that.primaryContentType) &&
                Objects.equals(requiredProperties, that.requiredProperties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(required, description, primaryContentType, requiredProperties);
    }

    @Override
    public String toString() {
        return "RequestBodyInfo{" +
                "required=" + required +
                ", primaryContentType='" + primaryContentType + '\'' +
                ", propertyCount=" + propertyConstraints.size() +
                ", requiredProperties=" + requiredProperties.size() +
                ", testComplexity=" + testComplexity +
                ", securitySensitivity=" + securitySensitivity +
                ", supportsFileUpload=" + supportsFileUpload +
                ", complexity=" + complexity +
                ", estimatedTests=" + estimateTestCaseCount() +
                ", executionId='" + executionId + '\'' +
                '}';
    }

    // ===== INNER CLASSES (Standard Interface Alignment) =====

    public static class RequestBodyTestCase {
        private final String name;
        private final String payload;
        private final String contentType;
        private final TestScenario scenario;
        private final boolean shouldSucceed;
        private final String description;
        private final Map<String, Object> metadata = new HashMap<>();

        public RequestBodyTestCase(String name, String payload, String contentType,
                                   TestScenario scenario, boolean shouldSucceed, String description) {
            this.name = name;
            this.payload = payload;
            this.contentType = contentType;
            this.scenario = scenario;
            this.shouldSucceed = shouldSucceed;
            this.description = description;
        }

        public String getName() { return name; }
        public String getPayload() { return payload; }
        public String getContentType() { return contentType; }
        public TestScenario getScenario() { return scenario; }
        public boolean shouldSucceed() { return shouldSucceed; }
        public String getDescription() { return description; }
        public Map<String, Object> getMetadata() { return new HashMap<>(metadata); }

        public int getPayloadSize() {
            return payload != null ? payload.length() : 0;
        }

        public boolean isSecurityTest() {
            return scenario == TestScenario.SECURITY_INJECTION ||
                    scenario == TestScenario.SECURITY_XSS ||
                    scenario == TestScenario.SECURITY_XXE;
        }

        public boolean isPerformanceTest() {
            return scenario == TestScenario.PERFORMANCE_LARGE;
        }

        @Override
        public String toString() {
            return "RequestBodyTestCase{" +
                    "name='" + name + '\'' +
                    ", contentType='" + contentType + '\'' +
                    ", scenario=" + scenario +
                    ", shouldSucceed=" + shouldSucceed +
                    ", payloadSize=" + getPayloadSize() +
                    '}';
        }
    }

    public static class MediaTypeInfo {
        private final String type;
        private final String displayName;
        private final boolean supportsStructuredData;
        private final boolean supportsValidation;

        public MediaTypeInfo(String type, String displayName, boolean supportsStructuredData, boolean supportsValidation) {
            this.type = type;
            this.displayName = displayName;
            this.supportsStructuredData = supportsStructuredData;
            this.supportsValidation = supportsValidation;
        }

        public String getType() { return type; }
        public String getDisplayName() { return displayName; }
        public boolean supportsStructuredData() { return supportsStructuredData; }
        public boolean supportsValidation() { return supportsValidation; }
    }

    public static class RequestBodySchema {
        private final String type;
        private final Map<String, DataConstraints> propertyConstraints = new HashMap<>();
        private final List<String> requiredProperties = new ArrayList<>();
        private final boolean hasNestedObjects;
        private final boolean hasArrayProperties;
        private final int complexityScore;

        public RequestBodySchema(String type, boolean hasNestedObjects, boolean hasArrayProperties, int complexityScore) {
            this.type = type;
            this.hasNestedObjects = hasNestedObjects;
            this.hasArrayProperties = hasArrayProperties;
            this.complexityScore = complexityScore;
        }

        public String getType() { return type; }
        public Map<String, DataConstraints> getPropertyConstraints() { return new HashMap<>(propertyConstraints); }
        public List<String> getRequiredProperties() { return new ArrayList<>(requiredProperties); }
        public boolean hasNestedObjects() { return hasNestedObjects; }
        public boolean hasArrayProperties() { return hasArrayProperties; }
        public int getComplexityScore() { return complexityScore; }
    }

    public static class ValidationRule {
        private final String name;
        private final String expression;
        private final String errorMessage;
        private final RuleSeverity severity;

        public ValidationRule(String name, String expression, String errorMessage, RuleSeverity severity) {
            this.name = name;
            this.expression = expression;
            this.errorMessage = errorMessage;
            this.severity = severity;
        }

        public String getName() { return name; }
        public String getExpression() { return expression; }
        public String getErrorMessage() { return errorMessage; }
        public RuleSeverity getSeverity() { return severity; }

        public enum RuleSeverity {
            ERROR, WARNING, INFO
        }
    }

    // ===== ENTERPRISE ANALYSIS CLASSES =====

    public static class AdvancedRequestBodyAnalysis {
        private final RequestBodyInfo requestBody;
        private final int complexityScore;
        private final int estimatedTestCount;
        private final Map<TestGenerationScenario, Integer> scenarioBreakdown;
        private final QualityMetrics qualityMetrics;
        private final SecurityProfile securityProfile;
        private final Instant analysisTimestamp;

        public AdvancedRequestBodyAnalysis(RequestBodyInfo requestBody) {
            this.requestBody = requestBody;
            this.complexityScore = requestBody.calculateComplexityScore();
            this.estimatedTestCount = requestBody.estimateTestCaseCount();
            this.scenarioBreakdown = calculateScenarioBreakdown();
            this.qualityMetrics = calculateQualityMetrics();
            this.securityProfile = calculateSecurityProfile();
            this.analysisTimestamp = Instant.now();
        }

        private Map<TestGenerationScenario, Integer> calculateScenarioBreakdown() {
            Map<TestGenerationScenario, Integer> breakdown = new HashMap<>();
            for (TestGenerationScenario scenario : requestBody.enabledTestScenarios) {
                breakdown.put(scenario, 3); // Estimated 3 test cases per scenario
            }
            return breakdown;
        }

        private QualityMetrics calculateQualityMetrics() {
            return QualityMetrics.builder()
                    .withCoverageScore(calculateCoverageScore())
                    .withQualityScore(calculateQualityScore())
                    .withSecurityScore(1.0 - (requestBody.securityRisks.size() * 0.1))
                    .withComplexityScore((double) complexityScore / 100.0)
                    .build();
        }

        private SecurityProfile calculateSecurityProfile() {
            return SecurityProfile.builder()
                    .withSecurityLevel(mapSecuritySensitivityToLevel(requestBody.securitySensitivity))
                    .withSecurityScore(1.0 - (requestBody.securityRisks.size() * 0.1))
                    .withRiskLevel(requestBody.securityRisks.size() > 3 ? 4 : requestBody.securityRisks.size())
                    .build();
        }

        private double calculateCoverageScore() {
            double baseScore = requestBody.enabledTestScenarios.size() / 10.0; // Assuming max 10 scenarios
            if (requestBody.dataConstraints != null) baseScore += 0.2;
            if (requestBody.schema != null) baseScore += 0.3;
            return Math.min(1.0, baseScore);
        }

        private double calculateQualityScore() {
            double score = 0.0;
            score += requestBody.required ? 0.2 : 0.1;
            score += requestBody.dataConstraints != null ? 0.3 : 0.1;
            score += requestBody.customValidationRules.size() > 0 ? 0.2 : 0.0;
            score += requestBody.schema != null ? 0.3 : 0.1;
            return Math.min(1.0, score);
        }

        private SecurityProfile.SecurityLevel mapSecuritySensitivityToLevel(SecuritySensitivity sensitivity) {
            switch (sensitivity) {
                case CRITICAL: return SecurityProfile.SecurityLevel.CRITICAL;
                case HIGH: return SecurityProfile.SecurityLevel.HIGH;
                case NORMAL: return SecurityProfile.SecurityLevel.MEDIUM;
                case LOW: return SecurityProfile.SecurityLevel.LOW;
                default: return SecurityProfile.SecurityLevel.MINIMAL;
            }
        }

        // Getters
        public RequestBodyInfo getRequestBody() { return requestBody; }
        public int getComplexityScore() { return complexityScore; }
        public int getEstimatedTestCount() { return estimatedTestCount; }
        public Map<TestGenerationScenario, Integer> getScenarioBreakdown() { return new HashMap<>(scenarioBreakdown); }
        public QualityMetrics getQualityMetrics() { return qualityMetrics; }
        public SecurityProfile getSecurityProfile() { return securityProfile; }
        public Instant getAnalysisTimestamp() { return analysisTimestamp; }
    }

    // ===== CACHE CLASS =====

    private static class AnalysisCache {
        private final AdvancedRequestBodyAnalysis analysis;
        private final Instant timestamp;

        public AnalysisCache(AdvancedRequestBodyAnalysis analysis) {
            this.analysis = analysis;
            this.timestamp = Instant.now();
        }

        public boolean isExpired(Duration maxAge) {
            return Instant.now().isAfter(timestamp.plus(maxAge));
        }

        public AdvancedRequestBodyAnalysis getAnalysis() { return analysis; }
    }

    // ===== LEGACY SUPPORT =====

    @Deprecated
    public DataConstraints getConstraints() { return dataConstraints; }

    @Deprecated
    public void setConstraints(DataConstraints constraints) { setDataConstraints(constraints); }

    // ===== STATIC FACTORY METHODS =====

    public static RequestBodyInfo createSimpleJson(boolean required) {
        return builder()
                .withRequired(required)
                .withDescription("Simple JSON request body")
                .withContentType("application/json")
                .build();
    }

    public static RequestBodyInfo createFormData(boolean required) {
        return builder()
                .withRequired(required)
                .withDescription("Form data request body")
                .withContentType("application/x-www-form-urlencoded")
                .build();
    }

    public static RequestBodyInfo createFileUpload(boolean required, String... allowedTypes) {
        return builder()
                .withRequired(required)
                .withDescription("File upload request body")
                .withContentType("multipart/form-data")
                .withFileUpload(allowedTypes)
                .withSecuritySensitivity(SecuritySensitivity.HIGH)
                .build();
    }

    public static RequestBodyInfo createSecure(boolean required, String description) {
        return builder()
                .withRequired(required)
                .withDescription(description)
                .withContentType("application/json")
                .withSecuritySensitivity(SecuritySensitivity.HIGH)
                .withTestComplexity(TestComplexity.COMPREHENSIVE)
                .build();
    }

    public static RequestBodyInfo createBulkOperation(boolean required, String description) {
        return builder()
                .withRequired(required)
                .withDescription(description)
                .withContentType("application/json")
                .withBulkOperations(true)
                .withTestComplexity(TestComplexity.COMPREHENSIVE)
                .build();
    }

}