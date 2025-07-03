package com.sapphire.rma.service;

import com.sapphire.rma.entity.ProductLine;
import com.sapphire.rma.repository.ProductLineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductLineService {
    
    @Autowired
    private ProductLineRepository productLineRepository;
    
    /**
     * 取得所有產品線
     */
    public List<ProductLine> getAllProductLines() {
        return productLineRepository.findAllOrderById();
    }
    
    /**
     * 取得所有產品線名稱
     */
    public List<String> getAllProductLineNames() {
        return productLineRepository.findAllProductLineNames();
    }
    
    /**
     * 根據產品線名稱查找
     */
    public Optional<ProductLine> getProductLineByName(String productLine) {
        return productLineRepository.findByProductLine(productLine);
    }
    
    /**
     * 驗證產品線是否有效
     */
    public boolean isValidProductLine(String productLine) {
        return productLineRepository.existsByProductLine(productLine);
    }
    
    /**
     * 新增產品線
     */
    public ProductLine saveProductLine(ProductLine productLine) {
        return productLineRepository.save(productLine);
    }
    
    /**
     * 新增產品線 (只提供名稱)
     */
    public ProductLine addProductLine(String productLineName) {
        ProductLine productLine = new ProductLine(productLineName);
        return productLineRepository.save(productLine);
    }
    
    /**
     * 檢查並初始化預設產品線
     */
    public void initializeDefaultProductLines() {
        // 檢查是否已有資料
        if (productLineRepository.count() == 0) {
            // 建立預設產品線
            addProductLine("VGA");
            addProductLine("MB");
            addProductLine("MiniPC");
            
            System.out.println("已初始化預設產品線: VGA, MB, MiniPC");
        }
    }
    
    /**
     * 刪除產品線
     */
    public void deleteProductLine(Long id) {
        productLineRepository.deleteById(id);
    }
    
    /**
     * 取得產品線數量
     */
    public long getProductLineCount() {
        return productLineRepository.count();
    }
}