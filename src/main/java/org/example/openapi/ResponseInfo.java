package org.example.openapi;

/**
 * Response bilgisini detaylı tutan sınıf
 */
public class ResponseInfo {
    private String statusCode;
    private String description;
    private DataConstraints constraints;

    // Getters and Setters
    public String getStatusCode() { return statusCode; }
    public void setStatusCode(String statusCode) { this.statusCode = statusCode; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public DataConstraints getConstraints() { return constraints; }
    public void setConstraints(DataConstraints constraints) { this.constraints = constraints; }
}
