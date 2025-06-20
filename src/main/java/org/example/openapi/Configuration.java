package org.example.openapi;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * ===== STANDARDIZED CONFIGURATION CLASS - TUTARLILIK REHBERİ UYUMLU =====
 *
 * Tutarlılık rehberine göre standardize edilmiş configuration class.
 * EndpointInfo, GeneratedTestCase ve ComprehensiveTestSuite ile uyumlu.
 * Standard enum'lar ve method signature'ları kullanır.
 *
 * @author Enterprise Solutions Team
 * @version 4.0.0-STANDARDIZED
 * @since 2025.1
 */
public class Configuration {

    // ===== STANDARD ENUMS - Tutarlılık Rehberi Uyumlu =====

    /**
     * Standard StrategyType enum - Tutarlılık rehberi uyumlu
     */
    public enum StrategyType {
        // FUNCTIONAL
        FUNCTIONAL_BASIC("Basic functional testing", 1, true, false, false, StrategyCategory.FUNCTIONAL),
        FUNCTIONAL_COMPREHENSIVE("Comprehensive functional testing", 2, true, false, false, StrategyCategory.FUNCTIONAL),
        FUNCTIONAL_BOUNDARY("Boundary condition testing", 2, true, false, true, StrategyCategory.FUNCTIONAL),
        FUNCTIONAL_EDGE_CASE("Edge case testing", 3, true, false, true, StrategyCategory.FUNCTIONAL),

        // SECURITY
        SECURITY_BASIC("Basic security validation", 2, false, true, false, StrategyCategory.SECURITY),
        SECURITY_INJECTION("SQL injection testing", 3, false, true, true, StrategyCategory.SECURITY),
        SECURITY_XSS("XSS testing", 3, false, true, true, StrategyCategory.SECURITY),
        SECURITY_OWASP_TOP10("OWASP Top 10 testing", 4, false, true, true, StrategyCategory.SECURITY),
        SECURITY_PENETRATION("Penetration testing", 5, false, true, true, StrategyCategory.SECURITY),

        // PERFORMANCE
        PERFORMANCE_BASIC("Basic performance testing", 2, false, false, true, StrategyCategory.PERFORMANCE),
        PERFORMANCE_LOAD("Load testing", 3, false, false, true, StrategyCategory.PERFORMANCE),
        PERFORMANCE_STRESS("Stress testing", 4, false, false, true, StrategyCategory.PERFORMANCE),

        // ADVANCED
        ADVANCED_AI_DRIVEN("AI-driven testing", 5, true, false, true, StrategyCategory.ADVANCED),
        ADVANCED_FUZZING("Fuzzing testing", 4, true, true, true, StrategyCategory.ADVANCED),
        ADVANCED_CONCURRENCY("Concurrency testing", 4, true, false, true, StrategyCategory.ADVANCED),
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

    /**
     * Standard TestGenerationScenario enum - Tutarlılık rehberi uyumlu
     */
    public enum TestGenerationScenario {
        // FUNCTIONAL
        HAPPY_PATH("Happy path testing", StrategyType.FUNCTIONAL_BASIC, 1, ScenarioCategory.FUNCTIONAL),
        ERROR_HANDLING("Error handling testing", StrategyType.FUNCTIONAL_COMPREHENSIVE, 2, ScenarioCategory.FUNCTIONAL),
        BOUNDARY_VALUES("Boundary value testing", StrategyType.FUNCTIONAL_BOUNDARY, 2, ScenarioCategory.FUNCTIONAL),
        EDGE_CASES("Edge case testing", StrategyType.FUNCTIONAL_EDGE_CASE, 3, ScenarioCategory.FUNCTIONAL),

        // SECURITY
        SQL_INJECTION_BASIC("Basic SQL injection testing", StrategyType.SECURITY_INJECTION, 3, ScenarioCategory.SECURITY),
        XSS_REFLECTED("Reflected XSS testing", StrategyType.SECURITY_XSS, 3, ScenarioCategory.SECURITY),
        XSS_STORED("Stored XSS testing", StrategyType.SECURITY_XSS, 4, ScenarioCategory.SECURITY),
        CSRF_PROTECTION("CSRF protection testing", StrategyType.SECURITY_OWASP_TOP10, 3, ScenarioCategory.SECURITY),
        AUTH_BYPASS("Authentication bypass testing", StrategyType.SECURITY_PENETRATION, 4, ScenarioCategory.SECURITY),

        // PERFORMANCE
        LOAD_TESTING_LIGHT("Light load testing", StrategyType.PERFORMANCE_LOAD, 2, ScenarioCategory.PERFORMANCE),
        LOAD_TESTING_HEAVY("Heavy load testing", StrategyType.PERFORMANCE_LOAD, 4, ScenarioCategory.PERFORMANCE),
        STRESS_TESTING("Stress testing", StrategyType.PERFORMANCE_STRESS, 4, ScenarioCategory.PERFORMANCE),

        // ADVANCED
        FUZZING_INPUT("Input fuzzing testing", StrategyType.ADVANCED_FUZZING, 4, ScenarioCategory.ADVANCED),
        CONCURRENCY_RACE_CONDITIONS("Race condition testing", StrategyType.ADVANCED_CONCURRENCY, 4, ScenarioCategory.ADVANCED),
        AI_DRIVEN_EXPLORATION("AI-driven exploration", StrategyType.ADVANCED_AI_DRIVEN, 5, ScenarioCategory.ADVANCED);

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
     * Standard supporting enums
     */
    public enum StrategyCategory {
        FUNCTIONAL, SECURITY, PERFORMANCE, ADVANCED, SPECIALIZED
    }

    public enum ScenarioCategory {
        FUNCTIONAL, SECURITY, PERFORMANCE, INTEGRATION, COMPLIANCE, ADVANCED
    }

    // ===== ENHANCED CONFIGURATION ENUMS =====

    public enum TestCoverageLevel {
        BASIC(20, StrategyType.FUNCTIONAL_BASIC),
        STANDARD(35, StrategyType.FUNCTIONAL_COMPREHENSIVE),
        COMPREHENSIVE(50, StrategyType.FUNCTIONAL_BOUNDARY),
        EXHAUSTIVE(100, StrategyType.ADVANCED_AI_DRIVEN);

        private final int maxTestsPerEndpoint;
        private final StrategyType defaultStrategy;

        TestCoverageLevel(int maxTestsPerEndpoint, StrategyType defaultStrategy) {
            this.maxTestsPerEndpoint = maxTestsPerEndpoint;
            this.defaultStrategy = defaultStrategy;
        }

        public int getMaxTestsPerEndpoint() { return maxTestsPerEndpoint; }
        public StrategyType getDefaultStrategy() { return defaultStrategy; }
    }

    public enum TestDataStrategy {
        MINIMAL(StrategyType.FUNCTIONAL_BASIC, 5),
        REALISTIC(StrategyType.FUNCTIONAL_COMPREHENSIVE, 15),
        COMPREHENSIVE(StrategyType.FUNCTIONAL_BOUNDARY, 30),
        FUZZING(StrategyType.ADVANCED_FUZZING, 50),
        COMBINATORIAL(StrategyType.ADVANCED_AI_DRIVEN, 100);

        private final StrategyType associatedStrategy;
        private final int defaultVariations;

        TestDataStrategy(StrategyType associatedStrategy, int defaultVariations) {
            this.associatedStrategy = associatedStrategy;
            this.defaultVariations = defaultVariations;
        }

        public StrategyType getAssociatedStrategy() { return associatedStrategy; }
        public int getDefaultVariations() { return defaultVariations; }
    }

    public enum TestFramework {
        JUNIT4("JUnit 4", "junit:junit:4.13.2"),
        JUNIT5("JUnit 5", "org.junit.jupiter:junit-jupiter:5.8.2"),
        TESTNG("TestNG", "org.testng:testng:7.4.0"),
        SPOCK("Spock Framework", "org.spockframework:spock-core:2.0-groovy-3.0");

        private final String displayName;
        private final String dependency;

