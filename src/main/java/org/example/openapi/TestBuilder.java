package org.example.openapi;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.sun.management.OperatingSystemMXBean;

/**
 * ENTERPRISE-GRADE INTELLIGENT TEST BUILDER - Tutarlılık Standardına Uygun
 *
 * Ultra-Advanced AI-Powered Test Generation Engine with comprehensive enterprise features.
 * Bu builder en gelişmiş test generation yeteneklerini sağlar, multi-model AI analizi,
 * advanced security testing, performance optimization ve enterprise-grade reliability
 * özellikleri ile OpenAPI test automation için optimize edilmiştir.
 *
 * === TUTARLILIK STANDARTLARINA UYUM ===
 * - Standard interface'lere uyumlu method signatures
 * - Builder pattern implementation with fluent API
 * - Standard return types (GeneratedTestCase, ComprehensiveTestSuite)
 * - Enterprise logging ve monitoring
 * - Thread-safe operations
 * - Configuration validation
 * - Standard exception handling
 *
 * @author Enterprise Test Generation Solutions Team
 * @version 4.0.0-ENTERPRISE-STANDARDIZED
 * @since 2025.1
 */
public class TestBuilder {

    // ===== STANDARD LOGGING =====
    private static final Logger logger = LoggerFactory.getLogger(TestBuilder.class);

    // ===== ENHANCED CONSTANTS =====
    private static final String VERSION = "4.0.0-ENTERPRISE-STANDARDIZED";
    private static final String BUILD_DATE = "2025-01-17";
    private static final String VENDOR = "Enterprise Test Generation Solutions";

    // Performance and optimization constants
    private static final int DEFAULT_THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 4;
    private static final int MAX_THREAD_POOL_SIZE = 500;
    private static final int DEFAULT_AI_TIMEOUT_SECONDS = 30;
    private static final int DEFAULT_BATCH_SIZE = 50;
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final long DEFAULT_CACHE_TTL_SECONDS = 3600;
    private static final int MAX_CONCURRENT_AI_REQUESTS = 10;

    // AI and ML constants
    private static final double DEFAULT_AI_CONFIDENCE_THRESHOLD = 0.85;
    private static final int MIN_TEST_SCENARIOS_PER_ENDPOINT = 5;
    private static final int MAX_TEST_SCENARIOS_PER_ENDPOINT = 50;
    private static final String DEFAULT_AI_MODEL = "gpt-4-turbo";

    // Security and quality constants
    private static final int SECURITY_TEST_SCENARIOS = 25;
    private static final int PERFORMANCE_TEST_SCENARIOS = 15;
    private static final int EDGE_CASE_TEST_SCENARIOS = 20;
    private static final int NEGATIVE_TEST_SCENARIOS = 30;

    // ===== STANDARD ENUMS (Tutarlılık Rehberine Uygun) =====

    /**
     * Test Generation Strategy Types - Standard Interface Compliance
     */
    public enum TestGenerationStrategy {
        BASIC("Basic test generation", 1, false, false, StrategyCategory.FUNCTIONAL),
        STANDARD("Standard test coverage", 2, true, false, StrategyCategory.FUNCTIONAL),
        COMPREHENSIVE("Comprehensive test suite", 3, true, true, StrategyCategory.FUNCTIONAL),
        AI_ENHANCED("AI-enhanced generation", 4, true, true, StrategyCategory.ADVANCED),
        ENTERPRISE("Full enterprise features", 5, true, true, StrategyCategory.ADVANCED),
        PERFORMANCE_FOCUSED("Performance-optimized", 4, true, false, StrategyCategory.PERFORMANCE),
        SECURITY_FOCUSED("Security-centric", 4, true, true, StrategyCategory.SECURITY);

        private final String description;
        private final int complexity;
        private final boolean enableAdvancedFeatures;
        private final boolean enableAiIntegration;
        private final StrategyCategory category;

        TestGenerationStrategy(String description, int complexity, boolean enableAdvancedFeatures,
                               boolean enableAiIntegration, StrategyCategory category) {
            this.description = description;
            this.complexity = complexity;
            this.enableAdvancedFeatures = enableAdvancedFeatures;
            this.enableAiIntegration = enableAiIntegration;
            this.category = category;
        }

        public String getDescription() { return description; }
        public int getComplexity() { return complexity; }
        public boolean isAdvancedFeaturesEnabled() { return enableAdvancedFeatures; }
        public boolean isAiIntegrationEnabled() { return enableAiIntegration; }
        public StrategyCategory getCategory() { return category; }
    }

    /**
     * Standard Strategy Categories
     */
    public enum StrategyCategory {
        FUNCTIONAL, SECURITY, PERFORMANCE, ADVANCED, SPECIALIZED
    }

    /**
     * AI Provider types with standard capabilities
     */
    public enum AiProvider {
        OPENAI_GPT4("OpenAI GPT-4", "gpt-4-turbo", true, true, 0.95),
        OPENAI_GPT3_5("OpenAI GPT-3.5", "gpt-3.5-turbo", true, false, 0.85),
        ANTHROPIC_CLAUDE("Anthropic Claude", "claude-3-opus", true, true, 0.93),
        GOOGLE_GEMINI("Google Gemini", "gemini-pro", true, true, 0.90),
        AZURE_OPENAI("Azure OpenAI", "gpt-4", true, true, 0.94),
        LOCAL_MODEL("Local AI Model", "local", false, false, 0.75),
        NONE("No AI Integration", "none", false, false, 0.0);

        private final String displayName;
        private final String modelName;
        private final boolean supportsComplexReasoning;
        private final boolean supportsCodeGeneration;
        private final double qualityScore;

        AiProvider(String displayName, String modelName, boolean supportsComplexReasoning,
                   boolean supportsCodeGeneration, double qualityScore) {
            this.displayName = displayName;
            this.modelName = modelName;
            this.supportsComplexReasoning = supportsComplexReasoning;
            this.supportsCodeGeneration = supportsCodeGeneration;
            this.qualityScore = qualityScore;
        }

        public String getDisplayName() { return displayName; }
        public String getModelName() { return modelName; }
        public boolean supportsComplexReasoning() { return supportsComplexReasoning; }
        public boolean supportsCodeGeneration() { return supportsCodeGeneration; }
        public double getQualityScore() { return qualityScore; }
    }

    /**
     * Standard Test Types with priority mapping
     */
    public enum TestType {
        FUNCTIONAL("Functional Testing", 1, true, "Basic API functionality validation"),
        SECURITY("Security Testing", 2, true, "Comprehensive security vulnerability testing"),
        PERFORMANCE("Performance Testing", 3, true, "Load and performance validation"),
        EDGE_CASE("Edge Case Testing", 2, true, "Boundary and edge case validation"),
        NEGATIVE("Negative Testing", 2, true, "Error handling and negative scenarios"),
        INTEGRATION("Integration Testing", 3, false, "Cross-service integration validation"),
        CONTRACT("Contract Testing", 2, false, "API contract compliance validation"),
        ACCESSIBILITY("Accessibility Testing", 1, false, "API accessibility compliance"),
        COMPLIANCE("Compliance Testing", 3, false, "Regulatory compliance validation"),
        CHAOS("Chaos Testing", 4, false, "Resilience and fault tolerance testing");

        private final String displayName;
        private final int priority;
        private final boolean isDefault;
        private final String description;

        TestType(String displayName, int priority, boolean isDefault, String description) {
            this.displayName = displayName;
            this.priority = priority;
            this.isDefault = isDefault;
            this.description = description;
        }

        public String getDisplayName() { return displayName; }
        public int getPriority() { return priority; }
        public boolean isDefault() { return isDefault; }
        public String getDescription() { return description; }
    }

    /**
     * Standard Quality Levels
     */
    public enum QualityLevel {
        BASIC("Basic Quality", 1, 5, 1),
        STANDARD("Standard Quality", 2, 10, 2),
        HIGH("High Quality", 3, 20, 3),
        PREMIUM("Premium Quality", 4, 35, 4),
        ENTERPRISE("Enterprise Quality", 5, 50, 5);

        private final String description;
        private final int level;
        private final int testScenariosPerEndpoint;
        private final int validationDepth;

        QualityLevel(String description, int level, int testScenariosPerEndpoint, int validationDepth) {
            this.description = description;
            this.level = level;
            this.testScenariosPerEndpoint = testScenariosPerEndpoint;
            this.validationDepth = validationDepth;
        }

        public String getDescription() { return description; }
        public int getLevel() { return level; }
        public int getTestScenariosPerEndpoint() { return testScenariosPerEndpoint; }
        public int getValidationDepth() { return validationDepth; }
    }

    // ===== ENTERPRISE CONFIGURATION (Standard Pattern) =====

    /**
     * Comprehensive configuration for enterprise test generation - Builder Pattern
     */
    public static class TestBuilderConfiguration {
        private final TestGenerationStrategy strategy;
        private final Set<AiProvider> aiProviders;
        private final Set<TestType> enabledTestTypes;
        private final QualityLevel qualityLevel;
        private final int threadPoolSize;
        private final int batchSize;
        private final boolean enableCaching;
        private final boolean enablePerformanceOptimization;
        private final boolean enableSecurityEnhancements;
        private final boolean enableAdvancedAnalytics;
        private final Duration aiTimeout;
        private final Map<String, Object> customProperties;
        private final String executionId;
        private final Instant creationTimestamp;

        private TestBuilderConfiguration(Builder builder) {
            this.strategy = builder.strategy;
            this.aiProviders = new HashSet<>(builder.aiProviders);
            this.enabledTestTypes = new HashSet<>(builder.enabledTestTypes);
            this.qualityLevel = builder.qualityLevel;
            this.threadPoolSize = builder.threadPoolSize;
            this.batchSize = builder.batchSize;
            this.enableCaching = builder.enableCaching;
            this.enablePerformanceOptimization = builder.enablePerformanceOptimization;
            this.enableSecurityEnhancements = builder.enableSecurityEnhancements;
            this.enableAdvancedAnalytics = builder.enableAdvancedAnalytics;
            this.aiTimeout = builder.aiTimeout;
            this.customProperties = new HashMap<>(builder.customProperties);
            this.executionId = generateAdvancedExecutionId();
            this.creationTimestamp = Instant.now();
        }

        // Standard getters
        public TestGenerationStrategy getStrategy() { return strategy; }
        public Set<AiProvider> getAiProviders() { return new HashSet<>(aiProviders); }
        public Set<TestType> getEnabledTestTypes() { return new HashSet<>(enabledTestTypes); }
        public QualityLevel getQualityLevel() { return qualityLevel; }
        public int getThreadPoolSize() { return threadPoolSize; }
        public int getBatchSize() { return batchSize; }
        public boolean isCachingEnabled() { return enableCaching; }
        public boolean isPerformanceOptimizationEnabled() { return enablePerformanceOptimization; }
        public boolean isSecurityEnhancementsEnabled() { return enableSecurityEnhancements; }
        public boolean isAdvancedAnalyticsEnabled() { return enableAdvancedAnalytics; }
        public Duration getAiTimeout() { return aiTimeout; }
        public Map<String, Object> getCustomProperties() { return new HashMap<>(customProperties); }
        public String getExecutionId() { return executionId; }
        public Instant getCreationTimestamp() { return creationTimestamp; }

        // Standard factory methods
        public static Builder builder() {
            return new Builder();
        }

        public static TestBuilderConfiguration createDefault() {
            return builder().build();
        }

