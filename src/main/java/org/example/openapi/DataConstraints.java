package org.example.openapi;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * ===== STANDARDIZED DATA CONSTRAINTS CLASS - TUTARLILIK REHBERİ UYUMLU =====
 *
 * Tutarlılık rehberine göre standardize edilmiş data constraints class.
 * EndpointInfo, GeneratedTestCase ve ComprehensiveTestSuite ile uyumlu.
 * Standard enum'lar ve method signature'ları kullanır.
 *
 * @author Enterprise Solutions Team
 * @version 4.0.0-STANDARDIZED
 * @since 2025.1
 */
public class DataConstraints {

    // ===== ENHANCED CONSTRAINT ENUMS =====

    public enum NumericFormat {
        INTEGER("Integer format", StrategyType.FUNCTIONAL_BASIC),
        DECIMAL("Decimal format", StrategyType.FUNCTIONAL_BASIC),
        PERCENTAGE("Percentage format", StrategyType.FUNCTIONAL_COMPREHENSIVE),
        CURRENCY("Currency format", StrategyType.FUNCTIONAL_COMPREHENSIVE),
        SCIENTIFIC("Scientific notation", StrategyType.FUNCTIONAL_BOUNDARY),
        BINARY("Binary format", StrategyType.ADVANCED_AI_DRIVEN),
        HEXADECIMAL("Hexadecimal format", StrategyType.ADVANCED_AI_DRIVEN);

        private final String description;
        private final StrategyType associatedStrategy;

        NumericFormat(String description, StrategyType associatedStrategy) {
            this.description = description;
            this.associatedStrategy = associatedStrategy;
        }

        public String getDescription() { return description; }
        public StrategyType getAssociatedStrategy() { return associatedStrategy; }
    }

    public enum SecurityLevel {
        LOW("Low security", StrategyType.FUNCTIONAL_BASIC),
        NORMAL("Normal security", StrategyType.SECURITY_BASIC),
        HIGH("High security", StrategyType.SECURITY_OWASP_TOP10),
        CRITICAL("Critical security", StrategyType.SECURITY_PENETRATION);

        private final String description;
        private final StrategyType requiredStrategy;

        SecurityLevel(String description, StrategyType requiredStrategy) {
            this.description = description;
            this.requiredStrategy = requiredStrategy;
        }

        public String getDescription() { return description; }
        public StrategyType getRequiredStrategy() { return requiredStrategy; }
    }

    public enum TestComplexity {
        MINIMAL(5, StrategyType.FUNCTIONAL_BASIC, TestGenerationScenario.HAPPY_PATH),
        STANDARD(15, StrategyType.FUNCTIONAL_COMPREHENSIVE, TestGenerationScenario.BOUNDARY_VALUES),
        COMPREHENSIVE(30, StrategyType.FUNCTIONAL_BOUNDARY, TestGenerationScenario.EDGE_CASES),
        EXHAUSTIVE(50, StrategyType.ADVANCED_AI_DRIVEN, TestGenerationScenario.AI_DRIVEN_EXPLORATION);

        private final int maxVariations;
        private final StrategyType defaultStrategy;
        private final TestGenerationScenario defaultScenario;

        TestComplexity(int maxVariations, StrategyType defaultStrategy, TestGenerationScenario defaultScenario) {
            this.maxVariations = maxVariations;
            this.defaultStrategy = defaultStrategy;
            this.defaultScenario = defaultScenario;
        }

        public int getMaxVariations() { return maxVariations; }
        public StrategyType getDefaultStrategy() { return defaultStrategy; }
        public TestGenerationScenario getDefaultScenario() { return defaultScenario; }
    }

    public enum TestCaseType {
        // Functional test types
        VALID_MINIMAL(TestGenerationScenario.HAPPY_PATH, 1),
        VALID_TYPICAL(TestGenerationScenario.HAPPY_PATH, 1),
        VALID_BOUNDARY(TestGenerationScenario.BOUNDARY_VALUES, 2),
        VALID_EDGE(TestGenerationScenario.EDGE_CASES, 3),

        // Invalid test types
        INVALID_NULL(TestGenerationScenario.ERROR_HANDLING, 2),
        INVALID_EMPTY(TestGenerationScenario.ERROR_HANDLING, 2),
        INVALID_TYPE(TestGenerationScenario.ERROR_HANDLING, 2),
        INVALID_FORMAT(TestGenerationScenario.ERROR_HANDLING, 2),
        INVALID_RANGE(TestGenerationScenario.BOUNDARY_VALUES, 2),
        INVALID_LENGTH(TestGenerationScenario.BOUNDARY_VALUES, 2),
        INVALID_PATTERN(TestGenerationScenario.ERROR_HANDLING, 2),
        INVALID_ENUM(TestGenerationScenario.ERROR_HANDLING, 2),

        // Security test types
        SECURITY_INJECTION(TestGenerationScenario.SQL_INJECTION_BASIC, 4),
        SECURITY_XSS(TestGenerationScenario.XSS_REFLECTED, 4),
        SECURITY_OVERFLOW(TestGenerationScenario.FUZZING_INPUT, 4),

        // Performance test types
        PERFORMANCE_LARGE(TestGenerationScenario.LOAD_TESTING_HEAVY, 3),
        PERFORMANCE_COMPLEX(TestGenerationScenario.STRESS_TESTING, 4);

        private final TestGenerationScenario associatedScenario;
        private final int priority;

        TestCaseType(TestGenerationScenario associatedScenario, int priority) {
            this.associatedScenario = associatedScenario;
            this.priority = priority;
        }

        public TestGenerationScenario getAssociatedScenario() { return associatedScenario; }
        public int getPriority() { return priority; }
    }

    // ===== STANDARD DATA CLASSES - Tutarlılık Rehberi Uyumlu =====