        TestFramework(String displayName, String dependency) {
            this.displayName = displayName;
            this.dependency = dependency;
        }

        public String getDisplayName() { return displayName; }
        public String getDependency() { return dependency; }
    }

    public enum MockFramework {
        MOCKITO("Mockito", "org.mockito:mockito-core"),
        EASYMOCK("EasyMock", "org.easymock:easymock"),
        POWERMOCK("PowerMock", "org.powermock:powermock-module-junit4"),
        WIREMOCK("WireMock", "com.github.tomakehurst:wiremock-jre8");

        private final String displayName;
        private final String dependency;

        MockFramework(String displayName, String dependency) {
            this.displayName = displayName;
            this.dependency = dependency;
        }

        public String getDisplayName() { return displayName; }
        public String getDependency() { return dependency; }
    }

    public enum AssertionLibrary {
        JUNIT("JUnit Assertions", "Built-in"),
        HAMCREST("Hamcrest", "org.hamcrest:hamcrest"),
        ASSERTJ("AssertJ", "org.assertj:assertj-core"),
        TRUTH("Google Truth", "com.google.truth:truth");

        private final String displayName;
        private final String dependency;

        AssertionLibrary(String displayName, String dependency) {
            this.displayName = displayName;
            this.dependency = dependency;
        }

        public String getDisplayName() { return displayName; }
        public String getDependency() { return dependency; }
    }

    public enum ReportFormat {
        HTML("HTML Report"),
        JSON("JSON Report"),
        XML("XML Report"),
        PDF("PDF Report"),
        CONSOLE("Console Output");

        private final String displayName;

        ReportFormat(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() { return displayName; }
    }

    // ===== CORE CONFIGURATION FIELDS =====

    // Temel Dosya Ayarları
    private String inputFile;
    private String outputFile;
    private String testClassName;
    private String packageName = "org.example.generated.tests";
    private String testOutputDir = "src/test/java";

    // API Ayarları
    private String apiKey;
    private String apiKeyEnvVar;
    private String model;
    private int maxTokens;
    private int maxRetries;
    private int timeoutSeconds;
    private String baseUri;

    // Performance Ayarları
    private int threadPoolSize;
    private boolean verbose;
    private boolean useFallbackOnError;
    private int batchSize = 10;
    private boolean enableParallelExecution = true;

    // ===== STANDARD TEST CONFIGURATION =====

    // Test Coverage Optimization
    private TestCoverageLevel coverageLevel = TestCoverageLevel.COMPREHENSIVE;
    private int maxTestsPerEndpoint = 50;
    private boolean enableIntelligentTestSelection = true;
    private boolean prioritizeHighRiskEndpoints = true;
    private double coverageThreshold = 0.95;

    // Standard Strategy Configuration
    private Set<StrategyType> enabledStrategies = new HashSet<>();
    private Set<TestGenerationScenario> enabledScenarios = new HashSet<>();
    private StrategyType defaultStrategy = StrategyType.FUNCTIONAL_COMPREHENSIVE;

    // ===== STANDARD TEST TYPES - Scenario-based =====

    // Functional Test Scenarios
    private boolean generateHappyPathTests = true;
    private boolean generateBoundaryTests = true;
    private boolean includeNegativeTests = true;
    private boolean generateErrorTests = true;
    private boolean generateEdgeCaseTests = true;

    // Security Test Scenarios
    private boolean includeSecurityTests = false;
    private boolean generateSqlInjectionTests = false;
    private boolean generateXssTests = false;
    private boolean generateCsrfTests = false;
    private boolean generateAuthenticationTests = true;
    private boolean generateAuthorizationTests = true;

    // Performance Test Scenarios
    private boolean includePerformanceTests = false;
    private boolean generateLoadTests = false;
    private boolean generateStressTests = false;

    // Advanced Test Scenarios
    private boolean generateDataIntegrityTests = true;
    private boolean generateConcurrencyTests = false;
    private boolean generateRegressionTests = true;
    private boolean generateFuzzingTests = false;

    // Validation Test Scenarios
    private boolean generateSchemaValidationTests = true;
    private boolean generateFormatValidationTests = true;
    private boolean generateConstraintValidationTests = true;
    private boolean generateBusinessRuleTests = true;
    private boolean generateDataConsistencyTests = true;

    // HTTP Protocol Test Scenarios
    private boolean generateHttpMethodTests = true;
    private boolean generateHttpHeaderTests = true;
    private boolean generateHttpStatusTests = true;
    private boolean generateContentTypeTests = true;

    // Data-driven Test Scenarios
    private boolean generateParameterCombinationTests = true;

    // ===== ENHANCED FEATURES =====

    // Test Data Generation
    private TestDataStrategy testDataStrategy = TestDataStrategy.COMPREHENSIVE;
    private boolean generateRealisticTestData = true;
    private boolean useExternalTestDataSources = false;
    private boolean generateSyntheticData = true;
    private int maxTestDataVariations = 20;

    // Advanced Features
    private boolean generateApiTests = true;
    private boolean generateModelTests = true;
    private boolean generateClientTests = false;
    private boolean enableAsyncTesting = true;
    private boolean enableRetryOnFailure = true;
    private boolean enableDetailedReporting = true;
    private boolean enableTestDataGeneration = true;
    private boolean enableConstraintValidation = true;
    private boolean enableDependencyAnalysis = true;
    private boolean enableComplexityAnalysis = true;

    // Enterprise Features
    private boolean enableTestOrchestration = true;
    private boolean enableTestPrioritization = true;
    private boolean enableAdaptiveTestGeneration = true;
    private boolean enableTestOptimization = true;
    private boolean enableCrossEndpointTesting = true;
    private boolean enableStatefulTesting = false;
    private boolean enableTestMaintenance = true;

    // OpenAI Ayarları
    private double temperature = 0.1;
    private double topP = 0.9;
    private int presencePenalty = 0;
    private int frequencyPenalty = 0;

    // Test Framework Ayarları
    private TestFramework testFramework = TestFramework.JUNIT5;
    private MockFramework mockFramework = MockFramework.MOCKITO;
    private AssertionLibrary assertionLibrary = AssertionLibrary.ASSERTJ;
    private boolean generateTestUtilities = true;
    private boolean generateTestFixtures = true;
    private boolean generateTestHelpers = true;

    // Report ve Documentation
    private boolean generateTestReport = true;
    private boolean generateCoverageReport = true;
    private boolean generateDocumentation = true;
    private boolean generateMetrics = true;
    private ReportFormat reportFormat = ReportFormat.HTML;

    // Custom Patterns ve Rules
    private Map<String, TestPattern> customTestPatterns = new ConcurrentHashMap<>();
    private List<ValidationRule> customValidationRules = new ArrayList<>();
    private Set<String> excludedEndpoints = new HashSet<>();
    private Set<String> priorityEndpoints = new HashSet<>();

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

    // ===== CONSTRUCTORS =====

    public Configuration() {
        initializeDefaults();
        initializeStandardStrategies();
    }

    public Configuration(String inputFile, String outputFile) {
        this();
        this.inputFile = inputFile;
        this.outputFile = outputFile;
    }

    /**
     * Standard validation method
     */
    private void initializeDefaults() {
        this.verbose = false;
        this.threadPoolSize = Runtime.getRuntime().availableProcessors();
        this.maxRetries = 3;
        this.timeoutSeconds = 120;
        this.maxTokens = 4096;
        this.model = "gpt-4";
        this.apiKeyEnvVar = "OPENAI_API_KEY";
        this.testClassName = "GeneratedApiTests";
        this.baseUri = "https://api.example.com";
        this.useFallbackOnError = true;
        this.batchSize = Math.max(1, threadPoolSize / 2);
    }

