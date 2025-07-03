package com.sapphire.rma.dto;

import java.util.Map;

/**
 * 庫存統計回應 DTO
 */
public class StockStatsResponse {
    
    private boolean success;
    private String message;
    private Map<String, Object> stats;
    
    // 預設建構子
    public StockStatsResponse() {}
    
    // 完整建構子
    public StockStatsResponse(boolean success, String message, Map<String, Object> stats) {
        this.success = success;
        this.message = message;
        this.stats = stats;
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
    
    public Map<String, Object> getStats() {
        return stats;
    }
    
    public void setStats(Map<String, Object> stats) {
        this.stats = stats;
    }
    
    /**
     * 建立成功回應
     */
    public static StockStatsResponse success(String message, Map<String, Object> stats) {
        return new StockStatsResponse(true, message, stats);
    }
    
    /**
     * 建立錯誤回應
     */
    public static StockStatsResponse error(String message) {
        return new StockStatsResponse(false, message, null);
    }
    
    /**
     * 檢查是否有統計資料
     */
    public boolean hasStats() {
        return stats != null && !stats.isEmpty();
    }
    
    @Override
    public String toString() {
        return "StockStatsResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", hasStats=" + hasStats() +
                '}';
    }
}