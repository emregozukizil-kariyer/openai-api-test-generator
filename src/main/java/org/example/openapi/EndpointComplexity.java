package org.example.openapi;

/**
 * Endpoint kompleksitesini tutan sınıf
 */
public class EndpointComplexity {
    private int score;
    private String level; // LOW, MEDIUM, HIGH, VERY_HIGH
    private String reasoning;

    // Getters and Setters
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getReasoning() { return reasoning; }
    public void setReasoning(String reasoning) { this.reasoning = reasoning; }
}