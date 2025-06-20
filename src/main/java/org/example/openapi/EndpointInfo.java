package org.example.openapi;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ===== STANDARDIZED ENDPOINT INFO CLASS - TUTARLILIK REHBERİ UYUMLU =====
 *
 * Tutarlılık rehberine göre standardize edilmiş endpoint info class.
 * Standard enum'lar, method signature'ları ve data classes kullanır.
 * EndpointComplexity interface'i ile uyumlu.
 *
 * @author Enterprise Solutions Team
 * @version 4.0.0-STANDARDIZED
 * @since 2025.1
 */
public class EndpointInfo {

    // ===== STANDARD ENUMS - Tutarlılık Rehberi Uyumlu =====

    /**
     * Standard supporting enums
     */
    public enum StrategyCategory {
        FUNCTIONAL, SECURITY, PERFORMANCE, ADVANCED, SPECIALIZED
    }

    public enum ScenarioCategory {
        FUNCTIONAL, SECURITY, PERFORMANCE, INTEGRATION, COMPLIANCE, ADVANCED
    }

    // ===== ENHANCED ENDPOINT ENUMS =====

    public enum SecurityRisk {
        SQL_INJECTION("SQL injection risk", StrategyType.SECURITY_INJECTION, TestGenerationScenario.SQL_INJECTION_BASIC),
        XSS("XSS risk", StrategyType.SECURITY_XSS, TestGenerationScenario.XSS_REFLECTED),
        CSRF("CSRF risk", StrategyType.SECURITY_OWASP_TOP10, TestGenerationScenario.CSRF_PROTECTION),
        UNAUTHORIZED_ACCESS("Unauthorized access risk", StrategyType.SECURITY_PENETRATION, TestGenerationScenario.AUTH_BYPASS),
        DATA_EXPOSURE("Data exposure risk", StrategyType.SECURITY_OWASP_TOP10, TestGenerationScenario.XSS_REFLECTED),
        PRIVILEGE_ESCALATION("Privilege escalation risk", StrategyType.SECURITY_PENETRATION, TestGenerationScenario.AUTH_BYPASS),
        RATE_LIMIT_BYPASS("Rate limit bypass risk", StrategyType.SECURITY_BASIC, TestGenerationScenario.LOAD_TESTING_HEAVY);

        private final String description;
        private final StrategyType requiredStrategy;
        private final TestGenerationScenario associatedScenario;

        SecurityRisk(String description, StrategyType requiredStrategy, TestGenerationScenario associatedScenario) {
            this.description = description;
            this.requiredStrategy = requiredStrategy;
            this.associatedScenario = associatedScenario;
        }

        public String getDescription() { return description; }
        public StrategyType getRequiredStrategy() { return requiredStrategy; }
        public TestGenerationScenario getAssociatedScenario() { return associatedScenario; }
    }

    public enum BusinessCriticality {
        LOW("Low criticality", StrategyType.FUNCTIONAL_BASIC, 1),
        NORMAL("Normal criticality", StrategyType.FUNCTIONAL_COMPREHENSIVE, 2),
        HIGH("High criticality", StrategyType.FUNCTIONAL_BOUNDARY, 3),
        CRITICAL("Critical endpoint", StrategyType.SECURITY_BASIC, 4),
        MISSION_CRITICAL("Mission critical", StrategyType.SECURITY_PENETRATION, 5);

        private final String description;
        private final StrategyType minRequiredStrategy;
        private final int priority;

        BusinessCriticality(String description, StrategyType minRequiredStrategy, int priority) {
            this.description = description;
            this.minRequiredStrategy = minRequiredStrategy;
            this.priority = priority;
        }

        public String getDescription() { return description; }
        public StrategyType getMinRequiredStrategy() { return minRequiredStrategy; }
        public int getPriority() { return priority; }
    }

    public enum TestCategory {
        FUNCTIONAL("Functional testing", StrategyType.FUNCTIONAL_BASIC, TestGenerationScenario.HAPPY_PATH),
        SECURITY("Security testing", StrategyType.SECURITY_BASIC, TestGenerationScenario.XSS_REFLECTED),
        PERFORMANCE("Performance testing", StrategyType.PERFORMANCE_BASIC, TestGenerationScenario.LOAD_TESTING_LIGHT),
        INTEGRATION("Integration testing", StrategyType.FUNCTIONAL_COMPREHENSIVE, TestGenerationScenario.ERROR_HANDLING),
        CONTRACT("Contract testing", StrategyType.FUNCTIONAL_BOUNDARY, TestGenerationScenario.BOUNDARY_VALUES),
        REGRESSION("Regression testing", StrategyType.FUNCTIONAL_COMPREHENSIVE, TestGenerationScenario.ERROR_HANDLING),
        LOAD("Load testing", StrategyType.PERFORMANCE_LOAD, TestGenerationScenario.LOAD_TESTING_HEAVY),
        STRESS("Stress testing", StrategyType.PERFORMANCE_STRESS, TestGenerationScenario.STRESS_TESTING),
        CHAOS("Chaos testing", StrategyType.ADVANCED_CONCURRENCY, TestGenerationScenario.CONCURRENCY_RACE_CONDITIONS);

        private final String description;
        private final StrategyType associatedStrategy;
        private final TestGenerationScenario defaultScenario;

        TestCategory(String description, StrategyType associatedStrategy, TestGenerationScenario defaultScenario) {
            this.description = description;
            this.associatedStrategy = associatedStrategy;
            this.defaultScenario = defaultScenario;
        }

        public String getDescription() { return description; }
        public StrategyType getAssociatedStrategy() { return associatedStrategy; }
        public TestGenerationScenario getDefaultScenario() { return defaultScenario; }
    }

