package com.sapphire.rma.service;

import com.sapphire.rma.dto.*;
import com.sapphire.rma.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 庫存業務邏輯服務
 * 處理庫存相關的業務邏輯
 */
@Service
@Transactional
public class StockService {
    
    @Autowired
    private StockRepository stockRepository;
    
    @Autowired
    private ProductLineService productLineService;
    
    /**
     * 取得所有庫存記錄（按產品線）
     */
    @Transactional(readOnly = true)
    public StockSearchResponse getAllStockRecords(String productType) {
        try {
            // 驗證產品線
            if (!productLineService.isValidProductLine(productType)) {
                return StockSearchResponse.error("無效的產品線: " + productType);
            }
            
            List<Map<String, Object>> stockRecords = stockRepository.findAllByProductType(productType);
            
            String message = String.format("找到 %d 筆 %s 產品線的庫存記錄", 
                                         stockRecords.size(), productType);
            
            return StockSearchResponse.success(message, stockRecords, productType);
            
        } catch (Exception e) {
            return StockSearchResponse.error("查詢庫存失敗: " + e.getMessage());
        }
    }
    
    /**
     * 搜尋庫存記錄
     */
    @Transactional(readOnly = true)
    public StockSearchResponse searchStockRecords(StockSearchRequest request) {
        try {
            // 驗證產品線
            if (!productLineService.isValidProductLine(request.getProductType())) {
                return StockSearchResponse.error("無效的產品線: " + request.getProductType());
            }
            
            // 準備搜尋參數
            Map<String, Object> searchParams = new HashMap<>();
            if (request.getSerialNo() != null) {
                searchParams.put("serialNo", request.getSerialNo());
            }
            if (request.getPn() != null) {
                searchParams.put("pn", request.getPn());
            }
            if (request.getSku() != null) {
                searchParams.put("sku", request.getSku());
            }
            if (request.getProductName() != null) {
                searchParams.put("productName", request.getProductName());
            }
            
            List<Map<String, Object>> stockRecords = stockRepository.findBySearchCriteria(
                request.getProductType(), searchParams);
            
            String message = String.format("找到 %d 筆符合條件的 %s 產品線庫存記錄", 
                                         stockRecords.size(), request.getProductType());
            
            return StockSearchResponse.success(message, stockRecords, request.getProductType());
            
        } catch (Exception e) {
            return StockSearchResponse.error("搜尋庫存失敗: " + e.getMessage());
        }
    }
    
    /**
     * 根據序列號取得庫存記錄詳細資料
     */
    @Transactional(readOnly = true)
    public StockDetailResponse getStockBySerialNo(String productType, String serialNo) {
        try {
            // 驗證產品線
            if (!productLineService.isValidProductLine(productType)) {
                return StockDetailResponse.error("無效的產品線: " + productType);
            }
            
            Optional<Map<String, Object>> stockRecordOpt = stockRepository.findBySerialNo(productType, serialNo);
            
            if (stockRecordOpt.isPresent()) {
                return StockDetailResponse.success("找到庫存記錄", stockRecordOpt.get(), productType);
            } else {
                return StockDetailResponse.error("未找到序列號 " + serialNo + " 的庫存記錄");
            }
            
        } catch (Exception e) {
            return StockDetailResponse.error("查詢庫存詳細資料失敗: " + e.getMessage());
        }
    }
    
    /**
     * 新增庫存記錄
     */
    public StockOperationResponse createStockRecord(StockCreateRequest request) {
        try {
            // 驗證產品線
            if (!productLineService.isValidProductLine(request.getProductType())) {
                return StockOperationResponse.createError("無效的產品線: " + request.getProductType());
            }
            
            // 檢查序列號是否已存在
            if (stockRepository.existsBySerialNo(request.getProductType(), request.getSerialNo())) {
                return StockOperationResponse.createError("序列號 " + request.getSerialNo() + " 已存在於 " + request.getProductType() + " 產品線庫存");
            }
            
            // 準備庫存資料 Map
            Map<String, Object> stockData = new HashMap<>();
            stockData.put("Serial_No", request.getSerialNo()); // 必填
            if (request.getProductName() != null) stockData.put("Prodcut_name", request.getProductName()); // 注意拼字
            if (request.getPn() != null) stockData.put("PN", request.getPn());
            if (request.getSku() != null) stockData.put("SKU", request.getSku());
            
            // 新增庫存記錄 (使用你的 Repository 方法)
            int result = stockRepository.insertStockRecord(request.getProductType(), stockData);
            
            if (result > 0) {
                return StockOperationResponse.createSuccess(request.getProductType(), request.getSerialNo());
            } else {
                return StockOperationResponse.createError("新增失敗，資料庫操作無效果");
            }
            
        } catch (Exception e) {
            return StockOperationResponse.createError("新增庫存失敗: " + e.getMessage());
        }
    }
    
