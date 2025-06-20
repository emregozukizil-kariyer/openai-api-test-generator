package org.example.openapi;

import java.util.HashMap;
import java.util.Map;

public class TestDataSet {
    private final Map<String, Object> data = new HashMap<>();
    private final Map<String, Object> metadata = new HashMap<>();

    public void addRequestBody(String contentType, String payload) {
        data.put("contentType", contentType);
        data.put("payload", payload);
    }

    public void addMetadata(String key, Object value) {
        metadata.put(key, value);
    }

    public Object getData(String key) {
        return data.get(key);
    }

    public Object getMetadata(String key) {
        return metadata.get(key);
    }

    public Map<String, Object> getAllData() {
        return new HashMap<>(data);
    }

    public Map<String, Object> getAllMetadata() {
        return new HashMap<>(metadata);
    }

    public void addParameterValue(String key, Object value) {
        data.put(key, value);
    }
}