    /**
     * Standard EndpointInfo class - Tutarlılık rehberi uyumlu
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

        public EndpointInfo() {}

        public EndpointInfo(String method, String path, String operationId) {
            this.method = method;
            this.path = path;
            this.operationId = operationId;
            this.parameters = new ArrayList<>();
            this.responses = new HashMap<>();
            this.securitySchemes = new ArrayList<>();
        }

        // Standard getters
        public String getMethod() { return method; }
        public String getPath() { return path; }
        public String getOperationId() { return operationId; }
        public List<ParameterInfo> getParameters() { return parameters; }
        public RequestBodyInfo getRequestBodyInfo() { return requestBodyInfo; }
        public Map<String, ResponseInfo> getResponses() { return responses; }
        public List<String> getSecuritySchemes() { return securitySchemes; }
        public boolean isRequiresAuthentication() { return requiresAuthentication; }
        public boolean isHasParameters() { return hasParameters; }
        public boolean isHasRequestBody() { return hasRequestBody; }

        // Standard setters
        public void setMethod(String method) { this.method = method; }
        public void setPath(String path) { this.path = path; }
        public void setOperationId(String operationId) { this.operationId = operationId; }
        public void setParameters(List<ParameterInfo> parameters) { this.parameters = parameters; }
        public void setRequestBodyInfo(RequestBodyInfo requestBodyInfo) { this.requestBodyInfo = requestBodyInfo; }
        public void setResponses(Map<String, ResponseInfo> responses) { this.responses = responses; }
        public void setSecuritySchemes(List<String> securitySchemes) { this.securitySchemes = securitySchemes; }
        public void setRequiresAuthentication(boolean requiresAuthentication) { this.requiresAuthentication = requiresAuthentication; }
        public void setHasParameters(boolean hasParameters) { this.hasParameters = hasParameters; }
        public void setHasRequestBody(boolean hasRequestBody) { this.hasRequestBody = hasRequestBody; }
    }



    // ===== CORE CONSTRAINT FIELDS =====

    // Temel Constraint Alanları
    private String type;
    private String format;
    private String pattern;
    private String description;
    private Object defaultValue;
    private Object example;

    // String Constraints
    private Integer minLength;
    private Integer maxLength;
    private String charset = "UTF-8";
    private Boolean caseSensitive;
    private List<String> allowedPrefixes = new ArrayList<>();
    private List<String> allowedSuffixes = new ArrayList<>();

    // Numeric Constraints
    private BigDecimal minimum;
    private BigDecimal maximum;
    private Boolean exclusiveMinimum;
    private Boolean exclusiveMaximum;
    private BigDecimal multipleOf;
    private Integer decimalPlaces;
    private NumericFormat numberFormat = NumericFormat.DECIMAL;

    // Array Constraints
    private Integer minItems;
    private Integer maxItems;
    private Boolean uniqueItems;
    private DataConstraints itemConstraints;
    private String sortOrder;

    // Object Constraints
    private Integer minProperties;
    private Integer maxProperties;
    private List<String> requiredFields = new ArrayList<>();
    private Map<String, DataConstraints> propertyConstraints = new HashMap<>();
    private List<String> additionalProperties = new ArrayList<>();

    // Enum ve Choice Constraints
    private List<Object> enumValues = new ArrayList<>();
    private Map<String, String> enumDescriptions = new HashMap<>();
    private Boolean allowCustomValues = false;

    // Date/Time Constraints
    private LocalDateTime minDate;
    private LocalDateTime maxDate;
    private List<String> dateFormats = new ArrayList<>();
    private String timezone;
    private Boolean allowPastDates = true;
    private Boolean allowFutureDates = true;

    // File/Binary Constraints
    private Long minFileSize;
    private Long maxFileSize;
    private List<String> allowedMimeTypes = new ArrayList<>();
    private List<String> allowedExtensions = new ArrayList<>();

    // Security Constraints
    private Boolean allowNull = true;
    private Boolean allowEmpty = true;
    private Boolean sanitizeInput = false;
    private List<String> blacklistedPatterns = new ArrayList<>();
    private SecurityLevel securityLevel = SecurityLevel.NORMAL;

    // ===== STANDARD TEST CONFIGURATION =====

    // Test Generation Settings - Strategy-based
    private TestComplexity testComplexity = TestComplexity.STANDARD;
    private Set<StrategyType> enabledStrategies = new HashSet<>();
    private Set<TestGenerationScenario> enabledScenarios = new HashSet<>();
    private StrategyType defaultStrategy = StrategyType.FUNCTIONAL_COMPREHENSIVE;

    // Test Generation Flags
    private Boolean generateBoundaryValues = true;
    private Boolean generateInvalidValues = true;
    private Boolean generateEdgeCases = true;
    private Integer maxTestVariations = 20;
    private Set<TestCaseType> enabledTestTypes = EnumSet.allOf(TestCaseType.class);

    // Validation Cache
    private transient Pattern compiledPattern;
    private transient Map<String, Boolean> validationCache = new HashMap<>();

    // ===== CONSTRUCTORS =====

    public DataConstraints() {
        initializeDefaults();
        initializeStandardStrategies();
    }

    public DataConstraints(String type) {
        this();
        this.type = type;
        applyTypeDefaults(type);
    }

    /**
     * Standard validation method
     */
    private void initializeDefaults() {
        this.dateFormats.add("yyyy-MM-dd");
        this.dateFormats.add("yyyy-MM-dd'T'HH:mm:ss");
        this.dateFormats.add("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        this.charset = "UTF-8";
    }

    /**
     * Standard method: Initialize standard strategies based on test complexity
     */
    private void initializeStandardStrategies() {
        enabledStrategies.clear();
        enabledScenarios.clear();

        // Add strategies based on test complexity
        switch (testComplexity) {
            case MINIMAL:
                enabledStrategies.add(StrategyType.FUNCTIONAL_BASIC);
                enabledScenarios.add(TestGenerationScenario.HAPPY_PATH);
                break;
            case STANDARD:
                enabledStrategies.add(StrategyType.FUNCTIONAL_BASIC);
                enabledStrategies.add(StrategyType.FUNCTIONAL_COMPREHENSIVE);
                enabledScenarios.add(TestGenerationScenario.HAPPY_PATH);
                enabledScenarios.add(TestGenerationScenario.BOUNDARY_VALUES);
                break;
            case COMPREHENSIVE:
                enabledStrategies.add(StrategyType.FUNCTIONAL_BASIC);
                enabledStrategies.add(StrategyType.FUNCTIONAL_COMPREHENSIVE);
                enabledStrategies.add(StrategyType.FUNCTIONAL_BOUNDARY);
                enabledScenarios.add(TestGenerationScenario.HAPPY_PATH);
                enabledScenarios.add(TestGenerationScenario.BOUNDARY_VALUES);
                enabledScenarios.add(TestGenerationScenario.ERROR_HANDLING);
                break;
            case EXHAUSTIVE:
                enabledStrategies.addAll(Arrays.asList(StrategyType.values()));
                enabledScenarios.addAll(Arrays.asList(TestGenerationScenario.values()));
                break;
        }

        this.maxTestVariations = testComplexity.getMaxVariations();
        this.defaultStrategy = testComplexity.getDefaultStrategy();
    }

    private void applyTypeDefaults(String type) {
        switch (type.toLowerCase()) {
            case "string":
                if (maxLength == null) maxLength = 255;
                break;
            case "integer":
                if (minimum == null) minimum = BigDecimal.valueOf(Integer.MIN_VALUE);
                if (maximum == null) maximum = BigDecimal.valueOf(Integer.MAX_VALUE);
                break;
            case "number":
                if (decimalPlaces == null) decimalPlaces = 2;
                break;
            case "array":
                if (maxItems == null) maxItems = 100;
                break;
            case "object":
                if (maxProperties == null) maxProperties = 50;
                break;
        }
    }

    // ===== BUILDER PATTERN - STANDARDIZED =====

    /**
     * Standard factory method
     */
    public static DataConstraints create(String type) {
        return new DataConstraints(type);
    }

    /**
     * Standard builder factory method
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Standard Builder class - Tutarlılık rehberi uyumlu
     */
    public static class Builder {
        private DataConstraints constraints = new DataConstraints();

        public Builder withType(String type) {
            constraints.type = type;
            constraints.applyTypeDefaults(type);
            return this;
        }

        public Builder withFormat(String format) {
            constraints.format = format;
            constraints.applyFormatDefaults(format);
            return this;
        }

        public Builder withPattern(String pattern) {
            constraints.pattern = pattern;
            constraints.compiledPattern = null; // Reset cache
            return this;
        }

        public Builder withRange(BigDecimal min, BigDecimal max) {
            constraints.minimum = min;
            constraints.maximum = max;
            return this;
        }

        public Builder withLength(Integer min, Integer max) {
            constraints.minLength = min;
            constraints.maxLength = max;
            return this;
        }

        public Builder withItems(Integer min, Integer max) {
            constraints.minItems = min;
            constraints.maxItems = max;
            return this;
        }

        public Builder withRequired(String... fields) {
            constraints.requiredFields.addAll(Arrays.asList(fields));
            return this;
        }

        public Builder withEnumValues(Object... values) {
            constraints.enumValues.addAll(Arrays.asList(values));
            return this;
        }

        public Builder withSecurity(SecurityLevel level) {
            constraints.securityLevel = level;
            constraints.applySecurity(level);
            constraints.enabledStrategies.add(level.getRequiredStrategy());
            return this;
        }

        public Builder withTestComplexity(TestComplexity complexity) {
            constraints.testComplexity = complexity;
            constraints.initializeStandardStrategies();
            return this;
        }

        public Builder withStrategy(StrategyType strategy) {
            constraints.enabledStrategies.add(strategy);
            return this;
        }

        public Builder withScenario(TestGenerationScenario scenario) {
            constraints.enabledScenarios.add(scenario);
            constraints.enabledStrategies.add(scenario.getRecommendedStrategy());
            return this;
        }

        public Builder withAllowNull(boolean allowNull) {
            constraints.allowNull = allowNull;
            return this;
        }

        public Builder withAllowEmpty(boolean allowEmpty) {
            constraints.allowEmpty = allowEmpty;
            return this;
        }

        public Builder withMaxTestVariations(int maxVariations) {
            constraints.maxTestVariations = maxVariations;
            return this;
        }

        public DataConstraints build() {
            validateAndEnhanceConfiguration(constraints);
            return constraints;
        }
    }

    // ===== STANDARD UTILITY METHODS =====

    /**
     * Standard validation method
     */
    private static void validateAndEnhanceConfiguration(DataConstraints constraints) {
        if (constraints.type == null || constraints.type.trim().isEmpty()) {
            throw new IllegalArgumentException("Type cannot be null or empty");
        }

        if (constraints.maxTestVariations <= 0) {
            throw new IllegalArgumentException("Max test variations must be positive");
        }

        // Ensure we have at least basic strategies enabled
        if (constraints.enabledStrategies.isEmpty()) {
            constraints.enabledStrategies.add(StrategyType.FUNCTIONAL_BASIC);
        }

        if (constraints.enabledScenarios.isEmpty()) {
            constraints.enabledScenarios.add(TestGenerationScenario.HAPPY_PATH);
        }
    }

    /**
     * Standard method: Generate advanced cache key for constraints
     */
    private String generateAdvancedCacheKey() {
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(type != null ? type : "unknown");
        keyBuilder.append("_");
        keyBuilder.append(format != null ? format : "default");
        keyBuilder.append("_");
        keyBuilder.append(testComplexity.name());
        keyBuilder.append("_");
        keyBuilder.append(System.currentTimeMillis());

        return keyBuilder.toString();
    }

    /**
     * Standard method: Generate advanced execution ID
     */
    private static String generateAdvancedExecutionId() {
        return "constraint_" + System.currentTimeMillis() + "_" +
                Thread.currentThread().getId() + "_" +
                (int)(Math.random() * 10000);
    }

    // ===== FLUENT API METHODS =====

    public DataConstraints withFormat(String format) {
        this.format = format;
        applyFormatDefaults(format);
        return this;
    }

    private void applyFormatDefaults(String format) {
        switch (format.toLowerCase()) {
            case "email":
                this.pattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
                this.maxLength = 320; // RFC 5321 limit
                break;
            case "uri":
                this.maxLength = 2048;
                break;
            case "uuid":
                this.pattern = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
                this.minLength = 36;
                this.maxLength = 36;
                break;
            case "date":
                this.pattern = "^\\d{4}-\\d{2}-\\d{2}$";
                break;
            case "date-time":
                this.pattern = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}";
                break;
            case "ipv4":
                this.pattern = "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
                break;
            case "ipv6":
                this.pattern = "^(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$";
                break;
        }
    }

