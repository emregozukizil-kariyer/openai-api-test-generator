// ===== ReportWriter.java - Rapor Oluşturucu =====

package org.example.openapi;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * ReportWriter - Sadece rapor oluşturur
 * Eski koddan rapor ile ilgili tüm işlemler buraya taşındı
 */
public class ReportWriter {

    private static final Logger logger = Logger.getLogger(ReportWriter.class.getName());
    private final Configuration config;

    public ReportWriter(Configuration config) {
        this.config = config;
    }

    /**
     * Ana rapor oluşturma metodu
     */
    public void generateReport(List<EndpointInfo> endpoints, List<String> testCases) {
        if (!config.isGenerateReport()) {
            logger.info("Rapor oluşturma kapalı, atlanıyor...");
            return;
        }

        try {
            String reportPath = createReportPath();
            writeHtmlReport(reportPath, endpoints, testCases);
            logger.info("Rapor oluşturuldu: " + reportPath);
        } catch (Exception e) {
            logger.warning("Rapor oluşturulamadı: " + e.getMessage());
        }
    }

    /**
     * HTML raporu yazar
     */
    private void writeHtmlReport(String reportPath, List<EndpointInfo> endpoints, List<String> testCases) throws IOException {
        try (FileWriter writer = new FileWriter(reportPath)) {
            writeHtmlHeader(writer);
            writeReportSummary(writer, endpoints, testCases);
            writeEndpointDetails(writer, endpoints);
            writeHtmlFooter(writer);
        }
    }

    /**
     * HTML başlığını yazar
     */
    private void writeHtmlHeader(FileWriter writer) throws IOException {
        writer.write("<!DOCTYPE html>\n");
        writer.write("<html>\n");
        writer.write("<head>\n");
        writer.write("    <title>API Test Raporu</title>\n");
        writer.write("    <style>\n");
        writer.write("        body { font-family: Arial, sans-serif; margin: 20px; }\n");
        writer.write("        .header { background: #4CAF50; color: white; padding: 20px; }\n");
        writer.write("        .summary { background: #f1f1f1; padding: 15px; margin: 20px 0; }\n");
        writer.write("        .endpoint { border: 1px solid #ddd; margin: 10px 0; padding: 10px; }\n");
        writer.write("        .get { border-left: 5px solid #61affe; }\n");
        writer.write("        .post { border-left: 5px solid #49cc90; }\n");
        writer.write("        .put { border-left: 5px solid #fca130; }\n");
        writer.write("        .delete { border-left: 5px solid #f93e3e; }\n");
        writer.write("    </style>\n");
        writer.write("</head>\n");
        writer.write("<body>\n");

        // Başlık
        writer.write("    <div class='header'>\n");
        writer.write("        <h1>🚀 API Test Raporu</h1>\n");
        writer.write("        <p>Oluşturulma Tarihi: " + new Date() + "</p>\n");
        writer.write("    </div>\n");
    }

    /**
     * Rapor özetini yazar
     */
    private void writeReportSummary(FileWriter writer, List<EndpointInfo> endpoints, List<String> testCases) throws IOException {
        writer.write("    <div class='summary'>\n");
        writer.write("        <h2>📊 Özet</h2>\n");
        writer.write("        <p><strong>Toplam Endpoint:</strong> " + endpoints.size() + "</p>\n");
        writer.write("        <p><strong>Oluşturulan Test:</strong> " + testCases.size() + "</p>\n");
        writer.write("        <p><strong>Giriş Dosyası:</strong> " + config.getInputFile() + "</p>\n");
        writer.write("        <p><strong>Çıkış Dosyası:</strong> " + config.getOutputFile() + "</p>\n");

        // HTTP metod dağılımı
        writeMethodDistribution(writer, endpoints);

        writer.write("    </div>\n");
    }

    /**
     * HTTP metod dağılımını yazar
     */
    private void writeMethodDistribution(FileWriter writer, List<EndpointInfo> endpoints) throws IOException {
        int getCount = 0, postCount = 0, putCount = 0, deleteCount = 0, otherCount = 0;

        for (EndpointInfo endpoint : endpoints) {
            switch (endpoint.getMethod().toLowerCase()) {
                case "get": getCount++; break;
                case "post": postCount++; break;
                case "put": putCount++; break;
                case "delete": deleteCount++; break;
                default: otherCount++; break;
            }
        }

        writer.write("        <h3>HTTP Metod Dağılımı</h3>\n");
        writer.write("        <ul>\n");
        if (getCount > 0) writer.write("            <li>GET: " + getCount + "</li>\n");
        if (postCount > 0) writer.write("            <li>POST: " + postCount + "</li>\n");
        if (putCount > 0) writer.write("            <li>PUT: " + putCount + "</li>\n");
        if (deleteCount > 0) writer.write("            <li>DELETE: " + deleteCount + "</li>\n");
        if (otherCount > 0) writer.write("            <li>Diğer: " + otherCount + "</li>\n");
        writer.write("        </ul>\n");
    }

    /**
     * Endpoint detaylarını yazar
     */
    private void writeEndpointDetails(FileWriter writer, List<EndpointInfo> endpoints) throws IOException {
        writer.write("    <h2>🔗 Endpoint Detayları</h2>\n");

        for (EndpointInfo endpoint : endpoints) {
            String cssClass = endpoint.getMethod().toLowerCase();

            writer.write("    <div class='endpoint " + cssClass + "'>\n");
            writer.write("        <h3>" + endpoint.getMethod().toUpperCase() + " " + endpoint.getPath() + "</h3>\n");
            writer.write("        <p><strong>Operasyon ID:</strong> " + endpoint.getOperationId() + "</p>\n");

            if (endpoint.isHasParameters()) {
                writer.write("        <p>✅ Parametreler var</p>\n");
            }

            if (endpoint.isHasRequestBody()) {
                writer.write("        <p>✅ Request Body var</p>\n");
            }

            writer.write("        <p><strong>Test Metodu:</strong> test" + sanitizeMethodName(endpoint.getOperationId()) + "()</p>\n");
            writer.write("    </div>\n");
        }
    }

    /**
     * HTML sonunu yazar
     */
    private void writeHtmlFooter(FileWriter writer) throws IOException {
        writer.write("    <hr>\n");
        writer.write("    <p><small>Test Generator v4.0 ile oluşturuldu</small></p>\n");
        writer.write("</body>\n");
        writer.write("</html>\n");
    }

    /**
     * Rapor dosya yolunu oluşturur
     */
    private String createReportPath() {
        String outputFile = config.getOutputFile();
        if (outputFile.endsWith(".java")) {
            return outputFile.replace(".java", "_report.html");
        } else {
            return outputFile + "_report.html";
        }
    }

    /**
     * Metod ismini temizler
     */
    private String sanitizeMethodName(String name) {
        if (name == null) return "unknown";
        return name.replaceAll("[^a-zA-Z0-9]", "_");
    }

    /**
     * Basit metin raporu oluşturur (HTML'e alternatif)
     */
    public void generateSimpleReport(List<EndpointInfo> endpoints, List<String> testCases) {
        logger.info("=== API TEST RAPORU ===");
        logger.info("Toplam Endpoint: " + endpoints.size());
        logger.info("Oluşturulan Test: " + testCases.size());
        logger.info("Giriş Dosyası: " + config.getInputFile());
        logger.info("Çıkış Dosyası: " + config.getOutputFile());
        logger.info("========================");

        for (EndpointInfo endpoint : endpoints) {
            logger.info("- " + endpoint.getMethod().toUpperCase() + " " + endpoint.getPath());
        }
    }
}