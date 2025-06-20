package org.example.openapi;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.sun.management.OperatingSystemMXBean;

/**
 * ENTERPRISE ReportWriter - Standard Interface Implementation
 *
 * Fully compliant with TestStrategyManager.java standards:
 * - Standard method signatures with ComprehensiveTestSuite
 * - Builder pattern implementation
 * - Enterprise monitoring and caching
 * - Advanced report generation capabilities
 * - Multi-format support with quality metrics
 * - Performance optimization features
 *
 * @version 2.0
 * @since 1.0
 */
public class ReportWriter {

    private static final Logger logger = Logger.getLogger(ReportWriter.class.getName());
    private static final Map<String, ReportCache> reportCache = new ConcurrentHashMap<>();
    private static final Duration CACHE_EXPIRATION = Duration.ofMinutes(30);

    // ===== STANDARD CONFIGURATION =====
    private final Configuration config;
    private final AdvancedReportConfiguration reportConfig;
    private final ExecutorService executorService;

    // ===== STANDARD ENTERPRISE FEATURES =====
    private final Map<String, Object> enterpriseMetrics = new ConcurrentHashMap<>();
    private final List<AdvancedReportMetric> advancedMetrics = new ArrayList<>();
    private final Map<String, ChartDataSet> chartDataSets = new ConcurrentHashMap<>();

    // ===== STANDARD TRACKING =====
    private Instant creationTimestamp;
    private Instant lastReportGeneration;
    private String executionId;
    private int reportCount = 0;

    // ===== STANDARD ENUMS (Aligned with TestStrategyManager) =====

    public enum ReportFormat {
        HTML("html", "Interactive HTML Report", true, true),
        JSON("json", "JSON Data Export", false, true),
        CSV("csv", "CSV Spreadsheet", false, false),
        XML("xml", "XML Data Export", false, true),
        PDF("pdf", "PDF Document", true, false),
        MARKDOWN("md", "Markdown Documentation", true, true),
        CONSOLE("txt", "Console Output", false, false);

        private final String extension;
        private final String description;
        private final boolean supportsInteractive;
        private final boolean supportsCharts;

        ReportFormat(String extension, String description, boolean supportsInteractive, boolean supportsCharts) {
            this.extension = extension;
            this.description = description;
            this.supportsInteractive = supportsInteractive;
            this.supportsCharts = supportsCharts;
        }

        public String getExtension() { return extension; }
        public String getDescription() { return description; }
        public boolean isSupportsInteractive() { return supportsInteractive; }
        public boolean isSupportsCharts() { return supportsCharts; }
    }

    public enum ReportSection {
        EXECUTIVE_SUMMARY("Executive Summary", 1, true),
        TEST_OVERVIEW("Test Overview", 2, true),
        ENDPOINT_ANALYSIS("Endpoint Analysis", 3, true),
        SECURITY_ANALYSIS("Security Analysis", 4, true),
        PERFORMANCE_METRICS("Performance Metrics", 5, true),
        COVERAGE_REPORT("Coverage Report", 6, true),
        QUALITY_METRICS("Quality Metrics", 7, true),
        TEST_RECOMMENDATIONS("Test Recommendations", 8, true),
        TECHNICAL_DETAILS("Technical Details", 9, false),
        APPENDIX("Appendix", 10, false);

        private final String displayName;
        private final int order;
        private final boolean includeByDefault;

        ReportSection(String displayName, int order, boolean includeByDefault) {
            this.displayName = displayName;
            this.order = order;
            this.includeByDefault = includeByDefault;
        }

        public String getDisplayName() { return displayName; }
        public int getOrder() { return order; }
        public boolean isIncludeByDefault() { return includeByDefault; }
    }

    public enum MetricType {
        COUNT("Count", "items", false),
        PERCENTAGE("Percentage", "%", true),
        DURATION("Duration", "ms", false),
        SIZE("Size", "bytes", false),
        RATIO("Ratio", "ratio", true),
        SCORE("Score", "points", true);

        private final String displayName;
        private final String unit;
        private final boolean isDecimal;

        MetricType(String displayName, String unit, boolean isDecimal) {
            this.displayName = displayName;
            this.unit = unit;
            this.isDecimal = isDecimal;
        }

        public String getDisplayName() { return displayName; }
        public String getUnit() { return unit; }
        public boolean isDecimal() { return isDecimal; }
    }

    // ===== STANDARD CONSTRUCTORS =====

    public ReportWriter(Configuration config) {
        this(config, new AdvancedReportConfiguration(config));
    }

    public ReportWriter(Configuration config, AdvancedReportConfiguration reportConfig) {
        this.config = config;
        this.reportConfig = reportConfig;
        this.executorService = createOptimizedExecutorService();
        this.creationTimestamp = Instant.now();
        this.executionId = generateAdvancedExecutionId();

        initializeEnterpriseFeatures();
        validateConfiguration(config, reportConfig);

        logger.info(String.format("ReportWriter initialized with executionId: %s", executionId));
    }

    private void initializeEnterpriseFeatures() {
        enterpriseMetrics.put("creationTimestamp", creationTimestamp);
        enterpriseMetrics.put("toolVersion", "Enhanced OpenAPI Test Generator v2.0.0");
        enterpriseMetrics.put("javaVersion", System.getProperty("java.version"));
        enterpriseMetrics.put("operatingSystem", System.getProperty("os.name"));
        enterpriseMetrics.put("availableProcessors", Runtime.getRuntime().availableProcessors());
        enterpriseMetrics.put("maxMemory", Runtime.getRuntime().maxMemory());
        enterpriseMetrics.put("executionId", executionId);
    }

    private ExecutorService createOptimizedExecutorService() {
        int threadCount = Math.max(2, Runtime.getRuntime().availableProcessors() / 2);
        return Executors.newFixedThreadPool(threadCount);
    }

    private String generateAdvancedExecutionId() {
        return "RPT_" + System.currentTimeMillis() + "_" +
                Integer.toHexString(System.identityHashCode(this)).toUpperCase();
    }

    // ===== STANDARD VALIDATION METHODS =====

