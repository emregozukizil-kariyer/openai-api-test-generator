package org.example.openapi;

/**
 * Request body bilgisini detaylı tutan sınıf
 */
public class RequestBodyInfo {
    private boolean required;
    private String description;
    private DataConstraints constraints;
    private String exampleData;

    // Getters and Setters
    public boolean isRequired() { return required; }
    public void setRequired(boolean required) { this.required = required; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public DataConstraints getConstraints() { return constraints; }
    public void setConstraints(DataConstraints constraints) { this.constraints = constraints; }

    public String getExampleData() { return exampleData; }
    public void setExampleData(String exampleData) { this.exampleData = exampleData; }
}
