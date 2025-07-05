package com.sapphire.rma.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

/**
 * 檔案上傳控制器
 * 只負責將檔案儲存到 data/ 資料夾
 */
@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "*")
public class FileUploadController {
    
    // data 資料夾路徑
    private static final String DATA_FOLDER = "data";
    
    /**
     * 上傳檔案到 data/ 資料夾
     */
    @PostMapping("/file")
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 驗證檔案
            if (file.isEmpty()) {
                response.put("success", false);
                response.put("message", "檔案不能為空");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 驗證檔案類型（只允許 Excel 檔案）
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || (!originalFilename.endsWith(".xlsx") && !originalFilename.endsWith(".xls"))) {
                response.put("success", false);
                response.put("message", "只允許上傳 Excel 檔案（.xlsx 或 .xls）");
                return ResponseEntity.badRequest().body(response);
            }
            
            // 確保 data 資料夾存在
            File dataDir = new File(DATA_FOLDER);
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }
            
            // 儲存檔案
            Path targetPath = Paths.get(DATA_FOLDER, originalFilename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            
            // 回傳成功訊息
            response.put("success", true);
            response.put("message", "檔案上傳成功");
            response.put("filename", originalFilename);
            response.put("fileSize", file.getSize());
            response.put("filePath", targetPath.toString());
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "檔案上傳失敗: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 取得 data/ 資料夾中的檔案列表
     */
    @GetMapping("/files")
    public ResponseEntity<Map<String, Object>> getDataFiles() {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            File dataDir = new File(DATA_FOLDER);
            
            if (!dataDir.exists()) {
                response.put("success", true);
                response.put("message", "data 資料夾不存在");
                response.put("files", new String[0]);
                return ResponseEntity.ok(response);
            }
            
            File[] files = dataDir.listFiles((dir, name) -> 
                name.endsWith(".xlsx") || name.endsWith(".xls"));
            
            if (files == null) {
                files = new File[0];
            }
            
            String[] fileNames = new String[files.length];
            for (int i = 0; i < files.length; i++) {
                fileNames[i] = files[i].getName();
            }
            
            response.put("success", true);
            response.put("message", "取得檔案列表成功");
            response.put("files", fileNames);
            response.put("count", fileNames.length);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "取得檔案列表失敗: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 刪除 data/ 資料夾中的檔案
     */
    @DeleteMapping("/files/{filename}")
    public ResponseEntity<Map<String, Object>> deleteFile(@PathVariable String filename) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 安全檢查：只允許刪除 Excel 檔案
            if (!filename.endsWith(".xlsx") && !filename.endsWith(".xls")) {
                response.put("success", false);
                response.put("message", "只允許刪除 Excel 檔案");
                return ResponseEntity.badRequest().body(response);
            }
            
            Path filePath = Paths.get(DATA_FOLDER, filename);
            File file = filePath.toFile();
            
            if (!file.exists()) {
                response.put("success", false);
                response.put("message", "檔案不存在: " + filename);
                return ResponseEntity.badRequest().body(response);
            }
            
            if (file.delete()) {
                response.put("success", true);
                response.put("message", "檔案刪除成功: " + filename);
            } else {
                response.put("success", false);
                response.put("message", "檔案刪除失敗: " + filename);
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "刪除檔案時發生錯誤: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * 健康檢查
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        
        File dataDir = new File(DATA_FOLDER);
        
        response.put("status", "healthy");
        response.put("service", "Simple File Upload");
        response.put("dataFolderExists", dataDir.exists());
        response.put("dataFolderPath", dataDir.getAbsolutePath());
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
}