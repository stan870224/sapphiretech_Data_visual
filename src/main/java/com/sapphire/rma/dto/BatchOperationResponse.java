package com.sapphire.rma.dto;

import java.util.Map;

/**
 * 批次操作回應 DTO
 */
public class BatchOperationResponse {
    
    private boolean success;
    private String message;
    private Map<String, Object> result;
    
    // 預設建構子
    public BatchOperationResponse() {}
    
    // 完整建構子
    public BatchOperationResponse(boolean success, String message, Map<String, Object> result) {
        this.success = success;
        this.message = message;
        this.result = result;
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
    
    public Map<String, Object> getResult() {
        return result;
    }
    
    public void setResult(Map<String, Object> result) {
        this.result = result;
    }
    
    /**
     * 建立成功回應
     */
    public static BatchOperationResponse success(String message, Map<String, Object> result) {
        return new BatchOperationResponse(true, message, result);
    }
    
    /**
     * 建立錯誤回應
     */
    public static BatchOperationResponse error(String message) {
        return new BatchOperationResponse(false, message, null);
    }
    
    /**
     * 檢查是否有結果資料
     */
    public boolean hasResult() {
        return result != null && !result.isEmpty();
    }
    
    @Override
    public String toString() {
        return "BatchOperationResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", hasResult=" + hasResult() +
                '}';
    }
}