    /**
     * Standard method: Initialize standard strategies based on coverage level
     */
    private void initializeStandardStrategies() {
        enabledStrategies.clear();
        enabledScenarios.clear();

        // Add strategies based on coverage level
        switch (coverageLevel) {
            case BASIC:
                enabledStrategies.add(StrategyType.FUNCTIONAL_BASIC);
                enabledScenarios.add(TestGenerationScenario.HAPPY_PATH);
                break;
            case STANDARD:
                enabledStrategies.add(StrategyType.FUNCTIONAL_BASIC);
                enabledStrategies.add(StrategyType.FUNCTIONAL_COMPREHENSIVE);
                enabledScenarios.add(TestGenerationScenario.HAPPY_PATH);
                enabledScenarios.add(TestGenerationScenario.ERROR_HANDLING);
                break;
            case COMPREHENSIVE:
                enabledStrategies.add(StrategyType.FUNCTIONAL_BASIC);
                enabledStrategies.add(StrategyType.FUNCTIONAL_COMPREHENSIVE);
                enabledStrategies.add(StrategyType.FUNCTIONAL_BOUNDARY);
                enabledScenarios.add(TestGenerationScenario.HAPPY_PATH);
                enabledScenarios.add(TestGenerationScenario.ERROR_HANDLING);
                enabledScenarios.add(TestGenerationScenario.BOUNDARY_VALUES);
                break;
            case EXHAUSTIVE:
                enabledStrategies.addAll(Arrays.asList(StrategyType.values()));
                enabledScenarios.addAll(Arrays.asList(TestGenerationScenario.values()));
                break;
        }
    }

    // ===== BUILDER PATTERN - STANDARDIZED =====

    /**
     * Standard factory method
     */
    public static Configuration create() {
        return new Configuration();
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
        private Configuration config = new Configuration();

        public Builder withInputFile(String inputFile) {
            config.inputFile = inputFile;
            return this;
        }

        public Builder withOutputFile(String outputFile) {
            config.outputFile = outputFile;
            return this;
        }

        public Builder withTestClassName(String testClassName) {
            config.testClassName = testClassName;
            return this;
        }

        public Builder withPackageName(String packageName) {
            config.packageName = packageName;
            return this;
        }

        public Builder withCoverageLevel(TestCoverageLevel level) {
            config.coverageLevel = level;
            config.maxTestsPerEndpoint = level.getMaxTestsPerEndpoint();
            config.defaultStrategy = level.getDefaultStrategy();
            config.initializeStandardStrategies();
            return this;
        }

        public Builder withTestDataStrategy(TestDataStrategy strategy) {
            config.testDataStrategy = strategy;
            config.maxTestDataVariations = strategy.getDefaultVariations();
            config.enabledStrategies.add(strategy.getAssociatedStrategy());
            return this;
        }

        public Builder withStrategyType(StrategyType strategyType) {
            config.enabledStrategies.add(strategyType);
            config.defaultStrategy = strategyType;
            return this;
        }

        public Builder withScenario(TestGenerationScenario scenario) {
            config.enabledScenarios.add(scenario);
            config.enabledStrategies.add(scenario.getRecommendedStrategy());
            return this;
        }

        public Builder withTestFramework(TestFramework framework) {
            config.testFramework = framework;
            return this;
        }

        public Builder withMockFramework(MockFramework framework) {
            config.mockFramework = framework;
            return this;
        }

        public Builder withAssertionLibrary(AssertionLibrary library) {
            config.assertionLibrary = library;
            return this;
        }

        public Builder withMaxTestsPerEndpoint(int maxTests) {
            config.maxTestsPerEndpoint = maxTests;
            return this;
        }

        public Builder withCoverageThreshold(double threshold) {
            config.coverageThreshold = Math.max(0.0, Math.min(1.0, threshold));
            return this;
        }

        public Builder withThreadPoolSize(int size) {
            config.threadPoolSize = Math.max(1, size);
            return this;
        }

        public Builder withVerbose(boolean verbose) {
            config.verbose = verbose;
            return this;
        }

        public Builder enableAllTestTypes() {
            config.generateHappyPathTests = true;
            config.generateBoundaryTests = true;
            config.includeNegativeTests = true;
            config.generateErrorTests = true;
            config.generateEdgeCaseTests = true;
            config.generateDataIntegrityTests = true;
            config.generateSchemaValidationTests = true;
            config.generateFormatValidationTests = true;
            config.generateConstraintValidationTests = true;
            config.generateBusinessRuleTests = true;
            config.generateHttpMethodTests = true;
            config.generateHttpHeaderTests = true;
            config.generateHttpStatusTests = true;
            config.generateContentTypeTests = true;
            config.generateAuthenticationTests = true;
            config.generateParameterCombinationTests = true;
            return this;
        }

        public Builder enableAdvancedTestTypes() {
            enableAllTestTypes();
            config.includeSecurityTests = true;
            config.includePerformanceTests = true;
            config.generateRegressionTests = true;
            config.generateConcurrencyTests = true;
            config.generateFuzzingTests = true;
            config.enableCrossEndpointTesting = true;
            config.enableAdaptiveTestGeneration = true;
            return this;
        }

        public Builder enableSecurityTestSuite() {
            config.includeSecurityTests = true;
            config.generateSqlInjectionTests = true;
            config.generateXssTests = true;
            config.generateCsrfTests = true;
            config.generateAuthenticationTests = true;
            config.generateAuthorizationTests = true;
            config.enabledStrategies.add(StrategyType.SECURITY_OWASP_TOP10);
            config.enabledScenarios.add(TestGenerationScenario.SQL_INJECTION_BASIC);
            config.enabledScenarios.add(TestGenerationScenario.XSS_REFLECTED);
            config.enabledScenarios.add(TestGenerationScenario.CSRF_PROTECTION);
            return this;
        }

        public Builder enablePerformanceTestSuite() {
            config.includePerformanceTests = true;
            config.generateLoadTests = true;
            config.generateStressTests = true;
            config.enabledStrategies.add(StrategyType.PERFORMANCE_LOAD);
            config.enabledStrategies.add(StrategyType.PERFORMANCE_STRESS);
            config.enabledScenarios.add(TestGenerationScenario.LOAD_TESTING_LIGHT);
            config.enabledScenarios.add(TestGenerationScenario.STRESS_TESTING);
            return this;
        }

        public Configuration build() {
            validateAndEnhanceConfiguration(config);
            return config;
        }
    }

    // ===== STANDARD UTILITY METHODS =====

    /**
     * Standard validation method
     */
    private static void validateAndEnhanceConfiguration(Configuration config) {
        if (config.inputFile == null || config.inputFile.trim().isEmpty()) {
            throw new IllegalArgumentException("Input file cannot be null or empty");
        }

        if (config.outputFile == null || config.outputFile.trim().isEmpty()) {
            throw new IllegalArgumentException("Output file cannot be null or empty");
        }

        if (config.maxTestsPerEndpoint <= 0) {
            throw new IllegalArgumentException("Max tests per endpoint must be positive");
        }

        if (config.coverageThreshold < 0.0 || config.coverageThreshold > 1.0) {
            throw new IllegalArgumentException("Coverage threshold must be between 0.0 and 1.0");
        }

        // Ensure we have at least basic strategies enabled
        if (config.enabledStrategies.isEmpty()) {
            config.enabledStrategies.add(StrategyType.FUNCTIONAL_BASIC);
        }

        if (config.enabledScenarios.isEmpty()) {
            config.enabledScenarios.add(TestGenerationScenario.HAPPY_PATH);
        }
    }

    /**
     * Standard method: Generate advanced cache key for endpoint
     */
    private String generateAdvancedCacheKey(EndpointInfo endpoint) {
        if (endpoint == null) {
            return "unknown_endpoint_" + System.currentTimeMillis();
        }

        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(endpoint.getMethod() != null ? endpoint.getMethod().toUpperCase() : "UNKNOWN");
        keyBuilder.append("_");
        keyBuilder.append(endpoint.getPath() != null ?
                endpoint.getPath().replaceAll("[^a-zA-Z0-9]", "_") : "unknown_path");
        keyBuilder.append("_");
        keyBuilder.append(endpoint.getOperationId() != null ?
                endpoint.getOperationId() : "op_" + System.currentTimeMillis());

        return keyBuilder.toString();
    }