    // ===== CORE ENDPOINT FIELDS =====

    // Standard interface fields
    private String method;
    private String path;
    private String operationId;
    private List<ParameterInfo> parameters = new ArrayList<>();
    private RequestBodyInfo requestBodyInfo;
    private Map<String, ResponseInfo> responses = new HashMap<>();
    private List<String> securitySchemes = new ArrayList<>();
    private boolean requiresAuthentication;
    private boolean hasParameters;
    private boolean hasRequestBody;

    // Enhanced endpoint fields
    private String summary;
    private String description;
    private String resourceType;
    private Set<String> tags = new HashSet<>();

    // Standard Strategy Configuration
    private Set<StrategyType> enabledStrategies = new HashSet<>();
    private Set<TestGenerationScenario> enabledScenarios = new HashSet<>();
    private StrategyType defaultStrategy = StrategyType.FUNCTIONAL_COMPREHENSIVE;

    // Parameters - Enhanced
    private List<String> requiredParameters = new ArrayList<>();
    private Map<String, ParameterInfo> parameterMap = new HashMap<>();

    // Parameter complexity metrics
    private int complexParameterCount = 0;
    private int nestedObjectCount = 0;
    private int arrayParameterCount = 0;
    private int enumParameterCount = 0;

    // Request Body - Enhanced
    private boolean hasFileUpload = false;
    private boolean hasBulkOperations = false;
    private int requestBodyComplexity = 0;

    // Response complexity metrics
    private int responseTypeCount = 0;
    private int complexResponseCount = 0;
    private int errorResponseCount = 0;
    private int customErrorCount = 0;

    // Security - Enhanced
    private boolean requiresAuthorization;
    private boolean hasRoleBasedAccess;
    private boolean handlesPersonalData;
    private boolean hasRateLimiting;
    private Set<SecurityRisk> securityRisks = new HashSet<>();

    // Business Logic
    private boolean isDataModifying;
    private boolean hasBusinessRules;
    private boolean hasValidationRules;
    private boolean hasWorkflow;
    private boolean isAsyncOperation;
    private boolean hasStateTransition;
    private BusinessCriticality businessCriticality = BusinessCriticality.NORMAL;

    // Data Structure Analysis
    private int nestedLevels = 0;
    private int arrayFieldCount = 0;
    private int objectFieldCount = 0;
    private int enumFieldCount = 0;
    private int totalFieldCount = 0;

    // Error Handling
    private int errorStatusCount = 0;
    private boolean hasErrorRecovery = false;
    private boolean hasRetryLogic = false;
    private List<String> possibleErrors = new ArrayList<>();

    // Performance Indicators
    private boolean isHighTraffic = false;
    private boolean isComputeIntensive = false;
    private boolean hasExternalDependencies = false;
    private PerformanceProfile performanceProfile = PerformanceProfile.STANDARD;

    // Dependencies & Relationships
    private List<String> dependencies = new ArrayList<>();
    private Set<String> relatedEndpoints = new HashSet<>();
    private List<String> externalApis = new ArrayList<>();
    private List<String> databaseTables = new ArrayList<>();

    // Test Generation
    private Set<TestCategory> recommendedTestCategories = new HashSet<>();
    private int estimatedTestCount = 0;
    private TestGenerationHints testHints;

    // Metadata & Documentation
    private boolean isDeprecated = false;
    private String deprecationMessage;
    private String version;
    private Map<String, String> extensions = new HashMap<>();
    private List<String> examples = new ArrayList<>();

    // ===== CONSTRUCTORS =====

    public EndpointInfo() {
        this.testHints = new TestGenerationHints();
        initializeStandardStrategies();
    }

    public EndpointInfo(String method, String path, String operationId) {
        this();
        this.method = method;
        this.path = path;
        this.operationId = operationId;
        analyzeMethod();
        initializeStandardStrategies();
    }

    /**
     * Standard method: Initialize standard strategies based on endpoint characteristics
     */
    private void initializeStandardStrategies() {
        enabledStrategies.clear();
        enabledScenarios.clear();

        // Always add basic functional strategy
        enabledStrategies.add(StrategyType.FUNCTIONAL_BASIC);
        enabledScenarios.add(TestGenerationScenario.HAPPY_PATH);

        // Add strategies based on business criticality
        if (businessCriticality != null) {
            enabledStrategies.add(businessCriticality.getMinRequiredStrategy());
            this.defaultStrategy = businessCriticality.getMinRequiredStrategy();
        }

        // Add strategies based on security risks
        for (SecurityRisk risk : securityRisks) {
            enabledStrategies.add(risk.getRequiredStrategy());
            enabledScenarios.add(risk.getAssociatedScenario());
        }

        // Add strategies based on performance profile
        if (performanceProfile != null) {
            enabledStrategies.add(performanceProfile.getRecommendedStrategy());
            enabledScenarios.add(performanceProfile.getAssociatedScenario());
        }

        // Add strategies based on test categories
        for (TestCategory category : recommendedTestCategories) {
            enabledStrategies.add(category.getAssociatedStrategy());
            enabledScenarios.add(category.getDefaultScenario());
        }
    }

