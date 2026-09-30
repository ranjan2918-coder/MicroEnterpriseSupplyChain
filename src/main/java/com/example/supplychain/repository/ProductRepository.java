package com.example.supplychain.repository;

import com.example.supplychain.model.Product;
import com.example.supplychain.model.ProductType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByProductTypeAndStockQtyLessThanEqual(ProductType productType, Integer stockQty);
}
