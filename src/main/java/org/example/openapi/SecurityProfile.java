package org.example.openapi;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class SecurityProfile {
    private final SecurityLevel securityLevel;
    private final double securityScore;
    private final int riskLevel;
    private final int securityTestCount;
    private final double securityCoverage;
    private final String riskLevelDescription;
    private final List<String> vulnerabilities;
    private final List<String> mitigations;
    private final Instant assessmentTimestamp;
    private final boolean loadTestingEnabled;
    private final int expectedThroughput;

    private SecurityProfile(Builder builder) {
        this.securityLevel = builder.securityLevel;
        this.securityScore = builder.securityScore;
        this.riskLevel = builder.riskLevel;
        this.securityTestCount = builder.securityTestCount;
        this.securityCoverage = builder.securityCoverage;
        this.riskLevelDescription = builder.riskLevelDescription != null ?
                builder.riskLevelDescription : generateRiskDescription(builder.riskLevel);
        this.vulnerabilities = new ArrayList<>(builder.vulnerabilities);
        this.mitigations = new ArrayList<>(builder.mitigations);
        this.assessmentTimestamp = builder.assessmentTimestamp != null ?
                builder.assessmentTimestamp : Instant.now();
        this.loadTestingEnabled = builder.loadTestingEnabled;
        this.expectedThroughput = builder.expectedThroughput;
    }

    // Getters
    public SecurityLevel getSecurityLevel() {
        return securityLevel;
    }

    public double getSecurityScore() {
        return securityScore;
    }

    public int getRiskLevel() {
        return riskLevel;
    }

    public int getSecurityTestCount() {
        return securityTestCount;
    }

    public double getSecurityCoverage() {
        return securityCoverage;
    }

    public String getRiskLevelDescription() {
        return riskLevelDescription;
    }

    public List<String> getVulnerabilities() {
        return new ArrayList<>(vulnerabilities);
    }

    public List<String> getMitigations() {
        return new ArrayList<>(mitigations);
    }

    public Instant getAssessmentTimestamp() {
        return assessmentTimestamp;
    }

    public boolean isLoadTestingEnabled() {
        return loadTestingEnabled;
    }

    public int getExpectedThroughput() {
        return expectedThroughput;
    }

    // Utility methods
    public boolean isHighRisk() {
        return riskLevel >= 4 || securityLevel == SecurityLevel.CRITICAL;
    }

    public boolean hasVulnerabilities() {
        return !vulnerabilities.isEmpty();
    }

    public boolean isSecurityTestingAdequate() {
        return securityTestCount >= 5 && securityCoverage >= 0.7;
    }

    public String getSecurityGrade() {
        if (securityScore >= 0.9) return "A";
        if (securityScore >= 0.8) return "B";
        if (securityScore >= 0.7) return "C";
        if (securityScore >= 0.6) return "D";
        return "F";
    }

    // Static factory methods
    public static Builder builder() {
        return new Builder();
    }

    public static SecurityProfile createDefault() {
        return builder().build();
    }

    public static SecurityProfile createHighSecurity() {
        return builder()
                .withSecurityLevel(SecurityLevel.HIGH)
                .withSecurityScore(0.9)
                .withRiskLevel(2)
                .withSecurityTestCount(15)
                .withSecurityCoverage(0.85)
                .build();
    }

    public static SecurityProfile createMinimal() {
        return builder()
                .withSecurityLevel(SecurityLevel.MINIMAL)
                .withSecurityScore(0.3)
                .withRiskLevel(1)
                .withSecurityTestCount(3)
                .withSecurityCoverage(0.4)
                .build();
    }

    // Enums
    public enum SecurityLevel {
        MINIMAL("Minimal security requirements", 1),
        LOW("Low security requirements", 2),
        MEDIUM("Medium security requirements", 3),
        HIGH("High security requirements", 4),
        CRITICAL("Critical security requirements", 5);

        private final String description;
        private final int priority;

        SecurityLevel(String description, int priority) {
            this.description = description;
            this.priority = priority;
        }

        public String getDescription() { return description; }
        public int getPriority() { return priority; }
    }

    // Builder class
    public static class Builder {
        private SecurityLevel securityLevel = SecurityLevel.MEDIUM;
        private double securityScore = 0.5;
        private int riskLevel = 2;
        private int securityTestCount = 0;
        private double securityCoverage = 0.0;
        private String riskLevelDescription;
        private List<String> vulnerabilities = new ArrayList<>();
        private List<String> mitigations = new ArrayList<>();
        private Instant assessmentTimestamp;
        private boolean loadTestingEnabled = false;
        private int expectedThroughput = 100;

        public Builder withSecurityLevel(SecurityLevel securityLevel) {
            this.securityLevel = securityLevel;
            return this;
        }

        public Builder withSecurityScore(double securityScore) {
            if (securityScore < 0.0 || securityScore > 1.0) {
                throw new IllegalArgumentException("Security score must be between 0.0 and 1.0");
            }
            this.securityScore = securityScore;
            return this;
        }

        public Builder withRiskLevel(int riskLevel) {
            if (riskLevel < 1 || riskLevel > 5) {
                throw new IllegalArgumentException("Risk level must be between 1 and 5");
            }
            this.riskLevel = riskLevel;
            return this;
        }

        public Builder withRiskLevel(String riskLevel) {
            // Convert string risk level to int
            switch (riskLevel.toUpperCase()) {
                case "LOW":
                    this.riskLevel = 1;
                    break;
                case "MEDIUM":
                    this.riskLevel = 2;
                    break;
                case "HIGH":
                    this.riskLevel = 4;
                    break;
                case "CRITICAL":
                    this.riskLevel = 5;
                    break;
                default:
                    this.riskLevel = 2; // Default to medium
            }
            return this;
        }

        public Builder withSecurityTestCount(int securityTestCount) {
            if (securityTestCount < 0) {
                throw new IllegalArgumentException("Security test count cannot be negative");
            }
            this.securityTestCount = securityTestCount;
            return this;
        }

        public Builder withSecurityCoverage(double securityCoverage) {
            if (securityCoverage < 0.0 || securityCoverage > 1.0) {
                throw new IllegalArgumentException("Security coverage must be between 0.0 and 1.0");
            }
            this.securityCoverage = securityCoverage;
            return this;
        }

        public Builder withRiskLevelDescription(String description) {
            this.riskLevelDescription = description;
            return this;
        }

        public Builder withVulnerability(String vulnerability) {
            if (vulnerability != null && !vulnerability.trim().isEmpty()) {
                this.vulnerabilities.add(vulnerability);
            }
            return this;
        }

        public Builder withVulnerabilities(List<String> vulnerabilities) {
            if (vulnerabilities != null) {
                this.vulnerabilities.addAll(vulnerabilities);
            }
            return this;
        }

        public Builder withMitigation(String mitigation) {
            if (mitigation != null && !mitigation.trim().isEmpty()) {
                this.mitigations.add(mitigation);
            }
            return this;
        }

        public Builder withMitigations(List<String> mitigations) {
            if (mitigations != null) {
                this.mitigations.addAll(mitigations);
            }
            return this;
        }

        public Builder withAssessmentTimestamp(Instant timestamp) {
            this.assessmentTimestamp = timestamp;
            return this;
        }

        public Builder withLoadTestingEnabled(boolean enabled) {
            this.loadTestingEnabled = enabled;
            return this;
        }

        public Builder withExpectedThroughput(int throughput) {
            if (throughput < 0) {
                throw new IllegalArgumentException("Expected throughput cannot be negative");
            }
            this.expectedThroughput = throughput;
            return this;
        }

        // Convenience methods for common security configurations
        public Builder withOWASPTop10Protection() {
            return this
                    .withSecurityLevel(SecurityLevel.HIGH)
                    .withSecurityScore(0.85)
                    .withRiskLevel(3)
                    .withSecurityTestCount(20)
                    .withSecurityCoverage(0.9)
                    .withMitigation("OWASP Top 10 protection enabled");
        }

        public Builder withBasicSecurity() {
            return this
                    .withSecurityLevel(SecurityLevel.LOW)
                    .withSecurityScore(0.6)
                    .withRiskLevel(2)
                    .withSecurityTestCount(5)
                    .withSecurityCoverage(0.5);
        }

        public Builder withEnterpriseGradeSecurity() {
            return this
                    .withSecurityLevel(SecurityLevel.CRITICAL)
                    .withSecurityScore(0.95)
                    .withRiskLevel(5)
                    .withSecurityTestCount(50)
                    .withSecurityCoverage(0.95)
                    .withMitigation("Enterprise-grade security measures")
                    .withMitigation("Multi-factor authentication")
                    .withMitigation("End-to-end encryption")
                    .withMitigation("Regular security audits");
        }

        // Validation
        private void validate() {
            if (securityTestCount > 0 && securityCoverage == 0.0) {
                // If we have security tests, we should have some coverage
                this.securityCoverage = Math.min(0.8, securityTestCount * 0.05);
            }

            // Adjust security score based on other factors
            if (securityScore == 0.5 && securityTestCount > 10) {
                this.securityScore = Math.min(0.9, 0.5 + (securityTestCount * 0.02));
            }
        }

        public SecurityProfile build() {
            validate();
            return new SecurityProfile(this);
        }
    }

    // Helper methods
    private static String generateRiskDescription(int riskLevel) {
        switch (riskLevel) {
            case 1:
                return "LOW";
            case 2:
                return "MEDIUM";
            case 3:
                return "MEDIUM-HIGH";
            case 4:
                return "HIGH";
            case 5:
                return "CRITICAL";
            default:
                return "UNKNOWN";
        }
    }

    // Override methods
    @Override
    public String toString() {
        return String.format("SecurityProfile{level=%s, score=%.2f, risk=%s, tests=%d, coverage=%.1f%%}",
                securityLevel, securityScore, riskLevelDescription, securityTestCount, securityCoverage * 100);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SecurityProfile that = (SecurityProfile) o;
        return Double.compare(that.securityScore, securityScore) == 0 &&
                riskLevel == that.riskLevel &&
                securityTestCount == that.securityTestCount &&
                Double.compare(that.securityCoverage, securityCoverage) == 0 &&
                securityLevel == that.securityLevel;
    }

    @Override
    public int hashCode() {
        return Objects.hash(securityLevel, securityScore, riskLevel, securityTestCount, securityCoverage);
    }
}