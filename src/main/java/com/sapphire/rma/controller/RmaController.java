package com.sapphire.rma.controller;

import com.sapphire.rma.dto.*;
import com.sapphire.rma.service.RmaService;
import com.sapphire.rma.service.ProductLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * RMA 控制器
 * 提供 RMA 相關的 REST API 端點
 */
@RestController
@RequestMapping("/api/rma")
@CrossOrigin(origins = "*")
public class RmaController {
    
    @Autowired
    private RmaService rmaService;
    
    @Autowired
    private ProductLineService productLineService;
    
    // ==================== 資料查詢頁面 API ====================
    
    /**
     * 搜尋 RMA 記錄 (資料查詢頁面使用)
     * POST /api/rma/search
     */
    @PostMapping("/search")
    public ResponseEntity<RmaSearchResponse> searchRmaRecords(@Valid @RequestBody RmaSearchRequest request) {
        try {
            RmaSearchResponse response = rmaService.searchRmaRecords(request);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            RmaSearchResponse errorResponse = RmaSearchResponse.error("搜尋 RMA 記錄時發生錯誤: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    // ==================== 資料調整頁面 API ====================
    
    /**
     * 載入資料調整頁面初始資料 (庫存資料)
     * GET /api/rma/update-page/{productType}
     */
    @GetMapping("/update-page/{productType}")
    public ResponseEntity<UpdatePageResponse> loadUpdatePageData(@PathVariable String productType) {
        try {
            UpdatePageResponse response = rmaService.loadUpdatePageData(productType);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            UpdatePageResponse errorResponse = UpdatePageResponse.error("載入頁面資料時發生錯誤: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * 搜尋特定 RMA 記錄用於更新 (資料調整頁面使用)
     * GET /api/rma/search-for-update
     */
    @GetMapping("/search-for-update")
    public ResponseEntity<UpdatePageResponse> searchForUpdate(
            @RequestParam String productType,
            @RequestParam(required = false) String serialNo,
            @RequestParam(required = false) String pn,
            @RequestParam(required = false) String sku) {
        try {
            UpdatePageResponse response = rmaService.searchForUpdate(productType, serialNo, pn, sku);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            UpdatePageResponse errorResponse = UpdatePageResponse.error("搜尋 RMA 記錄時發生錯誤: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    // ==================== RMA 記錄 CRUD 操作 ====================
    
    /**
     * 新增 RMA 記錄 (資料調整頁面 - 新增按鈕)
     * POST /api/rma/create
     */
    @PostMapping("/create")
    public ResponseEntity<RmaOperationResponse> createRmaRecord(@Valid @RequestBody RmaCreateRequest request) {
        try {
            RmaOperationResponse response = rmaService.createRmaRecord(request);
            
            if (response.isSuccess()) {
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            RmaOperationResponse errorResponse = RmaOperationResponse.createError("新增 RMA 記錄時發生錯誤: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * 更新 RMA 記錄 (資料調整頁面 - 更新按鈕)
     * 支援同時刪除庫存的操作
     * PUT /api/rma/update
     */
    @PutMapping("/update")
    public ResponseEntity<RmaOperationResponse> updateRmaRecord(@Valid @RequestBody RmaUpdateWithStockRequest request) {
        try {
            RmaOperationResponse response = rmaService.updateRmaRecord(request);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            RmaOperationResponse errorResponse = RmaOperationResponse.updateError("更新 RMA 記錄時發生錯誤: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * 刪除 RMA 記錄
     * DELETE /api/rma/{productType}/{serialNo}
     */
    @DeleteMapping("/{productType}/{serialNo}")
    public ResponseEntity<RmaOperationResponse> deleteRmaRecord(
            @PathVariable String productType, 
            @PathVariable String serialNo) {
        try {
            RmaOperationResponse response = rmaService.deleteRmaRecord(productType, serialNo);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            RmaOperationResponse errorResponse = RmaOperationResponse.deleteError("刪除 RMA 記錄時發生錯誤: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    // ==================== 輔助 API ====================
    
    /**
     * 檢查 RMA 記錄是否存在
     * GET /api/rma/exists/{productType}/{serialNo}
     */
    @GetMapping("/exists/{productType}/{serialNo}")
    public ResponseEntity<Map<String, Object>> checkRmaRecordExists(
            @PathVariable String productType, 
            @PathVariable String serialNo) {
        try {
            boolean exists = rmaService.existsBySerialNo(productType, serialNo);
            
            Map<String, Object> response = Map.of(
                "exists", exists,
                "productType", productType,
                "serialNo", serialNo,
                "message", exists ? "RMA 記錄存在" : "RMA 記錄不存在"
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = Map.of(
                "exists", false,
                "error", true,
                "message", "檢查 RMA 記錄時發生錯誤: " + e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * 取得支援的產品線列表
     * GET /api/rma/product-lines
     */
    @GetMapping("/product-lines")
    public ResponseEntity<List<String>> getSupportedProductLines() {
        try {
            List<String> productLines = productLineService.getAllProductLineNames();
            return ResponseEntity.ok(productLines);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
    
    /**
     * 驗證產品線是否有效
     * GET /api/rma/validate-product-line/{productType}
     */
    @GetMapping("/validate-product-line/{productType}")
    public ResponseEntity<Map<String, Object>> validateProductLine(@PathVariable String productType) {
        try {
            boolean isValid = productLineService.isValidProductLine(productType);
            
            Map<String, Object> response = Map.of(
                "valid", isValid,
                "productType", productType,
                "message", isValid ? "產品線有效" : "無效的產品線"
            );
            
            if (!isValid) {
                List<String> availableProductLines = productLineService.getAllProductLineNames();
                response = Map.of(
                    "valid", false,
                    "productType", productType,
                    "message", "無效的產品線",
                    "availableProductLines", availableProductLines
                );
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = Map.of(
                "valid", false,
                "error", true,
                "message", "驗證產品線時發生錯誤: " + e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    // ==================== 健康檢查 ====================
    
    /**
     * RMA 服務健康檢查
     * GET /api/rma/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        try {
            List<String> productLines = productLineService.getAllProductLineNames();
            
            Map<String, Object> healthInfo = Map.of(
                "status", "healthy",
                "service", "RMA Service",
                "timestamp", System.currentTimeMillis(),
                "productLines", productLines,
                "productLineCount", productLines.size()
            );
            
            return ResponseEntity.ok(healthInfo);
            
        } catch (Exception e) {
            Map<String, Object> errorInfo = Map.of(
                "status", "unhealthy",
                "service", "RMA Service",
                "timestamp", System.currentTimeMillis(),
                "error", e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorInfo);
        }
    }
}