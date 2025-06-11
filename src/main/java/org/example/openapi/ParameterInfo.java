package org.example.openapi;

/**
 * Parameter bilgisini detaylı tutan sınıf
 */
public class ParameterInfo {
    private String name;
    private String location; // query, path, header, cookie
    private boolean required;
    private String description;
    private DataConstraints constraints;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public boolean isRequired() { return required; }
    public void setRequired(boolean required) { this.required = required; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public DataConstraints getConstraints() { return constraints; }
    public void setConstraints(DataConstraints constraints) { this.constraints = constraints; }
}