    public DataConstraints range(BigDecimal min, BigDecimal max) {
        this.minimum = min;
        this.maximum = max;
        return this;
    }

    public DataConstraints length(Integer min, Integer max) {
        this.minLength = min;
        this.maxLength = max;
        return this;
    }

    public DataConstraints items(Integer min, Integer max) {
        this.minItems = min;
        this.maxItems = max;
        return this;
    }

    public DataConstraints required(String... fields) {
        this.requiredFields.addAll(Arrays.asList(fields));
        return this;
    }

    public DataConstraints enumValues(Object... values) {
        this.enumValues.addAll(Arrays.asList(values));
        return this;
    }

    public DataConstraints pattern(String regex) {
        this.pattern = regex;
        this.compiledPattern = null; // Reset cache
        return this;
    }

    public DataConstraints security(SecurityLevel level) {
        this.securityLevel = level;
        applySecurity(level);
        this.enabledStrategies.add(level.getRequiredStrategy());
        return this;
    }

    private void applySecurity(SecurityLevel level) {
        switch (level) {
            case HIGH:
            case CRITICAL:
                this.sanitizeInput = true;
                this.blacklistedPatterns.addAll(Arrays.asList(
                        "(?i).*<script.*>.*</script>.*",  // XSS
                        "(?i).*(union|select|insert|delete|update|drop).*", // SQL Injection
                        ".*['\";].*", // SQL/Command injection chars
                        ".*\\.\\..*" // Path traversal
                ));
                break;
        }
    }

    public DataConstraints testComplexity(TestComplexity complexity) {
        this.testComplexity = complexity;
        initializeStandardStrategies();
        return this;
    }

    public DataConstraints addStrategy(StrategyType strategy) {
        this.enabledStrategies.add(strategy);
        return this;
    }

    public DataConstraints addScenario(TestGenerationScenario scenario) {
        this.enabledScenarios.add(scenario);
        this.enabledStrategies.add(scenario.getRecommendedStrategy());
        return this;
    }

    // ===== STANDARD TEST DATA GENERATION METHODS =====

    /**
     * Standard method: Generate comprehensive test suite for constraints
     *
     * @return ComprehensiveTestSuite with all test cases
     */
    public ComprehensiveTestSuite generateComprehensiveTestSuite() {
        String executionId = generateAdvancedExecutionId();

        List<GeneratedTestCase> testCases = new ArrayList<>();

        // Generate test cases for each enabled scenario
        for (TestGenerationScenario scenario : enabledScenarios) {
            testCases.addAll(generateTestCasesForScenario(scenario, executionId));
        }

        // Apply test variation limits
        if (testCases.size() > maxTestVariations) {
            testCases = prioritizeAndLimit(testCases);
        }

        return ComprehensiveTestSuite.builder()
                .withTestCases(testCases)
                .withExecutionId(executionId)
                .withGenerationTimestamp(Instant.now())
                .build();
    }

    /**
     * Standard method: Generate test cases for specific scenario
     *
     * @param scenario Test generation scenario
     * @param executionId Execution identifier
     * @return List of generated test cases
     */
    public List<GeneratedTestCase> generateTestCasesForScenario(TestGenerationScenario scenario, String executionId) {
        List<GeneratedTestCase> testCases = new ArrayList<>();

        switch (scenario) {
            case HAPPY_PATH:
                testCases.addAll(generateHappyPathTestCases(executionId));
                break;
            case BOUNDARY_VALUES:
                testCases.addAll(generateBoundaryValueTestCases(executionId));
                break;
            case EDGE_CASES:
                testCases.addAll(generateEdgeCaseTestCases(executionId));
                break;
            case ERROR_HANDLING:
                testCases.addAll(generateErrorHandlingTestCases(executionId));
                break;
            case SQL_INJECTION_BASIC:
                testCases.addAll(generateSqlInjectionTestCases(executionId));
                break;
            case XSS_REFLECTED:
            case XSS_STORED:
                testCases.addAll(generateXssTestCases(executionId));
                break;
            case FUZZING_INPUT:
                testCases.addAll(generateFuzzingTestCases(executionId));
                break;
            default:
                testCases.addAll(generateGenericTestCases(scenario, executionId));
                break;
        }

        return testCases;
    }

    /**
     * Tüm test case'ler için değer üretir - Legacy support
     */
    public List<TestCase> generateAllTestCases() {
        List<TestCase> testCases = new ArrayList<>();

        for (TestCaseType caseType : enabledTestTypes) {
            testCases.addAll(generateTestCasesForType(caseType));
        }

        // Maksimum varyasyon sınırı uygula
        if (testCases.size() > maxTestVariations) {
            testCases = prioritizeAndLimitLegacy(testCases);
        }

        return testCases;
    }

    private List<GeneratedTestCase> generateHappyPathTestCases(String executionId) {
        List<GeneratedTestCase> testCases = new ArrayList<>();

        switch (type.toLowerCase()) {
            case "string":
                testCases.add(GeneratedTestCase.builder()
                        .withTestId(executionId + "_happy_string")
                        .withTestName("Valid String Test")
                        .withDescription("Test with valid string value")
                        .withScenario(TestGenerationScenario.HAPPY_PATH)
                        .withStrategyType(StrategyType.FUNCTIONAL_BASIC)
                        .withComplexity(1)
                        .withPriority(1)
                        .withTags(Set.of("happy-path", "string", "valid"))
                        .build());
                break;

            case "integer":
                testCases.add(GeneratedTestCase.builder()
                        .withTestId(executionId + "_happy_integer")
                        .withTestName("Valid Integer Test")
                        .withDescription("Test with valid integer value")
                        .withScenario(TestGenerationScenario.HAPPY_PATH)
                        .withStrategyType(StrategyType.FUNCTIONAL_BASIC)
                        .withComplexity(1)
                        .withPriority(1)
                        .withTags(Set.of("happy-path", "integer", "valid"))
                        .build());
                break;

            case "array":
                testCases.add(GeneratedTestCase.builder()
                        .withTestId(executionId + "_happy_array")
                        .withTestName("Valid Array Test")
                        .withDescription("Test with valid array value")
                        .withScenario(TestGenerationScenario.HAPPY_PATH)
                        .withStrategyType(StrategyType.FUNCTIONAL_BASIC)
                        .withComplexity(1)
                        .withPriority(1)
                        .withTags(Set.of("happy-path", "array", "valid"))
                        .build());
                break;
        }

        return testCases;
    }