    /**
     * Standard method: Generate advanced execution ID
     */
    private static String generateAdvancedExecutionId() {
        return "config_" + System.currentTimeMillis() + "_" +
                Thread.currentThread().getId() + "_" +
                (int)(Math.random() * 10000);
    }

    // ===== FLUENT API METHODS =====

    // Temel ayarlar
    public Configuration inputFile(String inputFile) {
        this.inputFile = inputFile;
        return this;
    }

    public Configuration outputFile(String outputFile) {
        this.outputFile = outputFile;
        return this;
    }

    public Configuration testClassName(String testClassName) {
        this.testClassName = testClassName;
        return this;
    }

    public Configuration packageName(String packageName) {
        this.packageName = packageName;
        return this;
    }

    // Coverage ayarları
    public Configuration coverageLevel(TestCoverageLevel level) {
        this.coverageLevel = level;
        this.maxTestsPerEndpoint = level.getMaxTestsPerEndpoint();
        this.defaultStrategy = level.getDefaultStrategy();
        initializeStandardStrategies();
        return this;
    }

    public Configuration maxTestsPerEndpoint(int maxTests) {
        this.maxTestsPerEndpoint = maxTests;
        return this;
    }

    public Configuration coverageThreshold(double threshold) {
        this.coverageThreshold = Math.max(0.0, Math.min(1.0, threshold));
        return this;
    }

    // Strategy configuration
    public Configuration addStrategy(StrategyType strategy) {
        this.enabledStrategies.add(strategy);
        return this;
    }

    public Configuration addScenario(TestGenerationScenario scenario) {
        this.enabledScenarios.add(scenario);
        this.enabledStrategies.add(scenario.getRecommendedStrategy());
        return this;
    }

    public Configuration setDefaultStrategy(StrategyType strategy) {
        this.defaultStrategy = strategy;
        this.enabledStrategies.add(strategy);
        return this;
    }

    // Test türleri - Toplu ayarlama
    public Configuration enableAllTestTypes() {
        return this
                .generateHappyPathTests(true)
                .generateBoundaryTests(true)
                .includeNegativeTests(true)
                .generateErrorTests(true)
                .generateEdgeCaseTests(true)
                .generateDataIntegrityTests(true)
                .generateSchemaValidationTests(true)
                .generateFormatValidationTests(true)
                .generateConstraintValidationTests(true)
                .generateBusinessRuleTests(true)
                .generateHttpMethodTests(true)
                .generateHttpHeaderTests(true)
                .generateHttpStatusTests(true)
                .generateContentTypeTests(true)
                .generateAuthenticationTests(true)
                .generateParameterCombinationTests(true);
    }

    public Configuration enableAdvancedTestTypes() {
        return enableAllTestTypes()
                .includeSecurityTests(true)
                .includePerformanceTests(true)
                .generateRegressionTests(true)
                .generateConcurrencyTests(true)
                .generateFuzzingTests(true)
                .enableCrossEndpointTesting(true)
                .enableAdaptiveTestGeneration(true);
    }

    public Configuration enableSecurityTestSuite() {
        return this
                .includeSecurityTests(true)
                .generateSqlInjectionTests(true)
                .generateXssTests(true)
                .generateCsrfTests(true)
                .generateAuthenticationTests(true)
                .generateAuthorizationTests(true)
                .addStrategy(StrategyType.SECURITY_OWASP_TOP10)
                .addScenario(TestGenerationScenario.SQL_INJECTION_BASIC)
                .addScenario(TestGenerationScenario.XSS_REFLECTED)
                .addScenario(TestGenerationScenario.CSRF_PROTECTION);
    }

    public Configuration enablePerformanceTestSuite() {
        return this
                .includePerformanceTests(true)
                .generateLoadTests(true)
                .generateStressTests(true)
                .addStrategy(StrategyType.PERFORMANCE_LOAD)
                .addStrategy(StrategyType.PERFORMANCE_STRESS)
                .addScenario(TestGenerationScenario.LOAD_TESTING_LIGHT)
                .addScenario(TestGenerationScenario.STRESS_TESTING);
    }

    // Test Framework ayarları
    public Configuration testFramework(TestFramework framework) {
        this.testFramework = framework;
        return this;
    }

    public Configuration mockFramework(MockFramework framework) {
        this.mockFramework = framework;
        return this;
    }

    public Configuration assertionLibrary(AssertionLibrary library) {
        this.assertionLibrary = library;
        return this;
    }

    // Test data strategy
    public Configuration testDataStrategy(TestDataStrategy strategy) {
        this.testDataStrategy = strategy;
        this.maxTestDataVariations = strategy.getDefaultVariations();
        this.enabledStrategies.add(strategy.getAssociatedStrategy());

        // Strategy'ye göre otomatik ayarlama
        switch (strategy) {
            case MINIMAL:
                this.generateRealisticTestData = false;
                break;
            case REALISTIC:
                this.generateRealisticTestData = true;
                break;
            case COMPREHENSIVE:
                this.generateRealisticTestData = true;
                this.generateSyntheticData = true;
                break;
            case FUZZING:
                this.generateFuzzingTests = true;
                this.enabledScenarios.add(TestGenerationScenario.FUZZING_INPUT);
                break;
            case COMBINATORIAL:
                this.generateParameterCombinationTests = true;
                break;
        }
        return this;
    }

    // Özel pattern ekleme
    public Configuration addCustomTestPattern(String name, TestPattern pattern) {
        this.customTestPatterns.put(name, pattern);
        return this;
    }

    public Configuration addValidationRule(ValidationRule rule) {
        this.customValidationRules.add(rule);
        return this;
    }

    public Configuration excludeEndpoint(String endpoint) {
        this.excludedEndpoints.add(endpoint);
        return this;
    }

    public Configuration prioritizeEndpoint(String endpoint) {
        this.priorityEndpoints.add(endpoint);
        return this;
    }

    // ===== FLUENT API için tüm boolean metodlar =====

    public Configuration generateHappyPathTests(boolean value) {
        this.generateHappyPathTests = value;
        if (value) addScenario(TestGenerationScenario.HAPPY_PATH);
        return this;
    }

    public Configuration generateBoundaryTests(boolean value) {
        this.generateBoundaryTests = value;
        if (value) addScenario(TestGenerationScenario.BOUNDARY_VALUES);
        return this;
    }

    public Configuration includeNegativeTests(boolean value) {
        this.includeNegativeTests = value;
        return this;
    }

    public Configuration generateErrorTests(boolean value) {
        this.generateErrorTests = value;
        if (value) addScenario(TestGenerationScenario.ERROR_HANDLING);
        return this;
    }

    public Configuration generateEdgeCaseTests(boolean value) {
        this.generateEdgeCaseTests = value;
        if (value) addScenario(TestGenerationScenario.EDGE_CASES);
        return this;
    }

    public Configuration generateDataIntegrityTests(boolean value) {
        this.generateDataIntegrityTests = value;
        return this;
    }

    public Configuration generateConcurrencyTests(boolean value) {
        this.generateConcurrencyTests = value;
        if (value) addScenario(TestGenerationScenario.CONCURRENCY_RACE_CONDITIONS);
        return this;
    }

    public Configuration generateRegressionTests(boolean value) {
        this.generateRegressionTests = value;
        return this;
    }

    public Configuration generateSchemaValidationTests(boolean value) {
        this.generateSchemaValidationTests = value;
        return this;
    }

    public Configuration generateFormatValidationTests(boolean value) {
        this.generateFormatValidationTests = value;
        return this;
    }

    public Configuration generateConstraintValidationTests(boolean value) {
        this.generateConstraintValidationTests = value;
        return this;
    }

    public Configuration generateBusinessRuleTests(boolean value) {
        this.generateBusinessRuleTests = value;
        return this;
    }

    public Configuration generateHttpMethodTests(boolean value) {
        this.generateHttpMethodTests = value;
        return this;
    }

    public Configuration generateHttpHeaderTests(boolean value) {
        this.generateHttpHeaderTests = value;
        return this;
    }

    public Configuration generateHttpStatusTests(boolean value) {
        this.generateHttpStatusTests = value;
        return this;
    }

