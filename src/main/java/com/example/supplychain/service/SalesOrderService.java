package com.example.supplychain.service;

import com.example.supplychain.model.*;
import com.example.supplychain.repository.ContactRepository;
import com.example.supplychain.repository.ProductRepository;
import com.example.supplychain.repository.SalesOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SalesOrderService {

    @Autowired
    private SalesOrderRepository salesOrderRepository;
    
    @Autowired
    private ContactRepository contactRepository;
    
    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public SalesOrder createSalesOrder(SalesOrder order) {
        Contact merchant = contactRepository.findById(order.getMerchantId())
            .orElseThrow(() -> new com.example.supplychain.exception.ResourceNotFoundException("Merchant does not exist"));
            
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new com.example.supplychain.exception.InvalidOperationException("Order must have at least one item");
        }
        
        for (SalesOrderItem item : order.getItems()) {
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new com.example.supplychain.exception.InvalidOperationException("Quantity must be > 0");
            }
            
            Product product = productRepository.findById(item.getProductId())
                .orElseThrow(() -> new com.example.supplychain.exception.ResourceNotFoundException("Product does not exist"));
                
            if (product.getProductType() != ProductType.GOODS) {
                throw new com.example.supplychain.exception.InvalidOperationException("Product must be GOODS");
            }
            
            if (product.getStockQty() < item.getQuantity()) {
                throw new com.example.supplychain.exception.InsufficientStockException("Insufficient stock for product ID: " + product.getId() + ". Available: " + product.getStockQty() + ", Requested: " + item.getQuantity());
            }
            
            // Atomically decrease stockQty
            product.setStockQty(product.getStockQty() - item.getQuantity());
            productRepository.save(product);
            
            BigDecimal itemTotal = item.getUnitPrice().multiply(new BigDecimal(item.getQuantity()));
            item.setTotalPrice(itemTotal);
            totalAmount = totalAmount.add(itemTotal);
            
            item.setSalesOrder(order);
        }
        
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(SalesOrderStatus.CREATED);
        order.setTotalAmount(totalAmount);
        
        return salesOrderRepository.save(order);
    }

    public List<SalesOrder> getAllOrders() {
        return salesOrderRepository.findAll();
    }

    public Optional<SalesOrder> getOrderById(Long id) {
        return salesOrderRepository.findById(id);
    }

    @Transactional
    public SalesOrder updateStatus(Long id, SalesOrderStatus newStatus) {
        SalesOrder order = salesOrderRepository.findById(id)
            .orElseThrow(() -> new com.example.supplychain.exception.ResourceNotFoundException("Sales order not found"));
            
        order.setStatus(newStatus);
        return salesOrderRepository.save(order);
    }
}
