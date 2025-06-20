package org.example.openapi;

public enum StrategyType {
    // Functional strategies
    FUNCTIONAL_BASIC("Basic functional testing", "Test happy path scenarios", 1),
    FUNCTIONAL_BOUNDARY("Boundary value testing", "Test edge cases and boundaries", 2),
    FUNCTIONAL_COMPREHENSIVE("Comprehensive functional testing", "Complete functional test coverage", 3), // ADDED
    FUNCTIONAL_EDGE_CASE("Functional edge case testing", "Edge case and boundary condition testing", 3), // MISSING

    // Performance strategies
    PERFORMANCE_BASIC("Basic performance testing", "Light performance validation", 1),
    PERFORMANCE_STRESS("Stress performance testing", "Heavy load and stress testing", 3),
    PERFORMANCE_LOAD("Load performance testing", "High volume load testing", 3), // ADDED

    // Security strategies
    SECURITY_INJECTION("Security injection testing", "SQL injection and similar attacks", 2),
    SECURITY_XSS("Cross-site scripting testing", "XSS and related security tests", 2),
    SECURITY_BASIC("Basic security testing", "Fundamental security validation", 1), // ADDED
    SECURITY_OWASP_TOP10("OWASP Top 10 security testing", "Complete OWASP Top 10 coverage", 3), // ADDED
    SECURITY_PENETRATION("Penetration testing", "Advanced penetration testing", 4), // ADDED
    SECURITY_AUTHENTICATION("Authentication testing", "Authentication and session testing", 2), // ADDED
    SECURITY_AUTHORIZATION("Authorization testing", "Authorization and access control testing", 2), // ADDED

    // Advanced strategies
    ADVANCED_CONCURRENCY("Advanced concurrency testing", "Race conditions and threading", 3),
    ADVANCED_AI_DRIVEN("AI-driven testing", "Machine learning powered test generation", 4), // ADDED
    ADVANCED_FUZZING("Advanced fuzzing testing", "Automated fuzzing and mutation testing", 4), // ADDED

    // Additional enum values needed for ResponseInfo compatibility
    BOUNDARY_VALUE_ANALYSIS("Boundary value analysis", "Systematic boundary value testing", 2),
    SECURITY_COMPREHENSIVE("Comprehensive security testing", "Complete security test coverage", 3),
    PERFORMANCE_COMPREHENSIVE("Comprehensive performance testing", "Complete performance test coverage", 3),
    AI_DRIVEN_EXPLORATION("AI-driven exploration", "AI-powered exploratory testing", 4);

    private final String displayName;
    private final String description;
    private final int baseComplexity;

    StrategyType(String displayName, String description, int baseComplexity) {
        this.displayName = displayName;
        this.description = description;
        this.baseComplexity = baseComplexity;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        switch(this) {
            case FUNCTIONAL_BASIC:
            case FUNCTIONAL_BOUNDARY:
            case FUNCTIONAL_COMPREHENSIVE:
            case FUNCTIONAL_EDGE_CASE:
            case BOUNDARY_VALUE_ANALYSIS:
                return "FUNCTIONAL";
            case PERFORMANCE_BASIC:
            case PERFORMANCE_STRESS:
            case PERFORMANCE_LOAD:
            case PERFORMANCE_COMPREHENSIVE:
                return "PERFORMANCE";
            case SECURITY_INJECTION:
            case SECURITY_XSS:
            case SECURITY_BASIC:
            case SECURITY_OWASP_TOP10:
            case SECURITY_PENETRATION:
            case SECURITY_AUTHENTICATION:
            case SECURITY_AUTHORIZATION:
            case SECURITY_COMPREHENSIVE:
                return "SECURITY";
            case ADVANCED_CONCURRENCY:
            case ADVANCED_AI_DRIVEN:
            case ADVANCED_FUZZING:
            case AI_DRIVEN_EXPLORATION:
                return "ADVANCED";
            default:
                return "UNKNOWN";
        }
    }

    public int getComplexity() {
        return baseComplexity;
    }

