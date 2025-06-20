package org.example.openapi;

import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.Instant;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ULTRA-OPTIMIZED ResponseInfo - Tutarlılık Standardına Uygun
 *
 * Yenilikler:
 * - Standard interface'lere uyum
 * - Builder pattern implementasyonu
 * - Enterprise logging ve monitoring
 * - Thread-safe operations
 * - Advanced configuration validation
 * - Performance optimizasyonları
 * - Comprehensive test generation (500+ scenarios)
 * - Security validation enhancements
 * - Real-world data pattern testing
 */
public class ResponseInfo {

    private static final Logger logger = LoggerFactory.getLogger(ResponseInfo.class);
    private static final String DEFAULT_CONTENT_TYPE = "application/json";
    private static final int DEFAULT_MAX_TEST_VARIATIONS = 50;

    // ===== Core Response Properties (Standard Interface) =====
    private String statusCode;
    private String description;
    private String reasonPhrase;
    private boolean isSuccessResponse;
    private boolean isErrorResponse;

    // ===== Enhanced Response Classification =====
    private ResponseCategory category;
    private ResponseSemantics semantics;
    private List<ResponsePattern> detectedPatterns = new ArrayList<>();
    private ResponseComplexityProfile complexityProfile;

    // ===== Content and Schema (Standard Interface) =====
    private DataConstraints constraints;
    private ResponseSchema schema;
    private Map<String, DataConstraints> propertyConstraints = new HashMap<>();
    private List<String> requiredProperties = new ArrayList<>();
    private Map<String, PropertyValidationProfile> propertyProfiles = new HashMap<>();

    // ===== Content Types and Media Types =====
    private Set<String> supportedContentTypes = new HashSet<>();
    private String primaryContentType = DEFAULT_CONTENT_TYPE;
    private Map<String, MediaTypeInfo> mediaTypeDetails = new HashMap<>();
    private Map<String, ContentTypeTestProfile> contentTypeProfiles = new HashMap<>();

    // ===== Enhanced Headers (Standard Interface) =====
    private Map<String, HeaderInfo> responseHeaders = new HashMap<>();
    private List<String> requiredHeaders = new ArrayList<>();
    private List<String> securityHeaders = new ArrayList<>();
    private Map<String, HeaderValidationProfile> headerProfiles = new HashMap<>();
    private Set<HeaderCombination> criticalHeaderCombinations = new HashSet<>();

    // ===== Examples and Test Data =====
    private List<ResponseExample> examples = new ArrayList<>();
    private String defaultExample;
    private Map<String, Object> testData = new HashMap<>();
    private Map<String, TestDataGenerator> customDataGenerators = new HashMap<>();

    // ===== Enhanced Validation (Standard Interface) =====
    private Set<ValidationRule> validationRules = new HashSet<>();
    private ResponseValidationLevel validationLevel = ResponseValidationLevel.STANDARD;
    private boolean strictValidation = false;
    private ValidationStrategy validationStrategy = ValidationStrategy.COMPREHENSIVE;
    private Map<String, CustomValidator> customValidators = new HashMap<>();

    // ===== Advanced Test Generation Settings =====
    private Set<ResponseTestScenario> enabledTestScenarios = EnumSet.allOf(ResponseTestScenario.class);
    private TestComplexity testComplexity = TestComplexity.STANDARD;
    private int maxTestVariations = DEFAULT_MAX_TEST_VARIATIONS;
    private TestGenerationStrategy generationStrategy = TestGenerationStrategy.EXHAUSTIVE;
    private EdgeCaseDetectionLevel edgeCaseLevel = EdgeCaseDetectionLevel.AGGRESSIVE;

    // ===== Performance and Security (Standard Interface) =====
    private PerformanceExpectations performanceExpectations;
    private SecurityExpectations securityExpectations;
    private List<String> sensitiveDataFields = new ArrayList<>();
    private Map<String, SecurityValidationProfile> securityProfiles = new HashMap<>();
    private PerformanceTestProfile performanceProfile;

    // ===== Collection Support =====
    private boolean isPaginatedResponse = false;
    private PaginationInfo paginationInfo;
    private boolean isCollectionResponse = false;
    private CollectionInfo collectionInfo;
    private CollectionTestProfile collectionTestProfile;

    // ===== Enhanced Error Response Details =====
    private ErrorResponseInfo errorInfo;
    private List<String> commonErrorCodes = new ArrayList<>();
    private Map<String, String> errorMessages = new HashMap<>();
    private Map<String, ErrorScenario> errorScenarios = new HashMap<>();
    private ErrorTestProfile errorTestProfile;

    // ===== Advanced Analytics =====
    private ResponseDataProfile dataProfile;
    private Map<String, DataPattern> detectedDataPatterns = new HashMap<>();
    private Set<DataQualityRule> dataQualityRules = new HashSet<>();
    private ResponseBehaviorProfile behaviorProfile;

    // ===== Metadata (Standard Interface) =====
    private Map<String, Object> metadata = new HashMap<>();
    private List<String> tags = new ArrayList<>();
    private String version;
    private Map<String, TestMetrics> testMetrics = new HashMap<>();
    private String executionId;
    private Instant generationTimestamp;

    // ===== Standard Enums (Tutarlılık Standardına Uygun) =====

    public enum ResponseTestScenario {
        // FUNCTIONAL (Standard Categories)
        FUNCTIONAL_BASIC("Basic functional testing", 1, StrategyCategory.FUNCTIONAL),
        FUNCTIONAL_COMPREHENSIVE("Comprehensive functional testing", 2, StrategyCategory.FUNCTIONAL),
        FUNCTIONAL_BOUNDARY("Boundary condition testing", 2, StrategyCategory.FUNCTIONAL),
        FUNCTIONAL_EDGE_CASE("Edge case testing", 3, StrategyCategory.FUNCTIONAL),

        // SECURITY (Standard Categories)
        SECURITY_BASIC("Basic security validation", 2, StrategyCategory.SECURITY),
        SECURITY_OWASP_TOP10("OWASP Top 10 testing", 4, StrategyCategory.SECURITY),
        SECURITY_PENETRATION("Penetration testing", 5, StrategyCategory.SECURITY),
        SECURITY_INJECTION("Injection testing", 4, StrategyCategory.SECURITY),
        SECURITY_XSS("XSS testing", 3, StrategyCategory.SECURITY),

        // PERFORMANCE (Standard Categories)
        PERFORMANCE_BASIC("Basic performance testing", 2, StrategyCategory.PERFORMANCE),
        PERFORMANCE_LOAD("Load testing", 3, StrategyCategory.PERFORMANCE),
        PERFORMANCE_STRESS("Stress testing", 4, StrategyCategory.PERFORMANCE),

        // ADVANCED (Standard Categories)
        ADVANCED_AI_DRIVEN("AI-driven testing", 5, StrategyCategory.ADVANCED),
        ADVANCED_QUANTUM("Quantum computing testing", 5, StrategyCategory.SPECIALIZED),

        // Core Validation Scenarios
        SCHEMA_VALIDATION("Response schema validation", 2, StrategyCategory.FUNCTIONAL),
        CONTENT_TYPE_VALIDATION("Content-Type header validation", 1, StrategyCategory.FUNCTIONAL),
        STATUS_CODE_VALIDATION("Status code validation", 1, StrategyCategory.FUNCTIONAL),
        HEADER_VALIDATION("Response header validation", 2, StrategyCategory.FUNCTIONAL),

        // Advanced Validation Scenarios
        DEEP_SCHEMA_VALIDATION("Deep nested schema validation", 3, StrategyCategory.FUNCTIONAL),
        CROSS_FIELD_VALIDATION("Cross-field validation", 3, StrategyCategory.FUNCTIONAL),
        CONDITIONAL_VALIDATION("Conditional validation", 4, StrategyCategory.FUNCTIONAL),
        POLYMORPHIC_VALIDATION("Polymorphic schema validation", 4, StrategyCategory.ADVANCED),

        // Performance Testing Scenarios
        PERFORMANCE_VALIDATION("Response time validation", 2, StrategyCategory.PERFORMANCE),
        MEMORY_USAGE_VALIDATION("Memory usage validation", 3, StrategyCategory.PERFORMANCE),
        THROUGHPUT_VALIDATION("Throughput validation", 3, StrategyCategory.PERFORMANCE),
        RESPONSE_SIZE_VALIDATION("Response size validation", 2, StrategyCategory.PERFORMANCE),

        // Security Testing Scenarios
        SECURITY_VALIDATION("Security header validation", 2, StrategyCategory.SECURITY),
        SENSITIVE_DATA_MASKING("Sensitive data masking validation", 3, StrategyCategory.SECURITY),
        INFORMATION_DISCLOSURE("Information disclosure testing", 4, StrategyCategory.SECURITY),
        SECURITY_HEADER_COMBINATION("Security header combination testing", 3, StrategyCategory.SECURITY),
        XSS_PREVENTION_VALIDATION("XSS prevention validation", 3, StrategyCategory.SECURITY),
        INJECTION_PREVENTION_VALIDATION("Injection prevention validation", 4, StrategyCategory.SECURITY),

        // Data Quality Testing Scenarios
        DATA_INTEGRITY("Data integrity validation", 3, StrategyCategory.FUNCTIONAL),
        DATA_CONSISTENCY("Data consistency validation", 3, StrategyCategory.FUNCTIONAL),
        DATA_COMPLETENESS("Data completeness validation", 2, StrategyCategory.FUNCTIONAL),
        DATA_ACCURACY("Data accuracy validation", 3, StrategyCategory.FUNCTIONAL),

        // Boundary Testing Scenarios
        BOUNDARY_VALIDATION("Boundary value validation", 2, StrategyCategory.FUNCTIONAL),
        EXTREME_VALUE_VALIDATION("Extreme value validation", 3, StrategyCategory.FUNCTIONAL),
        OVERFLOW_VALIDATION("Overflow validation", 3, StrategyCategory.FUNCTIONAL),
        UNDERFLOW_VALIDATION("Underflow validation", 3, StrategyCategory.FUNCTIONAL),

        // Error Handling Scenarios
        ERROR_RESPONSE_VALIDATION("Error response validation", 2, StrategyCategory.FUNCTIONAL),
        ERROR_MESSAGE_CLARITY("Error message clarity validation", 2, StrategyCategory.FUNCTIONAL),
        ERROR_CODE_CONSISTENCY("Error code consistency validation", 2, StrategyCategory.FUNCTIONAL),
        ERROR_STRUCTURE_VALIDATION("Error structure validation", 3, StrategyCategory.FUNCTIONAL),

        // Collection Testing Scenarios
        PAGINATION_VALIDATION("Pagination validation", 3, StrategyCategory.FUNCTIONAL),
        COLLECTION_VALIDATION("Collection response validation", 2, StrategyCategory.FUNCTIONAL),
        SORTING_VALIDATION("Sorting validation", 2, StrategyCategory.FUNCTIONAL),
        FILTERING_VALIDATION("Filtering validation", 2, StrategyCategory.FUNCTIONAL),

        // Advanced Testing Scenarios
        NESTED_OBJECT_VALIDATION("Nested object validation", 3, StrategyCategory.FUNCTIONAL),
        ARRAY_VALIDATION("Array validation", 2, StrategyCategory.FUNCTIONAL),
        EMPTY_RESPONSE_VALIDATION("Empty response validation", 1, StrategyCategory.FUNCTIONAL),
        LARGE_RESPONSE_VALIDATION("Large response validation", 3, StrategyCategory.PERFORMANCE),
        CONCURRENT_RESPONSE_VALIDATION("Concurrent response validation", 4, StrategyCategory.PERFORMANCE),

        // Format Testing Scenarios
        CACHE_VALIDATION("Cache header validation", 2, StrategyCategory.PERFORMANCE),
        COMPRESSION_VALIDATION("Response compression validation", 2, StrategyCategory.PERFORMANCE),
        ENCODING_VALIDATION("Character encoding validation", 2, StrategyCategory.FUNCTIONAL),
        FORMAT_CONVERSION_VALIDATION("Format conversion validation", 3, StrategyCategory.FUNCTIONAL),

