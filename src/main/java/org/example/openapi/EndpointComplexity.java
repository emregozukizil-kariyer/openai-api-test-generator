package org.example.openapi;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ===== STANDARDIZED ENDPOINT COMPLEXITY CLASS - TUTARLILIK REHBERİ UYUMLU =====
 *
 * Tutarlılık rehberine göre standardize edilmiş endpoint complexity class.
 * EndpointInfo, GeneratedTestCase ve ComprehensiveTestSuite ile uyumlu.
 * Standard enum'lar ve method signature'ları kullanır.
 *
 * @author Enterprise Solutions Team
 * @version 4.0.0-STANDARDIZED
 * @since 2025.1
 */
public class EndpointComplexity {

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

    // ===== ENHANCED COMPLEXITY ENUMS =====

    public enum ComplexityLevel {
        TRIVIAL(0, 10, 5, "Çok basit endpoint", StrategyType.FUNCTIONAL_BASIC),
        LOW(11, 30, 15, "Basit endpoint", StrategyType.FUNCTIONAL_COMPREHENSIVE),
        MEDIUM(31, 60, 25, "Orta karmaşıklık", StrategyType.FUNCTIONAL_BOUNDARY),
        HIGH(61, 85, 40, "Yüksek karmaşıklık", StrategyType.SECURITY_BASIC),
        VERY_HIGH(86, 100, 60, "Çok yüksek karmaşıklık", StrategyType.SECURITY_OWASP_TOP10),
        CRITICAL(101, Integer.MAX_VALUE, 100, "Kritik karmaşıklık", StrategyType.ADVANCED_AI_DRIVEN);

        private final int minScore;
        private final int maxScore;
        private final int recommendedTests;
        private final String description;
        private final StrategyType defaultStrategy;

        ComplexityLevel(int minScore, int maxScore, int recommendedTests, String description, StrategyType defaultStrategy) {
            this.minScore = minScore;
            this.maxScore = maxScore;
            this.recommendedTests = recommendedTests;
            this.description = description;
            this.defaultStrategy = defaultStrategy;
        }

        public int getMinScore() { return minScore; }
        public int getMaxScore() { return maxScore; }
        public int getRecommendedTests() { return recommendedTests; }
        public String getDescription() { return description; }
        public StrategyType getDefaultStrategy() { return defaultStrategy; }

        public static ComplexityLevel fromScore(int score) {
            for (ComplexityLevel level : values()) {
                if (score >= level.minScore && score <= level.maxScore) {
                    return level;
                }
            }
            return CRITICAL;
        }
    }

    public enum TestStrategy {
        // Mapping legacy strategies to standard scenarios
        HAPPY_PATH("Temel pozitif test case'leri", TestGenerationScenario.HAPPY_PATH),
        BOUNDARY_TESTING("Sınır değer testleri", TestGenerationScenario.BOUNDARY_VALUES),
        NEGATIVE_TESTING("Negatif test case'leri", TestGenerationScenario.ERROR_HANDLING),
        SECURITY_TESTING("Güvenlik testleri", TestGenerationScenario.XSS_REFLECTED),
        PERFORMANCE_TESTING("Performans testleri", TestGenerationScenario.LOAD_TESTING_LIGHT),
        ERROR_HANDLING("Hata yönetimi testleri", TestGenerationScenario.ERROR_HANDLING),
        INTEGRATION_TESTING("Entegrasyon testleri", TestGenerationScenario.HAPPY_PATH),
        REGRESSION_TESTING("Regresyon testleri", TestGenerationScenario.HAPPY_PATH),
        LOAD_TESTING("Yük testleri", TestGenerationScenario.LOAD_TESTING_HEAVY),
        STRESS_TESTING("Stres testleri", TestGenerationScenario.STRESS_TESTING),
        FUZZ_TESTING("Fuzzing testleri", TestGenerationScenario.FUZZING_INPUT),
        CONTRACT_TESTING("Contract testleri", TestGenerationScenario.HAPPY_PATH),
        END_TO_END("End-to-end testleri", TestGenerationScenario.HAPPY_PATH),
        CHAOS_TESTING("Chaos engineering", TestGenerationScenario.CONCURRENCY_RACE_CONDITIONS),
        ACCESSIBILITY_TESTING("Erişilebilirlik testleri", TestGenerationScenario.HAPPY_PATH);

        private final String description;
        private final TestGenerationScenario associatedScenario;

        TestStrategy(String description, TestGenerationScenario associatedScenario) {
            this.description = description;
            this.associatedScenario = associatedScenario;
        }

        public String getDescription() { return description; }
        public TestGenerationScenario getAssociatedScenario() { return associatedScenario; }
        public StrategyType getRecommendedStrategyType() { return associatedScenario.getRecommendedStrategy(); }
    }

    public enum TestPriority {
        CRITICAL(1, "Kritik - İlk öncelik", StrategyType.SECURITY_PENETRATION),
        HIGH(2, "Yüksek - Öncelikli", StrategyType.SECURITY_OWASP_TOP10),
        MEDIUM(3, "Orta - Normal öncelik", StrategyType.FUNCTIONAL_BOUNDARY),
        LOW(4, "Düşük - Son öncelik", StrategyType.FUNCTIONAL_BASIC),
        OPTIONAL(5, "Opsiyonel - Zaman varsa", StrategyType.FUNCTIONAL_BASIC);

        private final int order;
        private final String description;
        private final StrategyType recommendedStrategy;

        TestPriority(int order, String description, StrategyType recommendedStrategy) {
            this.order = order;
            this.description = description;
            this.recommendedStrategy = recommendedStrategy;
        }

        public int getOrder() { return order; }
        public String getDescription() { return description; }
        public StrategyType getRecommendedStrategy() { return recommendedStrategy; }
    }

