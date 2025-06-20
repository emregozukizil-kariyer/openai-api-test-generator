package org.example.openapi;

import java.util.ArrayList;
import java.util.List;

public class TestGenerationHints {
    private final List<String> hints = new ArrayList<>();
    private boolean enableSecurityTesting = false;
    private boolean enablePerformanceTesting = false;
    private boolean enableBoundaryTesting = true;
    private int maxTestCases = 20;

    public void addHint(String hint) {
        hints.add(hint);
    }

    public List<String> getHints() {
        return new ArrayList<>(hints);
    }

    public boolean isEnableSecurityTesting() { return enableSecurityTesting; }
    public void setEnableSecurityTesting(boolean enableSecurityTesting) {
        this.enableSecurityTesting = enableSecurityTesting;
    }

    public boolean isEnablePerformanceTesting() { return enablePerformanceTesting; }
    public void setEnablePerformanceTesting(boolean enablePerformanceTesting) {
        this.enablePerformanceTesting = enablePerformanceTesting;
    }

    public boolean isEnableBoundaryTesting() { return enableBoundaryTesting; }
    public void setEnableBoundaryTesting(boolean enableBoundaryTesting) {
        this.enableBoundaryTesting = enableBoundaryTesting;
    }

    public int getMaxTestCases() { return maxTestCases; }
    public void setMaxTestCases(int maxTestCases) {
        this.maxTestCases = maxTestCases;
    }

    public void updateFromEndpoint(EndpointInfo endpoint) {
        // Endpoint'ten hint'leri güncelle
        if (endpoint.getMethod().equalsIgnoreCase("POST") ||
                endpoint.getMethod().equalsIgnoreCase("PUT")) {
            setEnableSecurityTesting(true);
        }

        if (endpoint.getParameters().size() > 5) {
            setEnableBoundaryTesting(true);
        }
    }
}