package org.example.openapi;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ComprehensiveTestSuite {
    private final List<GeneratedTestCase> testCases;
    private final String suiteId;
    private final String description;
    private final Instant creationTimestamp;
    private final Instant generationTimestamp;
    private final String executionId;
    private final QualityMetrics qualityMetrics;
    private final SecurityProfile securityProfile;
    private final PerformanceProfile performanceProfile;
    private final ComplianceProfile complianceProfile;
    private final AdvancedStrategyRecommendation recommendation;
    private final AdvancedStrategyExecutionPlan executionPlan;
    private final Duration generationDuration;
    private final Exception error;

    // Private constructor for builder pattern
    private ComprehensiveTestSuite(Builder builder) {
        this.testCases = new ArrayList<>(builder.testCases);
        this.suiteId = builder.suiteId != null ? builder.suiteId : generateSuiteId();
        this.description = builder.description != null ? builder.description : "Generated Test Suite";
        this.creationTimestamp = builder.creationTimestamp != null ? builder.creationTimestamp : Instant.now();
        this.generationTimestamp = builder.generationTimestamp != null ? builder.generationTimestamp : Instant.now();
        this.executionId = builder.executionId != null ? builder.executionId : generateExecutionId();
        this.qualityMetrics = builder.qualityMetrics;
        this.securityProfile = builder.securityProfile;
        this.performanceProfile = builder.performanceProfile;
        this.complianceProfile = builder.complianceProfile;
        this.recommendation = builder.recommendation;
        this.executionPlan = builder.executionPlan;
        this.generationDuration = builder.generationDuration != null ? builder.generationDuration : Duration.ZERO;
        this.error = builder.error;
    }

    // Getters
    public List<GeneratedTestCase> getTestCases() {
        return new ArrayList<>(testCases);
    }

    public String getSuiteId() {
        return suiteId;
    }

    public String getDescription() {
        return description;
    }

    public Instant getCreationTimestamp() {
        return creationTimestamp;
    }

    public Instant getGenerationTimestamp() {
        return generationTimestamp;
    }

    public String getExecutionId() {
        return executionId;
    }

    public QualityMetrics getQualityMetrics() {
        return qualityMetrics;
    }

    public SecurityProfile getSecurityProfile() {
        return securityProfile;
    }

    public PerformanceProfile getPerformanceProfile() {
        return performanceProfile;
    }

    public ComplianceProfile getComplianceProfile() {
        return complianceProfile;
    }

    public AdvancedStrategyRecommendation getRecommendation() {
        return recommendation;
    }

    public AdvancedStrategyExecutionPlan getExecutionPlan() {
        return executionPlan;
    }

    public Duration getGenerationDuration() {
        return generationDuration;
    }

    public Exception getError() {
        return error;
    }

    public int getTestCaseCount() {
        return testCases.size();
    }

    public double getQualityScore() {
        return qualityMetrics != null ? qualityMetrics.getQualityScore() : 0.0;
    }

    public boolean hasError() {
        return error != null;
    }

    public boolean isSuccessful() {
        return error == null && !testCases.isEmpty();
    }

    // Utility methods
    public long getPassedTestCount() {
        return testCases.stream()
                .filter(tc -> "PASSED".equals(tc.getExecutionStatus()))
                .count();
    }

    public long getFailedTestCount() {
        return testCases.stream()
                .filter(tc -> "FAILED".equals(tc.getExecutionStatus()))
                .count();
    }

    public long getSkippedTestCount() {
        return testCases.stream()
                .filter(tc -> "SKIPPED".equals(tc.getExecutionStatus()))
                .count();
    }

    public double getSuccessRate() {
        if (testCases.isEmpty()) {
            return 0.0;
        }
        return (double) getPassedTestCount() / testCases.size();
    }

    // Static factory methods
    public static Builder builder() {
        return new Builder();
    }

    public static ComprehensiveTestSuite empty() {
        return builder().build();
    }

    public static ComprehensiveTestSuite failed(String reason, Exception error) {
        return builder()
                .withDescription("Failed test suite: " + reason)
                .withError(error)
                .build();
    }

    // Builder class
    public static class Builder {
        private List<GeneratedTestCase> testCases = new ArrayList<>();
        private String suiteId;
        private String description;
        private Instant creationTimestamp;
        private Instant generationTimestamp;
        private String executionId;
        private QualityMetrics qualityMetrics;
        private SecurityProfile securityProfile;
        private PerformanceProfile performanceProfile;
        private ComplianceProfile complianceProfile;
        private AdvancedStrategyRecommendation recommendation;
        private AdvancedStrategyExecutionPlan executionPlan;
        private Duration generationDuration;
        private Exception error;

        public Builder withSuiteId(String suiteId) {
            this.suiteId = suiteId;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withCreationTimestamp(Instant timestamp) {
            this.creationTimestamp = timestamp;
            return this;
        }

        public Builder withGenerationTimestamp(Instant timestamp) {
            this.generationTimestamp = timestamp;
            return this;
        }

        public Builder withExecutionId(String executionId) {
            this.executionId = executionId;
            return this;
        }

        public Builder withTestCase(GeneratedTestCase testCase) {
            if (testCase != null) {
                this.testCases.add(testCase);
            }
            return this;
        }

        public Builder withTestCases(List<GeneratedTestCase> testCases) {
            if (testCases != null) {
                this.testCases.addAll(testCases);
            }
            return this;
        }

        public Builder withQualityMetrics(QualityMetrics qualityMetrics) {
            this.qualityMetrics = qualityMetrics;
            return this;
        }

        public Builder withSecurityProfile(SecurityProfile securityProfile) {
            this.securityProfile = securityProfile;
            return this;
        }

        public Builder withPerformanceProfile(PerformanceProfile performanceProfile) {
            this.performanceProfile = performanceProfile;
            return this;
        }

        public Builder withComplianceProfile(ComplianceProfile complianceProfile) {
            this.complianceProfile = complianceProfile;
            return this;
        }

        public Builder withRecommendation(AdvancedStrategyRecommendation recommendation) {
            this.recommendation = recommendation;
            return this;
        }

        public Builder withExecutionPlan(AdvancedStrategyExecutionPlan executionPlan) {
            this.executionPlan = executionPlan;
            return this;
        }

        public Builder withGenerationDuration(Duration duration) {
            this.generationDuration = duration;
            return this;
        }

        public Builder withError(Exception error) {
            this.error = error;
            return this;
        }

        // Deprecated method for backward compatibility - replaced by withTestCases
        @Deprecated
        public Builder withEndpoint(EndpointInfo endpoint) {
            // This method is deprecated, use withTestCases instead
            return this;
        }

        // Deprecated method for backward compatibility - replaced by withTestCases
        @Deprecated
        public Builder withEndpoints(List<EndpointInfo> endpoints) {
            // This method is deprecated, use withTestCases instead
            return this;
        }

        // Getter for accessing current recommendation during building
        public AdvancedStrategyRecommendation getRecommendation() {
            return recommendation;
        }

        // Getter for accessing current test cases during building
        public List<GeneratedTestCase> getTestCases() {
            return new ArrayList<>(testCases);
        }

        // Validation method
        private void validate() {
            // Add validation logic if needed
            if (testCases.isEmpty() && error == null) {
                // Warning: empty test suite without error
            }
        }

        public ComprehensiveTestSuite build() {
            validate();
            return new ComprehensiveTestSuite(this);
        }
    }

    // Utility methods for ID generation
    private static String generateSuiteId() {
        return "TS_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }

    private static String generateExecutionId() {
        return "EX_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }

    // Override methods
    @Override
    public String toString() {
        return String.format("ComprehensiveTestSuite{suiteId='%s', testCases=%d, qualityScore=%.2f, successful=%s}",
                suiteId, testCases.size(), getQualityScore(), isSuccessful());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComprehensiveTestSuite that = (ComprehensiveTestSuite) o;
        return Objects.equals(suiteId, that.suiteId) &&
                Objects.equals(executionId, that.executionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(suiteId, executionId);
    }
}