package com.sapphire.rma.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 統一的 RMA Repository
 * 使用動態表名，前端控制產品線為必填
 */
@Repository
public class RmaRepository {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    /**
     * 根據產品線獲取對應的表名
     */
    private String getTableName(String productType) {
        return productType + "_RMA_record";
    }
    
    /**
     * 動態查詢 RMA 記錄
     * @param productType 產品線 (必填，前端控制)
     * @param searchParams 查詢參數 Map
     * @return RMA 記錄列表
     */
    public List<Map<String, Object>> findByDynamicConditions(String productType, Map<String, Object> searchParams) {
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
        
        if (searchParams.get("rmaNo") != null && !searchParams.get("rmaNo").toString().trim().isEmpty()) {
            sql.append(" AND Rma_No = ?");
            params.add(searchParams.get("rmaNo").toString().trim());
        }
        
        if (searchParams.get("customerName") != null && !searchParams.get("customerName").toString().trim().isEmpty()) {
            sql.append(" AND Customer_Name LIKE ?");
            params.add("%" + searchParams.get("customerName").toString().trim() + "%");
        }
        
        if (searchParams.get("productName") != null && !searchParams.get("productName").toString().trim().isEmpty()) {
            sql.append(" AND Product_Name LIKE ?");
            params.add("%" + searchParams.get("productName").toString().trim() + "%");
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
        
        // 排序
        sql.append(" ORDER BY Create_Date DESC");
        
        return jdbcTemplate.queryForList(sql.toString(), params.toArray());
    }
    
    /**
     * 根據序列號精確查詢單筆記錄
     */
    public Optional<Map<String, Object>> findBySerialNo(String productType, String serialNo) {
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
     * 查詢指定產品線的所有記錄
     */
    public List<Map<String, Object>> findAllByProductType(String productType) {
        String tableName = getTableName(productType);
        String sql = "SELECT * FROM " + tableName + " ORDER BY Create_Date DESC";
        return jdbcTemplate.queryForList(sql);
    }
    
    /**
     * 檢查序列號是否存在
     */
    public boolean existsBySerialNo(String productType, String serialNo) {
        String tableName = getTableName(productType);
        String sql = "SELECT COUNT(*) FROM " + tableName + " WHERE Serial_No = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, serialNo);
        return count != null && count > 0;
    }
    
    /**
     * 檢查 RMA No 是否存在
     */
    public boolean existsByRmaNo(String productType, String rmaNo) {
        String tableName = getTableName(productType);
        String sql = "SELECT COUNT(*) FROM " + tableName + " WHERE Rma_No = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, rmaNo);
        return count != null && count > 0;
    }
    
    /**
     * 新增 RMA 記錄
     */
    public int insertRmaRecord(String productType, Map<String, Object> rmaData) {
        String tableName = getTableName(productType);
        
        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();
        List<Object> params = new ArrayList<>();
        
        for (Map.Entry<String, Object> entry : rmaData.entrySet()) {
            if (entry.getValue() != null) {
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
     * 更新 RMA 記錄
     */
    public int updateRmaRecord(String productType, String serialNo, Map<String, Object> updateData) {
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
    
    /**
     * 更新 TW 替換資料 (用於資料調整頁面)
     */
    public int updateTwReplacementData(String productType, String serialNo, 
                                      String replacementSn, String replacementPn, String replacementSku) {
        String tableName = getTableName(productType);
        String sql = "UPDATE " + tableName + " SET " +
                    "Replacement_SN_in_TW = ?, " +
                    "Replacement_PN_in_TW = ?, " +
                    "Replacement_SKU_in_TW = ? " +
                    "WHERE Serial_No = ?";
        
        return jdbcTemplate.update(sql, replacementSn, replacementPn, replacementSku, serialNo);
    }
    
    /**
     * 查詢待處理的 RMA (沒有 TW 替換資料)
     */
    public List<Map<String, Object>> findPendingReplacement(String productType) {
        String tableName = getTableName(productType);
        String sql = "SELECT * FROM " + tableName + " WHERE " +
                    "(Replacement_SN_in_TW IS NULL OR Replacement_SN_in_TW = '') " +
                    "ORDER BY Create_Date DESC";
        return jdbcTemplate.queryForList(sql);
    }
    
    /**
     * 查詢已完成替換的 RMA
     */
    public List<Map<String, Object>> findCompletedReplacement(String productType) {
        String tableName = getTableName(productType);
        String sql = "SELECT * FROM " + tableName + " WHERE " +
                    "Replacement_SN_in_TW IS NOT NULL AND Replacement_SN_in_TW != '' " +
                    "ORDER BY Create_Date DESC";
        return jdbcTemplate.queryForList(sql);
    }
    
    /**
     * 彈性查詢 - 支援模糊查詢 (掃碼功能)
     */
    public List<Map<String, Object>> findByKeyword(String productType, String keyword) {
        String tableName = getTableName(productType);
        String sql = "SELECT * FROM " + tableName + " WHERE " +
                    "Serial_No LIKE ? OR PN LIKE ? OR SKU LIKE ? " +
                    "ORDER BY Create_Date DESC";
        String likeKeyword = "%" + keyword + "%";
        return jdbcTemplate.queryForList(sql, likeKeyword, likeKeyword, likeKeyword);
    }
    
    /**
     * 刪除 RMA 記錄
     */
    public int deleteBySerialNo(String productType, String serialNo) {
        String tableName = getTableName(productType);
        String sql = "DELETE FROM " + tableName + " WHERE Serial_No = ?";
        return jdbcTemplate.update(sql, serialNo);
    }
}