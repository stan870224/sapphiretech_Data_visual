package com.sapphire.rma.dto;

import java.util.List;
import java.util.Map;

/**
 * RMA 搜尋回應 DTO
 * 用於資料查詢頁面的搜尋結果
 */
public class RmaSearchResponse {
    
    private boolean success;
    private String message;
    private List<Map<String, Object>> rmaRecords;
    private int totalCount;
    private String productType;
    
    // 預設建構子
    public RmaSearchResponse() {}
    
    // 成功回應建構子
    public RmaSearchResponse(boolean success, String message, List<Map<String, Object>> rmaRecords, int totalCount, String productType) {
        this.success = success;
        this.message = message;
        this.rmaRecords = rmaRecords;
        this.totalCount = totalCount;
        this.productType = productType;
    }
    
    // 錯誤回應建構子
    public RmaSearchResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.rmaRecords = null;
        this.totalCount = 0;
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
    
    public List<Map<String, Object>> getRmaRecords() {
        return rmaRecords;
    }
    
    public void setRmaRecords(List<Map<String, Object>> rmaRecords) {
        this.rmaRecords = rmaRecords;
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
    public static RmaSearchResponse success(String message, List<Map<String, Object>> rmaRecords, String productType) {
        return new RmaSearchResponse(true, message, rmaRecords, 
                                   rmaRecords != null ? rmaRecords.size() : 0, productType);
    }
    
    /**
     * 建立錯誤回應
     */
    public static RmaSearchResponse error(String message) {
        return new RmaSearchResponse(false, message);
    }
    
    @Override
    public String toString() {
        return "RmaSearchResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", totalCount=" + totalCount +
                ", productType='" + productType + '\'' +
                '}';
    }
}