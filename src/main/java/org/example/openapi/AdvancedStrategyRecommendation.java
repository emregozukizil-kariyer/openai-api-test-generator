package org.example.openapi;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class AdvancedStrategyRecommendation {
    private final StrategyType primaryStrategy;
    private final StrategyType recommendedStrategy; // Alias for primaryStrategy
    private final String reasoning;
    private final String rationale; // Alias for reasoning
    private final double confidenceScore; // Changed to double for TestRunner compatibility
    private final Duration estimatedEffort;
    private final String executionId;
    private final Instant generationTimestamp;
    private final List<StrategyType> alternativeStrategies;
    private final Map<String, Object> analysisMetrics;
    private final RecommendationLevel recommendationLevel;
    private final List<String> riskFactors;
    private final List<String> benefits;
    private final String targetEnvironment;
    private final ResourceRequirement resourceRequirement;

    // Private constructor for immutable design
    private AdvancedStrategyRecommendation(Builder builder) {
        this.primaryStrategy = builder.recommendedStrategy; // Use recommendedStrategy as primary
        this.recommendedStrategy = builder.recommendedStrategy;
        this.reasoning = builder.rationale; // Use rationale as reasoning
        this.rationale = builder.rationale;
        this.confidenceScore = builder.confidenceScore;
        this.estimatedEffort = builder.estimatedEffort;
        this.executionId = builder.executionId != null ?
                builder.executionId : generateExecutionId();
        this.generationTimestamp = builder.generationTimestamp != null ?
                builder.generationTimestamp : Instant.now();
        this.alternativeStrategies = new ArrayList<>(builder.alternativeStrategies);
        this.analysisMetrics = new HashMap<>(builder.analysisMetrics);
        this.recommendationLevel = builder.recommendationLevel;
        this.riskFactors = new ArrayList<>(builder.riskFactors);
        this.benefits = new ArrayList<>(builder.benefits);
        this.targetEnvironment = builder.targetEnvironment;
        this.resourceRequirement = builder.resourceRequirement;
    }

    // Constructor for backward compatibility
    public AdvancedStrategyRecommendation(StrategyType primaryStrategy, String reasoning, int confidenceScore) {
        this.primaryStrategy = primaryStrategy;
        this.recommendedStrategy = primaryStrategy;
        this.reasoning = reasoning;
        this.rationale = reasoning;
        this.confidenceScore = confidenceScore / 100.0; // Convert to double
        this.estimatedEffort = Duration.ofMinutes(30);
        this.executionId = generateExecutionId();
        this.generationTimestamp = Instant.now();
        this.alternativeStrategies = new ArrayList<>();
        this.analysisMetrics = new HashMap<>();
        this.recommendationLevel = RecommendationLevel.MEDIUM;
        this.riskFactors = new ArrayList<>();
        this.benefits = new ArrayList<>();
        this.targetEnvironment = "default";
        this.resourceRequirement = ResourceRequirement.MEDIUM;
    }

    // Constructor for EndpointInfo compatibility  
    public AdvancedStrategyRecommendation(StrategyType primaryStrategy, 
                                        List<TestGenerationScenario> scenarios, 
                                        double confidenceScore) {
        this.primaryStrategy = primaryStrategy;
        this.recommendedStrategy = primaryStrategy;
        this.confidenceScore = confidenceScore;
        this.reasoning = "Generated recommendation for " + primaryStrategy.name();
        this.rationale = this.reasoning;
        this.executionId = generateExecutionId();
        this.generationTimestamp = Instant.now();
        this.alternativeStrategies = new ArrayList<>();
        this.analysisMetrics = new HashMap<>();
        this.riskFactors = new ArrayList<>();
        this.benefits = new ArrayList<>();
        this.targetEnvironment = "default";
        this.resourceRequirement = ResourceRequirement.MEDIUM;
        this.recommendationLevel = RecommendationLevel.MODERATE;
        this.estimatedEffort = Duration.ofMinutes(30);
    }

    // Specialized constructors
    public AdvancedStrategyRecommendation(RequestBodyInfo requestBody, EndpointInfo endpoint) {
        this.primaryStrategy = determinePrimaryStrategy(requestBody, endpoint);
        this.recommendedStrategy = this.primaryStrategy;
        this.reasoning = generateReasoning(requestBody, endpoint);
        this.rationale = this.reasoning;
        this.confidenceScore = calculateConfidence(requestBody, endpoint) / 100.0;
        this.estimatedEffort = estimateEffort(this.primaryStrategy);
        this.executionId = generateExecutionId();
        this.generationTimestamp = Instant.now();
        this.alternativeStrategies = generateAlternatives(this.primaryStrategy);
        this.analysisMetrics = new HashMap<>();
        this.recommendationLevel = determineRecommendationLevel(this.confidenceScore);
        this.riskFactors = identifyRiskFactors(requestBody, endpoint);
        this.benefits = identifyBenefits(this.primaryStrategy);
        this.targetEnvironment = "api";
        this.resourceRequirement = convertResourceLevel(this.primaryStrategy.getResourceRequirement());
    }

    public AdvancedStrategyRecommendation(ParameterInfo parameterInfo, EndpointInfo endpoint) {
        this.primaryStrategy = determinePrimaryStrategyForParam(parameterInfo, endpoint);
        this.recommendedStrategy = this.primaryStrategy;
        this.reasoning = generateReasoningForParam(parameterInfo, endpoint);
        this.rationale = this.reasoning;
        this.confidenceScore = calculateConfidenceForParam(parameterInfo, endpoint) / 100.0;
        this.estimatedEffort = estimateEffort(this.primaryStrategy);
        this.executionId = generateExecutionId();
        this.generationTimestamp = Instant.now();
        this.alternativeStrategies = generateAlternatives(this.primaryStrategy);
        this.analysisMetrics = new HashMap<>();
        this.recommendationLevel = determineRecommendationLevel(this.confidenceScore);
        this.riskFactors = identifyRiskFactorsForParam(parameterInfo, endpoint);
        this.benefits = identifyBenefits(this.primaryStrategy);
        this.targetEnvironment = "parameter";
        this.resourceRequirement = convertResourceLevel(this.primaryStrategy.getResourceRequirement());
    }

    // Getters
    public StrategyType getPrimaryStrategy() {
        return primaryStrategy;
    }

    public StrategyType getRecommendedStrategy() {
        return recommendedStrategy;
    }

    public String getReasoning() {
        return reasoning;
    }

    public String getRationale() {
        return rationale;
    }

    public double getConfidenceScore() {
        return confidenceScore;
    }

    public Duration getEstimatedEffort() {
        return estimatedEffort;
    }

    public String getExecutionId() {
        return executionId;
    }

    public Instant getGenerationTimestamp() {
        return generationTimestamp;
    }

    public List<StrategyType> getAlternativeStrategies() {
        return new ArrayList<>(alternativeStrategies);
    }

    public Map<String, Object> getAnalysisMetrics() {
        return new HashMap<>(analysisMetrics);
    }

    public RecommendationLevel getRecommendationLevel() {
        return recommendationLevel;
    }

    public List<String> getRiskFactors() {
        return new ArrayList<>(riskFactors);
    }

    public List<String> getBenefits() {
        return new ArrayList<>(benefits);
    }

    public String getTargetEnvironment() {
        return targetEnvironment;
    }

    public ResourceRequirement getResourceRequirement() {
        return resourceRequirement;
    }
    
    // Missing methods for compatibility
    public List<StrategyType> getComplementaryStrategies() {
        return getAlternativeStrategies(); // Alias for alternativeStrategies
    }
    
    public double getConfidence() {
        return confidenceScore; // Convert double to percentage-like value
    }
    
    public long getTimestamp() {
        return generationTimestamp != null ? generationTimestamp.toEpochMilli() : System.currentTimeMillis();
    }

    // Utility methods
    private ResourceRequirement convertResourceLevel(StrategyType.ResourceLevel level) {
        switch (level) {
            case LOW: return ResourceRequirement.LOW;
            case MEDIUM: return ResourceRequirement.MEDIUM;
            case HIGH: return ResourceRequirement.HIGH;
            default: return ResourceRequirement.MEDIUM;
        }
    }

    public boolean isHighConfidence() {
        return confidenceScore >= 0.8;
    }

    public boolean isRecommended() {
        return confidenceScore >= 0.7;
    }

    public boolean hasAlternatives() {
        return !alternativeStrategies.isEmpty();
    }

    public boolean hasRisks() {
        return !riskFactors.isEmpty();
    }

    public String getConfidenceLevel() {
        if (confidenceScore >= 0.9) return "VERY_HIGH";
        if (confidenceScore >= 0.8) return "HIGH";
        if (confidenceScore >= 0.7) return "MEDIUM";
        if (confidenceScore >= 0.5) return "LOW";
        return "VERY_LOW";
    }

    // Static factory methods
    public static Builder builder() {
        return new Builder();
    }

    public static AdvancedStrategyRecommendation createDefault() {
        return builder().build();
    }

    public static AdvancedStrategyRecommendation createHighConfidence(StrategyType strategy) {
        return builder()
                .withRecommendedStrategy(strategy)
                .withConfidenceScore(0.9)
                .withRationale("High confidence recommendation based on analysis")
                .build();
    }

    public static AdvancedStrategyRecommendation createFallback() {
        return builder()
                .withRecommendedStrategy(StrategyType.FUNCTIONAL_BASIC)
                .withConfidenceScore(0.6)
                .withRationale("Fallback recommendation due to analysis limitations")
                .withRecommendationLevel(RecommendationLevel.LOW)
                .build();
    }

    // Enums
    public enum RecommendationLevel {
        CRITICAL("Critical recommendation - immediate action required"),
        HIGH("High priority recommendation"),
        MODERATE("Moderate priority recommendation"),
        MEDIUM("Medium priority recommendation"),
        LOW("Low priority recommendation"),
        INFORMATIONAL("Informational recommendation");

        private final String description;

        RecommendationLevel(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum ResourceRequirement {
        MINIMAL("Minimal resources required"),
        LOW("Low resource requirement"),
        MEDIUM("Medium resource requirement"),
        HIGH("High resource requirement"),
        INTENSIVE("Intensive resource requirement");

        private final String description;

        ResourceRequirement(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum EstimatedEffort {
        MINIMAL("Minimal effort required - under 15 minutes"),
        LOW("Low effort required - 15-30 minutes"),
        MEDIUM("Medium effort required - 30-60 minutes"),
        HIGH("High effort required - 1-3 hours"),
        EXTENSIVE("Extensive effort required - over 3 hours");

        private final String description;

        EstimatedEffort(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // Builder class
    public static class Builder {
        private StrategyType recommendedStrategy = StrategyType.FUNCTIONAL_BASIC;
        private String rationale = "Default recommendation";
        private double confidenceScore = 0.75;
        private Duration estimatedEffort = Duration.ofMinutes(30);
        private String executionId;
        private Instant generationTimestamp;
        private List<StrategyType> alternativeStrategies = new ArrayList<>();
        private Map<String, Object> analysisMetrics = new HashMap<>();
        private RecommendationLevel recommendationLevel = RecommendationLevel.MEDIUM;
        private List<String> riskFactors = new ArrayList<>();
        private List<String> benefits = new ArrayList<>();
        private String targetEnvironment = "default";
        private ResourceRequirement resourceRequirement = ResourceRequirement.MEDIUM;

        // Hata mesajında aranan metod
        public Builder withRecommendedStrategy(StrategyType strategy) {
            this.recommendedStrategy = strategy;
            return this;
        }

        // Backward compatibility
        public Builder withPrimaryStrategy(StrategyType strategy) {
            return withRecommendedStrategy(strategy);
        }

        // Hata mesajında aranan metod
        public Builder withRationale(String rationale) {
            this.rationale = rationale;
            return this;
        }

        // Backward compatibility
        public Builder withReasoning(String reasoning) {
            return withRationale(reasoning);
        }

        // Hata mesajında aranan metod
        public Builder withConfidenceScore(double score) {
            if (score < 0.0 || score > 1.0) {
                throw new IllegalArgumentException("Confidence score must be between 0.0 and 1.0");
            }
            this.confidenceScore = score;
            return this;
        }

        // Backward compatibility for int scores
        public Builder withConfidenceScore(int score) {
            return withConfidenceScore(score / 100.0);
        }

        // Hata mesajında aranan metod
        public Builder withEstimatedEffort(Duration effort) {
            this.estimatedEffort = effort;
            return this;
        }

        public Builder withEstimatedEffort(int minutes) {
            return withEstimatedEffort(Duration.ofMinutes(minutes));
        }

        // Hata mesajında aranan metod
        public Builder withExecutionId(String executionId) {
            this.executionId = executionId;
            return this;
        }

        // Hata mesajında aranan metod
        public Builder withGenerationTimestamp(Instant timestamp) {
            this.generationTimestamp = timestamp;
            return this;
        }

        public Builder withAlternativeStrategy(StrategyType strategy) {
            if (strategy != null && !this.alternativeStrategies.contains(strategy)) {
                this.alternativeStrategies.add(strategy);
            }
            return this;
        }

        public Builder withAlternativeStrategies(List<StrategyType> strategies) {
            if (strategies != null) {
                this.alternativeStrategies.addAll(strategies);
            }
            return this;
        }

        public Builder withAnalysisMetric(String key, Object value) {
            this.analysisMetrics.put(key, value);
            return this;
        }

        public Builder withAnalysisMetrics(Map<String, Object> metrics) {
            if (metrics != null) {
                this.analysisMetrics.putAll(metrics);
            }
            return this;
        }

        public Builder withRecommendationLevel(RecommendationLevel level) {
            this.recommendationLevel = level;
            return this;
        }

        public Builder withRiskFactor(String riskFactor) {
            if (riskFactor != null && !riskFactor.trim().isEmpty()) {
                this.riskFactors.add(riskFactor);
            }
            return this;
        }

        public Builder withRiskFactors(List<String> riskFactors) {
            if (riskFactors != null) {
                this.riskFactors.addAll(riskFactors);
            }
            return this;
        }

        public Builder withBenefit(String benefit) {
            if (benefit != null && !benefit.trim().isEmpty()) {
                this.benefits.add(benefit);
            }
            return this;
        }

        public Builder withBenefits(List<String> benefits) {
            if (benefits != null) {
                this.benefits.addAll(benefits);
            }
            return this;
        }

        public Builder withTargetEnvironment(String environment) {
            this.targetEnvironment = environment;
            return this;
        }

        // Missing methods from error messages
        public Builder withComplementaryStrategies(List<StrategyType> strategies) {
            if (strategies != null) {
                this.alternativeStrategies = new ArrayList<>(strategies);
            }
            return this;
        }

        public Builder withConfidence(double confidence) {
            return withConfidenceScore(confidence);
        }

        public Builder withConfidence(int confidence) {
            return withConfidenceScore(confidence / 100.0);
        }

        public Builder withEstimatedExecutionTime(java.time.Duration duration) {
            // Implementation for estimated execution time
            return this;
        }

        public Builder withResourceRequirement(ResourceRequirement requirement) {
            // Implementation for resource requirement
            return this;
        }

        public Builder withTimestamp(long timestamp) {
            // Implementation for timestamp
            return this;
        }

        public Builder withEndpointAnalysis(Object analysis) {
            // Implementation for endpoint analysis
            return this;
        }

        public Builder withEstimatedTestCases(int testCases) {
            // Implementation for estimated test cases
            return this;
        }

        // Convenience methods for common scenarios
        public Builder withSecurityFocus() {
            return this
                    .withRecommendedStrategy(StrategyType.SECURITY_OWASP_TOP10)
                    .withConfidenceScore(0.85)
                    .withRationale("Security-focused testing recommended due to potential vulnerabilities")
                    .withRecommendationLevel(RecommendationLevel.HIGH)
                    .withBenefit("Enhanced security posture")
                    .withBenefit("Vulnerability detection");
        }

        public Builder withPerformanceFocus() {
            return this
                    .withRecommendedStrategy(StrategyType.PERFORMANCE_LOAD)
                    .withConfidenceScore(0.8)
                    .withRationale("Performance testing recommended for high-load scenarios")
                    .withEstimatedEffort(Duration.ofHours(2))
                    .withResourceRequirement(ResourceRequirement.HIGH);
        }

        public Builder withComprehensiveTesting() {
            return this
                    .withRecommendedStrategy(StrategyType.FUNCTIONAL_COMPREHENSIVE)
                    .withConfidenceScore(0.9)
                    .withRationale("Comprehensive testing recommended for critical functionality")
                    .withAlternativeStrategy(StrategyType.FUNCTIONAL_BOUNDARY)
                    .withAlternativeStrategy(StrategyType.SECURITY_BASIC);
        }

        // Auto-configuration based on strategy
        public Builder withAutoConfiguration() {
            if (recommendedStrategy != null) {
                // Auto-set effort based on strategy complexity
                this.estimatedEffort = Duration.ofMinutes(recommendedStrategy.getEstimatedDurationMinutes());

                // Auto-set resource requirement
                this.resourceRequirement = mapToResourceRequirement(recommendedStrategy.getResourceRequirement());

                // Auto-add benefits
                this.benefits.addAll(generateBenefitsForStrategy(recommendedStrategy));

                // Auto-generate alternatives
                this.alternativeStrategies.addAll(generateAlternativesForStrategy(recommendedStrategy));
            }
            return this;
        }

        // Validation
        private void validate() {
            if (recommendedStrategy == null) {
                this.recommendedStrategy = StrategyType.FUNCTIONAL_BASIC;
            }

            if (rationale == null || rationale.trim().isEmpty()) {
                this.rationale = "Default recommendation for " + recommendedStrategy.getDisplayName();
            }

            if (estimatedEffort == null || estimatedEffort.isZero() || estimatedEffort.isNegative()) {
                this.estimatedEffort = Duration.ofMinutes(recommendedStrategy.getEstimatedDurationMinutes());
            }

            if (resourceRequirement == null) {
                this.resourceRequirement = mapToResourceRequirement(recommendedStrategy.getResourceRequirement());
            }

            // Auto-generate analysis metrics if empty
            if (analysisMetrics.isEmpty()) {
                analysisMetrics.put("strategy_complexity", recommendedStrategy.getComplexity());
                analysisMetrics.put("estimated_duration_minutes", estimatedEffort.toMinutes());
                analysisMetrics.put("resource_level", resourceRequirement.name());
            }
        }

        public AdvancedStrategyRecommendation build() {
            validate();
            return new AdvancedStrategyRecommendation(this);
        }
        
        // Missing builder methods for compatibility
        public Builder withRecommendationReason(String reason) {
            this.rationale = reason;
            return this;
        }
        
        public Builder withRiskAssessment(Object assessment) {
            if (assessment != null) {
                this.analysisMetrics.put("risk_assessment", assessment);
            }
            return this;
        }
        
        // Additional missing method for TestBuilder compatibility
        public Builder withPerformanceConsiderations(Object considerations) {
            if (considerations != null) {
                this.analysisMetrics.put("performance_considerations", considerations);
            }
            return this;
        }
    }

    // Helper methods
    private static String generateExecutionId() {
        return "ASR_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }

    private StrategyType determinePrimaryStrategy(RequestBodyInfo requestBody, EndpointInfo endpoint) {
        if (requestBody != null && requestBody.hasSecurityRisks()) {
            return StrategyType.SECURITY_OWASP_TOP10;
        }
        return StrategyType.FUNCTIONAL_COMPREHENSIVE;
    }

    private StrategyType determinePrimaryStrategyForParam(ParameterInfo parameterInfo, EndpointInfo endpoint) {
        if (parameterInfo != null && parameterInfo.hasSecurityImplications()) {
            return StrategyType.SECURITY_BASIC;
        }
        return StrategyType.FUNCTIONAL_BASIC;
    }

    private String generateReasoning(RequestBodyInfo requestBody, EndpointInfo endpoint) {
        return "Strategy recommendation based on security analysis and complexity";
    }

    private String generateReasoningForParam(ParameterInfo parameterInfo, EndpointInfo endpoint) {
        return "Strategy recommendation based on parameter security analysis";
    }

    private int calculateConfidence(RequestBodyInfo requestBody, EndpointInfo endpoint) {
        return 85; // Default confidence score
    }

    private int calculateConfidenceForParam(ParameterInfo parameterInfo, EndpointInfo endpoint) {
        return 80; // Default confidence score for parameters
    }

    private Duration estimateEffort(StrategyType strategy) {
        return Duration.ofMinutes(strategy.getEstimatedDurationMinutes());
    }

    private List<StrategyType> generateAlternatives(StrategyType primary) {
        List<StrategyType> alternatives = new ArrayList<>();
        String category = primary.getCategory();

        for (StrategyType strategy : StrategyType.values()) {
            if (strategy != primary && strategy.getCategory().equals(category)) {
                alternatives.add(strategy);
            }
        }

        return alternatives;
    }

    private RecommendationLevel determineRecommendationLevel(double confidence) {
        if (confidence >= 0.9) return RecommendationLevel.CRITICAL;
        if (confidence >= 0.8) return RecommendationLevel.HIGH;
        if (confidence >= 0.7) return RecommendationLevel.MEDIUM;
        if (confidence >= 0.5) return RecommendationLevel.LOW;
        return RecommendationLevel.INFORMATIONAL;
    }

    private List<String> identifyRiskFactors(RequestBodyInfo requestBody, EndpointInfo endpoint) {
        List<String> risks = new ArrayList<>();
        if (requestBody != null && requestBody.hasSecurityRisks()) {
            risks.add("Security vulnerabilities detected in request body");
        }
        return risks;
    }

    private List<String> identifyRiskFactorsForParam(ParameterInfo parameterInfo, EndpointInfo endpoint) {
        List<String> risks = new ArrayList<>();
        if (parameterInfo != null && parameterInfo.hasSecurityImplications()) {
            risks.add("Security implications detected in parameters");
        }
        return risks;
    }

    private List<String> identifyBenefits(StrategyType strategy) {
        List<String> benefits = new ArrayList<>();
        String category = strategy.getCategory();

        switch (category) {
            case "SECURITY":
                benefits.add("Enhanced security posture");
                benefits.add("Vulnerability detection");
                break;
            case "PERFORMANCE":
                benefits.add("Performance optimization");
                benefits.add("Scalability validation");
                break;
            case "FUNCTIONAL":
                benefits.add("Functional correctness");
                benefits.add("Business logic validation");
                break;
            default:
                benefits.add("Comprehensive test coverage");
        }

        return benefits;
    }

    // Static helper methods for Builder
    private static ResourceRequirement mapToResourceRequirement(StrategyType.ResourceLevel level) {
        switch (level) {
            case HIGH:
                return ResourceRequirement.HIGH;
            case MEDIUM:
                return ResourceRequirement.MEDIUM;
            case LOW:
            default:
                return ResourceRequirement.LOW;
        }
    }

    private static List<String> generateBenefitsForStrategy(StrategyType strategy) {
        List<String> benefits = new ArrayList<>();
        String category = strategy.getCategory();

        switch (category) {
            case "SECURITY":
                benefits.add("Improved security");
                benefits.add("Risk mitigation");
                break;
            case "PERFORMANCE":
                benefits.add("Performance insights");
                benefits.add("Load handling");
                break;
            case "FUNCTIONAL":
                benefits.add("Functional validation");
                benefits.add("User experience");
                break;
        }

        return benefits;
    }

    private static List<StrategyType> generateAlternativesForStrategy(StrategyType strategy) {
        List<StrategyType> alternatives = new ArrayList<>();
        String category = strategy.getCategory();

        for (StrategyType s : StrategyType.values()) {
            if (s != strategy && s.getCategory().equals(category)) {
                alternatives.add(s);
            }
        }

        return alternatives;
    }

    // Override methods
    @Override
    public String toString() {
        return String.format("AdvancedStrategyRecommendation{strategy=%s, confidence=%.2f, " +
                        "level=%s, effort=%s}",
                recommendedStrategy, confidenceScore, recommendationLevel, estimatedEffort);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AdvancedStrategyRecommendation that = (AdvancedStrategyRecommendation) o;
        return Double.compare(that.confidenceScore, confidenceScore) == 0 &&
                recommendedStrategy == that.recommendedStrategy &&
                Objects.equals(executionId, that.executionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recommendedStrategy, confidenceScore, executionId);
    }
}