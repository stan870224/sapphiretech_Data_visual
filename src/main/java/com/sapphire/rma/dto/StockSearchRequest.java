package com.sapphire.rma.dto;

import javax.validation.constraints.NotBlank;

/**
 * 庫存搜尋請求 DTO
 */
public class StockSearchRequest {
    
    @NotBlank(message = "產品線不能為空")
    private String productType;
    
    private String serialNo;
    private String pn;
    private String sku;
    private String productName;
    
    // 預設建構子
    public StockSearchRequest() {}
    
    // 建構子
    public StockSearchRequest(String productType) {
        this.productType = productType;
    }
    
    // Getters and Setters
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
    
    public String getPn() {
        return pn;
    }
    
    public void setPn(String pn) {
        this.pn = pn;
    }
    
    public String getSku() {
        return sku;
    }
    
    public void setSku(String sku) {
        this.sku = sku;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    /**
     * 檢查是否有搜尋條件
     */
    public boolean hasSearchCriteria() {
        return (serialNo != null && !serialNo.trim().isEmpty()) ||
               (pn != null && !pn.trim().isEmpty()) ||
               (sku != null && !sku.trim().isEmpty()) ||
               (productName != null && !productName.trim().isEmpty());
    }
    
    @Override
    public String toString() {
        return "StockSearchRequest{" +
                "productType='" + productType + '\'' +
                ", serialNo='" + serialNo + '\'' +
                ", pn='" + pn + '\'' +
                ", sku='" + sku + '\'' +
                ", productName='" + productName + '\'' +
                '}';
    }
}