    /**
     * 更新庫存記錄
     */
    public StockOperationResponse updateStockRecord(StockUpdateRequest request) {
        try {
            // 驗證產品線
            if (!productLineService.isValidProductLine(request.getProductType())) {
                return StockOperationResponse.updateError("無效的產品線: " + request.getProductType());
            }
            
            // 檢查庫存記錄是否存在
            if (!stockRepository.existsBySerialNo(request.getProductType(), request.getSerialNo())) {
                return StockOperationResponse.updateError("序列號 " + request.getSerialNo() + " 在 " + request.getProductType() + " 產品線庫存中不存在");
            }
            
            // 準備更新資料
            Map<String, Object> updateData = new HashMap<>();
            if (request.getProductName() != null) updateData.put("Prodcut_name", request.getProductName()); // 注意拼字
            if (request.getPn() != null) updateData.put("PN", request.getPn());
            if (request.getSku() != null) updateData.put("SKU", request.getSku());
            
            // 更新庫存記錄
            int result = stockRepository.updateStockRecord(request.getProductType(), request.getSerialNo(), updateData);
            
            if (result > 0) {
                return StockOperationResponse.updateSuccess(request.getProductType(), request.getSerialNo());
            } else {
                return StockOperationResponse.updateError("更新失敗，資料庫操作無效果");
            }
            
        } catch (Exception e) {
            return StockOperationResponse.updateError("更新庫存失敗: " + e.getMessage());
        }
    }
    
    /**
     * 刪除庫存記錄
     */
    public StockOperationResponse deleteStockRecord(String productType, String serialNo) {
        try {
            // 驗證產品線
            if (!productLineService.isValidProductLine(productType)) {
                return StockOperationResponse.deleteError("無效的產品線: " + productType);
            }
            
            // 檢查庫存記錄是否存在
            if (!stockRepository.existsBySerialNo(productType, serialNo)) {
                return StockOperationResponse.deleteError("序列號 " + serialNo + " 在 " + productType + " 產品線庫存中不存在");
            }
            
            // 執行刪除
            int result = stockRepository.deleteBySerialNo(productType, serialNo);
            
            if (result > 0) {
                return StockOperationResponse.deleteSuccess(productType, serialNo);
            } else {
                return StockOperationResponse.deleteError("刪除失敗，資料庫操作無效果");
            }
            
        } catch (Exception e) {
            return StockOperationResponse.deleteError("刪除庫存失敗: " + e.getMessage());
        }
    }
    
