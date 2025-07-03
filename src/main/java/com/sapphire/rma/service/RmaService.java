package com.sapphire.rma.service;

import com.sapphire.rma.dto.*;
import com.sapphire.rma.repository.RmaRepository;
import com.sapphire.rma.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * RMA 業務邏輯服務
 * 處理資料查詢和資料調整頁面的主要業務邏輯
 */
@Service
@Transactional
public class RmaService {
    
    @Autowired
    private RmaRepository rmaRepository;
    
    @Autowired
    private StockRepository stockRepository;
    
    @Autowired
    private ProductLineService productLineService;
    
    /**
     * 資料查詢頁面 - 搜尋 RMA 記錄
     */
    @Transactional(readOnly = true)
    public RmaSearchResponse searchRmaRecords(RmaSearchRequest request) {
        try {
            // 驗證產品線
            if (!productLineService.isValidProductLine(request.getProductType())) {
                return RmaSearchResponse.error("無效的產品線: " + request.getProductType());
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
            if (request.getStartDate() != null) {
                searchParams.put("startDate", request.getStartDate());
            }
            if (request.getEndDate() != null) {
                searchParams.put("endDate", request.getEndDate());
            }
            
            // 執行搜尋
            List<Map<String, Object>> records = rmaRepository.findBySearchCriteria(
                request.getProductType(), searchParams);
            
            String message = String.format("找到 %d 筆 %s 產品線的 RMA 記錄", 
                                         records.size(), request.getProductType());
            
            return RmaSearchResponse.success(message, records, request.getProductType());
            
        } catch (Exception e) {
            return RmaSearchResponse.error("搜尋失敗: " + e.getMessage());
        }
    }
    
    /**
     * 資料調整頁面 - 載入初始資料（只有庫存）
     */
    @Transactional(readOnly = true)
    public UpdatePageResponse loadUpdatePageData(String productType) {
        try {
            // 驗證產品線
            if (!productLineService.isValidProductLine(productType)) {
                return UpdatePageResponse.error("無效的產品線: " + productType);
            }
            
            // 載入庫存資料
            List<Map<String, Object>> stockRecords = stockRepository.findAllByProductType(productType);
            
            String message = String.format("載入 %s 產品線資料，共 %d 筆庫存", 
                                         productType, stockRecords.size());
            
            return UpdatePageResponse.successWithStockOnly(message, productType, stockRecords);
            
        } catch (Exception e) {
            return UpdatePageResponse.error("載入資料失敗: " + e.getMessage());
        }
    }
    
    /**
     * 資料調整頁面 - 查詢特定 RMA 記錄和庫存
     */
    @Transactional(readOnly = true)
    public UpdatePageResponse searchForUpdate(String productType, String serialNo, String pn, String sku) {
        try {
            // 驗證產品線
            if (!productLineService.isValidProductLine(productType)) {
                return UpdatePageResponse.error("無效的產品線: " + productType);
            }
            
            // 準備搜尋參數
            Map<String, Object> searchParams = new HashMap<>();
            if (serialNo != null) {
                searchParams.put("serialNo", serialNo);
            }
            if (pn != null) {
                searchParams.put("pn", pn);
            }
            if (sku != null) {
                searchParams.put("sku", sku);
            }
            
            // 查詢 RMA 記錄
            Optional<Map<String, Object>> rmaRecordOpt = rmaRepository.findRmaRecordForUpdate(productType, searchParams);
            
            // 載入庫存資料
            List<Map<String, Object>> stockRecords = stockRepository.findAllByProductType(productType);
            
            if (rmaRecordOpt.isPresent()) {
                String message = String.format("找到 RMA 記錄，載入 %d 筆庫存", stockRecords.size());
                return UpdatePageResponse.success(message, productType, rmaRecordOpt.get(), stockRecords);
            } else {
                String message = String.format("未找到符合條件的 RMA 記錄，載入 %d 筆庫存", stockRecords.size());
                return UpdatePageResponse.successWithStockOnly(message, productType, stockRecords);
            }
            
        } catch (Exception e) {
            return UpdatePageResponse.error("查詢失敗: " + e.getMessage());
        }
    }
    
    /**
     * 新增 RMA 記錄
     */
    public RmaOperationResponse createRmaRecord(RmaCreateRequest request) {
        try {
            // 驗證產品線
            if (!productLineService.isValidProductLine(request.getProductType())) {
                return RmaOperationResponse.createError("無效的產品線: " + request.getProductType());
            }
            
            // 檢查序列號是否已存在
            if (rmaRepository.existsBySerialNo(request.getProductType(), request.getSerialNo())) {
                return RmaOperationResponse.createError("序列號 " + request.getSerialNo() + " 已存在於 " + request.getProductType() + " 產品線");
            }
            
            // 準備 RMA 資料
            Map<String, Object> rmaData = new HashMap<>();
            if (request.getRmaNo() != null) rmaData.put("Rma_No", request.getRmaNo());
            if (request.getCustomerName() != null) rmaData.put("Customer_Name", request.getCustomerName());
            rmaData.put("Serial_No", request.getSerialNo()); // 必填
            if (request.getPn() != null) rmaData.put("PN", request.getPn());
            if (request.getSku() != null) rmaData.put("SKU", request.getSku());
            if (request.getProductName() != null) rmaData.put("Product_Name", request.getProductName());
            if (request.getSellShipDate() != null) rmaData.put("Sell_Ship_Date", request.getSellShipDate());
            if (request.getCreateDate() != null) rmaData.put("Create_Date", request.getCreateDate());
            if (request.getReturnDate() != null) rmaData.put("Return_Date", request.getReturnDate());
            if (request.getFailureDesc() != null) rmaData.put("Failure_desc", request.getFailureDesc());
            if (request.getViDamageStatus() != null) rmaData.put("VI_Damage_Status", request.getViDamageStatus());
            if (request.getTestResultDesc() != null) rmaData.put("Test_Result_Desc", request.getTestResultDesc());
            if (request.getReplacementSnInTw() != null) rmaData.put("Replacement_SN_in_TW", request.getReplacementSnInTw());
            if (request.getReplacementPnInTw() != null) rmaData.put("Replacement_PN_in_TW", request.getReplacementPnInTw());
            if (request.getReplacementSkuInTw() != null) rmaData.put("Replacement_SKU_in_TW", request.getReplacementSkuInTw());
            if (request.getReplacementSnFromHk() != null) rmaData.put("Replacement_SN_from_HK", request.getReplacementSnFromHk());
            if (request.getReplacementPnFromHk() != null) rmaData.put("Replacement_PN_from_HK", request.getReplacementPnFromHk());
            if (request.getReplacementSkuFromHk() != null) rmaData.put("Replacement_SKU_from_HK", request.getReplacementSkuFromHk());
            if (request.getRmaBoardTestResult() != null) rmaData.put("RMA_board_Test_Result", request.getRmaBoardTestResult());
            if (request.getEndUserInvoiceDate() != null) rmaData.put("End_user_invoice_date", request.getEndUserInvoiceDate());
            if (request.getWarrantyUntil() != null) rmaData.put("Warranty_Until", request.getWarrantyUntil());
            if (request.getRemark() != null) rmaData.put("Remark", request.getRemark());
            
            // 建立 RMA 記錄
            int result = rmaRepository.insertRmaRecord(request.getProductType(), rmaData);
            
            if (result > 0) {
                return RmaOperationResponse.createSuccess(request.getProductType(), request.getSerialNo());
            } else {
                return RmaOperationResponse.createError("新增失敗，資料庫操作無效果");
            }
            
        } catch (Exception e) {
            return RmaOperationResponse.createError("新增失敗: " + e.getMessage());
        }
    }
    
    /**
     * 更新 RMA 記錄（可能包含刪除庫存）
     */
    public RmaOperationResponse updateRmaRecord(RmaUpdateWithStockRequest request) {
        try {
            // 驗證產品線
            if (!productLineService.isValidProductLine(request.getProductType())) {
                return RmaOperationResponse.updateError("無效的產品線: " + request.getProductType());
            }
            
            // 檢查 RMA 記錄是否存在
            if (!rmaRepository.existsBySerialNo(request.getProductType(), request.getSerialNo())) {
                return RmaOperationResponse.updateError("序列號 " + request.getSerialNo() + " 在 " + request.getProductType() + " 產品線中不存在");
            }
            
            // 準備更新資料
            Map<String, Object> updateData = new HashMap<>();
            if (request.getRmaNo() != null) updateData.put("Rma_No", request.getRmaNo());
            if (request.getCustomerName() != null) updateData.put("Customer_Name", request.getCustomerName());
            if (request.getPn() != null) updateData.put("PN", request.getPn());
            if (request.getSku() != null) updateData.put("SKU", request.getSku());
            if (request.getProductName() != null) updateData.put("Product_Name", request.getProductName());
            if (request.getSellShipDate() != null) updateData.put("Sell_Ship_Date", request.getSellShipDate());
            if (request.getCreateDate() != null) updateData.put("Create_Date", request.getCreateDate());
            if (request.getReturnDate() != null) updateData.put("Return_Date", request.getReturnDate());
            if (request.getFailureDesc() != null) updateData.put("Failure_desc", request.getFailureDesc());
            if (request.getViDamageStatus() != null) updateData.put("VI_Damage_Status", request.getViDamageStatus());
            if (request.getTestResultDesc() != null) updateData.put("Test_Result_Desc", request.getTestResultDesc());
            if (request.getReplacementSnInTw() != null) updateData.put("Replacement_SN_in_TW", request.getReplacementSnInTw());
            if (request.getReplacementPnInTw() != null) updateData.put("Replacement_PN_in_TW", request.getReplacementPnInTw());
            if (request.getReplacementSkuInTw() != null) updateData.put("Replacement_SKU_in_TW", request.getReplacementSkuInTw());
            if (request.getReplacementSnFromHk() != null) updateData.put("Replacement_SN_from_HK", request.getReplacementSnFromHk());
            if (request.getReplacementPnFromHk() != null) updateData.put("Replacement_PN_from_HK", request.getReplacementPnFromHk());
            if (request.getReplacementSkuFromHk() != null) updateData.put("Replacement_SKU_from_HK", request.getReplacementSkuFromHk());
            if (request.getRmaBoardTestResult() != null) updateData.put("RMA_board_Test_Result", request.getRmaBoardTestResult());
            if (request.getEndUserInvoiceDate() != null) updateData.put("End_user_invoice_date", request.getEndUserInvoiceDate());
            if (request.getWarrantyUntil() != null) updateData.put("Warranty_Until", request.getWarrantyUntil());
            if (request.getRemark() != null) updateData.put("Remark", request.getRemark());
            
            // 執行更新（包含庫存刪除）
            boolean result = rmaRepository.updateRmaRecordWithStockDeletion(
                request.getProductType(),
                request.getSerialNo(),
                updateData,
                request.getStockSerialNoToDelete()
            );
            
            if (result) {
                if (request.shouldDeleteStock()) {
                    return RmaOperationResponse.updateWithStockDeleteSuccess(
                        request.getProductType(), 
                        request.getSerialNo(), 
                        request.getStockSerialNoToDelete()
                    );
                } else {
                    return RmaOperationResponse.updateSuccess(request.getProductType(), request.getSerialNo());
                }
            } else {
                return RmaOperationResponse.updateError("更新失敗，資料庫操作無效果");
            }
            
        } catch (Exception e) {
            return RmaOperationResponse.updateError("更新失敗: " + e.getMessage());
        }
    }
    
    /**
     * 刪除 RMA 記錄
     */
    public RmaOperationResponse deleteRmaRecord(String productType, String serialNo) {
        try {
            // 驗證產品線
            if (!productLineService.isValidProductLine(productType)) {
                return RmaOperationResponse.deleteError("無效的產品線: " + productType);
            }
            
            // 檢查記錄是否存在
            if (!rmaRepository.existsBySerialNo(productType, serialNo)) {
                return RmaOperationResponse.deleteError("序列號 " + serialNo + " 在 " + productType + " 產品線中不存在");
            }
            
            // 執行刪除
            int result = rmaRepository.deleteBySerialNo(productType, serialNo);
            
            if (result > 0) {
                return RmaOperationResponse.deleteSuccess(productType, serialNo);
            } else {
                return RmaOperationResponse.deleteError("刪除失敗，資料庫操作無效果");
            }
            
        } catch (Exception e) {
            return RmaOperationResponse.deleteError("刪除失敗: " + e.getMessage());
        }
    }
    
    /**
     * 檢查 RMA 記錄是否存在
     */
    @Transactional(readOnly = true)
    public boolean existsBySerialNo(String productType, String serialNo) {
        try {
            return productLineService.isValidProductLine(productType) && 
                   rmaRepository.existsBySerialNo(productType, serialNo);
        } catch (Exception e) {
            return false;
        }
    }
}