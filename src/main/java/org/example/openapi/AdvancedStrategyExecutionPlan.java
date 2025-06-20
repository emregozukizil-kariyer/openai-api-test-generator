package org.example.openapi;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class AdvancedStrategyExecutionPlan {
    private final StrategyType strategy;
    private final Duration estimatedDuration;
    private final List<GeneratedTestCase> testCases;
    private final List<String> executionOrder;
    private final String parallelizationStrategy;
    private final Map<String, Object> resourceRequirements;
    private final String executionId;
    private final Instant generationTimestamp;
    private final ExecutionPhase[] executionPhases;
    private final Map<String, String> environmentVariables;
    private final List<String> dependencies;
    private final PriorityLevel priority;
    private final int maxRetries;
    private final Duration timeout;
    private final boolean allowParallelExecution;

    // Private constructor for immutable design
    private AdvancedStrategyExecutionPlan(Builder builder) {
        this.strategy = builder.strategy;
        this.estimatedDuration = builder.estimatedDuration;
        this.testCases = new ArrayList<>(builder.testCases);
        this.executionOrder = new ArrayList<>(builder.executionOrder);
        this.parallelizationStrategy = builder.parallelizationStrategy;
        this.resourceRequirements = new HashMap<>(builder.resourceRequirements);
        this.executionId = builder.executionId != null ?
                builder.executionId : generateExecutionId();
        this.generationTimestamp = builder.generationTimestamp != null ?
                builder.generationTimestamp : Instant.now();
        this.executionPhases = builder.executionPhases.toArray(new ExecutionPhase[0]);
        this.environmentVariables = new HashMap<>(builder.environmentVariables);
        this.dependencies = new ArrayList<>(builder.dependencies);
        this.priority = builder.priority;
        this.maxRetries = builder.maxRetries;
        this.timeout = builder.timeout;
        this.allowParallelExecution = builder.allowParallelExecution;
    }

    // Getters
    public StrategyType getStrategy() {
        return strategy;
    }

    public Duration getEstimatedDuration() {
        return estimatedDuration;
    }

    public List<GeneratedTestCase> getTestCases() {
        return new ArrayList<>(testCases);
    }

    public List<String> getExecutionOrder() {
        return new ArrayList<>(executionOrder);
    }

    public String getParallelizationStrategy() {
        return parallelizationStrategy;
    }

    public Map<String, Object> getResourceRequirements() {
        return new HashMap<>(resourceRequirements);
    }

    public String getExecutionId() {
        return executionId;
    }

    public Instant getGenerationTimestamp() {
        return generationTimestamp;
    }

    public ExecutionPhase[] getExecutionPhases() {
        return executionPhases.clone();
    }

    public Map<String, String> getEnvironmentVariables() {
        return new HashMap<>(environmentVariables);
    }

    public List<String> getDependencies() {
        return new ArrayList<>(dependencies);
    }

    public PriorityLevel getPriority() {
        return priority;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public Duration getTimeout() {
        return timeout;
    }

    public boolean isParallelExecutionAllowed() {
        return allowParallelExecution;
    }

    // Utility methods
    public int getTotalTestCount() {
        return testCases.size();
    }

    public long getEstimatedDurationMinutes() {
        return estimatedDuration.toMinutes();
    }

    public boolean hasTestCases() {
        return !testCases.isEmpty();
    }

    public boolean hasDependencies() {
        return !dependencies.isEmpty();
    }

    public List<GeneratedTestCase> getTestCasesByPriority(int priority) {
        return testCases.stream()
                .filter(tc -> tc.getPriority() == priority)
                .collect(Collectors.toList());
    }

    public List<GeneratedTestCase> getHighPriorityTestCases() {
        return getTestCasesByPriority(1);
    }

    public boolean isComplexExecution() {
        return estimatedDuration.toMinutes() > 30 ||
                testCases.size() > 100 ||
                executionPhases.length > 5;
    }

    public ResourceLevel getRequiredResourceLevel() {
        if (isComplexExecution()) {
            return ResourceLevel.HIGH;
        } else if (testCases.size() > 20 || estimatedDuration.toMinutes() > 10) {
            return ResourceLevel.MEDIUM;
        } else {
            return ResourceLevel.LOW;
        }
    }

    // Static factory methods
    public static Builder builder() {
        return new Builder();
    }

    public static AdvancedStrategyExecutionPlan createDefault() {
        return builder().build();
    }

    public static AdvancedStrategyExecutionPlan createQuick() {
        return builder()
                .withStrategy(StrategyType.FUNCTIONAL_BASIC)
                .withEstimatedDuration(Duration.ofMinutes(5))
                .withParallelizationStrategy("SEQUENTIAL")
                .withPriority(PriorityLevel.LOW)
                .build();
    }

    public static AdvancedStrategyExecutionPlan createComprehensive() {
        return builder()
                .withStrategy(StrategyType.FUNCTIONAL_COMPREHENSIVE)
                .withEstimatedDuration(Duration.ofHours(2))
                .withParallelizationStrategy("PARALLEL")
                .withPriority(PriorityLevel.HIGH)
                .withAllowParallelExecution(true)
                .withMaxRetries(3)
                .build();
    }

    // Enums
    public enum ExecutionPhase {
        PREPARATION("Preparation phase"),
        SETUP("Environment setup"),
        EXECUTION("Test execution"),
        VALIDATION("Result validation"),
        CLEANUP("Cleanup operations"),
        REPORTING("Report generation");

        private final String description;

        ExecutionPhase(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum PriorityLevel {
        CRITICAL(1, "Critical priority"),
        HIGH(2, "High priority"),
        MEDIUM(3, "Medium priority"),
        LOW(4, "Low priority"),
        BACKGROUND(5, "Background priority");

        private final int level;
        private final String description;

        PriorityLevel(int level, String description) {
            this.level = level;
            this.description = description;
        }

        public int getLevel() {
            return level;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum ResourceLevel {
        LOW, MEDIUM, HIGH
    }

    // Builder class
    public static class Builder {
        private StrategyType strategy = StrategyType.FUNCTIONAL_BASIC;
        private Duration estimatedDuration = Duration.ofMinutes(10);
        private List<GeneratedTestCase> testCases = new ArrayList<>();
        private List<String> executionOrder = new ArrayList<>();
        private String parallelizationStrategy = "SEQUENTIAL";
        private Map<String, Object> resourceRequirements = new HashMap<>();
        private String executionId;
        private Instant generationTimestamp;
        private List<ExecutionPhase> executionPhases = new ArrayList<>();
        private Map<String, String> environmentVariables = new HashMap<>();
        private List<String> dependencies = new ArrayList<>();
        private PriorityLevel priority = PriorityLevel.MEDIUM;
        private int maxRetries = 1;
        private Duration timeout = Duration.ofMinutes(30);
        private boolean allowParallelExecution = false;

        public Builder withStrategy(StrategyType strategy) {
            this.strategy = strategy;
            return this;
        }

        public Builder withEstimatedDuration(Duration duration) {
            this.estimatedDuration = duration;
            return this;
        }

        public Builder withEstimatedDuration(int minutes) {
            return withEstimatedDuration(Duration.ofMinutes(minutes));
        }

        // Ana method - GeneratedTestCase list için
        public Builder withGeneratedTestCases(List<GeneratedTestCase> testCases) {
            if (testCases != null) {
                this.testCases.clear();
                this.testCases.addAll(testCases);
            }
            return this;
        }

        // Object list için - type erasure problemini çözmek için farklı isim
        public Builder withRawTestCases(List<Object> testCases) {
            if (testCases != null) {
                this.testCases.clear();
                List<GeneratedTestCase> converted = testCases.stream()
                        .filter(obj -> obj instanceof GeneratedTestCase)
                        .map(obj -> (GeneratedTestCase) obj)
                        .collect(Collectors.toList());
                this.testCases.addAll(converted);
            }
            return this;
        }

        // Backward compatibility için - ana method'u alias et
        public Builder withTestCases(List<GeneratedTestCase> testCases) {
            return withGeneratedTestCases(testCases);
        }

        public Builder withTestCase(GeneratedTestCase testCase) {
            if (testCase != null) {
                this.testCases.add(testCase);
            }
            return this;
        }

        public Builder withExecutionOrder(List<String> executionOrder) {
            if (executionOrder != null) {
                this.executionOrder.clear();
                this.executionOrder.addAll(executionOrder);
            }
            return this;
        }

        public Builder withExecutionOrder(String... testIds) {
            this.executionOrder.clear();
            this.executionOrder.addAll(Arrays.asList(testIds));
            return this;
        }

        public Builder withParallelizationStrategy(String strategy) {
            this.parallelizationStrategy = strategy;
            return this;
        }

        public Builder withResourceRequirements(Map<String, Object> requirements) {
            if (requirements != null) {
                this.resourceRequirements.clear();
                this.resourceRequirements.putAll(requirements);
            }
            return this;
        }

        public Builder withResourceRequirement(String key, Object value) {
            this.resourceRequirements.put(key, value);
            return this;
        }

        public Builder withExecutionId(String executionId) {
            this.executionId = executionId;
            return this;
        }

        public Builder withGenerationTimestamp(Instant timestamp) {
            this.generationTimestamp = timestamp;
            return this;
        }

        public Builder withExecutionPhase(ExecutionPhase phase) {
            if (phase != null) {
                this.executionPhases.add(phase);
            }
            return this;
        }

        public Builder withExecutionPhases(ExecutionPhase... phases) {
            this.executionPhases.clear();
            this.executionPhases.addAll(Arrays.asList(phases));
            return this;
        }

        public Builder withEnvironmentVariable(String key, String value) {
            this.environmentVariables.put(key, value);
            return this;
        }

        public Builder withEnvironmentVariables(Map<String, String> variables) {
            if (variables != null) {
                this.environmentVariables.clear();
                this.environmentVariables.putAll(variables);
            }
            return this;
        }

        public Builder withDependency(String dependency) {
            if (dependency != null && !dependency.trim().isEmpty()) {
                this.dependencies.add(dependency);
            }
            return this;
        }

        public Builder withDependencies(List<String> dependencies) {
            if (dependencies != null) {
                this.dependencies.clear();
                this.dependencies.addAll(dependencies);
            }
            return this;
        }

        public Builder withPriority(PriorityLevel priority) {
            this.priority = priority;
            return this;
        }

        public Builder withMaxRetries(int maxRetries) {
            if (maxRetries < 0) {
                throw new IllegalArgumentException("Max retries cannot be negative");
            }
            this.maxRetries = maxRetries;
            return this;
        }

        public Builder withTimeout(Duration timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder withTimeout(int minutes) {
            return withTimeout(Duration.ofMinutes(minutes));
        }

        public Builder withAllowParallelExecution(boolean allow) {
            this.allowParallelExecution = allow;
            return this;
        }

        // Convenience methods for common configurations
        public Builder withQuickConfiguration() {
            return this
                    .withStrategy(StrategyType.FUNCTIONAL_BASIC)
                    .withEstimatedDuration(Duration.ofMinutes(5))
                    .withParallelizationStrategy("SEQUENTIAL")
                    .withPriority(PriorityLevel.LOW)
                    .withMaxRetries(1);
        }

        public Builder withComprehensiveConfiguration() {
            return this
                    .withStrategy(StrategyType.FUNCTIONAL_COMPREHENSIVE)
                    .withEstimatedDuration(Duration.ofHours(1))
                    .withParallelizationStrategy("PARALLEL")
                    .withPriority(PriorityLevel.HIGH)
                    .withAllowParallelExecution(true)
                    .withMaxRetries(3)
                    .withExecutionPhases(ExecutionPhase.values());
        }

        public Builder withSecurityConfiguration() {
            return this
                    .withStrategy(StrategyType.SECURITY_OWASP_TOP10)
                    .withEstimatedDuration(Duration.ofMinutes(45))
                    .withParallelizationStrategy("PARALLEL")
                    .withPriority(PriorityLevel.HIGH)
                    .withMaxRetries(2);
        }

        public Builder withPerformanceConfiguration() {
            return this
                    .withStrategy(StrategyType.PERFORMANCE_LOAD)
                    .withEstimatedDuration(Duration.ofHours(2))
                    .withParallelizationStrategy("PARALLEL")
                    .withPriority(PriorityLevel.HIGH)
                    .withAllowParallelExecution(true)
                    .withResourceRequirement("cpu", "high")
                    .withResourceRequirement("memory", "8GB");
        }

        // Auto-generation of execution order based on test cases
        public Builder withAutoGeneratedExecutionOrder() {
            this.executionOrder.clear();
            this.executionOrder.addAll(
                    testCases.stream()
                            .sorted(Comparator.comparingInt(GeneratedTestCase::getPriority))
                            .map(GeneratedTestCase::getTestId)
                            .collect(Collectors.toList())
            );
            return this;
        }

        // Validation
        private void validate() {
            if (strategy == null) {
                this.strategy = StrategyType.FUNCTIONAL_BASIC;
            }

            if (estimatedDuration == null || estimatedDuration.isZero() || estimatedDuration.isNegative()) {
                this.estimatedDuration = Duration.ofMinutes(10);
            }

            if (parallelizationStrategy == null || parallelizationStrategy.trim().isEmpty()) {
                this.parallelizationStrategy = "SEQUENTIAL";
            }

            if (priority == null) {
                this.priority = PriorityLevel.MEDIUM;
            }

            // Auto-generate execution order if not provided
            if (executionOrder.isEmpty() && !testCases.isEmpty()) {
                withAutoGeneratedExecutionOrder();
            }

            // Set default execution phases if none provided
            if (executionPhases.isEmpty()) {
                this.executionPhases.addAll(Arrays.asList(
                        ExecutionPhase.PREPARATION,
                        ExecutionPhase.EXECUTION,
                        ExecutionPhase.VALIDATION
                ));
            }

            // Adjust resource requirements based on strategy
            if (resourceRequirements.isEmpty()) {
                adjustResourceRequirements();
            }
        }

        private void adjustResourceRequirements() {
            // StrategyType'dan ResourceLevel almaya çalışıyoruz
            // Bu method'un StrategyType'da olduğunu varsayıyoruz
            try {
                // Strategy'den resource level alınmaya çalışılıyor ama bu method'un var olup olmadığını kontrol edelim
                Object resourceLevel = getStrategyResourceLevel();
                if (resourceLevel instanceof ResourceLevel) {
                    ResourceLevel level = (ResourceLevel) resourceLevel;
                    setResourceRequirementsByLevel(level);
                } else {
                    // Default olarak MEDIUM ayarla
                    setResourceRequirementsByLevel(ResourceLevel.MEDIUM);
                }
            } catch (Exception e) {
                // Hata durumunda default ayarları kullan
                setResourceRequirementsByLevel(ResourceLevel.MEDIUM);
            }
        }

        private Object getStrategyResourceLevel() {
            // StrategyType'da ResourceLevel field'ı olup olmadığını kontrol et
            // Şimdilik default döndürüyoruz
            switch (strategy.getComplexity()) {
                case 1:
                case 2:
                    return ResourceLevel.LOW;
                case 3:
                case 4:
                    return ResourceLevel.MEDIUM;
                case 5:
                default:
                    return ResourceLevel.HIGH;
            }
        }

        private void setResourceRequirementsByLevel(ResourceLevel level) {
            switch (level) {
                case HIGH:
                    resourceRequirement("threads", 16);
                    resourceRequirement("memory", "4GB");
                    resourceRequirement("cpu", "high");
                    break;
                case MEDIUM:
                    resourceRequirement("threads", 8);
                    resourceRequirement("memory", "2GB");
                    resourceRequirement("cpu", "medium");
                    break;
                case LOW:
                default:
                    resourceRequirement("threads", 4);
                    resourceRequirement("memory", "1GB");
                    resourceRequirement("cpu", "low");
                    break;
            }
        }

        private Builder resourceRequirement(String key, Object value) {
            this.resourceRequirements.put(key, value);
            return this;
        }

        public AdvancedStrategyExecutionPlan build() {
            validate();
            return new AdvancedStrategyExecutionPlan(this);
        }
    }

    // Helper methods
    private static String generateExecutionId() {
        return "ASEP_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }

    // Override methods
    @Override
    public String toString() {
        return String.format("AdvancedStrategyExecutionPlan{strategy=%s, duration=%s, " +
                        "testCases=%d, priority=%s, parallel=%s}",
                strategy, estimatedDuration, testCases.size(), priority, allowParallelExecution);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AdvancedStrategyExecutionPlan that = (AdvancedStrategyExecutionPlan) o;
        return Objects.equals(executionId, that.executionId) &&
                strategy == that.strategy &&
                Objects.equals(estimatedDuration, that.estimatedDuration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(executionId, strategy, estimatedDuration);
    }
}