    private void validateConfiguration(Configuration config, AdvancedReportConfiguration reportConfig) {
        if (config == null) {
            throw new IllegalArgumentException("Configuration cannot be null");
        }
        if (reportConfig == null) {
            throw new IllegalArgumentException("Report configuration cannot be null");
        }

        List<String> errors = new ArrayList<>();

        if (reportConfig.getEnabledFormats().isEmpty()) {
            errors.add("At least one report format must be enabled");
        }

        if (reportConfig.getMaxReportSize() <= 0) {
            errors.add("Maximum report size must be positive");
        }

        if (!errors.isEmpty()) {
            String errorMessage = "Report configuration validation failed: " + String.join(", ", errors);
            logger.severe(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        logger.fine("Report configuration validation completed successfully");
    }

    // ===== STANDARD MAIN GENERATION METHODS =====

    /**
     * Standard method - Generates comprehensive report from test suite
     */
    public ComprehensiveReportResult generateComprehensiveReport(ComprehensiveTestSuite testSuite) {
        return generateComprehensiveReport(Collections.singletonList(testSuite));
    }

    /**
     * Standard method - Generates comprehensive report from multiple test suites
     */
    public ComprehensiveReportResult generateComprehensiveReport(List<ComprehensiveTestSuite> testSuites) {
        if (!shouldGenerateReport()) {
            logger.info("Report generation disabled, skipping...");
            return ComprehensiveReportResult.builder()
                    .withSuccess(false)
                    .withMessage("Report generation disabled")
                    .withExecutionId(executionId)
                    .build();
        }

        long startTime = System.currentTimeMillis();
        this.lastReportGeneration = Instant.now();
        this.reportCount++;

        logger.info(String.format("Generating comprehensive report for %d test suites (execution: %s)",
                testSuites.size(), executionId));

        try {
            // Check cache first
            String cacheKey = generateAdvancedCacheKey(testSuites);
            ComprehensiveReportResult cachedResult = getCachedReport(cacheKey);
            if (cachedResult != null) {
                logger.info("Returning cached report result");
                return cachedResult;
            }

            // Analyze and collect data
            AdvancedReportData reportData = analyzeComprehensiveTestData(testSuites);

            // Generate reports in all enabled formats
            List<GeneratedReport> reports = generateReportsInAllFormats(reportData);

            // Generate dashboard if enabled
            if (reportConfig.isEnableDashboard()) {
                GeneratedReport dashboard = generateInteractiveDashboard(reportData);
                if (dashboard != null) {
                    reports.add(dashboard);
                }
            }

            long endTime = System.currentTimeMillis();
            Duration generationTime = Duration.ofMillis(endTime - startTime);

            // Update enterprise metrics
            updateEnterpriseMetrics(generationTime, reports);

            ComprehensiveReportResult result = ComprehensiveReportResult.builder()
                    .withSuccess(true)
                    .withMessage("Report generation completed successfully")
                    .withReports(reports)
                    .withReportData(reportData)
                    .withQualityMetrics(calculateQualityMetrics(reportData))
                    .withGenerationTime(generationTime)
                    .withExecutionId(executionId)
                    .withReportCount(reportCount)
                    .build();

            // Cache the result
            cacheReport(cacheKey, result);

            logger.info(String.format("Report generation completed in %dms, generated %d files",
                    generationTime.toMillis(), reports.size()));

            return result;

        } catch (Exception e) {
            String errorMessage = "Comprehensive report generation failed: " + e.getMessage();
            logger.log(Level.SEVERE, errorMessage, e);

            return ComprehensiveReportResult.builder()
                    .withSuccess(false)
                    .withMessage(errorMessage)
                    .withExecutionId(executionId)
                    .build();
        }
    }

    /**
     * Standard method - Generates report from endpoint list (legacy compatibility)
     */
    public ComprehensiveReportResult generateReport(List<EndpointInfo> endpoints,
                                                    AdvancedStrategyRecommendation recommendation) {
        if (!shouldGenerateReport()) {
            logger.info("Report generation disabled, skipping...");
            return ComprehensiveReportResult.builder()
                    .withSuccess(false)
                    .withMessage("Report generation disabled")
                    .build();
        }

        try {
            // Convert endpoints to test suites
            List<ComprehensiveTestSuite> testSuites = convertEndpointsToTestSuites(endpoints, recommendation);
            return generateComprehensiveReport(testSuites);

        } catch (Exception e) {
            String errorMessage = "Legacy report generation failed: " + e.getMessage();
            logger.log(Level.WARNING, errorMessage, e);

            return ComprehensiveReportResult.builder()
                    .withSuccess(false)
                    .withMessage(errorMessage)
                    .build();
        }
    }

    /**
     * Builder pattern for creating report configurations
     */
    public static Builder builder() {
        return new Builder();
    }

    // ===== STANDARD BUILDER PATTERN =====

    public static class Builder {
        private Configuration config;
        private AdvancedReportConfiguration reportConfig;

        private Builder() {}

        public Builder withConfiguration(Configuration config) {
            this.config = config;
            return this;
        }

        public Builder withReportConfiguration(AdvancedReportConfiguration reportConfig) {
            this.reportConfig = reportConfig;
            return this;
        }

        public Builder withFormat(ReportFormat format) {
            if (this.reportConfig == null) {
                this.reportConfig = new AdvancedReportConfiguration();
            }
            this.reportConfig.addEnabledFormat(format);
            return this;
        }

        public Builder withDashboard(boolean enabled) {
            if (this.reportConfig == null) {
                this.reportConfig = new AdvancedReportConfiguration();
            }
            this.reportConfig.setEnableDashboard(enabled);
            return this;
        }

        public ReportWriter build() {
            if (config == null) {
                throw new IllegalArgumentException("Configuration is required");
            }
            if (reportConfig == null) {
                reportConfig = new AdvancedReportConfiguration(config);
            }
            return new ReportWriter(config, reportConfig);
        }
    }

    // ===== STANDARD DATA ANALYSIS METHODS =====

    private AdvancedReportData analyzeComprehensiveTestData(List<ComprehensiveTestSuite> testSuites) {
        logger.info("Analyzing comprehensive test data...");

        AdvancedReportData data = new AdvancedReportData();

        // Basic statistics
        data.setTotalTestSuites(testSuites.size());
        data.setTotalEndpoints(testSuites.stream().mapToInt(ts -> 1).sum()); // Each suite = 1 endpoint
        data.setTotalTestCases(testSuites.stream().mapToInt(ts -> ts.getTestCases().size()).sum());
        data.setGenerationTime(calculateTotalGenerationTime(testSuites));

        // Quality analysis
        data.setQualityAnalysis(analyzeQualityMetrics(testSuites));

        // Security analysis
        data.setSecurityAnalysis(analyzeSecurityProfiles(testSuites));

        // Performance analysis
        data.setPerformanceAnalysis(analyzePerformanceProfiles(testSuites));

        // Coverage analysis
        data.setCoverageAnalysis(analyzeCoverageMetrics(testSuites));

        // Strategy analysis
        data.setStrategyAnalysis(analyzeStrategyRecommendations(testSuites));

        // Generate advanced metrics
        generateAdvancedMetrics(data);
        generateAdvancedCharts(data);

        logger.info("Advanced data analysis completed");
        return data;
    }

    private QualityAnalysis analyzeQualityMetrics(List<ComprehensiveTestSuite> testSuites) {
        QualityAnalysis analysis = new QualityAnalysis();

        // Calculate average quality score
        double avgQualityScore = testSuites.stream()
                .mapToDouble(ComprehensiveTestSuite::getQualityScore)
                .average()
                .orElse(0.0);
        analysis.setAverageQualityScore(avgQualityScore);

        // Quality distribution
        Map<String, Integer> qualityDistribution = testSuites.stream()
                .collect(Collectors.groupingBy(
                        ts -> getQualityCategory(ts.getQualityScore()),
                        Collectors.collectingAndThen(Collectors.counting(), Math::toIntExact)
                ));
        analysis.setQualityDistribution(qualityDistribution);

        // High quality test suites
        List<ComprehensiveTestSuite> highQualitySuites = testSuites.stream()
                .filter(ts -> ts.getQualityScore() > 0.8)
                .collect(Collectors.toList());
        analysis.setHighQualityTestSuites(highQualitySuites);

        return analysis;
    }

    private SecurityAnalysis analyzeSecurityProfiles(List<ComprehensiveTestSuite> testSuites) {
        SecurityAnalysis analysis = new SecurityAnalysis();

        // Count security-enabled test suites
        long securityEnabledCount = testSuites.stream()
                .filter(ts -> ts.getSecurityProfile() != null)
                .count();
        analysis.setSecurityEnabledTestSuites((int) securityEnabledCount);

        // Calculate average security score
        double avgSecurityScore = testSuites.stream()
                .filter(ts -> ts.getSecurityProfile() != null)
                .mapToDouble(ts -> ts.getSecurityProfile().getSecurityScore())
                .average()
                .orElse(0.0);
        analysis.setAverageSecurityScore(avgSecurityScore);

        // High-risk test suites
        List<ComprehensiveTestSuite> highRiskSuites = testSuites.stream()
                .filter(ts -> ts.getSecurityProfile() != null)
                .collect(Collectors.toList());
        analysis.setHighRiskTestSuites(highRiskSuites);

        return analysis;
    }

    private PerformanceAnalysis analyzePerformanceProfiles(List<ComprehensiveTestSuite> testSuites) {
        PerformanceAnalysis analysis = new PerformanceAnalysis();

        // Count performance-enabled test suites
        long performanceEnabledCount = testSuites.stream()
                .filter(ts -> ts.getPerformanceProfile() != null)
                .count();
        analysis.setPerformanceEnabledTestSuites((int) performanceEnabledCount);

        // Calculate average performance score
        double avgPerformanceScore = testSuites.stream()
                .filter(ts -> ts.getPerformanceProfile() != null)
                .mapToDouble(ts -> ts.getPerformanceProfile().getPerformanceScore())
                .average()
                .orElse(0.0);
        analysis.setAveragePerformanceScore(avgPerformanceScore);

        // Performance-critical test suites
        List<ComprehensiveTestSuite> criticalSuites = testSuites.stream()
                .filter(ts -> ts.getPerformanceProfile() != null &&
                        ts.getPerformanceProfile().isCriticalPath())
                .collect(Collectors.toList());
        analysis.setPerformanceCriticalTestSuites(criticalSuites);

        return analysis;
    }

    private CoverageAnalysis analyzeCoverageMetrics(List<ComprehensiveTestSuite> testSuites) {
        CoverageAnalysis analysis = new CoverageAnalysis();

        // Calculate strategy coverage
        Set<StrategyType> allStrategies = Arrays.stream(StrategyType.values()).collect(Collectors.toSet());
        Set<StrategyType> usedStrategies = testSuites.stream()
                .map(ts -> ts.getRecommendation().getPrimaryStrategy())
                .collect(Collectors.toSet());

        double strategyCoverage = allStrategies.isEmpty() ? 100.0 :
                (double) usedStrategies.size() / allStrategies.size() * 100.0;
        analysis.setStrategyCoverage(strategyCoverage);

        // Calculate scenario coverage
        Set<TestGenerationScenario> allScenarios = Arrays.stream(TestGenerationScenario.values())
                .collect(Collectors.toSet());
        Set<TestGenerationScenario> usedScenarios = testSuites.stream()
                .flatMap(ts -> ts.getTestCases().stream())
                .map(GeneratedTestCase::getScenario)
                .collect(Collectors.toSet());

        double scenarioCoverage = allScenarios.isEmpty() ? 100.0 :
                (double) usedScenarios.size() / allScenarios.size() * 100.0;
        analysis.setScenarioCoverage(scenarioCoverage);

        // Overall coverage
        analysis.setOverallCoverage((strategyCoverage + scenarioCoverage) / 2.0);

        return analysis;
    }

    private StrategyAnalysis analyzeStrategyRecommendations(List<ComprehensiveTestSuite> testSuites) {
        StrategyAnalysis analysis = new StrategyAnalysis();

        // Strategy distribution
        Map<StrategyType, Integer> strategyDistribution = testSuites.stream()
                .collect(Collectors.groupingBy(
                        ts -> ts.getRecommendation().getPrimaryStrategy(),
                        Collectors.collectingAndThen(Collectors.counting(), Math::toIntExact)
                ));
        analysis.setStrategyDistribution(strategyDistribution);

        // Most used strategy
        Optional<Map.Entry<StrategyType, Integer>> mostUsed = strategyDistribution.entrySet().stream()
                .max(Map.Entry.comparingByValue());
        if (mostUsed.isPresent()) {
            analysis.setMostUsedStrategy(mostUsed.get().getKey());
        }

        // Average confidence score
        double avgConfidence = testSuites.stream()
                .mapToDouble(ts -> ts.getRecommendation().getConfidenceScore())
                .average()
                .orElse(0.0);
        analysis.setAverageConfidenceScore(avgConfidence);

        return analysis;
    }

    // ===== STANDARD REPORT GENERATION METHODS =====

    private List<GeneratedReport> generateReportsInAllFormats(AdvancedReportData data) {
        List<GeneratedReport> reports = new ArrayList<>();
        List<Future<GeneratedReport>> futures = new ArrayList<>();

        // Generate reports in parallel for better performance
        for (ReportFormat format : reportConfig.getEnabledFormats()) {
            Future<GeneratedReport> future = executorService.submit(() ->
                    generateReportInFormat(data, format));
            futures.add(future);
        }

        // Collect results
        for (Future<GeneratedReport> future : futures) {
            try {
                GeneratedReport report = future.get();
                if (report != null) {
                    reports.add(report);
                }
            } catch (Exception e) {
                logger.log(Level.WARNING, "Error generating report", e);
            }
        }

        logger.info(String.format("Generated %d reports in %d formats",
                reports.size(), reportConfig.getEnabledFormats().size()));

        return reports;
    }

    private GeneratedReport generateReportInFormat(AdvancedReportData data, ReportFormat format) {
        try {
            long startTime = System.currentTimeMillis();

            GeneratedReport report = switch (format) {
                case HTML -> generateAdvancedHtmlReport(data);
                case JSON -> generateAdvancedJsonReport(data);
                case CSV -> generateAdvancedCsvReport(data);
                case MARKDOWN -> generateAdvancedMarkdownReport(data);
                case XML -> generateAdvancedXmlReport(data);
                default -> {
                    logger.warning("Unsupported report format: " + format);
                    yield null;
                }
            };

            if (report != null) {
                long endTime = System.currentTimeMillis();
                report.setGenerationTime(Duration.ofMillis(endTime - startTime));

                logger.info(String.format("Generated %s report in %dms: %s",
                        format, endTime - startTime, report.getFileName()));
            }

            return report;

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to generate " + format + " report", e);
            return null;
        }
    }

    private GeneratedReport generateAdvancedHtmlReport(AdvancedReportData data) throws IOException {
        String reportPath = createAdvancedReportPath("comprehensive_report", ReportFormat.HTML);

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(reportPath), StandardCharsets.UTF_8)) {
            writeAdvancedHtmlHeader(writer);
            writeAdvancedExecutiveSummary(writer, data);
            writeAdvancedTestOverview(writer, data);
            writeAdvancedQualityAnalysis(writer, data);
            writeAdvancedSecurityAnalysis(writer, data);
            writeAdvancedPerformanceAnalysis(writer, data);
            writeAdvancedCoverageAnalysis(writer, data);
            writeAdvancedStrategyAnalysis(writer, data);
            writeAdvancedRecommendations(writer, data);
            writeAdvancedTechnicalDetails(writer, data);
            writeAdvancedHtmlFooter(writer);
        }

        return new GeneratedReport(reportPath, ReportFormat.HTML, new File(reportPath).length());
    }

    private GeneratedReport generateAdvancedJsonReport(AdvancedReportData data) throws IOException {
        String reportPath = createAdvancedReportPath("comprehensive_report", ReportFormat.JSON);

        // Ensure directory exists
        new File(reportPath).getParentFile().mkdirs();

        try (FileWriter writer = new FileWriter(reportPath, StandardCharsets.UTF_8)) {
            writer.write("{\n");
            writer.write("  \"executionId\": \"" + executionId + "\",\n");
            writer.write("  \"generationTimestamp\": \"" + Instant.now() + "\",\n");
            writer.write("  \"summary\": {\n");
            writer.write("    \"totalTestSuites\": " + data.getTotalTestSuites() + ",\n");
            writer.write("    \"totalTestCases\": " + data.getTotalTestCases() + ",\n");
            writer.write("    \"totalEndpoints\": " + data.getTotalEndpoints() + ",\n");
            writer.write("    \"generationTime\": " + data.getGenerationTime() + ",\n");
            writer.write("    \"averageQualityScore\": " + data.getQualityAnalysis().getAverageQualityScore() + "\n");
            writer.write("  },\n");
            writer.write("  \"qualityAnalysis\": {\n");
            writer.write("    \"averageQualityScore\": " + data.getQualityAnalysis().getAverageQualityScore() + ",\n");
            writer.write("    \"qualityDistribution\": " + mapToJson(data.getQualityAnalysis().getQualityDistribution()) + "\n");
            writer.write("  },\n");
            writer.write("  \"securityAnalysis\": {\n");
            writer.write("    \"securityEnabledTestSuites\": " + data.getSecurityAnalysis().getSecurityEnabledTestSuites() + ",\n");
            writer.write("    \"averageSecurityScore\": " + data.getSecurityAnalysis().getAverageSecurityScore() + "\n");
            writer.write("  },\n");
            writer.write("  \"coverageAnalysis\": {\n");
            writer.write("    \"strategyCoverage\": " + data.getCoverageAnalysis().getStrategyCoverage() + ",\n");
            writer.write("    \"scenarioCoverage\": " + data.getCoverageAnalysis().getScenarioCoverage() + ",\n");
            writer.write("    \"overallCoverage\": " + data.getCoverageAnalysis().getOverallCoverage() + "\n");
            writer.write("  }\n");
            writer.write("}\n");
        }

        return new GeneratedReport(reportPath, ReportFormat.JSON, new File(reportPath).length());
    }

