package com.example.supplychain.service;

import com.example.supplychain.model.OrderStatus;
import com.example.supplychain.model.Product;
import com.example.supplychain.model.ProductType;
import com.example.supplychain.model.PurchaseOrder;
import com.example.supplychain.model.PurchaseOrderItem;
import com.example.supplychain.repository.ContactRepository;
import com.example.supplychain.repository.ProductRepository;
import com.example.supplychain.repository.PurchaseOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PurchaseOrderService {

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;
    
    @Autowired
    private ContactRepository contactRepository;
    
    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public PurchaseOrder createPurchaseOrder(PurchaseOrder order) {
        if (!contactRepository.existsById(order.getArtisanId())) {
            throw new RuntimeException("Artisan does not exist");
        }
        
        BigDecimal total = BigDecimal.ZERO;
        
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            for (PurchaseOrderItem item : order.getItems()) {
                if (item.getQuantity() == null || item.getQuantity() <= 0) {
                    throw new RuntimeException("Quantity must be > 0");
                }
                
                Product product = productRepository.findById(item.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product does not exist"));
                
                if (product.getProductType() != ProductType.GOODS) {
                    throw new RuntimeException("Product must be GOODS");
                }
                
                BigDecimal itemTotal = item.getUnitPrice().multiply(new BigDecimal(item.getQuantity()));
                item.setTotalPrice(itemTotal);
                total = total.add(itemTotal);
                
                item.setPurchaseOrder(order);
            }
        } else {
            throw new RuntimeException("Order must have at least one item");
        }
        
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.CREATED);
        order.setTotalAmount(total);
        
        return purchaseOrderRepository.save(order);
    }

    public List<PurchaseOrder> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAll();
    }

    public Optional<PurchaseOrder> getPurchaseOrderById(Long id) {
        return purchaseOrderRepository.findById(id);
    }

    @Transactional
    public PurchaseOrder updateStatus(Long id, OrderStatus newStatus) {
        PurchaseOrder order = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id " + id));
        
        if (newStatus == OrderStatus.RECEIVED && order.getStatus() != OrderStatus.RECEIVED) {
            BigDecimal total = BigDecimal.ZERO;
            for (PurchaseOrderItem item : order.getItems()) {
                Product product = productRepository.findById(item.getProductId())
                        .orElseThrow(() -> new RuntimeException("Product not found"));
                product.setStockQty(product.getStockQty() + item.getQuantity());
                productRepository.save(product);
                total = total.add(item.getTotalPrice());
            }
            order.setTotalAmount(total);
        }
        
        order.setStatus(newStatus);
        return purchaseOrderRepository.save(order);
    }
}