    // Method reference için statik versiyon - EndpointInfo.java'daki hata için
    // NOT: Bu metodu kaldırmayın, çünkü EndpointInfo.java'da kullanılıyor
    public static int getComplexity(StrategyType strategy) {
        return strategy != null ? strategy.getComplexity() : 1;
    }

    // Test süresi tahmini (dakika cinsinden)
    public int getEstimatedDurationMinutes() {
        switch(this) {
            case FUNCTIONAL_BASIC:
            case SECURITY_BASIC:
                return 5;
            case FUNCTIONAL_BOUNDARY:
            case FUNCTIONAL_EDGE_CASE:
            case PERFORMANCE_BASIC:
            case SECURITY_INJECTION:
            case SECURITY_XSS:
            case SECURITY_AUTHENTICATION:
            case SECURITY_AUTHORIZATION:
                return 15;
            case FUNCTIONAL_COMPREHENSIVE:
            case PERFORMANCE_STRESS:
            case SECURITY_OWASP_TOP10:
                return 30;
            case PERFORMANCE_LOAD:
            case SECURITY_PENETRATION:
                return 45;
            case ADVANCED_CONCURRENCY:
            case ADVANCED_AI_DRIVEN:
            case ADVANCED_FUZZING:
                return 60;
            default:
                return 10;
        }
    }

    // Bu stratejinin önerdiği minimum test case sayısı
    public int getRecommendedTestCaseCount() {
        switch(this) {
            case FUNCTIONAL_BASIC:
            case SECURITY_BASIC:
                return 3;
            case FUNCTIONAL_BOUNDARY:
            case FUNCTIONAL_EDGE_CASE:
            case PERFORMANCE_BASIC:
            case SECURITY_AUTHENTICATION:
            case SECURITY_AUTHORIZATION:
                return 8;
            case FUNCTIONAL_COMPREHENSIVE:
                return 15;
            case SECURITY_INJECTION:
            case SECURITY_XSS:
                return 12;
            case PERFORMANCE_STRESS:
            case PERFORMANCE_LOAD:
                return 20;
            case SECURITY_OWASP_TOP10:
                return 25;
            case SECURITY_PENETRATION:
                return 30;
            case ADVANCED_CONCURRENCY:
                return 40;
            case ADVANCED_AI_DRIVEN:
            case ADVANCED_FUZZING:
                return 50;
            default:
                return 5;
        }
    }