    private List<GeneratedTestCase> generateBoundaryValueTestCases(String executionId) {
        List<GeneratedTestCase> testCases = new ArrayList<>();

        switch (type.toLowerCase()) {
            case "string":
                if (minLength != null && minLength > 0) {
                    testCases.add(GeneratedTestCase.builder()
                            .withTestId(executionId + "_boundary_min_length")
                            .withTestName("Minimum Length String Test")
                            .withDescription("Test with minimum allowed string length")
                            .withScenario(TestGenerationScenario.BOUNDARY_VALUES)
                            .withStrategyType(StrategyType.FUNCTIONAL_BOUNDARY)
                            .withComplexity(2)
                            .withPriority(2)
                            .withTags(Set.of("boundary", "string", "min-length"))
                            .build());
                }

                if (maxLength != null) {
                    testCases.add(GeneratedTestCase.builder()
                            .withTestId(executionId + "_boundary_max_length")
                            .withTestName("Maximum Length String Test")
                            .withDescription("Test with maximum allowed string length")
                            .withScenario(TestGenerationScenario.BOUNDARY_VALUES)
                            .withStrategyType(StrategyType.FUNCTIONAL_BOUNDARY)
                            .withComplexity(2)
                            .withPriority(2)
                            .withTags(Set.of("boundary", "string", "max-length"))
                            .build());
                }
                break;

            case "integer":
            case "number":
                if (minimum != null) {
                    testCases.add(GeneratedTestCase.builder()
                            .withTestId(executionId + "_boundary_min_value")
                            .withTestName("Minimum Value Test")
                            .withDescription("Test with minimum allowed numeric value")
                            .withScenario(TestGenerationScenario.BOUNDARY_VALUES)
                            .withStrategyType(StrategyType.FUNCTIONAL_BOUNDARY)
                            .withComplexity(2)
                            .withPriority(2)
                            .withTags(Set.of("boundary", "numeric", "min-value"))
                            .build());
                }

                if (maximum != null) {
                    testCases.add(GeneratedTestCase.builder()
                            .withTestId(executionId + "_boundary_max_value")
                            .withTestName("Maximum Value Test")
                            .withDescription("Test with maximum allowed numeric value")
                            .withScenario(TestGenerationScenario.BOUNDARY_VALUES)
                            .withStrategyType(StrategyType.FUNCTIONAL_BOUNDARY)
                            .withComplexity(2)
                            .withPriority(2)
                            .withTags(Set.of("boundary", "numeric", "max-value"))
                            .build());
                }
                break;
        }

        return testCases;
    }

    private List<GeneratedTestCase> generateEdgeCaseTestCases(String executionId) {
        List<GeneratedTestCase> testCases = new ArrayList<>();

        // Generate edge cases based on type
        switch (type.toLowerCase()) {
            case "string":
                testCases.add(GeneratedTestCase.builder()
                        .withTestId(executionId + "_edge_special_chars")
                        .withTestName("Special Characters Test")
                        .withDescription("Test with special characters and unicode")
                        .withScenario(TestGenerationScenario.EDGE_CASES)
                        .withStrategyType(StrategyType.FUNCTIONAL_EDGE_CASE)
                        .withComplexity(3)
                        .withPriority(3)
                        .withTags(Set.of("edge-case", "string", "special-chars"))
                        .build());
                break;

            case "integer":
                testCases.add(GeneratedTestCase.builder()
                        .withTestId(executionId + "_edge_zero")
                        .withTestName("Zero Value Test")
                        .withDescription("Test with zero value")
                        .withScenario(TestGenerationScenario.EDGE_CASES)
                        .withStrategyType(StrategyType.FUNCTIONAL_EDGE_CASE)
                        .withComplexity(3)
                        .withPriority(3)
                        .withTags(Set.of("edge-case", "integer", "zero"))
                        .build());
                break;
        }

        return testCases;
    }

    private List<GeneratedTestCase> generateErrorHandlingTestCases(String executionId) {
        List<GeneratedTestCase> testCases = new ArrayList<>();

        if (!allowNull) {
            testCases.add(GeneratedTestCase.builder()
                    .withTestId(executionId + "_error_null")
                    .withTestName("Null Value Error Test")
                    .withDescription("Test error handling for null values")
                    .withScenario(TestGenerationScenario.ERROR_HANDLING)
                    .withStrategyType(StrategyType.FUNCTIONAL_COMPREHENSIVE)
                    .withComplexity(2)
                    .withPriority(2)
                    .withTags(Set.of("error-handling", "null", "invalid"))
                    .build());
        }

        if (!allowEmpty) {
            testCases.add(GeneratedTestCase.builder()
                    .withTestId(executionId + "_error_empty")
                    .withTestName("Empty Value Error Test")
                    .withDescription("Test error handling for empty values")
                    .withScenario(TestGenerationScenario.ERROR_HANDLING)
                    .withStrategyType(StrategyType.FUNCTIONAL_COMPREHENSIVE)
                    .withComplexity(2)
                    .withPriority(2)
                    .withTags(Set.of("error-handling", "empty", "invalid"))
                    .build());
        }

        return testCases;
    }

    private List<GeneratedTestCase> generateSqlInjectionTestCases(String executionId) {
        List<GeneratedTestCase> testCases = new ArrayList<>();

        if ("string".equals(type)) {
            testCases.add(GeneratedTestCase.builder()
                    .withTestId(executionId + "_sql_injection")
                    .withTestName("SQL Injection Security Test")
                    .withDescription("Test protection against SQL injection attacks")
                    .withScenario(TestGenerationScenario.SQL_INJECTION_BASIC)
                    .withStrategyType(StrategyType.SECURITY_INJECTION)
                    .withComplexity(4)
                    .withPriority(1)
                    .withTags(Set.of("security", "sql-injection", "attack"))
                    .build());
        }

        return testCases;
    }

    private List<GeneratedTestCase> generateXssTestCases(String executionId) {
        List<GeneratedTestCase> testCases = new ArrayList<>();

        if ("string".equals(type)) {
            testCases.add(GeneratedTestCase.builder()
                    .withTestId(executionId + "_xss_attack")
                    .withTestName("XSS Security Test")
                    .withDescription("Test protection against XSS attacks")
                    .withScenario(TestGenerationScenario.XSS_REFLECTED)
                    .withStrategyType(StrategyType.SECURITY_XSS)
                    .withComplexity(4)
                    .withPriority(1)
                    .withTags(Set.of("security", "xss", "attack"))
                    .build());
        }

        return testCases;
    }

    private List<GeneratedTestCase> generateFuzzingTestCases(String executionId) {
        List<GeneratedTestCase> testCases = new ArrayList<>();

        testCases.add(GeneratedTestCase.builder()
                .withTestId(executionId + "_fuzzing")
                .withTestName("Fuzzing Test")
                .withDescription("Test with randomized input data")
                .withScenario(TestGenerationScenario.FUZZING_INPUT)
                .withStrategyType(StrategyType.ADVANCED_FUZZING)
                .withComplexity(4)
                .withPriority(3)
                .withTags(Set.of("fuzzing", "random", "advanced"))
                .build());

        return testCases;
    }

