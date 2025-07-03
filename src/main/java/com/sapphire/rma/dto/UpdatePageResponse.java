package com.sapphire.rma.dto;

import java.util.List;
import java.util.Map;

/**
 * 資料調整頁面回應 DTO
 * 包含 RMA 記錄和對應的庫存資料
 */
public class UpdatePageResponse {
    
    private boolean success;
    private String message;
    private String productType;
    private Map<String, Object> rmaRecord;
    private List<Map<String, Object>> stockRecords;
    private int stockCount;
    
    // 預設建構子
    public UpdatePageResponse() {}
    
    // 成功回應建構子
    public UpdatePageResponse(boolean success, String message, String productType, 
                            Map<String, Object> rmaRecord, List<Map<String, Object>> stockRecords) {
        this.success = success;
        this.message = message;
        this.productType = productType;
        this.rmaRecord = rmaRecord;
        this.stockRecords = stockRecords;
        this.stockCount = stockRecords != null ? stockRecords.size() : 0;
    }
    
    // 錯誤回應建構子
    public UpdatePageResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.rmaRecord = null;
        this.stockRecords = null;
        this.stockCount = 0;
    }
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getProductType() {
        return productType;
    }
    
    public void setProductType(String productType) {
        this.productType = productType;
    }
    
    public Map<String, Object> getRmaRecord() {
        return rmaRecord;
    }
    
    public void setRmaRecord(Map<String, Object> rmaRecord) {
        this.rmaRecord = rmaRecord;
    }
    
    public List<Map<String, Object>> getStockRecords() {
        return stockRecords;
    }
    
    public void setStockRecords(List<Map<String, Object>> stockRecords) {
        this.stockRecords = stockRecords;
        this.stockCount = stockRecords != null ? stockRecords.size() : 0;
    }
    
    public int getStockCount() {
        return stockCount;
    }
    
    public void setStockCount(int stockCount) {
        this.stockCount = stockCount;
    }
    
    /**
     * 建立成功回應（有 RMA 和庫存資料）
     */
    public static UpdatePageResponse success(String message, String productType, 
                                           Map<String, Object> rmaRecord, List<Map<String, Object>> stockRecords) {
        return new UpdatePageResponse(true, message, productType, rmaRecord, stockRecords);
    }
    
    /**
     * 建立成功回應（只有庫存資料，用於初始載入）
     */
    public static UpdatePageResponse successWithStockOnly(String message, String productType, 
                                                        List<Map<String, Object>> stockRecords) {
        return new UpdatePageResponse(true, message, productType, null, stockRecords);
    }
    
    /**
     * 建立錯誤回應
     */
    public static UpdatePageResponse error(String message) {
        return new UpdatePageResponse(false, message);
    }
    
    /**
     * 檢查是否有 RMA 資料
     */
    public boolean hasRmaRecord() {
        return rmaRecord != null && !rmaRecord.isEmpty();
    }
    
    /**
     * 檢查是否有庫存資料
     */
    public boolean hasStockRecords() {
        return stockRecords != null && !stockRecords.isEmpty();
    }
    
    @Override
    public String toString() {
        return "UpdatePageResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", productType='" + productType + '\'' +
                ", hasRmaRecord=" + hasRmaRecord() +
                ", stockCount=" + stockCount +
                '}';
    }
}