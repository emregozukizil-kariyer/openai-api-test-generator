package org.example.openapi;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class GeneratedTestCase {
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
    private Instant creationTimestamp;
    private Instant generationTimestamp;
    private String testCode;
    private Duration executionDuration;
    private String executionStatus = "PENDING";
    private Map<String, Object> metadata = new HashMap<>();


    // Constructor
    private GeneratedTestCase() {
        this.creationTimestamp = Instant.now();
        this.testSteps = new ArrayList<>();
        this.assertions = new ArrayList<>();
        this.tags = new HashSet<>();
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final GeneratedTestCase testCase;

        private Builder() {
            this.testCase = new GeneratedTestCase();
        }

        public Builder withTestId(String testId) {
            testCase.testId = testId;
            return this;
        }

        public Builder withTestName(String testName) {
            testCase.testName = testName;
            return this;
        }

        public Builder withDescription(String description) {
            testCase.description = description;
            return this;
        }

        public Builder withScenario(TestGenerationScenario scenario) {
            testCase.scenario = scenario;
            return this;
        }

        public Builder withStrategyType(StrategyType strategyType) {
            testCase.strategyType = strategyType;
            return this;
        }

        public Builder withEndpoint(EndpointInfo endpoint) {
            testCase.endpoint = endpoint;
            return this;
        }

        public Builder withTestSteps(List<TestStep> testSteps) {
            testCase.testSteps = new ArrayList<>(testSteps);
            return this;
        }

        public Builder withTestData(TestDataSet testData) {
            testCase.testData = testData;
            return this;
        }

        public Builder withAssertions(List<TestAssertion> assertions) {
            testCase.assertions = new ArrayList<>(assertions);
            return this;
        }

        public Builder withPriority(int priority) {
            testCase.priority = priority;
            return this;
        }

        public Builder withEstimatedDuration(Duration estimatedDuration) {
            testCase.estimatedDuration = estimatedDuration;
            return this;
        }

        public Builder withComplexity(int complexity) {
            testCase.complexity = complexity;
            return this;
        }

        public Builder withTags(Set<String> tags) {
            testCase.tags = new HashSet<>(tags);
            return this;
        }

        public Builder withTestCode(String testCode) {
            testCase.testCode = testCode;
            return this;
        }

        public Builder withGenerationTimestamp(Instant timestamp) {
            testCase.generationTimestamp = timestamp;
            return this;
        }

        public Instant getGenerationTimestamp() {
            return testCase.generationTimestamp;
        }

        public Builder withExecutionStatus(org.example.openapi.TestRunner.TestStatus status) {
            if (status != null) {
                testCase.executionStatus = status.name();
            }
            return this;
        }

        public Builder withExecutionDuration(Duration duration) {
            testCase.executionDuration = duration;
            return this;
        }
        
        // Additional missing methods for TestRunner compatibility
        public Builder withFailureMessages(List<String> messages) {
            // Store failure messages in metadata for now
            if (testCase.metadata == null) {
                testCase.metadata = new HashMap<>();
            }
            testCase.metadata.put("failureMessages", messages);
            return this;
        }
        
        public Builder withSkipReason(String reason) {
            // Store skip reason in metadata
            if (testCase.metadata == null) {
                testCase.metadata = new HashMap<>();
            }
            testCase.metadata.put("skipReason", reason);
            return this;
        }

        public GeneratedTestCase build() {
            return testCase;
        }
    }

    public String getTestCode() { return testCode; }
    public void setTestCode(String testCode) { this.testCode = testCode; }

    public Duration getExecutionDuration() { return executionDuration; }
    public void setExecutionDuration(Duration executionDuration) {
        this.executionDuration = executionDuration;
    }

    public String getExecutionStatus() { return executionStatus; }
    public void setExecutionStatus(String executionStatus) {
        this.executionStatus = executionStatus;
    }

    // Getters
    public String getTestId() { return testId; }
    public String getTestName() { return testName; }
    public String getDescription() { return description; }
    public TestGenerationScenario getScenario() { return scenario; }
    public StrategyType getStrategyType() { return strategyType; }
    public EndpointInfo getEndpoint() { return endpoint; }
    public List<TestStep> getTestSteps() { return new ArrayList<>(testSteps); }
    public TestDataSet getTestData() { return testData; }
    public List<TestAssertion> getAssertions() { return new ArrayList<>(assertions); }
    public int getPriority() { return priority; }
    public Duration getEstimatedDuration() { return estimatedDuration; }
    public int getComplexity() { return complexity; }
    public Set<String> getTags() { return new HashSet<>(tags); }
    public Instant getCreationTimestamp() { return creationTimestamp; }

    // Metadata support methods
    public void addMetadata(String key, Object value) {
        if (metadata == null) {
            metadata = new HashMap<>();
        }
        metadata.put(key, value);
    }

    public Map<String, Object> getMetadata() {
        return metadata != null ? new HashMap<>(metadata) : new HashMap<>();
    }
}