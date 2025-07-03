package com.sapphire.rma.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * RMA Repository - 處理 RMA 記錄的所有操作
 */
@Repository
public class RmaRepository {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    /**
     * 根據產品線獲取 RMA 表名
     */
    private String getRmaTableName(String productType) {
        return productType + "_RMA_record";
    }
    
    // ==================== 資料查詢頁面 ====================
    
    /**
     * 資料查詢頁面 - 動態查詢 RMA 記錄
     * @param productType 產品線 (必填)
     */
    public List<Map<String, Object>> findBySearchCriteria(String productType, Map<String, Object> searchParams) {
        if (productType == null || productType.trim().isEmpty()) {
            throw new IllegalArgumentException("產品線為必填項");
        }
        
        String tableName = getRmaTableName(productType);
        
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
        
        // 日期範圍查詢
        if (searchParams.get("startDate") != null) {
            sql.append(" AND Create_Date >= ?");
            params.add(searchParams.get("startDate"));
        }
        
        if (searchParams.get("endDate") != null) {
            sql.append(" AND Create_Date <= ?");
            params.add(searchParams.get("endDate"));
        }
        
        sql.append(" ORDER BY Create_Date DESC");
        
        return jdbcTemplate.queryForList(sql.toString(), params.toArray());
    }
    
    // ==================== 資料調整頁面 ====================
    
    /**
     * 資料調整頁面 - 查詢單筆 RMA 記錄 (更新模式)
     * @param productType 產品線 (必填)
     */
    public Optional<Map<String, Object>> findRmaRecordForUpdate(String productType, Map<String, Object> searchParams) {
        if (productType == null || productType.trim().isEmpty()) {
            throw new IllegalArgumentException("產品線為必填項");
        }
        
        String tableName = getRmaTableName(productType);
        
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
        
        try {
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql.toString(), params.toArray());
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    

    
    /**
     * 資料調整頁面 - 新增 RMA 記錄 (新增模式)
     * @param productType 產品線 (必填)
     */
    public int insertRmaRecord(String productType, Map<String, Object> rmaData) {
        if (productType == null || productType.trim().isEmpty()) {
            throw new IllegalArgumentException("產品線為必填項");
        }
        
        String tableName = getRmaTableName(productType);
        
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();
        List<Object> params = new ArrayList<>();
        
        for (Map.Entry<String, Object> entry : rmaData.entrySet()) {
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
     * 資料調整頁面 - 更新 RMA 記錄並刪除庫存 (更新模式點擊更新按鈕)
     * 這是核心的事務操作
     * @param productType 產品線 (必填)
     */
    @Transactional
    public boolean updateRmaRecordWithStockDeletion(String productType, String rmaSerialNo, 
                                                   Map<String, Object> rmaUpdateData, 
                                                   String stockSerialNoToDelete) {
        if (productType == null || productType.trim().isEmpty()) {
            throw new IllegalArgumentException("產品線為必填項");
        }
        
        try {
            // 1. 更新 RMA 記錄
            String rmaTableName = getRmaTableName(productType);
            StringBuilder setClause = new StringBuilder();
            List<Object> params = new ArrayList<>();
            
            for (Map.Entry<String, Object> entry : rmaUpdateData.entrySet()) {
                if (!entry.getKey().equals("Serial_No")) { // 序列號不能更新
                    if (setClause.length() > 0) {
                        setClause.append(", ");
                    }
                    setClause.append(entry.getKey()).append(" = ?");
                    params.add(entry.getValue());
                }
            }
            
            params.add(rmaSerialNo); // WHERE 條件的參數
            
            String updateRmaSql = "UPDATE " + rmaTableName + " SET " + setClause.toString() + " WHERE Serial_No = ?";
            int rmaUpdated = jdbcTemplate.update(updateRmaSql, params.toArray());
            
            // 2. 刪除庫存記錄 (如果有指定要刪除的庫存)
            if (stockSerialNoToDelete != null && !stockSerialNoToDelete.trim().isEmpty()) {
                String stockTableName = getStockTableName(productType);
                String deleteStockSql = "DELETE FROM " + stockTableName + " WHERE Serial_No = ?";
                int stockDeleted = jdbcTemplate.update(deleteStockSql, stockSerialNoToDelete);
                
                return rmaUpdated > 0 && stockDeleted > 0;
            }
            
            // 如果沒有要刪除庫存，只要 RMA 更新成功就可以
            return rmaUpdated > 0;
            
        } catch (Exception e) {
            throw new RuntimeException("更新 RMA 記錄失敗", e);
        }
    }
    
    // ==================== 共用方法 ====================
    
    /**
     * 檢查 RMA 記錄是否存在
     * @param productType 產品線 (必填)
     */
    public boolean existsBySerialNo(String productType, String serialNo) {
        if (productType == null || productType.trim().isEmpty()) {
            throw new IllegalArgumentException("產品線為必填項");
        }
        
        String tableName = getRmaTableName(productType);
        String sql = "SELECT COUNT(*) FROM " + tableName + " WHERE Serial_No = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, serialNo);
        return count != null && count > 0;
    }

    // 需要添加到 RmaRepository.java 中的方法

    /**
     * 根據產品線獲取庫存表名 (用於 updateRmaRecordWithStockDeletion 方法)
     */
    private String getStockTableName(String productType) {
        return productType + "_buffer_stock";
    }

    /**
     * 刪除 RMA 記錄
     * @param productType 產品線 (必填)
     * @param serialNo 序列號
     */
    public int deleteBySerialNo(String productType, String serialNo) {
        if (productType == null || productType.trim().isEmpty()) {
            throw new IllegalArgumentException("產品線為必填項");
        }

        String tableName = getRmaTableName(productType);
        String sql = "DELETE FROM " + tableName + " WHERE Serial_No = ?";
        return jdbcTemplate.update(sql, serialNo);
    }

    /**
     * 統計 RMA 記錄數量
     * @param productType 產品線 (必填)
     */
    public int countByProductType(String productType) {
        if (productType == null || productType.trim().isEmpty()) {
            throw new IllegalArgumentException("產品線為必填項");
        }

        String tableName = getRmaTableName(productType);
        String sql = "SELECT COUNT(*) FROM " + tableName;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
        return count != null ? count : 0;
    }

}