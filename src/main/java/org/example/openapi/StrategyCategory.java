package org.example.openapi;

public enum StrategyCategory {
    FUNCTIONAL("Functional Testing", "Core functionality validation"),
    SECURITY("Security Testing", "Security vulnerability assessment"), 
    PERFORMANCE("Performance Testing", "Performance and scalability validation"),
    RELIABILITY("Reliability Testing", "System reliability and resilience"),
    INTEGRATION("Integration Testing", "System integration validation"),
    COMPLIANCE("Compliance Testing", "Regulatory and standards compliance"),
    ADVANCED("Advanced Testing", "Advanced testing methodologies"),
    CUSTOM("Custom Testing", "Custom and domain-specific testing"),
    HYBRID("Hybrid Testing", "Multi-dimensional hybrid approaches"),
    SPECIALIZED("Specialized Testing", "Technology-specific testing");

    private final String name;
    private final String description;

    StrategyCategory(String name, String description) {
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
