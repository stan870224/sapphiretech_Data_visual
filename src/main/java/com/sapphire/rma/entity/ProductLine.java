package com.sapphire.rma.entity;

import javax.persistence.*;

@Entity
@Table(name = "product_lines")
public class ProductLine {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "product_line", unique = true, nullable = false)
    private String productLine;
    
    // Constructors
    public ProductLine() {}
    
    public ProductLine(String productLine) {
        this.productLine = productLine;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getProductLine() {
        return productLine;
    }
    
    public void setProductLine(String productLine) {
        this.productLine = productLine;
    }
    
    @Override
    public String toString() {
        return "ProductLine{" +
                "id=" + id +
                ", productLine='" + productLine + '\'' +
                '}';
    }
}