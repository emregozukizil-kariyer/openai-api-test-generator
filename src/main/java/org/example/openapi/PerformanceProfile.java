package org.example.openapi;

public enum PerformanceProfile {
    LOW("Low performance requirements", 1.0, false),
    MEDIUM("Medium performance requirements", 2.0, false),
    HIGH("High performance requirements", 3.0, false),
    CRITICAL("Critical performance requirements", 4.0, true),

    // Hata mesajlarında aranan değerler
    FAST("Fast performance profile for quick tests", 1.5, false),
    STANDARD("Standard performance profile", 2.5, false),
    THOROUGH("Thorough performance testing", 3.5, true);

    private final String description;
    private final double performanceScore;
    private final boolean criticalPath;

    PerformanceProfile(String description, double performanceScore, boolean criticalPath) {
        this.description = description;
        this.performanceScore = performanceScore;
        this.criticalPath = criticalPath;
    }

    public String getDescription() {
        return description;
    }

    public double getPerformanceScore() {
        return performanceScore;
    }

    public boolean isCriticalPath() {
        return criticalPath;
    }

    // Hata mesajlarında aranan eksik metodlar
    public StrategyType getRecommendedStrategy() {
        switch(this) {
            case LOW:
            case FAST:
                return StrategyType.PERFORMANCE_BASIC;
            case MEDIUM:
            case STANDARD:
            case HIGH:
            case CRITICAL:
            case THOROUGH:
                return StrategyType.PERFORMANCE_STRESS;
            default:
                return StrategyType.PERFORMANCE_BASIC;
        }
    }

    public TestGenerationScenario getAssociatedScenario() {
        switch(this) {
            case LOW:
            case FAST:
                return TestGenerationScenario.LOAD_TESTING_LIGHT;
            case MEDIUM:
            case STANDARD:
                return TestGenerationScenario.LOAD_TESTING_HEAVY;
            case HIGH:
            case CRITICAL:
            case THOROUGH:
                return TestGenerationScenario.STRESS_TESTING;
            default:
                return TestGenerationScenario.LOAD_TESTING_LIGHT;
        }
    }

    // Ek faydalı metodlar
    public int getRecommendedConcurrentUsers() {
        switch(this) {
            case LOW:
            case FAST:
                return 10;
            case MEDIUM:
            case STANDARD:
                return 50;
            case HIGH:
                return 100;
            case CRITICAL:
            case THOROUGH:
                return 200;
            default:
                return 25;
        }
    }

    public int getTestDurationMinutes() {
        switch(this) {
            case LOW:
            case FAST:
                return 5;
            case MEDIUM:
            case STANDARD:
                return 15;
            case HIGH:
                return 30;
            case CRITICAL:
            case THOROUGH:
                return 60;
            default:
                return 10;
        }
    }

    public double getTargetResponseTimeMs() {
        switch(this) {
            case LOW:
                return 5000.0; // 5 seconds
            case FAST:
                return 1000.0; // 1 second
            case MEDIUM:
            case STANDARD:
                return 2000.0; // 2 seconds
            case HIGH:
                return 1500.0; // 1.5 seconds
            case CRITICAL:
            case THOROUGH:
                return 500.0; // 0.5 seconds
            default:
                return 2000.0;
        }
    }

    public double getTargetThroughputRps() {
        switch(this) {
            case LOW:
                return 10.0; // 10 requests per second
            case FAST:
                return 25.0;
            case MEDIUM:
            case STANDARD:
                return 50.0;
            case HIGH:
                return 100.0;
            case CRITICAL:
            case THOROUGH:
                return 200.0;
            default:
                return 50.0;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private PerformanceProfile profile = MEDIUM;
        private int performanceTestCount = 5;
        private int concurrentUsers = 25;
        private int testDurationMinutes = 10;

        public Builder withProfile(PerformanceProfile profile) {
            this.profile = profile;
            return this;
        }

        // Hata mesajında aranan metod
        public Builder withPerformanceTestCount(int performanceTestCount) {
            this.performanceTestCount = performanceTestCount;
            return this;
        }

        public Builder withConcurrentUsers(int concurrentUsers) {
            this.concurrentUsers = concurrentUsers;
            return this;
        }

        public Builder withTestDurationMinutes(int testDurationMinutes) {
            this.testDurationMinutes = testDurationMinutes;
            return this;
        }

        public PerformanceProfile build() {
            return profile;
        }

        // Builder'dan test parametrelerini almak için
        public int getPerformanceTestCount() {
            return performanceTestCount;
        }

        public int getConcurrentUsers() {
            return concurrentUsers;
        }

        public int getTestDurationMinutes() {
            return testDurationMinutes;
        }
        
        // Missing method for TestRunner compatibility
        public Builder withLoadTestingEnabled(boolean enabled) {
            // If load testing is enabled, use a more intensive profile
            if (enabled) {
                this.profile = PerformanceProfile.HIGH;
                this.concurrentUsers = Math.max(this.concurrentUsers, 50);
                this.testDurationMinutes = Math.max(this.testDurationMinutes, 15);
            } else {
                this.profile = PerformanceProfile.LOW;
            }
            return this;
        }
        
        // Additional missing method for TestRunner compatibility
        public Builder withExpectedThroughput(int throughput) {
            // Store expected throughput as a configuration parameter
            this.concurrentUsers = Math.max(this.concurrentUsers, throughput / 10); // Estimate concurrent users
            return this;
        }
    }

    // Performance profilleri karşılaştırma
    public boolean isMoreIntensiveThan(PerformanceProfile other) {
        return this.performanceScore > other.performanceScore;
    }

    public boolean isLessIntensiveThan(PerformanceProfile other) {
        return this.performanceScore < other.performanceScore;
    }

    // Resource requirements
    public ResourceLevel getResourceRequirement() {
        switch(this) {
            case LOW:
            case FAST:
                return ResourceLevel.LOW;
            case MEDIUM:
            case STANDARD:
                return ResourceLevel.MEDIUM;
            case HIGH:
            case CRITICAL:
            case THOROUGH:
                return ResourceLevel.HIGH;
            default:
                return ResourceLevel.MEDIUM;
        }
    }

    public enum ResourceLevel {
        LOW("Minimal system resources required"),
        MEDIUM("Moderate system resources required"),
        HIGH("High system resources required");

        private final String description;

        ResourceLevel(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // Performance metrics validation
    public boolean isValidPerformanceScore(double score) {
        return score >= 0.0 && score <= 5.0;
    }

    public String getPerformanceCategory() {
        if (performanceScore <= 1.5) {
            return "LIGHT";
        } else if (performanceScore <= 2.5) {
            return "MODERATE";
        } else if (performanceScore <= 3.5) {
            return "INTENSIVE";
        } else {
            return "EXTREME";
        }
    }
}