    private void analyzeMethod() {
        if (method == null) return;

        switch (method.toUpperCase()) {
            case "GET":
                this.isDataModifying = false;
                this.performanceProfile = PerformanceProfile.FAST;
                break;
            case "POST":
                this.isDataModifying = true;
                this.hasBusinessRules = true;
                this.enabledScenarios.add(TestGenerationScenario.ERROR_HANDLING);
                break;
            case "PUT":
            case "PATCH":
                this.isDataModifying = true;
                this.hasBusinessRules = true;
                this.hasValidationRules = true;
                this.enabledScenarios.add(TestGenerationScenario.BOUNDARY_VALUES);
                break;
            case "DELETE":
                this.isDataModifying = true;
                this.businessCriticality = BusinessCriticality.HIGH;
                this.enabledStrategies.add(StrategyType.SECURITY_BASIC);
                break;
        }
    }

    // ===== BUILDER PATTERN - STANDARDIZED =====

    /**
     * Standard factory method
     */
    public static EndpointInfo create(String method, String path, String operationId) {
        return new EndpointInfo(method, path, operationId);
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
        private EndpointInfo endpoint = new EndpointInfo();

        public Builder withMethod(String method) {
            endpoint.method = method;
            endpoint.analyzeMethod();
            return this;
        }

        public Builder withPath(String path) {
            endpoint.path = path;
            return this;
        }

        public Builder withOperationId(String operationId) {
            endpoint.operationId = operationId;
            return this;
        }

        public Builder withSummary(String summary) {
            endpoint.summary = summary;
            return this;
        }

        public Builder withDescription(String description) {
            endpoint.description = description;
            return this;
        }

        public Builder withTag(String tag) {
            endpoint.tags.add(tag);
            return this;
        }

        public Builder withParameter(ParameterInfo parameter) {
            endpoint.addParameter(parameter);
            return this;
        }

        public Builder withResponse(String statusCode, ResponseInfo response) {
            endpoint.addResponse(statusCode, response);
            return this;
        }

        public Builder withSecurity(String scheme) {
            endpoint.addSecurityScheme(scheme);
            return this;
        }

        public Builder withStrategy(StrategyType strategy) {
            endpoint.enabledStrategies.add(strategy);
            return this;
        }

        public Builder withScenario(TestGenerationScenario scenario) {
            endpoint.enabledScenarios.add(scenario);
            endpoint.enabledStrategies.add(scenario.getRecommendedStrategy());
            return this;
        }

        public Builder withBusinessCriticality(BusinessCriticality criticality) {
            endpoint.businessCriticality = criticality;
            endpoint.enabledStrategies.add(criticality.getMinRequiredStrategy());
            return this;
        }

        public Builder withSecurityRisk(SecurityRisk risk) {
            endpoint.securityRisks.add(risk);
            endpoint.enabledStrategies.add(risk.getRequiredStrategy());
            endpoint.enabledScenarios.add(risk.getAssociatedScenario());
            endpoint.recommendedTestCategories.add(TestCategory.SECURITY);
            return this;
        }

        public Builder withPerformanceProfile(PerformanceProfile profile) {
            endpoint.performanceProfile = profile;
            endpoint.enabledStrategies.add(profile.getRecommendedStrategy());
            endpoint.enabledScenarios.add(profile.getAssociatedScenario());
            return this;
        }

        public Builder withTestCategory(TestCategory category) {
            endpoint.recommendedTestCategories.add(category);
            endpoint.enabledStrategies.add(category.getAssociatedStrategy());
            endpoint.enabledScenarios.add(category.getDefaultScenario());
            return this;
        }

        public Builder withDependency(String dependency) {
            endpoint.dependencies.add(dependency);
            endpoint.hasExternalDependencies = true;
            endpoint.recommendedTestCategories.add(TestCategory.INTEGRATION);
            return this;
        }

        public EndpointInfo build() {
            validateAndEnhanceConfiguration(endpoint);
            return endpoint;
        }
    }

    // ===== STANDARD UTILITY METHODS =====

    /**
     * Standard validation method
     */
    private static void validateAndEnhanceConfiguration(EndpointInfo endpoint) {
        if (endpoint.method == null || endpoint.method.trim().isEmpty()) {
            throw new IllegalArgumentException("HTTP method cannot be null or empty");
        }

        if (endpoint.path == null || endpoint.path.trim().isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }

        // Ensure we have at least basic strategies enabled
        if (endpoint.enabledStrategies.isEmpty()) {
            endpoint.enabledStrategies.add(StrategyType.FUNCTIONAL_BASIC);
        }

        if (endpoint.enabledScenarios.isEmpty()) {
            endpoint.enabledScenarios.add(TestGenerationScenario.HAPPY_PATH);
        }

        // Perform complexity analysis
        endpoint.performComplexityAnalysis();
    }

    /**
     * Standard method: Generate advanced cache key for endpoint
     */
    private String generateAdvancedCacheKey() {
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(method != null ? method.toUpperCase() : "UNKNOWN");
        keyBuilder.append("_");
        keyBuilder.append(path != null ? path.replaceAll("[^a-zA-Z0-9]", "_") : "unknown_path");
        keyBuilder.append("_");
        keyBuilder.append(operationId != null ? operationId : "op_" + System.currentTimeMillis());

        return keyBuilder.toString();
    }

    /**
     * Standard method: Generate advanced execution ID
     */
    private static String generateAdvancedExecutionId() {
        return "endpoint_" + System.currentTimeMillis() + "_" +
                Thread.currentThread().getId() + "_" +
                (int)(Math.random() * 10000);
    }

    // ===== FLUENT API METHODS =====

    public EndpointInfo withSummary(String summary) {
        this.summary = summary;
        return this;
    }

    public EndpointInfo withDescription(String description) {
        this.description = description;
        return this;
    }

    public EndpointInfo withTag(String tag) {
        this.tags.add(tag);
        return this;
    }

    public EndpointInfo withParameter(ParameterInfo parameter) {
        addParameter(parameter);
        return this;
    }

