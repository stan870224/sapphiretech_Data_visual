package com.sapphire.rma.repository;

import com.sapphire.rma.entity.ProductLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductLineRepository extends JpaRepository<ProductLine, Long> {
    
    /**
     * 根據產品線名稱查找
     */
    Optional<ProductLine> findByProductLine(String productLine);
    
    /**
     * 查找所有產品線，按 id 排序
     */
    @Query("SELECT p FROM ProductLine p ORDER BY p.id")
    List<ProductLine> findAllOrderById();
    
    /**
     * 只取得產品線名稱列表
     */
    @Query("SELECT p.productLine FROM ProductLine p ORDER BY p.id")
    List<String> findAllProductLineNames();
    
    /**
     * 檢查產品線是否存在
     */
    Boolean existsByProductLine(String productLine);
}