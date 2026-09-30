package com.example.supplychain.controller;

import com.example.supplychain.model.OrderStatus;
import com.example.supplychain.model.PurchaseOrder;
import com.example.supplychain.service.PurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/purchase-orders")
public class PurchaseOrderController {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @PostMapping
    public ResponseEntity<?> createPurchaseOrder(@RequestBody PurchaseOrder purchaseOrder) { return ResponseEntity.ok(purchaseOrderService.createPurchaseOrder(purchaseOrder));
    }

    @GetMapping
    public List<PurchaseOrder> getAllPurchaseOrders() {
        return purchaseOrderService.getAllPurchaseOrders();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrder> getPurchaseOrderById(@PathVariable Long id) {
        return purchaseOrderService.getPurchaseOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) { OrderStatus newStatus = OrderStatus.valueOf(body.get("status").toUpperCase());
            PurchaseOrder updatedOrder = purchaseOrderService.updateStatus(id, newStatus);
            return ResponseEntity.ok(updatedOrder);
    }
}