    /**
     * 檢查庫存記錄是否存在
     */
    @Transactional(readOnly = true)
    public boolean existsBySerialNo(String productType, String serialNo) {
        try {
            return productLineService.isValidProductLine(productType) && 
                   stockRepository.existsBySerialNo(productType, serialNo);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 取得庫存統計
     */
    @Transactional(readOnly = true)
    public StockStatsResponse getStockStats(String productType) {
        try {
            // 驗證產品線
            if (!productLineService.isValidProductLine(productType)) {
                return StockStatsResponse.error("無效的產品線: " + productType);
            }
            
            int totalCount = stockRepository.countByProductType(productType);
            List<Map<String, Object>> productStats = stockRepository.getStockStatisticsByProduct(productType);
            
            // 組合統計資料
            Map<String, Object> stats = new HashMap<>();
            stats.put("productType", productType);
            stats.put("totalCount", totalCount);
            stats.put("productStats", productStats);
            stats.put("lastUpdated", System.currentTimeMillis());
            
            return StockStatsResponse.success("庫存統計取得成功", stats);
            
        } catch (Exception e) {
            return StockStatsResponse.error("取得庫存統計失敗: " + e.getMessage());
        }
    }
    
    /**
     * 批次刪除庫存記錄
     */
    public BatchOperationResponse batchDeleteStockRecords(String productType, List<String> serialNos) {
        try {
            // 驗證產品線
            if (!productLineService.isValidProductLine(productType)) {
                return BatchOperationResponse.error("無效的產品線: " + productType);
            }
            
            if (serialNos == null || serialNos.isEmpty()) {
                return BatchOperationResponse.error("未提供要刪除的序列號");
            }
            
            int successCount = 0;
            int failCount = 0;
            
            for (String serialNo : serialNos) {
                try {
                    if (stockRepository.existsBySerialNo(productType, serialNo)) {
                        int result = stockRepository.deleteBySerialNo(productType, serialNo);
                        if (result > 0) {
                            successCount++;
                        } else {
                            failCount++;
                        }
                    } else {
                        failCount++;
                    }
                } catch (Exception e) {
                    failCount++;
                }
            }
            
            String message = String.format("批次刪除完成：成功 %d 筆，失敗 %d 筆", successCount, failCount);
            
            Map<String, Object> result = new HashMap<>();
            result.put("totalRequested", serialNos.size());
            result.put("successCount", successCount);
            result.put("failCount", failCount);
            result.put("productType", productType);
            
            return BatchOperationResponse.success(message, result);
            
        } catch (Exception e) {
            return BatchOperationResponse.error("批次刪除失敗: " + e.getMessage());
        }
    }
    
    /**
     * 關鍵字搜尋庫存 (支援掃碼功能)
     */
    @Transactional(readOnly = true)
    public StockSearchResponse searchStockByKeyword(String productType, String keyword) {
        try {
            // 驗證產品線
            if (!productLineService.isValidProductLine(productType)) {
                return StockSearchResponse.error("無效的產品線: " + productType);
            }
            
            if (keyword == null || keyword.trim().isEmpty()) {
                return StockSearchResponse.error("搜尋關鍵字不能為空");
            }
            
            List<Map<String, Object>> stockRecords = stockRepository.findByKeyword(productType, keyword.trim());
            
            String message = String.format("關鍵字 '%s' 找到 %d 筆 %s 產品線的庫存記錄", 
                                         keyword, stockRecords.size(), productType);
            
            return StockSearchResponse.success(message, stockRecords, productType);
            
        } catch (Exception e) {
            return StockSearchResponse.error("關鍵字搜尋失敗: " + e.getMessage());
        }
    }
    
    /**
     * 根據 P/N 搜尋庫存
     */
    @Transactional(readOnly = true)
    public StockSearchResponse searchStockByPN(String productType, String pn) {
        try {
            // 驗證產品線
            if (!productLineService.isValidProductLine(productType)) {
                return StockSearchResponse.error("無效的產品線: " + productType);
            }
            
            if (pn == null || pn.trim().isEmpty()) {
                return StockSearchResponse.error("P/N 不能為空");
            }
            
            List<Map<String, Object>> stockRecords = stockRepository.findByPN(productType, pn.trim());
            
            String message = String.format("P/N '%s' 找到 %d 筆 %s 產品線的庫存記錄", 
                                         pn, stockRecords.size(), productType);
            
            return StockSearchResponse.success(message, stockRecords, productType);
            
        } catch (Exception e) {
            return StockSearchResponse.error("P/N 搜尋失敗: " + e.getMessage());
        }
    }
    
    /**
     * 根據 SKU 搜尋庫存
     */
    @Transactional(readOnly = true)
    public StockSearchResponse searchStockBySKU(String productType, String sku) {
        try {
            // 驗證產品線
            if (!productLineService.isValidProductLine(productType)) {
                return StockSearchResponse.error("無效的產品線: " + productType);
            }
            
            if (sku == null || sku.trim().isEmpty()) {
                return StockSearchResponse.error("SKU 不能為空");
            }
            
            List<Map<String, Object>> stockRecords = stockRepository.findBySKU(productType, sku.trim());
            
            String message = String.format("SKU '%s' 找到 %d 筆 %s 產品線的庫存記錄", 
                                         sku, stockRecords.size(), productType);
            
            return StockSearchResponse.success(message, stockRecords, productType);
            
        } catch (Exception e) {
            return StockSearchResponse.error("SKU 搜尋失敗: " + e.getMessage());
        }
    }
}