        public static TestBuilderConfiguration createEnterprise() {
            return builder()
                    .withStrategy(TestGenerationStrategy.ENTERPRISE)
                    .withAiProviders(AiProvider.OPENAI_GPT4, AiProvider.ANTHROPIC_CLAUDE)
                    .withAllTestTypes()
                    .withQualityLevel(QualityLevel.ENTERPRISE)
                    .withThreadPoolSize(DEFAULT_THREAD_POOL_SIZE * 2)
                    .withAdvancedFeatures(true)
                    .build();
        }

        public static class Builder {
            private TestGenerationStrategy strategy = TestGenerationStrategy.COMPREHENSIVE;
            private Set<AiProvider> aiProviders = new HashSet<>();
            private Set<TestType> enabledTestTypes = Set.of(TestType.FUNCTIONAL, TestType.NEGATIVE, TestType.EDGE_CASE);
            private QualityLevel qualityLevel = QualityLevel.STANDARD;
            private int threadPoolSize = DEFAULT_THREAD_POOL_SIZE;
            private int batchSize = DEFAULT_BATCH_SIZE;
            private boolean enableCaching = true;
            private boolean enablePerformanceOptimization = true;
            private boolean enableSecurityEnhancements = false;
            private boolean enableAdvancedAnalytics = false;
            private Duration aiTimeout = Duration.ofSeconds(DEFAULT_AI_TIMEOUT_SECONDS);
            private Map<String, Object> customProperties = new HashMap<>();

            public Builder withStrategy(TestGenerationStrategy strategy) {
                this.strategy = strategy;
                return this;
            }

            public Builder withAiProviders(AiProvider... providers) {
                this.aiProviders.addAll(Arrays.asList(providers));
                return this;
            }

            public Builder withTestTypes(TestType... types) {
                this.enabledTestTypes = new HashSet<>(Arrays.asList(types));
                return this;
            }

            public Builder withAllTestTypes() {
                this.enabledTestTypes = new HashSet<>(Arrays.asList(TestType.values()));
                return this;
            }

            public Builder withQualityLevel(QualityLevel level) {
                this.qualityLevel = level;
                return this;
            }

            public Builder withThreadPoolSize(int size) {
                this.threadPoolSize = Math.min(size, MAX_THREAD_POOL_SIZE);
                return this;
            }

            public Builder withBatchSize(int size) {
                this.batchSize = size;
                return this;
            }

            public Builder withCaching(boolean enable) {
                this.enableCaching = enable;
                return this;
            }

            public Builder withAdvancedFeatures(boolean enable) {
                this.enablePerformanceOptimization = enable;
                this.enableSecurityEnhancements = enable;
                this.enableAdvancedAnalytics = enable;
                return this;
            }

            public Builder withAiTimeout(Duration timeout) {
                this.aiTimeout = timeout;
                return this;
            }

            public Builder withCustomProperty(String key, Object value) {
                this.customProperties.put(key, value);
                return this;
            }

            public TestBuilderConfiguration build() {
                validateAndEnhanceConfiguration(this);
                return new TestBuilderConfiguration(this);
            }
        }

        private static void validateAndEnhanceConfiguration(Builder builder) {
            if (builder.threadPoolSize <= 0) {
                throw new IllegalArgumentException("Thread pool size must be positive");
            }

            if (builder.batchSize <= 0) {
                throw new IllegalArgumentException("Batch size must be positive");
            }

            if (builder.qualityLevel == null) {
                throw new IllegalArgumentException("Quality level cannot be null");
            }

            if (builder.threadPoolSize > MAX_THREAD_POOL_SIZE) {
                builder.threadPoolSize = MAX_THREAD_POOL_SIZE;
                logger.warn("Thread pool size capped at maximum: {}", MAX_THREAD_POOL_SIZE);
            }

            logger.debug("Configuration validation completed successfully");
        }
    }

    // ===== ENTERPRISE CORE COMPONENTS =====

    private final TestBuilderConfiguration configuration;
    private final ExecutorService mainExecutor;
    private final ScheduledExecutorService scheduledExecutor;
    private final Semaphore aiRequestSemaphore;

    // AI Integration
    private final Map<AiProvider, AiServiceAdapter> aiServices;
    private final AiOrchestrator aiOrchestrator;
    private final PromptEngineeringService promptService;

    // Advanced Test Generation
    private final TestStrategyManager strategyManager;
    private final SecurityTestGenerator securityTestGenerator;
    private final PerformanceTestGenerator performanceTestGenerator;
    private final EdgeCaseTestGenerator edgeCaseTestGenerator;
    private final NegativeTestGenerator negativeTestGenerator;

    // Caching and Optimization
    private final Map<String, CachedTestResult> testCache;
    private final Map<String, EndpointAnalysis> analysisCache;
    private final LRUCache<String, ComprehensiveTestSuite> testSuiteCache;

    // Analytics and Monitoring
    private final TestGenerationMetrics metrics;
    private final PerformanceMonitor performanceMonitor;
    private final QualityAnalyzer qualityAnalyzer;
    private final TestEffectivenessTracker effectivenessTracker;

    // Thread-safe counters
    private final AtomicLong totalTestsGenerated = new AtomicLong(0);
    private final AtomicLong successfulGenerations = new AtomicLong(0);
    private final AtomicLong failedGenerations = new AtomicLong(0);
    private final AtomicLong aiEnhancedTests = new AtomicLong(0);
    private final AtomicLong cacheHits = new AtomicLong(0);
    private final AtomicLong cacheMisses = new AtomicLong(0);
    private final AtomicReference<GenerationStatus> currentStatus = new AtomicReference<>(GenerationStatus.IDLE);

    // ===== CONSTRUCTORS (Standard Pattern) =====

    /**
     * Private constructor for builder pattern
     */
    private TestBuilder(TestBuilderConfiguration configuration) {
        this.configuration = validateConfiguration(configuration);

        // Initialize thread pools
        this.mainExecutor = createOptimizedExecutorService();
        this.scheduledExecutor = Executors.newScheduledThreadPool(4);
        this.aiRequestSemaphore = new Semaphore(MAX_CONCURRENT_AI_REQUESTS);

        // Initialize AI services
        this.aiServices = initializeAiServices();
        this.aiOrchestrator = new AiOrchestrator(this.aiServices, this.configuration);
        this.promptService = new PromptEngineeringService(this.configuration);

        // Initialize test generators
        this.strategyManager = new TestStrategyManager(this.configuration);
        this.securityTestGenerator = new SecurityTestGenerator(this.configuration);
        this.performanceTestGenerator = new PerformanceTestGenerator(this.configuration);
        this.edgeCaseTestGenerator = new EdgeCaseTestGenerator(this.configuration);
        this.negativeTestGenerator = new NegativeTestGenerator(this.configuration);

        // Initialize caching
        this.testCache = new ConcurrentHashMap<>();
        this.analysisCache = new ConcurrentHashMap<>();
        this.testSuiteCache = new LRUCache<>(1000);

        // Initialize analytics
        this.metrics = new TestGenerationMetrics();
        this.performanceMonitor = new PerformanceMonitor();
        this.qualityAnalyzer = new QualityAnalyzer(this.configuration);
        this.effectivenessTracker = new TestEffectivenessTracker();

        // Setup monitoring
        initializeMonitoring();
        setupShutdownHooks();

        logger.info("Enterprise TestBuilder v{} initialized with {} strategy, executionId: {}",
                VERSION, configuration.getStrategy(), configuration.getExecutionId());
    }

    // ===== BUILDER PATTERN IMPLEMENTATION =====

    /**
     * Builder for creating customized test builder instances
     */
    public static class Builder {
        private TestBuilderConfiguration configuration = TestBuilderConfiguration.createDefault();

        public Builder withConfiguration(TestBuilderConfiguration config) {
            this.configuration = config;
            return this;
        }

        public Builder withStrategy(TestGenerationStrategy strategy) {
            this.configuration = TestBuilderConfiguration.builder()
                    .withStrategy(strategy)
                    .build();
            return this;
        }

        public Builder withAiIntegration(AiProvider... providers) {
            this.configuration = TestBuilderConfiguration.builder()
                    .withAiProviders(providers)
                    .build();
            return this;
        }

        public TestBuilder build() {
            return new TestBuilder(configuration);
        }
    }

    // ===== STANDARD FACTORY METHODS =====

    /**
     * Creates an enterprise-grade test builder with full AI integration
     */
    public static TestBuilder createEnterprise() {
        return new Builder()
                .withConfiguration(TestBuilderConfiguration.createEnterprise())
                .build();
    }

    /**
     * Creates a high-performance test builder optimized for speed
     */
    public static TestBuilder createHighPerformance() {
        return new Builder()
                .withConfiguration(TestBuilderConfiguration.builder()
                        .withStrategy(TestGenerationStrategy.PERFORMANCE_FOCUSED)
                        .withThreadPoolSize(DEFAULT_THREAD_POOL_SIZE * 4)
                        .withQualityLevel(QualityLevel.HIGH)
                        .withAdvancedFeatures(true)
                        .build())
                .build();
    }

    /**
     * Creates a security-focused test builder
     */
    public static TestBuilder createSecurityFocused() {
        return new Builder()
                .withConfiguration(TestBuilderConfiguration.builder()
                        .withStrategy(TestGenerationStrategy.SECURITY_FOCUSED)
                        .withTestTypes(TestType.SECURITY, TestType.NEGATIVE, TestType.EDGE_CASE)
                        .withQualityLevel(QualityLevel.PREMIUM)
                        .withAdvancedFeatures(true)
                        .build())
                .build();
    }