    // Bu strateji için uygun test senaryolarını döndür
    public TestGenerationScenario[] getCompatibleScenarios() {
        switch(this) {
            case FUNCTIONAL_BASIC:
                return new TestGenerationScenario[]{
                        TestGenerationScenario.HAPPY_PATH,
                        TestGenerationScenario.ERROR_HANDLING,
                        TestGenerationScenario.INPUT_VALIDATION_BASIC
                };

            case FUNCTIONAL_BOUNDARY:
                return new TestGenerationScenario[]{
                        TestGenerationScenario.BOUNDARY_VALUES,
                        TestGenerationScenario.BOUNDARY_VALUE_ANALYSIS,
                        TestGenerationScenario.NULL_VALUE_TESTING,
                        TestGenerationScenario.REGEX_PATTERN_TESTING,
                        TestGenerationScenario.NESTED_OBJECT_TESTING,
                        TestGenerationScenario.ARRAY_BOUNDARY_TESTING
                };

            case FUNCTIONAL_COMPREHENSIVE:
                return new TestGenerationScenario[]{
                        TestGenerationScenario.HAPPY_PATH,
                        TestGenerationScenario.ERROR_HANDLING,
                        TestGenerationScenario.BOUNDARY_VALUES,
                        TestGenerationScenario.INPUT_VALIDATION_BASIC,
                        TestGenerationScenario.NESTED_OBJECT_TESTING,
                        TestGenerationScenario.ARRAY_BOUNDARY_TESTING
                };

            case PERFORMANCE_BASIC:
                return new TestGenerationScenario[]{
                        TestGenerationScenario.LOAD_TESTING_LIGHT
                };

            case PERFORMANCE_STRESS:
            case PERFORMANCE_LOAD:
                return new TestGenerationScenario[]{
                        TestGenerationScenario.LOAD_TESTING_HEAVY,
                        TestGenerationScenario.STRESS_TESTING
                };

            case SECURITY_BASIC:
                return new TestGenerationScenario[]{
                        TestGenerationScenario.SQL_INJECTION_BASIC,
                        TestGenerationScenario.XSS_REFLECTED,
                        TestGenerationScenario.INPUT_VALIDATION_BASIC
                };

            case SECURITY_INJECTION:
                return new TestGenerationScenario[]{
                        TestGenerationScenario.SQL_INJECTION_BASIC,
                        TestGenerationScenario.XML_EXTERNAL_ENTITY,
                        TestGenerationScenario.DESERIALIZATION_ATTACK,
                        TestGenerationScenario.AUTH_BYPASS,
                        TestGenerationScenario.PRIVILEGE_ESCALATION
                };

            case SECURITY_XSS:
                return new TestGenerationScenario[]{
                        TestGenerationScenario.XSS_REFLECTED,
                        TestGenerationScenario.CSRF_PROTECTION,
                        TestGenerationScenario.FILE_UPLOAD_MALICIOUS,
                        TestGenerationScenario.DATA_EXPOSURE_TEST
                };

            case SECURITY_OWASP_TOP10:
                return new TestGenerationScenario[]{
                        TestGenerationScenario.SQL_INJECTION_BASIC,
                        TestGenerationScenario.XSS_REFLECTED,
                        TestGenerationScenario.XML_EXTERNAL_ENTITY,
                        TestGenerationScenario.DESERIALIZATION_ATTACK,
                        TestGenerationScenario.AUTH_BYPASS,
                        TestGenerationScenario.PRIVILEGE_ESCALATION,
                        TestGenerationScenario.CSRF_PROTECTION,
                        TestGenerationScenario.FILE_UPLOAD_MALICIOUS,
                        TestGenerationScenario.DATA_EXPOSURE_TEST,
                        TestGenerationScenario.BUFFER_OVERFLOW_TEST
                };

            case SECURITY_PENETRATION:
                return new TestGenerationScenario[]{
                        TestGenerationScenario.SQL_INJECTION_BASIC,
                        TestGenerationScenario.XSS_REFLECTED,
                        TestGenerationScenario.XML_EXTERNAL_ENTITY,
                        TestGenerationScenario.DESERIALIZATION_ATTACK,
                        TestGenerationScenario.AUTH_BYPASS,
                        TestGenerationScenario.PRIVILEGE_ESCALATION,
                        TestGenerationScenario.BUFFER_OVERFLOW_TEST,
                        TestGenerationScenario.DATA_EXPOSURE_TEST
                };

            case ADVANCED_CONCURRENCY:
                return new TestGenerationScenario[]{
                        TestGenerationScenario.CONCURRENCY_RACE_CONDITIONS
                };

            case ADVANCED_AI_DRIVEN:
                return new TestGenerationScenario[]{
                        TestGenerationScenario.HAPPY_PATH,
                        TestGenerationScenario.BOUNDARY_VALUES,
                        TestGenerationScenario.ERROR_HANDLING,
                        TestGenerationScenario.LOAD_TESTING_HEAVY,
                        TestGenerationScenario.SQL_INJECTION_BASIC
                };

            case ADVANCED_FUZZING:
                return new TestGenerationScenario[]{
                        TestGenerationScenario.FUZZING_TESTING,
                        TestGenerationScenario.MUTATION_TESTING
                };

            default:
                return new TestGenerationScenario[]{TestGenerationScenario.HAPPY_PATH};
        }
    }

