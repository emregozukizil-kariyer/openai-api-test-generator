package org.example.openapi;

public enum TestGenerationScenario {
    // Basic Test Scenarios
    HAPPY_PATH("Valid request with expected response"),
    ERROR_HANDLING("Error cases and edge conditions"),

    // Boundary Testing
    BOUNDARY_VALUE_ANALYSIS("Test boundary values"),
    BOUNDARY_VALUES("Test boundary values"), // Hata mesajında aranan
    NULL_VALUE_TESTING("Test null values"),
    REGEX_PATTERN_TESTING("Test regex patterns"),

    // Security Testing
    SQL_INJECTION_BASIC("Basic SQL injection tests"),
    XSS_REFLECTED("Cross-site scripting tests"),
    XML_EXTERNAL_ENTITY("XXE attack tests"),
    DESERIALIZATION_ATTACK("Unsafe deserialization tests"),
    FILE_UPLOAD_MALICIOUS("Malicious file upload tests"),
    BUFFER_OVERFLOW_TEST("Buffer overflow tests"),
    DATA_EXPOSURE_TEST("Data exposure tests"),
    PRIVILEGE_ESCALATION("Privilege escalation tests"),
    CSRF_PROTECTION("CSRF protection tests"), // Hata mesajında aranan
    AUTH_BYPASS("Authentication bypass tests"), // Hata mesajında aranan

    // Input Validation
    INPUT_VALIDATION_BASIC("Basic input validation"),

    // Performance Testing
    LOAD_TESTING_LIGHT("Light load testing"),
    LOAD_TESTING_HEAVY("Heavy load testing"), // Hata mesajında aranan
    STRESS_TESTING("Stress testing scenarios"), // Hata mesajında aranan

    // Advanced Testing
    NESTED_OBJECT_TESTING("Nested object validation"),
    ARRAY_BOUNDARY_TESTING("Array boundary testing"),
    CONCURRENCY_RACE_CONDITIONS("Concurrency and race condition tests"), // Hata mesajında aranan
    
    // Missing enum values from error messages
    EDGE_CASES("Edge case testing"),
    AI_DRIVEN_EXPLORATION("AI-driven exploration testing"),
    FUZZING_INPUT("Input fuzzing testing"),
    FUZZING_TESTING("Fuzzing testing"),  // Missing from StrategyType compatibility
    MUTATION_TESTING("Mutation testing"), // Missing from StrategyType compatibility
    XSS_STORED("Stored XSS testing"),
    BOUNDARY_MIN("Minimum boundary testing"),
    BOUNDARY_MAX("Maximum boundary testing"),
    NULL_VALUE_HANDLING("Null value handling"),
    AUTHENTICATION_BYPASS("Authentication bypass testing"),
    STRESS_TESTING_CPU("CPU stress testing");

    private final String description;

