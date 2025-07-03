package com.sapphire.rma.entity;

/**
 * 統一的庫存記錄實體類別
 * 適用於所有產品線 (VGA, MB, MiniPC)
 * 對應資料庫欄位名稱
 */
public class StockRecord {
    
    // 產品線識別 (不存在資料庫中，用於程式邏輯)
    private String productType;
    
    // 庫存基本資訊 - 對應資料庫欄位
    private String prodcutName;              // Prodcut_name (注意：資料庫中拼字是錯的)
    private String pn;                       // PN
    private String sku;                      // SKU
    private String serialNo;                 // Serial_No
    
    // 建構子
    public StockRecord() {}
    
    public StockRecord(String productType) {
        this.productType = productType;
    }
    
    // Getters and Setters
    public String getProductType() {
        return productType;
    }
    
    public void setProductType(String productType) {
        this.productType = productType;
    }
    
    public String getProdcutName() {
        return prodcutName;
    }
    
    public void setProdcutName(String prodcutName) {
        this.prodcutName = prodcutName;
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
    
    public String getSerialNo() {
        return serialNo;
    }
    
    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }
    
    @Override
    public String toString() {
        return "StockRecord{" +
                "productType='" + productType + '\'' +
                ", prodcutName='" + prodcutName + '\'' +
                ", pn='" + pn + '\'' +
                ", sku='" + sku + '\'' +
                ", serialNo='" + serialNo + '\'' +
                '}';
    }
}