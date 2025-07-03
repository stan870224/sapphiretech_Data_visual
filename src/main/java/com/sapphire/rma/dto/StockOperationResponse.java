package com.sapphire.rma.dto;

/**
 * 庫存操作回應 DTO
 */
public class StockOperationResponse {
    
    private boolean success;
    private String message;
    private String operation; // CREATE, UPDATE, DELETE
    private String productType;
    private String serialNo;
    private Object data;
    
    // 預設建構子
    public StockOperationResponse() {}
    
    // 完整建構子
    public StockOperationResponse(boolean success, String message, String operation, 
                                String productType, String serialNo, Object data) {
        this.success = success;
        this.message = message;
        this.operation = operation;
        this.productType = productType;
        this.serialNo = serialNo;
        this.data = data;
    }
    
    // 簡化建構子
    public StockOperationResponse(boolean success, String message, String operation) {
        this.success = success;
        this.message = message;
        this.operation = operation;
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
    
    public String getOperation() {
        return operation;
    }
    
    public void setOperation(String operation) {
        this.operation = operation;
    }
    
    public String getProductType() {
        return productType;
    }
    
    public void setProductType(String productType) {
        this.productType = productType;
    }
    
    public String getSerialNo() {
        return serialNo;
    }
    
    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }
    
    public Object getData() {
        return data;
    }
    
    public void setData(Object data) {
        this.data = data;
    }
    
    /**
     * 建立成功回應 - 新增操作
     */
    public static StockOperationResponse createSuccess(String productType, String serialNo) {
        return new StockOperationResponse(true, "庫存記錄新增成功", "CREATE", productType, serialNo, null);
    }
    
    /**
     * 建立成功回應 - 更新操作
     */
    public static StockOperationResponse updateSuccess(String productType, String serialNo) {
        return new StockOperationResponse(true, "庫存記錄更新成功", "UPDATE", productType, serialNo, null);
    }
    
    /**
     * 建立成功回應 - 刪除操作
     */
    public static StockOperationResponse deleteSuccess(String productType, String serialNo) {
        return new StockOperationResponse(true, "庫存記錄刪除成功", "DELETE", productType, serialNo, null);
    }
    
    /**
     * 建立錯誤回應 - 新增操作
     */
    public static StockOperationResponse createError(String message) {
        return new StockOperationResponse(false, message, "CREATE");
    }
    
    /**
     * 建立錯誤回應 - 更新操作
     */
    public static StockOperationResponse updateError(String message) {
        return new StockOperationResponse(false, message, "UPDATE");
    }
    
    /**
     * 建立錯誤回應 - 刪除操作
     */
    public static StockOperationResponse deleteError(String message) {
        return new StockOperationResponse(false, message, "DELETE");
    }
    
    /**
     * 操作類型常數
     */
    public static final class Operations {
        public static final String CREATE = "CREATE";
        public static final String UPDATE = "UPDATE";
        public static final String DELETE = "DELETE";
        
        private Operations() {
            // 防止實例化
        }
    }
    
    @Override
    public String toString() {
        return "StockOperationResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", operation='" + operation + '\'' +
                ", productType='" + productType + '\'' +
                ", serialNo='" + serialNo + '\'' +
                ", hasData=" + (data != null) +
                '}';
    }
}