    private GeneratedReport generateAdvancedCsvReport(AdvancedReportData data) throws IOException {
        String reportPath = createAdvancedReportPath("test_suite_summary", ReportFormat.CSV);

        new File(reportPath).getParentFile().mkdirs();

        try (FileWriter writer = new FileWriter(reportPath, StandardCharsets.UTF_8)) {
            writer.write("Metric,Value,Category\n");
            writer.write("Total Test Suites," + data.getTotalTestSuites() + ",Summary\n");
            writer.write("Total Test Cases," + data.getTotalTestCases() + ",Summary\n");
            writer.write("Total Endpoints," + data.getTotalEndpoints() + ",Summary\n");
            writer.write("Generation Time (ms)," + data.getGenerationTime() + ",Performance\n");
            writer.write("Average Quality Score," + String.format("%.3f", data.getQualityAnalysis().getAverageQualityScore()) + ",Quality\n");
            writer.write("Security Enabled Suites," + data.getSecurityAnalysis().getSecurityEnabledTestSuites() + ",Security\n");
            writer.write("Strategy Coverage (%)," + String.format("%.2f", data.getCoverageAnalysis().getStrategyCoverage()) + ",Coverage\n");
            writer.write("Scenario Coverage (%)," + String.format("%.2f", data.getCoverageAnalysis().getScenarioCoverage()) + ",Coverage\n");
            writer.write("Overall Coverage (%)," + String.format("%.2f", data.getCoverageAnalysis().getOverallCoverage()) + ",Coverage\n");
        }

        return new GeneratedReport(reportPath, ReportFormat.CSV, new File(reportPath).length());
    }

