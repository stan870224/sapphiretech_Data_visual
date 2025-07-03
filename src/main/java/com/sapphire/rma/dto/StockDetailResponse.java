package com.sapphire.rma.dto;

import java.util.Map;

/**
 * 庫存詳細資料回應 DTO
 */
public class StockDetailResponse {
    
    private boolean success;
    private String message;
    private Map<String, Object> stockRecord;
    private String productType;
    
    // 預設建構子
    public StockDetailResponse() {}
    
    // 成功回應建構子
    public StockDetailResponse(boolean success, String message, Map<String, Object> stockRecord, String productType) {
        this.success = success;
        this.message = message;
        this.stockRecord = stockRecord;
        this.productType = productType;
    }
    
    // 錯誤回應建構子
    public StockDetailResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.stockRecord = null;
        this.productType = null;
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
    
    public Map<String, Object> getStockRecord() {
        return stockRecord;
    }
    
    public void setStockRecord(Map<String, Object> stockRecord) {
        this.stockRecord = stockRecord;
    }
    
    public String getProductType() {
        return productType;
    }
    
    public void setProductType(String productType) {
        this.productType = productType;
    }
    
    /**
     * 建立成功回應
     */
    public static StockDetailResponse success(String message, Map<String, Object> stockRecord, String productType) {
        return new StockDetailResponse(true, message, stockRecord, productType);
    }
    
    /**
     * 建立錯誤回應
     */
    public static StockDetailResponse error(String message) {
        return new StockDetailResponse(false, message);
    }
    
    /**
     * 檢查是否有庫存資料
     */
    public boolean hasStockRecord() {
        return stockRecord != null && !stockRecord.isEmpty();
    }
    
    @Override
    public String toString() {
        return "StockDetailResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", hasStockRecord=" + hasStockRecord() +
                ", productType='" + productType + '\'' +
                '}';
    }
}