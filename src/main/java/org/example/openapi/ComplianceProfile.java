package org.example.openapi;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ComplianceProfile {
    private final String standard;
    private final String version;
    private final boolean required;
    private final List<String> requirements;
    private final List<String> complianceStandards;
    private final double complianceScore;
    private final ComplianceLevel complianceLevel;
    private final Instant assessmentTimestamp;
    private final String assessmentBy;
    private final List<String> violations;
    private final List<String> recommendations;

    // Private constructor for immutable design
    private ComplianceProfile(Builder builder) {
        this.standard = builder.standard;
        this.version = builder.version;
        this.required = builder.required;
        this.requirements = new ArrayList<>(builder.requirements);
        this.complianceStandards = new ArrayList<>(builder.complianceStandards);
        this.complianceScore = builder.complianceScore;
        this.complianceLevel = builder.complianceLevel != null ?
                builder.complianceLevel : determineComplianceLevel(builder.complianceScore);
        this.assessmentTimestamp = builder.assessmentTimestamp != null ?
                builder.assessmentTimestamp : Instant.now();
        this.assessmentBy = builder.assessmentBy != null ?
                builder.assessmentBy : "System";
        this.violations = new ArrayList<>(builder.violations);
        this.recommendations = new ArrayList<>(builder.recommendations);
    }

    // Getters
    public String getStandard() {
        return standard;
    }

    public String getVersion() {
        return version;
    }

    public boolean isRequired() {
        return required;
    }

    public List<String> getRequirements() {
        return new ArrayList<>(requirements);
    }

    public List<String> getComplianceStandards() {
        return new ArrayList<>(complianceStandards);
    }

    public double getComplianceScore() {
        return complianceScore;
    }

    public ComplianceLevel getComplianceLevel() {
        return complianceLevel;
    }

    public Instant getAssessmentTimestamp() {
        return assessmentTimestamp;
    }

    public String getAssessmentBy() {
        return assessmentBy;
    }

    public List<String> getViolations() {
        return new ArrayList<>(violations);
    }

    public List<String> getRecommendations() {
        return new ArrayList<>(recommendations);
    }

    // Utility methods
    public boolean isCompliant() {
        return complianceScore >= 0.8 && violations.isEmpty();
    }

    public boolean hasViolations() {
        return !violations.isEmpty();
    }

    public int getRequirementCount() {
        return requirements.size();
    }

    public int getViolationCount() {
        return violations.size();
    }

    public String getComplianceGrade() {
        if (complianceScore >= 0.95) return "A+";
        if (complianceScore >= 0.9) return "A";
        if (complianceScore >= 0.8) return "B";
        if (complianceScore >= 0.7) return "C";
        if (complianceScore >= 0.6) return "D";
        return "F";
    }

    public boolean meetsMinimumRequirements() {
        return complianceScore >= 0.6 && complianceLevel != ComplianceLevel.NON_COMPLIANT;
    }

    // Static factory methods
    public static Builder builder() {
        return new Builder();
    }

    public static ComplianceProfile createDefault() {
        return builder()
                .withStandard("ISO-27001")
                .withVersion("2022")
                .withRequired(false)
                .withComplianceScore(0.7)
                .build();
    }

    public static ComplianceProfile createSOX() {
        return builder()
                .withStandard("SOX")
                .withVersion("2002")
                .withRequired(true)
                .withComplianceStandards(Arrays.asList("SOX", "PCAOB"))
                .withComplianceScore(0.9)
                .withRequirement("Financial reporting accuracy")
                .withRequirement("Internal controls testing")
                .withRequirement("Audit trail maintenance")
                .build();
    }

    public static ComplianceProfile createGDPR() {
        return builder()
                .withStandard("GDPR")
                .withVersion("2018")
                .withRequired(true)
                .withComplianceStandards(Arrays.asList("GDPR", "Privacy"))
                .withComplianceScore(0.85)
                .withRequirement("Data protection by design")
                .withRequirement("Right to be forgotten")
                .withRequirement("Data breach notification")
                .build();
    }

    public static ComplianceProfile createHIPAA() {
        return builder()
                .withStandard("HIPAA")
                .withVersion("1996")
                .withRequired(true)
                .withComplianceStandards(Arrays.asList("HIPAA", "Healthcare"))
                .withComplianceScore(0.9)
                .withRequirement("Patient data encryption")
                .withRequirement("Access controls")
                .withRequirement("Audit logging")
                .build();
    }

    // Enums
    public enum ComplianceLevel {
        FULLY_COMPLIANT("Fully compliant with all requirements"),
        SUBSTANTIALLY_COMPLIANT("Substantially compliant with minor gaps"),
        PARTIALLY_COMPLIANT("Partially compliant with significant gaps"),
        NON_COMPLIANT("Non-compliant with major violations");

        private final String description;

        ComplianceLevel(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // Builder class
    public static class Builder {
        private String standard = "Generic";
        private String version = "1.0";
        private boolean required = false;
        private List<String> requirements = new ArrayList<>();
        private List<String> complianceStandards = new ArrayList<>();
        private double complianceScore = 0.0;
        private ComplianceLevel complianceLevel;
        private Instant assessmentTimestamp;
        private String assessmentBy;
        private List<String> violations = new ArrayList<>();
        private List<String> recommendations = new ArrayList<>();

        public Builder withStandard(String standard) {
            this.standard = standard;
            return this;
        }

        public Builder withVersion(String version) {
            this.version = version;
            return this;
        }

        public Builder withRequired(boolean required) {
            this.required = required;
            return this;
        }

        public Builder withRequirement(String requirement) {
            if (requirement != null && !requirement.trim().isEmpty()) {
                this.requirements.add(requirement);
            }
            return this;
        }

        public Builder withRequirements(List<String> requirements) {
            if (requirements != null) {
                this.requirements.addAll(requirements);
            }
            return this;
        }

        // Hata mesajında aranan metod
        public Builder withComplianceStandards(List<String> complianceStandards) {
            if (complianceStandards != null) {
                this.complianceStandards.addAll(complianceStandards);
            }
            return this;
        }

        public Builder withComplianceStandard(String standard) {
            if (standard != null && !standard.trim().isEmpty()) {
                this.complianceStandards.add(standard);
            }
            return this;
        }

        // Hata mesajında aranan metod
        public Builder withComplianceScore(double complianceScore) {
            if (complianceScore < 0.0 || complianceScore > 1.0) {
                throw new IllegalArgumentException("Compliance score must be between 0.0 and 1.0");
            }
            this.complianceScore = complianceScore;
            return this;
        }

        public Builder withComplianceLevel(ComplianceLevel complianceLevel) {
            this.complianceLevel = complianceLevel;
            return this;
        }

        public Builder withAssessmentTimestamp(Instant timestamp) {
            this.assessmentTimestamp = timestamp;
            return this;
        }

        public Builder withAssessmentBy(String assessmentBy) {
            this.assessmentBy = assessmentBy;
            return this;
        }

        public Builder withViolation(String violation) {
            if (violation != null && !violation.trim().isEmpty()) {
                this.violations.add(violation);
            }
            return this;
        }

        public Builder withViolations(List<String> violations) {
            if (violations != null) {
                this.violations.addAll(violations);
            }
            return this;
        }

        public Builder withRecommendation(String recommendation) {
            if (recommendation != null && !recommendation.trim().isEmpty()) {
                this.recommendations.add(recommendation);
            }
            return this;
        }

        public Builder withRecommendations(List<String> recommendations) {
            if (recommendations != null) {
                this.recommendations.addAll(recommendations);
            }
            return this;
        }

        // Convenience methods for common compliance standards
        public Builder withSOXCompliance() {
            return this
                    .withStandard("SOX")
                    .withVersion("2002")
                    .withRequired(true)
                    .withComplianceStandard("SOX")
                    .withComplianceStandard("PCAOB")
                    .withRequirement("Financial reporting controls")
                    .withRequirement("Management assessment")
                    .withRequirement("Auditor attestation");
        }

        public Builder withGDPRCompliance() {
            return this
                    .withStandard("GDPR")
                    .withVersion("2018")
                    .withRequired(true)
                    .withComplianceStandard("GDPR")
                    .withRequirement("Data protection by design")
                    .withRequirement("Privacy impact assessments")
                    .withRequirement("Breach notification procedures");
        }

        public Builder withISO27001Compliance() {
            return this
                    .withStandard("ISO-27001")
                    .withVersion("2022")
                    .withRequired(false)
                    .withComplianceStandard("ISO-27001")
                    .withRequirement("Information security management")
                    .withRequirement("Risk assessment procedures")
                    .withRequirement("Security controls implementation");
        }

        // Validation
        private void validate() {
            if (standard == null || standard.trim().isEmpty()) {
                this.standard = "Generic";
            }

            if (version == null || version.trim().isEmpty()) {
                this.version = "1.0";
            }

            // Auto-adjust compliance score based on violations
            if (!violations.isEmpty() && complianceScore > 0.8) {
                this.complianceScore = Math.max(0.5, complianceScore - (violations.size() * 0.1));
            }

            // Ensure compliance level matches score
            if (complianceLevel == null) {
                this.complianceLevel = determineComplianceLevel(complianceScore);
            }
        }

        public ComplianceProfile build() {
            validate();
            return new ComplianceProfile(this);
        }
    }

    // Helper methods
    private static ComplianceLevel determineComplianceLevel(double score) {
        if (score >= 0.95) return ComplianceLevel.FULLY_COMPLIANT;
        if (score >= 0.8) return ComplianceLevel.SUBSTANTIALLY_COMPLIANT;
        if (score >= 0.6) return ComplianceLevel.PARTIALLY_COMPLIANT;
        return ComplianceLevel.NON_COMPLIANT;
    }

    // Override methods
    @Override
    public String toString() {
        return String.format("ComplianceProfile{standard='%s', version='%s', score=%.2f, " +
                        "level=%s, violations=%d, required=%s}",
                standard, version, complianceScore, complianceLevel,
                violations.size(), required);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComplianceProfile that = (ComplianceProfile) o;
        return required == that.required &&
                Double.compare(that.complianceScore, complianceScore) == 0 &&
                Objects.equals(standard, that.standard) &&
                Objects.equals(version, that.version) &&
                complianceLevel == that.complianceLevel;
    }

    @Override
    public int hashCode() {
        return Objects.hash(standard, version, required, complianceScore, complianceLevel);
    }
}