    public Configuration generateContentTypeTests(boolean value) {
        this.generateContentTypeTests = value;
        return this;
    }

    public Configuration generateAuthenticationTests(boolean value) {
        this.generateAuthenticationTests = value;
        if (value) addScenario(TestGenerationScenario.AUTH_BYPASS);
        return this;
    }

    public Configuration generateAuthorizationTests(boolean value) {
        this.generateAuthorizationTests = value;
        return this;
    }

    public Configuration generateParameterCombinationTests(boolean value) {
        this.generateParameterCombinationTests = value;
        return this;
    }

    public Configuration generateFuzzingTests(boolean value) {
        this.generateFuzzingTests = value;
        if (value) addScenario(TestGenerationScenario.FUZZING_INPUT);
        return this;
    }

    public Configuration generateSqlInjectionTests(boolean value) {
        this.generateSqlInjectionTests = value;
        if (value) addScenario(TestGenerationScenario.SQL_INJECTION_BASIC);
        return this;
    }

    public Configuration generateXssTests(boolean value) {
        this.generateXssTests = value;
        if (value) {
            addScenario(TestGenerationScenario.XSS_REFLECTED);
            addScenario(TestGenerationScenario.XSS_STORED);
        }
        return this;
    }

    public Configuration generateCsrfTests(boolean value) {
        this.generateCsrfTests = value;
        if (value) addScenario(TestGenerationScenario.CSRF_PROTECTION);
        return this;
    }

    public Configuration includeSecurityTests(boolean value) {
        this.includeSecurityTests = value;
        if (value) addStrategy(StrategyType.SECURITY_BASIC);
        return this;
    }

    public Configuration includePerformanceTests(boolean value) {
        this.includePerformanceTests = value;
        if (value) addStrategy(StrategyType.PERFORMANCE_BASIC);
        return this;
    }

    public Configuration generateLoadTests(boolean value) {
        this.generateLoadTests = value;
        if (value) addScenario(TestGenerationScenario.LOAD_TESTING_LIGHT);
        return this;
    }

    public Configuration generateStressTests(boolean value) {
        this.generateStressTests = value;
        if (value) addScenario(TestGenerationScenario.STRESS_TESTING);
        return this;
    }

    public Configuration enableCrossEndpointTesting(boolean value) {
        this.enableCrossEndpointTesting = value;
        return this;
    }

    public Configuration enableAdaptiveTestGeneration(boolean value) {
        this.enableAdaptiveTestGeneration = value;
        if (value) addStrategy(StrategyType.ADVANCED_AI_DRIVEN);
        return this;
    }

    public Configuration enableTestOptimization(boolean value) {
        this.enableTestOptimization = value;
        return this;
    }

    // Additional fluent methods
    public Configuration verbose(boolean verbose) {
        this.verbose = verbose;
        return this;
    }

    public Configuration threadPoolSize(int size) {
        this.threadPoolSize = Math.max(1, size);
        return this;
    }

    public Configuration apiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    public Configuration model(String model) {
        this.model = model;
        return this;
    }

    public Configuration maxTokens(int maxTokens) {
        this.maxTokens = maxTokens;
        return this;
    }

    // ===== STANDARD VALIDATION METHODS =====

    /**
     * Standard validation method - Enhanced
     */
    public ValidationResult validateComprehensive() {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        List<String> suggestions = new ArrayList<>();

        // Temel validasyon
        if (inputFile == null || inputFile.trim().isEmpty()) {
            errors.add("Input file zorunludur");
        } else {
            File file = new File(inputFile);
            if (!file.exists()) {
                errors.add("Input file bulunamadı: " + inputFile);
            } else if (!file.canRead()) {
                errors.add("Input file okunamıyor: " + inputFile);
            } else if (file.length() == 0) {
                warnings.add("Input file boş görünüyor");
            }
        }

        if (outputFile == null || outputFile.trim().isEmpty()) {
            errors.add("Output file zorunludur");
        }

        // API key kontrolü
        String actualApiKey = apiKey != null ? apiKey : System.getenv(apiKeyEnvVar);
        if (actualApiKey == null || actualApiKey.length() < 20) {
            warnings.add("API key eksik veya çok kısa");
        }

        // Performance validation
        int availableCpus = Runtime.getRuntime().availableProcessors();
        if (threadPoolSize > availableCpus * 4) {
            warnings.add(String.format("Thread pool çok büyük (%d), CPU sayısı: %d", threadPoolSize, availableCpus));
        }

        if (maxTestsPerEndpoint > 200) {
            warnings.add("Endpoint başına test sayısı çok yüksek: " + maxTestsPerEndpoint);
        }

        // Strategy validation
        if (enabledStrategies.isEmpty()) {
            errors.add("En az bir strateji aktif olmalı");
        }

        if (enabledScenarios.isEmpty()) {
            errors.add("En az bir senaryo aktif olmalı");
        }

        // Test configuration validation
        int enabledTestTypes = countEnabledTestTypes();
        if (enabledTestTypes == 0) {
            errors.add("En az bir test türü aktif olmalı");
        } else if (enabledTestTypes > 20) {
            warnings.add("Çok fazla test türü aktif (" + enabledTestTypes + "), uzun sürebilir");
        }

        // Coverage validation
        if (coverageThreshold > 1.0 || coverageThreshold < 0.0) {
            errors.add("Coverage threshold 0.0-1.0 arasında olmalı: " + coverageThreshold);
        }

        // Strategy-scenario consistency validation
        for (TestGenerationScenario scenario : enabledScenarios) {
            if (!enabledStrategies.contains(scenario.getRecommendedStrategy())) {
                suggestions.add(String.format("Senaryo '%s' için önerilen strateji '%s' aktif değil",
                        scenario.name(), scenario.getRecommendedStrategy().name()));
            }
        }

        // Suggestions
        if (!enableIntelligentTestSelection && enabledTestTypes > 10) {
            suggestions.add("Çok sayıda test türü için 'intelligentTestSelection' önerilir");
        }

        if (generateFuzzingTests && !includeSecurityTests) {
            suggestions.add("Fuzzing testleri için security testleri de önerilir");
        }

        if (includePerformanceTests && threadPoolSize < 2) {
            suggestions.add("Performance testleri için daha fazla thread önerilir");
        }

        if (enabledStrategies.contains(StrategyType.ADVANCED_AI_DRIVEN) && maxTokens < 2048) {
            suggestions.add("AI-driven testing için daha yüksek token limiti önerilir");
        }

        return new ValidationResult(errors.isEmpty(), errors, warnings, suggestions);
    }

    private int countEnabledTestTypes() {
        int count = 0;
        if (generateHappyPathTests) count++;
        if (generateBoundaryTests) count++;
        if (includeNegativeTests) count++;
        if (generateErrorTests) count++;
        if (generateEdgeCaseTests) count++;
        if (generateDataIntegrityTests) count++;
        if (generateConcurrencyTests) count++;
        if (generateRegressionTests) count++;
        if (generateSchemaValidationTests) count++;
        if (generateFormatValidationTests) count++;
        if (generateConstraintValidationTests) count++;
        if (generateBusinessRuleTests) count++;
        if (generateHttpMethodTests) count++;
        if (generateHttpHeaderTests) count++;
        if (generateHttpStatusTests) count++;
        if (generateContentTypeTests) count++;
        if (generateAuthenticationTests) count++;
        if (generateAuthorizationTests) count++;
        if (generateParameterCombinationTests) count++;
        if (generateFuzzingTests) count++;
        if (generateSqlInjectionTests) count++;
        if (generateXssTests) count++;
        if (generateCsrfTests) count++;
        if (includeSecurityTests) count++;
        if (includePerformanceTests) count++;
        if (generateLoadTests) count++;
        if (generateStressTests) count++;
        return count;
    }

    // ===== STANDARD GETTERS - Tutarlılık Rehberi Uyumlu =====

