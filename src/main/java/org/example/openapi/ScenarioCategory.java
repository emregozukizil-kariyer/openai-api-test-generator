package org.example.openapi;

public enum ScenarioCategory {
    FUNCTIONAL("Functional", "Functional testing scenarios"),
    SECURITY("Security", "Security testing scenarios"),
    PERFORMANCE("Performance", "Performance testing scenarios"),
    RELIABILITY("Reliability", "Reliability testing scenarios"),
    BOUNDARY("Boundary", "Boundary condition testing scenarios"),
    INTEGRATION("Integration", "Integration testing scenarios"),
    COMPLIANCE("Compliance", "Compliance testing scenarios"),
    ADVANCED("Advanced", "Advanced testing scenarios"),
    CONCURRENCY("Concurrency", "Concurrency testing scenarios"),
    CUSTOM("Custom", "Custom testing scenarios");

    private final String name;
    private final String description;

    ScenarioCategory(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() { 
        return name; 
    }
    
    public String getDescription() { 
        return description; 
    }
}