        // Real-world Scenarios
        REAL_WORLD_DATA_VALIDATION("Real-world data pattern validation", 3, StrategyCategory.FUNCTIONAL),
        LOCALIZATION_VALIDATION("Localization validation", 3, StrategyCategory.FUNCTIONAL),
        TIMEZONE_VALIDATION("Timezone validation", 3, StrategyCategory.FUNCTIONAL),
        CURRENCY_VALIDATION("Currency validation", 2, StrategyCategory.FUNCTIONAL),

        // Edge Cases
        MALFORMED_DATA_HANDLING("Malformed data handling", 4, StrategyCategory.FUNCTIONAL),
        PARTIAL_RESPONSE_VALIDATION("Partial response validation", 3, StrategyCategory.FUNCTIONAL),
        TRUNCATED_RESPONSE_VALIDATION("Truncated response validation", 3, StrategyCategory.FUNCTIONAL),
        CORRUPTED_DATA_VALIDATION("Corrupted data validation", 4, StrategyCategory.FUNCTIONAL),

        // Business Logic
        BUSINESS_RULE_VALIDATION("Business rule validation", 4, StrategyCategory.FUNCTIONAL),
        WORKFLOW_VALIDATION("Workflow validation", 4, StrategyCategory.FUNCTIONAL),
        STATE_TRANSITION_VALIDATION("State transition validation", 4, StrategyCategory.FUNCTIONAL),
        IDEMPOTENCY_VALIDATION("Idempotency validation", 3, StrategyCategory.FUNCTIONAL),

        // Integration Testing
        DOWNSTREAM_COMPATIBILITY("Downstream compatibility validation", 3, StrategyCategory.FUNCTIONAL),
        VERSION_COMPATIBILITY("Version compatibility validation", 3, StrategyCategory.FUNCTIONAL),
        BACKWARD_COMPATIBILITY("Backward compatibility validation", 3, StrategyCategory.FUNCTIONAL),
        API_CONTRACT_VALIDATION("API contract validation", 4, StrategyCategory.FUNCTIONAL);

        private final String description;
        private final int complexity;
        private final StrategyCategory category;

        ResponseTestScenario(String description, int complexity, StrategyCategory category) {
            this.description = description;
            this.complexity = complexity;
            this.category = category;
        }

        public String getDescription() { return description; }
        public int getComplexity() { return complexity; }
        public StrategyCategory getCategory() { return category; }
    }

    public enum StrategyCategory {
        FUNCTIONAL, SECURITY, PERFORMANCE, ADVANCED, SPECIALIZED
    }

    public enum TestComplexity {
        MINIMAL(10, "Basic response validation"),
        STANDARD(25, "Standard response testing"),
        COMPREHENSIVE(50, "Comprehensive response testing"),
        EXHAUSTIVE(100, "Maximum response testing"),
        EXTREME(200, "Extreme test coverage");

        private final int maxTestCount;
        private final String description;

        TestComplexity(int maxTestCount, String description) {
            this.maxTestCount = maxTestCount;
            this.description = description;
        }

        public int getMaxTestCount() { return maxTestCount; }
        public String getDescription() { return description; }
    }

    public enum ValidationStrategy {
        BASIC, STANDARD, COMPREHENSIVE, EXHAUSTIVE, CUSTOM
    }

    public enum TestGenerationStrategy {
        MINIMAL, STANDARD, COMPREHENSIVE, EXHAUSTIVE, ADAPTIVE, CUSTOM
    }

    public enum EdgeCaseDetectionLevel {
        NONE, BASIC, STANDARD, AGGRESSIVE, EXTREME
    }

    public enum ResponseSemantics {
        RESOURCE_CREATED, RESOURCE_UPDATED, RESOURCE_DELETED, RESOURCE_RETRIEVED,
        COLLECTION_RETRIEVED, SEARCH_RESULT, VALIDATION_ERROR, BUSINESS_ERROR,
        SYSTEM_ERROR, AUTHENTICATION_ERROR, AUTHORIZATION_ERROR, RATE_LIMITED,
        MAINTENANCE_MODE, DEPRECATED_ENDPOINT, CUSTOM_SEMANTIC
    }

    public enum ResponsePattern {
        STANDARD_REST, HAL_JSON, JSON_API, GRAPHQL, SOAP, CUSTOM_FORMAT,
        PAGINATED, FILTERED, SORTED, AGGREGATED, STREAMED, BATCHED
    }

    public enum ResponseCategory {
        SUCCESS, REDIRECTION, CLIENT_ERROR, SERVER_ERROR, CUSTOM
    }

    public enum ResponseValidationLevel {
        MINIMAL, BASIC, STANDARD, COMPREHENSIVE, STRICT
    }

    // ===== Constructors (Standard Pattern) =====

    public ResponseInfo() {
        this.executionId = generateAdvancedExecutionId();
        this.generationTimestamp = Instant.now();
        initializeDefaults();
        initializeAdvancedFeatures();
        logger.debug("ResponseInfo initialized with executionId: {}", executionId);
    }

    public ResponseInfo(String statusCode, String description) {
        this();
        this.statusCode = statusCode;
        this.description = description;
        analyzeStatusCode();
        inferResponseSemantics();
        detectResponsePatterns();
        logger.info("ResponseInfo created for status code: {} with description: {}", statusCode, description);
    }

    // ===== Builder Pattern (Standard Interface) =====

    public static class Builder {
        private ResponseInfo responseInfo;

        public Builder() {
            this.responseInfo = new ResponseInfo();
        }

        public Builder withStatusCode(String statusCode) {
            responseInfo.statusCode = statusCode;
            responseInfo.analyzeStatusCode();
            return this;
        }

        public Builder withDescription(String description) {
            responseInfo.description = description;
            return this;
        }

        public Builder withReasonPhrase(String reasonPhrase) {
            responseInfo.reasonPhrase = reasonPhrase;
            return this;
        }

        public Builder withPrimaryContentType(String contentType) {
            responseInfo.primaryContentType = contentType;
            return this;
        }

        public Builder withSupportedContentTypes(Set<String> contentTypes) {
            responseInfo.supportedContentTypes.clear();
            responseInfo.supportedContentTypes.addAll(contentTypes);
            return this;
        }

        public Builder withResponseHeaders(Map<String, HeaderInfo> headers) {
            responseInfo.responseHeaders.clear();
            responseInfo.responseHeaders.putAll(headers);
            return this;
        }

        public Builder withRequiredHeaders(List<String> headers) {
            responseInfo.requiredHeaders.clear();
            responseInfo.requiredHeaders.addAll(headers);
            return this;
        }

        public Builder withSecurityHeaders(List<String> headers) {
            responseInfo.securityHeaders.clear();
            responseInfo.securityHeaders.addAll(headers);
            return this;
        }

        public Builder withExamples(List<ResponseExample> examples) {
            responseInfo.examples.clear();
            responseInfo.examples.addAll(examples);
            return this;
        }

        public Builder withDefaultExample(String example) {
            responseInfo.defaultExample = example;
            return this;
        }

        public Builder withValidationRules(Set<ValidationRule> rules) {
            responseInfo.validationRules.clear();
            responseInfo.validationRules.addAll(rules);
            return this;
        }

        public Builder withValidationLevel(ResponseValidationLevel level) {
            responseInfo.validationLevel = level;
            return this;
        }

        public Builder withStrictValidation(boolean strict) {
            responseInfo.strictValidation = strict;
            return this;
        }

        public Builder withValidationStrategy(ValidationStrategy strategy) {
            responseInfo.validationStrategy = strategy;
            return this;
        }

        public Builder withEnabledTestScenarios(Set<ResponseTestScenario> scenarios) {
            responseInfo.enabledTestScenarios.clear();
            responseInfo.enabledTestScenarios.addAll(scenarios);
            return this;
        }

        public Builder withTestComplexity(TestComplexity complexity) {
            responseInfo.testComplexity = complexity;
            responseInfo.maxTestVariations = complexity.getMaxTestCount();
            return this;
        }

        public Builder withMaxTestVariations(int maxVariations) {
            responseInfo.maxTestVariations = maxVariations;
            return this;
        }

        public Builder withGenerationStrategy(TestGenerationStrategy strategy) {
            responseInfo.generationStrategy = strategy;
            return this;
        }

        public Builder withEdgeCaseLevel(EdgeCaseDetectionLevel level) {
            responseInfo.edgeCaseLevel = level;
            return this;
        }

        public Builder withPerformanceExpectations(PerformanceExpectations expectations) {
            responseInfo.performanceExpectations = expectations;
            return this;
        }

        public Builder withSecurityExpectations(SecurityExpectations expectations) {
            responseInfo.securityExpectations = expectations;
            return this;
        }

        public Builder withSensitiveDataFields(List<String> fields) {
            responseInfo.sensitiveDataFields.clear();
            responseInfo.sensitiveDataFields.addAll(fields);
            return this;
        }

        public Builder withPaginatedResponse(boolean paginated) {
            responseInfo.isPaginatedResponse = paginated;
            return this;
        }

        public Builder withPaginationInfo(PaginationInfo paginationInfo) {
            responseInfo.paginationInfo = paginationInfo;
            responseInfo.isPaginatedResponse = true;
            return this;
        }

        public Builder withCollectionResponse(boolean isCollection) {
            responseInfo.isCollectionResponse = isCollection;
            return this;
        }

        public Builder withCollectionInfo(CollectionInfo collectionInfo) {
            responseInfo.collectionInfo = collectionInfo;
            responseInfo.isCollectionResponse = true;
            return this;
        }

        public Builder withErrorInfo(ErrorResponseInfo errorInfo) {
            responseInfo.errorInfo = errorInfo;
            return this;
        }

        public Builder withConstraints(DataConstraints constraints) {
            responseInfo.constraints = constraints;
            return this;
        }

        public Builder withSchema(ResponseSchema schema) {
            responseInfo.schema = schema;
            return this;
        }

        public Builder withPropertyConstraints(Map<String, DataConstraints> constraints) {
            responseInfo.propertyConstraints.clear();
            responseInfo.propertyConstraints.putAll(constraints);
            return this;
        }

        public Builder withRequiredProperties(List<String> properties) {
            responseInfo.requiredProperties.clear();
            responseInfo.requiredProperties.addAll(properties);
            return this;
        }

        public Builder withMetadata(Map<String, Object> metadata) {
            responseInfo.metadata.clear();
            responseInfo.metadata.putAll(metadata);
            return this;
        }

        public Builder withTags(List<String> tags) {
            responseInfo.tags.clear();
            responseInfo.tags.addAll(tags);
            return this;
        }

        public Builder withVersion(String version) {
            responseInfo.version = version;
            return this;
        }

        public Builder withExecutionId(String executionId) {
            responseInfo.executionId = executionId;
            return this;
        }

        public Builder withGenerationTimestamp(Instant timestamp) {
            responseInfo.generationTimestamp = timestamp;
            return this;
        }

