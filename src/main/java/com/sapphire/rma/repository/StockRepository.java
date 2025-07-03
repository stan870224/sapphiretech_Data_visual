package com.sapphire.rma.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Stock Repository - 專門處理所有產品線的庫存記錄
 */
@Repository
public class StockRepository {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    /**
     * 根據產品線獲取庫存表名
     */
    private String getTableName(String productType) {
        return productType + "_buffer_stock";
    }
    
    // ==================== 資料調整頁面 - 庫存查詢 ====================
    
    /**
     * 查詢指定產品線的所有庫存 (資料調整頁面使用)
     * 直接撈出該產品線的所有庫存資料，不帶任何條件
     * @param productType 產品線 (必填)
     */
    public List<Map<String, Object>> findAllByProductType(String productType) {
        if (productType == null || productType.trim().isEmpty()) {
            throw new IllegalArgumentException("產品線為必填項");
        }
        
        String tableName = getTableName(productType);
        String sql = "SELECT * FROM " + tableName + " ORDER BY Serial_No";
        return jdbcTemplate.queryForList(sql);
    }
    
    /**
     * 根據序列號查詢庫存記錄 (用於前端顯示庫存詳細資料)
     * @param productType 產品線 (必填)
     */
    public Optional<Map<String, Object>> findBySerialNo(String productType, String serialNo) {
        if (productType == null || productType.trim().isEmpty()) {
            throw new IllegalArgumentException("產品線為必填項");
        }
        
        String tableName = getTableName(productType);
        String sql = "SELECT * FROM " + tableName + " WHERE Serial_No = ?";
        
        try {
            Map<String, Object> result = jdbcTemplate.queryForMap(sql, serialNo);
            return Optional.of(result);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    /**
     * 動態查詢庫存記錄 (支援多條件查詢，但資料調整頁面不會使用)
     * 這個方法主要用於其他可能的查詢需求
     * @param productType 產品線 (必填)
     */
    public List<Map<String, Object>> findBySearchCriteria(String productType, Map<String, Object> searchParams) {
        if (productType == null || productType.trim().isEmpty()) {
            throw new IllegalArgumentException("產品線為必填項");
        }
        
        String tableName = getTableName(productType);
        
        StringBuilder sql = new StringBuilder("SELECT * FROM " + tableName + " WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        // 動態添加查詢條件
        if (searchParams.get("serialNo") != null && !searchParams.get("serialNo").toString().trim().isEmpty()) {
            sql.append(" AND Serial_No = ?");
            params.add(searchParams.get("serialNo").toString().trim());
        }
        
        if (searchParams.get("pn") != null && !searchParams.get("pn").toString().trim().isEmpty()) {
            sql.append(" AND PN = ?");
            params.add(searchParams.get("pn").toString().trim());
        }
        
        if (searchParams.get("sku") != null && !searchParams.get("sku").toString().trim().isEmpty()) {
            sql.append(" AND SKU = ?");
            params.add(searchParams.get("sku").toString().trim());
        }
        
        if (searchParams.get("productName") != null && !searchParams.get("productName").toString().trim().isEmpty()) {
            sql.append(" AND Prodcut_name LIKE ?");
            params.add("%" + searchParams.get("productName").toString().trim() + "%");
        }
        
        sql.append(" ORDER BY Serial_No");
        
        return jdbcTemplate.queryForList(sql.toString(), params.toArray());
    }
    
    // ==================== 庫存記錄管理 ====================
    
    /**
     * 檢查序列號是否存在於庫存中
     * @param productType 產品線 (必填)
     */
    public boolean existsBySerialNo(String productType, String serialNo) {
        if (productType == null || productType.trim().isEmpty()) {
            throw new IllegalArgumentException("產品線為必填項");
        }
        
        String tableName = getTableName(productType);
        String sql = "SELECT COUNT(*) FROM " + tableName + " WHERE Serial_No = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, serialNo);
        return count != null && count > 0;
    }
    
    /**
     * 新增庫存記錄
     * @param productType 產品線 (必填)
     */
    public int insertStockRecord(String productType, Map<String, Object> stockData) {
        if (productType == null || productType.trim().isEmpty()) {
            throw new IllegalArgumentException("產品線為必填項");
        }
        
        String tableName = getTableName(productType);
        
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();
        List<Object> params = new ArrayList<>();
        
        for (Map.Entry<String, Object> entry : stockData.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().toString().trim().isEmpty()) {
                if (columns.length() > 0) {
                    columns.append(", ");
                    values.append(", ");
                }
                columns.append(entry.getKey());
                values.append("?");
                params.add(entry.getValue());
            }
        }
        
        String sql = "INSERT INTO " + tableName + " (" + columns.toString() + ") VALUES (" + values.toString() + ")";
        return jdbcTemplate.update(sql, params.toArray());
    }
    
    /**
     * 刪除庫存記錄 (用於資料調整頁面的"更換"功能)
     * 當庫存被用於替換時，會從庫存表中刪除
     * @param productType 產品線 (必填)
     */
    public int deleteBySerialNo(String productType, String serialNo) {
        if (productType == null || productType.trim().isEmpty()) {
            throw new IllegalArgumentException("產品線為必填項");
        }
        
        String tableName = getTableName(productType);
        String sql = "DELETE FROM " + tableName + " WHERE Serial_No = ?";
        return jdbcTemplate.update(sql, serialNo);
    }
    
    /**
     * 更新庫存記錄 (一般情況下庫存資料不會更新，但提供此方法以備不時之需)
     * @param productType 產品線 (必填)
     */
    public int updateStockRecord(String productType, String serialNo, Map<String, Object> updateData) {
        if (productType == null || productType.trim().isEmpty()) {
            throw new IllegalArgumentException("產品線為必填項");
        }
        
        String tableName = getTableName(productType);
        
        StringBuilder setClause = new StringBuilder();
        List<Object> params = new ArrayList<>();
        
        for (Map.Entry<String, Object> entry : updateData.entrySet()) {
            if (!entry.getKey().equals("Serial_No")) { // 序列號不能更新
                if (setClause.length() > 0) {
                    setClause.append(", ");
                }
                setClause.append(entry.getKey()).append(" = ?");
                params.add(entry.getValue());
            }
        }
        
        params.add(serialNo); // WHERE 條件的參數
        
        String sql = "UPDATE " + tableName + " SET " + setClause.toString() + " WHERE Serial_No = ?";
        return jdbcTemplate.update(sql, params.toArray());
    }
    
    // ==================== 查詢功能 ====================
    
    /**
     * 彈性查詢 - 支援模糊查詢 (掃碼功能)
     * @param productType 產品線 (必填)
     */
    public List<Map<String, Object>> findByKeyword(String productType, String keyword) {
        if (productType == null || productType.trim().isEmpty()) {
            throw new IllegalArgumentException("產品線為必填項");
        }
        
        String tableName = getTableName(productType);
        String sql = "SELECT * FROM " + tableName + " WHERE " +
                    "Serial_No LIKE ? OR PN LIKE ? OR SKU LIKE ? OR Prodcut_name LIKE ? " +
                    "ORDER BY Serial_No";
        String likeKeyword = "%" + keyword + "%";
        return jdbcTemplate.queryForList(sql, likeKeyword, likeKeyword, likeKeyword, likeKeyword);
    }
    
    /**
     * 根據 PN 查詢庫存
     * @param productType 產品線 (必填)
     */
    public List<Map<String, Object>> findByPN(String productType, String pn) {
        if (productType == null || productType.trim().isEmpty()) {
            throw new IllegalArgumentException("產品線為必填項");
        }
        
        String tableName = getTableName(productType);
        String sql = "SELECT * FROM " + tableName + " WHERE PN = ? ORDER BY Serial_No";
        return jdbcTemplate.queryForList(sql, pn);
    }
    
    /**
     * 根據 SKU 查詢庫存
     * @param productType 產品線 (必填)
     */
    public List<Map<String, Object>> findBySKU(String productType, String sku) {
        if (productType == null || productType.trim().isEmpty()) {
            throw new IllegalArgumentException("產品線為必填項");
        }
        
        String tableName = getTableName(productType);
        String sql = "SELECT * FROM " + tableName + " WHERE SKU = ? ORDER BY Serial_No";
        return jdbcTemplate.queryForList(sql, sku);
    }
    
    // ==================== 統計功能 ====================
    
    /**
     * 統計指定產品線的庫存數量
     * @param productType 產品線 (必填)
     */
    public int countByProductType(String productType) {
        if (productType == null || productType.trim().isEmpty()) {
            throw new IllegalArgumentException("產品線為必填項");
        }
        
        String tableName = getTableName(productType);
        String sql = "SELECT COUNT(*) FROM " + tableName;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }
    
    /**
     * 根據產品名稱統計庫存數量
     * @param productType 產品線 (必填)
     */
    public List<Map<String, Object>> getStockStatisticsByProduct(String productType) {
        if (productType == null || productType.trim().isEmpty()) {
            throw new IllegalArgumentException("產品線為必填項");
        }
        
        String tableName = getTableName(productType);
        String sql = "SELECT Prodcut_name, COUNT(*) as stock_count FROM " + tableName + 
                    " GROUP BY Prodcut_name ORDER BY stock_count DESC";
        return jdbcTemplate.queryForList(sql);
    }
}