    public enum RiskFactor {
        AUTHENTICATION_REQUIRED("Kimlik doğrulama gerekli", StrategyType.SECURITY_BASIC),
        AUTHORIZATION_COMPLEX("Karmaşık yetkilendirme", StrategyType.SECURITY_OWASP_TOP10),
        DATA_MODIFICATION("Veri değiştirme operasyonu", StrategyType.FUNCTIONAL_COMPREHENSIVE),
        EXTERNAL_DEPENDENCY("Dış sistem bağımlılığı", StrategyType.FUNCTIONAL_BOUNDARY),
        FINANCIAL_IMPACT("Finansal etki", StrategyType.SECURITY_PENETRATION),
        PERSONAL_DATA("Kişisel veri işleme", StrategyType.SECURITY_OWASP_TOP10),
        HIGH_TRAFFIC("Yüksek trafik beklenen", StrategyType.PERFORMANCE_LOAD),
        CRITICAL_BUSINESS("Kritik iş süreci", StrategyType.ADVANCED_AI_DRIVEN),
        LEGACY_INTEGRATION("Legacy sistem entegrasyonu", StrategyType.FUNCTIONAL_BOUNDARY),
        THIRD_PARTY_API("3. parti API kullanımı", StrategyType.FUNCTIONAL_COMPREHENSIVE),
        FILE_UPLOAD("Dosya yükleme", StrategyType.SECURITY_BASIC),
        BULK_OPERATION("Toplu işlem", StrategyType.PERFORMANCE_STRESS),
        REAL_TIME("Gerçek zamanlı işlem", StrategyType.PERFORMANCE_BASIC),
        ASYNC_OPERATION("Asenkron işlem", StrategyType.ADVANCED_CONCURRENCY),
        CACHING_INVOLVED("Cache mekanizması", StrategyType.PERFORMANCE_BASIC);

        private final String description;
        private final StrategyType requiredStrategy;

        RiskFactor(String description, StrategyType requiredStrategy) {
            this.description = description;
            this.requiredStrategy = requiredStrategy;
        }

        public String getDescription() { return description; }
        public StrategyType getRequiredStrategy() { return requiredStrategy; }
    }

    public enum ComplexityDimension {
        PARAMETERS("Parametre karmaşıklığı", StrategyType.FUNCTIONAL_BOUNDARY),
        RESPONSES("Response karmaşıklığı", StrategyType.FUNCTIONAL_COMPREHENSIVE),
        SECURITY("Güvenlik karmaşıklığı", StrategyType.SECURITY_BASIC),
        BUSINESS_LOGIC("İş mantığı karmaşıklığı", StrategyType.FUNCTIONAL_COMPREHENSIVE),
        DATA_STRUCTURE("Veri yapısı karmaşıklığı", StrategyType.FUNCTIONAL_BOUNDARY),
        ERROR_HANDLING("Hata yönetimi karmaşıklığı", StrategyType.FUNCTIONAL_COMPREHENSIVE),
        PERFORMANCE("Performans karmaşıklığı", StrategyType.PERFORMANCE_BASIC),
        INTEGRATION("Entegrasyon karmaşıklığı", StrategyType.FUNCTIONAL_COMPREHENSIVE);

        private final String description;
        private final StrategyType associatedStrategy;

        ComplexityDimension(String description, StrategyType associatedStrategy) {
            this.description = description;
            this.associatedStrategy = associatedStrategy;
        }

        public String getDescription() { return description; }
        public StrategyType getAssociatedStrategy() { return associatedStrategy; }
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

        // Legacy support methods for compatibility
        public String getHttpMethod() { return method; }
        public int getParameterCount() { return parameters != null ? parameters.size() : 0; }
        public int getComplexParameterCount() { return (int) parameters.stream().filter(p -> isComplexParameter(p)).count(); }
        public int getRequiredParameterCount() { return (int) parameters.stream().filter(ParameterInfo::isRequired).count(); }
        public int getNestedObjectCount() { return 0; } // Simplified for legacy compatibility
        public int getResponseTypeCount() { return responses != null ? responses.size() : 0; }
        public int getComplexResponseCount() { return responses != null ? responses.size() / 2 : 0; }
        public int getErrorResponseCount() { return (int) responses.entrySet().stream().filter(e -> e.getKey().startsWith("4") || e.getKey().startsWith("5")).count(); }
        public boolean requiresAuthorization() { return requiresAuthentication; }
        public boolean hasRoleBasedAccess() { return securitySchemes != null && !securitySchemes.isEmpty(); }
        public boolean handlesPersonalData() { return false; } // Would need schema analysis
        public boolean hasRateLimiting() { return false; } // Would need OpenAPI extension analysis
        public boolean isDataModifying() { return "POST".equals(method) || "PUT".equals(method) || "PATCH".equals(method) || "DELETE".equals(method); }
        public boolean hasBusinessRules() { return isDataModifying(); }
        public boolean hasValidationRules() { return hasParameters || hasRequestBody; }
        public boolean hasWorkflow() { return isDataModifying() && hasParameters; }
        public int getNestedLevels() { return 1; } // Simplified
        public int getArrayFieldCount() { return 0; } // Would need schema analysis
        public int getObjectFieldCount() { return 0; } // Would need schema analysis
        public int getEnumFieldCount() { return 0; } // Would need schema analysis
        public int getErrorStatusCount() { return getErrorResponseCount(); }
        public int getCustomErrorCount() { return 0; } // Would need response analysis
        public boolean hasErrorRecovery() { return false; } // Would need documentation analysis
        public boolean hasRetryLogic() { return false; } // Would need documentation analysis

        private boolean isComplexParameter(ParameterInfo param) {
            return param.getType() != null && (param.getType().equals("object") || param.getType().equals("array"));
        }
    }



    /**
     * Standard ComprehensiveTestSuite class - Tutarlılık rehberi uyumlu
     */
    public static class ComprehensiveTestSuite {
        private EndpointInfo endpoint;
        private List<GeneratedTestCase> testCases;
        private AdvancedStrategyRecommendation recommendation;
        private AdvancedStrategyExecutionPlan executionPlan;
        private QualityMetrics qualityMetrics;
        private SecurityProfile securityProfile;
        private PerformanceProfile performanceProfile;
        private ComplianceProfile complianceProfile;
        private String executionId;
        private Instant generationTimestamp;
        private double qualityScore;

        private ComprehensiveTestSuite() {}

        // Standard getters
        public EndpointInfo getEndpoint() { return endpoint; }
        public List<GeneratedTestCase> getTestCases() { return testCases; }
        public AdvancedStrategyRecommendation getRecommendation() { return recommendation; }
        public AdvancedStrategyExecutionPlan getExecutionPlan() { return executionPlan; }
        public QualityMetrics getQualityMetrics() { return qualityMetrics; }
        public SecurityProfile getSecurityProfile() { return securityProfile; }
        public PerformanceProfile getPerformanceProfile() { return performanceProfile; }
        public ComplianceProfile getComplianceProfile() { return complianceProfile; }
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

            public Builder withRecommendation(AdvancedStrategyRecommendation recommendation) {
                suite.recommendation = recommendation;
                return this;
            }