    private GeneratedReport generateAdvancedMarkdownReport(AdvancedReportData data) throws IOException {
        String reportPath = createAdvancedReportPath("comprehensive_report", ReportFormat.MARKDOWN);

        new File(reportPath).getParentFile().mkdirs();

        try (FileWriter writer = new FileWriter(reportPath, StandardCharsets.UTF_8)) {
            writer.write("# 🚀 Comprehensive API Test Report\n\n");
            writer.write("**Execution ID:** " + executionId + "\n");
            writer.write("**Generated:** " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n\n");

            writer.write("## 📊 Executive Summary\n\n");
            writer.write("- **Total Test Suites**: " + data.getTotalTestSuites() + "\n");
            writer.write("- **Total Test Cases**: " + data.getTotalTestCases() + "\n");
            writer.write("- **Total Endpoints**: " + data.getTotalEndpoints() + "\n");
            writer.write("- **Generation Time**: " + data.getGenerationTime() + "ms\n");
            writer.write("- **Average Quality Score**: " + String.format("%.3f", data.getQualityAnalysis().getAverageQualityScore()) + "\n\n");

            writer.write("## 🎯 Quality Analysis\n\n");
            writer.write("| Quality Category | Count |\n");
            writer.write("|------------------|-------|\n");
            for (Map.Entry<String, Integer> entry : data.getQualityAnalysis().getQualityDistribution().entrySet()) {
                writer.write("| " + entry.getKey() + " | " + entry.getValue() + " |\n");
            }

            writer.write("\n## 🔒 Security Analysis\n\n");
            writer.write("- **Security-Enabled Test Suites**: " + data.getSecurityAnalysis().getSecurityEnabledTestSuites() + "\n");
            writer.write("- **Average Security Score**: " + String.format("%.3f", data.getSecurityAnalysis().getAverageSecurityScore()) + "\n");
            writer.write("- **High-Risk Test Suites**: " + data.getSecurityAnalysis().getHighRiskTestSuites().size() + "\n\n");

            writer.write("## 📈 Coverage Analysis\n\n");
            writer.write("- **Strategy Coverage**: " + String.format("%.1f%%", data.getCoverageAnalysis().getStrategyCoverage()) + "\n");
            writer.write("- **Scenario Coverage**: " + String.format("%.1f%%", data.getCoverageAnalysis().getScenarioCoverage()) + "\n");
            writer.write("- **Overall Coverage**: " + String.format("%.1f%%", data.getCoverageAnalysis().getOverallCoverage()) + "\n\n");

            writer.write("## 🎯 Strategy Analysis\n\n");
            writer.write("| Strategy | Usage Count |\n");
            writer.write("|----------|-------------|\n");
            for (Map.Entry<StrategyType, Integer> entry : data.getStrategyAnalysis().getStrategyDistribution().entrySet()) {
                writer.write("| " + entry.getKey().name() + " | " + entry.getValue() + " |\n");
            }

            writer.write("\n---\n");
            writer.write("*Generated by Enhanced OpenAPI Test Generator v2.0.0*\n");
        }

        return new GeneratedReport(reportPath, ReportFormat.MARKDOWN, new File(reportPath).length());
    }

    private GeneratedReport generateAdvancedXmlReport(AdvancedReportData data) throws IOException {
        String reportPath = createAdvancedReportPath("comprehensive_report", ReportFormat.XML);

        new File(reportPath).getParentFile().mkdirs();

        try (FileWriter writer = new FileWriter(reportPath, StandardCharsets.UTF_8)) {
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<comprehensiveTestReport>\n");
            writer.write("  <metadata>\n");
            writer.write("    <executionId>" + executionId + "</executionId>\n");
            writer.write("    <generationTimestamp>" + Instant.now() + "</generationTimestamp>\n");
            writer.write("    <toolVersion>Enhanced OpenAPI Test Generator v2.0.0</toolVersion>\n");
            writer.write("  </metadata>\n");
            writer.write("  <summary>\n");
            writer.write("    <totalTestSuites>" + data.getTotalTestSuites() + "</totalTestSuites>\n");
            writer.write("    <totalTestCases>" + data.getTotalTestCases() + "</totalTestCases>\n");
            writer.write("    <totalEndpoints>" + data.getTotalEndpoints() + "</totalEndpoints>\n");
            writer.write("    <generationTime>" + data.getGenerationTime() + "</generationTime>\n");
            writer.write("  </summary>\n");
            writer.write("  <qualityAnalysis>\n");
            writer.write("    <averageQualityScore>" + data.getQualityAnalysis().getAverageQualityScore() + "</averageQualityScore>\n");
            writer.write("    <qualityDistribution>\n");
            for (Map.Entry<String, Integer> entry : data.getQualityAnalysis().getQualityDistribution().entrySet()) {
                writer.write("      <category name=\"" + entry.getKey() + "\" count=\"" + entry.getValue() + "\" />\n");
            }
            writer.write("    </qualityDistribution>\n");
            writer.write("  </qualityAnalysis>\n");
            writer.write("  <coverageAnalysis>\n");
            writer.write("    <strategyCoverage>" + data.getCoverageAnalysis().getStrategyCoverage() + "</strategyCoverage>\n");
            writer.write("    <scenarioCoverage>" + data.getCoverageAnalysis().getScenarioCoverage() + "</scenarioCoverage>\n");
            writer.write("    <overallCoverage>" + data.getCoverageAnalysis().getOverallCoverage() + "</overallCoverage>\n");
            writer.write("  </coverageAnalysis>\n");
            writer.write("</comprehensiveTestReport>\n");
        }

        return new GeneratedReport(reportPath, ReportFormat.XML, new File(reportPath).length());
    }

    // ===== STANDARD HTML GENERATION METHODS =====

    private void writeAdvancedHtmlHeader(BufferedWriter writer) throws IOException {
        writer.write("<!DOCTYPE html>\n");
        writer.write("<html lang=\"en\">\n");
        writer.write("<head>\n");
        writer.write("    <meta charset=\"UTF-8\">\n");
        writer.write("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        writer.write("    <title>Comprehensive API Test Report - " + executionId + "</title>\n");
        writer.write("    <style>\n");
        writeEnhancedCSS(writer);
        writer.write("    </style>\n");
        writer.write("    <script src=\"https://cdn.jsdelivr.net/npm/chart.js\"></script>\n");
        writer.write("</head>\n");
        writer.write("<body>\n");
        writer.write("    <div class=\"container\">\n");

        // Enhanced header
        writer.write("        <header class=\"report-header\">\n");
        writer.write("            <h1>🚀 Comprehensive API Test Report</h1>\n");
        writer.write("            <div class=\"report-meta\">\n");
        writer.write("                <span><strong>Execution ID:</strong> " + executionId + "</span>\n");
        writer.write("                <span><strong>Generated:</strong> " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "</span>\n");
        writer.write("                <span><strong>Tool:</strong> " + enterpriseMetrics.get("toolVersion") + "</span>\n");
        writer.write("            </div>\n");
        writer.write("        </header>\n");

        // Enhanced navigation
        writer.write("        <nav class=\"report-nav\">\n");
        writer.write("            <a href=\"#summary\">📊 Summary</a>\n");
        writer.write("            <a href=\"#quality\">🎯 Quality</a>\n");
        writer.write("            <a href=\"#security\">🔒 Security</a>\n");
        writer.write("            <a href=\"#performance\">⚡ Performance</a>\n");
        writer.write("            <a href=\"#coverage\">📈 Coverage</a>\n");
        writer.write("            <a href=\"#strategy\">🎯 Strategy</a>\n");
        writer.write("            <a href=\"#recommendations\">💡 Recommendations</a>\n");
        writer.write("        </nav>\n");
    }

    private void writeEnhancedCSS(BufferedWriter writer) throws IOException {
        writer.write("        :root {\n");
        writer.write("            --primary-color: #2196F3;\n");
        writer.write("            --success-color: #4CAF50;\n");
        writer.write("            --warning-color: #FF9800;\n");
        writer.write("            --danger-color: #F44336;\n");
        writer.write("            --info-color: #00BCD4;\n");
        writer.write("            --bg-color: #f8f9fa;\n");
        writer.write("            --card-bg: #ffffff;\n");
        writer.write("            --text-primary: #212529;\n");
        writer.write("            --text-secondary: #6c757d;\n");
        writer.write("            --border-color: #dee2e6;\n");
        writer.write("            --shadow: 0 4px 6px rgba(0, 0, 0, 0.1);\n");
        writer.write("        }\n\n");

        writer.write("        * { margin: 0; padding: 0; box-sizing: border-box; }\n");
        writer.write("        body { font-family: 'Segoe UI', system-ui, -apple-system, sans-serif; background: var(--bg-color); line-height: 1.6; color: var(--text-primary); }\n");
        writer.write("        .container { max-width: 1400px; margin: 0 auto; padding: 20px; }\n");

        // Enhanced header styles
        writer.write("        .report-header { background: linear-gradient(135deg, var(--primary-color), #1976D2); color: white; padding: 40px; border-radius: 12px; margin-bottom: 30px; text-align: center; box-shadow: var(--shadow); }\n");
        writer.write("        .report-header h1 { font-size: 3em; margin-bottom: 15px; font-weight: 700; }\n");
        writer.write("        .report-meta { display: flex; justify-content: center; gap: 40px; font-size: 0.95em; opacity: 0.95; flex-wrap: wrap; }\n");

        // Enhanced navigation
        writer.write("        .report-nav { background: var(--card-bg); padding: 20px; border-radius: 10px; margin-bottom: 30px; box-shadow: var(--shadow); }\n");
        writer.write("        .report-nav a { display: inline-block; padding: 12px 20px; margin: 5px 10px 5px 0; color: var(--primary-color); text-decoration: none; border-radius: 6px; transition: all 0.3s ease; font-weight: 500; }\n");
        writer.write("        .report-nav a:hover { background: var(--primary-color); color: white; transform: translateY(-2px); }\n");

        // Enhanced section styles
        writer.write("        .section { background: var(--card-bg); padding: 30px; margin-bottom: 30px; border-radius: 10px; box-shadow: var(--shadow); }\n");
        writer.write("        .section h2 { color: var(--text-primary); margin-bottom: 25px; border-bottom: 3px solid var(--primary-color); padding-bottom: 15px; font-size: 1.8em; font-weight: 600; }\n");
        writer.write("        .section h3 { color: var(--text-primary); margin: 25px 0 20px 0; font-size: 1.3em; font-weight: 500; }\n");

        // Enhanced metrics grid
        writer.write("        .metrics-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); gap: 25px; margin: 25px 0; }\n");
        writer.write("        .metric-card { background: linear-gradient(135deg, #f8f9fa, #e9ecef); padding: 25px; border-radius: 10px; text-align: center; border-left: 5px solid var(--primary-color); transition: transform 0.3s ease; }\n");
        writer.write("        .metric-card:hover { transform: translateY(-5px); }\n");
        writer.write("        .metric-value { font-size: 2.8em; font-weight: 700; color: var(--primary-color); margin-bottom: 8px; }\n");
        writer.write("        .metric-label { color: var(--text-secondary); font-size: 0.95em; text-transform: uppercase; letter-spacing: 1px; font-weight: 500; }\n");

        // Enhanced status badges
        writer.write("        .status-badge { display: inline-block; padding: 6px 14px; border-radius: 25px; font-size: 0.85em; font-weight: 600; text-transform: uppercase; letter-spacing: 0.5px; }\n");
        writer.write("        .status-excellent { background: var(--success-color); color: white; }\n");
        writer.write("        .status-good { background: var(--info-color); color: white; }\n");
        writer.write("        .status-fair { background: var(--warning-color); color: white; }\n");
        writer.write("        .status-poor { background: var(--danger-color); color: white; }\n");

        // Enhanced progress bars
        writer.write("        .progress-container { margin: 20px 0; }\n");
        writer.write("        .progress-bar { background: #e9ecef; height: 25px; border-radius: 15px; overflow: hidden; position: relative; }\n");
        writer.write("        .progress-fill { height: 100%; background: linear-gradient(90deg, var(--success-color), #66BB6A); transition: width 0.8s ease; border-radius: 15px; }\n");
        writer.write("        .progress-text { text-align: center; margin-top: 8px; font-size: 0.9em; color: var(--text-secondary); font-weight: 500; }\n");

        // Enhanced tables
        writer.write("        .data-table { width: 100%; border-collapse: collapse; margin: 20px 0; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }\n");
        writer.write("        .data-table th { background: var(--primary-color); color: white; padding: 15px; text-align: left; font-weight: 600; }\n");
        writer.write("        .data-table td { padding: 15px; border-bottom: 1px solid var(--border-color); }\n");
        writer.write("        .data-table tr:hover { background: #f8f9fa; }\n");
        writer.write("        .data-table tr:last-child td { border-bottom: none; }\n");

        // Enhanced charts
        writer.write("        .chart-container { position: relative; height: 450px; margin: 30px 0; }\n");
        writer.write("        .chart-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(500px, 1fr)); gap: 40px; margin: 30px 0; }\n");

        // Responsive design
        writer.write("        @media (max-width: 768px) {\n");
        writer.write("            .container { padding: 15px; }\n");
        writer.write("            .metrics-grid { grid-template-columns: 1fr; }\n");
        writer.write("            .chart-grid { grid-template-columns: 1fr; }\n");
        writer.write("            .report-meta { flex-direction: column; gap: 15px; }\n");
        writer.write("            .report-nav a { display: block; margin: 5px 0; }\n");
        writer.write("        }\n");
    }

    private void writeAdvancedExecutiveSummary(BufferedWriter writer, AdvancedReportData data) throws IOException {
        writer.write("        <section id=\"summary\" class=\"section\">\n");
        writer.write("            <h2>📊 Executive Summary</h2>\n");

        // Key metrics grid
        writer.write("            <div class=\"metrics-grid\">\n");
        writer.write("                <div class=\"metric-card\">\n");
        writer.write("                    <div class=\"metric-value\">" + data.getTotalTestSuites() + "</div>\n");
        writer.write("                    <div class=\"metric-label\">Test Suites Generated</div>\n");
        writer.write("                </div>\n");
        writer.write("                <div class=\"metric-card\">\n");
        writer.write("                    <div class=\"metric-value\">" + data.getTotalTestCases() + "</div>\n");
        writer.write("                    <div class=\"metric-label\">Total Test Cases</div>\n");
        writer.write("                </div>\n");
        writer.write("                <div class=\"metric-card\">\n");
        writer.write("                    <div class=\"metric-value\">" + String.format("%.2f", data.getQualityAnalysis().getAverageQualityScore()) + "</div>\n");
        writer.write("                    <div class=\"metric-label\">Average Quality Score</div>\n");
        writer.write("                </div>\n");
        writer.write("                <div class=\"metric-card\">\n");
        writer.write("                    <div class=\"metric-value\">" + data.getGenerationTime() + "ms</div>\n");
        writer.write("                    <div class=\"metric-label\">Generation Time</div>\n");
        writer.write("                </div>\n");
        writer.write("            </div>\n");

        // Overall assessment
        writer.write("            <div style=\"margin-top: 35px;\">\n");
        writer.write("                <h3>Overall Assessment</h3>\n");

        String overallStatus = getOverallQualityStatus(data);
        String statusClass = getQualityStatusClass(overallStatus);

        writer.write("                <p>Test suite quality status: <span class=\"status-badge " + statusClass + "\">" + overallStatus + "</span></p>\n");
        writer.write("                <p>Coverage level: " + String.format("%.1f%%", data.getCoverageAnalysis().getOverallCoverage()) + "</p>\n");
        writer.write("                <p>Security-enabled test suites: " + data.getSecurityAnalysis().getSecurityEnabledTestSuites() + " (" +
                String.format("%.1f%%", (double) data.getSecurityAnalysis().getSecurityEnabledTestSuites() / Math.max(1, data.getTotalTestSuites()) * 100) + ")</p>\n");
        writer.write("            </div>\n");
        writer.write("        </section>\n");
    }

    private void writeAdvancedTestOverview(BufferedWriter writer, AdvancedReportData data) throws IOException {
        writer.write("        <section id=\"overview\" class=\"section\">\n");
        writer.write("            <h2>🧪 Test Overview</h2>\n");

        // Test suite breakdown
        writer.write("            <h3>Test Suite Breakdown</h3>\n");
        writer.write("            <div class=\"metrics-grid\">\n");
        writer.write("                <div class=\"metric-card\">\n");
        writer.write("                    <div class=\"metric-value\">" + data.getTotalTestSuites() + "</div>\n");
        writer.write("                    <div class=\"metric-label\">Total Test Suites</div>\n");
        writer.write("                </div>\n");
        writer.write("                <div class=\"metric-card\">\n");
        writer.write("                    <div class=\"metric-value\">" + data.getTotalEndpoints() + "</div>\n");
        writer.write("                    <div class=\"metric-label\">Endpoints Analyzed</div>\n");
        writer.write("                </div>\n");
        writer.write("                <div class=\"metric-card\">\n");
        writer.write("                    <div class=\"metric-value\">" + String.format("%.1f", (double) data.getTotalTestCases() / Math.max(1, data.getTotalTestSuites())) + "</div>\n");
        writer.write("                    <div class=\"metric-label\">Avg Tests per Suite</div>\n");
        writer.write("                </div>\n");
        writer.write("            </div>\n");

        writer.write("        </section>\n");
    }

    private void writeAdvancedQualityAnalysis(BufferedWriter writer, AdvancedReportData data) throws IOException {
        writer.write("        <section id=\"quality\" class=\"section\">\n");
        writer.write("            <h2>🎯 Quality Analysis</h2>\n");

        // Quality metrics
        writer.write("            <div class=\"metrics-grid\">\n");
        writer.write("                <div class=\"metric-card\">\n");
        writer.write("                    <div class=\"metric-value\">" + String.format("%.3f", data.getQualityAnalysis().getAverageQualityScore()) + "</div>\n");
        writer.write("                    <div class=\"metric-label\">Average Quality Score</div>\n");
        writer.write("                </div>\n");
        writer.write("                <div class=\"metric-card\">\n");
        writer.write("                    <div class=\"metric-value\">" + data.getQualityAnalysis().getHighQualityTestSuites().size() + "</div>\n");
        writer.write("                    <div class=\"metric-label\">High Quality Suites</div>\n");
        writer.write("                </div>\n");
        writer.write("            </div>\n");

        // Quality distribution table
        writer.write("            <h3>Quality Distribution</h3>\n");
        writer.write("            <table class=\"data-table\">\n");
        writer.write("                <thead>\n");
        writer.write("                    <tr><th>Quality Category</th><th>Count</th><th>Percentage</th></tr>\n");
        writer.write("                </thead>\n");
        writer.write("                <tbody>\n");
        for (Map.Entry<String, Integer> entry : data.getQualityAnalysis().getQualityDistribution().entrySet()) {
            double percentage = (double) entry.getValue() / Math.max(1, data.getTotalTestSuites()) * 100;
            writer.write("                    <tr>\n");
            writer.write("                        <td>" + entry.getKey() + "</td>\n");
            writer.write("                        <td>" + entry.getValue() + "</td>\n");
            writer.write("                        <td>" + String.format("%.1f%%", percentage) + "</td>\n");
            writer.write("                    </tr>\n");
        }
        writer.write("                </tbody>\n");
        writer.write("            </table>\n");

        writer.write("        </section>\n");
    }

    private void writeAdvancedSecurityAnalysis(BufferedWriter writer, AdvancedReportData data) throws IOException {
        writer.write("        <section id=\"security\" class=\"section\">\n");
        writer.write("            <h2>🔒 Security Analysis</h2>\n");

        // Security metrics
        writer.write("            <div class=\"metrics-grid\">\n");
        writer.write("                <div class=\"metric-card\">\n");
        writer.write("                    <div class=\"metric-value\">" + data.getSecurityAnalysis().getSecurityEnabledTestSuites() + "</div>\n");
        writer.write("                    <div class=\"metric-label\">Security-Enabled Suites</div>\n");
        writer.write("                </div>\n");
        writer.write("                <div class=\"metric-card\">\n");
        writer.write("                    <div class=\"metric-value\">" + String.format("%.3f", data.getSecurityAnalysis().getAverageSecurityScore()) + "</div>\n");
        writer.write("                    <div class=\"metric-label\">Avg Security Score</div>\n");
        writer.write("                </div>\n");
        writer.write("                <div class=\"metric-card\">\n");
        writer.write("                    <div class=\"metric-value\">" + data.getSecurityAnalysis().getHighRiskTestSuites().size() + "</div>\n");
        writer.write("                    <div class=\"metric-label\">High-Risk Suites</div>\n");
        writer.write("                </div>\n");
        writer.write("            </div>\n");

        writer.write("        </section>\n");
    }

    private void writeAdvancedPerformanceAnalysis(BufferedWriter writer, AdvancedReportData data) throws IOException {
        writer.write("        <section id=\"performance\" class=\"section\">\n");
        writer.write("            <h2>⚡ Performance Analysis</h2>\n");

        // Performance metrics
        writer.write("            <div class=\"metrics-grid\">\n");
        writer.write("                <div class=\"metric-card\">\n");
        writer.write("                    <div class=\"metric-value\">" + data.getPerformanceAnalysis().getPerformanceEnabledTestSuites() + "</div>\n");
        writer.write("                    <div class=\"metric-label\">Performance-Enabled</div>\n");
        writer.write("                </div>\n");
        writer.write("                <div class=\"metric-card\">\n");
        writer.write("                    <div class=\"metric-value\">" + String.format("%.3f", data.getPerformanceAnalysis().getAveragePerformanceScore()) + "</div>\n");
        writer.write("                    <div class=\"metric-label\">Avg Performance Score</div>\n");
        writer.write("                </div>\n");
        writer.write("                <div class=\"metric-card\">\n");
        writer.write("                    <div class=\"metric-value\">" + data.getPerformanceAnalysis().getPerformanceCriticalTestSuites().size() + "</div>\n");
        writer.write("                    <div class=\"metric-label\">Critical Path Suites</div>\n");
        writer.write("                </div>\n");
        writer.write("            </div>\n");

        writer.write("        </section>\n");
    }

    private void writeAdvancedCoverageAnalysis(BufferedWriter writer, AdvancedReportData data) throws IOException {
        writer.write("        <section id=\"coverage\" class=\"section\">\n");
        writer.write("            <h2>📈 Coverage Analysis</h2>\n");

        // Coverage metrics
        writer.write("            <div class=\"metrics-grid\">\n");
        writer.write("                <div class=\"metric-card\">\n");
        writer.write("                    <div class=\"metric-value\">" + String.format("%.1f%%", data.getCoverageAnalysis().getStrategyCoverage()) + "</div>\n");
        writer.write("                    <div class=\"metric-label\">Strategy Coverage</div>\n");
        writer.write("                </div>\n");
        writer.write("                <div class=\"metric-card\">\n");
        writer.write("                    <div class=\"metric-value\">" + String.format("%.1f%%", data.getCoverageAnalysis().getScenarioCoverage()) + "</div>\n");
        writer.write("                    <div class=\"metric-label\">Scenario Coverage</div>\n");
        writer.write("                </div>\n");
        writer.write("                <div class=\"metric-card\">\n");
        writer.write("                    <div class=\"metric-value\">" + String.format("%.1f%%", data.getCoverageAnalysis().getOverallCoverage()) + "</div>\n");
        writer.write("                    <div class=\"metric-label\">Overall Coverage</div>\n");
        writer.write("                </div>\n");
        writer.write("            </div>\n");

        // Coverage progress bars
        writer.write("            <h3>Coverage Details</h3>\n");

        writer.write("            <div class=\"progress-container\">\n");
        writer.write("                <h4>Strategy Coverage</h4>\n");
        writer.write("                <div class=\"progress-bar\">\n");
        writer.write("                    <div class=\"progress-fill\" style=\"width: " + data.getCoverageAnalysis().getStrategyCoverage() + "%\"></div>\n");
        writer.write("                </div>\n");
        writer.write("                <div class=\"progress-text\">" + String.format("%.1f%% of available strategies used", data.getCoverageAnalysis().getStrategyCoverage()) + "</div>\n");
        writer.write("            </div>\n");

        writer.write("            <div class=\"progress-container\">\n");
        writer.write("                <h4>Scenario Coverage</h4>\n");
        writer.write("                <div class=\"progress-bar\">\n");
        writer.write("                    <div class=\"progress-fill\" style=\"width: " + data.getCoverageAnalysis().getScenarioCoverage() + "%\"></div>\n");
        writer.write("                </div>\n");
        writer.write("                <div class=\"progress-text\">" + String.format("%.1f%% of available scenarios covered", data.getCoverageAnalysis().getScenarioCoverage()) + "</div>\n");
        writer.write("            </div>\n");

        writer.write("        </section>\n");
    }

    private void writeAdvancedStrategyAnalysis(BufferedWriter writer, AdvancedReportData data) throws IOException {
        writer.write("        <section id=\"strategy\" class=\"section\">\n");
        writer.write("            <h2>🎯 Strategy Analysis</h2>\n");

        // Strategy metrics
        writer.write("            <div class=\"metrics-grid\">\n");
        writer.write("                <div class=\"metric-card\">\n");
        writer.write("                    <div class=\"metric-value\">" + data.getStrategyAnalysis().getStrategyDistribution().size() + "</div>\n");
        writer.write("                    <div class=\"metric-label\">Strategies Used</div>\n");
        writer.write("                </div>\n");
        writer.write("                <div class=\"metric-card\">\n");
        writer.write("                    <div class=\"metric-value\">" + (data.getStrategyAnalysis().getMostUsedStrategy() != null ? data.getStrategyAnalysis().getMostUsedStrategy().name() : "N/A") + "</div>\n");
        writer.write("                    <div class=\"metric-label\">Most Used Strategy</div>\n");
        writer.write("                </div>\n");
        writer.write("                <div class=\"metric-card\">\n");
        writer.write("                    <div class=\"metric-value\">" + String.format("%.3f", data.getStrategyAnalysis().getAverageConfidenceScore()) + "</div>\n");
        writer.write("                    <div class=\"metric-label\">Avg Confidence</div>\n");
        writer.write("                </div>\n");
        writer.write("            </div>\n");

        // Strategy distribution table
        writer.write("            <h3>Strategy Distribution</h3>\n");
        writer.write("            <table class=\"data-table\">\n");
        writer.write("                <thead>\n");
        writer.write("                    <tr><th>Strategy</th><th>Usage Count</th><th>Percentage</th></tr>\n");
        writer.write("                </thead>\n");
        writer.write("                <tbody>\n");
        for (Map.Entry<StrategyType, Integer> entry : data.getStrategyAnalysis().getStrategyDistribution().entrySet()) {
            double percentage = (double) entry.getValue() / Math.max(1, data.getTotalTestSuites()) * 100;
            writer.write("                    <tr>\n");
            writer.write("                        <td>" + entry.getKey().name() + "</td>\n");
            writer.write("                        <td>" + entry.getValue() + "</td>\n");
            writer.write("                        <td>" + String.format("%.1f%%", percentage) + "</td>\n");
            writer.write("                    </tr>\n");
        }
        writer.write("                </tbody>\n");
        writer.write("            </table>\n");

        writer.write("        </section>\n");
    }

    private void writeAdvancedRecommendations(BufferedWriter writer, AdvancedReportData data) throws IOException {
        writer.write("        <section id=\"recommendations\" class=\"section\">\n");
        writer.write("            <h2>💡 Advanced Recommendations</h2>\n");

        List<String> recommendations = generateAdvancedRecommendations(data);

        writer.write("            <div class=\"recommendations-list\">\n");
        for (int i = 0; i < recommendations.size(); i++) {
            String priority = i < 3 ? "status-poor" : (i < 6 ? "status-fair" : "status-good");
            writer.write("                <div class=\"recommendation-item\" style=\"margin: 15px 0; padding: 20px; border-left: 4px solid var(--primary-color); background: #f8f9fa;\">\n");
            writer.write("                    <span class=\"status-badge " + priority + "\">Priority " + (i + 1) + "</span>\n");
            writer.write("                    <p style=\"margin-top: 10px;\">" + recommendations.get(i) + "</p>\n");
            writer.write("                </div>\n");
        }
        writer.write("            </div>\n");

        writer.write("        </section>\n");
    }

    private void writeAdvancedTechnicalDetails(BufferedWriter writer, AdvancedReportData data) throws IOException {
        writer.write("        <section id=\"technical\" class=\"section\">\n");
        writer.write("            <h2>🔧 Technical Details</h2>\n");

        writer.write("            <h3>Execution Information</h3>\n");
        writer.write("            <table class=\"data-table\">\n");
        writer.write("                <tr><td><strong>Execution ID</strong></td><td>" + executionId + "</td></tr>\n");
        writer.write("                <tr><td><strong>Tool Version</strong></td><td>" + enterpriseMetrics.get("toolVersion") + "</td></tr>\n");
        writer.write("                <tr><td><strong>Java Version</strong></td><td>" + enterpriseMetrics.get("javaVersion") + "</td></tr>\n");
        writer.write("                <tr><td><strong>Operating System</strong></td><td>" + enterpriseMetrics.get("operatingSystem") + "</td></tr>\n");
        writer.write("                <tr><td><strong>Available Processors</strong></td><td>" + enterpriseMetrics.get("availableProcessors") + "</td></tr>\n");
        writer.write("                <tr><td><strong>Generation Time</strong></td><td>" + data.getGenerationTime() + "ms</td></tr>\n");
        writer.write("                <tr><td><strong>Report Count</strong></td><td>" + reportCount + "</td></tr>\n");
        writer.write("            </table>\n");

        writer.write("            <h3>Configuration Details</h3>\n");
        writer.write("            <table class=\"data-table\">\n");
        writer.write("                <tr><td><strong>Enabled Formats</strong></td><td>" + reportConfig.getEnabledFormats().size() + "</td></tr>\n");
        writer.write("                <tr><td><strong>Dashboard Enabled</strong></td><td>" + reportConfig.isEnableDashboard() + "</td></tr>\n");
        writer.write("                <tr><td><strong>Interactive Charts</strong></td><td>" + reportConfig.isEnableInteractiveCharts() + "</td></tr>\n");
        writer.write("                <tr><td><strong>Max Report Size</strong></td><td>" + formatFileSize(reportConfig.getMaxReportSize()) + "</td></tr>\n");
        writer.write("            </table>\n");

        writer.write("        </section>\n");
    }

    private void writeAdvancedHtmlFooter(BufferedWriter writer) throws IOException {
        writer.write("    </div>\n");
        writer.write("    <footer style=\"text-align: center; padding: 30px; color: var(--text-secondary); background: var(--card-bg); margin-top: 40px; border-radius: 10px;\">\n");
        writer.write("        <p><strong>Generated by Enhanced OpenAPI Test Generator v2.0.0</strong></p>\n");
        writer.write("        <p>Execution ID: " + executionId + " | Generated on " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "</p>\n");
        writer.write("        <p>Enterprise-grade test report generation with advanced analytics</p>\n");
        writer.write("    </footer>\n");
        writer.write("</body>\n");
        writer.write("</html>\n");
    }

    // ===== STANDARD DASHBOARD GENERATION =====

    private GeneratedReport generateInteractiveDashboard(AdvancedReportData data) throws IOException {
        String dashboardPath = createAdvancedReportPath("interactive_dashboard", ReportFormat.HTML);

        new File(dashboardPath).getParentFile().mkdirs();

        try (FileWriter writer = new FileWriter(dashboardPath, StandardCharsets.UTF_8)) {
            writer.write("<!DOCTYPE html>\n");
            writer.write("<html lang=\"en\">\n");
            writer.write("<head>\n");
            writer.write("    <meta charset=\"UTF-8\">\n");
            writer.write("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
            writer.write("    <title>Interactive API Test Dashboard - " + executionId + "</title>\n");
            writer.write("    <script src=\"https://cdn.jsdelivr.net/npm/chart.js\"></script>\n");
            writer.write("    <style>\n");
            writer.write("        body { font-family: 'Segoe UI', sans-serif; margin: 0; padding: 20px; background: #f5f7fa; }\n");
            writer.write("        .dashboard { max-width: 1600px; margin: 0 auto; }\n");
            writer.write("        .dashboard-header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; border-radius: 12px; margin-bottom: 30px; text-align: center; }\n");
            writer.write("        .widget { background: white; padding: 25px; margin: 15px; border-radius: 12px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); }\n");
            writer.write("        .widget-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(400px, 1fr)); gap: 20px; }\n");
            writer.write("        .metric-widget { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); color: white; text-align: center; }\n");
            writer.write("        .metric-value { font-size: 3em; font-weight: bold; margin-bottom: 10px; }\n");
            writer.write("        .metric-label { font-size: 1.1em; opacity: 0.9; }\n");
            writer.write("    </style>\n");
            writer.write("</head>\n");
            writer.write("<body>\n");
            writer.write("    <div class=\"dashboard\">\n");
            writer.write("        <div class=\"dashboard-header\">\n");
            writer.write("            <h1>🚀 Interactive API Test Dashboard</h1>\n");
            writer.write("            <p>Real-time comprehensive test analytics and insights</p>\n");
            writer.write("            <p><strong>Execution ID:</strong> " + executionId + "</p>\n");
            writer.write("        </div>\n");

            writer.write("        <div class=\"widget-grid\">\n");
            writer.write("            <div class=\"widget metric-widget\">\n");
            writer.write("                <div class=\"metric-value\">" + data.getTotalTestSuites() + "</div>\n");
            writer.write("                <div class=\"metric-label\">Test Suites Generated</div>\n");
            writer.write("            </div>\n");
            writer.write("            <div class=\"widget metric-widget\">\n");
            writer.write("                <div class=\"metric-value\">" + String.format("%.2f", data.getQualityAnalysis().getAverageQualityScore()) + "</div>\n");
            writer.write("                <div class=\"metric-label\">Average Quality Score</div>\n");
            writer.write("            </div>\n");
            writer.write("            <div class=\"widget metric-widget\">\n");
            writer.write("                <div class=\"metric-value\">" + String.format("%.1f%%", data.getCoverageAnalysis().getOverallCoverage()) + "</div>\n");
            writer.write("                <div class=\"metric-label\">Overall Coverage</div>\n");
            writer.write("            </div>\n");

            writer.write("            <div class=\"widget\">\n");
            writer.write("                <h3>Quality Distribution</h3>\n");
            writer.write("                <canvas id=\"qualityChart\"></canvas>\n");
            writer.write("            </div>\n");

            writer.write("            <div class=\"widget\">\n");
            writer.write("                <h3>Strategy Usage</h3>\n");
            writer.write("                <canvas id=\"strategyChart\"></canvas>\n");
            writer.write("            </div>\n");

            writer.write("            <div class=\"widget\">\n");
            writer.write("                <h3>Coverage Metrics</h3>\n");
            writer.write("                <canvas id=\"coverageChart\"></canvas>\n");
            writer.write("            </div>\n");
            writer.write("        </div>\n");

            writer.write("    </div>\n");

            writer.write("    <script>\n");
            writer.write("        // Quality Distribution Chart\n");
            writer.write("        const qualityCtx = document.getElementById('qualityChart').getContext('2d');\n");
            writer.write("        new Chart(qualityCtx, {\n");
            writer.write("            type: 'doughnut',\n");
            writer.write("            data: {\n");
            writer.write("                labels: " + listToJson(new ArrayList<>(data.getQualityAnalysis().getQualityDistribution().keySet())) + ",\n");
            writer.write("                datasets: [{\n");
            writer.write("                    data: " + listToJson(new ArrayList<>(data.getQualityAnalysis().getQualityDistribution().values())) + ",\n");
            writer.write("                    backgroundColor: ['#ff6384', '#36a2eb', '#cc65fe', '#ffce56', '#4bc0c0']\n");
            writer.write("                }]\n");
            writer.write("            },\n");
            writer.write("            options: { responsive: true, plugins: { title: { display: true, text: 'Quality Distribution' } } }\n");
            writer.write("        });\n");

            writer.write("        // Coverage Chart\n");
            writer.write("        const coverageCtx = document.getElementById('coverageChart').getContext('2d');\n");
            writer.write("        new Chart(coverageCtx, {\n");
            writer.write("            type: 'bar',\n");
            writer.write("            data: {\n");
            writer.write("                labels: ['Strategy Coverage', 'Scenario Coverage', 'Overall Coverage'],\n");
            writer.write("                datasets: [{\n");
            writer.write("                    label: 'Coverage %',\n");
            writer.write("                    data: [" + data.getCoverageAnalysis().getStrategyCoverage() + ", " +
                    data.getCoverageAnalysis().getScenarioCoverage() + ", " +
                    data.getCoverageAnalysis().getOverallCoverage() + "],\n");
            writer.write("                    backgroundColor: ['#36a2eb', '#4bc0c0', '#ff6384']\n");
            writer.write("                }]\n");
            writer.write("            },\n");
            writer.write("            options: { responsive: true, scales: { y: { beginAtZero: true, max: 100 } } }\n");
            writer.write("        });\n");

            writer.write("        console.log('Interactive dashboard loaded with " + data.getTotalTestSuites() + " test suites');\n");
            writer.write("    </script>\n");
            writer.write("</body>\n");
            writer.write("</html>\n");
        }

        return new GeneratedReport(dashboardPath, ReportFormat.HTML, new File(dashboardPath).length());
    }

    // ===== STANDARD HELPER METHODS =====

    private boolean shouldGenerateReport() {
        return config.isGenerateTestReport();
    }

    private String createAdvancedReportPath(String baseName, ReportFormat format) {
        String outputDir = getOutputDirectory();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = String.format("%s_%s_%s.%s", baseName, executionId, timestamp, format.getExtension());

        Path reportsDir = Paths.get(outputDir, "reports");
        try {
            Files.createDirectories(reportsDir);
        } catch (IOException e) {
            logger.warning("Failed to create reports directory: " + e.getMessage());
        }

        return reportsDir.resolve(fileName).toString();
    }

    private String getOutputDirectory() {
        String outputFile = config.getOutputFile();
        if (outputFile != null) {
            File parent = new File(outputFile).getParentFile();
            return parent != null ? parent.getAbsolutePath() : ".";
        }
        return ".";
    }

    private String generateAdvancedCacheKey(List<ComprehensiveTestSuite> testSuites) {
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append("testSuites_").append(testSuites.size()).append("_");

        for (ComprehensiveTestSuite suite : testSuites) {
            keyBuilder.append(suite.getExecutionId()).append("_");
            keyBuilder.append(suite.getTestCases().size()).append("_");
        }

        return Integer.toHexString(keyBuilder.toString().hashCode());
    }

    private void cacheReport(String cacheKey, ComprehensiveReportResult result) {
        try {
            reportCache.put(cacheKey, new ReportCache(result, Instant.now()));
            logger.fine("Cached report result with key: " + cacheKey);
        } catch (Exception e) {
            logger.warning("Failed to cache report: " + e.getMessage());
        }
    }

    private ComprehensiveReportResult getCachedReport(String cacheKey) {
        try {
            ReportCache cached = reportCache.get(cacheKey);
            if (cached != null && !cached.isExpired(CACHE_EXPIRATION)) {
                logger.fine("Retrieved cached report with key: " + cacheKey);
                return cached.getResult();
            }

            // Remove expired cache entry
            if (cached != null) {
                reportCache.remove(cacheKey);
            }
        } catch (Exception e) {
            logger.warning("Error retrieving cached report: " + e.getMessage());
        }

        return null;
    }

    private void updateEnterpriseMetrics(Duration generationTime, List<GeneratedReport> reports) {
        enterpriseMetrics.put("lastGenerationTime", generationTime.toMillis());
        enterpriseMetrics.put("totalReportsGenerated",
                (Integer) enterpriseMetrics.getOrDefault("totalReportsGenerated", 0) + reports.size());
        enterpriseMetrics.put("totalFileSize",
                reports.stream().mapToLong(GeneratedReport::getFileSize).sum());
        enterpriseMetrics.put("lastReportGeneration", lastReportGeneration);

        logger.info(String.format("Updated enterprise metrics: generation time=%dms, reports=%d",
                generationTime.toMillis(), reports.size()));
    }

    private QualityMetrics calculateQualityMetrics(AdvancedReportData data) {
        return QualityMetrics.builder()
                .withCoverageScore(data.getCoverageAnalysis().getOverallCoverage() / 100.0)
                .withQualityScore(data.getQualityAnalysis().getAverageQualityScore())
                .withSecurityScore(data.getSecurityAnalysis().getAverageSecurityScore())
                .withComplexityScore(calculateComplexityScore(data))
                .build();
    }

    private double calculateComplexityScore(AdvancedReportData data) {
        // Calculate complexity score based on test suite distribution and variety
        double strategyVariety = data.getStrategyAnalysis().getStrategyDistribution().size() / 10.0; // Assuming max 10 strategies
        double coverageScore = data.getCoverageAnalysis().getOverallCoverage() / 100.0;
        return Math.min(1.0, (strategyVariety + coverageScore) / 2.0);
    }

    private List<ComprehensiveTestSuite> convertEndpointsToTestSuites(List<EndpointInfo> endpoints,
                                                                      AdvancedStrategyRecommendation recommendation) {
        List<ComprehensiveTestSuite> testSuites = new ArrayList<>();

        for (EndpointInfo endpoint : endpoints) {
            try {
                // Create a mock test suite for each endpoint
                ComprehensiveTestSuite suite = ComprehensiveTestSuite.builder()
                        .withEndpoint(endpoint)
                        .withRecommendation(recommendation != null ? recommendation :
                                createDefaultRecommendation(endpoint))
                        .withExecutionId(generateAdvancedExecutionId())
                        .withGenerationTimestamp(Instant.now())
                        .build();

                testSuites.add(suite);
            } catch (Exception e) {
                logger.warning("Failed to convert endpoint to test suite: " + endpoint.getPath());
            }
        }

        return testSuites;
    }

    private AdvancedStrategyRecommendation createDefaultRecommendation(EndpointInfo endpoint) {
        return AdvancedStrategyRecommendation.builder()
                .withPrimaryStrategy(StrategyType.FUNCTIONAL_BASIC)
                .withConfidenceScore(0.8)
                .withRecommendationReason("Default recommendation for endpoint: " + endpoint.getPath())
                .build();
    }

    private void generateAdvancedMetrics(AdvancedReportData data) {
        advancedMetrics.clear();

        advancedMetrics.add(new AdvancedReportMetric("Total Test Suites", data.getTotalTestSuites(), MetricType.COUNT));
        advancedMetrics.add(new AdvancedReportMetric("Total Test Cases", data.getTotalTestCases(), MetricType.COUNT));
        advancedMetrics.add(new AdvancedReportMetric("Average Quality Score", data.getQualityAnalysis().getAverageQualityScore(), MetricType.SCORE));
        advancedMetrics.add(new AdvancedReportMetric("Strategy Coverage", data.getCoverageAnalysis().getStrategyCoverage(), MetricType.PERCENTAGE));
        advancedMetrics.add(new AdvancedReportMetric("Security Score", data.getSecurityAnalysis().getAverageSecurityScore(), MetricType.SCORE));
        advancedMetrics.add(new AdvancedReportMetric("Generation Time", data.getGenerationTime(), MetricType.DURATION));

        logger.fine("Generated " + advancedMetrics.size() + " advanced metrics");
    }

    private void generateAdvancedCharts(AdvancedReportData data) {
        chartDataSets.clear();

        // Quality distribution chart
        ChartDataSet qualityChart = new ChartDataSet("qualityDistribution", "Quality Distribution");
        qualityChart.setLabels(new ArrayList<>(data.getQualityAnalysis().getQualityDistribution().keySet()));
        qualityChart.setData(new ArrayList<>(data.getQualityAnalysis().getQualityDistribution().values()));
        chartDataSets.put("qualityDistribution", qualityChart);

        // Strategy distribution chart
        ChartDataSet strategyChart = new ChartDataSet("strategyDistribution", "Strategy Usage");
        strategyChart.setLabels(data.getStrategyAnalysis().getStrategyDistribution().keySet().stream()
                .map(StrategyType::name).collect(Collectors.toList()));
        strategyChart.setData(new ArrayList<>(data.getStrategyAnalysis().getStrategyDistribution().values()));
        chartDataSets.put("strategyDistribution", strategyChart);

        logger.fine("Generated " + chartDataSets.size() + " chart datasets");
    }

    private long calculateTotalGenerationTime(List<ComprehensiveTestSuite> testSuites) {
        return testSuites.stream()
                .mapToLong(ts -> ts.getGenerationTimestamp() != null ?
                        ts.getGenerationTimestamp().toEpochMilli() : 0)
                .sum();
    }

    private String getQualityCategory(double qualityScore) {
        if (qualityScore >= 0.9) return "Excellent";
        if (qualityScore >= 0.7) return "Good";
        if (qualityScore >= 0.5) return "Fair";
        return "Poor";
    }

    private String getOverallQualityStatus(AdvancedReportData data) {
        double avgQuality = data.getQualityAnalysis().getAverageQualityScore();
        double coverage = data.getCoverageAnalysis().getOverallCoverage();

        if (avgQuality >= 0.9 && coverage >= 90) return "EXCELLENT";
        if (avgQuality >= 0.7 && coverage >= 75) return "GOOD";
        if (avgQuality >= 0.5 && coverage >= 50) return "FAIR";
        return "NEEDS IMPROVEMENT";
    }

    private String getQualityStatusClass(String status) {
        return switch (status) {
            case "EXCELLENT" -> "status-excellent";
            case "GOOD" -> "status-good";
            case "FAIR" -> "status-fair";
            default -> "status-poor";
        };
    }

    private List<String> generateAdvancedRecommendations(AdvancedReportData data) {
        List<String> recommendations = new ArrayList<>();

        // Quality-based recommendations
        if (data.getQualityAnalysis().getAverageQualityScore() < 0.7) {
            recommendations.add("Improve overall test quality - current average quality score is " +
                    String.format("%.3f", data.getQualityAnalysis().getAverageQualityScore()) +
                    ". Consider enabling more comprehensive test strategies.");
        }

        // Coverage-based recommendations
        if (data.getCoverageAnalysis().getOverallCoverage() < 80) {
            recommendations.add("Increase test coverage - currently at " +
                    String.format("%.1f%%", data.getCoverageAnalysis().getOverallCoverage()) +
                    ". Enable additional test scenarios and strategies.");
        }

        // Strategy-based recommendations
        if (data.getStrategyAnalysis().getStrategyDistribution().size() < 3) {
            recommendations.add("Diversify testing strategies - only " +
                    data.getStrategyAnalysis().getStrategyDistribution().size() +
                    " strategies currently in use. Consider security and performance testing.");
        }

        // Security-based recommendations
        if (data.getSecurityAnalysis().getSecurityEnabledTestSuites() < data.getTotalTestSuites() * 0.5) {
            recommendations.add("Enable security testing for more endpoints - only " +
                    data.getSecurityAnalysis().getSecurityEnabledTestSuites() + " of " +
                    data.getTotalTestSuites() + " test suites have security testing enabled.");
        }

        // Performance-based recommendations
        if (data.getPerformanceAnalysis().getPerformanceEnabledTestSuites() < data.getTotalTestSuites() * 0.3) {
            recommendations.add("Consider performance testing for critical endpoints - only " +
                    data.getPerformanceAnalysis().getPerformanceEnabledTestSuites() +
                    " test suites have performance testing enabled.");
        }

        // General recommendations
        recommendations.add("Regularly review and update test assertions based on business requirements.");
        recommendations.add("Implement continuous integration for automated test execution and reporting.");
        recommendations.add("Consider implementing test data management strategies for better test isolation.");
        recommendations.add("Review test execution times and optimize performance-critical test scenarios.");

        return recommendations;
    }

    // ===== STANDARD UTILITY METHODS =====

    private String mapToJson(Map<String, Integer> map) {
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            if (!first) json.append(",");
            json.append("\"").append(escapeJson(entry.getKey())).append("\":").append(entry.getValue());
            first = false;
        }
        json.append("}");
        return json.toString();
    }

    private String listToJson(List<?> list) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) json.append(",");
            Object item = list.get(i);
            if (item instanceof String) {
                json.append("\"").append(escapeJson(item.toString())).append("\"");
            } else {
                json.append(item.toString());
            }
        }
        json.append("]");
        return json.toString();
    }

    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }

    // ===== STANDARD CLEANUP =====

    public void shutdown() {
        try {
            if (executorService != null && !executorService.isShutdown()) {
                executorService.shutdown();
                logger.info("ReportWriter executor service shutdown completed");
            }
        } catch (Exception e) {
            logger.warning("Error during ReportWriter shutdown: " + e.getMessage());
        }
    }

    // ===== STANDARD GETTERS =====

    public String getExecutionId() { return executionId; }
    public int getReportCount() { return reportCount; }
    public Instant getCreationTimestamp() { return creationTimestamp; }
    public Instant getLastReportGeneration() { return lastReportGeneration; }
    public Map<String, Object> getEnterpriseMetrics() { return new HashMap<>(enterpriseMetrics); }
    public List<AdvancedReportMetric> getAdvancedMetrics() { return new ArrayList<>(advancedMetrics); }

    // ===== LEGACY SUPPORT METHODS =====

    @Deprecated
    public void generateReport(List<EndpointInfo> endpoints, List<String> testCases) {
        logger.info("=== ENHANCED API TEST REPORT (Legacy Mode) ===");
        logger.info("Total Endpoints: " + endpoints.size());
        logger.info("Generated Tests: " + testCases.size());
        logger.info("Input File: " + config.getInputFile());
        logger.info("Output File: " + config.getOutputFile());
        logger.info("Test Framework: " + config.getTestFramework());
        logger.info("Coverage Level: " + config.getCoverageLevel());
        logger.info("Execution ID: " + executionId);

        // Method distribution
        Map<String, Long> methodCount = endpoints.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getMethod().toUpperCase(),
                        Collectors.counting()
                ));

        logger.info("=== HTTP Method Distribution ===");
        methodCount.forEach((method, count) ->
                logger.info("- " + method + ": " + count + " endpoints"));

        // Security summary
        long securitySensitive = endpoints.stream()
                .filter(e -> !e.getSecurityRisks().isEmpty())
                .count();

        logger.info("=== Security Summary ===");
        logger.info("Security-sensitive endpoints: " + securitySensitive);
        logger.info("===============================");

        // Try to generate comprehensive report as well
        try {
            ComprehensiveReportResult result = generateReport(endpoints, (AdvancedStrategyRecommendation) null);
            if (result.isSuccess()) {
                logger.info("Comprehensive report also generated successfully");
            }
        } catch (Exception e) {
            logger.warning("Failed to generate comprehensive report: " + e.getMessage());
        }
    }

    @Deprecated
    public void generateSimpleReport(List<EndpointInfo> endpoints, List<String> testCases) {
        generateReport(endpoints, testCases);
    }

    // ===== INNER CLASSES (Standard Interface Alignment) =====

    public static class AdvancedReportConfiguration {
        private Set<ReportFormat> enabledFormats = EnumSet.of(ReportFormat.HTML);
        private boolean enableDashboard = true;
        private boolean enableInteractiveCharts = true;
        private boolean enableDetailedAnalysis = true;
        private boolean enableCaching = true;
        private long maxReportSize = 50 * 1024 * 1024; // 50MB
        private Duration cacheExpiration = Duration.ofMinutes(30);
        private Set<ReportSection> enabledSections = EnumSet.allOf(ReportSection.class);

        public AdvancedReportConfiguration() {}

        public AdvancedReportConfiguration(Configuration config) {
            if (config.isGenerateTestReport()) {
                enabledFormats.add(ReportFormat.HTML);
                if (config.isGenerateDocumentation()) {
                    enabledFormats.add(ReportFormat.MARKDOWN);
                }
            }
        }

        // Standard getters and setters
        public Set<ReportFormat> getEnabledFormats() { return new HashSet<>(enabledFormats); }
        public void setEnabledFormats(Set<ReportFormat> enabledFormats) { this.enabledFormats = enabledFormats; }
        public void addEnabledFormat(ReportFormat format) { this.enabledFormats.add(format); }
        public boolean isEnableDashboard() { return enableDashboard; }
        public void setEnableDashboard(boolean enableDashboard) { this.enableDashboard = enableDashboard; }
        public boolean isEnableInteractiveCharts() { return enableInteractiveCharts; }
        public void setEnableInteractiveCharts(boolean enableInteractiveCharts) { this.enableInteractiveCharts = enableInteractiveCharts; }
        public boolean isEnableDetailedAnalysis() { return enableDetailedAnalysis; }
        public void setEnableDetailedAnalysis(boolean enableDetailedAnalysis) { this.enableDetailedAnalysis = enableDetailedAnalysis; }
        public boolean isEnableCaching() { return enableCaching; }
        public void setEnableCaching(boolean enableCaching) { this.enableCaching = enableCaching; }
        public long getMaxReportSize() { return maxReportSize; }
        public void setMaxReportSize(long maxReportSize) { this.maxReportSize = maxReportSize; }
        public Duration getCacheExpiration() { return cacheExpiration; }
        public void setCacheExpiration(Duration cacheExpiration) { this.cacheExpiration = cacheExpiration; }
        public Set<ReportSection> getEnabledSections() { return new HashSet<>(enabledSections); }
        public void setEnabledSections(Set<ReportSection> enabledSections) { this.enabledSections = enabledSections; }
    }

    public static class AdvancedReportData {
        private int totalTestSuites;
        private int totalTestCases;
        private int totalEndpoints;
        private long generationTime;
        private QualityAnalysis qualityAnalysis;
        private SecurityAnalysis securityAnalysis;
        private PerformanceAnalysis performanceAnalysis;
        private CoverageAnalysis coverageAnalysis;
        private StrategyAnalysis strategyAnalysis;

        // Standard getters and setters
        public int getTotalTestSuites() { return totalTestSuites; }
        public void setTotalTestSuites(int totalTestSuites) { this.totalTestSuites = totalTestSuites; }
        public int getTotalTestCases() { return totalTestCases; }
        public void setTotalTestCases(int totalTestCases) { this.totalTestCases = totalTestCases; }
        public int getTotalEndpoints() { return totalEndpoints; }
        public void setTotalEndpoints(int totalEndpoints) { this.totalEndpoints = totalEndpoints; }
        public long getGenerationTime() { return generationTime; }
        public void setGenerationTime(long generationTime) { this.generationTime = generationTime; }
        public QualityAnalysis getQualityAnalysis() { return qualityAnalysis; }
        public void setQualityAnalysis(QualityAnalysis qualityAnalysis) { this.qualityAnalysis = qualityAnalysis; }
        public SecurityAnalysis getSecurityAnalysis() { return securityAnalysis; }
        public void setSecurityAnalysis(SecurityAnalysis securityAnalysis) { this.securityAnalysis = securityAnalysis; }
        public PerformanceAnalysis getPerformanceAnalysis() { return performanceAnalysis; }
        public void setPerformanceAnalysis(PerformanceAnalysis performanceAnalysis) { this.performanceAnalysis = performanceAnalysis; }
        public CoverageAnalysis getCoverageAnalysis() { return coverageAnalysis; }
        public void setCoverageAnalysis(CoverageAnalysis coverageAnalysis) { this.coverageAnalysis = coverageAnalysis; }
        public StrategyAnalysis getStrategyAnalysis() { return strategyAnalysis; }
        public void setStrategyAnalysis(StrategyAnalysis strategyAnalysis) { this.strategyAnalysis = strategyAnalysis; }
    }

    public static class QualityAnalysis {
        private double averageQualityScore;
        private Map<String, Integer> qualityDistribution = new HashMap<>();
        private List<ComprehensiveTestSuite> highQualityTestSuites = new ArrayList<>();

        public double getAverageQualityScore() { return averageQualityScore; }
        public void setAverageQualityScore(double averageQualityScore) { this.averageQualityScore = averageQualityScore; }
        public Map<String, Integer> getQualityDistribution() { return qualityDistribution; }
        public void setQualityDistribution(Map<String, Integer> qualityDistribution) { this.qualityDistribution = qualityDistribution; }
        public List<ComprehensiveTestSuite> getHighQualityTestSuites() { return highQualityTestSuites; }
        public void setHighQualityTestSuites(List<ComprehensiveTestSuite> highQualityTestSuites) { this.highQualityTestSuites = highQualityTestSuites; }
    }

    public static class SecurityAnalysis {
        private int securityEnabledTestSuites;
        private double averageSecurityScore;
        private List<ComprehensiveTestSuite> highRiskTestSuites = new ArrayList<>();

        public int getSecurityEnabledTestSuites() { return securityEnabledTestSuites; }
        public void setSecurityEnabledTestSuites(int securityEnabledTestSuites) { this.securityEnabledTestSuites = securityEnabledTestSuites; }
        public double getAverageSecurityScore() { return averageSecurityScore; }
        public void setAverageSecurityScore(double averageSecurityScore) { this.averageSecurityScore = averageSecurityScore; }
        public List<ComprehensiveTestSuite> getHighRiskTestSuites() { return highRiskTestSuites; }
        public void setHighRiskTestSuites(List<ComprehensiveTestSuite> highRiskTestSuites) { this.highRiskTestSuites = highRiskTestSuites; }
    }

    public static class PerformanceAnalysis {
        private int performanceEnabledTestSuites;
        private double averagePerformanceScore;
        private List<ComprehensiveTestSuite> performanceCriticalTestSuites = new ArrayList<>();

        public int getPerformanceEnabledTestSuites() { return performanceEnabledTestSuites; }
        public void setPerformanceEnabledTestSuites(int performanceEnabledTestSuites) { this.performanceEnabledTestSuites = performanceEnabledTestSuites; }
        public double getAveragePerformanceScore() { return averagePerformanceScore; }
        public void setAveragePerformanceScore(double averagePerformanceScore) { this.averagePerformanceScore = averagePerformanceScore; }
        public List<ComprehensiveTestSuite> getPerformanceCriticalTestSuites() { return performanceCriticalTestSuites; }
        public void setPerformanceCriticalTestSuites(List<ComprehensiveTestSuite> performanceCriticalTestSuites) { this.performanceCriticalTestSuites = performanceCriticalTestSuites; }
    }

    public static class CoverageAnalysis {
        private double strategyCoverage;
        private double scenarioCoverage;
        private double overallCoverage;

        public double getStrategyCoverage() { return strategyCoverage; }
        public void setStrategyCoverage(double strategyCoverage) { this.strategyCoverage = strategyCoverage; }
        public double getScenarioCoverage() { return scenarioCoverage; }
        public void setScenarioCoverage(double scenarioCoverage) { this.scenarioCoverage = scenarioCoverage; }
        public double getOverallCoverage() { return overallCoverage; }
        public void setOverallCoverage(double overallCoverage) { this.overallCoverage = overallCoverage; }
    }

    public static class StrategyAnalysis {
        private Map<StrategyType, Integer> strategyDistribution = new HashMap<>();
        private StrategyType mostUsedStrategy;
        private double averageConfidenceScore;

        public Map<StrategyType, Integer> getStrategyDistribution() { return strategyDistribution; }
        public void setStrategyDistribution(Map<StrategyType, Integer> strategyDistribution) { this.strategyDistribution = strategyDistribution; }
        public StrategyType getMostUsedStrategy() { return mostUsedStrategy; }
        public void setMostUsedStrategy(StrategyType mostUsedStrategy) { this.mostUsedStrategy = mostUsedStrategy; }
        public double getAverageConfidenceScore() { return averageConfidenceScore; }
        public void setAverageConfidenceScore(double averageConfidenceScore) { this.averageConfidenceScore = averageConfidenceScore; }
    }

    public static class GeneratedReport {
        private final String filePath;
        private final ReportFormat format;
        private final long fileSize;
        private final LocalDateTime createdAt;
        private Duration generationTime;
        private Map<String, Object> metadata = new HashMap<>();

        public GeneratedReport(String filePath, ReportFormat format, long fileSize) {
            this.filePath = filePath;
            this.format = format;
            this.fileSize = fileSize;
            this.createdAt = LocalDateTime.now();
        }

        public String getFilePath() { return filePath; }
        public ReportFormat getFormat() { return format; }
        public long getFileSize() { return fileSize; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public Duration getGenerationTime() { return generationTime; }
        public void setGenerationTime(Duration generationTime) { this.generationTime = generationTime; }
        public Map<String, Object> getMetadata() { return new HashMap<>(metadata); }
        public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

        public String getFileName() {
            return new File(filePath).getName();
        }

        @Override
        public String toString() {
            return "GeneratedReport{" +
                    "fileName='" + getFileName() + '\'' +
                    ", format=" + format +
                    ", fileSize=" + formatFileSize(fileSize) +
                    ", generationTime=" + (generationTime != null ? generationTime.toMillis() + "ms" : "N/A") +
                    ", createdAt=" + createdAt +
                    '}';
        }

        private String formatFileSize(long bytes) {
            if (bytes < 1024) return bytes + " B";
            if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
            return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        }
    }

    public static class ComprehensiveReportResult {
        private final boolean success;
        private final String message;
        private final List<GeneratedReport> reports;
        private final AdvancedReportData reportData;
        private final QualityMetrics qualityMetrics;
        private final Duration generationTime;
        private final String executionId;
        private final int reportCount;
        private final Instant timestamp;

        private ComprehensiveReportResult(Builder builder) {
            this.success = builder.success;
            this.message = builder.message;
            this.reports = builder.reports != null ? new ArrayList<>(builder.reports) : new ArrayList<>();
            this.reportData = builder.reportData;
            this.qualityMetrics = builder.qualityMetrics;
            this.generationTime = builder.generationTime;
            this.executionId = builder.executionId;
            this.reportCount = builder.reportCount;
            this.timestamp = Instant.now();
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private boolean success;
            private String message;
            private List<GeneratedReport> reports;
            private AdvancedReportData reportData;
            private QualityMetrics qualityMetrics;
            private Duration generationTime;
            private String executionId;
            private int reportCount;

            public Builder withSuccess(boolean success) {
                this.success = success;
                return this;
            }

            public Builder withMessage(String message) {
                this.message = message;
                return this;
            }

            public Builder withReports(List<GeneratedReport> reports) {
                this.reports = reports;
                return this;
            }

            public Builder withReportData(AdvancedReportData reportData) {
                this.reportData = reportData;
                return this;
            }

            public Builder withQualityMetrics(QualityMetrics qualityMetrics) {
                this.qualityMetrics = qualityMetrics;
                return this;
            }

            public Builder withGenerationTime(Duration generationTime) {
                this.generationTime = generationTime;
                return this;
            }

            public Builder withExecutionId(String executionId) {
                this.executionId = executionId;
                return this;
            }

            public Builder withReportCount(int reportCount) {
                this.reportCount = reportCount;
                return this;
            }

            public ComprehensiveReportResult build() {
                return new ComprehensiveReportResult(this);
            }
        }

        // Standard getters
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public List<GeneratedReport> getReports() { return new ArrayList<>(reports); }
        public AdvancedReportData getReportData() { return reportData; }
        public QualityMetrics getQualityMetrics() { return qualityMetrics; }
        public Duration getGenerationTime() { return generationTime; }
        public String getExecutionId() { return executionId; }
        public int getReportCount() { return reportCount; }
        public Instant getTimestamp() { return timestamp; }

        public int getGeneratedReportCount() { return reports.size(); }

        public long getTotalFileSize() {
            return reports.stream().mapToLong(GeneratedReport::getFileSize).sum();
        }

        @Override
        public String toString() {
            return "ComprehensiveReportResult{" +
                    "success=" + success +
                    ", message='" + message + '\'' +
                    ", reportCount=" + reports.size() +
                    ", totalFileSize=" + formatFileSize(getTotalFileSize()) +
                    ", generationTime=" + (generationTime != null ? generationTime.toMillis() + "ms" : "N/A") +
                    ", executionId='" + executionId + '\'' +
                    '}';
        }

        private String formatFileSize(long bytes) {
            if (bytes < 1024) return bytes + " B";
            if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
            return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        }
    }

    public static class AdvancedReportMetric {
        private final String name;
        private final Object value;
        private final MetricType type;
        private final LocalDateTime timestamp;
        private final Map<String, Object> metadata;

        public AdvancedReportMetric(String name, Object value, MetricType type) {
            this.name = name;
            this.value = value;
            this.type = type;
            this.timestamp = LocalDateTime.now();
            this.metadata = new HashMap<>();
        }

        public String getName() { return name; }
        public Object getValue() { return value; }
        public MetricType getType() { return type; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public Map<String, Object> getMetadata() { return new HashMap<>(metadata); }

        public String getFormattedValue() {
            if (value == null) return "N/A";

            switch (type) {
                case PERCENTAGE:
                    return String.format("%.1f%%", ((Number) value).doubleValue());
                case DURATION:
                    return value + "ms";
                case SIZE:
                    return formatFileSize(((Number) value).longValue());
                case SCORE:
                    return String.format("%.3f", ((Number) value).doubleValue());
                case RATIO:
                    return String.format("%.2f", ((Number) value).doubleValue());
                default:
                    return value.toString();
            }
        }

        private String formatFileSize(long bytes) {
            if (bytes < 1024) return bytes + " B";
            if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
            return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        }

        @Override
        public String toString() {
            return "AdvancedReportMetric{" +
                    "name='" + name + '\'' +
                    ", value=" + getFormattedValue() +
                    ", type=" + type +
                    ", timestamp=" + timestamp +
                    '}';
        }
    }

    public static class ChartDataSet {
        private final String id;
        private final String title;
        private List<String> labels = new ArrayList<>();
        private List<Object> data = new ArrayList<>();
        private String chartType = "pie";
        private Map<String, Object> options = new HashMap<>();

        public ChartDataSet(String id, String title) {
            this.id = id;
            this.title = title;
        }

        public String getId() { return id; }
        public String getTitle() { return title; }
        public List<String> getLabels() { return new ArrayList<>(labels); }
        public void setLabels(List<String> labels) { this.labels = new ArrayList<>(labels); }
        public List<Object> getData() { return new ArrayList<>(data); }
        public void setData(List<Object> data) { this.data = new ArrayList<>(data); }
        public String getChartType() { return chartType; }
        public void setChartType(String chartType) { this.chartType = chartType; }
        public Map<String, Object> getOptions() { return new HashMap<>(options); }
        public void setOptions(Map<String, Object> options) { this.options = new HashMap<>(options); }

        public ChartDataSet withChartType(String chartType) {
            this.chartType = chartType;
            return this;
        }

        public ChartDataSet withOption(String key, Object value) {
            this.options.put(key, value);
            return this;
        }

        @Override
        public String toString() {
            return "ChartDataSet{" +
                    "id='" + id + '\'' +
                    ", title='" + title + '\'' +
                    ", chartType='" + chartType + '\'' +
                    ", dataPoints=" + data.size() +
                    '}';
        }
    }

    private static class ReportCache {
        private final ComprehensiveReportResult result;
        private final Instant timestamp;

        public ReportCache(ComprehensiveReportResult result, Instant timestamp) {
            this.result = result;
            this.timestamp = timestamp;
        }

        public boolean isExpired(Duration maxAge) {
            return Instant.now().isAfter(timestamp.plus(maxAge));
        }

        public ComprehensiveReportResult getResult() { return result; }
        public Instant getTimestamp() { return timestamp; }
    }
}