// ===== Gelişmiş Model Sınıfları =====

package org.example.openapi;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Gelişmiş EndpointInfo - Eski kodun tüm özelliklerini içerir
 */
public class EndpointInfo {
    private String path;
    private String method;
    private String operationId;
    private String summary;
    private String description;
    private String resourceType;

    // Parameters
    private boolean hasParameters;
    private List<ParameterInfo> parameters = new ArrayList<>();
    private List<String> requiredParameters = new ArrayList<>();

    // Request Body
    private boolean hasRequestBody;
    private RequestBodyInfo requestBodyInfo;

    // Responses
    private List<ResponseInfo> responses = new ArrayList<>();
    private List<String> expectedStatusCodes = new ArrayList<>();

    // Security
    private boolean requiresAuthentication;
    private List<String> securitySchemes = new ArrayList<>();

    // Dependencies & Complexity
    private List<String> dependencies = new ArrayList<>();
    private EndpointComplexity complexity;

    // Getters and Setters
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public String getOperationId() { return operationId; }
    public void setOperationId(String operationId) { this.operationId = operationId; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getResourceType() { return resourceType; }
    public void setResourceType(String resourceType) { this.resourceType = resourceType; }

    public boolean isHasParameters() { return hasParameters; }
    public void setHasParameters(boolean hasParameters) { this.hasParameters = hasParameters; }

    public List<ParameterInfo> getParameters() { return parameters; }
    public void setParameters(List<ParameterInfo> parameters) { this.parameters = parameters; }

    public List<String> getRequiredParameters() { return requiredParameters; }
    public void setRequiredParameters(List<String> requiredParameters) { this.requiredParameters = requiredParameters; }

    public boolean isHasRequestBody() { return hasRequestBody; }
    public void setHasRequestBody(boolean hasRequestBody) { this.hasRequestBody = hasRequestBody; }

    public RequestBodyInfo getRequestBodyInfo() { return requestBodyInfo; }
    public void setRequestBodyInfo(RequestBodyInfo requestBodyInfo) { this.requestBodyInfo = requestBodyInfo; }

    public List<ResponseInfo> getResponses() { return responses; }
    public void setResponses(List<ResponseInfo> responses) { this.responses = responses; }

    public List<String> getExpectedStatusCodes() { return expectedStatusCodes; }
    public void setExpectedStatusCodes(List<String> expectedStatusCodes) { this.expectedStatusCodes = expectedStatusCodes; }

    public boolean isRequiresAuthentication() { return requiresAuthentication; }
    public void setRequiresAuthentication(boolean requiresAuthentication) { this.requiresAuthentication = requiresAuthentication; }

    public List<String> getSecuritySchemes() { return securitySchemes; }
    public void setSecuritySchemes(List<String> securitySchemes) { this.securitySchemes = securitySchemes; }

    public List<String> getDependencies() { return dependencies; }
    public void setDependencies(List<String> dependencies) { this.dependencies = dependencies; }

    public EndpointComplexity getComplexity() { return complexity; }
    public void setComplexity(EndpointComplexity complexity) { this.complexity = complexity; }
}