            public Builder withExecutionPlan(AdvancedStrategyExecutionPlan executionPlan) {
                suite.executionPlan = executionPlan;
                return this;
            }

            public Builder withQualityMetrics(QualityMetrics qualityMetrics) {
                suite.qualityMetrics = qualityMetrics;
                return this;
            }

            public Builder withSecurityProfile(SecurityProfile securityProfile) {
                suite.securityProfile = securityProfile;
                return this;
            }

            public Builder withPerformanceProfile(PerformanceProfile performanceProfile) {
                suite.performanceProfile = performanceProfile;
                return this;
            }

            public Builder withComplianceProfile(ComplianceProfile complianceProfile) {
                suite.complianceProfile = complianceProfile;
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
                // Set defaults
                if (suite.executionId == null) {
                    suite.executionId = generateAdvancedExecutionId();
                }
                if (suite.generationTimestamp == null) {
                    suite.generationTimestamp = Instant.now();
                }
                if (suite.testCases == null) {
                    suite.testCases = new ArrayList<>();
                }

                // Calculate quality score
                suite.qualityScore = calculateQualityScore(suite);

                return suite;
            }
        }
    }

    // ===== CORE COMPLEXITY FIELDS =====

    // Temel Complexity Alanları
    private int totalScore;
    private ComplexityLevel level;
    private String reasoning;
    private double confidenceScore = 1.0;

    // Standard Strategy Configuration
    private Set<StrategyType> enabledStrategies = new HashSet<>();
    private Set<TestGenerationScenario> enabledScenarios = new HashSet<>();
    private StrategyType defaultStrategy = StrategyType.FUNCTIONAL_COMPREHENSIVE;

    // Detailed Complexity Metrics
    private int parameterComplexity;
    private int responseComplexity;
    private int securityComplexity;
    private int businessLogicComplexity;
    private int dataStructureComplexity;
    private int errorHandlingComplexity;

    // Test Generation Recommendations
    private int recommendedTestCount;
    private Set<TestStrategy> recommendedStrategies;
    private Map<String, Integer> testTypeDistribution;
    private TestPriority priority;

    // Risk Factors
    private Set<RiskFactor> riskFactors;
    private double riskScore = 0.0;
    private List<String> securityConcerns;
    private List<String> performanceConcerns;

    // Complexity Breakdown
    private Map<ComplexityDimension, ComplexityMetric> dimensionBreakdown;
    private List<ComplexityReason> complexityReasons;

    // ===== CONSTRUCTORS =====

    public EndpointComplexity() {
        this.recommendedStrategies = EnumSet.noneOf(TestStrategy.class);
        this.testTypeDistribution = new HashMap<>();
        this.riskFactors = EnumSet.noneOf(RiskFactor.class);
        this.securityConcerns = new ArrayList<>();
        this.performanceConcerns = new ArrayList<>();
        this.dimensionBreakdown = new HashMap<>();
        this.complexityReasons = new ArrayList<>();
        this.enabledStrategies = new HashSet<>();
        this.enabledScenarios = new HashSet<>();
        initializeStandardStrategies();
    }

    public EndpointComplexity(int totalScore) {
        this();
        setTotalScore(totalScore);
    }

    /**
     * Standard method: Initialize standard strategies based on complexity level
     */
    private void initializeStandardStrategies() {
        enabledStrategies.clear();
        enabledScenarios.clear();

        if (level != null) {
            switch (level) {
                case TRIVIAL:
                case LOW:
                    enabledStrategies.add(StrategyType.FUNCTIONAL_BASIC);
                    enabledScenarios.add(TestGenerationScenario.HAPPY_PATH);
                    break;
                case MEDIUM:
                    enabledStrategies.add(StrategyType.FUNCTIONAL_BASIC);
                    enabledStrategies.add(StrategyType.FUNCTIONAL_COMPREHENSIVE);
                    enabledScenarios.add(TestGenerationScenario.HAPPY_PATH);
                    enabledScenarios.add(TestGenerationScenario.BOUNDARY_VALUES);
                    break;
                case HIGH:
                    enabledStrategies.add(StrategyType.FUNCTIONAL_COMPREHENSIVE);
                    enabledStrategies.add(StrategyType.FUNCTIONAL_BOUNDARY);
                    enabledStrategies.add(StrategyType.SECURITY_BASIC);
                    enabledScenarios.add(TestGenerationScenario.HAPPY_PATH);
                    enabledScenarios.add(TestGenerationScenario.BOUNDARY_VALUES);
                    enabledScenarios.add(TestGenerationScenario.ERROR_HANDLING);
                    break;
                case VERY_HIGH:
                case CRITICAL:
                    enabledStrategies.addAll(Arrays.asList(StrategyType.values()));
                    enabledScenarios.addAll(Arrays.asList(TestGenerationScenario.values()));
                    break;
            }

            this.defaultStrategy = level.getDefaultStrategy();
        }
    }

    // ===== BUILDER PATTERN - STANDARDIZED =====

    /**
     * Standard factory method
     */
    public static EndpointComplexity create() {
        return new EndpointComplexity();
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
        private EndpointComplexity complexity = new EndpointComplexity();

        public Builder withScore(int score) {
            complexity.setTotalScore(score);
            return this;
        }

        public Builder withStrategy(StrategyType strategy) {
            complexity.enabledStrategies.add(strategy);
            return this;
        }

        public Builder withScenario(TestGenerationScenario scenario) {
            complexity.enabledScenarios.add(scenario);
            complexity.enabledStrategies.add(scenario.getRecommendedStrategy());
            return this;
        }

        public Builder withLegacyStrategy(TestStrategy strategy) {
            complexity.recommendedStrategies.add(strategy);
            complexity.enabledScenarios.add(strategy.getAssociatedScenario());
            complexity.enabledStrategies.add(strategy.getRecommendedStrategyType());
            return this;
        }

        public Builder withAllStrategies() {
            complexity.enabledStrategies.addAll(Arrays.asList(StrategyType.values()));
            complexity.enabledScenarios.addAll(Arrays.asList(TestGenerationScenario.values()));
            complexity.recommendedStrategies.addAll(Arrays.asList(TestStrategy.values()));
            return this;
        }

        public Builder withPriority(TestPriority priority) {
            complexity.priority = priority;
            complexity.enabledStrategies.add(priority.getRecommendedStrategy());
            return this;
        }

        public Builder withRisk(RiskFactor risk) {
            complexity.riskFactors.add(risk);
            complexity.enabledStrategies.add(risk.getRequiredStrategy());
            complexity.updateRiskScore();
            return this;
        }

        public Builder withReason(String category, String reason, int impact) {
            complexity.complexityReasons.add(new ComplexityReason(category, reason, impact));
            return this;
        }

        public Builder withComplexityLevel(ComplexityLevel level) {
            complexity.level = level;
            complexity.totalScore = (level.getMinScore() + level.getMaxScore()) / 2;
            complexity.defaultStrategy = level.getDefaultStrategy();
            complexity.initializeStandardStrategies();
            return this;
        }

        public EndpointComplexity build() {
            validateAndEnhanceConfiguration(complexity);
            return complexity;
        }
    }

    // ===== STANDARD FACTORY METHODS =====

    public static EndpointComplexity simple() {
        return EndpointComplexity.builder()
                .withScore(15)
                .withLegacyStrategy(TestStrategy.HAPPY_PATH)
                .withLegacyStrategy(TestStrategy.BOUNDARY_TESTING)
                .withPriority(TestPriority.MEDIUM)
                .build();
    }

    public static EndpointComplexity complex() {
        return EndpointComplexity.builder()
                .withScore(75)
                .withLegacyStrategy(TestStrategy.HAPPY_PATH)
                .withLegacyStrategy(TestStrategy.BOUNDARY_TESTING)
                .withLegacyStrategy(TestStrategy.NEGATIVE_TESTING)
                .withLegacyStrategy(TestStrategy.SECURITY_TESTING)
                .withLegacyStrategy(TestStrategy.ERROR_HANDLING)
                .withPriority(TestPriority.HIGH)
                .build();
    }

    public static EndpointComplexity critical() {
        return EndpointComplexity.builder()
                .withScore(95)
                .withAllStrategies()
                .withPriority(TestPriority.CRITICAL)
                .withRisk(RiskFactor.CRITICAL_BUSINESS)
                .withRisk(RiskFactor.FINANCIAL_IMPACT)
                .build();
    }

    // ===== STANDARD UTILITY METHODS =====

    /**
     * Standard validation method
     */
    private static void validateAndEnhanceConfiguration(EndpointComplexity complexity) {
        if (complexity.totalScore < 0) {
            throw new IllegalArgumentException("Total score cannot be negative");
        }

        // Ensure we have at least basic strategies enabled
        if (complexity.enabledStrategies.isEmpty()) {
            complexity.enabledStrategies.add(StrategyType.FUNCTIONAL_BASIC);
        }

        if (complexity.enabledScenarios.isEmpty()) {
            complexity.enabledScenarios.add(TestGenerationScenario.HAPPY_PATH);
        }

        // Generate recommendations if not set
        if (complexity.recommendedTestCount == 0) {
            complexity.generateRecommendations();
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
        return "complexity_" + System.currentTimeMillis() + "_" +
                Thread.currentThread().getId() + "_" +
                (int)(Math.random() * 10000);
    }

    // ===== FLUENT API METHODS =====

    public EndpointComplexity withScore(int score) {
        setTotalScore(score);
        return this;
    }

    public EndpointComplexity withStrategy(TestStrategy strategy) {
        this.recommendedStrategies.add(strategy);
        this.enabledScenarios.add(strategy.getAssociatedScenario());
        this.enabledStrategies.add(strategy.getRecommendedStrategyType());
        return this;
    }

    public EndpointComplexity withAllStrategies() {
        this.recommendedStrategies.addAll(Arrays.asList(TestStrategy.values()));
        this.enabledStrategies.addAll(Arrays.asList(StrategyType.values()));
        this.enabledScenarios.addAll(Arrays.asList(TestGenerationScenario.values()));
        return this;
    }

    public EndpointComplexity withPriority(TestPriority priority) {
        this.priority = priority;
        this.enabledStrategies.add(priority.getRecommendedStrategy());
        return this;
    }

    public EndpointComplexity withRisk(RiskFactor risk) {
        this.riskFactors.add(risk);
        this.enabledStrategies.add(risk.getRequiredStrategy());
        updateRiskScore();
        return this;
    }

    public EndpointComplexity withReason(String category, String reason, int impact) {
        this.complexityReasons.add(new ComplexityReason(category, reason, impact));
        return this;
    }

    // ===== STANDARD COMPLEXITY CALCULATION METHODS =====

    /**
     * Standard method: Calculate comprehensive complexity for endpoint
     *
     * @param endpoint EndpointInfo to analyze
     * @return ComprehensiveTestSuite with complexity analysis
     */
    public static ComprehensiveTestSuite generateComprehensiveComplexityAnalysis(EndpointInfo endpoint) {
        EndpointComplexity complexity = calculateComplexity(endpoint);
        String executionId = generateAdvancedExecutionId();

        List<GeneratedTestCase> testCases = new ArrayList<>();

        // Generate test cases based on enabled scenarios
        for (TestGenerationScenario scenario : complexity.enabledScenarios) {
            testCases.addAll(generateTestCasesForComplexityScenario(scenario, endpoint, executionId));
        }

        // Apply complexity-based limits
        if (testCases.size() > complexity.recommendedTestCount) {
            testCases = prioritizeAndLimit(testCases, complexity.recommendedTestCount);
        }

        return ComprehensiveTestSuite.builder()
                .withEndpoint(endpoint)
                .withTestCases(testCases)
                .withExecutionId(executionId)
                .withGenerationTimestamp(Instant.now())
                .build();
    }

    /**
     * Endpoint'in toplam karmaşıklığını hesaplar - Enhanced with strategy integration
     */
    public static EndpointComplexity calculateComplexity(EndpointInfo endpoint) {
        EndpointComplexity complexity = new EndpointComplexity();

        int score = 0;
        List<ComplexityReason> reasons = new ArrayList<>();

        // Parameter complexity
        int paramScore = calculateParameterComplexity(endpoint);
        score += paramScore;
        complexity.parameterComplexity = paramScore;
        if (paramScore > 0) {
            reasons.add(new ComplexityReason("Parameters",
                    String.format("%d parameters found", endpoint.getParameterCount()), paramScore));
            complexity.enabledStrategies.add(StrategyType.FUNCTIONAL_BOUNDARY);
        }

        // Response complexity
        int responseScore = calculateResponseComplexity(endpoint);
        score += responseScore;
        complexity.responseComplexity = responseScore;
        if (responseScore > 0) {
            reasons.add(new ComplexityReason("Response",
                    "Response structure complexity", responseScore));
            complexity.enabledStrategies.add(StrategyType.FUNCTIONAL_COMPREHENSIVE);
        }

        // Security complexity
        int securityScore = calculateSecurityComplexity(endpoint);
        score += securityScore;
        complexity.securityComplexity = securityScore;
        if (securityScore > 0) {
            reasons.add(new ComplexityReason("Security",
                    "Authentication/authorization required", securityScore));
            complexity.enabledStrategies.add(StrategyType.SECURITY_BASIC);
            complexity.enabledScenarios.add(TestGenerationScenario.AUTH_BYPASS);
        }

        // Business logic complexity
        int businessScore = calculateBusinessLogicComplexity(endpoint);
        score += businessScore;
        complexity.businessLogicComplexity = businessScore;
        if (businessScore > 10) {
            complexity.enabledStrategies.add(StrategyType.FUNCTIONAL_COMPREHENSIVE);
            complexity.enabledScenarios.add(TestGenerationScenario.ERROR_HANDLING);
        }

        // Data structure complexity
        int dataScore = calculateDataStructureComplexity(endpoint);
        score += dataScore;
        complexity.dataStructureComplexity = dataScore;
        if (dataScore > 5) {
            complexity.enabledStrategies.add(StrategyType.FUNCTIONAL_BOUNDARY);
            complexity.enabledScenarios.add(TestGenerationScenario.BOUNDARY_VALUES);
        }

        // Error handling complexity
        int errorScore = calculateErrorHandlingComplexity(endpoint);
        score += errorScore;
        complexity.errorHandlingComplexity = errorScore;
        if (errorScore > 0) {
            complexity.enabledScenarios.add(TestGenerationScenario.ERROR_HANDLING);
        }

        complexity.setTotalScore(score);
        complexity.complexityReasons = reasons;
        complexity.generateRecommendations();

        return complexity;
    }

    private static List<GeneratedTestCase> generateTestCasesForComplexityScenario(
            TestGenerationScenario scenario, EndpointInfo endpoint, String executionId) {
        List<GeneratedTestCase> testCases = new ArrayList<>();

        testCases.add(GeneratedTestCase.builder()
                .withTestId(executionId + "_" + scenario.name().toLowerCase())
                .withTestName("Complexity Analysis: " + scenario.getDescription())
                .withDescription("Test case generated based on endpoint complexity analysis")
                .withScenario(scenario)
                .withStrategyType(scenario.getRecommendedStrategy())
                .withComplexity(scenario.getComplexity())
                .withPriority(scenario.getComplexity())
                .withTags(Set.of("complexity-analysis", scenario.getCategory().toLowerCase()))
                .build());

        return testCases;
    }

    private static List<GeneratedTestCase> prioritizeAndLimit(List<GeneratedTestCase> cases, int limit) {
        return cases.stream()
                .sorted((a, b) -> {
                    // Sort by priority (lower is higher priority), then by complexity
                    int priorityComparison = Integer.compare(a.getPriority(), b.getPriority());
                    if (priorityComparison != 0) return priorityComparison;
                    return Integer.compare(b.getComplexity(), a.getComplexity());
                })
                .limit(limit)
                .collect(Collectors.toList());
    }

    private static int calculateParameterComplexity(EndpointInfo endpoint) {
        int score = 0;
        int paramCount = endpoint.getParameterCount();

        // Base parameter count score
        score += Math.min(paramCount * 2, 20);

        // Complex parameter types
        score += endpoint.getComplexParameterCount() * 3;

        // Required parameters
        score += endpoint.getRequiredParameterCount() * 1;

        // Nested objects
        score += endpoint.getNestedObjectCount() * 5;

        return score;
    }

    private static int calculateResponseComplexity(EndpointInfo endpoint) {
        int score = 0;

        // Multiple response types
        score += endpoint.getResponseTypeCount() * 3;

        // Complex response structures
        score += endpoint.getComplexResponseCount() * 4;

        // Error response variety
        score += endpoint.getErrorResponseCount() * 2;

        return score;
    }

    private static int calculateSecurityComplexity(EndpointInfo endpoint) {
        int score = 0;

        if (endpoint.isRequiresAuthentication()) score += 10;
        if (endpoint.requiresAuthorization()) score += 10;
        if (endpoint.hasRoleBasedAccess()) score += 15;
        if (endpoint.handlesPersonalData()) score += 15;
        if (endpoint.hasRateLimiting()) score += 5;

        return score;
    }

    private static int calculateBusinessLogicComplexity(EndpointInfo endpoint) {
        int score = 0;

        // HTTP method complexity
        switch (endpoint.getHttpMethod().toUpperCase()) {
            case "GET":
                score += 2;
                break;
            case "POST":
                score += 8;
                break;
            case "PUT":
                score += 10;
                break;
            case "PATCH":
                score += 12;
                break;
            case "DELETE":
                score += 15;
                break;
        }

        // Business operations
        if (endpoint.isDataModifying()) score += 10;
        if (endpoint.hasBusinessRules()) score += 15;
        if (endpoint.hasValidationRules()) score += 8;
        if (endpoint.hasWorkflow()) score += 20;

        return score;
    }

    private static int calculateDataStructureComplexity(EndpointInfo endpoint) {
        int score = 0;

        score += endpoint.getNestedLevels() * 3;
        score += endpoint.getArrayFieldCount() * 2;
        score += endpoint.getObjectFieldCount() * 2;
        score += endpoint.getEnumFieldCount() * 1;

        return score;
    }

    private static int calculateErrorHandlingComplexity(EndpointInfo endpoint) {
        int score = 0;

        score += endpoint.getErrorStatusCount() * 2;
        score += endpoint.getCustomErrorCount() * 3;
        if (endpoint.hasErrorRecovery()) score += 10;
        if (endpoint.hasRetryLogic()) score += 8;

        return score;
    }

    // ===== RECOMMENDATION GENERATION =====

    private void generateRecommendations() {
        // Reset recommendations
        recommendedStrategies.clear();
        testTypeDistribution.clear();

        // Base strategies for all endpoints
        recommendedStrategies.add(TestStrategy.HAPPY_PATH);
        testTypeDistribution.put("happy_path", 3);
        enabledScenarios.add(TestGenerationScenario.HAPPY_PATH);

        // Complexity-based strategies
        if (level != null && level.ordinal() >= ComplexityLevel.MEDIUM.ordinal()) {
            recommendedStrategies.add(TestStrategy.BOUNDARY_TESTING);
            recommendedStrategies.add(TestStrategy.NEGATIVE_TESTING);
            testTypeDistribution.put("boundary", 4);
            testTypeDistribution.put("negative", 4);
            enabledScenarios.add(TestGenerationScenario.BOUNDARY_VALUES);
            enabledScenarios.add(TestGenerationScenario.ERROR_HANDLING);
        }

        if (level != null && level.ordinal() >= ComplexityLevel.HIGH.ordinal()) {
            recommendedStrategies.add(TestStrategy.ERROR_HANDLING);
            recommendedStrategies.add(TestStrategy.INTEGRATION_TESTING);
            testTypeDistribution.put("error_handling", 5);
            testTypeDistribution.put("integration", 3);
        }

        if (level != null && level.ordinal() >= ComplexityLevel.VERY_HIGH.ordinal()) {
            recommendedStrategies.add(TestStrategy.PERFORMANCE_TESTING);
            recommendedStrategies.add(TestStrategy.STRESS_TESTING);
            testTypeDistribution.put("performance", 6);
            testTypeDistribution.put("stress", 4);
            enabledScenarios.add(TestGenerationScenario.LOAD_TESTING_HEAVY);
            enabledScenarios.add(TestGenerationScenario.STRESS_TESTING);
        }

        // Security-based strategies
        if (securityComplexity > 10) {
            recommendedStrategies.add(TestStrategy.SECURITY_TESTING);
            testTypeDistribution.put("security", 8);
            enabledStrategies.add(StrategyType.SECURITY_BASIC);
            enabledScenarios.add(TestGenerationScenario.XSS_REFLECTED);
        }

        if (securityComplexity > 20) {
            recommendedStrategies.add(TestStrategy.FUZZ_TESTING);
            testTypeDistribution.put("fuzzing", 6);
            enabledStrategies.add(StrategyType.ADVANCED_FUZZING);
            enabledScenarios.add(TestGenerationScenario.FUZZING_INPUT);
        }

        // Risk-based strategies
        if (riskFactors.contains(RiskFactor.HIGH_TRAFFIC)) {
            recommendedStrategies.add(TestStrategy.LOAD_TESTING);
            testTypeDistribution.put("load", 8);
            enabledStrategies.add(StrategyType.PERFORMANCE_LOAD);
            enabledScenarios.add(TestGenerationScenario.LOAD_TESTING_HEAVY);
        }

        if (riskFactors.contains(RiskFactor.CRITICAL_BUSINESS)) {
            recommendedStrategies.add(TestStrategy.REGRESSION_TESTING);
            recommendedStrategies.add(TestStrategy.CONTRACT_TESTING);
            testTypeDistribution.put("regression", 10);
            testTypeDistribution.put("contract", 6);
            enabledStrategies.add(StrategyType.ADVANCED_AI_DRIVEN);
        }

        // Calculate total recommended test count
        recommendedTestCount = testTypeDistribution.values().stream()
                .mapToInt(Integer::intValue)
                .sum();

        // Apply complexity level multiplier
        if (level != null) {
            recommendedTestCount = Math.max(recommendedTestCount, level.getRecommendedTests());
        }

        // Set priority based on complexity and risks
        determinePriority();
    }

    private void determinePriority() {
        if (riskFactors.contains(RiskFactor.CRITICAL_BUSINESS) ||
                riskFactors.contains(RiskFactor.FINANCIAL_IMPACT)) {
            priority = TestPriority.CRITICAL;
        } else if (level != null && level.ordinal() >= ComplexityLevel.HIGH.ordinal() ||
                securityComplexity > 20) {
            priority = TestPriority.HIGH;
        } else if (level != null && level.ordinal() >= ComplexityLevel.MEDIUM.ordinal()) {
            priority = TestPriority.MEDIUM;
        } else {
            priority = TestPriority.LOW;
        }
    }

    private void updateRiskScore() {
        if (riskFactors.isEmpty()) {
            riskScore = 0.0;
            return;
        }

        // High-impact risk factors
        int highImpactCount = 0;
        for (RiskFactor factor : riskFactors) {
            switch (factor) {
                case CRITICAL_BUSINESS:
                case FINANCIAL_IMPACT:
                case PERSONAL_DATA:
                    highImpactCount++;
                    break;
            }
        }

        double baseRisk = (double) riskFactors.size() / RiskFactor.values().length;
        double highImpactBonus = (double) highImpactCount * 0.2;

        riskScore = Math.min(1.0, baseRisk + highImpactBonus);
    }

    // ===== STANDARD ANALYSIS METHODS =====

    /**
     * Standard method: Generate comprehensive test strategy recommendation
     *
     * @return AdvancedStrategyRecommendation with standard interface
     */
    public AdvancedStrategyRecommendation generateAdvancedStrategyRecommendation() {
        return new AdvancedStrategyRecommendation(
                defaultStrategy,
                new ArrayList<>(enabledScenarios),
                confidenceScore
        );
    }

    /**
     * Karmaşıklık analizi raporu üretir
     */
    public ComplexityAnalysisReport generateAnalysisReport() {
        return new ComplexityAnalysisReport(this);
    }

    /**
     * Test stratejisi önerisi üretir
     */
    public TestStrategyRecommendation generateTestStrategy() {
        return new TestStrategyRecommendation(this);
    }

    /**
     * Karmaşıklık seviyesini günceller
     */
    public void updateComplexityLevel() {
        this.level = ComplexityLevel.fromScore(totalScore);
        initializeStandardStrategies();
        generateRecommendations();
    }

    /**
     * Confidence score'u hesaplar
     */
    public void calculateConfidenceScore() {
        double completeness = (double) complexityReasons.size() / 8.0; // 8 boyut
        double consistency = 1.0 - (Math.abs(totalScore - calculateExpectedScore()) / 100.0);

        confidenceScore = Math.max(0.0, Math.min(1.0, (completeness + consistency) / 2.0));
    }

    private int calculateExpectedScore() {
        return parameterComplexity + responseComplexity + securityComplexity +
                businessLogicComplexity + dataStructureComplexity + errorHandlingComplexity;
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

    // ===== STANDARD GETTERS - Tutarlılık Rehberi Uyumlu =====

    // Basic complexity getters
    public int getTotalScore() { return totalScore; }
    public ComplexityLevel getLevel() { return level; }
    public String getReasoning() { return reasoning; }
    public double getConfidenceScore() { return confidenceScore; }

    // Standard strategy getters
    public Set<StrategyType> getEnabledStrategies() { return new HashSet<>(enabledStrategies); }
    public Set<TestGenerationScenario> getEnabledScenarios() { return new HashSet<>(enabledScenarios); }
    public StrategyType getDefaultStrategy() { return defaultStrategy; }

    // Detailed complexity getters
    public int getParameterComplexity() { return parameterComplexity; }
    public int getResponseComplexity() { return responseComplexity; }
    public int getSecurityComplexity() { return securityComplexity; }
    public int getBusinessLogicComplexity() { return businessLogicComplexity; }
    public int getDataStructureComplexity() { return dataStructureComplexity; }
    public int getErrorHandlingComplexity() { return errorHandlingComplexity; }

    // Test recommendation getters
    public int getRecommendedTestCount() { return recommendedTestCount; }
    public Set<TestStrategy> getRecommendedStrategies() { return new HashSet<>(recommendedStrategies); }
    public Map<String, Integer> getTestTypeDistribution() { return new HashMap<>(testTypeDistribution); }
    public TestPriority getPriority() { return priority; }

    // Risk assessment getters
    public Set<RiskFactor> getRiskFactors() { return new HashSet<>(riskFactors); }
    public double getRiskScore() { return riskScore; }
    public List<String> getSecurityConcerns() { return new ArrayList<>(securityConcerns); }
    public List<String> getPerformanceConcerns() { return new ArrayList<>(performanceConcerns); }

    // Analysis getters
    public Map<ComplexityDimension, ComplexityMetric> getDimensionBreakdown() { return new HashMap<>(dimensionBreakdown); }
    public List<ComplexityReason> getComplexityReasons() { return new ArrayList<>(complexityReasons); }

    // ===== STANDARD SETTERS =====

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
        this.level = ComplexityLevel.fromScore(totalScore);
        initializeStandardStrategies();
        generateRecommendations();
    }

    public void setLevel(ComplexityLevel level) {
        this.level = level;
        initializeStandardStrategies();
    }

    public void setReasoning(String reasoning) { this.reasoning = reasoning; }
    public void setConfidenceScore(double confidenceScore) { this.confidenceScore = confidenceScore; }
    public void setParameterComplexity(int parameterComplexity) { this.parameterComplexity = parameterComplexity; }
    public void setResponseComplexity(int responseComplexity) { this.responseComplexity = responseComplexity; }
    public void setSecurityComplexity(int securityComplexity) { this.securityComplexity = securityComplexity; }
    public void setBusinessLogicComplexity(int businessLogicComplexity) { this.businessLogicComplexity = businessLogicComplexity; }
    public void setDataStructureComplexity(int dataStructureComplexity) { this.dataStructureComplexity = dataStructureComplexity; }
    public void setErrorHandlingComplexity(int errorHandlingComplexity) { this.errorHandlingComplexity = errorHandlingComplexity; }
    public void setRecommendedTestCount(int recommendedTestCount) { this.recommendedTestCount = recommendedTestCount; }
    public void setRecommendedStrategies(Set<TestStrategy> recommendedStrategies) { this.recommendedStrategies = recommendedStrategies; }
    public void setTestTypeDistribution(Map<String, Integer> testTypeDistribution) { this.testTypeDistribution = testTypeDistribution; }
    public void setPriority(TestPriority priority) { this.priority = priority; }
    public void setRiskFactors(Set<RiskFactor> riskFactors) {
        this.riskFactors = riskFactors;
        updateRiskScore();
    }
    public void setRiskScore(double riskScore) { this.riskScore = riskScore; }
    public void setSecurityConcerns(List<String> securityConcerns) { this.securityConcerns = securityConcerns; }
    public void setPerformanceConcerns(List<String> performanceConcerns) { this.performanceConcerns = performanceConcerns; }
    public void setComplexityReasons(List<ComplexityReason> complexityReasons) { this.complexityReasons = complexityReasons; }

    // ===== UTILITY METHODS =====

    public boolean isHighComplexity() {
        return level != null && level.ordinal() >= ComplexityLevel.HIGH.ordinal();
    }

    public boolean isCritical() {
        return level == ComplexityLevel.CRITICAL || priority == TestPriority.CRITICAL;
    }

    public boolean requiresSecurityTesting() {
        return securityComplexity > 10 || riskFactors.contains(RiskFactor.PERSONAL_DATA);
    }

    public boolean requiresPerformanceTesting() {
        return (level != null && level.ordinal() >= ComplexityLevel.HIGH.ordinal()) ||
                riskFactors.contains(RiskFactor.HIGH_TRAFFIC);
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



    public static class AdvancedStrategyExecutionPlan {
        private List<String> executionSteps;
        private Duration estimatedDuration;
        private int priority;

        public AdvancedStrategyExecutionPlan() {
            this.executionSteps = new ArrayList<>();
        }

        public List<String> getExecutionSteps() { return executionSteps; }
        public Duration getEstimatedDuration() { return estimatedDuration; }
        public int getPriority() { return priority; }
        public void setExecutionSteps(List<String> executionSteps) { this.executionSteps = executionSteps; }
        public void setEstimatedDuration(Duration estimatedDuration) { this.estimatedDuration = estimatedDuration; }
        public void setPriority(int priority) { this.priority = priority; }
    }

    public static class QualityMetrics {
        private double coverageScore;
        private double complexityScore;
        private double reliabilityScore;

        public QualityMetrics() {}
        public QualityMetrics(double coverage, double complexity, double reliability) {
            this.coverageScore = coverage;
            this.complexityScore = complexity;
            this.reliabilityScore = reliability;
        }

        public double getCoverageScore() { return coverageScore; }
        public double getComplexityScore() { return complexityScore; }
        public double getReliabilityScore() { return reliabilityScore; }
        public void setCoverageScore(double coverageScore) { this.coverageScore = coverageScore; }
        public void setComplexityScore(double complexityScore) { this.complexityScore = complexityScore; }
        public void setReliabilityScore(double reliabilityScore) { this.reliabilityScore = reliabilityScore; }
    }

    public static class SecurityProfile {
        private List<String> securityTests;
        private double vulnerabilityScore;

        public SecurityProfile() {
            this.securityTests = new ArrayList<>();
        }

        public List<String> getSecurityTests() { return securityTests; }
        public double getVulnerabilityScore() { return vulnerabilityScore; }
        public void setSecurityTests(List<String> securityTests) { this.securityTests = securityTests; }
        public void setVulnerabilityScore(double vulnerabilityScore) { this.vulnerabilityScore = vulnerabilityScore; }
    }

    public static class PerformanceProfile {
        private Duration expectedResponseTime;
        private int expectedThroughput;

        public PerformanceProfile() {}
        public PerformanceProfile(Duration responseTime, int throughput) {
            this.expectedResponseTime = responseTime;
            this.expectedThroughput = throughput;
        }

        public Duration getExpectedResponseTime() { return expectedResponseTime; }
        public int getExpectedThroughput() { return expectedThroughput; }
        public void setExpectedResponseTime(Duration expectedResponseTime) { this.expectedResponseTime = expectedResponseTime; }
        public void setExpectedThroughput(int expectedThroughput) { this.expectedThroughput = expectedThroughput; }
    }

    public static class ComplianceProfile {
        private List<String> complianceStandards;
        private double complianceScore;

        public ComplianceProfile() {
            this.complianceStandards = new ArrayList<>();
        }

        public List<String> getComplianceStandards() { return complianceStandards; }
        public double getComplianceScore() { return complianceScore; }
        public void setComplianceStandards(List<String> complianceStandards) { this.complianceStandards = complianceStandards; }
        public void setComplianceScore(double complianceScore) { this.complianceScore = complianceScore; }
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

    // ===== toString, equals, hashCode =====

    @Override
    public String toString() {
        return "EndpointComplexity{" +
                "totalScore=" + totalScore +
                ", level=" + level +
                ", priority=" + priority +
                ", recommendedTests=" + recommendedTestCount +
                ", strategies=" + enabledStrategies.size() +
                ", scenarios=" + enabledScenarios.size() +
                ", riskScore=" + String.format("%.2f", riskScore) +
                ", confidence=" + String.format("%.2f", confidenceScore) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EndpointComplexity that = (EndpointComplexity) o;
        return totalScore == that.totalScore &&
                level == that.level &&
                Objects.equals(reasoning, that.reasoning);
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalScore, level, reasoning);
    }

    // ===== INNER CLASSES =====

    public static class ComplexityReason {
        private final String category;
        private final String reason;
        private final int impact;

        public ComplexityReason(String category, String reason, int impact) {
            this.category = category;
            this.reason = reason;
            this.impact = impact;
        }

        public String getCategory() {
            return category;
        }

        public String getReason() {
            return reason;
        }

        public int getImpact() {
            return impact;
        }

        @Override
        public String toString() {
            return String.format("%s: %s (impact: %d)", category, reason, impact);
        }
    }

    public static class ComplexityMetric {
        private final int score;
        private final String description;
        private final List<String> factors;

        public ComplexityMetric(int score, String description, List<String> factors) {
            this.score = score;
            this.description = description;
            this.factors = new ArrayList<>(factors);
        }

        public int getScore() {
            return score;
        }

        public String getDescription() {
            return description;
        }

        public List<String> getFactors() {
            return new ArrayList<>(factors);
        }
    }

    public static class ComplexityAnalysisReport {
        private final EndpointComplexity complexity;
        private final Map<String, Object> analysisData;

        public ComplexityAnalysisReport(EndpointComplexity complexity) {
            this.complexity = complexity;
            this.analysisData = generateAnalysisData();
        }

        private Map<String, Object> generateAnalysisData() {
            Map<String, Object> data = new HashMap<>();
            data.put("totalScore", complexity.totalScore);
            data.put("level", complexity.level != null ? complexity.level.name() : "UNKNOWN");
            data.put("confidence", complexity.confidenceScore);
            data.put("riskScore", complexity.riskScore);
            data.put("recommendedTests", complexity.recommendedTestCount);
            data.put("priority", complexity.priority != null ? complexity.priority.name() : "MEDIUM");
            data.put("strategies", complexity.enabledStrategies.stream()
                    .map(Enum::name)
                    .collect(Collectors.toList()));
            data.put("scenarios", complexity.enabledScenarios.stream()
                    .map(Enum::name)
                    .collect(Collectors.toList()));
            data.put("riskFactors", complexity.riskFactors.stream()
                    .map(Enum::name)
                    .collect(Collectors.toList()));
            return data;
        }

        public EndpointComplexity getComplexity() {
            return complexity;
        }

        public Map<String, Object> getAnalysisData() {
            return new HashMap<>(analysisData);
        }
    }

    public static class TestStrategyRecommendation {
        private final EndpointComplexity complexity;
        private final List<StrategyItem> strategies;

        public TestStrategyRecommendation(EndpointComplexity complexity) {
            this.complexity = complexity;
            this.strategies = generateStrategies();
        }

        private List<StrategyItem> generateStrategies() {
            List<StrategyItem> items = new ArrayList<>();

            for (TestStrategy strategy : complexity.recommendedStrategies) {
                int testCount = complexity.testTypeDistribution.getOrDefault(
                        strategy.name().toLowerCase(), 3);
                String rationale = generateRationale(strategy);

                items.add(new StrategyItem(strategy, testCount, rationale));
            }

            return items;
        }

        private String generateRationale(TestStrategy strategy) {
            switch (strategy) {
                case SECURITY_TESTING:
                    return "Security complexity: " + complexity.securityComplexity;
                case PERFORMANCE_TESTING:
                    return "High complexity level: " + (complexity.level != null ? complexity.level : "UNKNOWN");
                case BOUNDARY_TESTING:
                    return "Parameter complexity: " + complexity.parameterComplexity;
                case ERROR_HANDLING:
                    return "Error handling complexity: " + complexity.errorHandlingComplexity;
                case INTEGRATION_TESTING:
                    return "Business logic complexity: " + complexity.businessLogicComplexity;
                case FUZZ_TESTING:
                    return "High security risk factors detected";
                case LOAD_TESTING:
                    return "High traffic risk factor present";
                case REGRESSION_TESTING:
                    return "Critical business process identified";
                default:
                    return "Recommended based on overall complexity";
            }
        }

        public List<StrategyItem> getStrategies() {
            return new ArrayList<>(strategies);
        }

        public int getTotalTestCount() {
            return strategies.stream().mapToInt(StrategyItem::getTestCount).sum();
        }

        public static class StrategyItem {
            private final TestStrategy strategy;
            private final int testCount;
            private final String rationale;

            public StrategyItem(TestStrategy strategy, int testCount, String rationale) {
                this.strategy = strategy;
                this.testCount = testCount;
                this.rationale = rationale;
            }

            public TestStrategy getStrategy() {
                return strategy;
            }

            public int getTestCount() {
                return testCount;
            }

            public String getRationale() {
                return rationale;
            }

            @Override
            public String toString() {
                return String.format("%s: %d tests (%s)",
                        strategy.name(), testCount, rationale);
            }
        }
    }
}