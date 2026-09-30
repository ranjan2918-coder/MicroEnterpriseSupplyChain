package com.example.supplychain.controller;

import com.example.supplychain.model.Payment;
import com.example.supplychain.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/customer")
    public ResponseEntity<?> processCustomerPayment(@RequestBody Payment payment) { return ResponseEntity.ok(paymentService.processCustomerPayment(payment));
    }

    @PostMapping("/artisan")
    public ResponseEntity<?> processArtisanPayment(@RequestBody Payment payment) { return ResponseEntity.ok(paymentService.processArtisanPayment(payment));
    }

    @GetMapping
    public List<Payment> getAllPayments() {
        return paymentService.getAllPayments();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getPaymentById(@PathVariable Long id) {
        return paymentService.getPaymentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

