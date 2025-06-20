package org.example.openapi;

import java.time.Instant;
import java.util.Objects;

public class QualityMetrics {
    private final double coverageScore;
    private final double qualityScore;
    private final double securityScore;
    private final double complexityScore;
    private final int totalTests;
    private final int passedTests;
    private final int failedTests;
    private final int skippedTests;
    private final double successRate;
    private final Instant assessmentTimestamp;

    private QualityMetrics(Builder builder) {
        this.coverageScore = builder.coverageScore;
        this.qualityScore = builder.qualityScore;
        this.securityScore = builder.securityScore;
        this.complexityScore = builder.complexityScore;
        this.totalTests = builder.totalTests;
        this.passedTests = builder.passedTests;
        this.failedTests = builder.failedTests;
        this.skippedTests = builder.skippedTests;
        this.successRate = calculateSuccessRate(builder.passedTests, builder.totalTests);
        this.assessmentTimestamp = builder.assessmentTimestamp != null ?
                builder.assessmentTimestamp : Instant.now();
    }

    // Getters
    public double getCoverageScore() {
        return coverageScore;
    }

    public double getQualityScore() {
        return qualityScore;
    }

    public double getSecurityScore() {
        return securityScore;
    }

    public double getComplexityScore() {
        return complexityScore;
    }

    public int getTotalTests() {
        return totalTests;
    }

    public int getPassedTests() {
        return passedTests;
    }

    public int getFailedTests() {
        return failedTests;
    }

    public int getSkippedTests() {
        return skippedTests;
    }

    public double getSuccessRate() {
        return successRate;
    }

    public Instant getAssessmentTimestamp() {
        return assessmentTimestamp;
    }

    // Utility methods
    public double getOverallScore() {
        return (coverageScore + qualityScore + securityScore + (5.0 - complexityScore)) / 4.0;
    }

    public String getQualityGrade() {
        double overall = getOverallScore();
        if (overall >= 0.9) return "A";
        if (overall >= 0.8) return "B";
        if (overall >= 0.7) return "C";
        if (overall >= 0.6) return "D";
        return "F";
    }

    public boolean isAcceptableQuality() {
        return getOverallScore() >= 0.7 && successRate >= 0.8;
    }

    public boolean hasHighCoverage() {
        return coverageScore >= 0.8;
    }

    public boolean hasLowComplexity() {
        return complexityScore <= 2.0;
    }

    // Static factory methods
    public static Builder builder() {
        return new Builder();
    }

    public static QualityMetrics createDefault() {
        return builder().build();
    }

    public static QualityMetrics createHighQuality() {
        return builder()
                .withCoverageScore(0.9)
                .withQualityScore(0.85)
                .withSecurityScore(0.8)
                .withComplexityScore(2.0)
                .withTotalTests(100)
                .withPassedTests(95)
                .withFailedTests(3)
                .withSkippedTests(2)
                .build();
    }

    // Builder class
    public static class Builder {
        private double coverageScore = 0.0;
        private double qualityScore = 0.0;
        private double securityScore = 0.0;
        private double complexityScore = 0.0;
        private int totalTests = 0;
        private int passedTests = 0;
        private int failedTests = 0;
        private int skippedTests = 0;
        private Instant assessmentTimestamp;

        public Builder withCoverageScore(double coverageScore) {
            if (coverageScore < 0.0 || coverageScore > 1.0) {
                throw new IllegalArgumentException("Coverage score must be between 0.0 and 1.0");
            }
            this.coverageScore = coverageScore;
            return this;
        }

        public Builder withQualityScore(double qualityScore) {
            if (qualityScore < 0.0 || qualityScore > 1.0) {
                throw new IllegalArgumentException("Quality score must be between 0.0 and 1.0");
            }
            this.qualityScore = qualityScore;
            return this;
        }

        public Builder withSecurityScore(double securityScore) {
            if (securityScore < 0.0 || securityScore > 1.0) {
                throw new IllegalArgumentException("Security score must be between 0.0 and 1.0");
            }
            this.securityScore = securityScore;
            return this;
        }