    // Temel getters
    public String getInputFile() { return inputFile; }
    public String getOutputFile() { return outputFile; }
    public String getTestClassName() { return testClassName; }
    public String getPackageName() { return packageName; }
    public String getTestOutputDir() { return testOutputDir; }

    // Standard strategy getters
    public Set<StrategyType> getEnabledStrategies() { return new HashSet<>(enabledStrategies); }
    public Set<TestGenerationScenario> getEnabledScenarios() { return new HashSet<>(enabledScenarios); }
    public StrategyType getDefaultStrategy() { return defaultStrategy; }

    // Coverage getters
    public TestCoverageLevel getCoverageLevel() { return coverageLevel; }
    public int getMaxTestsPerEndpoint() { return maxTestsPerEndpoint; }
    public double getCoverageThreshold() { return coverageThreshold; }
    public boolean isEnableIntelligentTestSelection() { return enableIntelligentTestSelection; }
    public boolean isPrioritizeHighRiskEndpoints() { return prioritizeHighRiskEndpoints; }

    // Test türü getters
    public boolean isGenerateHappyPathTests() { return generateHappyPathTests; }
    public boolean isGenerateErrorTests() { return generateErrorTests; }
    public boolean isGenerateEdgeCaseTests() { return generateEdgeCaseTests; }
    public boolean isGenerateDataIntegrityTests() { return generateDataIntegrityTests; }
    public boolean isGenerateConcurrencyTests() { return generateConcurrencyTests; }
    public boolean isGenerateRegressionTests() { return generateRegressionTests; }
    public boolean isGenerateSchemaValidationTests() { return generateSchemaValidationTests; }
    public boolean isGenerateFormatValidationTests() { return generateFormatValidationTests; }
    public boolean isGenerateConstraintValidationTests() { return generateConstraintValidationTests; }
    public boolean isGenerateBusinessRuleTests() { return generateBusinessRuleTests; }
    public boolean isGenerateHttpMethodTests() { return generateHttpMethodTests; }
    public boolean isGenerateHttpHeaderTests() { return generateHttpHeaderTests; }
    public boolean isGenerateHttpStatusTests() { return generateHttpStatusTests; }
    public boolean isGenerateContentTypeTests() { return generateContentTypeTests; }
    public boolean isGenerateAuthenticationTests() { return generateAuthenticationTests; }
    public boolean isGenerateAuthorizationTests() { return generateAuthorizationTests; }
    public boolean isGenerateParameterCombinationTests() { return generateParameterCombinationTests; }
    public boolean isGenerateFuzzingTests() { return generateFuzzingTests; }
    public boolean isGenerateSqlInjectionTests() { return generateSqlInjectionTests; }
    public boolean isGenerateXssTests() { return generateXssTests; }
    public boolean isGenerateCsrfTests() { return generateCsrfTests; }
    public boolean isIncludeSecurityTests() { return includeSecurityTests; }
    public boolean isIncludePerformanceTests() { return includePerformanceTests; }
    public boolean isGenerateLoadTests() { return generateLoadTests; }
    public boolean isGenerateStressTests() { return generateStressTests; }

    // Framework getters
    public TestFramework getTestFramework() { return testFramework; }
    public MockFramework getMockFramework() { return mockFramework; }
    public AssertionLibrary getAssertionLibrary() { return assertionLibrary; }

    // Test data getters
    public TestDataStrategy getTestDataStrategy() { return testDataStrategy; }
    public int getMaxTestDataVariations() { return maxTestDataVariations; }
    public boolean isGenerateRealisticTestData() { return generateRealisticTestData; }
    public boolean isGenerateSyntheticData() { return generateSyntheticData; }

    // Custom patterns
    public Map<String, TestPattern> getCustomTestPatterns() { return new HashMap<>(customTestPatterns); }
    public List<ValidationRule> getCustomValidationRules() { return new ArrayList<>(customValidationRules); }
    public Set<String> getExcludedEndpoints() { return new HashSet<>(excludedEndpoints); }
    public Set<String> getPriorityEndpoints() { return new HashSet<>(priorityEndpoints); }

    // Advanced feature getters
    public boolean isEnableCrossEndpointTesting() { return enableCrossEndpointTesting; }
    public boolean isEnableAdaptiveTestGeneration() { return enableAdaptiveTestGeneration; }
    public boolean isEnableTestOptimization() { return enableTestOptimization; }

    // API configuration getters
    public String getApiKey() { return apiKey; }
    public String getApiKeyEnvVar() { return apiKeyEnvVar; }
    public String getModel() { return model; }
    public int getMaxTokens() { return maxTokens; }
    public int getMaxRetries() { return maxRetries; }
    public int getTimeoutSeconds() { return timeoutSeconds; }
    public int getThreadPoolSize() { return threadPoolSize; }
    public boolean isVerbose() { return verbose; }
    public boolean isUseFallbackOnError() { return useFallbackOnError; }
    public String getBaseUri() { return baseUri; }
    public int getBatchSize() { return batchSize; }
    public boolean isEnableParallelExecution() { return enableParallelExecution; }

    // OpenAI specific getters
    public double getTemperature() { return temperature; }
    public double getTopP() { return topP; }
    public int getPresencePenalty() { return presencePenalty; }
    public int getFrequencyPenalty() { return frequencyPenalty; }

    // Additional feature getters
    public boolean isGenerateBoundaryTests() { return generateBoundaryTests; }
    public boolean isIncludeNegativeTests() { return includeNegativeTests; }
    public boolean isGenerateApiTests() { return generateApiTests; }
    public boolean isGenerateModelTests() { return generateModelTests; }
    public boolean isGenerateClientTests() { return generateClientTests; }
    public boolean isEnableAsyncTesting() { return enableAsyncTesting; }
    public boolean isEnableRetryOnFailure() { return enableRetryOnFailure; }
    public boolean isEnableDetailedReporting() { return enableDetailedReporting; }
    public boolean isEnableTestDataGeneration() { return enableTestDataGeneration; }
    public boolean isEnableConstraintValidation() { return enableConstraintValidation; }
    public boolean isEnableDependencyAnalysis() { return enableDependencyAnalysis; }
    public boolean isEnableComplexityAnalysis() { return enableComplexityAnalysis; }
    public boolean isEnableTestOrchestration() { return enableTestOrchestration; }
    public boolean isEnableTestPrioritization() { return enableTestPrioritization; }
    public boolean isEnableStatefulTesting() { return enableStatefulTesting; }
    public boolean isEnableTestMaintenance() { return enableTestMaintenance; }

    // Report getters
    public boolean isGenerateTestReport() { return generateTestReport; }
    public boolean isGenerateCoverageReport() { return generateCoverageReport; }
    public boolean isGenerateDocumentation() { return generateDocumentation; }
    public boolean isGenerateMetrics() { return generateMetrics; }
    public ReportFormat getReportFormat() { return reportFormat; }

    // Test utilities getters
    public boolean isGenerateTestUtilities() { return generateTestUtilities; }
    public boolean isGenerateTestFixtures() { return generateTestFixtures; }
    public boolean isGenerateTestHelpers() { return generateTestHelpers; }

    // External data getters
    public boolean isUseExternalTestDataSources() { return useExternalTestDataSources; }

    // ===== UTILITY METHODS =====

    /**
     * Standard utility method: Get estimated test count
     */
    public int getEstimatedTestCount() {
        int baseTests = maxTestsPerEndpoint;
        int enabledScenarioCount = enabledScenarios.size();
        int multiplier = Math.max(1, enabledScenarioCount);
        return Math.min(baseTests * multiplier, coverageLevel.getMaxTestsPerEndpoint());
    }

    /**
     * Standard utility method: Get strategies for scenario category
     */
    public Set<StrategyType> getStrategiesForCategory(StrategyCategory category) {
        return enabledStrategies.stream()
                .filter(strategy -> strategy.getCategory() == category)
                .collect(HashSet::new, HashSet::add, HashSet::addAll);
    }

    /**
     * Standard utility method: Get scenarios for category
     */
    public Set<TestGenerationScenario> getScenariosForCategory(ScenarioCategory category) {
        return enabledScenarios.stream()
                .filter(scenario -> scenario.getCategory() == category)
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
                .mapToInt(StrategyType::getComplexity)
                .average()
                .orElse(1.0);
    }

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