    public EndpointInfo withResponse(String statusCode, ResponseInfo response) {
        addResponse(statusCode, response);
        return this;
    }

    public EndpointInfo withSecurity(String scheme) {
        addSecurityScheme(scheme);
        return this;
    }

    public EndpointInfo withBusinessCriticality(BusinessCriticality criticality) {
        this.businessCriticality = criticality;
        this.enabledStrategies.add(criticality.getMinRequiredStrategy());
        initializeStandardStrategies();
        return this;
    }

    public EndpointInfo withSecurityRisk(SecurityRisk risk) {
        this.securityRisks.add(risk);
        this.enabledStrategies.add(risk.getRequiredStrategy());
        this.enabledScenarios.add(risk.getAssociatedScenario());
        this.recommendedTestCategories.add(TestCategory.SECURITY);
        return this;
    }

    public EndpointInfo withDependency(String dependency) {
        this.dependencies.add(dependency);
        this.hasExternalDependencies = true;
        this.recommendedTestCategories.add(TestCategory.INTEGRATION);
        return this;
    }

    // ===== STANDARD ANALYSIS METHODS =====

    /**
     * Standard method: Generate comprehensive test suite for endpoint
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

        // Apply test limits based on estimated count
        if (testCases.size() > estimatedTestCount && estimatedTestCount > 0) {
            testCases = prioritizeAndLimit(testCases);
        }

        return ComprehensiveTestSuite.builder()
                .withEndpoint(this)
                .withTestCases(testCases)
                .withExecutionId(executionId)
                .withGenerationTimestamp(Instant.now())
                .build();
    }

    /**
     * Standard method: Generate advanced strategy recommendation
     *
     * @return AdvancedStrategyRecommendation with standard interface
     */
    public AdvancedStrategyRecommendation generateAdvancedStrategyRecommendation() {
        return new AdvancedStrategyRecommendation(
                defaultStrategy,
                new ArrayList<TestGenerationScenario>(enabledScenarios),
                calculateConfidenceScore()
        );
    }

    private List<GeneratedTestCase> generateTestCasesForScenario(TestGenerationScenario scenario, String executionId) {
        List<GeneratedTestCase> testCases = new ArrayList<>();

        testCases.add(GeneratedTestCase.builder()
                .withTestId(executionId + "_" + scenario.name().toLowerCase())
                .withTestName(scenario.getDescription() + " for " + method + " " + path)
                .withDescription("Test case for " + scenario.getDescription())
                .withScenario(scenario)
                .withStrategyType(scenario.getRecommendedStrategy())
                .withEndpoint(this)
                .withComplexity(scenario.getComplexity())
                .withPriority(businessCriticality.getPriority())
                .withTags(Set.of(scenario.getCategory().toLowerCase(), method.toLowerCase()))
                .build());

        return testCases;
    }

    private List<GeneratedTestCase> prioritizeAndLimit(List<GeneratedTestCase> cases) {
        return cases.stream()
                .sorted((a, b) -> {
                    // Sort by priority (lower is higher priority), then by complexity
                    int priorityComparison = Integer.compare(a.getPriority(), b.getPriority());
                    if (priorityComparison != 0) return priorityComparison;
                    return Integer.compare(b.getComplexity(), a.getComplexity());
                })
                .limit(estimatedTestCount > 0 ? estimatedTestCount : 50)
                .collect(Collectors.toList());
    }

    private double calculateConfidenceScore() {
        double baseScore = 0.7; // Base confidence

        // Higher confidence with more analysis
        if (!parameters.isEmpty()) baseScore += 0.1;
        if (!responses.isEmpty()) baseScore += 0.1;
        if (businessCriticality != BusinessCriticality.NORMAL) baseScore += 0.05;
        if (!securityRisks.isEmpty()) baseScore += 0.05;

        return Math.min(1.0, baseScore);
    }

    /**
     * Endpoint'in kapsamlı analizini yapar ve complexity hesaplar
     */
    public void performComplexityAnalysis() {
        // Calculate nested levels
        calculateNestedLevels();

        // Analyze business logic
        analyzeBusinessLogic();

        // Analyze security requirements
        analyzeSecurityRequirements();

        // Analyze performance requirements
        analyzePerformanceRequirements();

        // Generate test recommendations
        generateTestRecommendations();

        // Update test hints
        updateTestHints();

        // Update strategy configuration
        initializeStandardStrategies();
    }

    private void calculateNestedLevels() {
        int maxLevel = 0;

        // Check parameters
        for (ParameterInfo param : parameters) {
            if (param.getDataConstraints() != null) {
                maxLevel = Math.max(maxLevel, calculateTypeNestingLevel(param.getDataConstraints()));
            }
        }

        // Check request body
        if (requestBodyInfo != null) {
            maxLevel = Math.max(maxLevel, 1); // Simplified for now
        }

        // Check responses
        maxLevel = Math.max(maxLevel, responses.size() > 0 ? 1 : 0);

        this.nestedLevels = maxLevel;
    }

    private int calculateTypeNestingLevel(DataConstraints constraints) {
        // Simplified nesting calculation
        int level = 0;

        if ("object".equals(constraints.getType())) {
            level = 1;
        } else if ("array".equals(constraints.getType())) {
            level = 1;
        }

        return level;
    }