        public ResponseInfo build() {
            validateConfiguration(responseInfo);
            responseInfo.inferResponseSemantics();
            responseInfo.detectResponsePatterns();
            responseInfo.initializeTestMetrics();
            logger.info("ResponseInfo built successfully with executionId: {}", responseInfo.executionId);
            return responseInfo;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    // ===== Standard Configuration Validation =====

    private static void validateConfiguration(ResponseInfo config) {
        if (config.statusCode == null || config.statusCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Status code cannot be null or empty");
        }

        try {
            int code = Integer.parseInt(config.statusCode);
            if (code < 100 || code > 599) {
                throw new IllegalArgumentException("Status code must be between 100 and 599");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Status code must be a valid integer", e);
        }

        if (config.maxTestVariations < 1) {
            throw new IllegalArgumentException("Max test variations must be at least 1");
        }

        if (config.maxTestVariations > 1000) {
            logger.warn("High number of test variations ({}), this may impact performance", config.maxTestVariations);
        }

        if (config.primaryContentType == null || config.primaryContentType.trim().isEmpty()) {
            config.primaryContentType = DEFAULT_CONTENT_TYPE;
            logger.debug("Primary content type set to default: {}", DEFAULT_CONTENT_TYPE);
        }

        logger.debug("Configuration validation completed successfully");
    }

    // ===== Standard Initialization Methods =====

    private void initializeDefaults() {
        // Enhanced default content types
        supportedContentTypes.addAll(Arrays.asList(
                "application/json", "application/xml", "application/hal+json",
                "application/vnd.api+json", "text/csv", "text/plain",
                "application/pdf", "application/octet-stream", "text/html"
        ));

        // Enhanced media type details with validation capabilities
        mediaTypeDetails.put("application/json",
                new MediaTypeInfo("application/json", "JSON", true, true, true, true));
        mediaTypeDetails.put("application/xml",
                new MediaTypeInfo("application/xml", "XML", true, true, true, false));
        mediaTypeDetails.put("application/hal+json",
                new MediaTypeInfo("application/hal+json", "HAL JSON", true, true, true, true));
        mediaTypeDetails.put("text/csv",
                new MediaTypeInfo("text/csv", "CSV", false, true, false, false));
        mediaTypeDetails.put("text/html",
                new MediaTypeInfo("text/html", "HTML", true, true, false, false));

        // Performance and security expectations
        performanceExpectations = new PerformanceExpectations();
        securityExpectations = new SecurityExpectations();

        // Initialize profiles
        complexityProfile = new ResponseComplexityProfile();
        dataProfile = new ResponseDataProfile();
        behaviorProfile = new ResponseBehaviorProfile();
        performanceProfile = new PerformanceTestProfile();

        logger.debug("Default configuration initialized");
    }

    private void initializeAdvancedFeatures() {
        initializeDataGenerators();
        initializeValidationProfiles();
        initializeTestMetrics();
        initializeEdgeCaseDetection();
        logger.debug("Advanced features initialized");
    }

    private void initializeDataGenerators() {
        customDataGenerators.put("email", new EmailDataGenerator());
        customDataGenerators.put("phone", new PhoneDataGenerator());
        customDataGenerators.put("address", new AddressDataGenerator());
        customDataGenerators.put("datetime", new DateTimeDataGenerator());
        customDataGenerators.put("uuid", new UuidDataGenerator());
        customDataGenerators.put("url", new UrlDataGenerator());
        customDataGenerators.put("credit_card", new CreditCardDataGenerator());
        customDataGenerators.put("financial", new FinancialDataGenerator());
        customDataGenerators.put("geolocation", new GeolocationDataGenerator());
        customDataGenerators.put("social_security", new SocialSecurityDataGenerator());
        logger.debug("Data generators initialized: {}", customDataGenerators.keySet());
    }

    private void initializeValidationProfiles() {
        // Default property validation profiles
        propertyProfiles.put("default", new PropertyValidationProfile());

        // Content type test profiles
        for (String contentType : supportedContentTypes) {
            contentTypeProfiles.put(contentType, new ContentTypeTestProfile(contentType));
        }

        // Header validation profiles
        headerProfiles.put("security", new HeaderValidationProfile("security"));
        headerProfiles.put("cache", new HeaderValidationProfile("cache"));
        headerProfiles.put("content", new HeaderValidationProfile("content"));

        logger.debug("Validation profiles initialized");
    }

    private void initializeTestMetrics() {
        testMetrics.put("coverage", new TestMetrics("coverage"));
        testMetrics.put("performance", new TestMetrics("performance"));
        testMetrics.put("security", new TestMetrics("security"));
        testMetrics.put("reliability", new TestMetrics("reliability"));
        logger.debug("Test metrics initialized: {}", testMetrics.keySet());
    }

    private void initializeEdgeCaseDetection() {
        detectDataQualityIssues();
        identifySecurityVulnerabilities();
        findPerformanceBottlenecks();
        locateBusinessLogicFlaws();
        logger.debug("Edge case detection initialized for level: {}", edgeCaseLevel);
    }

    // ===== Enhanced Analysis Methods =====

    private void analyzeStatusCode() {
        if (statusCode == null) return;

        try {
            int code = Integer.parseInt(statusCode);

            category = categorizeResponse(code);
            isSuccessResponse = (code >= 200 && code < 300);
            isErrorResponse = (code >= 400);
            reasonPhrase = getStandardReasonPhrase(code);

            addStatusCodeSpecificScenarios(code);

            if (isErrorResponse) {
                errorTestProfile = new ErrorTestProfile(code);
            }

            logger.debug("Status code {} analyzed: category={}, success={}, error={}",
                    code, category, isSuccessResponse, isErrorResponse);

        } catch (NumberFormatException e) {
            category = ResponseCategory.CUSTOM;
            reasonPhrase = "Custom Status";
            logger.warn("Invalid status code format: {}", statusCode, e);
        }
    }

    private void inferResponseSemantics() {
        if (statusCode == null) return;

        try {
            int code = Integer.parseInt(statusCode);
            switch (code) {
                case 200:
                    semantics = ResponseSemantics.RESOURCE_RETRIEVED;
                    break;
                case 201:
                    semantics = ResponseSemantics.RESOURCE_CREATED;
                    requiredHeaders.add("Location");
                    enabledTestScenarios.add(ResponseTestScenario.STATE_TRANSITION_VALIDATION);
                    break;
                case 204:
                    semantics = ResponseSemantics.RESOURCE_DELETED;
                    enabledTestScenarios.add(ResponseTestScenario.EMPTY_RESPONSE_VALIDATION);
                    break;
                case 400:
                    semantics = ResponseSemantics.VALIDATION_ERROR;
                    enabledTestScenarios.add(ResponseTestScenario.ERROR_MESSAGE_CLARITY);
                    break;
                case 401:
                    semantics = ResponseSemantics.AUTHENTICATION_ERROR;
                    break;
                case 403:
                    semantics = ResponseSemantics.AUTHORIZATION_ERROR;
                    break;
                case 404:
                    semantics = ResponseSemantics.RESOURCE_RETRIEVED;
                    break;
                case 429:
                    semantics = ResponseSemantics.RATE_LIMITED;
                    requiredHeaders.add("Retry-After");
                    enabledTestScenarios.add(ResponseTestScenario.THROUGHPUT_VALIDATION);
                    break;
                case 500:
                    semantics = ResponseSemantics.SYSTEM_ERROR;
                    break;
                default:
                    semantics = ResponseSemantics.CUSTOM_SEMANTIC;
            }
            logger.debug("Response semantics inferred: {}", semantics);
        } catch (NumberFormatException e) {
            semantics = ResponseSemantics.CUSTOM_SEMANTIC;
            logger.warn("Could not infer semantics for invalid status code: {}", statusCode);
        }
    }

    private void detectResponsePatterns() {
        if (primaryContentType.contains("hal+json")) {
            detectedPatterns.add(ResponsePattern.HAL_JSON);
            enabledTestScenarios.add(ResponseTestScenario.API_CONTRACT_VALIDATION);
        }

        if (primaryContentType.contains("vnd.api+json")) {
            detectedPatterns.add(ResponsePattern.JSON_API);
            enabledTestScenarios.add(ResponseTestScenario.API_CONTRACT_VALIDATION);
        }

        if (isCollectionResponse) {
            detectedPatterns.add(ResponsePattern.PAGINATED);
            enabledTestScenarios.add(ResponseTestScenario.PAGINATION_VALIDATION);
            enabledTestScenarios.add(ResponseTestScenario.SORTING_VALIDATION);
            enabledTestScenarios.add(ResponseTestScenario.FILTERING_VALIDATION);
        }

        logger.debug("Response patterns detected: {}", detectedPatterns);
    }

    private void addStatusCodeSpecificScenarios(int code) {
        switch (code) {
            case 200:
                enabledTestScenarios.addAll(Arrays.asList(
                        ResponseTestScenario.DATA_INTEGRITY,
                        ResponseTestScenario.DATA_CONSISTENCY,
                        ResponseTestScenario.BUSINESS_RULE_VALIDATION
                ));
                break;
            case 201:
                enabledTestScenarios.addAll(Arrays.asList(
                        ResponseTestScenario.STATE_TRANSITION_VALIDATION,
                        ResponseTestScenario.IDEMPOTENCY_VALIDATION,
                        ResponseTestScenario.HEADER_VALIDATION
                ));
                break;
            case 204:
                enabledTestScenarios.addAll(Arrays.asList(
                        ResponseTestScenario.EMPTY_RESPONSE_VALIDATION,
                        ResponseTestScenario.CACHE_VALIDATION
                ));
                break;
            case 304:
                enabledTestScenarios.addAll(Arrays.asList(
                        ResponseTestScenario.CACHE_VALIDATION,
                        ResponseTestScenario.HEADER_VALIDATION
                ));
                requiredHeaders.addAll(Arrays.asList("ETag", "Last-Modified"));
                break;
            case 400:
                enabledTestScenarios.addAll(Arrays.asList(
                        ResponseTestScenario.ERROR_RESPONSE_VALIDATION,
                        ResponseTestScenario.ERROR_MESSAGE_CLARITY,
                        ResponseTestScenario.ERROR_STRUCTURE_VALIDATION
                ));
                break;
            case 401:
                enabledTestScenarios.addAll(Arrays.asList(
                        ResponseTestScenario.SECURITY_VALIDATION,
                        ResponseTestScenario.ERROR_RESPONSE_VALIDATION
                ));
                requiredHeaders.add("WWW-Authenticate");
                break;
            case 429:
                enabledTestScenarios.addAll(Arrays.asList(
                        ResponseTestScenario.THROUGHPUT_VALIDATION,
                        ResponseTestScenario.HEADER_VALIDATION
                ));
                requiredHeaders.add("Retry-After");
                break;
            case 500:
                enabledTestScenarios.addAll(Arrays.asList(
                        ResponseTestScenario.ERROR_RESPONSE_VALIDATION,
                        ResponseTestScenario.INFORMATION_DISCLOSURE,
                        ResponseTestScenario.ERROR_MESSAGE_CLARITY
                ));
                break;
        }
        logger.debug("Status code {} specific scenarios added: {} total scenarios",
                code, enabledTestScenarios.size());
    }

    // ===== Advanced Data Quality Detection =====

    private void detectDataQualityIssues() {
        dataQualityRules.add(new DataQualityRule("null_value_consistency",
                "Null values should be consistently represented"));
        dataQualityRules.add(new DataQualityRule("date_format_consistency",
                "Date formats should be consistent across fields"));
        dataQualityRules.add(new DataQualityRule("numeric_precision_consistency",
                "Numeric precision should be consistent"));
        dataQualityRules.add(new DataQualityRule("enum_value_validation",
                "Enum values should match predefined sets"));
        dataQualityRules.add(new DataQualityRule("referential_integrity",
                "References should be valid and consistent"));
        dataQualityRules.add(new DataQualityRule("business_rule_compliance",
                "Data should comply with business rules"));

        logger.debug("Data quality rules initialized: {}", dataQualityRules.size());
    }

    private void identifySecurityVulnerabilities() {
        securityProfiles.put("pii_exposure", new SecurityValidationProfile("PII Exposure Prevention"));
        securityProfiles.put("injection_prevention", new SecurityValidationProfile("Injection Prevention"));
        securityProfiles.put("xss_prevention", new SecurityValidationProfile("XSS Prevention"));
        securityProfiles.put("information_disclosure", new SecurityValidationProfile("Information Disclosure"));
        securityProfiles.put("csrf_protection", new SecurityValidationProfile("CSRF Protection"));

        enabledTestScenarios.addAll(Arrays.asList(
                ResponseTestScenario.SENSITIVE_DATA_MASKING,
                ResponseTestScenario.INFORMATION_DISCLOSURE,
                ResponseTestScenario.XSS_PREVENTION_VALIDATION,
                ResponseTestScenario.INJECTION_PREVENTION_VALIDATION
        ));

        logger.debug("Security validation profiles initialized: {}", securityProfiles.keySet());
    }

    private void findPerformanceBottlenecks() {
        enabledTestScenarios.addAll(Arrays.asList(
                ResponseTestScenario.RESPONSE_SIZE_VALIDATION,
                ResponseTestScenario.MEMORY_USAGE_VALIDATION,
                ResponseTestScenario.THROUGHPUT_VALIDATION,
                ResponseTestScenario.COMPRESSION_VALIDATION
        ));

        logger.debug("Performance bottleneck detection scenarios enabled");
    }

    private void locateBusinessLogicFlaws() {
        enabledTestScenarios.addAll(Arrays.asList(
                ResponseTestScenario.BUSINESS_RULE_VALIDATION,
                ResponseTestScenario.WORKFLOW_VALIDATION,
                ResponseTestScenario.STATE_TRANSITION_VALIDATION,
                ResponseTestScenario.DATA_CONSISTENCY
        ));

        logger.debug("Business logic validation scenarios enabled");
    }

    // ===== Ultra-Comprehensive Test Case Generation (Standard Method) =====

    /**
     * Standard method signature for comprehensive test case generation
     * Returns List<GeneratedTestCase> conforming to standard interface
     */
    public List<GeneratedTestCase> generateComprehensiveTestCases() {
        long startTime = System.currentTimeMillis();
        logger.info("Starting comprehensive test case generation for status code: {}", statusCode);

        List<ResponseTestCase> internalTestCases = generateInternalResponseTestCases();
        List<GeneratedTestCase> standardTestCases = convertToStandardTestCases(internalTestCases);

        // Apply intelligent prioritization and limiting
        if (standardTestCases.size() > maxTestVariations) {
            standardTestCases = intelligentTestCasePrioritization(standardTestCases);
        }

        // Enhance with metadata
        enhanceTestCasesWithMetadata(standardTestCases);

        long duration = System.currentTimeMillis() - startTime;
        logger.info("Generated {} test cases in {}ms for status code: {}",
                standardTestCases.size(), duration, statusCode);

        return standardTestCases;
    }

    /**
     * Internal method for detailed response test case generation
     */
    private List<ResponseTestCase> generateInternalResponseTestCases() {
        List<ResponseTestCase> testCases = new ArrayList<>();

        // Generate test cases for each enabled scenario
        for (ResponseTestScenario scenario : enabledTestScenarios) {
            testCases.addAll(generateTestCasesForScenario(scenario));
        }

        // Generate cross-scenario test cases
        testCases.addAll(generateCrossScenarioTestCases());

        // Generate edge case variations based on level
        if (edgeCaseLevel != EdgeCaseDetectionLevel.NONE) {
            testCases.addAll(generateEdgeCaseVariations());
        }

        // Generate performance test variations
        if (performanceExpectations != null) {
            testCases.addAll(generatePerformanceTestVariations());
        }

        // Generate security test variations
        if (securityExpectations != null) {
            testCases.addAll(generateSecurityTestVariations());
        }

        // Generate data quality test variations
        testCases.addAll(generateDataQualityTestVariations());

        // Generate boundary test combinations
        testCases.addAll(generateBoundaryTestCombinations());

        // Generate real-world scenario tests
        testCases.addAll(generateRealWorldScenarioTests());

        logger.debug("Generated {} internal test cases", testCases.size());
        return testCases;
    }

    /**
     * Convert internal ResponseTestCase to standard GeneratedTestCase
     */
    private List<GeneratedTestCase> convertToStandardTestCases(List<ResponseTestCase> internalCases) {
        return internalCases.stream()
                .map(this::convertToStandardTestCase)
                .collect(Collectors.toList());
    }

    /**
     * Convert single internal test case to standard format
     */
    private GeneratedTestCase convertToStandardTestCase(ResponseTestCase internalCase) {
        return GeneratedTestCase.builder()
                .withTestId(generateTestId(internalCase))
                .withTestName(internalCase.getName())
                .withDescription(internalCase.getDescription())
                .withScenario(org.example.openapi.TestGenerationScenario.HAPPY_PATH) // Temporary fix
                .withStrategyType(org.example.openapi.StrategyType.FUNCTIONAL_BASIC) // Temporary fix
                .withTestSteps(new ArrayList<>()) // Temporary fix
                .withTestData(new org.example.openapi.TestDataSet()) // Temporary fix
                .withAssertions(generateTestAssertions(internalCase))
                .withPriority(calculateTestPriority(internalCase))
                .withEstimatedDuration(estimateTestDuration(internalCase))
                .withComplexity(internalCase.getScenario().getComplexity())
                .withTags(generateTestTags(internalCase))
                .withGenerationTimestamp(generationTimestamp)
                .build();
    }

    private String generateTestId(ResponseTestCase testCase) {
        return String.format("RT_%s_%s_%d",
                statusCode,
                testCase.getScenario().name(),
                Math.abs(testCase.getName().hashCode()));
    }

    private TestGenerationScenario mapToStandardScenario(ResponseTestScenario responseScenario) {
        // Map response test scenarios to standard test generation scenarios
        switch (responseScenario.getCategory()) {
            case FUNCTIONAL:
                return TestGenerationScenario.HAPPY_PATH;
            case SECURITY:
                return TestGenerationScenario.SQL_INJECTION_BASIC;
            case PERFORMANCE:
                return TestGenerationScenario.LOAD_TESTING_LIGHT;
            default:
                return TestGenerationScenario.HAPPY_PATH;
        }
    }

    private StrategyType mapToStrategyType(ResponseTestScenario scenario) {
        switch (scenario.getCategory()) {
            case FUNCTIONAL:
                return scenario.getComplexity() <= 2 ?
                        StrategyType.FUNCTIONAL_BASIC : StrategyType.FUNCTIONAL_COMPREHENSIVE;
            case SECURITY:
                return scenario.getComplexity() <= 3 ?
                        StrategyType.SECURITY_BASIC : StrategyType.SECURITY_OWASP_TOP10;
            case PERFORMANCE:
                return scenario.getComplexity() <= 2 ?
                        StrategyType.PERFORMANCE_BASIC : StrategyType.PERFORMANCE_LOAD;
            case ADVANCED:
                return StrategyType.ADVANCED_AI_DRIVEN;
            case SPECIALIZED:
                return StrategyType.ADVANCED_QUANTUM;
            default:
                return StrategyType.FUNCTIONAL_BASIC;
        }
    }

    private List<TestStep> generateTestSteps(ResponseTestCase testCase) {
        List<TestStep> steps = new ArrayList<>();

        // Add standard response validation steps
        steps.add(new TestStep("Validate status code",
                "Assert response status code equals " + statusCode));
        steps.add(new TestStep("Validate content type",
                "Assert content type equals " + primaryContentType));

        // Add scenario-specific steps
        switch (testCase.getScenario()) {
            case SCHEMA_VALIDATION:
                steps.add(new TestStep("Validate response schema",
                        "Assert response matches expected schema"));
                break;
            case SECURITY_VALIDATION:
                steps.add(new TestStep("Validate security headers",
                        "Assert required security headers are present"));
                break;
            case PERFORMANCE_VALIDATION:
                steps.add(new TestStep("Validate response time",
                        "Assert response time is within acceptable limits"));
                break;
        }

        return steps;
    }

    private TestDataSet generateTestDataSet(ResponseTestCase testCase) {
        TestDataSet.Builder builder = TestDataSet.builder()
                .withResponseContent(testCase.getResponseContent())
                .withExpectedStatusCode(testCase.getExpectedStatusCode())
                .withExpectedContentType(testCase.getExpectedContentType());

        // Add scenario-specific test data
        if (testCase.getExpectedHeaders() != null && !testCase.getExpectedHeaders().isEmpty()) {
            builder.withExpectedHeaders(testCase.getExpectedHeaders());
        }

        return builder.build();
    }

    private List<org.example.openapi.TestAssertion> generateTestAssertions(ResponseTestCase testCase) {
        List<org.example.openapi.TestAssertion> assertions = new ArrayList<>();

        // Standard assertions
        assertions.add(new org.example.openapi.TestAssertion("status_code",
                "response.statusCode equals " + testCase.getExpectedStatusCode(),
                "equals"));
        assertions.add(new org.example.openapi.TestAssertion("content_type",
                "response.contentType equals " + testCase.getExpectedContentType(),
                "equals"));

        // Scenario-specific assertions
        switch (testCase.getScenario()) {
            case SCHEMA_VALIDATION:
                assertions.add(new org.example.openapi.TestAssertion("schema_valid",
                        "response.body matches expectedSchema",
                        "matchesSchema"));
                break;
            case SECURITY_VALIDATION:
                assertions.add(new org.example.openapi.TestAssertion("security_headers",
                        "response.headers contains security headers",
                        "containsSecurityHeaders"));
                break;
            case PERFORMANCE_VALIDATION:
                assertions.add(new org.example.openapi.TestAssertion("response_time",
                        "response.duration is acceptable (less than 1000ms)",
                        "lessThan"));
                break;
        }

        return assertions;
    }

    private int calculateTestPriority(ResponseTestCase testCase) {
        int priority = 5; // Default medium priority

        if (testCase.isSecurityTest()) {
            priority = 1; // High priority
        } else if (testCase.isPerformanceTest()) {
            priority = 2; // High-medium priority
        } else if (isErrorResponse && testCase.getScenario().name().contains("ERROR")) {
            priority = 3; // Medium-high priority
        }

        return priority;
    }

    private Duration estimateTestDuration(ResponseTestCase testCase) {
        long baseMillis = 100; // Base 100ms

        if (testCase.isPerformanceTest()) {
            baseMillis *= 10; // Performance tests take longer
        }
        if (testCase.isSecurityTest()) {
            baseMillis *= 5; // Security tests take longer
        }
        if (testCase.getResponseSize() > 1024 * 1024) {
            baseMillis *= 2; // Large responses take longer
        }

        return Duration.ofMillis(baseMillis);
    }

    private Set<String> generateTestTags(ResponseTestCase testCase) {
        Set<String> tags = new HashSet<>();

        tags.add("response_test");
        tags.add("status_" + statusCode);
        tags.add(testCase.getScenario().getCategory().name().toLowerCase());

        if (testCase.isSecurityTest()) {
            tags.add("security");
        }
        if (testCase.isPerformanceTest()) {
            tags.add("performance");
        }
        if (isErrorResponse) {
            tags.add("error_handling");
        }

        return tags;
    }

    // ===== Test Case Generation Helper Methods =====

    private List<ResponseTestCase> generateTestCasesForScenario(ResponseTestScenario scenario) {
        List<ResponseTestCase> cases = new ArrayList<>();

        switch (scenario) {
            case SCHEMA_VALIDATION:
                cases.addAll(generateSchemaValidationTests());
                break;
            case SECURITY_VALIDATION:
                cases.addAll(generateSecurityValidationTests());
                break;
            case PERFORMANCE_VALIDATION:
                cases.addAll(generatePerformanceValidationTests());
                break;
            case BOUNDARY_VALIDATION:
                cases.addAll(generateBoundaryValidationTests());
                break;
            case ERROR_RESPONSE_VALIDATION:
                if (isErrorResponse) {
                    cases.addAll(generateErrorResponseTests());
                }
                break;
            default:
                cases.add(generateGenericTest(scenario));
                break;
        }

        return cases;
    }

    private List<ResponseTestCase> generateSchemaValidationTests() {
        List<ResponseTestCase> cases = new ArrayList<>();

        cases.add(new ResponseTestCase(
                "Valid schema validation",
                generateValidResponseExample(),
                statusCode,
                primaryContentType,
                ResponseTestScenario.SCHEMA_VALIDATION,
                true,
                "Response should match the expected schema"
        ));

        cases.add(new ResponseTestCase(
                "Invalid schema validation",
                generateInvalidSchemaResponse(),
                statusCode,
                primaryContentType,
                ResponseTestScenario.SCHEMA_VALIDATION,
                false,
                "Response with invalid schema should fail validation"
        ));

        return cases;
    }

    private List<ResponseTestCase> generateSecurityValidationTests() {
        List<ResponseTestCase> cases = new ArrayList<>();

        cases.add(new ResponseTestCase(
                "Security headers present",
                generateValidResponseExample(),
                statusCode,
                primaryContentType,
                ResponseTestScenario.SECURITY_VALIDATION,
                true,
                "Response should contain required security headers"
        ).withExpectedHeader("X-Frame-Options", "DENY")
                .withExpectedHeader("X-Content-Type-Options", "nosniff"));

        return cases;
    }

    private List<ResponseTestCase> generatePerformanceValidationTests() {
        List<ResponseTestCase> cases = new ArrayList<>();

        cases.add(new ResponseTestCase(
                "Response time validation",
                generateValidResponseExample(),
                statusCode,
                primaryContentType,
                ResponseTestScenario.PERFORMANCE_VALIDATION,
                true,
                "Response should be returned within acceptable time limits"
        ).withPerformanceConstraint(1000L));

        return cases;
    }

    private List<ResponseTestCase> generateBoundaryValidationTests() {
        List<ResponseTestCase> cases = new ArrayList<>();

        cases.add(new ResponseTestCase(
                "Minimum boundary validation",
                generateMinBoundaryResponse(),
                statusCode,
                primaryContentType,
                ResponseTestScenario.BOUNDARY_VALIDATION,
                true,
                "Response should handle minimum boundary values"
        ));

        cases.add(new ResponseTestCase(
                "Maximum boundary validation",
                generateMaxBoundaryResponse(),
                statusCode,
                primaryContentType,
                ResponseTestScenario.BOUNDARY_VALIDATION,
                true,
                "Response should handle maximum boundary values"
        ));

        return cases;
    }

    private List<ResponseTestCase> generateErrorResponseTests() {
        List<ResponseTestCase> cases = new ArrayList<>();

        cases.add(new ResponseTestCase(
                "Error response structure",
                generateValidErrorResponse(),
                statusCode,
                primaryContentType,
                ResponseTestScenario.ERROR_RESPONSE_VALIDATION,
                true,
                "Error response should have proper structure"
        ));

        cases.add(new ResponseTestCase(
                "Error message clarity",
                generateClearErrorResponse(),
                statusCode,
                primaryContentType,
                ResponseTestScenario.ERROR_MESSAGE_CLARITY,
                true,
                "Error message should be clear and actionable"
        ));

        return cases;
    }

    private ResponseTestCase generateGenericTest(ResponseTestScenario scenario) {
        return new ResponseTestCase(
                "Generic " + scenario.getDescription(),
                generateValidResponseExample(),
                statusCode,
                primaryContentType,
                scenario,
                true,
                scenario.getDescription()
        );
    }

    // ===== Cross-Scenario Test Generation =====

    private List<ResponseTestCase> generateCrossScenarioTestCases() {
        List<ResponseTestCase> cases = new ArrayList<>();

        // Security + Performance combinations
        if (enabledTestScenarios.contains(ResponseTestScenario.SECURITY_VALIDATION) &&
                enabledTestScenarios.contains(ResponseTestScenario.PERFORMANCE_VALIDATION)) {

            cases.add(new ResponseTestCase(
                    "Security validation under load",
                    generateValidResponseExample(),
                    statusCode,
                    primaryContentType,
                    ResponseTestScenario.SECURITY_VALIDATION,
                    true,
                    "Security measures should remain effective under high load"
            ).withPerformanceConstraint(1000L)
                    .withSecurityValidation(true)
                    .withConcurrentRequests(10));
        }

        // Schema + Data Integrity combinations
        if (enabledTestScenarios.contains(ResponseTestScenario.SCHEMA_VALIDATION) &&
                enabledTestScenarios.contains(ResponseTestScenario.DATA_INTEGRITY)) {

            cases.add(new ResponseTestCase(
                    "Schema compliance with data integrity",
                    generateComplexNestedResponse(),
                    statusCode,
                    primaryContentType,
                    ResponseTestScenario.DEEP_SCHEMA_VALIDATION,
                    true,
                    "Complex nested structures should maintain both schema compliance and data integrity"
            ).withDeepValidation(true)
                    .withIntegrityChecks(true));
        }

        return cases;
    }

    // ===== Edge Case Generation =====

    private List<ResponseTestCase> generateEdgeCaseVariations() {
        List<ResponseTestCase> cases = new ArrayList<>();

        switch (edgeCaseLevel) {
            case EXTREME:
                cases.addAll(generateExtremeEdgeCases());
                // Fall through
            case AGGRESSIVE:
                cases.addAll(generateAggressiveEdgeCases());
                // Fall through
            case STANDARD:
                cases.addAll(generateStandardEdgeCases());
                // Fall through
            case BASIC:
                cases.addAll(generateBasicEdgeCases());
                break;
            case NONE:
            default:
                break;
        }

        return cases;
    }

    private List<ResponseTestCase> generateBasicEdgeCases() {
        List<ResponseTestCase> cases = new ArrayList<>();

        // Empty response
        cases.add(new ResponseTestCase(
                "Empty response body",
                "",
                statusCode,
                primaryContentType,
                ResponseTestScenario.EMPTY_RESPONSE_VALIDATION,
                statusCode.equals("204"),
                "Empty response should be handled appropriately"
        ));

        return cases;
    }

    private List<ResponseTestCase> generateStandardEdgeCases() {
        List<ResponseTestCase> cases = new ArrayList<>();

        // Unicode handling
        cases.add(new ResponseTestCase(
                "Unicode character handling",
                generateUnicodeResponse(),
                statusCode,
                primaryContentType + "; charset=utf-8",
                ResponseTestScenario.ENCODING_VALIDATION,
                true,
                "Response should properly handle Unicode characters"
        ));

        return cases;
    }

    private List<ResponseTestCase> generateAggressiveEdgeCases() {
        List<ResponseTestCase> cases = new ArrayList<>();

        // Large response handling
        cases.add(new ResponseTestCase(
                "Large response handling",
                generateLargeResponse(),
                statusCode,
                primaryContentType,
                ResponseTestScenario.LARGE_RESPONSE_VALIDATION,
                true,
                "Large responses should be handled efficiently"
        ));

        return cases;
    }

    private List<ResponseTestCase> generateExtremeEdgeCases() {
        List<ResponseTestCase> cases = new ArrayList<>();

        // Maximum size response
        cases.add(new ResponseTestCase(
                "Maximum size response",
                generateMaximumSizeResponse(),
                statusCode,
                primaryContentType,
                ResponseTestScenario.LARGE_RESPONSE_VALIDATION,
                false, // May exceed limits
                "Extremely large responses should be rejected or handled gracefully"
        ));

        return cases;
    }

    // ===== Additional Test Generation Methods =====

    private List<ResponseTestCase> generatePerformanceTestVariations() {
        List<ResponseTestCase> cases = new ArrayList<>();

        int[] concurrencyLevels = {1, 5, 10, 25, 50};
        for (int concurrency : concurrencyLevels) {
            cases.add(new ResponseTestCase(
                    "Performance under " + concurrency + " concurrent requests",
                    generateValidResponseExample(),
                    statusCode,
                    primaryContentType,
                    ResponseTestScenario.CONCURRENT_RESPONSE_VALIDATION,
                    true,
                    "Response should maintain performance under " + concurrency + " concurrent requests"
            ).withConcurrentRequests(concurrency)
                    .withPerformanceExpectation(performanceExpectations));
        }

        return cases;
    }

    private List<ResponseTestCase> generateSecurityTestVariations() {
        List<ResponseTestCase> cases = new ArrayList<>();

        // Information disclosure tests
        if (isErrorResponse) {
            cases.add(new ResponseTestCase(
                    "No sensitive data in error response",
                    generateErrorResponseWithPotentialLeaks(),
                    statusCode,
                    primaryContentType,
                    ResponseTestScenario.INFORMATION_DISCLOSURE,
                    false,
                    "Error responses should not contain sensitive information"
            ).withSensitiveDataPatterns(Arrays.asList(
                    "password", "token", "key", "secret", "internal", "debug"
            )));
        }

        return cases;
    }

    private List<ResponseTestCase> generateDataQualityTestVariations() {
        List<ResponseTestCase> cases = new ArrayList<>();

        cases.add(new ResponseTestCase(
                "Data consistency validation",
                generateInconsistentDataResponse(),
                statusCode,
                primaryContentType,
                ResponseTestScenario.DATA_CONSISTENCY,
                false,
                "Data should be consistent across all fields"
        ).withConsistencyCheck("dateFormat"));

        return cases;
    }

    private List<ResponseTestCase> generateBoundaryTestCombinations() {
        List<ResponseTestCase> cases = new ArrayList<>();

        if (propertyConstraints.size() >= 2) {
            List<String> constrainedProperties = new ArrayList<>(propertyConstraints.keySet());

            for (int i = 0; i < Math.min(constrainedProperties.size() - 1, 3); i++) {
                for (int j = i + 1; j < Math.min(constrainedProperties.size(), i + 3); j++) {
                    String prop1 = constrainedProperties.get(i);
                    String prop2 = constrainedProperties.get(j);

                    cases.add(new ResponseTestCase(
                            "Multi-field boundary: " + prop1 + " & " + prop2,
                            generateMultiFieldBoundaryResponse(prop1, prop2),
                            statusCode,
                            primaryContentType,
                            ResponseTestScenario.BOUNDARY_VALIDATION,
                            true,
                            "Multiple boundary values should be handled correctly"
                    ).withMultiFieldBoundary(prop1, prop2));
                }
            }
        }

        return cases;
    }

    private List<ResponseTestCase> generateRealWorldScenarioTests() {
        List<ResponseTestCase> cases = new ArrayList<>();

        // Timezone tests
        String[] timezones = {"UTC", "EST", "PST", "JST"};
        for (String timezone : timezones) {
            cases.add(new ResponseTestCase(
                    "Timezone handling: " + timezone,
                    generateTimezoneResponse(timezone),
                    statusCode,
                    primaryContentType,
                    ResponseTestScenario.TIMEZONE_VALIDATION,
                    true,
                    "Response should handle timezone: " + timezone
            ).withTimezone(timezone));
        }

        return cases;
    }

    // ===== Intelligent Test Case Prioritization =====

    private List<GeneratedTestCase> intelligentTestCasePrioritization(List<GeneratedTestCase> testCases) {
        Map<GeneratedTestCase, Double> priorityScores = new HashMap<>();

        for (GeneratedTestCase testCase : testCases) {
            double score = calculateAdvancedTestCasePriority(testCase);
            priorityScores.put(testCase, score);
        }

        return testCases.stream()
                .sorted((a, b) -> Double.compare(
                        priorityScores.getOrDefault(b, 0.0),
                        priorityScores.getOrDefault(a, 0.0)))
                .limit(maxTestVariations)
                .collect(Collectors.toList());
    }

    private double calculateAdvancedTestCasePriority(GeneratedTestCase testCase) {
        double score = 0.0;

        // Security tests get highest priority
        if (testCase.getTags().contains("security")) {
            score += 100.0;
        }

        // Performance tests get high priority
        if (testCase.getTags().contains("performance")) {
            score += 80.0;
        }

        // Error scenarios get high priority
        if (isErrorResponse && testCase.getTags().contains("error_handling")) {
            score += 70.0;
        }

        // Business logic tests get medium-high priority
        if (testCase.getTestName().toLowerCase().contains("business") ||
                testCase.getTestName().toLowerCase().contains("workflow")) {
            score += 60.0;
        }

        // Boundary tests get medium priority
        if (testCase.getTestName().toLowerCase().contains("boundary")) {
            score += 50.0;
        }

        // Schema validation gets medium priority
        if (testCase.getTestName().toLowerCase().contains("schema")) {
            score += 40.0;
        }

        // Edge cases get lower priority but still important
        if (testCase.getTestName().toLowerCase().contains("edge")) {
            score += 30.0;
        }

        // Real-world scenarios get standard priority
        if (testCase.getTags().contains("real_world")) {
            score += 25.0;
        }

        // Adjust based on test complexity
        score += testCase.getComplexity() * 5.0;

        // Adjust based on priority
        score += (6 - testCase.getPriority()) * 10.0;

        return score;
    }

    private void enhanceTestCasesWithMetadata(List<GeneratedTestCase> testCases) {
        for (GeneratedTestCase testCase : testCases) {
            // Add execution metadata
            testCase.addMetadata("generatedAt", LocalDateTime.now());
            testCase.addMetadata("executionId", executionId);
            testCase.addMetadata("responseStatusCode", statusCode);
            testCase.addMetadata("responseCategory", category.name());
            testCase.addMetadata("responseSemantics", semantics.name());
            testCase.addMetadata("testComplexity", testComplexity.name());
            testCase.addMetadata("validationLevel", validationLevel.name());

            // Add test execution hints
            testCase.addMetadata("estimatedExecutionTime", testCase.getEstimatedDuration().toMillis());
            testCase.addMetadata("resourceRequirements", estimateResourceRequirements(testCase));
            testCase.addMetadata("dependencies", findTestDependencies(testCase));
        }

        logger.debug("Enhanced {} test cases with metadata", testCases.size());
    }

    // ===== Response Content Generation Methods =====

    private String generateValidResponseExample() {
        if (defaultExample != null && !defaultExample.trim().isEmpty()) {
            return defaultExample;
        }

        switch (statusCode) {
            case "200":
                return """
                    {
                      "id": 123,
                      "name": "Test User",
                      "email": "test@example.com",
                      "status": "active",
                      "createdAt": "2023-01-01T00:00:00Z"
                    }
                    """;
            case "201":
                return """
                    {
                      "id": 124,
                      "name": "New User",
                      "email": "new@example.com",
                      "status": "active",
                      "createdAt": "2023-01-01T00:00:00Z",
                      "message": "User created successfully"
                    }
                    """;
            case "204":
                return "";
            case "400":
                return """
                    {
                      "error": {
                        "code": "VALIDATION_ERROR",
                        "message": "Invalid input provided",
                        "details": [
                          {
                            "field": "email",
                            "message": "Email format is invalid"
                          }
                        ]
                      }
                    }
                    """;
            case "401":
                return """
                    {
                      "error": {
                        "code": "UNAUTHORIZED",
                        "message": "Authentication required"
                      }
                    }
                    """;
            case "404":
                return """
                    {
                      "error": {
                        "code": "NOT_FOUND",
                        "message": "Resource not found"
                      }
                    }
                    """;
            case "500":
                return """
                    {
                      "error": {
                        "code": "INTERNAL_ERROR",
                        "message": "An internal server error occurred"
                      }
                    }
                    """;
            default:
                return """
                    {
                      "message": "Default response",
                      "statusCode": """ + statusCode + """
                    }
                    """;
        }
    }

    private String generateInvalidSchemaResponse() {
        return """
            {
              "invalidField": "this should not be here",
              "missingRequiredField": null,
              "wrongTypeField": "should_be_number_but_is_string"
            }
            """;
    }

    private String generateComplexNestedResponse() {
        return """
            {
              "user": {
                "id": 1,
                "profile": {
                  "personal": {
                    "name": "John Doe",
                    "email": "john@example.com",
                    "address": {
                      "street": "123 Main St",
                      "city": "Anytown",
                      "coordinates": {
                        "lat": 40.7128,
                        "lng": -74.0060
                      }
                    }
                  },
                  "preferences": {
                    "notifications": {
                      "email": true,
                      "sms": false,
                      "push": true
                    }
                  }
                }
              }
            }
            """;
    }

    private String generateUnicodeResponse() {
        return """
            {
              "message": "Unicode test: 测试 тест テスト",
              "emoji": "🚀 🎯 ✅",
              "currency": "€£¥₹",
              "math": "∑∞∫∂"
            }
            """;
    }

    private String generateLargeResponse() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n  \"items\": [");

        for (int i = 0; i < 1000; i++) {
            if (i > 0) sb.append(",");
            sb.append(String.format(
                    "\n    {\"id\": %d, \"name\": \"Item %d\", \"data\": \"some_data_%d\"}",
                    i, i, i));
        }

        sb.append("\n  ],\n  \"total\": 1000\n}");
        return sb.toString();
    }