    private List<GeneratedTestCase> generateGenericTestCases(TestGenerationScenario scenario, String executionId) {
        List<GeneratedTestCase> testCases = new ArrayList<>();

        testCases.add(GeneratedTestCase.builder()
                .withTestId(executionId + "_generic_" + scenario.name().toLowerCase())
                .withTestName("Generic " + scenario.getDescription())
                .withDescription("Generic test case for " + scenario.getDescription())
                .withScenario(scenario)
                .withStrategyType(scenario.getRecommendedStrategy())
                .withComplexity(scenario.getComplexity())
                .withPriority(scenario.getComplexity())
                .withTags(Set.of("generic", scenario.getCategory().toLowerCase()))
                .build());

        return testCases;
    }

    // ===== LEGACY TEST CASE GENERATION =====

    private List<TestCase> generateTestCasesForType(TestCaseType caseType) {
        List<TestCase> cases = new ArrayList<>();

        switch (caseType) {
            case VALID_MINIMAL:
                cases.addAll(generateValidMinimalCases());
                break;
            case VALID_TYPICAL:
                cases.addAll(generateValidTypicalCases());
                break;
            case VALID_BOUNDARY:
                cases.addAll(generateValidBoundaryCases());
                break;
            case VALID_EDGE:
                cases.addAll(generateValidEdgeCases());
                break;
            case INVALID_NULL:
                if (!allowNull) cases.add(new TestCase(null, caseType, false, "Null value not allowed"));
                break;
            case INVALID_EMPTY:
                if (!allowEmpty) cases.addAll(generateInvalidEmptyCases());
                break;
            case INVALID_TYPE:
                cases.addAll(generateInvalidTypeCases());
                break;
            case INVALID_FORMAT:
                cases.addAll(generateInvalidFormatCases());
                break;
            case INVALID_RANGE:
                cases.addAll(generateInvalidRangeCases());
                break;
            case INVALID_LENGTH:
                cases.addAll(generateInvalidLengthCases());
                break;
            case INVALID_PATTERN:
                cases.addAll(generateInvalidPatternCases());
                break;
            case INVALID_ENUM:
                cases.addAll(generateInvalidEnumCases());
                break;
            case SECURITY_INJECTION:
                cases.addAll(generateSecurityInjectionCases());
                break;
            case SECURITY_XSS:
                cases.addAll(generateXssCases());
                break;
            case PERFORMANCE_LARGE:
                cases.addAll(generatePerformanceCases());
                break;
        }

        return cases;
    }

    private List<TestCase> generateValidMinimalCases() {
        List<TestCase> cases = new ArrayList<>();

        switch (type.toLowerCase()) {
            case "string":
                if (minLength != null && minLength > 0) {
                    cases.add(new TestCase(generateString(minLength), TestCaseType.VALID_MINIMAL, true, "Minimum length string"));
                } else {
                    cases.add(new TestCase("a", TestCaseType.VALID_MINIMAL, true, "Single character"));
                }
                break;
            case "integer":
                if (minimum != null) {
                    cases.add(new TestCase(minimum.intValue(), TestCaseType.VALID_MINIMAL, true, "Minimum value"));
                } else {
                    cases.add(new TestCase(0, TestCaseType.VALID_MINIMAL, true, "Zero value"));
                }
                break;
            case "number":
                if (minimum != null) {
                    cases.add(new TestCase(minimum, TestCaseType.VALID_MINIMAL, true, "Minimum value"));
                } else {
                    cases.add(new TestCase(BigDecimal.ZERO, TestCaseType.VALID_MINIMAL, true, "Zero value"));
                }
                break;
            case "array":
                if (minItems != null && minItems > 0) {
                    List<Object> array = new ArrayList<>();
                    for (int i = 0; i < minItems; i++) {
                        array.add(generateValidItemValue());
                    }
                    cases.add(new TestCase(array, TestCaseType.VALID_MINIMAL, true, "Minimum items array"));
                } else {
                    cases.add(new TestCase(Collections.emptyList(), TestCaseType.VALID_MINIMAL, true, "Empty array"));
                }
                break;
            case "object":
                Map<String, Object> obj = new HashMap<>();
                for (String field : requiredFields) {
                    obj.put(field, generateValidFieldValue(field));
                }
                cases.add(new TestCase(obj, TestCaseType.VALID_MINIMAL, true, "Required fields only"));
                break;
            case "boolean":
                cases.add(new TestCase(true, TestCaseType.VALID_MINIMAL, true, "Boolean true"));
                cases.add(new TestCase(false, TestCaseType.VALID_MINIMAL, true, "Boolean false"));
                break;
        }

        return cases;
    }

    private List<TestCase> generateValidBoundaryCases() {
        List<TestCase> cases = new ArrayList<>();

        switch (type.toLowerCase()) {
            case "string":
                if (minLength != null && minLength > 0) {
                    cases.add(new TestCase(generateString(minLength), TestCaseType.VALID_BOUNDARY, true, "Minimum length"));
                }
                if (maxLength != null) {
                    cases.add(new TestCase(generateString(maxLength), TestCaseType.VALID_BOUNDARY, true, "Maximum length"));
                }
                break;
            case "integer":
            case "number":
                if (minimum != null) {
                    if (exclusiveMinimum != null && exclusiveMinimum) {
                        cases.add(new TestCase(minimum.add(BigDecimal.ONE), TestCaseType.VALID_BOUNDARY, true, "Just above minimum"));
                    } else {
                        cases.add(new TestCase(minimum, TestCaseType.VALID_BOUNDARY, true, "Minimum value"));
                    }
                }
                if (maximum != null) {
                    if (exclusiveMaximum != null && exclusiveMaximum) {
                        cases.add(new TestCase(maximum.subtract(BigDecimal.ONE), TestCaseType.VALID_BOUNDARY, true, "Just below maximum"));
                    } else {
                        cases.add(new TestCase(maximum, TestCaseType.VALID_BOUNDARY, true, "Maximum value"));
                    }
                }
                break;
            case "array":
                if (minItems != null && minItems > 0) {
                    cases.add(new TestCase(generateArray(minItems), TestCaseType.VALID_BOUNDARY, true, "Minimum items"));
                }
                if (maxItems != null) {
                    cases.add(new TestCase(generateArray(maxItems), TestCaseType.VALID_BOUNDARY, true, "Maximum items"));
                }
                break;
        }

        return cases;
    }

    private List<TestCase> generateInvalidRangeCases() {
        List<TestCase> cases = new ArrayList<>();

        if ("integer".equals(type) || "number".equals(type)) {
            if (minimum != null) {
                BigDecimal belowMin = minimum.subtract(BigDecimal.ONE);
                cases.add(new TestCase(belowMin, TestCaseType.INVALID_RANGE, false, "Below minimum"));
            }
            if (maximum != null) {
                BigDecimal aboveMax = maximum.add(BigDecimal.ONE);
                cases.add(new TestCase(aboveMax, TestCaseType.INVALID_RANGE, false, "Above maximum"));
            }
        }

        return cases;
    }

    private List<TestCase> generateInvalidLengthCases() {
        List<TestCase> cases = new ArrayList<>();

        if ("string".equals(type)) {
            if (minLength != null && minLength > 0) {
                cases.add(new TestCase(generateString(minLength - 1), TestCaseType.INVALID_LENGTH, false, "Below minimum length"));
            }
            if (maxLength != null) {
                cases.add(new TestCase(generateString(maxLength + 1), TestCaseType.INVALID_LENGTH, false, "Above maximum length"));
            }
        }

        return cases;
    }

    private List<TestCase> generateSecurityInjectionCases() {
        List<TestCase> cases = new ArrayList<>();

        if ("string".equals(type)) {
            String[] injectionPayloads = {
                    "'; DROP TABLE users; --",
                    "' OR '1'='1",
                    "admin'--",
                    "'; INSERT INTO users VALUES ('hacker', 'password'); --",
                    "1' UNION SELECT * FROM sensitive_table--"
            };

            for (String payload : injectionPayloads) {
                cases.add(new TestCase(payload, TestCaseType.SECURITY_INJECTION, false, "SQL Injection attempt"));
            }
        }

        return cases;
    }

