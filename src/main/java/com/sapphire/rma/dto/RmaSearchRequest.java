package com.sapphire.rma.dto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

/**
 * RMA 搜尋請求 DTO
 * 用於資料查詢頁面的搜尋功能
 */
public class RmaSearchRequest {
    
    @NotBlank(message = "產品線不能為空")
    private String productType;
    
    private String serialNo;
    private String pn;
    private String sku;
    private LocalDate startDate;
    private LocalDate endDate;
    
    // 預設建構子
    public RmaSearchRequest() {}
    
    // 建構子
    public RmaSearchRequest(String productType) {
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
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    /**
     * 檢查是否有搜尋條件
     */
    public boolean hasSearchCriteria() {
        return (serialNo != null && !serialNo.trim().isEmpty()) ||
               (pn != null && !pn.trim().isEmpty()) ||
               (sku != null && !sku.trim().isEmpty()) ||
               startDate != null ||
               endDate != null;
    }
    
    @Override
    public String toString() {
        return "RmaSearchRequest{" +
                "productType='" + productType + '\'' +
                ", serialNo='" + serialNo + '\'' +
                ", pn='" + pn + '\'' +
                ", sku='" + sku + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}