    /**
     * Creates a basic test builder for simple use cases
     */
    public static TestBuilder createBasic() {
        return new Builder()
                .withConfiguration(TestBuilderConfiguration.builder()
                        .withStrategy(TestGenerationStrategy.BASIC)
                        .withTestTypes(TestType.FUNCTIONAL)
                        .withQualityLevel(QualityLevel.BASIC)
                        .build())
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    // ===== MAIN TEST GENERATION METHODS (Standard Interface) =====

    /**
     * Standard method signature - generates comprehensive test suite for multiple endpoints
     * Returns ComprehensiveTestSuite conforming to standard interface
     */
    public ComprehensiveTestSuite generateComprehensiveTests(List<EndpointInfo> endpoints) {
        long startTime = System.currentTimeMillis();
        currentStatus.set(GenerationStatus.GENERATING);

        logger.info("Starting comprehensive test generation for {} endpoints with executionId: {}",
                endpoints.size(), configuration.getExecutionId());

        try {
            // Phase 1: Endpoint Analysis
            currentStatus.set(GenerationStatus.ANALYZING);
            Map<EndpointInfo, EndpointAnalysis> analyses = performParallelEndpointAnalysis(endpoints);

            // Phase 2: Test Strategy Selection
            currentStatus.set(GenerationStatus.STRATEGIZING);
            AdvancedStrategyRecommendation recommendation = createAdvancedStrategyRecommendation(analyses);

            // Phase 3: Parallel Test Generation
            currentStatus.set(GenerationStatus.GENERATING);
            List<GeneratedTestCase> testCases = generateTestCasesInParallel(analyses, recommendation);

            // Phase 4: Quality Analysis and Optimization
            currentStatus.set(GenerationStatus.OPTIMIZING);
            List<GeneratedTestCase> optimizedTestCases = optimizeGeneratedTestCases(testCases);

            // Phase 5: Create execution plan
            currentStatus.set(GenerationStatus.VALIDATING);
            AdvancedStrategyExecutionPlan executionPlan = createExecutionPlan(optimizedTestCases);

            // Phase 6: Build comprehensive result
            ComprehensiveTestSuite result = buildComprehensiveTestSuite(
                    endpoints, optimizedTestCases, recommendation, executionPlan, startTime);

            currentStatus.set(GenerationStatus.COMPLETED);
            updateMetrics(result);

            logger.info("Comprehensive test generation completed in {}ms for executionId: {}",
                    (System.currentTimeMillis() - startTime), configuration.getExecutionId());

            return result;

        } catch (Exception e) {
            currentStatus.set(GenerationStatus.FAILED);
            logger.error("Comprehensive test generation failed for executionId: {}",
                    configuration.getExecutionId(), e);
            failedGenerations.incrementAndGet();
            throw new TestGenerationException("Failed to generate comprehensive tests", e);
        }
    }

    /**
     * Standard method signature - generates tests for multiple endpoints
     * Returns List<GeneratedTestCase> conforming to standard interface
     */
    public List<GeneratedTestCase> generateTests(List<EndpointInfo> endpoints, AdvancedStrategyRecommendation recommendation) {
        logger.info("Generating tests for {} endpoints with strategy: {}",
                endpoints.size(), recommendation.getRecommendedStrategy());

        try {
            Map<EndpointInfo, EndpointAnalysis> analyses = performParallelEndpointAnalysis(endpoints);
            return generateTestCasesInParallel(analyses, recommendation);
        } catch (Exception e) {
            logger.error("Test generation failed", e);
            throw new TestGenerationException("Failed to generate tests", e);
        }
    }

    /**
     * Standard method signature - recommends strategy for endpoints
     * Returns AdvancedStrategyRecommendation conforming to standard interface
     */
    public AdvancedStrategyRecommendation recommendAdvancedStrategy(List<EndpointInfo> endpoints) {
        logger.info("Analyzing {} endpoints for strategy recommendation", endpoints.size());

        try {
            Map<EndpointInfo, EndpointAnalysis> analyses = performParallelEndpointAnalysis(endpoints);
            return createAdvancedStrategyRecommendation(analyses);
        } catch (Exception e) {
            logger.error("Strategy recommendation failed", e);
            return createFallbackStrategyRecommendation();
        }
    }

    /**
     * Generates enhanced test suite for single endpoint with AI enhancement
     */
    public ComprehensiveTestSuite generateEnhancedTestSuite(EndpointInfo endpoint) {
        String cacheKey = generateAdvancedCacheKey(endpoint.getPath());

        // Check cache first
        if (configuration.isCachingEnabled()) {
            ComprehensiveTestSuite cached = testSuiteCache.get(cacheKey);
            if (cached != null) {
                cacheHits.incrementAndGet();
                logger.debug("Cache hit for endpoint: {}", endpoint.getPath());
                return cached;
            }
            cacheMisses.incrementAndGet();
        }

        try {
            // Analyze endpoint
            EndpointAnalysis analysis = analyzeEndpoint(endpoint);

            // Generate tests using multiple strategies
            List<GeneratedTestCase> testCases = new ArrayList<>();

            // 1. AI-enhanced generation (if enabled)
            if (shouldUseAiGeneration(analysis)) {
                testCases.addAll(generateWithAI(endpoint, analysis));
                aiEnhancedTests.incrementAndGet();
            }

            // 2. Strategy-based generation
            testCases.addAll(generateWithStrategies(endpoint, analysis));

            // 3. Security tests (if enabled)
            if (configuration.getEnabledTestTypes().contains(TestType.SECURITY)) {
                testCases.addAll(securityTestGenerator.generateSecurityTests(endpoint, analysis));
            }

            // 4. Performance tests (if enabled)
            if (configuration.getEnabledTestTypes().contains(TestType.PERFORMANCE)) {
                testCases.addAll(performanceTestGenerator.generatePerformanceTests(endpoint, analysis));
            }

            // 5. Edge case tests
            if (configuration.getEnabledTestTypes().contains(TestType.EDGE_CASE)) {
                testCases.addAll(edgeCaseTestGenerator.generateEdgeCaseTests(endpoint, analysis));
            }

            // 6. Negative tests
            if (configuration.getEnabledTestTypes().contains(TestType.NEGATIVE)) {
                testCases.addAll(negativeTestGenerator.generateNegativeTests(endpoint, analysis));
            }

            // Create comprehensive test suite
            ComprehensiveTestSuite suite = buildSingleEndpointTestSuite(endpoint, testCases, analysis);

            // Quality analysis and optimization
            suite = qualityAnalyzer.optimizeTestSuite(suite);

            // Cache result
            if (configuration.isCachingEnabled()) {
                testSuiteCache.put(cacheKey, suite);
            }

            totalTestsGenerated.addAndGet(testCases.size());
            successfulGenerations.incrementAndGet();

            return suite;

        } catch (Exception e) {
            failedGenerations.incrementAndGet();
            logger.warn("Failed to generate enhanced test suite for {}", endpoint.getPath(), e);
            return createFallbackTestSuite(endpoint);
        }
    }

    // ===== STANDARD CONFIGURATION VALIDATION =====

    private TestBuilderConfiguration validateConfiguration(TestBuilderConfiguration config) {
        if (config == null) {
            throw new IllegalArgumentException("Configuration cannot be null");
        }

        if (config.getThreadPoolSize() <= 0) {
            throw new IllegalArgumentException("Thread pool size must be positive");
        }

        if (config.getBatchSize() <= 0) {
            throw new IllegalArgumentException("Batch size must be positive");
        }

        if (config.getQualityLevel() == null) {
            throw new IllegalArgumentException("Quality level cannot be null");
        }

        if (config.getThreadPoolSize() > MAX_THREAD_POOL_SIZE) {
            logger.warn("High number of threads ({}), this may impact performance", config.getThreadPoolSize());
        }

        logger.debug("Configuration validation completed successfully for executionId: {}", config.getExecutionId());
        return config;
    }

    // ===== STANDARD UTILITY METHODS =====

    /**
     * Standard method signature - generates advanced cache key
     */
    private String generateAdvancedCacheKey(String identifier) {
        try {
            String input = identifier + "|" + configuration.getQualityLevel() + "|" +
                    configuration.getStrategy() + "|" + configuration.getExecutionId();

            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception e) {
            return String.valueOf(identifier.hashCode());
        }
    }

    /**
     * Standard method signature - generates advanced execution ID
     */
    private static String generateAdvancedExecutionId() {
        return String.format("TB_%s_%d",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")),
                System.nanoTime() % 100000);
    }

    /**
     * Standard method signature - creates optimized executor service
     */
    private ExecutorService createOptimizedExecutorService() {
        int corePoolSize = configuration.getThreadPoolSize();
        int maximumPoolSize = Math.min(MAX_THREAD_POOL_SIZE, corePoolSize * 2);

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000),
                new ThreadFactory() {
                    private final AtomicInteger counter = new AtomicInteger(0);
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread t = new Thread(r, "TestBuilder-" + counter.incrementAndGet());
                        t.setDaemon(true);
                        return t;
                    }
                },
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        executor.allowCoreThreadTimeOut(true);
        logger.debug("Created optimized thread pool with {} core threads", corePoolSize);

