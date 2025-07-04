package com.sapphire.rma.controller;

import com.sapphire.rma.dto.*;
import com.sapphire.rma.service.StockService;
import com.sapphire.rma.service.ProductLineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 庫存控制器
 * 提供庫存相關的 REST API 端點
 */
@RestController
@RequestMapping("/api/stock")
@CrossOrigin(origins = "*")
public class StockController {
    
    @Autowired
    private StockService stockService;
    
    @Autowired
    private ProductLineService productLineService;
    
    // ==================== 庫存查詢 API ====================
    
    /**
     * 取得所有庫存記錄 (按產品線)
     * GET /api/stock/{productType}
     */
    @GetMapping("/{productType}")
    public ResponseEntity<StockSearchResponse> getAllStockRecords(@PathVariable String productType) {
        try {
            StockSearchResponse response = stockService.getAllStockRecords(productType);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            StockSearchResponse errorResponse = StockSearchResponse.error("查詢庫存記錄時發生錯誤: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * 搜尋庫存記錄
     * POST /api/stock/search
     */
    @PostMapping("/search")
    public ResponseEntity<StockSearchResponse> searchStockRecords(@Valid @RequestBody StockSearchRequest request) {
        try {
            StockSearchResponse response = stockService.searchStockRecords(request);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            StockSearchResponse errorResponse = StockSearchResponse.error("搜尋庫存記錄時發生錯誤: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * 根據序列號取得庫存詳細資料
     * GET /api/stock/{productType}/detail/{serialNo}
     */
    @GetMapping("/{productType}/detail/{serialNo}")
    public ResponseEntity<StockDetailResponse> getStockBySerialNo(
            @PathVariable String productType, 
            @PathVariable String serialNo) {
        try {
            StockDetailResponse response = stockService.getStockBySerialNo(productType, serialNo);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            StockDetailResponse errorResponse = StockDetailResponse.error("查詢庫存詳細資料時發生錯誤: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    // ==================== 庫存 CRUD 操作 ====================
    
    /**
     * 新增庫存記錄
     * POST /api/stock/create
     */
    @PostMapping("/create")
    public ResponseEntity<StockOperationResponse> createStockRecord(@Valid @RequestBody StockCreateRequest request) {
        try {
            StockOperationResponse response = stockService.createStockRecord(request);
            
            if (response.isSuccess()) {
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            StockOperationResponse errorResponse = StockOperationResponse.createError("新增庫存記錄時發生錯誤: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * 更新庫存記錄
     * PUT /api/stock/update
     */
    @PutMapping("/update")
    public ResponseEntity<StockOperationResponse> updateStockRecord(@Valid @RequestBody StockUpdateRequest request) {
        try {
            StockOperationResponse response = stockService.updateStockRecord(request);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            StockOperationResponse errorResponse = StockOperationResponse.updateError("更新庫存記錄時發生錯誤: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * 刪除庫存記錄
     * DELETE /api/stock/{productType}/{serialNo}
     */
    @DeleteMapping("/{productType}/{serialNo}")
    public ResponseEntity<StockOperationResponse> deleteStockRecord(
            @PathVariable String productType, 
            @PathVariable String serialNo) {
        try {
            StockOperationResponse response = stockService.deleteStockRecord(productType, serialNo);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            StockOperationResponse errorResponse = StockOperationResponse.deleteError("刪除庫存記錄時發生錯誤: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    // ==================== 進階搜尋功能 ====================
    
    /**
     * 關鍵字搜尋庫存 (支援掃碼功能)
     * GET /api/stock/{productType}/search-keyword
     */
    @GetMapping("/{productType}/search-keyword")
    public ResponseEntity<StockSearchResponse> searchStockByKeyword(
            @PathVariable String productType,
            @RequestParam String keyword) {
        try {
            StockSearchResponse response = stockService.searchStockByKeyword(productType, keyword);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            StockSearchResponse errorResponse = StockSearchResponse.error("關鍵字搜尋時發生錯誤: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * 根據 P/N 搜尋庫存
     * GET /api/stock/{productType}/search-pn
     */
    @GetMapping("/{productType}/search-pn")
    public ResponseEntity<StockSearchResponse> searchStockByPN(
            @PathVariable String productType,
            @RequestParam String pn) {
        try {
            StockSearchResponse response = stockService.searchStockByPN(productType, pn);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            StockSearchResponse errorResponse = StockSearchResponse.error("P/N 搜尋時發生錯誤: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * 根據 SKU 搜尋庫存
     * GET /api/stock/{productType}/search-sku
     */
    @GetMapping("/{productType}/search-sku")
    public ResponseEntity<StockSearchResponse> searchStockBySKU(
            @PathVariable String productType,
            @RequestParam String sku) {
        try {
            StockSearchResponse response = stockService.searchStockBySKU(productType, sku);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            StockSearchResponse errorResponse = StockSearchResponse.error("SKU 搜尋時發生錯誤: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    // ==================== 統計功能 ====================
    
    /**
     * 取得庫存統計
     * GET /api/stock/{productType}/stats
     */
    @GetMapping("/{productType}/stats")
    public ResponseEntity<StockStatsResponse> getStockStats(@PathVariable String productType) {
        try {
            StockStatsResponse response = stockService.getStockStats(productType);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            StockStatsResponse errorResponse = StockStatsResponse.error("取得庫存統計時發生錯誤: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    // ==================== 批次操作 ====================
    
    /**
     * 批次刪除庫存記錄
     * POST /api/stock/{productType}/batch-delete
     */
    @PostMapping("/{productType}/batch-delete")
    public ResponseEntity<BatchOperationResponse> batchDeleteStockRecords(
            @PathVariable String productType,
            @RequestBody List<String> serialNos) {
        try {
            BatchOperationResponse response = stockService.batchDeleteStockRecords(productType, serialNos);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            BatchOperationResponse errorResponse = BatchOperationResponse.error("批次刪除時發生錯誤: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    // ==================== 輔助 API ====================
    
    /**
     * 檢查庫存記錄是否存在
     * GET /api/stock/exists/{productType}/{serialNo}
     */
    @GetMapping("/exists/{productType}/{serialNo}")
    public ResponseEntity<Map<String, Object>> checkStockRecordExists(
            @PathVariable String productType, 
            @PathVariable String serialNo) {
        try {
            boolean exists = stockService.existsBySerialNo(productType, serialNo);
            
            Map<String, Object> response = Map.of(
                "exists", exists,
                "productType", productType,
                "serialNo", serialNo,
                "message", exists ? "庫存記錄存在" : "庫存記錄不存在"
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = Map.of(
                "exists", false,
                "error", true,
                "message", "檢查庫存記錄時發生錯誤: " + e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    /**
     * 庫存服務健康檢查
     * GET /api/stock/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        try {
            List<String> productLines = productLineService.getAllProductLineNames();
            
            Map<String, Object> healthInfo = Map.of(
                "status", "healthy",
                "service", "Stock Service",
                "timestamp", System.currentTimeMillis(),
                "productLines", productLines,
                "productLineCount", productLines.size()
            );
            
            return ResponseEntity.ok(healthInfo);
            
        } catch (Exception e) {
            Map<String, Object> errorInfo = Map.of(
                "status", "unhealthy",
                "service", "Stock Service",
                "timestamp", System.currentTimeMillis(),
                "error", e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorInfo);
        }
    }
}