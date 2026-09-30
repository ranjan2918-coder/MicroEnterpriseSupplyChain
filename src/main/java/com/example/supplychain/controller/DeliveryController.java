package com.example.supplychain.controller;

import com.example.supplychain.model.Delivery;
import com.example.supplychain.model.DeliveryStatus;
import com.example.supplychain.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/deliveries")
public class DeliveryController {

    @Autowired
    private DeliveryService deliveryService;

    @PostMapping
    public ResponseEntity<?> createDelivery(@RequestBody Delivery delivery) { return ResponseEntity.ok(deliveryService.createDelivery(delivery));
    }

    @GetMapping
    public List<Delivery> getAllDeliveries() {
        return deliveryService.getAllDeliveries();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Delivery> getDeliveryById(@PathVariable Long id) {
        return deliveryService.getDeliveryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        DeliveryStatus newStatus = DeliveryStatus.valueOf(body.get("status").toUpperCase());
        Delivery updatedDelivery = deliveryService.updateStatus(id, newStatus);
        return ResponseEntity.ok(updatedDelivery);
    }
}

