package org.example.openapi;

import java.io.File;
import java.util.*;

/**
 * TAM ÖZELLİKLİ Configuration - Eski kodun tüm ayarları
 * Eksik kalan tüm özellikler eklendi:
 * - API key environment variable desteği
 * - Timeout Duration desteği
 * - Tüm test türleri
 * - Gelişmiş validation seçenekleri
 */
public class Configuration {

    // Temel dosya ayarları
    private String inputFile;
    private String outputFile;
    private String testClassName;

    // API ayarları
    private String apiKey;
    private String apiKeyEnvVar;
    private String model;
    private int maxTokens;
    private int maxRetries;
    private int timeoutSeconds;

    // Performance ayarları
    private int threadPoolSize;
    private boolean verbose;
    private boolean useFallbackOnError;

    // API test ayarları
    private String baseUri;

    // Test türleri - TAM LİSTE
    private boolean generateMockTests;
    private boolean includePerformanceTests;
    private boolean includeSecurityTests;
    private boolean generateTestReport;
    private boolean includeContractTests;
    private boolean enableSmartValidation;
    private boolean generateBoundaryTests;
    private boolean includeNegativeTests;
    private boolean enableResponsePatternAnalysis;

    // Eski koddan eksik kalan özellikler
    private boolean generateApiTests = true;
    private boolean generateModelTests = true;
    private boolean generateClientTests = false;
    private boolean enableAsyncTesting = true;
    private boolean enableRetryOnFailure = true;
    private boolean enableDetailedReporting = true;
    private boolean enableTestDataGeneration = true;
    private boolean enableConstraintValidation = true;
    private boolean enableDependencyAnalysis = true;
    private boolean enableComplexityAnalysis = true;

    // OpenAI ayarları - Gelişmiş
    private double temperature = 0.1;
    private double topP = 0.9;
    private int presencePenalty = 0;
    private int frequencyPenalty = 0;

    public Configuration() {
        // Varsayılan değerler - Eski koddan
        this.verbose = false;
        this.threadPoolSize = 4;
        this.maxRetries = 3;
        this.timeoutSeconds = 120;
        this.maxTokens = 4096;
        this.model = "gpt-4";
        this.apiKeyEnvVar = "OPENAI_API_KEY";
        this.generateTestReport = false;
        this.testClassName = "GeneratedApiTests";
        this.baseUri = "https://api.example.com";
        this.useFallbackOnError = true;
    }

    public Configuration(String inputFile, String outputFile) {
        this();
        this.inputFile = inputFile;
        this.outputFile = outputFile;
    }

    // ===== Builder Pattern - Gelişmiş =====

    public static Configuration create() {
        return new Configuration();
    }

    // Temel ayarlar
    public Configuration inputFile(String inputFile) {
        this.inputFile = inputFile;
        return this;
    }

    public Configuration outputFile(String outputFile) {
        this.outputFile = outputFile;
        return this;
    }

    public Configuration testClassName(String testClassName) {
        this.testClassName = testClassName;
        return this;
    }

    // API ayarları
    public Configuration apiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    public Configuration apiKeyEnvVar(String apiKeyEnvVar) {
        this.apiKeyEnvVar = apiKeyEnvVar;
        return this;
    }

    public Configuration model(String model) {
        this.model = model;
        return this;
    }

    public Configuration maxTokens(int maxTokens) {
        this.maxTokens = maxTokens;
        return this;
    }

