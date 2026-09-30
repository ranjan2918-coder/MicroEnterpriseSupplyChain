package com.example.supplychain.service;

import com.example.supplychain.model.Product;
import com.example.supplychain.model.ProductType;
import com.example.supplychain.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Product createProduct(Product product) {
        if (product.getProductType() == ProductType.SERVICE) {
            product.setStockQty(null); // Services don't track stock
        }
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product updateProduct(Long id, Product productDetails) {
        return productRepository.findById(id).map(product -> {
            product.setName(productDetails.getName());
            product.setDescription(productDetails.getDescription());
            product.setProductType(productDetails.getProductType());
            product.setSellingPrice(productDetails.getSellingPrice());
            product.setPurchasePrice(productDetails.getPurchasePrice());
            
            if (productDetails.getProductType() == ProductType.SERVICE) {
                product.setStockQty(null);
            } else {
                product.setStockQty(productDetails.getStockQty());
            }
            
            product.setArtisanId(productDetails.getArtisanId());
            product.setActive(productDetails.getActive());
            return productRepository.save(product);
        }).orElseThrow(() -> new RuntimeException("Product not found with id " + id));
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public List<Product> getLowStockProducts(Integer threshold) {
        return productRepository.findByProductTypeAndStockQtyLessThanEqual(ProductType.GOODS, threshold);
    }
}