    private void analyzeBusinessLogic() {
        // Determine if endpoint modifies data
        this.isDataModifying = Arrays.asList("POST", "PUT", "PATCH", "DELETE")
                .contains(method.toUpperCase());

        // Check for business rules based on parameters and responses
        this.hasBusinessRules = complexParameterCount > 2 ||
                requiredParameters.size() > 3 ||
                responseTypeCount > 2;

        // Check for validation rules
        this.hasValidationRules = parameters.stream()
                .anyMatch(p -> p.getDataConstraints() != null &&
                        (p.getDataConstraints().getPattern() != null ||
                                p.getDataConstraints().getMinimum() != null ||
                                p.getDataConstraints().getMaximum() != null));

        // Check for workflow (multi-step operations)
        this.hasWorkflow = dependencies.size() > 2 ||
                tags.stream().anyMatch(tag -> tag.toLowerCase().contains("workflow"));

        // Add appropriate strategies
        if (hasBusinessRules) {
            enabledStrategies.add(StrategyType.FUNCTIONAL_COMPREHENSIVE);
        }
        if (hasValidationRules) {
            enabledStrategies.add(StrategyType.FUNCTIONAL_BOUNDARY);
            enabledScenarios.add(TestGenerationScenario.BOUNDARY_VALUES);
        }
    }

    private void analyzeSecurityRequirements() {
        // Authorization vs Authentication
        this.requiresAuthorization = securitySchemes.size() > 1 ||
                tags.stream().anyMatch(tag -> tag.toLowerCase().contains("admin"));

        // Role-based access
        this.hasRoleBasedAccess = tags.stream().anyMatch(tag ->
                tag.toLowerCase().contains("role") || tag.toLowerCase().contains("permission"));

        // Personal data handling
        this.handlesPersonalData = parameters.stream().anyMatch(p ->
                isPersonalDataField(p.getName())) ||
                tags.stream().anyMatch(tag -> tag.toLowerCase().contains("personal"));

        // Rate limiting requirements
        this.hasRateLimiting = isHighTraffic || businessCriticality.ordinal() >= BusinessCriticality.HIGH.ordinal();

        // Add security strategies based on analysis
        if (requiresAuthentication || requiresAuthorization) {
            enabledStrategies.add(StrategyType.SECURITY_BASIC);
            enabledScenarios.add(TestGenerationScenario.AUTH_BYPASS);
        }
        if (handlesPersonalData) {
            enabledStrategies.add(StrategyType.SECURITY_OWASP_TOP10);
            securityRisks.add(SecurityRisk.DATA_EXPOSURE);
        }
    }

    private boolean isPersonalDataField(String fieldName) {
        String lower = fieldName.toLowerCase();
        return lower.contains("email") || lower.contains("phone") ||
                lower.contains("name") || lower.contains("address") ||
                lower.contains("ssn") || lower.contains("personal");
    }

    private void analyzePerformanceRequirements() {
        // High traffic detection
        this.isHighTraffic = tags.stream().anyMatch(tag ->
                tag.toLowerCase().contains("public") || tag.toLowerCase().contains("api"));

        // Compute intensive detection
        this.isComputeIntensive = method.equals("POST") &&
                (hasFileUpload || arrayParameterCount > 0 || hasBulkOperations);

        // External dependencies
        this.hasExternalDependencies = !dependencies.isEmpty() || !externalApis.isEmpty();

        // Add performance strategies
        if (isHighTraffic) {
            enabledStrategies.add(StrategyType.PERFORMANCE_LOAD);
            enabledScenarios.add(TestGenerationScenario.LOAD_TESTING_HEAVY);
        }
        if (isComputeIntensive) {
            enabledStrategies.add(StrategyType.PERFORMANCE_STRESS);
            enabledScenarios.add(TestGenerationScenario.STRESS_TESTING);
        }
    }

    private void generateTestRecommendations() {
        recommendedTestCategories.clear();

        // Always recommend functional tests
        recommendedTestCategories.add(TestCategory.FUNCTIONAL);

        // Security tests
        if (requiresAuthentication || !securityRisks.isEmpty()) {
            recommendedTestCategories.add(TestCategory.SECURITY);
        }

        // Performance tests
        if (isHighTraffic || isComputeIntensive) {
            recommendedTestCategories.add(TestCategory.PERFORMANCE);
        }

        // Integration tests
        if (hasExternalDependencies || !dependencies.isEmpty()) {
            recommendedTestCategories.add(TestCategory.INTEGRATION);
        }

        // Contract tests
        if (businessCriticality.ordinal() >= BusinessCriticality.HIGH.ordinal()) {
            recommendedTestCategories.add(TestCategory.CONTRACT);
        }

        // Load tests
        if (isHighTraffic) {
            recommendedTestCategories.add(TestCategory.LOAD);
        }

        // Regression tests
        if (businessCriticality == BusinessCriticality.CRITICAL ||
                businessCriticality == BusinessCriticality.MISSION_CRITICAL) {
            recommendedTestCategories.add(TestCategory.REGRESSION);
        }

        // Estimate test count
        estimatedTestCount = Math.max(10,
                parameters.size() * 2 +
                        responses.size() * 3 +
                        (requiresAuthentication ? 5 : 0) +
                        (isDataModifying ? 8 : 3) +
                        securityRisks.size() * 3 +
                        recommendedTestCategories.size() * 2);
    }

    private void updateTestHints() {
        if (testHints == null) {
            testHints = new TestGenerationHints();
        }

        testHints.updateFromEndpoint(this);
    }

    // ===== PARAMETER AND RESPONSE MANAGEMENT =====

    public void addParameter(ParameterInfo parameter) {
        this.parameters.add(parameter);
        this.parameterMap.put(parameter.getName(), parameter);
        this.hasParameters = true;

        if (parameter.isRequired()) {
            this.requiredParameters.add(parameter.getName());
        }

        analyzeParameterComplexity(parameter);
    }

