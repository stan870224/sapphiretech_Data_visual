package com.sapphire.rma.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * RMA 新增請求 DTO
 * 用於資料調整頁面的新增功能
 */
public class RmaCreateRequest {
    
    @NotBlank(message = "產品線不能為空")
    private String productType;
    
    @NotBlank(message = "序列號不能為空")
    private String serialNo;
    
    private String rmaNo;
    private String customerName;
    private String pn;
    private String sku;
    private String productName;
    private String sellShipDate;
    private String createDate;
    private String returnDate;
    private String failureDesc;
    private String viDamageStatus;
    private String testResultDesc;
    private String replacementSnInTw;
    private String replacementPnInTw;
    private String replacementSkuInTw;
    private String replacementSnFromHk;
    private String replacementPnFromHk;
    private String replacementSkuFromHk;
    private String rmaBoardTestResult;
    private String endUserInvoiceDate;
    private String warrantyUntil;
    private String remark;
    
    // 預設建構子
    public RmaCreateRequest() {}
    
    // 必要欄位建構子
    public RmaCreateRequest(String productType, String serialNo) {
        this.productType = productType;
        this.serialNo = serialNo;
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
    
    public String getRmaNo() {
        return rmaNo;
    }
    
    public void setRmaNo(String rmaNo) {
        this.rmaNo = rmaNo;
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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
    
    public String getSellShipDate() {
        return sellShipDate;
    }
    
    public void setSellShipDate(String sellShipDate) {
        this.sellShipDate = sellShipDate;
    }
    
    public String getCreateDate() {
        return createDate;
    }
    
    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
    
    public String getReturnDate() {
        return returnDate;
    }
    
    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }
    
    public String getFailureDesc() {
        return failureDesc;
    }
    
    public void setFailureDesc(String failureDesc) {
        this.failureDesc = failureDesc;
    }
    
    public String getViDamageStatus() {
        return viDamageStatus;
    }
    
    public void setViDamageStatus(String viDamageStatus) {
        this.viDamageStatus = viDamageStatus;
    }
    
    public String getTestResultDesc() {
        return testResultDesc;
    }
    
    public void setTestResultDesc(String testResultDesc) {
        this.testResultDesc = testResultDesc;
    }
    
    public String getReplacementSnInTw() {
        return replacementSnInTw;
    }
    
    public void setReplacementSnInTw(String replacementSnInTw) {
        this.replacementSnInTw = replacementSnInTw;
    }
    
    public String getReplacementPnInTw() {
        return replacementPnInTw;
    }
    
    public void setReplacementPnInTw(String replacementPnInTw) {
        this.replacementPnInTw = replacementPnInTw;
    }
    
    public String getReplacementSkuInTw() {
        return replacementSkuInTw;
    }
    
    public void setReplacementSkuInTw(String replacementSkuInTw) {
        this.replacementSkuInTw = replacementSkuInTw;
    }
    
    public String getReplacementSnFromHk() {
        return replacementSnFromHk;
    }
    
    public void setReplacementSnFromHk(String replacementSnFromHk) {
        this.replacementSnFromHk = replacementSnFromHk;
    }
    
    public String getReplacementPnFromHk() {
        return replacementPnFromHk;
    }
    
    public void setReplacementPnFromHk(String replacementPnFromHk) {
        this.replacementPnFromHk = replacementPnFromHk;
    }
    
    public String getReplacementSkuFromHk() {
        return replacementSkuFromHk;
    }
    
    public void setReplacementSkuFromHk(String replacementSkuFromHk) {
        this.replacementSkuFromHk = replacementSkuFromHk;
    }
    
    public String getRmaBoardTestResult() {
        return rmaBoardTestResult;
    }
    
    public void setRmaBoardTestResult(String rmaBoardTestResult) {
        this.rmaBoardTestResult = rmaBoardTestResult;
    }
    
    public String getEndUserInvoiceDate() {
        return endUserInvoiceDate;
    }
    
    public void setEndUserInvoiceDate(String endUserInvoiceDate) {
        this.endUserInvoiceDate = endUserInvoiceDate;
    }
    
    public String getWarrantyUntil() {
        return warrantyUntil;
    }
    
    public void setWarrantyUntil(String warrantyUntil) {
        this.warrantyUntil = warrantyUntil;
    }
    
    public String getRemark() {
        return remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
    
    @Override
    public String toString() {
        return "RmaCreateRequest{" +
                "productType='" + productType + '\'' +
                ", serialNo='" + serialNo + '\'' +
                ", rmaNo='" + rmaNo + '\'' +
                ", customerName='" + customerName + '\'' +
                ", pn='" + pn + '\'' +
                ", sku='" + sku + '\'' +
                '}';
    }
}