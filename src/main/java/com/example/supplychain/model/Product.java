package com.example.supplychain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import java.math.BigDecimal;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String description;
    
    @Enumerated(EnumType.STRING)
    private ProductType productType;
    
    private BigDecimal sellingPrice;
    private BigDecimal purchasePrice;
    
    private Integer stockQty; // Not required for SERVICE
    
    private Long artisanId; // References Contact ID
    
    private Boolean active;

    public Product() {}
    
    public Product(String name, String description, ProductType productType, BigDecimal sellingPrice, BigDecimal purchasePrice, Integer stockQty, Long artisanId, Boolean active) {
        this.name = name;
        this.description = description;
        this.productType = productType;
        this.sellingPrice = sellingPrice;
        this.purchasePrice = purchasePrice;
        this.stockQty = stockQty;
        this.artisanId = artisanId;
        this.active = active;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public ProductType getProductType() { return productType; }
    public void setProductType(ProductType productType) { this.productType = productType; }
    public BigDecimal getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(BigDecimal sellingPrice) { this.sellingPrice = sellingPrice; }
    public BigDecimal getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(BigDecimal purchasePrice) { this.purchasePrice = purchasePrice; }
    public Integer getStockQty() { return stockQty; }
    public void setStockQty(Integer stockQty) { this.stockQty = stockQty; }
    public Long getArtisanId() { return artisanId; }
    public void setArtisanId(Long artisanId) { this.artisanId = artisanId; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