    public void addResponse(String statusCode, ResponseInfo response) {
        this.responses.put(statusCode, response);

        analyzeResponseComplexity(statusCode, response);
    }

    public void addSecurityScheme(String scheme) {
        this.securitySchemes.add(scheme);
        this.requiresAuthentication = true;
        this.enabledStrategies.add(StrategyType.SECURITY_BASIC);
    }

    private void analyzeParameterComplexity(ParameterInfo parameter) {
        totalFieldCount++;

        if (parameter.getDataConstraints() != null) {
            DataConstraints constraints = parameter.getDataConstraints();

            // Complex type analysis
            if (isComplexType(constraints.getType())) {
                complexParameterCount++;
            }

            // Nested object detection
            if ("object".equals(constraints.getType())) {
                objectFieldCount++;
                if (constraints.getMinProperties() != null && constraints.getMinProperties() > 3) {
                    nestedObjectCount++;
                }
            }

            // Array detection
            if ("array".equals(constraints.getType())) {
                arrayFieldCount++;
                arrayParameterCount++;
            }

            // Enum detection
            if (constraints.getEnumValues() != null && !constraints.getEnumValues().isEmpty()) {
                enumFieldCount++;
                enumParameterCount++;
            }
        }

        // File upload detection
        if ("file".equals(parameter.getType()) || "binary".equals(parameter.getFormat())) {
            hasFileUpload = true;
            recommendedTestCategories.add(TestCategory.SECURITY);
        }
    }

    private void analyzeResponseComplexity(String statusCode, ResponseInfo response) {
        responseTypeCount++;

        // Error response analysis
        if (statusCode.startsWith("4") || statusCode.startsWith("5")) {
            errorResponseCount++;
            errorStatusCount++;

            if (!statusCode.equals("400") && !statusCode.equals("401") &&
                    !statusCode.equals("403") && !statusCode.equals("404") &&
                    !statusCode.equals("500")) {
                customErrorCount++;
            }
        }

        // Complex response detection
        if (response != null && response.getSchema() != null) {
            if (isComplexSchema(response.getSchema())) {
                complexResponseCount++;
            }
        }
    }

    private boolean isComplexType(String type) {
        return "object".equals(type) || "array".equals(type) ||
                type == null || type.contains("oneOf") || type.contains("anyOf");
    }

