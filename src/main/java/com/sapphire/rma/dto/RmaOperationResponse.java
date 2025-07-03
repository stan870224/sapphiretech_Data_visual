package com.sapphire.rma.dto;

/**
 * RMA 操作回應 DTO
 * 用於新增、更新、刪除等操作的統一回應格式
 */
public class RmaOperationResponse {
    
    private boolean success;
    private String message;
    private String operation; // CREATE, UPDATE, DELETE
    private String productType;
    private String serialNo;
    private Object data; // 可選的額外資料
    
    // 預設建構子
    public RmaOperationResponse() {}
    
    // 完整建構子
    public RmaOperationResponse(boolean success, String message, String operation, 
                              String productType, String serialNo, Object data) {
        this.success = success;
        this.message = message;
        this.operation = operation;
        this.productType = productType;
        this.serialNo = serialNo;
        this.data = data;
    }
    
    // 簡化建構子
    public RmaOperationResponse(boolean success, String message, String operation) {
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
    public static RmaOperationResponse createSuccess(String productType, String serialNo) {
        return new RmaOperationResponse(true, "RMA 記錄新增成功", "CREATE", productType, serialNo, null);
    }
    
    /**
     * 建立成功回應 - 更新操作
     */
    public static RmaOperationResponse updateSuccess(String productType, String serialNo) {
        return new RmaOperationResponse(true, "RMA 記錄更新成功", "UPDATE", productType, serialNo, null);
    }
    
    /**
     * 建立成功回應 - 更新操作（含庫存刪除）
     */
    public static RmaOperationResponse updateWithStockDeleteSuccess(String productType, String serialNo, String deletedStockSerialNo) {
        String message = "RMA 記錄更新成功，庫存 " + deletedStockSerialNo + " 已刪除";
        return new RmaOperationResponse(true, message, "UPDATE_WITH_STOCK_DELETE", productType, serialNo, deletedStockSerialNo);
    }
    
    /**
     * 建立成功回應 - 刪除操作
     */
    public static RmaOperationResponse deleteSuccess(String productType, String serialNo) {
        return new RmaOperationResponse(true, "RMA 記錄刪除成功", "DELETE", productType, serialNo, null);
    }
    
    /**
     * 建立錯誤回應
     */
    public static RmaOperationResponse error(String message, String operation) {
        return new RmaOperationResponse(false, message, operation);
    }
    
    /**
     * 建立錯誤回應 - 新增操作
     */
    public static RmaOperationResponse createError(String message) {
        return new RmaOperationResponse(false, message, "CREATE");
    }
    
    /**
     * 建立錯誤回應 - 更新操作
     */
    public static RmaOperationResponse updateError(String message) {
        return new RmaOperationResponse(false, message, "UPDATE");
    }
    
    /**
     * 建立錯誤回應 - 刪除操作
     */
    public static RmaOperationResponse deleteError(String message) {
        return new RmaOperationResponse(false, message, "DELETE");
    }
    
    /**
     * 操作類型常數
     */
    public static final class Operations {
        public static final String CREATE = "CREATE";
        public static final String UPDATE = "UPDATE";
        public static final String UPDATE_WITH_STOCK_DELETE = "UPDATE_WITH_STOCK_DELETE";
        public static final String DELETE = "DELETE";
        public static final String SEARCH = "SEARCH";
        
        private Operations() {
            // 防止實例化
        }
    }
    
    @Override
    public String toString() {
        return "RmaOperationResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", operation='" + operation + '\'' +
                ", productType='" + productType + '\'' +
                ", serialNo='" + serialNo + '\'' +
                ", hasData=" + (data != null) +
                '}';
    }
}