package com.example.supplychain.controller;

import com.example.supplychain.model.SalesOrder;
import com.example.supplychain.model.SalesOrderStatus;
import com.example.supplychain.service.SalesOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class SalesOrderController {

    @Autowired
    private SalesOrderService salesOrderService;

    @PostMapping
    public ResponseEntity<?> createSalesOrder(@RequestBody SalesOrder salesOrder) {
        return ResponseEntity.ok(salesOrderService.createSalesOrder(salesOrder));
    }

    @GetMapping
    public List<SalesOrder> getAllOrders() {
        return salesOrderService.getAllOrders();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalesOrder> getOrderById(@PathVariable Long id) {
        return salesOrderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        SalesOrderStatus newStatus = SalesOrderStatus.valueOf(body.get("status").toUpperCase());
        SalesOrder updatedOrder = salesOrderService.updateStatus(id, newStatus);
        return ResponseEntity.ok(updatedOrder);
    }
}

