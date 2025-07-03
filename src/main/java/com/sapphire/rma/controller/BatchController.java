package com.sapphire.rma.controller;

import com.sapphire.rma.service.ProductLineService;
import com.sapphire.rma.service.PythonBatchService;
import com.sapphire.rma.service.PythonBatchService.BatchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/batch")
@CrossOrigin(origins = "*")
public class BatchController {
    
    @Autowired
    private PythonBatchService pythonBatchService;
    
    @Autowired
    private ProductLineService productLineService;
    
    /**
     * 執行批次處理
     */
    @PostMapping("/execute")
    public ResponseEntity<BatchResult> executeBatchProcess(@RequestBody BatchExecuteRequest request) {
        try {
            String productType = request.getProductType();
            
            // 驗證請求參數
            if (productType == null || productType.trim().isEmpty()) {
                BatchResult errorResult = new BatchResult();
                errorResult.setSuccess(false);
                errorResult.setMessage("請提供產品線類型");
                return ResponseEntity.badRequest().body(errorResult);
            }
            
            // 執行批次處理
            BatchResult result = pythonBatchService.executeBatchProcess(productType.trim());
            
            if (result.isSuccess()) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
            }
            
        } catch (Exception e) {
            BatchResult errorResult = new BatchResult();
            errorResult.setSuccess(false);
            errorResult.setMessage("執行批次處理時發生錯誤: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResult);
        }
    }
    
    /**
     * 取得支援的產品線列表
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
     * 初始化產品線資料
     */
    @PostMapping("/init-product-lines")
    public ResponseEntity<Map<String, Object>> initializeProductLines() {
        try {
            productLineService.initializeDefaultProductLines();
            List<String> productLines = productLineService.getAllProductLineNames();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "產品線初始化完成",
                "productLines", productLines,
                "count", productLines.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "初始化失敗: " + e.getMessage()
            ));
        }
    }
    
    /**
     * 健康檢查端點
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        try {
            long productLineCount = productLineService.getProductLineCount();
            return ResponseEntity.ok(Map.of(
                "status", "healthy",
                "message", "批次處理服務運行正常",
                "productLineCount", productLineCount,
                "timestamp", System.currentTimeMillis()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "status", "error",
                "message", "服務異常: " + e.getMessage(),
                "timestamp", System.currentTimeMillis()
            ));
        }
    }
    
    /**
     * 驗證產品線是否有效
     */
    @GetMapping("/validate-product-line/{productType}")
    public ResponseEntity<Map<String, Object>> validateProductLine(@PathVariable String productType) {
        try {
            boolean isValid = productLineService.isValidProductLine(productType);
            
            if (isValid) {
                return ResponseEntity.ok(Map.of(
                    "valid", true,
                    "message", "產品線有效",
                    "productType", productType
                ));
            } else {
                List<String> availableProductLines = productLineService.getAllProductLineNames();
                return ResponseEntity.ok(Map.of(
                    "valid", false,
                    "message", "無效的產品線",
                    "productType", productType,
                    "availableProductLines", availableProductLines
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "valid", false,
                "message", "驗證失敗: " + e.getMessage(),
                "productType", productType
            ));
        }
    }
    
    /**
     * 請求 DTO
     */
    public static class BatchExecuteRequest {
        private String productType;
        
        public String getProductType() {
            return productType;
        }
        
        public void setProductType(String productType) {
            this.productType = productType;
        }
    }
}