    TestGenerationScenario(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    // Hata mesajlarında aranan eksik metodlar
    public StrategyType getRecommendedStrategy() {
        switch(this) {
            case BOUNDARY_VALUES:
            case BOUNDARY_VALUE_ANALYSIS:
            case NULL_VALUE_TESTING:
            case REGEX_PATTERN_TESTING:
            case BOUNDARY_MIN:
            case BOUNDARY_MAX:
            case NULL_VALUE_HANDLING:
                return StrategyType.FUNCTIONAL_BOUNDARY;

            case EDGE_CASES:
                return StrategyType.FUNCTIONAL_EDGE_CASE;

            case SQL_INJECTION_BASIC:
            case XSS_REFLECTED:
            case XSS_STORED:
            case XML_EXTERNAL_ENTITY:
            case DESERIALIZATION_ATTACK:
            case FILE_UPLOAD_MALICIOUS:
            case BUFFER_OVERFLOW_TEST:
            case DATA_EXPOSURE_TEST:
            case PRIVILEGE_ESCALATION:
            case CSRF_PROTECTION:
            case AUTH_BYPASS:
            case AUTHENTICATION_BYPASS:
                return StrategyType.SECURITY_INJECTION;

            case LOAD_TESTING_LIGHT:
            case LOAD_TESTING_HEAVY:
                return StrategyType.PERFORMANCE_BASIC;

            case STRESS_TESTING:
            case STRESS_TESTING_CPU:
                return StrategyType.PERFORMANCE_STRESS;

            case CONCURRENCY_RACE_CONDITIONS:
                return StrategyType.ADVANCED_CONCURRENCY;

            case FUZZING_INPUT:
            case FUZZING_TESTING:
            case MUTATION_TESTING:
                return StrategyType.ADVANCED_FUZZING;

            case AI_DRIVEN_EXPLORATION:
                return StrategyType.ADVANCED_AI_DRIVEN;

            case INPUT_VALIDATION_BASIC:
            case NESTED_OBJECT_TESTING:
            case ARRAY_BOUNDARY_TESTING:
                return StrategyType.FUNCTIONAL_BOUNDARY;

            case HAPPY_PATH:
            case ERROR_HANDLING:
            default:
                return StrategyType.FUNCTIONAL_BASIC;
        }
    }

    public int getComplexity() {
        switch(this) {
            case HAPPY_PATH:
            case INPUT_VALIDATION_BASIC:
            case LOAD_TESTING_LIGHT:
                return 1; // Low complexity

            case ERROR_HANDLING:
            case BOUNDARY_VALUES:
            case BOUNDARY_VALUE_ANALYSIS:
            case BOUNDARY_MIN:
            case BOUNDARY_MAX:
            case NULL_VALUE_TESTING:
            case NULL_VALUE_HANDLING:
            case REGEX_PATTERN_TESTING:
            case SQL_INJECTION_BASIC:
            case XSS_REFLECTED:
            case XSS_STORED:
            case CSRF_PROTECTION:
            case NESTED_OBJECT_TESTING:
            case ARRAY_BOUNDARY_TESTING:
                return 2; // Medium complexity

            case EDGE_CASES:
            case XML_EXTERNAL_ENTITY:
            case DESERIALIZATION_ATTACK:
            case FILE_UPLOAD_MALICIOUS:
            case BUFFER_OVERFLOW_TEST:
            case DATA_EXPOSURE_TEST:
            case PRIVILEGE_ESCALATION:
            case AUTH_BYPASS:
            case AUTHENTICATION_BYPASS:
            case LOAD_TESTING_HEAVY:
            case STRESS_TESTING:
            case STRESS_TESTING_CPU:
            case CONCURRENCY_RACE_CONDITIONS:
                return 3; // High complexity

            case FUZZING_INPUT:
            case FUZZING_TESTING:
            case MUTATION_TESTING:
            case AI_DRIVEN_EXPLORATION:
                return 4; // Very high complexity

            default:
                return 1;
        }
    }

    public String getCategory() {
        switch(this) {
            case HAPPY_PATH:
            case ERROR_HANDLING:
            case BOUNDARY_VALUES:
            case BOUNDARY_VALUE_ANALYSIS:
            case BOUNDARY_MIN:
            case BOUNDARY_MAX:
            case NULL_VALUE_TESTING:
            case NULL_VALUE_HANDLING:
            case REGEX_PATTERN_TESTING:
            case INPUT_VALIDATION_BASIC:
            case NESTED_OBJECT_TESTING:
            case ARRAY_BOUNDARY_TESTING:
            case EDGE_CASES:
                return "FUNCTIONAL";

            case SQL_INJECTION_BASIC:
            case XSS_REFLECTED:
            case XSS_STORED:
            case XML_EXTERNAL_ENTITY:
            case DESERIALIZATION_ATTACK:
            case FILE_UPLOAD_MALICIOUS:
            case BUFFER_OVERFLOW_TEST:
            case DATA_EXPOSURE_TEST:
            case PRIVILEGE_ESCALATION:
            case CSRF_PROTECTION:
            case AUTH_BYPASS:
            case AUTHENTICATION_BYPASS:
                return "SECURITY";

            case LOAD_TESTING_LIGHT:
            case LOAD_TESTING_HEAVY:
            case STRESS_TESTING:
            case STRESS_TESTING_CPU:
                return "PERFORMANCE";

            case CONCURRENCY_RACE_CONDITIONS:
                return "CONCURRENCY";

            case FUZZING_INPUT:
            case FUZZING_TESTING:
            case MUTATION_TESTING:
            case AI_DRIVEN_EXPLORATION:
                return "ADVANCED";

            default:
                return "FUNCTIONAL";
        }
    }

    // Test senaryosunun önerilen süresini döndürür (saniye cinsinden)
    public int getEstimatedDurationSeconds() {
        switch(this) {
            case HAPPY_PATH:
            case INPUT_VALIDATION_BASIC:
                return 5;

            case ERROR_HANDLING:
            case BOUNDARY_VALUES:
            case BOUNDARY_VALUE_ANALYSIS:
            case NULL_VALUE_TESTING:
            case REGEX_PATTERN_TESTING:
                return 15;

            case SQL_INJECTION_BASIC:
            case XSS_REFLECTED:
            case CSRF_PROTECTION:
            case NESTED_OBJECT_TESTING:
            case ARRAY_BOUNDARY_TESTING:
            case LOAD_TESTING_LIGHT:
                return 30;

            case XML_EXTERNAL_ENTITY:
            case DESERIALIZATION_ATTACK:
            case FILE_UPLOAD_MALICIOUS:
            case BUFFER_OVERFLOW_TEST:
            case DATA_EXPOSURE_TEST:
            case PRIVILEGE_ESCALATION:
            case AUTH_BYPASS:
            case LOAD_TESTING_HEAVY:
                return 60;

            case STRESS_TESTING:
            case CONCURRENCY_RACE_CONDITIONS:
                return 120;

            default:
                return 30;
        }
    }

    // Test senaryosunun gerektirdiği minimum test case sayısı
    public int getMinimumTestCases() {
        switch(this) {
            case HAPPY_PATH:
                return 1;

            case ERROR_HANDLING:
            case INPUT_VALIDATION_BASIC:
                return 3;

            case BOUNDARY_VALUES:
            case BOUNDARY_VALUE_ANALYSIS:
            case NULL_VALUE_TESTING:
            case REGEX_PATTERN_TESTING:
            case NESTED_OBJECT_TESTING:
            case ARRAY_BOUNDARY_TESTING:
                return 5;

            case SQL_INJECTION_BASIC:
            case XSS_REFLECTED:
            case CSRF_PROTECTION:
            case LOAD_TESTING_LIGHT:
                return 10;

            case XML_EXTERNAL_ENTITY:
            case DESERIALIZATION_ATTACK:
            case FILE_UPLOAD_MALICIOUS:
            case BUFFER_OVERFLOW_TEST:
            case DATA_EXPOSURE_TEST:
            case PRIVILEGE_ESCALATION:
            case AUTH_BYPASS:
            case LOAD_TESTING_HEAVY:
                return 15;

            case STRESS_TESTING:
                return 25;

            case CONCURRENCY_RACE_CONDITIONS:
                return 50;

            default:
                return 5;
        }
    }
}