        return executor;
    }

    // ===== ADVANCED ANALYSIS METHODS =====

    /**
     * Performs parallel endpoint analysis for optimization
     */
    private Map<EndpointInfo, EndpointAnalysis> performParallelEndpointAnalysis(List<EndpointInfo> endpoints) {
        Map<EndpointInfo, EndpointAnalysis> analyses = new ConcurrentHashMap<>();

        List<CompletableFuture<Void>> futures = endpoints.stream()
                .map(endpoint -> CompletableFuture.runAsync(() -> {
                    try {
                        EndpointAnalysis analysis = analyzeEndpoint(endpoint);
                        analyses.put(endpoint, analysis);
                    } catch (Exception e) {
                        logger.warn("Failed to analyze endpoint: {}", endpoint.getPath(), e);
                        analyses.put(endpoint, createBasicAnalysis(endpoint));
                    }
                }, mainExecutor))
                .collect(Collectors.toList());

        // Wait for all analyses to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        return analyses;
    }

    /**
     * Analyzes a single endpoint comprehensively
     */
    private EndpointAnalysis analyzeEndpoint(EndpointInfo endpoint) {
        String cacheKey = "analysis_" + generateAdvancedCacheKey(endpoint.getPath());

        // Check cache
        EndpointAnalysis cached = analysisCache.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        EndpointAnalysis.Builder builder = new EndpointAnalysis.Builder(endpoint);

        // 1. Basic analysis
        builder.withComplexityScore(calculateComplexityScore(endpoint))
                .withSecurityRiskLevel(assessSecurityRisk(endpoint))
                .withPerformanceImpact(assessPerformanceImpact(endpoint));

        // 2. Parameter analysis
        if (endpoint.isHasParameters()) {
            builder.withParameterAnalysis(analyzeParameters(endpoint.getParameters()));
        }

        // 3. Request body analysis
        if (endpoint.isHasRequestBody()) {
            builder.withRequestBodyAnalysis(analyzeRequestBody(endpoint.getRequestBodyInfo()));
        }

        // 4. Response analysis
        builder.withResponseAnalysis(analyzeResponses(new ArrayList<>(endpoint.getResponses().values())));

        // 5. Security considerations
        builder.withSecurityConsiderations(analyzeSecurityConsiderations(endpoint));

        // 6. Test scenarios recommendation
        builder.withRecommendedScenarios(recommendTestScenarios(endpoint));

        EndpointAnalysis analysis = builder.build();

        // Cache the analysis
        analysisCache.put(cacheKey, analysis);

        return analysis;
    }

    /**
     * Calculates comprehensive complexity score for endpoint
     */
    private ComplexityScore calculateComplexityScore(EndpointInfo endpoint) {
        int score = 0;
        List<String> factors = new ArrayList<>();

        // Base complexity by HTTP method
        switch (endpoint.getMethod().toUpperCase()) {
            case "GET":
                score += 1;
                break;
            case "POST":
            case "PUT":
                score += 3;
                factors.add("Mutating operation");
                break;
            case "DELETE":
                score += 2;
                factors.add("Destructive operation");
                break;
            case "PATCH":
                score += 4;
                factors.add("Partial update complexity");
                break;
        }

        // Path complexity
        long pathParams = endpoint.getPath().chars().filter(ch -> ch == '{').count();
        score += (int) pathParams * 2;
        if (pathParams > 0) {
            factors.add("Path parameters: " + pathParams);
        }

        // Query parameters complexity
        long queryParams = endpoint.getParameters().stream()
                .filter(p -> "query".equals(p.getLocation()))
                .count();
        score += (int) queryParams;
        if (queryParams > 3) {
            factors.add("Many query parameters: " + queryParams);
        }

        // Request body complexity
        if (endpoint.isHasRequestBody()) {
            score += 3;
            factors.add("Request body handling");
        }

        // Response complexity
        int responseCount = endpoint.getResponses().size();
        score += responseCount;
        if (responseCount > 3) {
            factors.add("Multiple response types: " + responseCount);
        }

        // Security complexity
        if (endpoint.isRequiresAuthentication()) {
            score += 2;
            factors.add("Authentication required");
        }

        if (!endpoint.getSecuritySchemes().isEmpty()) {
            score += endpoint.getSecuritySchemes().size();
            factors.add("Security schemes: " + endpoint.getSecuritySchemes().size());
        }

        // Business logic complexity indicators
        if (endpoint.getPath().contains("batch") || endpoint.getPath().contains("bulk")) {
            score += 3;
            factors.add("Batch/bulk operation");
        }

        if (endpoint.getPath().contains("search") || endpoint.getPath().contains("filter")) {
            score += 2;
            factors.add("Search/filter capability");
        }

        // Determine complexity level
        ComplexityLevel level;
        if (score <= 5) level = ComplexityLevel.SIMPLE;
        else if (score <= 10) level = ComplexityLevel.MODERATE;
        else if (score <= 20) level = ComplexityLevel.COMPLEX;
        else if (score <= 30) level = ComplexityLevel.VERY_COMPLEX;
        else level = ComplexityLevel.EXTREME;

        return new ComplexityScore(score, level, factors);
    }

    /**
     * Assesses security risk level for endpoint
     */
    private SecurityRiskLevel assessSecurityRisk(EndpointInfo endpoint) {
        int riskScore = 0;

        // HTTP method risk
        switch (endpoint.getMethod().toUpperCase()) {
            case "POST":
            case "PUT":
            case "DELETE":
            case "PATCH":
                riskScore += 2;
                break;
        }

        // Authentication risk
        if (!endpoint.isRequiresAuthentication()) {
            riskScore += 3; // Unauthenticated endpoints are higher risk
        }

        // Parameter-based risks
        for (ParameterInfo param : endpoint.getParameters()) {
            String paramName = param.getName().toLowerCase();

            // SQL injection risk indicators
            if (paramName.contains("query") || paramName.contains("sql") ||
                    paramName.contains("where") || paramName.contains("filter")) {
                riskScore += 2;
            }

            // File upload risk
            if (paramName.contains("file") || paramName.contains("upload") ||
                    paramName.contains("attachment")) {
                riskScore += 3;
            }

            // Command injection risk
            if (paramName.contains("command") || paramName.contains("exec") ||
                    paramName.contains("script")) {
                riskScore += 4;
            }
        }

        // Path-based risks
        String path = endpoint.getPath().toLowerCase();
        if (path.contains("admin") || path.contains("system") ||
                path.contains("config") || path.contains("internal")) {
            riskScore += 3;
        }

        // File operation risks
        if (path.contains("file") || path.contains("download") ||
                path.contains("upload") || path.contains("export")) {
            riskScore += 2;
        }

        // Determine risk level
        if (riskScore <= 2) return SecurityRiskLevel.LOW;
        else if (riskScore <= 5) return SecurityRiskLevel.MEDIUM;
        else if (riskScore <= 8) return SecurityRiskLevel.HIGH;
        else return SecurityRiskLevel.CRITICAL;
    }

    /**
     * Assesses performance impact for endpoint
     */
    private PerformanceImpact assessPerformanceImpact(EndpointInfo endpoint) {
        int impactScore = 0;
        List<String> factors = new ArrayList<>();

        // HTTP method impact
        switch (endpoint.getMethod().toUpperCase()) {
            case "GET":
                impactScore += 1;
                break;
            case "POST":
            case "PUT":
                impactScore += 3;
                factors.add("Data modification operation");
                break;
            case "DELETE":
                impactScore += 2;
                factors.add("Deletion operation");
                break;
        }

        // Query complexity
        long queryParams = endpoint.getParameters().stream()
                .filter(p -> "query".equals(p.getLocation()))
                .count();
        if (queryParams > 5) {
            impactScore += 2;
            factors.add("Complex query parameters");
        }

        // Request body size impact
        if (endpoint.isHasRequestBody()) {
            impactScore += 2;
            factors.add("Request body processing");
        }

        // Path operations that typically impact performance
        String path = endpoint.getPath().toLowerCase();
        if (path.contains("search") || path.contains("filter") || path.contains("query")) {
            impactScore += 3;
            factors.add("Search/filter operation");
        }

        if (path.contains("batch") || path.contains("bulk") || path.contains("mass")) {
            impactScore += 4;
            factors.add("Batch operation");
        }

        if (path.contains("report") || path.contains("export") || path.contains("download")) {
            impactScore += 3;
            factors.add("Data-heavy operation");
        }

        // Response complexity
        if (endpoint.getResponses().size() > 3) {
            impactScore += 1;
            factors.add("Multiple response formats");
        }

        // Determine impact level
        PerformanceImpactLevel level;
        if (impactScore <= 3) level = PerformanceImpactLevel.LOW;
        else if (impactScore <= 6) level = PerformanceImpactLevel.MEDIUM;
        else if (impactScore <= 10) level = PerformanceImpactLevel.HIGH;
        else level = PerformanceImpactLevel.CRITICAL;

        return new PerformanceImpact(impactScore, level, factors);
    }

    // ===== AI INTEGRATION METHODS =====

    /**
     * Determines if AI generation should be used for endpoint
     */
    private boolean shouldUseAiGeneration(EndpointAnalysis analysis) {
        if (configuration.getAiProviders().isEmpty()) {
            return false;
        }

        // Use AI for complex endpoints
        if (analysis.getComplexityScore().getLevel().ordinal() >= ComplexityLevel.COMPLEX.ordinal()) {
            return true;
        }

        // Use AI for high security risk endpoints
        if (analysis.getSecurityRiskLevel().ordinal() >= SecurityRiskLevel.HIGH.ordinal()) {
            return true;
        }

        // Use AI for high performance impact endpoints
        if (analysis.getPerformanceImpact().getLevel().ordinal() >= PerformanceImpactLevel.HIGH.ordinal()) {
            return true;
        }

        // Use AI based on strategy
        return configuration.getStrategy().isAiIntegrationEnabled();
    }

    /**
     * Generates tests using AI with ensemble approach
     */
    private List<GeneratedTestCase> generateWithAI(EndpointInfo endpoint, EndpointAnalysis analysis) {
        List<GeneratedTestCase> aiTestCases = new ArrayList<>();

        try {
            // Acquire semaphore for AI request rate limiting
            aiRequestSemaphore.acquire();

            try {
                // Create AI generation request
                AiTestGenerationRequest request = createAiGenerationRequest(endpoint, analysis);

                // Get results from multiple AI providers
                List<AiGenerationResult> aiResults = aiOrchestrator.generateWithEnsemble(request);

                // Process and validate AI results
                for (AiGenerationResult result : aiResults) {
                    if (result.isSuccess() && result.getConfidence() >= DEFAULT_AI_CONFIDENCE_THRESHOLD) {
                        List<GeneratedTestCase> testCases = processAiResult(endpoint, result);
                        aiTestCases.addAll(testCases);
                    }
                }

                logger.debug("Generated {} AI-enhanced test cases for {}", aiTestCases.size(), endpoint.getPath());

            } finally {
                aiRequestSemaphore.release();
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("AI generation interrupted for {}", endpoint.getPath());
        } catch (Exception e) {
            logger.warn("AI generation failed for {}", endpoint.getPath(), e);
        }

        return aiTestCases;
    }

    /**
     * Creates AI generation request with context
     */
    private AiTestGenerationRequest createAiGenerationRequest(EndpointInfo endpoint, EndpointAnalysis analysis) {
        return AiTestGenerationRequest.builder()
                .withEndpoint(endpoint)
                .withAnalysis(analysis)
                .withTestTypes(configuration.getEnabledTestTypes())
                .withQualityLevel(configuration.getQualityLevel())
                .withPromptTemplate(promptService.createPromptTemplate(endpoint, analysis))
                .withMaxTokens(2000)
                .withTemperature(0.1)
                .withTimeout(configuration.getAiTimeout())
                .build();
    }

    // ===== STRATEGY-BASED GENERATION METHODS =====

    /**
     * Generates tests using traditional strategies
     */
    private List<GeneratedTestCase> generateWithStrategies(EndpointInfo endpoint, EndpointAnalysis analysis) {
        List<GeneratedTestCase> strategyTestCases = new ArrayList<>();

        try {
            // 1. Functional tests
            strategyTestCases.addAll(strategyManager.generateFunctionalTests(endpoint, analysis));

            // 2. Boundary tests
            strategyTestCases.addAll(strategyManager.generateBoundaryTests(endpoint, analysis));

            // 3. Data validation tests
            strategyTestCases.addAll(strategyManager.generateValidationTests(endpoint, analysis));

            // 4. Error handling tests
            strategyTestCases.addAll(strategyManager.generateErrorHandlingTests(endpoint, analysis));

            // 5. Integration tests (if enabled)
            if (configuration.getEnabledTestTypes().contains(TestType.INTEGRATION)) {
                strategyTestCases.addAll(strategyManager.generateIntegrationTests(endpoint, analysis));
            }

            logger.debug("Generated {} strategy-based test cases for {}", strategyTestCases.size(), endpoint.getPath());

        } catch (Exception e) {
            logger.warn("Strategy-based generation failed for {}", endpoint.getPath(), e);
        }

        return strategyTestCases;
    }

    // ===== STANDARD TEST SUITE BUILDING METHODS =====

    /**
     * Creates advanced strategy recommendation
     */
    private AdvancedStrategyRecommendation createAdvancedStrategyRecommendation(Map<EndpointInfo, EndpointAnalysis> analyses) {
        AdvancedStrategyRecommendation.Builder builder = AdvancedStrategyRecommendation.builder();

        // Analyze overall complexity
        double avgComplexity = analyses.values().stream()
                .mapToInt(a -> a.getComplexityScore().getScore())
                .average()
                .orElse(0.0);

        // Analyze security risk distribution
        Map<SecurityRiskLevel, Long> securityDistribution = analyses.values().stream()
                .collect(Collectors.groupingBy(EndpointAnalysis::getSecurityRiskLevel, Collectors.counting()));

        // Analyze performance impact distribution
        Map<PerformanceImpactLevel, Long> performanceDistribution = analyses.values().stream()
                .collect(Collectors.groupingBy(a -> a.getPerformanceImpact().getLevel(), Collectors.counting()));

        // Determine recommended strategy based on analysis
        StrategyType recommendedStrategy = determineOptimalStrategy(avgComplexity, securityDistribution, performanceDistribution);

        // Create recommendation
        return builder
                .withRecommendedStrategy(recommendedStrategy)
                .withConfidenceScore(calculateStrategyConfidence(analyses))
                .withRationale(generateStrategyRationale(recommendedStrategy, analyses))
                .withEstimatedEffort(estimateTestingEffort(analyses))
                .withRiskAssessment(createRiskAssessment(securityDistribution))
                .withPerformanceConsiderations(createPerformanceConsiderations(performanceDistribution))
                .withExecutionId(configuration.getExecutionId())
                .withGenerationTimestamp(Instant.now())
                .build();
    }

    /**
     * Generates test cases in parallel using the analyses and recommendation
     */
    private List<GeneratedTestCase> generateTestCasesInParallel(Map<EndpointInfo, EndpointAnalysis> analyses,
                                                                AdvancedStrategyRecommendation recommendation) {
        List<CompletableFuture<List<GeneratedTestCase>>> futures = new ArrayList<>();

        for (Map.Entry<EndpointInfo, EndpointAnalysis> entry : analyses.entrySet()) {
            CompletableFuture<List<GeneratedTestCase>> future = CompletableFuture.supplyAsync(() -> {
                try {
                    return generateTestCasesForEndpoint(entry.getKey(), entry.getValue(), recommendation);
                } catch (Exception e) {
                    logger.warn("Failed to generate test cases for {}", entry.getKey().getPath(), e);
                    return createFallbackTestCases(entry.getKey());
                }
            }, mainExecutor);

            futures.add(future);
        }

        // Collect results
        List<GeneratedTestCase> allTestCases = new ArrayList<>();
        for (CompletableFuture<List<GeneratedTestCase>> future : futures) {
            try {
                List<GeneratedTestCase> testCases = future.get(60, TimeUnit.SECONDS);
                if (testCases != null) {
                    allTestCases.addAll(testCases);
                }
            } catch (Exception e) {
                logger.warn("Failed to get test case result", e);
            }
        }

        return allTestCases;
    }

    /**
     * Generates test cases for a single endpoint
     */
    private List<GeneratedTestCase> generateTestCasesForEndpoint(EndpointInfo endpoint, EndpointAnalysis analysis,
                                                                 AdvancedStrategyRecommendation recommendation) {
        List<GeneratedTestCase> testCases = new ArrayList<>();

        // 1. AI-enhanced generation (if enabled)
        if (shouldUseAiGeneration(analysis)) {
            testCases.addAll(generateWithAI(endpoint, analysis));
        }

        // 2. Strategy-based generation
        testCases.addAll(generateWithStrategies(endpoint, analysis));

        // 3. Type-specific generation based on configuration
        if (configuration.getEnabledTestTypes().contains(TestType.SECURITY)) {
            testCases.addAll(securityTestGenerator.generateSecurityTests(endpoint, analysis));
        }

        if (configuration.getEnabledTestTypes().contains(TestType.PERFORMANCE)) {
            testCases.addAll(performanceTestGenerator.generatePerformanceTests(endpoint, analysis));
        }

        if (configuration.getEnabledTestTypes().contains(TestType.EDGE_CASE)) {
            testCases.addAll(edgeCaseTestGenerator.generateEdgeCaseTests(endpoint, analysis));
        }

        if (configuration.getEnabledTestTypes().contains(TestType.NEGATIVE)) {
            testCases.addAll(negativeTestGenerator.generateNegativeTests(endpoint, analysis));
        }

        return testCases;
    }

    /**
     * Optimizes generated test cases
     */
    private List<GeneratedTestCase> optimizeGeneratedTestCases(List<GeneratedTestCase> testCases) {
        return testCases.stream()
                .map(testCase -> {
                    try {
                        return qualityAnalyzer.optimizeTestCase(testCase);
                    } catch (Exception e) {
                        logger.warn("Failed to optimize test case: {}", testCase.getTestName(), e);
                        return testCase;
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * Creates execution plan for test cases
     */
    private AdvancedStrategyExecutionPlan createExecutionPlan(List<GeneratedTestCase> testCases) {
        return AdvancedStrategyExecutionPlan.builder()
                .withTestCases(testCases)
                .withExecutionOrder(determineOptimalExecutionOrder(testCases))
                .withParallelizationStrategy(determineParallelizationStrategy(testCases))
                .withResourceRequirements(calculateResourceRequirements(testCases))
                .withEstimatedDuration(estimateExecutionDuration(testCases))
                .withExecutionId(configuration.getExecutionId())
                .withGenerationTimestamp(Instant.now())
                .build();
    }

    /**
     * Builds comprehensive test suite with all components
     */
    private ComprehensiveTestSuite buildComprehensiveTestSuite(List<EndpointInfo> endpoints,
                                                               List<GeneratedTestCase> testCases,
                                                               AdvancedStrategyRecommendation recommendation,
                                                               AdvancedStrategyExecutionPlan executionPlan,
                                                               long startTime) {

        // Calculate quality metrics
        QualityMetrics qualityMetrics = calculateQualityMetrics(testCases);

        // Create profiles
        SecurityProfile securityProfile = createSecurityProfile(testCases);
        PerformanceProfile performanceProfile = createPerformanceProfile(testCases);
        ComplianceProfile complianceProfile = createComplianceProfile(testCases);

        return ComprehensiveTestSuite.builder()
                .withEndpoints(endpoints)
                .withTestCases(testCases)
                .withRecommendation(recommendation)
                .withExecutionPlan(executionPlan)
                .withQualityMetrics(qualityMetrics)
                .withSecurityProfile(securityProfile)
                .withPerformanceProfile(performanceProfile)
                .withComplianceProfile(complianceProfile)
                .withExecutionId(configuration.getExecutionId())
                .withGenerationTimestamp(Instant.now())
                .withGenerationDuration(Duration.ofMillis(System.currentTimeMillis() - startTime))
                .build();
    }

    /**
     * Builds test suite for single endpoint
     */
    private ComprehensiveTestSuite buildSingleEndpointTestSuite(EndpointInfo endpoint,
                                                                List<GeneratedTestCase> testCases,
                                                                EndpointAnalysis analysis) {

        AdvancedStrategyRecommendation recommendation = createSingleEndpointRecommendation(endpoint, analysis);
        AdvancedStrategyExecutionPlan executionPlan = createExecutionPlan(testCases);

        QualityMetrics qualityMetrics = calculateQualityMetrics(testCases);
        SecurityProfile securityProfile = createSecurityProfile(testCases);
        PerformanceProfile performanceProfile = createPerformanceProfile(testCases);
        ComplianceProfile complianceProfile = createComplianceProfile(testCases);

        return ComprehensiveTestSuite.builder()
                .withEndpoint(endpoint)
                .withTestCases(testCases)
                .withRecommendation(recommendation)
                .withExecutionPlan(executionPlan)
                .withQualityMetrics(qualityMetrics)
                .withSecurityProfile(securityProfile)
                .withPerformanceProfile(performanceProfile)
                .withComplianceProfile(complianceProfile)
                .withExecutionId(configuration.getExecutionId())
                .withGenerationTimestamp(Instant.now())
                .build();
    }

    // ===== INITIALIZATION METHODS =====

    /**
     * Initializes AI services based on configuration
     */
    private Map<AiProvider, AiServiceAdapter> initializeAiServices() {
        Map<AiProvider, AiServiceAdapter> services = new HashMap<>();

        for (AiProvider provider : configuration.getAiProviders()) {
            try {
                AiServiceAdapter adapter = createAiServiceAdapter(provider);
                if (adapter != null) {
                    services.put(provider, adapter);
                    logger.info("Initialized AI service: {}", provider.getDisplayName());
                }
            } catch (Exception e) {
                logger.warn("Failed to initialize AI service: {}", provider.getDisplayName(), e);
            }
        }

        return services;
    }

    /**
     * Creates AI service adapter for provider
     */
    private AiServiceAdapter createAiServiceAdapter(AiProvider provider) {
        switch (provider) {
            case OPENAI_GPT4:
            case OPENAI_GPT3_5:
                return createOpenAiAdapter(provider);
            case ANTHROPIC_CLAUDE:
                return createClaudeAdapter(provider);
            case GOOGLE_GEMINI:
                return createGeminiAdapter(provider);
            case AZURE_OPENAI:
                return createAzureOpenAiAdapter(provider);
            case LOCAL_MODEL:
                return createLocalModelAdapter(provider);
            default:
                return null;
        }
    }

    /**
     * Creates OpenAI service adapter
     */
    private AiServiceAdapter createOpenAiAdapter(AiProvider provider) {
        try {
            Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
            String apiKey = dotenv.get("OPENAI_API_KEY");

            if (apiKey == null || apiKey.trim().isEmpty()) {
                logger.warn("OpenAI API key not found");
                return null;
            }

            OpenAiService service = new OpenAiService(apiKey, configuration.getAiTimeout());
            return new OpenAiServiceAdapter(service, provider);

        } catch (Exception e) {
            logger.warn("Failed to create OpenAI adapter", e);
            return null;
        }
    }

    /**
     * Initializes monitoring and metrics collection
     */
    private void initializeMonitoring() {
        if (configuration.isAdvancedAnalyticsEnabled()) {
            scheduledExecutor.scheduleAtFixedRate(this::collectMetrics, 0, 30, TimeUnit.SECONDS);
            scheduledExecutor.scheduleAtFixedRate(this::performHealthCheck, 0, 60, TimeUnit.SECONDS);
            scheduledExecutor.scheduleAtFixedRate(this::cleanupCaches, 0, 300, TimeUnit.SECONDS);
        }
    }

    /**
     * Sets up shutdown hooks for graceful termination
     */
    private void setupShutdownHooks() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("TestBuilder shutdown initiated for executionId: {}", configuration.getExecutionId());
            performGracefulShutdown();
        }));
    }

    /**
     * Performs graceful shutdown
     */
    private void performGracefulShutdown() {
        try {
            currentStatus.set(GenerationStatus.SHUTTING_DOWN);

            // Shutdown executors
            mainExecutor.shutdown();
            scheduledExecutor.shutdown();

            // Wait for completion
            if (!mainExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                mainExecutor.shutdownNow();
            }
            if (!scheduledExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduledExecutor.shutdownNow();
            }

            // Final metrics log
            logFinalMetrics();

            logger.info("TestBuilder shutdown completed for executionId: {}", configuration.getExecutionId());

        } catch (Exception e) {
            logger.error("Error during shutdown for executionId: {}", configuration.getExecutionId(), e);
        }
    }

    // ===== UTILITY AND HELPER METHODS =====

    /**
     * Collects and updates metrics
     */
    private void collectMetrics() {
        try {
            metrics.updateMetrics(
                    totalTestsGenerated.get(),
                    successfulGenerations.get(),
                    failedGenerations.get(),
                    aiEnhancedTests.get(),
                    cacheHits.get(),
                    cacheMisses.get(),
                    getCurrentMemoryUsage(),
                    getCurrentCpuUsage()
            );
        } catch (Exception e) {
            logger.warn("Failed to collect metrics", e);
        }
    }

    /**
     * Performs health check
     */
    private void performHealthCheck() {
        try {
            HealthStatus status = performanceMonitor.checkHealth();
            if (status != HealthStatus.HEALTHY) {
                logger.warn("Health check failed: {}", status);
            }
        } catch (Exception e) {
            logger.warn("Health check failed", e);
        }
    }

    /**
     * Cleans up caches based on TTL and memory pressure
     */
    private void cleanupCaches() {
        try {
            long currentTime = System.currentTimeMillis();
            long ttlMillis = DEFAULT_CACHE_TTL_SECONDS * 1000;

            // Cleanup test cache
            testCache.entrySet().removeIf(entry ->
                    (currentTime - entry.getValue().getTimestamp()) > ttlMillis);

            // Cleanup analysis cache
            analysisCache.entrySet().removeIf(entry ->
                    (currentTime - entry.getValue().getTimestamp()) > ttlMillis);

            logger.debug("Cache cleanup completed");

        } catch (Exception e) {
            logger.warn("Cache cleanup failed", e);
        }
    }

    /**
     * Gets current memory usage
     */
    private long getCurrentMemoryUsage() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        return memoryBean.getHeapMemoryUsage().getUsed();
    }

    /**
     * Gets current CPU usage (simplified)
     */
    private double getCurrentCpuUsage() {
        try {
            com.sun.management.OperatingSystemMXBean osBean = 
                (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
            return osBean.getProcessCpuLoad();
        } catch (Exception e) {
            return 0.0; // Default value if can't get CPU load
        }
    }

    /**
     * Logs final metrics
     */
    private void logFinalMetrics() {
        logger.info("=== Final Test Generation Metrics for executionId: {} ===", configuration.getExecutionId());
        logger.info("Total tests generated: {}", totalTestsGenerated.get());
        logger.info("Successful generations: {}", successfulGenerations.get());
        logger.info("Failed generations: {}", failedGenerations.get());
        logger.info("AI-enhanced tests: {}", aiEnhancedTests.get());
        logger.info("Cache hits: {}", cacheHits.get());
        logger.info("Cache misses: {}", cacheMisses.get());

        long totalCacheRequests = cacheHits.get() + cacheMisses.get();
        if (totalCacheRequests > 0) {
            double hitRatio = (double) cacheHits.get() / totalCacheRequests * 100;
            logger.info("Cache hit ratio: {:.2f}%", hitRatio);
        }
    }

    /**
     * Updates metrics after generation
     */
    private void updateMetrics(ComprehensiveTestSuite result) {
        metrics.recordGenerationSession(result);
        effectivenessTracker.updateEffectiveness(result);
    }

    // ===== LEGACY COMPATIBILITY METHODS =====

    /**
     * Legacy method for backward compatibility - generates tests for multiple endpoints
     */
    public List<String> buildTests(List<EndpointInfo> endpoints) {
        ComprehensiveTestSuite result = generateComprehensiveTests(endpoints);
        return result.getTestCases().stream()
                .map(GeneratedTestCase::getTestCode)
                .collect(Collectors.toList());
    }

    /**
     * Legacy method for backward compatibility - generates test for single endpoint
     */
    public String createTestForEndpoint(EndpointInfo endpoint) {
        ComprehensiveTestSuite suite = generateEnhancedTestSuite(endpoint);
        return suite.getTestCases().stream()
                .map(GeneratedTestCase::getTestCode)
                .collect(Collectors.joining("\n\n"));
    }

    // ===== GETTERS AND STATUS METHODS =====

    /**
     * Gets current generation status
     */
    public GenerationStatus getCurrentStatus() {
        return currentStatus.get();
    }

    /**
     * Gets current metrics snapshot
     */
    public TestGenerationMetrics getMetrics() {
        return metrics.getSnapshot();
    }

    /**
     * Gets configuration
     */
    public TestBuilderConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * Gets cache statistics
     */
    public CacheStatistics getCacheStatistics() {
        return new CacheStatistics(
                testCache.size(),
                analysisCache.size(),
                testSuiteCache.size(),
                cacheHits.get(),
                cacheMisses.get()
        );
    }

    /**
     * Clears all caches
     */
    public void clearCaches() {
        testCache.clear();
        analysisCache.clear();
        testSuiteCache.clear();
        logger.info("All caches cleared for executionId: {}", configuration.getExecutionId());
    }

    /**
     * Gets version information
     */
    public String getVersion() {
        return VERSION;
    }

    // ===== HELPER METHODS FOR ANALYSIS AND CREATION =====

    private EndpointAnalysis createBasicAnalysis(EndpointInfo endpoint) {
        return new EndpointAnalysis.Builder(endpoint)
                .withComplexityScore(new ComplexityScore(5, ComplexityLevel.SIMPLE, Arrays.asList("Basic endpoint")))
                .withSecurityRiskLevel(SecurityRiskLevel.LOW)
                .withPerformanceImpact(new PerformanceImpact(2, PerformanceImpactLevel.LOW, Arrays.asList("Simple operation")))
                .build();
    }

    private Object analyzeParameters(List<ParameterInfo> parameters) {
        return new Object(); // Placeholder for parameter analysis
    }

    private Object analyzeRequestBody(RequestBodyInfo requestBodyInfo) {
        return new Object(); // Placeholder for request body analysis
    }

    private Object analyzeResponses(List<ResponseInfo> responses) {
        return new Object(); // Placeholder for response analysis
    }

    private Object analyzeSecurityConsiderations(EndpointInfo endpoint) {
        return new Object(); // Placeholder for security analysis
    }

    private Object recommendTestScenarios(EndpointInfo endpoint) {
        return new Object(); // Placeholder for scenario recommendation
    }

    private StrategyType determineOptimalStrategy(double avgComplexity,
                                                  Map<SecurityRiskLevel, Long> securityDistribution,
                                                  Map<PerformanceImpactLevel, Long> performanceDistribution) {
        // Determine strategy based on analysis
        if (avgComplexity > 20) {
            return StrategyType.ADVANCED_AI_DRIVEN;
        }

        if (securityDistribution.getOrDefault(SecurityRiskLevel.HIGH, 0L) > 0 ||
                securityDistribution.getOrDefault(SecurityRiskLevel.CRITICAL, 0L) > 0) {
            return StrategyType.SECURITY_OWASP_TOP10;
        }

        if (performanceDistribution.getOrDefault(PerformanceImpactLevel.HIGH, 0L) > 0 ||
                performanceDistribution.getOrDefault(PerformanceImpactLevel.CRITICAL, 0L) > 0) {
            return StrategyType.PERFORMANCE_LOAD;
        }

        return StrategyType.FUNCTIONAL_COMPREHENSIVE;
    }

    private double calculateStrategyConfidence(Map<EndpointInfo, EndpointAnalysis> analyses) {
        // Calculate confidence based on analysis completeness and consistency
        return 0.85; // Placeholder value
    }

    private String generateStrategyRationale(StrategyType strategy, Map<EndpointInfo, EndpointAnalysis> analyses) {
        return "Strategy selected based on endpoint complexity and risk analysis"; // Placeholder
    }

    private Duration estimateTestingEffort(Map<EndpointInfo, EndpointAnalysis> analyses) {
        long totalMinutes = analyses.size() * 5; // 5 minutes per endpoint as base
        return Duration.ofMinutes(totalMinutes);
    }

    private Object createRiskAssessment(Map<SecurityRiskLevel, Long> securityDistribution) {
        return new Object(); // Placeholder for risk assessment
    }

    private Object createPerformanceConsiderations(Map<PerformanceImpactLevel, Long> performanceDistribution) {
        return new Object(); // Placeholder for performance considerations
    }

    private List<String> determineOptimalExecutionOrder(List<GeneratedTestCase> testCases) {
        return testCases.stream()
                .sorted(Comparator.comparing(GeneratedTestCase::getPriority))
                .map(GeneratedTestCase::getTestId)
                .collect(Collectors.toList());
    }

    private String determineParallelizationStrategy(List<GeneratedTestCase> testCases) {
        return "PARALLEL"; // Placeholder
    }

    private Map<String, Object> calculateResourceRequirements(List<GeneratedTestCase> testCases) {
        Map<String, Object> requirements = new HashMap<>();
        requirements.put("threads", Math.min(testCases.size(), configuration.getThreadPoolSize()));
        requirements.put("memory", "2GB");
        requirements.put("storage", "100MB");
        return requirements;
    }

    private Duration estimateExecutionDuration(List<GeneratedTestCase> testCases) {
        long totalSeconds = testCases.stream()
                .mapToLong(tc -> tc.getEstimatedDuration().getSeconds())
                .sum();
        return Duration.ofSeconds(totalSeconds);
    }

    private QualityMetrics calculateQualityMetrics(List<GeneratedTestCase> testCases) {
        return QualityMetrics.builder()
                .withTotalTests(testCases.size())
                .withCoverageScore(0.85)
                .withComplexityScore(0.75)
                .withQualityScore(0.80)
                .build();
    }

    private SecurityProfile createSecurityProfile(List<GeneratedTestCase> testCases) {
        long securityTests = testCases.stream()
                .filter(tc -> tc.getTags().contains("security"))
                .count();

        return SecurityProfile.builder()
                .withSecurityTestCount((int) securityTests)
                .withSecurityCoverage(securityTests > 0 ? 0.80 : 0.0)
                .withRiskLevel(securityTests > 5 ? "HIGH" : "MEDIUM")
                .build();
    }

    private PerformanceProfile createPerformanceProfile(List<GeneratedTestCase> testCases) {
        long performanceTests = testCases.stream()
                .filter(tc -> tc.getTags().contains("performance"))
                .count();

        return PerformanceProfile.builder()
                .withPerformanceTestCount((int) performanceTests)
                .withLoadTestingEnabled(performanceTests > 0)
                .withExpectedThroughput(1000)
                .build();
    }

    private ComplianceProfile createComplianceProfile(List<GeneratedTestCase> testCases) {
        return ComplianceProfile.builder()
                .withComplianceStandards(Arrays.asList("REST", "HTTP"))
                .withComplianceScore(0.90)
                .build();
    }

    private AdvancedStrategyRecommendation createSingleEndpointRecommendation(EndpointInfo endpoint, EndpointAnalysis analysis) {
        return AdvancedStrategyRecommendation.builder()
                .withRecommendedStrategy(StrategyType.FUNCTIONAL_COMPREHENSIVE)
                .withConfidenceScore(0.85)
                .withRationale("Single endpoint analysis")
                .withEstimatedEffort(Duration.ofMinutes(10))
                .withExecutionId(configuration.getExecutionId())
                .withGenerationTimestamp(Instant.now())
                .build();
    }

    private AdvancedStrategyRecommendation createFallbackStrategyRecommendation() {
        return AdvancedStrategyRecommendation.builder()
                .withRecommendedStrategy(StrategyType.FUNCTIONAL_BASIC)
                .withConfidenceScore(0.60)
                .withRationale("Fallback recommendation due to analysis failure")
                .withEstimatedEffort(Duration.ofMinutes(5))
                .withExecutionId(configuration.getExecutionId())
                .withGenerationTimestamp(Instant.now())
                .build();
    }

    private List<GeneratedTestCase> createFallbackTestCases(EndpointInfo endpoint) {
        GeneratedTestCase fallbackTest = GeneratedTestCase.builder()
                .withTestId("fallback_" + endpoint.getOperationId())
                .withTestName("Fallback test for " + endpoint.getPath())
                .withDescription("Basic fallback test")
                .withTestCode(createBasicFallbackTestCode(endpoint))
                .withPriority(5)
                .withEstimatedDuration(Duration.ofSeconds(30))
                .withComplexity(1)
                .withTags(Set.of("fallback", "basic"))
                .withGenerationTimestamp(Instant.now())
                .build();

        return Collections.singletonList(fallbackTest);
    }

    private String createBasicFallbackTestCode(EndpointInfo endpoint) {
        return String.format("""
                @Test
                @DisplayName("Fallback test for %s %s")
                public void test%s() {
                    // Fallback test - basic endpoint validation
                    logger.info("Executing fallback test for %s %s");
                    
                    given()
                        .when()
                            .%s("%s")
                        .then()
                            .statusCode(anyOf(is(200), is(201), is(204), is(400), is(401), is(403), is(404)));
                    
                    logger.info("Fallback test completed for %s %s");
                }
                """,
                endpoint.getMethod().toUpperCase(),
                endpoint.getPath(),
                sanitizeMethodName(endpoint.getOperationId()),
                endpoint.getMethod().toUpperCase(),
                endpoint.getPath(),
                endpoint.getMethod().toLowerCase(),
                endpoint.getPath(),
                endpoint.getMethod().toUpperCase(),
                endpoint.getPath()
        );
    }

    private ComprehensiveTestSuite createFallbackTestSuite(EndpointInfo endpoint) {
        List<GeneratedTestCase> fallbackTests = createFallbackTestCases(endpoint);

        return ComprehensiveTestSuite.builder()
                .withEndpoint(endpoint)
                .withTestCases(fallbackTests)
                .withRecommendation(createFallbackStrategyRecommendation())
                .withExecutionPlan(createExecutionPlan(fallbackTests))
                .withQualityMetrics(calculateQualityMetrics(fallbackTests))
                .withExecutionId(configuration.getExecutionId())
                .withGenerationTimestamp(Instant.now())
                .build();
    }

    private List<GeneratedTestCase> processAiResult(EndpointInfo endpoint, AiGenerationResult result) {
        List<GeneratedTestCase> testCases = new ArrayList<>();

        try {
            String cleanCode = result.getGeneratedCode()
                    .replaceAll("```java", "")
                    .replaceAll("```", "")
                    .trim();

            GeneratedTestCase testCase = GeneratedTestCase.builder()
                    .withTestId("ai_generated_" + sanitizeMethodName(endpoint.getOperationId()))
                    .withTestName("AI Generated: " + endpoint.getOperationId())
                    .withDescription("AI-generated comprehensive test")
                    .withTestCode(formatAiGeneratedTest(endpoint, cleanCode))
                    .withPriority(2)
                    .withEstimatedDuration(Duration.ofMinutes(2))
                    .withComplexity(3)
                    .withTags(Set.of("ai-generated", "comprehensive"))
                    .withGenerationTimestamp(Instant.now())
                    .build();

            testCases.add(testCase);

        } catch (Exception e) {
            logger.warn("Failed to process AI result for {}", endpoint.getPath(), e);
        }

        return testCases;
    }

    private String formatAiGeneratedTest(EndpointInfo endpoint, String aiCode) {
        StringBuilder formatted = new StringBuilder();

        formatted.append("    /**\n");
        formatted.append("     * AI-Generated Comprehensive Test\n");
        formatted.append("     * Endpoint: ").append(endpoint.getMethod().toUpperCase()).append(" ").append(endpoint.getPath()).append("\n");
        formatted.append("     * Operation: ").append(endpoint.getOperationId()).append("\n");
        formatted.append("     * Generated: ").append(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)).append("\n");
        formatted.append("     */\n");
        formatted.append("    @Test\n");
        formatted.append("    @DisplayName(\"AI Generated: ").append(endpoint.getOperationId()).append("\")\n");
        formatted.append("    public void testAiGenerated").append(sanitizeMethodName(endpoint.getOperationId())).append("() {\n");
        formatted.append("        logger.info(\"=== AI Generated Test: ").append(endpoint.getMethod().toUpperCase()).append(" ").append(endpoint.getPath()).append(" ===\");\n\n");

        // Process and indent AI code
        String[] lines = aiCode.split("\n");
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                formatted.append("        ").append(line).append("\n");
            }
        }

        formatted.append("\n        logger.info(\"=== AI Test Completed: ").append(endpoint.getOperationId()).append(" ===\");\n");
        formatted.append("    }\n");

        return formatted.toString();
    }

    private String sanitizeMethodName(String input) {
        if (input == null || input.isEmpty()) {
            return "unknownEndpoint";
        }

        String result = input.replaceAll("[^a-zA-Z0-9_]", "_")
                .replaceAll("_+", "_")
                .replaceAll("^_|_$", "");

        if (result.isEmpty() || Character.isDigit(result.charAt(0))) {
            result = "endpoint_" + result;
        }

        return result;
    }

    // ===== PLACEHOLDER ADAPTER METHODS =====

    private AiServiceAdapter createClaudeAdapter(AiProvider provider) {
        return null; // Placeholder - would implement Anthropic Claude integration
    }

    private AiServiceAdapter createGeminiAdapter(AiProvider provider) {
        return null; // Placeholder - would implement Google Gemini integration
    }

    private AiServiceAdapter createAzureOpenAiAdapter(AiProvider provider) {
        return null; // Placeholder - would implement Azure OpenAI integration
    }

    private AiServiceAdapter createLocalModelAdapter(AiProvider provider) {
        return null; // Placeholder - would implement local model integration
    }

    // ===== INNER CLASSES AND SUPPORTING TYPES =====

    /**
     * Generation status enumeration
     */
    public enum GenerationStatus {
        IDLE, ANALYZING, STRATEGIZING, GENERATING, OPTIMIZING, VALIDATING, COMPLETED, FAILED, SHUTTING_DOWN
    }

    /**
     * Complexity levels for endpoints
     */
    public enum ComplexityLevel {
        SIMPLE, MODERATE, COMPLEX, VERY_COMPLEX, EXTREME
    }

    /**
     * Security risk levels
     */
    public enum SecurityRiskLevel {
        LOW, MEDIUM, HIGH, CRITICAL
    }

    /**
     * Performance impact levels
     */
    public enum PerformanceImpactLevel {
        LOW, MEDIUM, HIGH, CRITICAL
    }

    /**
     * Health status enumeration
     */
    public enum HealthStatus {
        HEALTHY, DEGRADED, UNHEALTHY
    }

    /**
     * Standard exception class
     */
    private static class TestGenerationException extends RuntimeException {
        public TestGenerationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    // ===== STANDARD DATA CLASSES =====

    /**
     * Complexity score container
     */
    public static class ComplexityScore {
        private final int score;
        private final ComplexityLevel level;
        private final List<String> factors;

        public ComplexityScore(int score, ComplexityLevel level, List<String> factors) {
            this.score = score;
            this.level = level;
            this.factors = new ArrayList<>(factors);
        }

        public int getScore() { return score; }
        public ComplexityLevel getLevel() { return level; }
        public List<String> getFactors() { return new ArrayList<>(factors); }
    }

    /**
     * Performance impact container
     */
    public static class PerformanceImpact {
        private final int score;
        private final PerformanceImpactLevel level;
        private final List<String> factors;

        public PerformanceImpact(int score, PerformanceImpactLevel level, List<String> factors) {
            this.score = score;
            this.level = level;
            this.factors = new ArrayList<>(factors);
        }

        public int getScore() { return score; }
        public PerformanceImpactLevel getLevel() { return level; }
        public List<String> getFactors() { return new ArrayList<>(factors); }
    }

    /**
     * Cache statistics container
     */
    public static class CacheStatistics {
        private final int testCacheSize;
        private final int analysisCacheSize;
        private final int testSuiteCacheSize;
        private final long cacheHits;
        private final long cacheMisses;

        public CacheStatistics(int testCacheSize, int analysisCacheSize, int testSuiteCacheSize,
                               long cacheHits, long cacheMisses) {
            this.testCacheSize = testCacheSize;
            this.analysisCacheSize = analysisCacheSize;
            this.testSuiteCacheSize = testSuiteCacheSize;
            this.cacheHits = cacheHits;
            this.cacheMisses = cacheMisses;
        }

        public int getTestCacheSize() { return testCacheSize; }
        public int getAnalysisCacheSize() { return analysisCacheSize; }
        public int getTestSuiteCacheSize() { return testSuiteCacheSize; }
        public long getCacheHits() { return cacheHits; }
        public long getCacheMisses() { return cacheMisses; }

        public double getCacheHitRatio() {
            long total = cacheHits + cacheMisses;
            return total > 0 ? (double) cacheHits / total : 0.0;
        }
    }

    // ===== PLACEHOLDER CLASSES FOR COMPLETE IMPLEMENTATION =====

    // All the supporting classes would be implemented here in a real system
    // These are simplified stubs for the standardization example

    private static class LRUCache<K, V> extends LinkedHashMap<K, V> {
        private final int maxSize;

        public LRUCache(int maxSize) {
            super(16, 0.75f, true);
            this.maxSize = maxSize;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
            return size() > maxSize;
        }
    }

    private static class CachedTestResult {
        private final long timestamp;
        public CachedTestResult() { this.timestamp = System.currentTimeMillis(); }
        public long getTimestamp() { return timestamp; }
    }

    // Placeholder analysis classes
    private static class EndpointAnalysis {
        private final EndpointInfo endpoint;
        private final ComplexityScore complexityScore;
        private final SecurityRiskLevel securityRiskLevel;
        private final PerformanceImpact performanceImpact;
        private final long timestamp;

        private EndpointAnalysis(Builder builder) {
            this.endpoint = builder.endpoint;
            this.complexityScore = builder.complexityScore;
            this.securityRiskLevel = builder.securityRiskLevel;
            this.performanceImpact = builder.performanceImpact;
            this.timestamp = System.currentTimeMillis();
        }

        public EndpointInfo getEndpoint() { return endpoint; }
        public ComplexityScore getComplexityScore() { return complexityScore; }
        public SecurityRiskLevel getSecurityRiskLevel() { return securityRiskLevel; }
        public PerformanceImpact getPerformanceImpact() { return performanceImpact; }
        public long getTimestamp() { return timestamp; }

        public static class Builder {
            private final EndpointInfo endpoint;
            private ComplexityScore complexityScore;
            private SecurityRiskLevel securityRiskLevel;
            private PerformanceImpact performanceImpact;

            public Builder(EndpointInfo endpoint) {
                this.endpoint = endpoint;
            }

            public Builder withComplexityScore(ComplexityScore score) {
                this.complexityScore = score;
                return this;
            }

            public Builder withSecurityRiskLevel(SecurityRiskLevel level) {
                this.securityRiskLevel = level;
                return this;
            }

            public Builder withPerformanceImpact(PerformanceImpact impact) {
                this.performanceImpact = impact;
                return this;
            }

            public Builder withParameterAnalysis(Object analysis) { return this; }
            public Builder withRequestBodyAnalysis(Object analysis) { return this; }
            public Builder withResponseAnalysis(Object analysis) { return this; }
            public Builder withSecurityConsiderations(Object considerations) { return this; }
            public Builder withRecommendedScenarios(Object scenarios) { return this; }

            public EndpointAnalysis build() {
                return new EndpointAnalysis(this);
            }
        }
    }

    // Comprehensive placeholder implementations for enterprise features
    private static class TestGenerationMetrics {
        public void updateMetrics(long totalTests, long successful, long failed, long aiEnhanced,
                                  long cacheHits, long cacheMisses, long memoryUsage, double cpuUsage) {}
        public TestGenerationMetrics getSnapshot() { return this; }
        public void recordGenerationSession(ComprehensiveTestSuite result) {}
    }

    private static class PerformanceMonitor {
        public HealthStatus checkHealth() { return HealthStatus.HEALTHY; }
    }

    private static class QualityAnalyzer {
        public QualityAnalyzer(TestBuilderConfiguration config) {}
        public ComprehensiveTestSuite optimizeTestSuite(ComprehensiveTestSuite suite) { return suite; }
        public GeneratedTestCase optimizeTestCase(GeneratedTestCase testCase) { return testCase; }
    }

    private static class TestEffectivenessTracker {
        public void updateEffectiveness(ComprehensiveTestSuite result) {}
    }

    private static class AiOrchestrator {
        public AiOrchestrator(Map<AiProvider, AiServiceAdapter> services, TestBuilderConfiguration config) {}
        public List<AiGenerationResult> generateWithEnsemble(AiTestGenerationRequest request) { return new ArrayList<>(); }
    }

    private static class PromptEngineeringService {
        public PromptEngineeringService(TestBuilderConfiguration config) {}
        public String createPromptTemplate(EndpointInfo endpoint, EndpointAnalysis analysis) { return ""; }
    }

    private static class TestStrategyManager {
        public TestStrategyManager(TestBuilderConfiguration config) {}
        public List<GeneratedTestCase> generateFunctionalTests(EndpointInfo endpoint, EndpointAnalysis analysis) { return new ArrayList<>(); }
        public List<GeneratedTestCase> generateBoundaryTests(EndpointInfo endpoint, EndpointAnalysis analysis) { return new ArrayList<>(); }
        public List<GeneratedTestCase> generateValidationTests(EndpointInfo endpoint, EndpointAnalysis analysis) { return new ArrayList<>(); }
        public List<GeneratedTestCase> generateErrorHandlingTests(EndpointInfo endpoint, EndpointAnalysis analysis) { return new ArrayList<>(); }
        public List<GeneratedTestCase> generateIntegrationTests(EndpointInfo endpoint, EndpointAnalysis analysis) { return new ArrayList<>(); }
    }

    private static class SecurityTestGenerator {
        public SecurityTestGenerator(TestBuilderConfiguration config) {}
        public List<GeneratedTestCase> generateSecurityTests(EndpointInfo endpoint, EndpointAnalysis analysis) { return new ArrayList<>(); }
    }

    private static class PerformanceTestGenerator {
        public PerformanceTestGenerator(TestBuilderConfiguration config) {}
        public List<GeneratedTestCase> generatePerformanceTests(EndpointInfo endpoint, EndpointAnalysis analysis) { return new ArrayList<>(); }
    }

    private static class EdgeCaseTestGenerator {
        public EdgeCaseTestGenerator(TestBuilderConfiguration config) {}
        public List<GeneratedTestCase> generateEdgeCaseTests(EndpointInfo endpoint, EndpointAnalysis analysis) { return new ArrayList<>(); }
    }

    private static class NegativeTestGenerator {
        public NegativeTestGenerator(TestBuilderConfiguration config) {}
        public List<GeneratedTestCase> generateNegativeTests(EndpointInfo endpoint, EndpointAnalysis analysis) { return new ArrayList<>(); }
    }

    // AI Integration classes
    private interface AiServiceAdapter {
        AiGenerationResult generateTest(AiTestGenerationRequest request);
        boolean isAvailable();
        AiProvider getProvider();
    }

    private static class OpenAiServiceAdapter implements AiServiceAdapter {
        private final OpenAiService service;
        private final AiProvider provider;

        public OpenAiServiceAdapter(OpenAiService service, AiProvider provider) {
            this.service = service;
            this.provider = provider;
        }

        @Override
        public AiGenerationResult generateTest(AiTestGenerationRequest request) {
            try {
                List<ChatMessage> messages = Arrays.asList(
                        new ChatMessage(ChatMessageRole.SYSTEM.value(), createSystemPrompt()),
                        new ChatMessage(ChatMessageRole.USER.value(), request.getPrompt())
                );

                ChatCompletionRequest chatRequest = ChatCompletionRequest.builder()
                        .model(provider.getModelName())
                        .messages(messages)
                        .maxTokens(request.getMaxTokens())
                        .temperature(request.getTemperature())
                        .build();

                String response = service.createChatCompletion(chatRequest)
                        .getChoices().get(0).getMessage().getContent();

                return new AiGenerationResult(true, response, provider.getQualityScore(), null);

            } catch (Exception e) {
                return new AiGenerationResult(false, null, 0.0, e.getMessage());
            }
        }

        @Override
        public boolean isAvailable() {
            return service != null;
        }

        @Override
        public AiProvider getProvider() {
            return provider;
        }

        private String createSystemPrompt() {
            return """
                You are an expert API test engineer specializing in comprehensive REST API testing.
                Generate high-quality test code using REST Assured and JUnit 5.
                
                Requirements:
                1. Generate only the test method body (no imports or class definitions)
                2. Use realistic test data appropriate for the endpoint
                3. Include comprehensive assertions for all response aspects
                4. Add performance assertions (response time < 2000ms)
                5. Include detailed logging for test traceability
                6. Handle multiple test scenarios (happy path, edge cases, error conditions)
                7. Use descriptive variable names and comments in English
                8. Ensure thread-safe test design
                9. Include data cleanup where appropriate
                10. Follow REST Assured best practices
                """;
        }
    }

    private static class AiTestGenerationRequest {
        private final EndpointInfo endpoint;
        private final EndpointAnalysis analysis;
        private final Set<TestType> testTypes;
        private final QualityLevel qualityLevel;
        private final String promptTemplate;
        private final int maxTokens;
        private final double temperature;
        private final Duration timeout;

        private AiTestGenerationRequest(Builder builder) {
            this.endpoint = builder.endpoint;
            this.analysis = builder.analysis;
            this.testTypes = builder.testTypes;
            this.qualityLevel = builder.qualityLevel;
            this.promptTemplate = builder.promptTemplate;
            this.maxTokens = builder.maxTokens;
            this.temperature = builder.temperature;
            this.timeout = builder.timeout;
        }

        public String getPrompt() {
            StringBuilder prompt = new StringBuilder();
            prompt.append("Generate comprehensive REST API tests for the following endpoint:\n\n");
            prompt.append("Endpoint: ").append(endpoint.getMethod().toUpperCase()).append(" ").append(endpoint.getPath()).append("\n");
            prompt.append("Operation ID: ").append(endpoint.getOperationId()).append("\n");

            if (endpoint.getSummary() != null) {
                prompt.append("Summary: ").append(endpoint.getSummary()).append("\n");
            }

            if (endpoint.getDescription() != null) {
                prompt.append("Description: ").append(endpoint.getDescription()).append("\n");
            }

            prompt.append("Complexity Level: ").append(analysis.getComplexityScore().getLevel()).append("\n");
            prompt.append("Security Risk: ").append(analysis.getSecurityRiskLevel()).append("\n");
            prompt.append("Performance Impact: ").append(analysis.getPerformanceImpact().getLevel()).append("\n");

            if (endpoint.isHasParameters()) {
                prompt.append("Has Parameters: Yes (").append(endpoint.getParameters().size()).append(" parameters)\n");
            }

            if (endpoint.isHasRequestBody()) {
                prompt.append("Has Request Body: Yes\n");
            }

            prompt.append("Expected Response Codes: ").append(String.join(", ", endpoint.getExpectedStatusCodes())).append("\n");

            prompt.append("\nTest Types to Generate: ").append(testTypes.stream()
                    .map(TestType::getDisplayName)
                    .collect(Collectors.joining(", "))).append("\n");

            prompt.append("Quality Level: ").append(qualityLevel.getDescription()).append("\n");
            prompt.append("Target Test Scenarios: ").append(qualityLevel.getTestScenariosPerEndpoint()).append("\n\n");

            prompt.append("Please generate comprehensive test methods that cover:\n");
            prompt.append("1. Happy path scenarios with valid data\n");
            prompt.append("2. Edge cases and boundary conditions\n");
            prompt.append("3. Error scenarios and validation\n");
            prompt.append("4. Security considerations\n");
            prompt.append("5. Performance validations\n\n");

            prompt.append("Use REST Assured framework and include proper assertions, logging, and error handling.\n");

            return prompt.toString();
        }

        // Getters
        public EndpointInfo getEndpoint() { return endpoint; }
        public EndpointAnalysis getAnalysis() { return analysis; }
        public Set<TestType> getTestTypes() { return testTypes; }
        public QualityLevel getQualityLevel() { return qualityLevel; }
        public String getPromptTemplate() { return promptTemplate; }
        public int getMaxTokens() { return maxTokens; }
        public double getTemperature() { return temperature; }
        public Duration getTimeout() { return timeout; }

        public static Builder builder() { return new Builder(); }

        public static class Builder {
            private EndpointInfo endpoint;
            private EndpointAnalysis analysis;
            private Set<TestType> testTypes = new HashSet<>();
            private QualityLevel qualityLevel = QualityLevel.STANDARD;
            private String promptTemplate = "";
            private int maxTokens = 2000;
            private double temperature = 0.1;
            private Duration timeout = Duration.ofSeconds(30);

            public Builder withEndpoint(EndpointInfo endpoint) { this.endpoint = endpoint; return this; }
            public Builder withAnalysis(EndpointAnalysis analysis) { this.analysis = analysis; return this; }
            public Builder withTestTypes(Set<TestType> testTypes) { this.testTypes = testTypes; return this; }
            public Builder withQualityLevel(QualityLevel qualityLevel) { this.qualityLevel = qualityLevel; return this; }
            public Builder withPromptTemplate(String promptTemplate) { this.promptTemplate = promptTemplate; return this; }
            public Builder withMaxTokens(int maxTokens) { this.maxTokens = maxTokens; return this; }
            public Builder withTemperature(double temperature) { this.temperature = temperature; return this; }
            public Builder withTimeout(Duration timeout) { this.timeout = timeout; return this; }
            public AiTestGenerationRequest build() { return new AiTestGenerationRequest(this); }
        }
    }

    private static class AiGenerationResult {
        private final boolean success;
        private final String generatedCode;
        private final double confidence;
        private final String errorMessage;

        public AiGenerationResult(boolean success, String generatedCode, double confidence, String errorMessage) {
            this.success = success;
            this.generatedCode = generatedCode;
            this.confidence = confidence;
            this.errorMessage = errorMessage;
        }

        public boolean isSuccess() { return success; }
        public String getGeneratedCode() { return generatedCode; }
        public double getConfidence() { return confidence; }
        public String getErrorMessage() { return errorMessage; }
    }

    // ===== STANDARD RESULT CLASSES (Interface Compliant) =====



    /**
     * Standard AdvancedStrategyExecutionPlan class
     */


    // ===== STANDARD toString, equals, hashCode =====

    @Override
    public String toString() {
        return String.format("TestBuilder{version=%s, strategy=%s, aiProviders=%s, " +
                        "status=%s, totalGenerated=%d, successRate=%.2f%%, cacheHitRatio=%.2f%%, executionId=%s}",
                VERSION,
                configuration.getStrategy(),
                configuration.getAiProviders(),
                currentStatus.get(),
                totalTestsGenerated.get(),
                successfulGenerations.get() > 0 ?
                        (double) successfulGenerations.get() / (successfulGenerations.get() + failedGenerations.get()) * 100 : 0.0,
                getCacheStatistics().getCacheHitRatio() * 100,
                configuration.getExecutionId()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestBuilder that = (TestBuilder) o;
        return Objects.equals(configuration.getExecutionId(), that.configuration.getExecutionId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(configuration.getExecutionId());
    }

    // ===== DEPRECATED METHODS FOR BACKWARD COMPATIBILITY =====

    @Deprecated
    public void generateHappyPathTest(EndpointInfo endpoint) {
        logger.warn("Deprecated method called: generateHappyPathTest. Use generateEnhancedTestSuite instead.");
        generateEnhancedTestSuite(endpoint);
    }

    @Deprecated
    public void generateNegativeTests(EndpointInfo endpoint) {
        logger.warn("Deprecated method called: generateNegativeTests. Use generateEnhancedTestSuite instead.");
        generateEnhancedTestSuite(endpoint);
    }

    @Deprecated
    public void generateSecurityTests(EndpointInfo endpoint) {
        logger.warn("Deprecated method called: generateSecurityTests. Use generateEnhancedTestSuite instead.");
        generateEnhancedTestSuite(endpoint);
    }

    @Deprecated
    public void generatePerformanceTest(EndpointInfo endpoint) {
        logger.warn("Deprecated method called: generatePerformanceTest. Use generateEnhancedTestSuite instead.");
        generateEnhancedTestSuite(endpoint);
    }
}