    // ===== INNER CLASSES =====

    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;
        private final List<String> warnings;
        private final List<String> suggestions;

        public ValidationResult(boolean valid, List<String> errors, List<String> warnings, List<String> suggestions) {
            this.valid = valid;
            this.errors = new ArrayList<>(errors);
            this.warnings = new ArrayList<>(warnings);
            this.suggestions = new ArrayList<>(suggestions != null ? suggestions : Collections.emptyList());
        }

        // Backward compatibility constructor
        public ValidationResult(boolean valid, List<String> errors, List<String> warnings) {
            this(valid, errors, warnings, Collections.emptyList());
        }

        public boolean isValid() { return valid; }
        public List<String> getErrors() { return new ArrayList<>(errors); }
        public List<String> getWarnings() { return new ArrayList<>(warnings); }
        public List<String> getSuggestions() { return new ArrayList<>(suggestions); }
        public boolean hasWarnings() { return !warnings.isEmpty(); }
        public boolean hasSuggestions() { return !suggestions.isEmpty(); }
    }

    public static class TestPattern {
        private final String name;
        private final String description;
        private final PatternType type;
        private final Map<String, Object> parameters;

        public TestPattern(String name, String description, PatternType type) {
            this.name = name;
            this.description = description;
            this.type = type;
            this.parameters = new HashMap<>();
        }

        public TestPattern addParameter(String key, Object value) {
            parameters.put(key, value);
            return this;
        }

        public String getName() { return name; }
        public String getDescription() { return description; }
        public PatternType getType() { return type; }
        public Map<String, Object> getParameters() { return new HashMap<>(parameters); }

        public enum PatternType {
            VALIDATION, BOUNDARY, ERROR_HANDLING, SECURITY, PERFORMANCE, CUSTOM
        }
    }

    public static class ValidationRule {
        private final String name;
        private final Pattern pattern;
        private final String errorMessage;
        private final RuleSeverity severity;

        public ValidationRule(String name, String regex, String errorMessage, RuleSeverity severity) {
            this.name = name;
            this.pattern = Pattern.compile(regex);
            this.errorMessage = errorMessage;
            this.severity = severity;
        }

        public boolean matches(String input) {
            return pattern.matcher(input).matches();
        }

        public String getName() { return name; }
        public String getErrorMessage() { return errorMessage; }
        public RuleSeverity getSeverity() { return severity; }

        public enum RuleSeverity {
            ERROR, WARNING, INFO
        }
    }

    // ===== STANDARD CONFIGURATION PROFILES =====

    /**
     * Standard method: Create basic development configuration
     */
    public static Configuration createDevelopmentProfile() {
        return Configuration.builder()
                .withCoverageLevel(TestCoverageLevel.BASIC)
                .withTestDataStrategy(TestDataStrategy.MINIMAL)
                .withTestFramework(TestFramework.JUNIT5)
                .withMockFramework(MockFramework.MOCKITO)
                .withAssertionLibrary(AssertionLibrary.ASSERTJ)
                .withThreadPoolSize(2)
                .withMaxTestsPerEndpoint(10)
                .withVerbose(true)
                .build();
    }

    /**
     * Standard method: Create comprehensive testing configuration
     */
    public static Configuration createComprehensiveProfile() {
        return Configuration.builder()
                .withCoverageLevel(TestCoverageLevel.COMPREHENSIVE)
                .withTestDataStrategy(TestDataStrategy.COMPREHENSIVE)
                .withTestFramework(TestFramework.JUNIT5)
                .withMockFramework(MockFramework.MOCKITO)
                .withAssertionLibrary(AssertionLibrary.ASSERTJ)
                .enableAllTestTypes()
                .withMaxTestsPerEndpoint(50)
                .withCoverageThreshold(0.95)
                .build();
    }

    /**
     * Standard method: Create security-focused configuration
     */
    public static Configuration createSecurityProfile() {
        return Configuration.builder()
                .withCoverageLevel(TestCoverageLevel.STANDARD)
                .withTestDataStrategy(TestDataStrategy.FUZZING)
                .withTestFramework(TestFramework.JUNIT5)
                .enableSecurityTestSuite()
                .withStrategyType(StrategyType.SECURITY_OWASP_TOP10)
                .withMaxTestsPerEndpoint(30)
                .build();
    }

    /**
     * Standard method: Create performance-focused configuration
     */
    public static Configuration createPerformanceProfile() {
        return Configuration.builder()
                .withCoverageLevel(TestCoverageLevel.STANDARD)
                .withTestDataStrategy(TestDataStrategy.REALISTIC)
                .withTestFramework(TestFramework.JUNIT5)
                .enablePerformanceTestSuite()
                .withStrategyType(StrategyType.PERFORMANCE_LOAD)
                .withThreadPoolSize(Runtime.getRuntime().availableProcessors())
                .withMaxTestsPerEndpoint(25)
                .build();
    }

    /**
     * Standard method: Create enterprise configuration
     */
    public static Configuration createEnterpriseProfile() {
        return Configuration.builder()
                .withCoverageLevel(TestCoverageLevel.EXHAUSTIVE)
                .withTestDataStrategy(TestDataStrategy.COMBINATORIAL)
                .withTestFramework(TestFramework.JUNIT5)
                .withMockFramework(MockFramework.MOCKITO)
                .withAssertionLibrary(AssertionLibrary.ASSERTJ)
                .enableAdvancedTestTypes()
                .enableSecurityTestSuite()
                .enablePerformanceTestSuite()
                .withStrategyType(StrategyType.ADVANCED_AI_DRIVEN)
                .withMaxTestsPerEndpoint(100)
                .withCoverageThreshold(0.98)
                .withThreadPoolSize(Runtime.getRuntime().availableProcessors() * 2)
                .build();
    }

    // ===== STANDARD UTILITY METHODS =====

    /**
     * Standard method: Create configuration summary
     */
    public ConfigurationSummary createSummary() {
        return new ConfigurationSummary(
                coverageLevel,
                enabledStrategies.size(),
                enabledScenarios.size(),
                countEnabledTestTypes(),
                getEstimatedTestCount(),
                getComplexityScore(),
                testDataStrategy,
                testFramework,
                maxTestsPerEndpoint,
                coverageThreshold
        );
    }

    /**
     * Standard method: Clone configuration
     */
    public Configuration clone() {
        Configuration cloned = new Configuration();

        // Copy basic settings
        cloned.inputFile = this.inputFile;
        cloned.outputFile = this.outputFile;
        cloned.testClassName = this.testClassName;
        cloned.packageName = this.packageName;
        cloned.testOutputDir = this.testOutputDir;

        // Copy strategy settings
        cloned.enabledStrategies = new HashSet<>(this.enabledStrategies);
        cloned.enabledScenarios = new HashSet<>(this.enabledScenarios);
        cloned.defaultStrategy = this.defaultStrategy;
        cloned.coverageLevel = this.coverageLevel;
        cloned.maxTestsPerEndpoint = this.maxTestsPerEndpoint;
        cloned.coverageThreshold = this.coverageThreshold;

        // Copy test type settings
        cloned.generateHappyPathTests = this.generateHappyPathTests;
        cloned.generateBoundaryTests = this.generateBoundaryTests;
        cloned.includeNegativeTests = this.includeNegativeTests;
        cloned.generateErrorTests = this.generateErrorTests;
        cloned.generateEdgeCaseTests = this.generateEdgeCaseTests;
        cloned.includeSecurityTests = this.includeSecurityTests;
        cloned.includePerformanceTests = this.includePerformanceTests;

        // Copy framework settings
        cloned.testFramework = this.testFramework;
        cloned.mockFramework = this.mockFramework;
        cloned.assertionLibrary = this.assertionLibrary;

        // Copy other settings
        cloned.testDataStrategy = this.testDataStrategy;
        cloned.maxTestDataVariations = this.maxTestDataVariations;
        cloned.threadPoolSize = this.threadPoolSize;
        cloned.verbose = this.verbose;
        cloned.apiKey = this.apiKey;
        cloned.model = this.model;
        cloned.maxTokens = this.maxTokens;

        // Copy collections
        cloned.customTestPatterns = new ConcurrentHashMap<>(this.customTestPatterns);
        cloned.customValidationRules = new ArrayList<>(this.customValidationRules);
        cloned.excludedEndpoints = new HashSet<>(this.excludedEndpoints);
        cloned.priorityEndpoints = new HashSet<>(this.priorityEndpoints);

        return cloned;
    }

