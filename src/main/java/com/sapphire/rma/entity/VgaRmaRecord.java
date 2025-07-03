package com.sapphire.rma.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "VGA_RMA_record")
public class VgaRmaRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "Rma_No")
    private String rmaNo;
    
    @Column(name = "Customer_Name")
    private String customerName;
    
    @Column(name = "Serial_No", unique = true, nullable = false)
    private String serialNo;
    
    @Column(name = "PN")
    private String partNumber;
    
    @Column(name = "SKU")
    private String sku;
    
    @Column(name = "Product_Name")
    private String productName;
    
    @Column(name = "Sell_Ship_Date")
    private String sellShipDate;
    
    @Column(name = "Create_Date")
    private String createDate;
    
    @Column(name = "Return_Date")
    private String returnDate;
    
    @Column(name = "Failure_desc", length = 500)
    private String failureDescription;
    
    @Column(name = "VI_Damage_Status")
    private String viDamageStatus;
    
    @Column(name = "Test_Result_Desc", length = 500)
    private String testResultDescription;
    
    @Column(name = "Replacement_SN_in_TW")
    private String replacementSnInTw;
    
    @Column(name = "Replacement_PN_in_TW")
    private String replacementPnInTw;
    
    @Column(name = "Replacement_SKU_in_TW")
    private String replacementSkuInTw;
    
    @Column(name = "Replacement_SN_from_HK")
    private String replacementSnFromHk;
    
    @Column(name = "Replacement_PN_from_HK")
    private String replacementPnFromHk;
    
    @Column(name = "Replacement_SKU_from_HK")
    private String replacementSkuFromHk;
    
    @Column(name = "RMA_board_Test_Result", length = 500)
    private String rmaBoardTestResult;
    
    @Column(name = "End_user_invoice_date")
    private String endUserInvoiceDate;
    
    @Column(name = "Warranty_Until")
    private String warrantyUntil;
    
    @Column(name = "Remark", length = 1000)
    private String remark;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public VgaRmaRecord() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
    
    public String getPartNumber() {
        return partNumber;
    }
    
    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
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
    
    public String getFailureDescription() {
        return failureDescription;
    }
    
    public void setFailureDescription(String failureDescription) {
        this.failureDescription = failureDescription;
    }
    
    public String getViDamageStatus() {
        return viDamageStatus;
    }
    
    public void setViDamageStatus(String viDamageStatus) {
        this.viDamageStatus = viDamageStatus;
    }
    
    public String getTestResultDescription() {
        return testResultDescription;
    }
    
    public void setTestResultDescription(String testResultDescription) {
        this.testResultDescription = testResultDescription;
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
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "VgaRmaRecord{" +
                "id=" + id +
                ", rmaNo='" + rmaNo + '\'' +
                ", customerName='" + customerName + '\'' +
                ", serialNo='" + serialNo + '\'' +
                ", partNumber='" + partNumber + '\'' +
                ", sku='" + sku + '\'' +
                ", productName='" + productName + '\'' +
                '}';
    }
}