    public Configuration maxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
        return this;
    }

    public Configuration timeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
        return this;
    }

    // Performance ayarları
    public Configuration threadPoolSize(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
        return this;
    }

    public Configuration verbose(boolean verbose) {
        this.verbose = verbose;
        return this;
    }

    public Configuration useFallbackOnError(boolean useFallbackOnError) {
        this.useFallbackOnError = useFallbackOnError;
        return this;
    }

    public Configuration baseUri(String baseUri) {
        this.baseUri = baseUri;
        return this;
    }

    // Test türleri
    public Configuration generateMockTests(boolean generateMockTests) {
        this.generateMockTests = generateMockTests;
        return this;
    }

    public Configuration includePerformanceTests(boolean includePerformanceTests) {
        this.includePerformanceTests = includePerformanceTests;
        return this;
    }

    public Configuration includeSecurityTests(boolean includeSecurityTests) {
        this.includeSecurityTests = includeSecurityTests;
        return this;
    }

    public Configuration generateTestReport(boolean generateTestReport) {
        this.generateTestReport = generateTestReport;
        return this;
    }

    public Configuration includeContractTests(boolean includeContractTests) {
        this.includeContractTests = includeContractTests;
        return this;
    }

    public Configuration enableSmartValidation(boolean enableSmartValidation) {
        this.enableSmartValidation = enableSmartValidation;
        return this;
    }

    public Configuration generateBoundaryTests(boolean generateBoundaryTests) {
        this.generateBoundaryTests = generateBoundaryTests;
        return this;
    }

    public Configuration includeNegativeTests(boolean includeNegativeTests) {
        this.includeNegativeTests = includeNegativeTests;
        return this;
    }

    public Configuration enableResponsePatternAnalysis(boolean enableResponsePatternAnalysis) {
        this.enableResponsePatternAnalysis = enableResponsePatternAnalysis;
        return this;
    }

    // Gelişmiş özellikler
    public Configuration enableAsyncTesting(boolean enableAsyncTesting) {
        this.enableAsyncTesting = enableAsyncTesting;
        return this;
    }

    public Configuration enableRetryOnFailure(boolean enableRetryOnFailure) {
        this.enableRetryOnFailure = enableRetryOnFailure;
        return this;
    }

    public Configuration enableDetailedReporting(boolean enableDetailedReporting) {
        this.enableDetailedReporting = enableDetailedReporting;
        return this;
    }

    public Configuration enableTestDataGeneration(boolean enableTestDataGeneration) {
        this.enableTestDataGeneration = enableTestDataGeneration;
        return this;
    }

    public Configuration enableConstraintValidation(boolean enableConstraintValidation) {
        this.enableConstraintValidation = enableConstraintValidation;
        return this;
    }

    public Configuration enableDependencyAnalysis(boolean enableDependencyAnalysis) {
        this.enableDependencyAnalysis = enableDependencyAnalysis;
        return this;
    }

    public Configuration enableComplexityAnalysis(boolean enableComplexityAnalysis) {
        this.enableComplexityAnalysis = enableComplexityAnalysis;
        return this;
    }

    // OpenAI gelişmiş ayarları
    public Configuration temperature(double temperature) {
        this.temperature = temperature;
        return this;
    }

    public Configuration topP(double topP) {
        this.topP = topP;
        return this;
    }

    public Configuration presencePenalty(int presencePenalty) {
        this.presencePenalty = presencePenalty;
        return this;
    }

    public Configuration frequencyPenalty(int frequencyPenalty) {
        this.frequencyPenalty = frequencyPenalty;
        return this;
    }

    // ===== Getter Metodları - TAM LİSTE =====

    public String getInputFile() { return inputFile; }
    public String getOutputFile() { return outputFile; }
    public String getTestClassName() { return testClassName; }

    public String getApiKey() { return apiKey; }
    public String getApiKeyEnvVar() { return apiKeyEnvVar; }
    public String getModel() { return model; }
    public int getMaxTokens() { return maxTokens; }
    public int getMaxRetries() { return maxRetries; }
    public int getTimeoutSeconds() { return timeoutSeconds; }

    public int getThreadPoolSize() { return threadPoolSize; }
    public boolean isVerbose() { return verbose; }
    public boolean isUseFallbackOnError() { return useFallbackOnError; }

    public String getBaseUri() { return baseUri; }

    // Test türleri
    public boolean isGenerateMockTests() { return generateMockTests; }
    public boolean isIncludePerformanceTests() { return includePerformanceTests; }
    public boolean isIncludeSecurityTests() { return includeSecurityTests; }
    public boolean isGenerateTestReport() { return generateTestReport; }
    public boolean isIncludeContractTests() { return includeContractTests; }
    public boolean isEnableSmartValidation() { return enableSmartValidation; }
    public boolean isGenerateBoundaryTests() { return generateBoundaryTests; }
    public boolean isIncludeNegativeTests() { return includeNegativeTests; }
    public boolean isEnableResponsePatternAnalysis() { return enableResponsePatternAnalysis; }

    // Gelişmiş özellikler
    public boolean isGenerateApiTests() { return generateApiTests; }
    public boolean isGenerateModelTests() { return generateModelTests; }
    public boolean isGenerateClientTests() { return generateClientTests; }
    public boolean isEnableAsyncTesting() { return enableAsyncTesting; }
    public boolean isEnableRetryOnFailure() { return enableRetryOnFailure; }
    public boolean isEnableDetailedReporting() { return enableDetailedReporting; }
    public boolean isEnableTestDataGeneration() { return enableTestDataGeneration; }
    public boolean isEnableConstraintValidation() { return enableConstraintValidation; }
    public boolean isEnableDependencyAnalysis() { return enableDependencyAnalysis; }
    public boolean isEnableComplexityAnalysis() { return enableComplexityAnalysis; }

    // OpenAI gelişmiş ayarları
    public double getTemperature() { return temperature; }
    public double getTopP() { return topP; }
    public int getPresencePenalty() { return presencePenalty; }
    public int getFrequencyPenalty() { return frequencyPenalty; }

    // ===== Setter Metodları - Geriye Uyumluluk İçin =====

    public void setInputFile(String inputFile) { this.inputFile = inputFile; }
    public void setOutputFile(String outputFile) { this.outputFile = outputFile; }
    public void setTestClassName(String testClassName) { this.testClassName = testClassName; }

    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    public void setApiKeyEnvVar(String apiKeyEnvVar) { this.apiKeyEnvVar = apiKeyEnvVar; }
    public void setModel(String model) { this.model = model; }
    public void setMaxTokens(int maxTokens) { this.maxTokens = maxTokens; }
    public void setMaxRetries(int maxRetries) { this.maxRetries = maxRetries; }
    public void setTimeoutSeconds(int timeoutSeconds) { this.timeoutSeconds = timeoutSeconds; }

    public void setThreadPoolSize(int threadPoolSize) { this.threadPoolSize = threadPoolSize; }
    public void setVerbose(boolean verbose) { this.verbose = verbose; }
    public void setUseFallbackOnError(boolean useFallbackOnError) { this.useFallbackOnError = useFallbackOnError; }

    public void setBaseUri(String baseUri) { this.baseUri = baseUri; }

    public void setGenerateMockTests(boolean generateMockTests) { this.generateMockTests = generateMockTests; }
    public void setIncludePerformanceTests(boolean includePerformanceTests) { this.includePerformanceTests = includePerformanceTests; }
    public void setIncludeSecurityTests(boolean includeSecurityTests) { this.includeSecurityTests = includeSecurityTests; }
    public void setGenerateTestReport(boolean generateTestReport) { this.generateTestReport = generateTestReport; }

    // ===== Validation Metodları =====

    /**
     * Konfigürasyonun geçerliliğini kontrol eder
     */
    public boolean isValid() {
        return inputFile != null && !inputFile.trim().isEmpty() &&
                outputFile != null && !outputFile.trim().isEmpty() &&
                threadPoolSize > 0 &&
                maxRetries >= 0 &&
                timeoutSeconds > 0 &&
                maxTokens > 0;
    }

    /**
     * Eksik ayarları varsayılan değerlerle doldurur
     */
    public void fillDefaults() {
        if (testClassName == null || testClassName.trim().isEmpty()) {
            testClassName = "GeneratedApiTests";
        }

        if (baseUri == null || baseUri.trim().isEmpty()) {
            baseUri = "https://api.example.com";
        }

        if (model == null || model.trim().isEmpty()) {
            model = "gpt-4";
        }

        if (apiKeyEnvVar == null || apiKeyEnvVar.trim().isEmpty()) {
            apiKeyEnvVar = "OPENAI_API_KEY";
        }

        if (threadPoolSize <= 0) {
            threadPoolSize = 4;
        }

        if (maxRetries < 0) {
            maxRetries = 3;
        }

        if (timeoutSeconds <= 0) {
            timeoutSeconds = 120;
        }

        if (maxTokens <= 0) {
            maxTokens = 4096;
        }

        if (temperature < 0 || temperature > 2) {
            temperature = 0.1;
        }

        if (topP < 0 || topP > 1) {
            topP = 0.9;
        }
    }

    /**
     * Gelişmiş validation - Detaylı kontroller
     */
    public ValidationResult validateDetailed() {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        // Zorunlu alanlar
        if (inputFile == null || inputFile.trim().isEmpty()) {
            errors.add("Input file boş olamaz");
        }
        if (outputFile == null || outputFile.trim().isEmpty()) {
            errors.add("Output file boş olamaz");
        }

        // Dosya kontrolü
        if (inputFile != null) {
            File file = new File(inputFile);
            if (!file.exists()) {
                errors.add("Input file bulunamadı: " + inputFile);
            } else if (!file.canRead()) {
                errors.add("Input file okunamıyor: " + inputFile);
            }
        }

        // API ayarları
        if (apiKey != null && apiKey.length() < 10) {
            warnings.add("API key çok kısa görünüyor");
        }

        // Performance ayarları
        if (threadPoolSize > Runtime.getRuntime().availableProcessors() * 2) {
            warnings.add("Thread pool boyutu CPU sayısından fazla: " + threadPoolSize);
        }

        if (maxTokens > 8000) {
            warnings.add("Max tokens çok yüksek, maliyetli olabilir: " + maxTokens);
        }

        // Test türü kontrolleri
        int enabledFeatures = 0;
        if (generateMockTests) enabledFeatures++;
        if (includePerformanceTests) enabledFeatures++;
        if (includeSecurityTests) enabledFeatures++;
        if (includeContractTests) enabledFeatures++;
        if (generateBoundaryTests) enabledFeatures++;
        if (includeNegativeTests) enabledFeatures++;

        if (enabledFeatures == 0) {
            warnings.add("Hiçbir gelişmiş test türü aktif değil");
        } else if (enabledFeatures > 5) {
            warnings.add("Çok fazla test türü aktif, uzun sürebilir: " + enabledFeatures);
        }

        return new ValidationResult(errors.isEmpty(), errors, warnings);
    }

    /**
     * Özellik sayısını döndürür - Debug için
     */
    public Map<String, Object> getFeatureSummary() {
        Map<String, Object> summary = new HashMap<>();

        // Temel ayarlar
        summary.put("inputFile", inputFile);
        summary.put("outputFile", outputFile);
        summary.put("model", model);
        summary.put("threadPoolSize", threadPoolSize);

        // Aktif özellikler
        List<String> activeFeatures = new ArrayList<>();
        if (generateMockTests) activeFeatures.add("Mock Tests");
        if (includePerformanceTests) activeFeatures.add("Performance Tests");
        if (includeSecurityTests) activeFeatures.add("Security Tests");
        if (includeContractTests) activeFeatures.add("Contract Tests");
        if (generateBoundaryTests) activeFeatures.add("Boundary Tests");
        if (includeNegativeTests) activeFeatures.add("Negative Tests");
        if (enableSmartValidation) activeFeatures.add("Smart Validation");
        if (enableResponsePatternAnalysis) activeFeatures.add("Pattern Analysis");

        summary.put("activeFeatures", activeFeatures);
        summary.put("featureCount", activeFeatures.size());

        return summary;
    }

    // ===== ToString ve Equals/HashCode =====

    @Override
    public String toString() {
        return "Configuration{" +
                "inputFile='" + inputFile + '\'' +
                ", outputFile='" + outputFile + '\'' +
                ", model='" + model + '\'' +
                ", threadPoolSize=" + threadPoolSize +
                ", verbose=" + verbose +
                ", activeFeatures=" + getFeatureSummary().get("featureCount") +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Configuration that = (Configuration) o;
        return maxTokens == that.maxTokens &&
                maxRetries == that.maxRetries &&
                threadPoolSize == that.threadPoolSize &&
                verbose == that.verbose &&
                Objects.equals(inputFile, that.inputFile) &&
                Objects.equals(outputFile, that.outputFile) &&
                Objects.equals(model, that.model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inputFile, outputFile, model, maxTokens, maxRetries, threadPoolSize, verbose);
    }

    // ===== Validation Result Inner Class =====

    public static class ValidationResult {
        private final boolean valid;
        private final List<String> errors;
        private final List<String> warnings;

        public ValidationResult(boolean valid, List<String> errors, List<String> warnings) {
            this.valid = valid;
            this.errors = new ArrayList<>(errors);
            this.warnings = new ArrayList<>(warnings);
        }

        public boolean isValid() { return valid; }
        public List<String> getErrors() { return new ArrayList<>(errors); }
        public List<String> getWarnings() { return new ArrayList<>(warnings); }
        public boolean hasWarnings() { return !warnings.isEmpty(); }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("ValidationResult{valid=").append(valid);

            if (!errors.isEmpty()) {
                sb.append(", errors=").append(errors);
            }

            if (!warnings.isEmpty()) {
                sb.append(", warnings=").append(warnings);
            }

            sb.append('}');
            return sb.toString();
        }
    }
}