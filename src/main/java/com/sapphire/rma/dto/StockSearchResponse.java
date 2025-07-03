package com.sapphire.rma.dto;

import java.util.List;
import java.util.Map;

/**
 * 庫存搜尋回應 DTO
 */
public class StockSearchResponse {
    
    private boolean success;
    private String message;
    private List<Map<String, Object>> stockRecords;
    private int totalCount;
    private String productType;
    
    // 預設建構子
    public StockSearchResponse() {}
    
    // 成功回應建構子
    public StockSearchResponse(boolean success, String message, List<Map<String, Object>> stockRecords, String productType) {
        this.success = success;
        this.message = message;
        this.stockRecords = stockRecords;
        this.totalCount = stockRecords != null ? stockRecords.size() : 0;
        this.productType = productType;
    }
    
    // 錯誤回應建構子
    public StockSearchResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.stockRecords = null;
        this.totalCount = 0;
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
    
    public List<Map<String, Object>> getStockRecords() {
        return stockRecords;
    }
    
    public void setStockRecords(List<Map<String, Object>> stockRecords) {
        this.stockRecords = stockRecords;
        this.totalCount = stockRecords != null ? stockRecords.size() : 0;
    }
    
    public int getTotalCount() {
        return totalCount;
    }
    
    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
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
    public static StockSearchResponse success(String message, List<Map<String, Object>> stockRecords, String productType) {
        return new StockSearchResponse(true, message, stockRecords, productType);
    }
    
    /**
     * 建立錯誤回應
     */
    public static StockSearchResponse error(String message) {
        return new StockSearchResponse(false, message);
    }
    
    @Override
    public String toString() {
        return "StockSearchResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", totalCount=" + totalCount +
                ", productType='" + productType + '\'' +
                '}';
    }
}