        public Builder withComplexityScore(double complexityScore) {
            if (complexityScore < 0.0 || complexityScore > 5.0) {
                throw new IllegalArgumentException("Complexity score must be between 0.0 and 5.0");
            }
            this.complexityScore = complexityScore;
            return this;
        }

        public Builder withTotalTests(int totalTests) {
            if (totalTests < 0) {
                throw new IllegalArgumentException("Total tests cannot be negative");
            }
            this.totalTests = totalTests;
            return this;
        }

        public Builder withPassedTests(int passedTests) {
            if (passedTests < 0) {
                throw new IllegalArgumentException("Passed tests cannot be negative");
            }
            this.passedTests = passedTests;
            return this;
        }

        public Builder withFailedTests(int failedTests) {
            if (failedTests < 0) {
                throw new IllegalArgumentException("Failed tests cannot be negative");
            }
            this.failedTests = failedTests;
            return this;
        }

        public Builder withSkippedTests(int skippedTests) {
            if (skippedTests < 0) {
                throw new IllegalArgumentException("Skipped tests cannot be negative");
            }
            this.skippedTests = skippedTests;
            return this;
        }

        public Builder withAssessmentTimestamp(Instant timestamp) {
            this.assessmentTimestamp = timestamp;
            return this;
        }

        // Convenience methods for setting test results
        public Builder withTestResults(int total, int passed, int failed, int skipped) {
            return this
                    .withTotalTests(total)
                    .withPassedTests(passed)
                    .withFailedTests(failed)
                    .withSkippedTests(skipped);
        }

        public Builder withTestResults(int total, int passed, int failed) {
            int skipped = total - passed - failed;
            return withTestResults(total, passed, failed, Math.max(0, skipped));
        }

        // Auto-calculate scores based on test results
        public Builder withAutoCalculatedScores() {
            if (totalTests > 0) {
                double successRate = (double) passedTests / totalTests;
                this.qualityScore = successRate;
                this.coverageScore = Math.min(1.0, totalTests / 100.0); // Assume 100 tests = 100% coverage
                this.securityScore = successRate * 0.9; // Security slightly lower than quality
                this.complexityScore = Math.max(1.0, 5.0 - (successRate * 4.0)); // Inverse relationship
            }
            return this;
        }

        // Validation
        private void validate() {
            if (totalTests > 0) {
                int calculatedTotal = passedTests + failedTests + skippedTests;
                if (calculatedTotal != totalTests) {
                    // Auto-adjust if there's a mismatch
                    this.totalTests = calculatedTotal;
                }
            }

            // Ensure realistic values
            if (coverageScore == 0.0 && qualityScore == 0.0 && totalTests > 0) {
                // Auto-calculate basic scores
                withAutoCalculatedScores();
            }
        }

        public QualityMetrics build() {
            validate();
            return new QualityMetrics(this);
        }
    }

    // Helper methods
    private static double calculateSuccessRate(int passed, int total) {
        return total > 0 ? (double) passed / total : 0.0;
    }

    // Override methods
    @Override
    public String toString() {
        return String.format("QualityMetrics{coverage=%.2f, quality=%.2f, security=%.2f, " +
                        "complexity=%.2f, tests=%d/%d, grade=%s}",
                coverageScore, qualityScore, securityScore, complexityScore,
                passedTests, totalTests, getQualityGrade());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QualityMetrics that = (QualityMetrics) o;
        return Double.compare(that.coverageScore, coverageScore) == 0 &&
                Double.compare(that.qualityScore, qualityScore) == 0 &&
                Double.compare(that.securityScore, securityScore) == 0 &&
                Double.compare(that.complexityScore, complexityScore) == 0 &&
                totalTests == that.totalTests &&
                passedTests == that.passedTests &&
                failedTests == that.failedTests &&
                skippedTests == that.skippedTests;
    }

    @Override
    public int hashCode() {
        return Objects.hash(coverageScore, qualityScore, securityScore, complexityScore,
                totalTests, passedTests, failedTests, skippedTests);
    }
}