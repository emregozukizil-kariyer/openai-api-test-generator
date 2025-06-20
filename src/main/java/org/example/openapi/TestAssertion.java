package org.example.openapi;

public class TestAssertion {
    private final String name;
    private final String description;
    private final String condition;

    public TestAssertion(String name, String description, String condition) {
        this.name = name;
        this.description = description;
        this.condition = condition;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getCondition() { return condition; }

    @Override
    public String toString() {
        return String.format("TestAssertion{name='%s', description='%s', condition='%s'}", name, description, condition);
    }
}