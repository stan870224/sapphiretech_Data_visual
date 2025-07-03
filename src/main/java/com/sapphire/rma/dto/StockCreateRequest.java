package com.sapphire.rma.dto;

import javax.validation.constraints.NotBlank;

/**
 * 庫存新增請求 DTO
 */
public class StockCreateRequest {
    
    @NotBlank(message = "產品線不能為空")
    private String productType;
    
    @NotBlank(message = "序列號不能為空")
    private String serialNo;
    
    private String productName;
    private String pn;
    private String sku;
    
    // 預設建構子
    public StockCreateRequest() {}
    
    // 必要欄位建構子
    public StockCreateRequest(String productType, String serialNo) {
        this.productType = productType;
        this.serialNo = serialNo;
    }
    
    // 完整建構子
    public StockCreateRequest(String productType, String serialNo, String productName, String pn, String sku) {
        this.productType = productType;
        this.serialNo = serialNo;
        this.productName = productName;
        this.pn = pn;
        this.sku = sku;
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
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
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
    
    @Override
    public String toString() {
        return "StockCreateRequest{" +
                "productType='" + productType + '\'' +
                ", serialNo='" + serialNo + '\'' +
                ", productName='" + productName + '\'' +
                ", pn='" + pn + '\'' +
                ", sku='" + sku + '\'' +
                '}';
    }
}