    private List<TestCase> generateXssCases() {
        List<TestCase> cases = new ArrayList<>();

        if ("string".equals(type)) {
            String[] xssPayloads = {
                    "<script>alert('XSS')</script>",
                    "<img src=x onerror=alert('XSS')>",
                    "javascript:alert('XSS')",
                    "<svg onload=alert('XSS')>",
                    "'+alert('XSS')+'",
                    "\"><script>alert('XSS')</script>"
            };

            for (String payload : xssPayloads) {
                cases.add(new TestCase(payload, TestCaseType.SECURITY_XSS, false, "XSS attack attempt"));
            }
        }

        return cases;
    }

    // ===== PRIORITIZATION AND LIMITING =====

    private List<GeneratedTestCase> prioritizeAndLimit(List<GeneratedTestCase> cases) {
        return cases.stream()
                .sorted((a, b) -> {
                    // Sort by priority (lower is higher priority), then by complexity
                    int priorityComparison = Integer.compare(a.getPriority(), b.getPriority());
                    if (priorityComparison != 0) return priorityComparison;
                    return Integer.compare(b.getComplexity(), a.getComplexity());
                })
                .limit(maxTestVariations)
                .collect(Collectors.toList());
    }

    private List<TestCase> prioritizeAndLimitLegacy(List<TestCase> cases) {
        // Öncelik sırası: Security > Boundary > Valid > Invalid
        Map<TestCaseType, Integer> priorities = new HashMap<>();
        priorities.put(TestCaseType.SECURITY_INJECTION, 1);
        priorities.put(TestCaseType.SECURITY_XSS, 1);
        priorities.put(TestCaseType.VALID_BOUNDARY, 2);
        priorities.put(TestCaseType.INVALID_RANGE, 3);
        priorities.put(TestCaseType.INVALID_LENGTH, 3);
        priorities.put(TestCaseType.VALID_MINIMAL, 4);

        return cases.stream()
                .sorted((a, b) -> {
                    int priorityA = priorities.getOrDefault(a.getType(), 5);
                    int priorityB = priorities.getOrDefault(b.getType(), 5);
                    return Integer.compare(priorityA, priorityB);
                })
                .limit(maxTestVariations)
                .collect(Collectors.toList());
    }

    // ===== VALIDATION METHODS =====

    public boolean isValid(Object value) {
        if (value == null) return allowNull;

        String key = value.toString();
        if (validationCache.containsKey(key)) {
            return validationCache.get(key);
        }

        boolean result = performValidation(value);
        validationCache.put(key, result);
        return result;
    }

    private boolean performValidation(Object value) {
        // Type validation
        if (!validateType(value)) return false;

        // Pattern validation
        if (pattern != null && !validatePattern(value.toString())) return false;

        // Range validation
        if (!validateRange(value)) return false;

        // Length validation
        if (!validateLength(value)) return false;

        // Enum validation
        if (!enumValues.isEmpty() && !enumValues.contains(value)) return false;

        // Security validation
        if (!validateSecurity(value)) return false;

        return true;
    }

    private boolean validateType(Object value) {
        if (type == null) return true;

        switch (type.toLowerCase()) {
            case "string": return value instanceof String;
            case "integer": return value instanceof Integer || value instanceof Long;
            case "number": return value instanceof Number;
            case "boolean": return value instanceof Boolean;
            case "array": return value instanceof List || value.getClass().isArray();
            case "object": return value instanceof Map;
            default: return true;
        }
    }

    private boolean validatePattern(String value) {
        if (pattern == null) return true;

        if (compiledPattern == null) {
            compiledPattern = Pattern.compile(pattern);
        }

        return compiledPattern.matcher(value).matches();
    }

    private boolean validateRange(Object value) {
        if (!(value instanceof Number)) return true;

        BigDecimal numValue = new BigDecimal(value.toString());

        if (minimum != null) {
            int comparison = numValue.compareTo(minimum);
            if (exclusiveMinimum != null && exclusiveMinimum) {
                if (comparison <= 0) return false;
            } else {
                if (comparison < 0) return false;
            }
        }

        if (maximum != null) {
            int comparison = numValue.compareTo(maximum);
            if (exclusiveMaximum != null && exclusiveMaximum) {
                if (comparison >= 0) return false;
            } else {
                if (comparison > 0) return false;
            }
        }

        return true;
    }

    private boolean validateLength(Object value) {
        int length = getLength(value);

        if (minLength != null && length < minLength) return false;
        if (maxLength != null && length > maxLength) return false;

        return true;
    }

    private int getLength(Object value) {
        if (value instanceof String) return ((String) value).length();
        if (value instanceof List) return ((List<?>) value).size();
        if (value instanceof Map) return ((Map<?, ?>) value).size();
        if (value.getClass().isArray()) return java.lang.reflect.Array.getLength(value);
        return value.toString().length();
    }

    private boolean validateSecurity(Object value) {
        if (securityLevel == SecurityLevel.LOW) return true;

        String strValue = value.toString();

        for (String blacklistedPattern : blacklistedPatterns) {
            if (Pattern.compile(blacklistedPattern).matcher(strValue).matches()) {
                return false;
            }
        }

        return true;
    }

    // ===== HELPER METHODS =====

    private String generateString(int length) {
        if (length <= 0) return "";

        StringBuilder sb = new StringBuilder();
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        return sb.toString();
    }

