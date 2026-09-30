package com.example.supplychain.service;

import com.example.supplychain.model.*;
import com.example.supplychain.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private VendorBillRepository vendorBillRepository;

    @Transactional
    public Payment processCustomerPayment(Payment payment) {
        if (payment.getInvoiceId() == null) {
            throw new RuntimeException("Invoice ID is required for customer payment");
        }

        Invoice invoice = invoiceRepository.findById(payment.getInvoiceId())
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new RuntimeException("Invoice is already paid");
        }

        if (payment.getAmount().compareTo(invoice.getAmount()) != 0) {
            throw new RuntimeException("Payment amount must match invoice amount exactly");
        }

        payment.setReferenceNumber("PAY-CUST-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus(PaymentStatus.RECEIVED);

        invoice.setStatus(InvoiceStatus.PAID);
        invoiceRepository.save(invoice);

        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment processArtisanPayment(Payment payment) {
        if (payment.getVendorBillId() == null) {
            throw new RuntimeException("Vendor Bill ID is required for artisan payment");
        }

        VendorBill bill = vendorBillRepository.findById(payment.getVendorBillId())
                .orElseThrow(() -> new RuntimeException("Vendor bill not found"));

        if (bill.getStatus() == VendorBillStatus.PAID) {
            throw new RuntimeException("Vendor bill is already paid");
        }

        if (payment.getAmount().compareTo(bill.getAmount()) != 0) {
            throw new RuntimeException("Payment amount must match bill amount exactly");
        }

        payment.setReferenceNumber("PAY-ART-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus(PaymentStatus.PAID);

        bill.setStatus(VendorBillStatus.PAID);
        bill.setPaymentId(payment.getReferenceNumber()); // Back-reference
        vendorBillRepository.save(bill);

        return paymentRepository.save(payment);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }
}