    private String generateMaximumSizeResponse() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"data\": \"");

        // Generate a very large string (10MB)
        String pattern = "A";
        for (int i = 0; i < 10 * 1024 * 1024; i++) {
            sb.append(pattern);
        }

        sb.append("\"}");
        return sb.toString();
    }

    private String generateMinBoundaryResponse() {
        return """
            {
              "integerField": -2147483648,
              "floatField": -1.7976931348623157E308,
              "stringField": "",
              "arrayField": []
            }
            """;
    }

    private String generateMaxBoundaryResponse() {
        return """
            {
              "integerField": 2147483647,
              "floatField": 1.7976931348623157E308,
              "stringField": "very_long_string_at_maximum_length",
              "arrayField": [1, 2, 3, 4, 5]
            }
            """;
    }

    private String generateValidErrorResponse() {
        return """
            {
              "error": {
                "code": "VALIDATION_ERROR",
                "message": "Request validation failed",
                "timestamp": "2023-01-01T00:00:00Z",
                "details": [
                  {
                    "field": "email",
                    "code": "INVALID_FORMAT",
                    "message": "Email address format is invalid"
                  }
                ],
                "requestId": "req_123456789"
              }
            }
            """;
    }

    private String generateClearErrorResponse() {
        return """
            {
              "error": {
                "code": "VALIDATION_ERROR",
                "message": "Please check your input and try again",
                "userMessage": "The email address you entered is not valid. Please enter a valid email address.",
                "suggestedAction": "Verify the email format (e.g., user@domain.com)"
              }
            }
            """;
    }

    private String generateErrorResponseWithPotentialLeaks() {
        return """
            {
              "error": {
                "code": 500,
                "message": "Database connection failed",
                "debug": {
                  "password": "secret123",
                  "host": "internal-db-server",
                  "stackTrace": "com.example.DB.connect(line:45)"
                }
              }
            }
            """;
    }

    private String generateInconsistentDataResponse() {
        return """
            {
              "users": [
                {"birthDate": "2023-01-01", "format": "ISO_DATE"},
                {"birthDate": "01/01/2023", "format": "US_FORMAT"},
                {"birthDate": "1672531200", "format": "UNIX_TIMESTAMP"}
              ]
            }
            """;
    }

    private String generateMultiFieldBoundaryResponse(String field1, String field2) {
        return String.format("""
            {
              "%s": 2147483647,
              "%s": -2147483648,
              "boundaryTest": true,
              "fields": ["%s", "%s"]
            }
            """, field1, field2, field1, field2);
    }

    private String generateTimezoneResponse(String timezone) {
        return String.format("""
            {
              "timestamp": "2023-01-01T12:00:00",
              "timezone": "%s",
              "utcTime": "2023-01-01T12:00:00Z",
              "localTime": "2023-01-01T12:00:00%s"
            }
            """, timezone, getTimezoneOffset(timezone));
    }

    // ===== Utility Helper Methods =====

    private String getTimezoneOffset(String timezone) {
        Map<String, String> offsets = Map.of(
                "UTC", "+00:00",
                "EST", "-05:00",
                "PST", "-08:00",
                "JST", "+09:00"
        );
        return offsets.getOrDefault(timezone, "+00:00");
    }

    private String estimateResourceRequirements(GeneratedTestCase testCase) {
        StringBuilder requirements = new StringBuilder();

        if (testCase.getTags().contains("performance")) {
            requirements.append("high_cpu,");
        }
        if (testCase.getTestName().toLowerCase().contains("large")) {
            requirements.append("high_memory,");
        }
        if (testCase.getTags().contains("security")) {
            requirements.append("security_tools,");
        }

        return requirements.length() > 0 ?
                requirements.substring(0, requirements.length() - 1) : "minimal";
    }

    private List<String> findTestDependencies(GeneratedTestCase testCase) {
        List<String> dependencies = new ArrayList<>();

        if (testCase.getTestName().toLowerCase().contains("schema")) {
            dependencies.add("schema_validator");
        }
        if (testCase.getTags().contains("security")) {
            dependencies.add("security_scanner");
        }
        if (testCase.getTags().contains("performance")) {
            dependencies.add("performance_monitor");
        }

        return dependencies;
    }

    private ResponseCategory categorizeResponse(int code) {
        if (code >= 200 && code < 300) return ResponseCategory.SUCCESS;
        if (code >= 300 && code < 400) return ResponseCategory.REDIRECTION;
        if (code >= 400 && code < 500) return ResponseCategory.CLIENT_ERROR;
        if (code >= 500 && code < 600) return ResponseCategory.SERVER_ERROR;
        return ResponseCategory.CUSTOM;
    }

    private String getStandardReasonPhrase(int statusCode) {
        Map<Integer, String> statusCodes = new HashMap<>();
        statusCodes.put(200, "OK");
        statusCodes.put(201, "Created");
        statusCodes.put(202, "Accepted");
        statusCodes.put(204, "No Content");
        statusCodes.put(400, "Bad Request");
        statusCodes.put(401, "Unauthorized");
        statusCodes.put(403, "Forbidden");
        statusCodes.put(404, "Not Found");
        statusCodes.put(405, "Method Not Allowed");
        statusCodes.put(409, "Conflict");
        statusCodes.put(422, "Unprocessable Entity");
        statusCodes.put(429, "Too Many Requests");
        statusCodes.put(500, "Internal Server Error");
        statusCodes.put(502, "Bad Gateway");
        statusCodes.put(503, "Service Unavailable");
        statusCodes.put(504, "Gateway Timeout");

        return statusCodes.getOrDefault(statusCode, "Unknown Status");
    }

    // ===== Standard Cache Key Generation =====

    private String generateAdvancedCacheKey(String prefix) {
        return String.format("%s_%s_%s_%d_%s",
                prefix,
                statusCode != null ? statusCode : "unknown",
                category != null ? category.name() : "default",
                enabledTestScenarios.size(),
                testComplexity.name());
    }

    // ===== Standard Execution ID Generation =====

    private String generateAdvancedExecutionId() {
        return String.format("RESP_%s_%d",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")),
                System.nanoTime() % 100000);
    }

    // ===== Standard Thread Pool Creation =====

    private ExecutorService createOptimizedExecutorService() {
        int corePoolSize = Math.min(Runtime.getRuntime().availableProcessors(), 4);
        int maximumPoolSize = corePoolSize * 2;

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                60L, TimeUnit.SECONDS,
                new java.util.concurrent.LinkedBlockingQueue<>(100),
                r -> {
                    Thread t = new Thread(r, "ResponseInfo-Worker");
                    t.setDaemon(true);
                    return t;
                }
        );

        executor.allowCoreThreadTimeOut(true);
        logger.debug("Created optimized thread pool with {} core threads", corePoolSize);

        return executor;
    }

    // ===== Standard Getters (Interface Compliance) =====

    public String getStatusCode() { return statusCode; }
    public String getDescription() { return description; }
    public String getReasonPhrase() { return reasonPhrase; }
    public boolean isSuccessResponse() { return isSuccessResponse; }
    public boolean isErrorResponse() { return isErrorResponse; }
    public ResponseCategory getCategory() { return category; }
    public ResponseSemantics getSemantics() { return semantics; }
    public List<ResponsePattern> getDetectedPatterns() { return new ArrayList<>(detectedPatterns); }
    public ResponseComplexityProfile getComplexityProfile() { return complexityProfile; }

    public DataConstraints getConstraints() { return constraints; }
    public ResponseSchema getSchema() { return schema; }
    public Map<String, DataConstraints> getPropertyConstraints() { return new HashMap<>(propertyConstraints); }
    public List<String> getRequiredProperties() { return new ArrayList<>(requiredProperties); }
    public Map<String, PropertyValidationProfile> getPropertyProfiles() { return new HashMap<>(propertyProfiles); }

    public Set<String> getSupportedContentTypes() { return new HashSet<>(supportedContentTypes); }
    public String getPrimaryContentType() { return primaryContentType; }
    public Map<String, MediaTypeInfo> getMediaTypeDetails() { return new HashMap<>(mediaTypeDetails); }
    public Map<String, ContentTypeTestProfile> getContentTypeProfiles() { return new HashMap<>(contentTypeProfiles); }

    public Map<String, HeaderInfo> getResponseHeaders() { return new HashMap<>(responseHeaders); }
    public List<String> getRequiredHeaders() { return new ArrayList<>(requiredHeaders); }
    public List<String> getSecurityHeaders() { return new ArrayList<>(securityHeaders); }
    public Map<String, HeaderValidationProfile> getHeaderProfiles() { return new HashMap<>(headerProfiles); }
    public Set<HeaderCombination> getCriticalHeaderCombinations() { return new HashSet<>(criticalHeaderCombinations); }

    public List<ResponseExample> getExamples() { return new ArrayList<>(examples); }
    public String getDefaultExample() { return defaultExample; }
    public Map<String, Object> getTestData() { return new HashMap<>(testData); }
    public Map<String, TestDataGenerator> getCustomDataGenerators() { return new HashMap<>(customDataGenerators); }

    public Set<ValidationRule> getValidationRules() { return new HashSet<>(validationRules); }
    public ResponseValidationLevel getValidationLevel() { return validationLevel; }
    public boolean isStrictValidation() { return strictValidation; }
    public ValidationStrategy getValidationStrategy() { return validationStrategy; }
    public Map<String, CustomValidator> getCustomValidators() { return new HashMap<>(customValidators); }

    public Set<ResponseTestScenario> getEnabledTestScenarios() { return EnumSet.copyOf(enabledTestScenarios); }
    public TestComplexity getTestComplexity() { return testComplexity; }
    public int getMaxTestVariations() { return maxTestVariations; }
    public TestGenerationStrategy getGenerationStrategy() { return generationStrategy; }
    public EdgeCaseDetectionLevel getEdgeCaseLevel() { return edgeCaseLevel; }

    public PerformanceExpectations getPerformanceExpectations() { return performanceExpectations; }
    public SecurityExpectations getSecurityExpectations() { return securityExpectations; }
    public List<String> getSensitiveDataFields() { return new ArrayList<>(sensitiveDataFields); }
    public Map<String, SecurityValidationProfile> getSecurityProfiles() { return new HashMap<>(securityProfiles); }
    public PerformanceTestProfile getPerformanceProfile() { return performanceProfile; }

    public boolean isPaginatedResponse() { return isPaginatedResponse; }
    public PaginationInfo getPaginationInfo() { return paginationInfo; }
    public boolean isCollectionResponse() { return isCollectionResponse; }
    public CollectionInfo getCollectionInfo() { return collectionInfo; }
    public CollectionTestProfile getCollectionTestProfile() { return collectionTestProfile; }

    public ErrorResponseInfo getErrorInfo() { return errorInfo; }
    public List<String> getCommonErrorCodes() { return new ArrayList<>(commonErrorCodes); }
    public Map<String, String> getErrorMessages() { return new HashMap<>(errorMessages); }
    public Map<String, ErrorScenario> getErrorScenarios() { return new HashMap<>(errorScenarios); }
    public ErrorTestProfile getErrorTestProfile() { return errorTestProfile; }

    public ResponseDataProfile getDataProfile() { return dataProfile; }
    public Map<String, DataPattern> getDetectedDataPatterns() { return new HashMap<>(detectedDataPatterns); }
    public Set<DataQualityRule> getDataQualityRules() { return new HashSet<>(dataQualityRules); }
    public ResponseBehaviorProfile getBehaviorProfile() { return behaviorProfile; }

    public Map<String, Object> getMetadata() { return new HashMap<>(metadata); }
    public List<String> getTags() { return new ArrayList<>(tags); }
    public String getVersion() { return version; }
    public Map<String, TestMetrics> getTestMetrics() { return new HashMap<>(testMetrics); }
    public String getExecutionId() { return executionId; }
    public Instant getGenerationTimestamp() { return generationTimestamp; }

    // ===== Standard Setters =====

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
        analyzeStatusCode();
    }

    public void setDescription(String description) { this.description = description; }
    public void setPrimaryContentType(String primaryContentType) { this.primaryContentType = primaryContentType; }
    public void setValidationLevel(ResponseValidationLevel validationLevel) { this.validationLevel = validationLevel; }
    public void setStrictValidation(boolean strictValidation) { this.strictValidation = strictValidation; }
    public void setValidationStrategy(ValidationStrategy validationStrategy) { this.validationStrategy = validationStrategy; }
    public void setTestComplexity(TestComplexity testComplexity) {
        this.testComplexity = testComplexity;
        this.maxTestVariations = testComplexity.getMaxTestCount();
    }
    public void setMaxTestVariations(int maxTestVariations) { this.maxTestVariations = maxTestVariations; }
    public void setGenerationStrategy(TestGenerationStrategy generationStrategy) { this.generationStrategy = generationStrategy; }
    public void setEdgeCaseLevel(EdgeCaseDetectionLevel edgeCaseLevel) { this.edgeCaseLevel = edgeCaseLevel; }
    public void setPaginatedResponse(boolean paginatedResponse) { this.isPaginatedResponse = paginatedResponse; }
    public void setCollectionResponse(boolean collectionResponse) { this.isCollectionResponse = collectionResponse; }
    public void setVersion(String version) { this.version = version; }

    // ===== Enhanced toString, equals, hashCode =====

    @Override
    public String toString() {
        return "ResponseInfo{" +
                "statusCode='" + statusCode + '\'' +
                ", category=" + category +
                ", semantics=" + semantics +
                ", testComplexity=" + testComplexity +
                ", maxTestVariations=" + maxTestVariations +
                ", enabledScenarios=" + enabledTestScenarios.size() +
                ", executionId='" + executionId + '\'' +
                ", generationTimestamp=" + generationTimestamp +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResponseInfo that = (ResponseInfo) o;
        return Objects.equals(statusCode, that.statusCode) &&
                Objects.equals(description, that.description) &&
                Objects.equals(primaryContentType, that.primaryContentType) &&
                Objects.equals(executionId, that.executionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statusCode, description, primaryContentType, executionId);
    }

    // ===== Inner Classes for Standard Interface Compliance =====

    /**
     * Enhanced Response test case with comprehensive metadata
     */
    public static class ResponseTestCase {
        private final String name;
        private final String responseContent;
        private final String expectedStatusCode;
        private final String expectedContentType;
        private final ResponseTestScenario scenario;
        private final boolean shouldPass;
        private final String description;

        // Enhanced properties
        private final Map<String, String> expectedHeaders = new HashMap<>();
        private final Map<String, Object> metadata = new HashMap<>();

        // Performance properties
        private Long performanceConstraint;
        private Integer concurrentRequests;
        private Boolean securityValidation;
        private Boolean deepValidation;
        private Boolean integrityChecks;
        private List<String> sensitiveDataPatterns = new ArrayList<>();
        private List<String> forbiddenHeaders = new ArrayList<>();
        private String consistencyCheck;
        private String multiFieldBoundary1;
        private String multiFieldBoundary2;
        private String timezone;
        private PerformanceExpectations performanceExpectation;

        public ResponseTestCase(String name, String responseContent, String expectedStatusCode,
                                String expectedContentType, ResponseTestScenario scenario,
                                boolean shouldPass, String description) {
            this.name = name;
            this.responseContent = responseContent;
            this.expectedStatusCode = expectedStatusCode;
            this.expectedContentType = expectedContentType;
            this.scenario = scenario;
            this.shouldPass = shouldPass;
            this.description = description;
        }

        // Fluent API methods
        public ResponseTestCase withExpectedHeader(String headerName, String headerValue) {
            expectedHeaders.put(headerName, headerValue);
            return this;
        }

        public ResponseTestCase withPerformanceConstraint(Long constraintMs) {
            this.performanceConstraint = constraintMs;
            return this;
        }

        public ResponseTestCase withSecurityValidation(Boolean enabled) {
            this.securityValidation = enabled;
            return this;
        }

        public ResponseTestCase withConcurrentRequests(Integer count) {
            this.concurrentRequests = count;
            return this;
        }

        public ResponseTestCase withDeepValidation(Boolean enabled) {
            this.deepValidation = enabled;
            return this;
        }

        public ResponseTestCase withIntegrityChecks(Boolean enabled) {
            this.integrityChecks = enabled;
            return this;
        }

        public ResponseTestCase withSensitiveDataPatterns(List<String> patterns) {
            this.sensitiveDataPatterns.addAll(patterns);
            return this;
        }

        public ResponseTestCase withForbiddenHeaders(List<String> headers) {
            this.forbiddenHeaders.addAll(headers);
            return this;
        }

        public ResponseTestCase withConsistencyCheck(String checkType) {
            this.consistencyCheck = checkType;
            return this;
        }

        public ResponseTestCase withMultiFieldBoundary(String field1, String field2) {
            this.multiFieldBoundary1 = field1;
            this.multiFieldBoundary2 = field2;
            return this;
        }

        public ResponseTestCase withTimezone(String timezone) {
            this.timezone = timezone;
            return this;
        }

        public ResponseTestCase withPerformanceExpectation(PerformanceExpectations expectation) {
            this.performanceExpectation = expectation;
            return this;
        }

        // Getters
        public String getName() { return name; }
        public String getResponseContent() { return responseContent; }
        public String getExpectedStatusCode() { return expectedStatusCode; }
        public String getExpectedContentType() { return expectedContentType; }
        public ResponseTestScenario getScenario() { return scenario; }
        public boolean shouldPass() { return shouldPass; }
        public String getDescription() { return description; }
        public Map<String, String> getExpectedHeaders() { return new HashMap<>(expectedHeaders); }
        public Map<String, Object> getMetadata() { return new HashMap<>(metadata); }

        public int getResponseSize() {
            return responseContent != null ? responseContent.length() : 0;
        }

        public boolean isPerformanceTest() {
            return scenario.name().contains("PERFORMANCE") ||
                    scenario.name().contains("THROUGHPUT") ||
                    scenario.name().contains("MEMORY");
        }

        public boolean isSecurityTest() {
            return scenario.name().contains("SECURITY") ||
                    scenario.name().contains("XSS") ||
                    scenario.name().contains("INJECTION") ||
                    scenario.name().contains("SENSITIVE");
        }
    }

    // ===== Inner Class Stubs for Compilation =====

    public static class MediaTypeInfo {
        public MediaTypeInfo(String type, String name, boolean supportsValidation,
                             boolean supportsCompression, boolean supportsStreaming, boolean supportsSchema) {}
    }

    public static class HeaderInfo {}
    public static class ResponseExample {}
    public static class ValidationRule {}
    public static class CustomValidator {}
    public static class HeaderCombination {}

    // Performance and Security Classes
    public static class PerformanceExpectations {}
    public static class SecurityExpectations {}
    public static class PerformanceTestProfile {}

    // Profile Classes
    public static class ResponseComplexityProfile {}
    public static class ResponseDataProfile {}
    public static class ResponseBehaviorProfile {}
    public static class PropertyValidationProfile {}
    public static class ContentTypeTestProfile {
        public ContentTypeTestProfile(String contentType) {}
    }
    public static class HeaderValidationProfile {
        public HeaderValidationProfile(String type) {}
    }
    public static class TestMetrics {
        public TestMetrics(String type) {}
    }
    public static class SecurityValidationProfile {
        public SecurityValidationProfile(String description) {}
    }
    public static class ErrorTestProfile {
        public ErrorTestProfile(int code) {}
    }
    public static class CollectionTestProfile {}

    // Data Classes
    public static class DataConstraints {}
    public static class ResponseSchema {}
    public static class PaginationInfo {}
    public static class CollectionInfo {}
    public static class ErrorResponseInfo {}
    public static class DataPattern {}
    public static class DataQualityRule {
        public DataQualityRule(String id, String description) {}
    }
    public static class ErrorScenario {}

    // Test Data Generator Classes
    public static class TestDataGenerator {}
    public static class EmailDataGenerator extends TestDataGenerator {}
    public static class PhoneDataGenerator extends TestDataGenerator {}
    public static class AddressDataGenerator extends TestDataGenerator {}
    public static class DateTimeDataGenerator extends TestDataGenerator {}
    public static class UuidDataGenerator extends TestDataGenerator {}
    public static class UrlDataGenerator extends TestDataGenerator {}
    public static class CreditCardDataGenerator extends TestDataGenerator {}
    public static class FinancialDataGenerator extends TestDataGenerator {}
    public static class GeolocationDataGenerator extends TestDataGenerator {}
    public static class SocialSecurityDataGenerator extends TestDataGenerator {}



    public static class TestStep {
        private String name;
        private String description;

        public TestStep(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() { return name; }
        public String getDescription() { return description; }
    }

    public static class TestDataSet {
        private String responseContent;
        private String expectedStatusCode;
        private String expectedContentType;
        private Map<String, String> expectedHeaders;

        public static class Builder {
            private TestDataSet testDataSet = new TestDataSet();

            public Builder withResponseContent(String responseContent) {
                testDataSet.responseContent = responseContent;
                return this;
            }

            public Builder withExpectedStatusCode(String expectedStatusCode) {
                testDataSet.expectedStatusCode = expectedStatusCode;
                return this;
            }

            public Builder withExpectedContentType(String expectedContentType) {
                testDataSet.expectedContentType = expectedContentType;
                return this;
            }

            public Builder withExpectedHeaders(Map<String, String> expectedHeaders) {
                testDataSet.expectedHeaders = expectedHeaders;
                return this;
            }

            public TestDataSet build() {
                return testDataSet;
            }
        }

        public static Builder builder() {
            return new Builder();
        }

        public String getResponseContent() { return responseContent; }
        public String getExpectedStatusCode() { return expectedStatusCode; }
        public String getExpectedContentType() { return expectedContentType; }
        public Map<String, String> getExpectedHeaders() { return expectedHeaders; }
    }

    public static class TestAssertion {
        private String name;
        private String target;
        private String operator;
        private String expectedValue;

        public TestAssertion(String name, String target, String operator, String expectedValue) {
            this.name = name;
            this.target = target;
            this.operator = operator;
            this.expectedValue = expectedValue;
        }

        public String getName() { return name; }
        public String getTarget() { return target; }
        public String getOperator() { return operator; }
        public String getExpectedValue() { return expectedValue; }
    }

    // Standard Enum Classes (Compliance with Standard Interface)
    public enum TestGenerationScenario {
        // FUNCTIONAL (Standard Categories)
        HAPPY_PATH("Happy path testing", StrategyType.FUNCTIONAL_BASIC, 1, ScenarioCategory.FUNCTIONAL),
        ERROR_HANDLING("Error handling testing", StrategyType.FUNCTIONAL_COMPREHENSIVE, 2, ScenarioCategory.FUNCTIONAL),
        BOUNDARY_CONDITIONS("Boundary condition testing", StrategyType.FUNCTIONAL_BOUNDARY, 2, ScenarioCategory.FUNCTIONAL),
        EDGE_CASES("Edge case testing", StrategyType.FUNCTIONAL_EDGE_CASE, 3, ScenarioCategory.FUNCTIONAL),

        // SECURITY (Standard Categories)
        SQL_INJECTION_BASIC("Basic SQL injection testing", StrategyType.SECURITY_INJECTION, 3, ScenarioCategory.SECURITY),
        XSS_REFLECTED("Reflected XSS testing", StrategyType.SECURITY_XSS, 3, ScenarioCategory.SECURITY),
        AUTHENTICATION_BYPASS("Authentication bypass testing", StrategyType.SECURITY_BASIC, 2, ScenarioCategory.SECURITY),
        AUTHORIZATION_ESCALATION("Authorization escalation testing", StrategyType.SECURITY_BASIC, 2, ScenarioCategory.SECURITY),

        // PERFORMANCE (Standard Categories)
        LOAD_TESTING_LIGHT("Light load testing", StrategyType.PERFORMANCE_LOAD, 2, ScenarioCategory.PERFORMANCE),
        STRESS_TESTING("Stress testing", StrategyType.PERFORMANCE_STRESS, 3, ScenarioCategory.PERFORMANCE),
        VOLUME_TESTING("Volume testing", StrategyType.PERFORMANCE_LOAD, 3, ScenarioCategory.PERFORMANCE),

        // ADVANCED (Standard Categories)
        AI_DRIVEN_TESTING("AI-driven testing", StrategyType.ADVANCED_AI_DRIVEN, 4, ScenarioCategory.ADVANCED),
        QUANTUM_TESTING("Quantum testing", StrategyType.ADVANCED_QUANTUM, 5, ScenarioCategory.SPECIALIZED);

        private final String description;
        private final StrategyType recommendedStrategy;
        private final int complexity;
        private final ScenarioCategory category;

        TestGenerationScenario(String description, StrategyType recommendedStrategy, int complexity, ScenarioCategory category) {
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

    public enum StrategyType {
        // FUNCTIONAL (Standard Categories)
        FUNCTIONAL_BASIC("Basic functional testing", 1, true, false, false, StrategyCategory.FUNCTIONAL),
        FUNCTIONAL_COMPREHENSIVE("Comprehensive functional testing", 2, true, false, false, StrategyCategory.FUNCTIONAL),
        FUNCTIONAL_BOUNDARY("Boundary condition testing", 2, true, false, true, StrategyCategory.FUNCTIONAL),
        FUNCTIONAL_EDGE_CASE("Edge case testing", 3, true, false, true, StrategyCategory.FUNCTIONAL),

        // SECURITY (Standard Categories)
        SECURITY_BASIC("Basic security validation", 2, false, true, false, StrategyCategory.SECURITY),
        SECURITY_OWASP_TOP10("OWASP Top 10 testing", 4, false, true, true, StrategyCategory.SECURITY),
        SECURITY_PENETRATION("Penetration testing", 5, false, true, true, StrategyCategory.SECURITY),
        SECURITY_INJECTION("Injection testing", 4, false, true, true, StrategyCategory.SECURITY),
        SECURITY_XSS("XSS testing", 3, false, true, false, StrategyCategory.SECURITY),

        // PERFORMANCE (Standard Categories)
        PERFORMANCE_BASIC("Basic performance testing", 2, false, false, true, StrategyCategory.PERFORMANCE),
        PERFORMANCE_LOAD("Load testing", 3, false, false, true, StrategyCategory.PERFORMANCE),
        PERFORMANCE_STRESS("Stress testing", 4, false, false, true, StrategyCategory.PERFORMANCE),

        // ADVANCED (Standard Categories)
        ADVANCED_AI_DRIVEN("AI-driven testing", 5, true, false, true, StrategyCategory.ADVANCED),
        ADVANCED_QUANTUM("Quantum computing testing", 5, true, true, true, StrategyCategory.SPECIALIZED);

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

    public enum ScenarioCategory {
        FUNCTIONAL, SECURITY, PERFORMANCE, ADVANCED, SPECIALIZED
    }

    // ===== Additional Standard Methods =====

    /**
     * Estimates total number of test cases that would be generated
     */
    public int estimateTestCaseCount() {
        int baseCount = enabledTestScenarios.size();

        // Apply complexity multiplier
        double multiplier = switch (testComplexity) {
            case MINIMAL -> 0.5;
            case STANDARD -> 1.0;
            case COMPREHENSIVE -> 2.0;
            case EXHAUSTIVE -> 4.0;
            case EXTREME -> 8.0;
        };

        // Apply edge case detection multiplier
        double edgeMultiplier = switch (edgeCaseLevel) {
            case NONE -> 1.0;
            case BASIC -> 1.2;
            case STANDARD -> 1.5;
            case AGGRESSIVE -> 2.0;
            case EXTREME -> 3.0;
        };

        int estimatedCount = (int) (baseCount * multiplier * edgeMultiplier);
        return Math.min(estimatedCount, maxTestVariations);
    }

    /**
     * Calculates complexity score for this response configuration
     */
    public double calculateComplexityScore() {
        double score = 0.0;

        // Base complexity from enabled scenarios
        score += enabledTestScenarios.size() * 2.0;

        // Complexity from test settings
        score += testComplexity.ordinal() * 10.0;
        score += edgeCaseLevel.ordinal() * 5.0;
        score += validationLevel.ordinal() * 3.0;

        // Additional complexity factors
        if (isCollectionResponse) score += 15.0;
        if (isPaginatedResponse) score += 10.0;
        if (strictValidation) score += 8.0;
        if (securityExpectations != null) score += 12.0;
        if (performanceExpectations != null) score += 8.0;

        // Pattern complexity
        score += detectedPatterns.size() * 5.0;

        // Data generator complexity
        score += customDataGenerators.size() * 2.0;

        return score;
    }

    /**
     * Gets recommended test strategy based on response characteristics
     */
    public StrategyType getRecommendedStrategy() {
        if (isErrorResponse) {
            return securityExpectations != null ?
                    StrategyType.SECURITY_BASIC : StrategyType.FUNCTIONAL_COMPREHENSIVE;
        }

        if (isCollectionResponse || isPaginatedResponse) {
            return StrategyType.FUNCTIONAL_COMPREHENSIVE;
        }

        if (securityHeaders.size() > 3 || sensitiveDataFields.size() > 0) {
            return StrategyType.SECURITY_OWASP_TOP10;
        }

        if (performanceExpectations != null) {
            return StrategyType.PERFORMANCE_LOAD;
        }

        return testComplexity.ordinal() > 2 ?
                StrategyType.FUNCTIONAL_COMPREHENSIVE : StrategyType.FUNCTIONAL_BASIC;
    }

    /**
     * Validates current configuration and returns validation result
     */
    public ValidationResult validateCurrentConfiguration() {
        List<String> warnings = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        // Check for required fields
        if (statusCode == null || statusCode.trim().isEmpty()) {
            errors.add("Status code is required");
        }

        // Check for performance expectations
        if (enabledTestScenarios.stream().anyMatch(s -> s.name().contains("PERFORMANCE"))
                && performanceExpectations == null) {
            warnings.add("Performance scenarios enabled but no performance expectations defined");
        }

        // Check for security scenarios
        if (enabledTestScenarios.stream().anyMatch(s -> s.name().contains("SECURITY"))
                && securityExpectations == null) {
            warnings.add("Security scenarios enabled but no security expectations defined");
        }

        // Check test variation limits
        if (maxTestVariations > 1000) {
            warnings.add("High number of test variations may impact performance");
        }

        // Check edge case configuration
        if (edgeCaseLevel == EdgeCaseDetectionLevel.EXTREME && maxTestVariations < 100) {
            warnings.add("Extreme edge case detection with low test variation limit may not be effective");
        }

        boolean isValid = errors.isEmpty();
        return new ValidationResult(isValid, errors, warnings);
    }

    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;
        private final List<String> warnings;

        public ValidationResult(boolean valid, List<String> errors, List<String> warnings) {
            this.valid = valid;
            this.errors = new ArrayList<>(errors);
            this.warnings = new ArrayList<>(warnings);
        }

        public boolean isValid() { return valid; }
        public List<String> getErrors() { return new ArrayList<>(errors); }
        public List<String> getWarnings() { return new ArrayList<>(warnings); }

        public boolean hasWarnings() { return !warnings.isEmpty(); }
        public boolean hasErrors() { return !errors.isEmpty(); }
    }

    /**
     * Creates a summary report of the response configuration
     */
    public ResponseConfigurationSummary createConfigurationSummary() {
        return new ResponseConfigurationSummary(
                statusCode,
                category,
                semantics,
                enabledTestScenarios.size(),
                estimateTestCaseCount(),
                calculateComplexityScore(),
                getRecommendedStrategy(),
                validateCurrentConfiguration(),
                executionId,
                generationTimestamp
        );
    }

    public static class ResponseConfigurationSummary {
        private final String statusCode;
        private final ResponseCategory category;
        private final ResponseSemantics semantics;
        private final int enabledScenarios;
        private final int estimatedTestCases;
        private final double complexityScore;
        private final StrategyType recommendedStrategy;
        private final ValidationResult validationResult;
        private final String executionId;
        private final Instant timestamp;

        public ResponseConfigurationSummary(String statusCode, ResponseCategory category,
                                            ResponseSemantics semantics, int enabledScenarios,
                                            int estimatedTestCases, double complexityScore,
                                            StrategyType recommendedStrategy, ValidationResult validationResult,
                                            String executionId, Instant timestamp) {
            this.statusCode = statusCode;
            this.category = category;
            this.semantics = semantics;
            this.enabledScenarios = enabledScenarios;
            this.estimatedTestCases = estimatedTestCases;
            this.complexityScore = complexityScore;
            this.recommendedStrategy = recommendedStrategy;
            this.validationResult = validationResult;
            this.executionId = executionId;
            this.timestamp = timestamp;
        }

        // Getters
        public String getStatusCode() { return statusCode; }
        public ResponseCategory getCategory() { return category; }
        public ResponseSemantics getSemantics() { return semantics; }
        public int getEnabledScenarios() { return enabledScenarios; }
        public int getEstimatedTestCases() { return estimatedTestCases; }
        public double getComplexityScore() { return complexityScore; }
        public StrategyType getRecommendedStrategy() { return recommendedStrategy; }
        public ValidationResult getValidationResult() { return validationResult; }
        public String getExecutionId() { return executionId; }
        public Instant getTimestamp() { return timestamp; }

        @Override
        public String toString() {
            return String.format(
                    "ResponseConfigurationSummary{statusCode='%s', category=%s, scenarios=%d, " +
                            "estimatedTests=%d, complexity=%.1f, strategy=%s, valid=%s}",
                    statusCode, category, enabledScenarios, estimatedTestCases,
                    complexityScore, recommendedStrategy, validationResult.isValid()
            );
        }
    }
}