    /**
     * Standard method: Merge configurations
     */
    public Configuration merge(Configuration other) {
        if (other == null) {
            return this;
        }

        Configuration merged = this.clone();

        // Merge strategies and scenarios
        merged.enabledStrategies.addAll(other.enabledStrategies);
        merged.enabledScenarios.addAll(other.enabledScenarios);

        // Take higher values for coverage
        if (other.coverageLevel.ordinal() > merged.coverageLevel.ordinal()) {
            merged.coverageLevel = other.coverageLevel;
        }

        merged.maxTestsPerEndpoint = Math.max(merged.maxTestsPerEndpoint, other.maxTestsPerEndpoint);
        merged.coverageThreshold = Math.max(merged.coverageThreshold, other.coverageThreshold);

        // Merge boolean test types (OR operation)
        merged.generateHappyPathTests = merged.generateHappyPathTests || other.generateHappyPathTests;
        merged.generateBoundaryTests = merged.generateBoundaryTests || other.generateBoundaryTests;
        merged.includeNegativeTests = merged.includeNegativeTests || other.includeNegativeTests;
        merged.generateErrorTests = merged.generateErrorTests || other.generateErrorTests;
        merged.generateEdgeCaseTests = merged.generateEdgeCaseTests || other.generateEdgeCaseTests;
        merged.includeSecurityTests = merged.includeSecurityTests || other.includeSecurityTests;
        merged.includePerformanceTests = merged.includePerformanceTests || other.includePerformanceTests;
        merged.generateFuzzingTests = merged.generateFuzzingTests || other.generateFuzzingTests;
        merged.generateConcurrencyTests = merged.generateConcurrencyTests || other.generateConcurrencyTests;

        // Merge collections
        merged.customTestPatterns.putAll(other.customTestPatterns);
        merged.customValidationRules.addAll(other.customValidationRules);
        merged.excludedEndpoints.addAll(other.excludedEndpoints);
        merged.priorityEndpoints.addAll(other.priorityEndpoints);

        return merged;
    }

    /**
     * Standard method: Convert to properties map
     */
    public Map<String, Object> toProperties() {
        Map<String, Object> properties = new HashMap<>();

        // Basic properties
        properties.put("inputFile", inputFile);
        properties.put("outputFile", outputFile);
        properties.put("testClassName", testClassName);
        properties.put("packageName", packageName);

        // Strategy properties
        properties.put("coverageLevel", coverageLevel.name());
        properties.put("defaultStrategy", defaultStrategy.name());
        properties.put("enabledStrategies", enabledStrategies.stream().map(Enum::name).toArray(String[]::new));
        properties.put("enabledScenarios", enabledScenarios.stream().map(Enum::name).toArray(String[]::new));

        // Coverage properties
        properties.put("maxTestsPerEndpoint", maxTestsPerEndpoint);
        properties.put("coverageThreshold", coverageThreshold);
        properties.put("estimatedTestCount", getEstimatedTestCount());
        properties.put("complexityScore", getComplexityScore());

        // Test type properties
        properties.put("generateHappyPathTests", generateHappyPathTests);
        properties.put("generateBoundaryTests", generateBoundaryTests);
        properties.put("includeNegativeTests", includeNegativeTests);
        properties.put("generateErrorTests", generateErrorTests);
        properties.put("includeSecurityTests", includeSecurityTests);
        properties.put("includePerformanceTests", includePerformanceTests);

        // Framework properties
        properties.put("testFramework", testFramework.name());
        properties.put("mockFramework", mockFramework.name());
        properties.put("assertionLibrary", assertionLibrary.name());

        // Performance properties
        properties.put("threadPoolSize", threadPoolSize);
        properties.put("verbose", verbose);
        properties.put("batchSize", batchSize);

        return properties;
    }

    /**
     * Standard method: Export configuration as JSON-like string
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Configuration{\n");
        sb.append("  inputFile='").append(inputFile).append("',\n");
        sb.append("  outputFile='").append(outputFile).append("',\n");
        sb.append("  testClassName='").append(testClassName).append("',\n");
        sb.append("  coverageLevel=").append(coverageLevel).append(",\n");
        sb.append("  defaultStrategy=").append(defaultStrategy).append(",\n");
        sb.append("  enabledStrategies=").append(enabledStrategies.size()).append(" strategies,\n");
        sb.append("  enabledScenarios=").append(enabledScenarios.size()).append(" scenarios,\n");
        sb.append("  maxTestsPerEndpoint=").append(maxTestsPerEndpoint).append(",\n");
        sb.append("  coverageThreshold=").append(coverageThreshold).append(",\n");
        sb.append("  testFramework=").append(testFramework).append(",\n");
        sb.append("  estimatedTestCount=").append(getEstimatedTestCount()).append(",\n");
        sb.append("  complexityScore=").append(String.format("%.2f", getComplexityScore())).append("\n");
        sb.append("}");
        return sb.toString();
    }

    // ===== CONFIGURATION SUMMARY CLASS =====

    public static class ConfigurationSummary {
        private final TestCoverageLevel coverageLevel;
        private final int enabledStrategiesCount;
        private final int enabledScenariosCount;
        private final int enabledTestTypesCount;
        private final int estimatedTestCount;
        private final double complexityScore;
        private final TestDataStrategy testDataStrategy;
        private final TestFramework testFramework;
        private final int maxTestsPerEndpoint;
        private final double coverageThreshold;

        public ConfigurationSummary(TestCoverageLevel coverageLevel, int enabledStrategiesCount,
                                    int enabledScenariosCount, int enabledTestTypesCount,
                                    int estimatedTestCount, double complexityScore,
                                    TestDataStrategy testDataStrategy, TestFramework testFramework,
                                    int maxTestsPerEndpoint, double coverageThreshold) {
            this.coverageLevel = coverageLevel;
            this.enabledStrategiesCount = enabledStrategiesCount;
            this.enabledScenariosCount = enabledScenariosCount;
            this.enabledTestTypesCount = enabledTestTypesCount;
            this.estimatedTestCount = estimatedTestCount;
            this.complexityScore = complexityScore;
            this.testDataStrategy = testDataStrategy;
            this.testFramework = testFramework;
            this.maxTestsPerEndpoint = maxTestsPerEndpoint;
            this.coverageThreshold = coverageThreshold;
        }

        // Getters
        public TestCoverageLevel getCoverageLevel() { return coverageLevel; }
        public int getEnabledStrategiesCount() { return enabledStrategiesCount; }
        public int getEnabledScenariosCount() { return enabledScenariosCount; }
        public int getEnabledTestTypesCount() { return enabledTestTypesCount; }
        public int getEstimatedTestCount() { return estimatedTestCount; }
        public double getComplexityScore() { return complexityScore; }
        public TestDataStrategy getTestDataStrategy() { return testDataStrategy; }
        public TestFramework getTestFramework() { return testFramework; }
        public int getMaxTestsPerEndpoint() { return maxTestsPerEndpoint; }
        public double getCoverageThreshold() { return coverageThreshold; }

        @Override
        public String toString() {
            return String.format(
                    "ConfigurationSummary{coverage=%s, strategies=%d, scenarios=%d, testTypes=%d, " +
                            "estimatedTests=%d, complexity=%.2f, framework=%s}",
                    coverageLevel, enabledStrategiesCount, enabledScenariosCount,
                    enabledTestTypesCount, estimatedTestCount, complexityScore, testFramework
            );
        }
    }
}