    private List<Object> generateArray(int size) {
        List<Object> array = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            array.add(generateValidItemValue());
        }
        return array;
    }

    private Object generateValidItemValue() {
        if (itemConstraints != null) {
            List<TestCase> validCases = itemConstraints.generateValidMinimalCases();
            return validCases.isEmpty() ? "item" : validCases.get(0).getValue();
        }
        return "item" + System.currentTimeMillis();
    }

    private Object generateValidFieldValue(String fieldName) {
        DataConstraints fieldConstraint = propertyConstraints.get(fieldName);
        if (fieldConstraint != null) {
            List<TestCase> validCases = fieldConstraint.generateValidMinimalCases();
            return validCases.isEmpty() ? "value" : validCases.get(0).getValue();
        }
        return "defaultValue";
    }

    // ===== PLACEHOLDER METHODS FOR LEGACY SUPPORT =====

    private List<TestCase> generateValidTypicalCases() {
        return new ArrayList<>();
    }

    private List<TestCase> generateValidEdgeCases() {
        return new ArrayList<>();
    }

    private List<TestCase> generateInvalidEmptyCases() {
        List<TestCase> cases = new ArrayList<>();
        cases.add(new TestCase("", TestCaseType.INVALID_EMPTY, false, "Empty string"));
        return cases;
    }

    private List<TestCase> generateInvalidTypeCases() {
        return new ArrayList<>();
    }

    private List<TestCase> generateInvalidFormatCases() {
        return new ArrayList<>();
    }

    private List<TestCase> generateInvalidPatternCases() {
        return new ArrayList<>();
    }

    private List<TestCase> generateInvalidEnumCases() {
        List<TestCase> cases = new ArrayList<>();
        if (!enumValues.isEmpty()) {
            cases.add(new TestCase("INVALID_ENUM_VALUE", TestCaseType.INVALID_ENUM, false, "Invalid enum value"));
        }
        return cases;
    }

    private List<TestCase> generatePerformanceCases() {
        return new ArrayList<>();
    }

    // ===== STANDARD UTILITY METHODS =====

    /**
     * Standard utility method: Get strategies for scenario category
     */
    public Set<StrategyType> getStrategiesForCategory(StrategyCategory category) {
        return enabledStrategies.stream()
                .filter(strategy -> strategy.getCategory().equals(category.name()))
                .collect(HashSet::new, HashSet::add, HashSet::addAll);
    }

    /**
     * Standard utility method: Get scenarios for category
     */
    public Set<TestGenerationScenario> getScenariosForCategory(ScenarioCategory category) {
        return enabledScenarios.stream()
                .filter(scenario -> scenario.getCategory().equals(category.name()))
                .collect(HashSet::new, HashSet::add, HashSet::addAll);
    }

    /**
     * Standard utility method: Check if strategy is enabled
     */
    public boolean isStrategyEnabled(StrategyType strategy) {
        return enabledStrategies.contains(strategy);
    }

    /**
     * Standard utility method: Check if scenario is enabled
     */
    public boolean isScenarioEnabled(TestGenerationScenario scenario) {
        return enabledScenarios.contains(scenario);
    }

    /**
     * Standard utility method: Get complexity score
     */
    public double getComplexityScore() {
        return enabledStrategies.stream()
                .mapToInt(strategy -> strategy.getComplexity())
                .average()
                .orElse(1.0);
    }

    /**
     * Standard utility method: Get estimated test count
     */
    public int getEstimatedTestCount() {
        int baseCount = enabledScenarios.size() * 2; // Rough estimate
        return Math.min(baseCount, maxTestVariations);
    }

    // ===== STANDARD GETTERS - Tutarlılık Rehberi Uyumlu =====

    // Basic constraint getters
    public String getType() { return type; }
    public String getFormat() { return format; }
    public String getPattern() { return pattern; }
    public String getDescription() { return description; }
    public Object getDefaultValue() { return defaultValue; }
    public Object getExample() { return example; }

    // Standard strategy getters
    public Set<StrategyType> getEnabledStrategies() { return new HashSet<>(enabledStrategies); }
    public Set<TestGenerationScenario> getEnabledScenarios() { return new HashSet<>(enabledScenarios); }
    public StrategyType getDefaultStrategy() { return defaultStrategy; }

    // Test configuration getters
    public TestComplexity getTestComplexity() { return testComplexity; }
    public SecurityLevel getSecurityLevel() { return securityLevel; }
    public Boolean getAllowNull() { return allowNull; }
    public Boolean getAllowEmpty() { return allowEmpty; }
    public Integer getMaxTestVariations() { return maxTestVariations; }
    public Set<TestCaseType> getEnabledTestTypes() { return enabledTestTypes; }

    // String constraint getters
    public Integer getMinLength() { return minLength; }
    public Integer getMaxLength() { return maxLength; }
    public String getCharset() { return charset; }
    public Boolean getCaseSensitive() { return caseSensitive; }
    public List<String> getAllowedPrefixes() { return new ArrayList<>(allowedPrefixes); }
    public List<String> getAllowedSuffixes() { return new ArrayList<>(allowedSuffixes); }

    // Numeric constraint getters
    public BigDecimal getMinimum() { return minimum; }
    public BigDecimal getMaximum() { return maximum; }
    public Boolean getExclusiveMinimum() { return exclusiveMinimum; }
    public Boolean getExclusiveMaximum() { return exclusiveMaximum; }
    public BigDecimal getMultipleOf() { return multipleOf; }
    public Integer getDecimalPlaces() { return decimalPlaces; }
    public NumericFormat getNumberFormat() { return numberFormat; }

    // Array constraint getters
    public Integer getMinItems() { return minItems; }
    public Integer getMaxItems() { return maxItems; }
    public Boolean getUniqueItems() { return uniqueItems; }
    public DataConstraints getItemConstraints() { return itemConstraints; }
    public String getSortOrder() { return sortOrder; }

    // Object constraint getters
    public Integer getMinProperties() { return minProperties; }
    public Integer getMaxProperties() { return maxProperties; }
    public List<String> getRequiredFields() { return new ArrayList<>(requiredFields); }
    public Map<String, DataConstraints> getPropertyConstraints() { return new HashMap<>(propertyConstraints); }
    public List<String> getAdditionalProperties() { return new ArrayList<>(additionalProperties); }

    // Enum constraint getters
    public List<Object> getEnumValues() { return new ArrayList<>(enumValues); }
    public Map<String, String> getEnumDescriptions() { return new HashMap<>(enumDescriptions); }
    public Boolean getAllowCustomValues() { return allowCustomValues; }

    // Date/Time constraint getters
    public LocalDateTime getMinDate() { return minDate; }
    public LocalDateTime getMaxDate() { return maxDate; }
    public List<String> getDateFormats() { return new ArrayList<>(dateFormats); }
    public String getTimezone() { return timezone; }
    public Boolean getAllowPastDates() { return allowPastDates; }
    public Boolean getAllowFutureDates() { return allowFutureDates; }

    // File constraint getters
    public Long getMinFileSize() { return minFileSize; }
    public Long getMaxFileSize() { return maxFileSize; }
    public List<String> getAllowedMimeTypes() { return new ArrayList<>(allowedMimeTypes); }
    public List<String> getAllowedExtensions() { return new ArrayList<>(allowedExtensions); }

    // Security constraint getters
    public Boolean getSanitizeInput() { return sanitizeInput; }
    public List<String> getBlacklistedPatterns() { return new ArrayList<>(blacklistedPatterns); }

    // Additional getters
    public Boolean getGenerateBoundaryValues() { return generateBoundaryValues; }
    public Boolean getGenerateInvalidValues() { return generateInvalidValues; }
    public Boolean getGenerateEdgeCases() { return generateEdgeCases; }

    // ===== STANDARD SETTERS =====

    // Basic setters
    public void setType(String type) { this.type = type; }
    public void setFormat(String format) { this.format = format; }
    public void setPattern(String pattern) { this.pattern = pattern; }
    public void setDescription(String description) { this.description = description; }
    public void setDefaultValue(Object defaultValue) { this.defaultValue = defaultValue; }
    public void setExample(Object example) { this.example = example; }

    // Test configuration setters
    public void setTestComplexity(TestComplexity testComplexity) {
        this.testComplexity = testComplexity;
        initializeStandardStrategies();
    }
    public void setSecurityLevel(SecurityLevel securityLevel) {
        this.securityLevel = securityLevel;
        applySecurity(securityLevel);
    }
    public void setAllowNull(Boolean allowNull) { this.allowNull = allowNull; }
    public void setAllowEmpty(Boolean allowEmpty) { this.allowEmpty = allowEmpty; }
    public void setMaxTestVariations(Integer maxTestVariations) { this.maxTestVariations = maxTestVariations; }
    public void setEnabledTestTypes(Set<TestCaseType> enabledTestTypes) { this.enabledTestTypes = enabledTestTypes; }

    // String constraint setters
    public void setMinLength(Integer minLength) { this.minLength = minLength; }
    public void setMaxLength(Integer maxLength) { this.maxLength = maxLength; }
    public void setCharset(String charset) { this.charset = charset; }
    public void setCaseSensitive(Boolean caseSensitive) { this.caseSensitive = caseSensitive; }

    // Numeric constraint setters
    public void setMinimum(BigDecimal minimum) { this.minimum = minimum; }
    public void setMaximum(BigDecimal maximum) { this.maximum = maximum; }
    public void setExclusiveMinimum(Boolean exclusiveMinimum) { this.exclusiveMinimum = exclusiveMinimum; }
    public void setExclusiveMaximum(Boolean exclusiveMaximum) { this.exclusiveMaximum = exclusiveMaximum; }
    public void setMultipleOf(BigDecimal multipleOf) { this.multipleOf = multipleOf; }
    public void setDecimalPlaces(Integer decimalPlaces) { this.decimalPlaces = decimalPlaces; }
    public void setNumberFormat(NumericFormat numberFormat) { this.numberFormat = numberFormat; }

    // Array constraint setters
    public void setMinItems(Integer minItems) { this.minItems = minItems; }
    public void setMaxItems(Integer maxItems) { this.maxItems = maxItems; }
    public void setUniqueItems(Boolean uniqueItems) { this.uniqueItems = uniqueItems; }
    public void setItemConstraints(DataConstraints itemConstraints) { this.itemConstraints = itemConstraints; }
    public void setSortOrder(String sortOrder) { this.sortOrder = sortOrder; }

    // Object constraint setters
    public void setMinProperties(Integer minProperties) { this.minProperties = minProperties; }
    public void setMaxProperties(Integer maxProperties) { this.maxProperties = maxProperties; }
    public void setRequiredFields(List<String> requiredFields) { this.requiredFields = requiredFields; }
    public void setPropertyConstraints(Map<String, DataConstraints> propertyConstraints) { this.propertyConstraints = propertyConstraints; }
    public void setAdditionalProperties(List<String> additionalProperties) { this.additionalProperties = additionalProperties; }

    // Enum constraint setters
    public void setEnumValues(List<Object> enumValues) { this.enumValues = enumValues; }
    public void setEnumDescriptions(Map<String, String> enumDescriptions) { this.enumDescriptions = enumDescriptions; }
    public void setAllowCustomValues(Boolean allowCustomValues) { this.allowCustomValues = allowCustomValues; }

    // Date/Time constraint setters
    public void setMinDate(LocalDateTime minDate) { this.minDate = minDate; }
    public void setMaxDate(LocalDateTime maxDate) { this.maxDate = maxDate; }
    public void setDateFormats(List<String> dateFormats) { this.dateFormats = dateFormats; }
    public void setTimezone(String timezone) { this.timezone = timezone; }
    public void setAllowPastDates(Boolean allowPastDates) { this.allowPastDates = allowPastDates; }
    public void setAllowFutureDates(Boolean allowFutureDates) { this.allowFutureDates = allowFutureDates; }

    // File constraint setters
    public void setMinFileSize(Long minFileSize) { this.minFileSize = minFileSize; }
    public void setMaxFileSize(Long maxFileSize) { this.maxFileSize = maxFileSize; }
    public void setAllowedMimeTypes(List<String> allowedMimeTypes) { this.allowedMimeTypes = allowedMimeTypes; }
    public void setAllowedExtensions(List<String> allowedExtensions) { this.allowedExtensions = allowedExtensions; }

    // Security constraint setters
    public void setSanitizeInput(Boolean sanitizeInput) { this.sanitizeInput = sanitizeInput; }
    public void setBlacklistedPatterns(List<String> blacklistedPatterns) { this.blacklistedPatterns = blacklistedPatterns; }

    // Additional setters
    public void setGenerateBoundaryValues(Boolean generateBoundaryValues) { this.generateBoundaryValues = generateBoundaryValues; }
    public void setGenerateInvalidValues(Boolean generateInvalidValues) { this.generateInvalidValues = generateInvalidValues; }
    public void setGenerateEdgeCases(Boolean generateEdgeCases) { this.generateEdgeCases = generateEdgeCases; }

    // ===== SUPPORTING DATA CLASSES =====

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

    public static class RequestBodyInfo {
        private String contentType;
        private Object schema;
        private boolean required;

        public RequestBodyInfo() {}
        public RequestBodyInfo(String contentType, Object schema, boolean required) {
            this.contentType = contentType;
            this.schema = schema;
            this.required = required;
        }

        public String getContentType() { return contentType; }
        public Object getSchema() { return schema; }
        public boolean isRequired() { return required; }
        public void setContentType(String contentType) { this.contentType = contentType; }
        public void setSchema(Object schema) { this.schema = schema; }
        public void setRequired(boolean required) { this.required = required; }
    }

    public static class ResponseInfo {
        private String description;
        private String contentType;
        private Object schema;

        public ResponseInfo() {}
        public ResponseInfo(String description, String contentType, Object schema) {
            this.description = description;
            this.contentType = contentType;
            this.schema = schema;
        }

        public String getDescription() { return description; }
        public String getContentType() { return contentType; }
        public Object getSchema() { return schema; }
        public void setDescription(String description) { this.description = description; }
        public void setContentType(String contentType) { this.contentType = contentType; }
        public void setSchema(Object schema) { this.schema = schema; }
    }

    public static class TestStep {
        private String stepName;
        private String action;
        private Map<String, Object> parameters;

        public TestStep() {}
        public TestStep(String stepName, String action, Map<String, Object> parameters) {
            this.stepName = stepName;
            this.action = action;
            this.parameters = parameters != null ? new HashMap<>(parameters) : new HashMap<>();
        }

        public String getStepName() { return stepName; }
        public String getAction() { return action; }
        public Map<String, Object> getParameters() { return parameters; }
        public void setStepName(String stepName) { this.stepName = stepName; }
        public void setAction(String action) { this.action = action; }
        public void setParameters(Map<String, Object> parameters) { this.parameters = parameters; }
    }

    public static class TestDataSet {
        private Map<String, Object> inputData;
        private Map<String, Object> expectedOutputs;

        public TestDataSet() {
            this.inputData = new HashMap<>();
            this.expectedOutputs = new HashMap<>();
        }

        public TestDataSet(Map<String, Object> inputData, Map<String, Object> expectedOutputs) {
            this.inputData = inputData != null ? new HashMap<>(inputData) : new HashMap<>();
            this.expectedOutputs = expectedOutputs != null ? new HashMap<>(expectedOutputs) : new HashMap<>();
        }

        public Map<String, Object> getInputData() { return inputData; }
        public Map<String, Object> getExpectedOutputs() { return expectedOutputs; }
        public void setInputData(Map<String, Object> inputData) { this.inputData = inputData; }
        public void setExpectedOutputs(Map<String, Object> expectedOutputs) { this.expectedOutputs = expectedOutputs; }
    }

    public static class TestAssertion {
        private String assertionType;
        private String expectedValue;
        private String actualValue;

        public TestAssertion() {}
        public TestAssertion(String assertionType, String expectedValue, String actualValue) {
            this.assertionType = assertionType;
            this.expectedValue = expectedValue;
            this.actualValue = actualValue;
        }

        public String getAssertionType() { return assertionType; }
        public String getExpectedValue() { return expectedValue; }
        public String getActualValue() { return actualValue; }
        public void setAssertionType(String assertionType) { this.assertionType = assertionType; }
        public void setExpectedValue(String expectedValue) { this.expectedValue = expectedValue; }
        public void setActualValue(String actualValue) { this.actualValue = actualValue; }
    }

    public static class ComprehensiveTestSuite {
        private EndpointInfo endpoint;
        private List<GeneratedTestCase> testCases;
        private String executionId;
        private Instant generationTimestamp;
        private double qualityScore;

        private ComprehensiveTestSuite() {}

        public EndpointInfo getEndpoint() { return endpoint; }
        public List<GeneratedTestCase> getTestCases() { return testCases; }
        public String getExecutionId() { return executionId; }
        public Instant getGenerationTimestamp() { return generationTimestamp; }
        public double getQualityScore() { return qualityScore; }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private ComprehensiveTestSuite suite = new ComprehensiveTestSuite();

            public Builder withEndpoint(EndpointInfo endpoint) {
                suite.endpoint = endpoint;
                return this;
            }

            public Builder withTestCases(List<GeneratedTestCase> testCases) {
                suite.testCases = testCases != null ? new ArrayList<>(testCases) : new ArrayList<>();
                return this;
            }

            public Builder withExecutionId(String executionId) {
                suite.executionId = executionId;
                return this;
            }

            public Builder withGenerationTimestamp(Instant generationTimestamp) {
                suite.generationTimestamp = generationTimestamp;
                return this;
            }

            public ComprehensiveTestSuite build() {
                if (suite.executionId == null) {
                    suite.executionId = generateAdvancedExecutionId();
                }
                if (suite.generationTimestamp == null) {
                    suite.generationTimestamp = Instant.now();
                }
                if (suite.testCases == null) {
                    suite.testCases = new ArrayList<>();
                }

                suite.qualityScore = calculateQualityScore(suite);
                return suite;
            }
        }
    }

    private static double calculateQualityScore(ComprehensiveTestSuite suite) {
        if (suite.getTestCases() == null || suite.getTestCases().isEmpty()) {
            return 0.0;
        }

        double baseScore = 0.5;
        double complexityBonus = suite.getTestCases().stream()
                .mapToInt(GeneratedTestCase::getComplexity)
                .average()
                .orElse(1.0) / 10.0;

        double coverageBonus = Math.min(suite.getTestCases().size() / 10.0, 0.3);

        return Math.min(1.0, baseScore + complexityBonus + coverageBonus);
    }

    /**
     * Legacy TestCase class for backward compatibility
     */
    public static class TestCase {
        private final Object value;
        private final TestCaseType type;
        private final boolean valid;
        private final String description;

        public TestCase(Object value, TestCaseType type, boolean valid, String description) {
            this.value = value;
            this.type = type;
            this.valid = valid;
            this.description = description;
        }

        public Object getValue() { return value; }
        public TestCaseType getType() { return type; }
        public boolean isValid() { return valid; }
        public String getDescription() { return description; }
    }
}