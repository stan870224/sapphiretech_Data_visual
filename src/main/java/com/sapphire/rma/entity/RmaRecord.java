package com.sapphire.rma.entity;

import java.time.LocalDate;

/**
 * 統一的 RMA 記錄實體類別
 * 適用於所有產品線 (VGA, MB, MiniPC)
 * 對應資料庫欄位名稱
 */
public class RmaRecord {
    
    // 產品線識別 (不存在資料庫中，用於程式邏輯)
    private String productType;
    
    // RMA 基本資訊 - 對應資料庫欄位
    private String rmaNo;                    // Rma_No
    private String customerName;             // Customer_Name
    private String serialNo;                 // Serial_No
    private String pn;                       // PN
    private String sku;                      // SKU
    private String productName;              // Product_Name
    
    // 日期資訊 - 對應資料庫欄位
    private LocalDate sellShipDate;          // Sell_Ship_Date
    private LocalDate createDate;            // Create_Date
    private LocalDate returnDate;            // Return_Date
    private LocalDate endUserInvoiceDate;    // End_user_invoice_date
    private LocalDate warrantyUntil;         // Warranty_Until
    
    // 故障和測試資訊 - 對應資料庫欄位
    private String failureDesc;              // Failure_desc
    private String viDamageStatus;           // VI_Damage_Status
    private String testResultDesc;           // Test_Result_Desc
    private String rmaboardTestResult;       // RMA_board_Test_Result
    
    // TW 替換資訊 - 對應資料庫欄位
    private String replacementSnInTw;        // Replacement_SN_in_TW
    private String replacementPnInTw;        // Replacement_PN_in_TW
    private String replacementSkuInTw;       // Replacement_SKU_in_TW
    
    // HK 替換資訊 - 對應資料庫欄位
    private String replacementSnFromHk;      // Replacement_SN_from_HK
    private String replacementPnFromHk;      // Replacement_PN_from_HK
    private String replacementSkuFromHk;     // Replacement_SKU_from_HK
    
    // 備註 - 對應資料庫欄位
    private String remark;                   // Remark
    
    // 建構子
    public RmaRecord() {}
    
    public RmaRecord(String productType) {
        this.productType = productType;
    }
    
    // Getters and Setters
    public String getProductType() {
        return productType;
    }
    
    public void setProductType(String productType) {
        this.productType = productType;
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
    
    public LocalDate getSellShipDate() {
        return sellShipDate;
    }
    
    public void setSellShipDate(LocalDate sellShipDate) {
        this.sellShipDate = sellShipDate;
    }
    
    public LocalDate getCreateDate() {
        return createDate;
    }
    
    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }
    
    public LocalDate getReturnDate() {
        return returnDate;
    }
    
    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
    
    public LocalDate getEndUserInvoiceDate() {
        return endUserInvoiceDate;
    }
    
    public void setEndUserInvoiceDate(LocalDate endUserInvoiceDate) {
        this.endUserInvoiceDate = endUserInvoiceDate;
    }
    
    public LocalDate getWarrantyUntil() {
        return warrantyUntil;
    }
    
    public void setWarrantyUntil(LocalDate warrantyUntil) {
        this.warrantyUntil = warrantyUntil;
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
    
    public String getRmaboardTestResult() {
        return rmaboardTestResult;
    }
    
    public void setRmaboardTestResult(String rmaboardTestResult) {
        this.rmaboardTestResult = rmaboardTestResult;
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
    
    public String getRemark() {
        return remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
    
    @Override
    public String toString() {
        return "RmaRecord{" +
                "productType='" + productType + '\'' +
                ", rmaNo='" + rmaNo + '\'' +
                ", customerName='" + customerName + '\'' +
                ", serialNo='" + serialNo + '\'' +
                ", pn='" + pn + '\'' +
                ", sku='" + sku + '\'' +
                ", productName='" + productName + '\'' +
                '}';
    }
}