    private boolean isComplexSchema(Object schema) {
        // This would analyze the schema complexity
        // For now, return true for any non-primitive schema
        return schema != null;
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
     * Endpoint'in risk skorunu hesaplar
     */
    public double calculateRiskScore() {
        double riskScore = 0.0;

        // Business criticality risk
        riskScore += businessCriticality.ordinal() * 0.15;

        // Security risk
        riskScore += securityRisks.size() * 0.1;

        // Data modification risk
        if (isDataModifying) riskScore += 0.2;

        // Authentication risk
        if (requiresAuthentication) riskScore += 0.1;

        // External dependency risk
        if (hasExternalDependencies) riskScore += 0.15;

        // Complexity risk
        riskScore += (getComplexityScore() / 5.0) * 0.3;

        return Math.min(1.0, riskScore);
    }

    // ===== STANDARD GETTERS - Tutarlılık Rehberi Uyumlu =====

    // Standard interface getters
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

    // Standard strategy getters
    public Set<StrategyType> getEnabledStrategies() { return new HashSet<>(enabledStrategies); }
    public Set<TestGenerationScenario> getEnabledScenarios() { return new HashSet<>(enabledScenarios); }
    public StrategyType getDefaultStrategy() { return defaultStrategy; }

    // Enhanced endpoint getters
    public String getSummary() { return summary; }
    public String getDescription() { return description; }
    public String getResourceType() { return resourceType; }
    public Set<String> getTags() { return new HashSet<>(tags); }

    // Complexity metrics getters
    public int getParameterCount() { return parameters.size(); }
    public int getComplexParameterCount() { return complexParameterCount; }
    public int getRequiredParameterCount() { return requiredParameters.size(); }
    public int getNestedObjectCount() { return nestedObjectCount; }
    public int getResponseTypeCount() { return responseTypeCount; }
    public int getComplexResponseCount() { return complexResponseCount; }
    public int getErrorResponseCount() { return errorResponseCount; }
    public int getCustomErrorCount() { return customErrorCount; }

    // Security getters
    public boolean requiresAuthorization() { return requiresAuthorization; }
    public boolean hasRoleBasedAccess() { return hasRoleBasedAccess; }
    public boolean handlesPersonalData() { return handlesPersonalData; }
    public boolean hasRateLimiting() { return hasRateLimiting; }
    public Set<SecurityRisk> getSecurityRisks() { return new HashSet<>(securityRisks); }

    // Business logic getters
    public String getHttpMethod() { return method; }
    public boolean isDataModifying() { return isDataModifying; }
    public boolean hasBusinessRules() { return hasBusinessRules; }
    public boolean hasValidationRules() { return hasValidationRules; }
    public boolean hasWorkflow() { return hasWorkflow; }
    public BusinessCriticality getBusinessCriticality() { return businessCriticality; }

    // Data structure getters
    public int getNestedLevels() { return nestedLevels; }
    public int getArrayFieldCount() { return arrayFieldCount; }
    public int getObjectFieldCount() { return objectFieldCount; }
    public int getEnumFieldCount() { return enumFieldCount; }
    public int getTotalFieldCount() { return totalFieldCount; }

    // Error handling getters
    public int getErrorStatusCount() { return errorStatusCount; }
    public boolean hasErrorRecovery() { return hasErrorRecovery; }
    public boolean hasRetryLogic() { return hasRetryLogic; }
    public List<String> getPossibleErrors() { return new ArrayList<>(possibleErrors); }

    // Performance getters
    public boolean isHighTraffic() { return isHighTraffic; }
    public boolean isComputeIntensive() { return isComputeIntensive; }
    public boolean isHasExternalDependencies() { return hasExternalDependencies; }
    public PerformanceProfile getPerformanceProfile() { return performanceProfile; }

    // Test generation getters
    public Set<TestCategory> getRecommendedTestCategories() { return new HashSet<>(recommendedTestCategories); }
    public int getEstimatedTestCount() { return estimatedTestCount; }
    public TestGenerationHints getTestHints() { return testHints; }

    // Dependencies getters
    public List<String> getDependencies() { return new ArrayList<>(dependencies); }
    public Set<String> getRelatedEndpoints() { return new HashSet<>(relatedEndpoints); }
    public List<String> getExternalApis() { return new ArrayList<>(externalApis); }
    public List<String> getDatabaseTables() { return new ArrayList<>(databaseTables); }

    // Metadata getters
    public boolean isDeprecated() { return isDeprecated; }
    public String getDeprecationMessage() { return deprecationMessage; }
    public String getVersion() { return version; }
    public Map<String, String> getExtensions() { return new HashMap<>(extensions); }
    public List<String> getExamples() { return new ArrayList<>(examples); }

    // Additional getters
    public List<String> getRequiredParameters() { return new ArrayList<>(requiredParameters); }
    public boolean isAsyncOperation() { return isAsyncOperation; }
    public boolean isHasBulkOperations() { return hasBulkOperations; }
    public boolean isHasFileUpload() { return hasFileUpload; }
    public int getArrayParameterCount() { return arrayParameterCount; }
    public int getEnumParameterCount() { return enumParameterCount; }
    public int getRequestBodyComplexity() { return requestBodyComplexity; }

    // ===== STANDARD SETTERS =====

    public void setMethod(String method) {
        this.method = method;
        analyzeMethod();
        initializeStandardStrategies();
    }
    public void setPath(String path) { this.path = path; }
    public void setOperationId(String operationId) { this.operationId = operationId; }
    public void setParameters(List<ParameterInfo> parameters) {
        this.parameters = parameters;
        this.hasParameters = !parameters.isEmpty();
        updateParameterMaps();
    }
    public void setRequestBodyInfo(RequestBodyInfo requestBodyInfo) {
        this.requestBodyInfo = requestBodyInfo;
        this.hasRequestBody = requestBodyInfo != null;
    }
    public void setResponses(Map<String, ResponseInfo> responses) {
        this.responses = responses;
        updateResponseMaps();
    }
    public void setSecuritySchemes(List<String> securitySchemes) {
        this.securitySchemes = securitySchemes;
        this.requiresAuthentication = !securitySchemes.isEmpty();
    }
    public void setRequiresAuthentication(boolean requiresAuthentication) { this.requiresAuthentication = requiresAuthentication; }
    public void setHasParameters(boolean hasParameters) { this.hasParameters = hasParameters; }
    public void setHasRequestBody(boolean hasRequestBody) { this.hasRequestBody = hasRequestBody; }

    public void setSummary(String summary) { this.summary = summary; }
    public void setDescription(String description) { this.description = description; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }
    public void setTags(Set<String> tags) { this.tags = tags; }
    public void setBusinessCriticality(BusinessCriticality businessCriticality) {
        this.businessCriticality = businessCriticality;
        initializeStandardStrategies();
    }
    public void setSecurityRisks(Set<SecurityRisk> securityRisks) { this.securityRisks = securityRisks; }
    public void setPerformanceProfile(PerformanceProfile performanceProfile) { this.performanceProfile = performanceProfile; }
    public void setRecommendedTestCategories(Set<TestCategory> recommendedTestCategories) { this.recommendedTestCategories = recommendedTestCategories; }
    public void setEstimatedTestCount(int estimatedTestCount) { this.estimatedTestCount = estimatedTestCount; }
    public void setTestHints(TestGenerationHints testHints) { this.testHints = testHints; }
    public void setDependencies(List<String> dependencies) { this.dependencies = dependencies; }
    public void setDeprecated(boolean deprecated) { this.isDeprecated = deprecated; }
    public void setDeprecationMessage(String deprecationMessage) { this.deprecationMessage = deprecationMessage; }
    public void setVersion(String version) { this.version = version; }

    private void updateParameterMaps() {
        parameterMap.clear();
        requiredParameters.clear();
        complexParameterCount = 0;
        arrayParameterCount = 0;
        enumParameterCount = 0;

        for (ParameterInfo param : parameters) {
            parameterMap.put(param.getName(), param);
            if (param.isRequired()) {
                requiredParameters.add(param.getName());
            }
            analyzeParameterComplexity(param);
        }
    }

    private void updateResponseMaps() {
        responseTypeCount = responses.size();
        errorResponseCount = 0;
        customErrorCount = 0;

        for (Map.Entry<String, ResponseInfo> entry : responses.entrySet()) {
            analyzeResponseComplexity(entry.getKey(), entry.getValue());
        }
    }

    // ===== UTILITY METHODS =====

    public boolean isHighComplexity() {
        return getComplexityScore() >= 3.0;
    }

    public boolean requiresExtensiveTesting() {
        return businessCriticality.ordinal() >= BusinessCriticality.HIGH.ordinal() ||
                !securityRisks.isEmpty() ||
                isHighComplexity();
    }

    // ===== ADDITIONAL SETTERS FOR NEW FIELDS =====

    public void setHighTraffic(boolean highTraffic) {
        this.isHighTraffic = highTraffic;
        if (highTraffic) {
            recommendedTestCategories.add(TestCategory.LOAD);
            enabledStrategies.add(StrategyType.PERFORMANCE_LOAD);
            enabledScenarios.add(TestGenerationScenario.LOAD_TESTING_HEAVY);
        }
    }

    public void setComputeIntensive(boolean computeIntensive) {
        this.isComputeIntensive = computeIntensive;
        if (computeIntensive) {
            recommendedTestCategories.add(TestCategory.PERFORMANCE);
            enabledStrategies.add(StrategyType.PERFORMANCE_STRESS);
            enabledScenarios.add(TestGenerationScenario.STRESS_TESTING);
        }
    }

    public void setAsyncOperation(boolean asyncOperation) {
        this.isAsyncOperation = asyncOperation;
        if (asyncOperation) {
            recommendedTestCategories.add(TestCategory.INTEGRATION);
            enabledStrategies.add(StrategyType.ADVANCED_CONCURRENCY);
            enabledScenarios.add(TestGenerationScenario.CONCURRENCY_RACE_CONDITIONS);
        }
    }

    public void setHasBulkOperations(boolean hasBulkOperations) {
        this.hasBulkOperations = hasBulkOperations;
        if (hasBulkOperations) {
            recommendedTestCategories.add(TestCategory.PERFORMANCE);
            this.isComputeIntensive = true;
            enabledStrategies.add(StrategyType.PERFORMANCE_STRESS);
        }
    }

    public void addExternalApi(String apiName) {
        this.externalApis.add(apiName);
        this.hasExternalDependencies = true;
        this.recommendedTestCategories.add(TestCategory.INTEGRATION);
        this.enabledStrategies.add(StrategyType.FUNCTIONAL_COMPREHENSIVE);
    }

    public void addDatabaseTable(String tableName) {
        this.databaseTables.add(tableName);
    }

    public void addRelatedEndpoint(String endpointPath) {
        this.relatedEndpoints.add(endpointPath);
    }

    public void addExample(String example) {
        this.examples.add(example);
    }

    public void addExtension(String key, String value) {
        this.extensions.put(key, value);
    }

    public List<String> getExpectedStatusCodes() {
        // Return list of expected status codes from responses
        return new ArrayList<>(responses.keySet());
    }

    // ===== toString, equals, hashCode =====

    @Override
    public String toString() {
        return "EndpointInfo{" +
                "method='" + method + '\'' +
                ", path='" + path + '\'' +
                ", operationId='" + operationId + '\'' +
                ", parameters=" + parameters.size() +
                ", businessCriticality=" + businessCriticality +
                ", strategies=" + enabledStrategies.size() +
                ", scenarios=" + enabledScenarios.size() +
                ", estimatedTests=" + estimatedTestCount +
                ", testCategories=" + recommendedTestCategories.size() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EndpointInfo that = (EndpointInfo) o;
        return Objects.equals(method, that.method) &&
                Objects.equals(path, that.path) &&
                Objects.equals(operationId, that.operationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, path, operationId);
    }

    // ===== STATIC FACTORY METHODS =====

    /**
     * Basit GET endpoint oluşturur
     */
    public static EndpointInfo createSimpleGet(String path) {
        return EndpointInfo.builder()
                .withMethod("GET")
                .withPath(path)
                .withBusinessCriticality(BusinessCriticality.LOW)
                .build();
    }

    /**
     * CRUD endpoint oluşturur
     */
    public static EndpointInfo createCrudEndpoint(String path, String method, String resourceType) {
        Builder builder = EndpointInfo.builder()
                .withMethod(method)
                .withPath(path)
                .withBusinessCriticality(BusinessCriticality.NORMAL);

        // Method-specific settings
        switch (method.toUpperCase()) {
            case "POST":
                builder.withBusinessCriticality(BusinessCriticality.HIGH)
                        .withTestCategory(TestCategory.FUNCTIONAL);
                break;
            case "PUT":
            case "PATCH":
                builder.withBusinessCriticality(BusinessCriticality.HIGH)
                        .withTestCategory(TestCategory.FUNCTIONAL)
                        .withScenario(TestGenerationScenario.BOUNDARY_VALUES);
                break;
            case "DELETE":
                builder.withBusinessCriticality(BusinessCriticality.CRITICAL)
                        .withTestCategory(TestCategory.SECURITY);
                break;
        }

        EndpointInfo endpoint = builder.build();
        endpoint.setResourceType(resourceType);
        return endpoint;
    }

    /**
     * Güvenlik-critical endpoint oluşturur
     */
    public static EndpointInfo createSecureEndpoint(String path, String method) {
        return EndpointInfo.builder()
                .withMethod(method)
                .withPath(path)
                .withSecurity("bearerAuth")
                .withBusinessCriticality(BusinessCriticality.HIGH)
                .withSecurityRisk(SecurityRisk.UNAUTHORIZED_ACCESS)
                .withTestCategory(TestCategory.SECURITY)
                .build();
    }

    /**
     * High-performance endpoint oluşturur
     */
    public static EndpointInfo createHighPerformanceEndpoint(String path, String method) {
        EndpointInfo endpoint = EndpointInfo.builder()
                .withMethod(method)
                .withPath(path)
                .withPerformanceProfile(PerformanceProfile.FAST)
                .withTestCategory(TestCategory.PERFORMANCE)
                .withTestCategory(TestCategory.LOAD)
                .build();

        endpoint.setHighTraffic(true);
        return endpoint;
    }
}