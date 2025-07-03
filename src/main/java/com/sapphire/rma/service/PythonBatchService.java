package com.sapphire.rma.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class PythonBatchService {
    
    @Autowired
    private ProductLineService productLineService;
    
    @Value("${spring.datasource.url}")
    private String databaseUrl;
    
    @Value("${spring.datasource.username}")
    private String databaseUsername;
    
    @Value("${spring.datasource.password}")
    private String databasePassword;
    
    @Value("${python.executable:python}")
    private String pythonExecutable;
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 執行批次處理
     */
    public BatchResult executeBatchProcess(String productType) {
        try {
            // 先驗證產品線是否有效
            if (!productLineService.isValidProductLine(productType)) {
                return createErrorResult(productType, 
                    "無效的產品線: " + productType + "，可用的產品線: " + 
                    productLineService.getAllProductLineNames());
            }
            
            // 建立 Python 腳本執行命令
            ProcessBuilder processBuilder = new ProcessBuilder(
                pythonExecutable, "backend/app.py",
                "--db-url", databaseUrl,
                "--db-user", databaseUsername,
                "--db-password", databasePassword,
                "--product-type", productType
            );
            
            // 設定工作目錄為專案根目錄
            processBuilder.directory(new File("."));

            Map<String, String> env = processBuilder.environment();
            env.put("PYTHONIOENCODING", "utf-8");
            env.put("PYTHONLEGACYWINDOWSSTDIO", "utf-8");
            
            return executeProcess(processBuilder, productType);
            
        } catch (Exception e) {
            return createErrorResult(productType, "執行批次處理失敗: " + e.getMessage());
        }
    }
    
    /**
     * 執行 Python 程序並處理結果
     */
    private BatchResult executeProcess(ProcessBuilder processBuilder, String productType) {
        try {
            // 啟動程序
            Process process = processBuilder.start();
            
            // 讀取標準輸出
            BufferedReader outputReader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), "UTF-8")
            );
            
            // 讀取錯誤輸出
            BufferedReader errorReader = new BufferedReader(
                new InputStreamReader(process.getErrorStream(), "UTF-8")
            );
            
            StringBuilder outputBuilder = new StringBuilder();
            StringBuilder errorBuilder = new StringBuilder();
            
            String line;
            while ((line = outputReader.readLine()) != null) {
                outputBuilder.append(line).append("\n");
            }
            
            while ((line = errorReader.readLine()) != null) {
                errorBuilder.append(line).append("\n");
            }
            
            // 等待程序完成，最多等待 10 分鐘
            boolean finished = process.waitFor(10, TimeUnit.MINUTES);
            
            if (!finished) {
                process.destroyForcibly();
                return createErrorResult(productType, "程序執行超時 (超過 10 分鐘)");
            }
            
            int exitCode = process.exitValue();
            String output = outputBuilder.toString().trim();
            String errorOutput = errorBuilder.toString().trim();
            
            if (exitCode == 0) {
                // 解析 JSON 結果
                return parseJsonResult(output, productType);
            } else {
                String errorMessage = "程序執行失敗，退出碼: " + exitCode;
                if (!errorOutput.isEmpty()) {
                    errorMessage += "\n錯誤輸出: " + errorOutput;
                }
                if (!output.isEmpty()) {
                    errorMessage += "\n標準輸出: " + output;
                }
                return createErrorResult(productType, errorMessage);
            }
        } catch (Exception e) {
            return createErrorResult(productType, "執行程序時發生錯誤: " + e.getMessage());
        }
    }
    
    /**
     * 解析 Python 腳本回傳的 JSON 結果
     */
    private BatchResult parseJsonResult(String jsonOutput, String productType) {
        try {
            // 找到 JSON 開始位置（第一個 { ）
            int jsonStart = jsonOutput.indexOf('{');
            if (jsonStart == -1) {
                return createErrorResult(productType, "無法找到有效的 JSON 輸出");
            }

            // 找到 JSON 結束位置（最後一個 } ）
            int jsonEnd = jsonOutput.lastIndexOf('}');
            if (jsonEnd == -1 || jsonEnd <= jsonStart) {
                return createErrorResult(productType, "無法找到完整的 JSON 輸出");
            }

            // 提取純 JSON 部分
            String pureJson = jsonOutput.substring(jsonStart, jsonEnd + 1);

            JsonNode jsonNode = objectMapper.readTree(pureJson);

            BatchResult result = new BatchResult();
            result.setSuccess(jsonNode.get("success").asBoolean());
            result.setMessage(jsonNode.get("message").asText());
            result.setProductType(jsonNode.get("productType").asText());

            if (jsonNode.has("rmaStats")) {
                JsonNode rmaStats = jsonNode.get("rmaStats");
                BatchStats rmaResult = new BatchStats();
                rmaResult.setInserted(rmaStats.get("inserted").asInt());
                rmaResult.setUpdated(rmaStats.get("updated").asInt());
                rmaResult.setTotal(rmaStats.get("total").asInt());
                rmaResult.setMessage(rmaStats.get("message").asText());
                result.setRmaStats(rmaResult);
            }

            if (jsonNode.has("stockStats")) {
                JsonNode stockStats = jsonNode.get("stockStats");
                BatchStats stockResult = new BatchStats();
                stockResult.setInserted(stockStats.get("inserted").asInt());
                stockResult.setUpdated(stockStats.get("updated").asInt());
                stockResult.setTotal(stockStats.get("total").asInt());
                stockResult.setMessage(stockStats.get("message").asText());
                result.setStockStats(stockResult);
            }

            return result;

        } catch (Exception e) {
            return createErrorResult(productType, "解析處理結果失敗: " + e.getMessage());
        }
    }
    
    /**
     * 建立錯誤結果
     */
    private BatchResult createErrorResult(String productType, String errorMessage) {
        BatchResult result = new BatchResult();
        result.setSuccess(false);
        result.setMessage(errorMessage);
        result.setProductType(productType);
        return result;
    }
    
    /**
     * 批次處理結果類別
     */
    public static class BatchResult {
        private boolean success;
        private String message;
        private String productType;
        private BatchStats rmaStats;
        private BatchStats stockStats;
        
        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public String getProductType() { return productType; }
        public void setProductType(String productType) { this.productType = productType; }
        
        public BatchStats getRmaStats() { return rmaStats; }
        public void setRmaStats(BatchStats rmaStats) { this.rmaStats = rmaStats; }
        
        public BatchStats getStockStats() { return stockStats; }
        public void setStockStats(BatchStats stockStats) { this.stockStats = stockStats; }
    }
    
    /**
     * 批次統計類別
     */
    public static class BatchStats {
        private int inserted;
        private int updated;
        private int total;
        private String message;
        
        // Getters and Setters
        public int getInserted() { return inserted; }
        public void setInserted(int inserted) { this.inserted = inserted; }
        
        public int getUpdated() { return updated; }
        public void setUpdated(int updated) { this.updated = updated; }
        
        public int getTotal() { return total; }
        public void setTotal(int total) { this.total = total; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}