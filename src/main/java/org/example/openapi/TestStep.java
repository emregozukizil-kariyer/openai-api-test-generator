package org.example.openapi;

public class TestStep {
    private final String action;
    private final String description;
    private final int order;

    public TestStep(String action, String description, int order) {
        this.action = action;
        this.description = description;
        this.order = order;
    }

    public String getAction() { return action; }
    public String getDescription() { return description; }
    public int getOrder() { return order; }

    @Override
    public String toString() {
        return String.format("TestStep{action='%s', description='%s', order=%d}", action, description, order);
    }
}