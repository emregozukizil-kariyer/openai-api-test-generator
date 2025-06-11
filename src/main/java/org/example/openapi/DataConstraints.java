package org.example.openapi;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Veri kısıtlarını detaylı tutan sınıf
 */
public class DataConstraints {
    private String type;
    private String format;
    private String pattern;

    // String constraints
    private Integer minLength;
    private Integer maxLength;

    // Numeric constraints
    private BigDecimal minimum;
    private BigDecimal maximum;
    private Boolean exclusiveMinimum;
    private Boolean exclusiveMaximum;
    private BigDecimal multipleOf;

    // Array constraints
    private Integer minItems;
    private Integer maxItems;
    private Boolean uniqueItems;

    // Object constraints
    private Integer minProperties;
    private Integer maxProperties;
    private List<String> requiredFields = new ArrayList<>();

    // Enum values
    private List<String> enumValues = new ArrayList<>();

    // Getters and Setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }

    public String getPattern() { return pattern; }
    public void setPattern(String pattern) { this.pattern = pattern; }

    public Integer getMinLength() { return minLength; }
    public void setMinLength(Integer minLength) { this.minLength = minLength; }

    public Integer getMaxLength() { return maxLength; }
    public void setMaxLength(Integer maxLength) { this.maxLength = maxLength; }

    public BigDecimal getMinimum() { return minimum; }
    public void setMinimum(BigDecimal minimum) { this.minimum = minimum; }

    public BigDecimal getMaximum() { return maximum; }
    public void setMaximum(BigDecimal maximum) { this.maximum = maximum; }

    public Boolean getExclusiveMinimum() { return exclusiveMinimum; }
    public void setExclusiveMinimum(Boolean exclusiveMinimum) { this.exclusiveMinimum = exclusiveMinimum; }

    public Boolean getExclusiveMaximum() { return exclusiveMaximum; }
    public void setExclusiveMaximum(Boolean exclusiveMaximum) { this.exclusiveMaximum = exclusiveMaximum; }

    public BigDecimal getMultipleOf() { return multipleOf; }
    public void setMultipleOf(BigDecimal multipleOf) { this.multipleOf = multipleOf; }

    public Integer getMinItems() { return minItems; }
    public void setMinItems(Integer minItems) { this.minItems = minItems; }

    public Integer getMaxItems() { return maxItems; }
    public void setMaxItems(Integer maxItems) { this.maxItems = maxItems; }

    public Boolean getUniqueItems() { return uniqueItems; }
    public void setUniqueItems(Boolean uniqueItems) { this.uniqueItems = uniqueItems; }

    public Integer getMinProperties() { return minProperties; }
    public void setMinProperties(Integer minProperties) { this.minProperties = minProperties; }

    public Integer getMaxProperties() { return maxProperties; }
    public void setMaxProperties(Integer maxProperties) { this.maxProperties = maxProperties; }

    public List<String> getRequiredFields() { return requiredFields; }
    public void setRequiredFields(List<String> requiredFields) { this.requiredFields = requiredFields; }

    public List<String> getEnumValues() { return enumValues; }
    public void setEnumValues(List<String> enumValues) { this.enumValues = enumValues; }
}