    // Bu stratejinin öncelik seviyesi (1 = en yüksek, 5 = en düşük)
    public int getPriority() {
        switch(this) {
            case FUNCTIONAL_BASIC:
                return 1; // En yüksek öncelik
            case SECURITY_BASIC:
            case SECURITY_INJECTION:
            case SECURITY_XSS:
            case SECURITY_OWASP_TOP10:
            case SECURITY_AUTHENTICATION:
            case SECURITY_AUTHORIZATION:
                return 2; // Yüksek öncelik
            case FUNCTIONAL_BOUNDARY:
            case FUNCTIONAL_COMPREHENSIVE:
                return 3; // Orta öncelik
            case PERFORMANCE_BASIC:
            case SECURITY_PENETRATION:
                return 4; // Düşük öncelik
            case PERFORMANCE_STRESS:
            case PERFORMANCE_LOAD:
            case ADVANCED_CONCURRENCY:
            case ADVANCED_AI_DRIVEN:
            case ADVANCED_FUZZING:
                return 5; // En düşük öncelik
            default:
                return 3;
        }
    }

    // Bu stratejinin gerektirdiği kaynak seviyesi
    public ResourceLevel getResourceRequirement() {
        switch(this) {
            case FUNCTIONAL_BASIC:
            case FUNCTIONAL_BOUNDARY:
            case SECURITY_BASIC:
                return ResourceLevel.LOW;
            case FUNCTIONAL_COMPREHENSIVE:
            case PERFORMANCE_BASIC:
            case SECURITY_INJECTION:
            case SECURITY_XSS:
            case SECURITY_OWASP_TOP10:
                return ResourceLevel.MEDIUM;
            case PERFORMANCE_STRESS:
            case PERFORMANCE_LOAD:
            case SECURITY_PENETRATION:
            case ADVANCED_CONCURRENCY:
            case ADVANCED_AI_DRIVEN:
            case ADVANCED_FUZZING:
                return ResourceLevel.HIGH;
            default:
                return ResourceLevel.LOW;
        }
    }

    // Risk seviyesi değerlendirmesi
    public int getRiskLevel() {
        switch(this) {
            case FUNCTIONAL_BASIC:
            case PERFORMANCE_BASIC:
                return 1; // Düşük risk
            case FUNCTIONAL_BOUNDARY:
            case SECURITY_BASIC:
                return 2; // Orta-düşük risk
            case FUNCTIONAL_COMPREHENSIVE:
            case SECURITY_INJECTION:
            case SECURITY_XSS:
                return 3; // Orta risk
            case PERFORMANCE_STRESS:
            case PERFORMANCE_LOAD:
            case SECURITY_OWASP_TOP10:
                return 4; // Yüksek risk
            case SECURITY_PENETRATION:
            case ADVANCED_CONCURRENCY:
            case ADVANCED_AI_DRIVEN:
            case ADVANCED_FUZZING:
                return 5; // Kritik risk
            default:
                return 2;
        }
    }

    // İş impact seviyesi
    public BusinessImpact getBusinessImpact() {
        switch(this) {
            case FUNCTIONAL_BASIC:
            case FUNCTIONAL_COMPREHENSIVE:
                return BusinessImpact.HIGH; // Fonksiyonel testler iş açısından kritik
            case SECURITY_BASIC:
            case SECURITY_INJECTION:
            case SECURITY_XSS:
            case SECURITY_OWASP_TOP10:
            case SECURITY_PENETRATION:
                return BusinessImpact.CRITICAL; // Güvenlik testleri kritik
            case PERFORMANCE_BASIC:
            case PERFORMANCE_STRESS:
            case PERFORMANCE_LOAD:
                return BusinessImpact.MEDIUM; // Performans testleri orta
            case FUNCTIONAL_BOUNDARY:
            case ADVANCED_CONCURRENCY:
                return BusinessImpact.MEDIUM;
            case ADVANCED_AI_DRIVEN:
            case ADVANCED_FUZZING:
                return BusinessImpact.LOW; // AI ve fuzzing testleri düşük iş etkisi
            default:
                return BusinessImpact.MEDIUM;
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

    public enum BusinessImpact {
        LOW("Low business impact"),
        MEDIUM("Medium business impact"),
        HIGH("High business impact"),
        CRITICAL("Critical business impact");

        private final String description;

        BusinessImpact(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}