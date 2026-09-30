package com.example.supplychain.service;

import com.example.supplychain.model.PurchaseOrder;
import com.example.supplychain.model.VendorBill;
import com.example.supplychain.model.VendorBillStatus;
import com.example.supplychain.repository.ContactRepository;
import com.example.supplychain.repository.PurchaseOrderRepository;
import com.example.supplychain.repository.VendorBillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class VendorBillService {

    @Autowired
    private VendorBillRepository vendorBillRepository;
    
    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;
    
    @Autowired
    private ContactRepository contactRepository;

    @Transactional
    public VendorBill createVendorBill(VendorBill bill) {
        if (bill.getAmount() == null || bill.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Vendor bill amount must be greater than zero");
        }
        
        if (!contactRepository.existsById(bill.getArtisanId())) {
            throw new RuntimeException("Artisan does not exist");
        }
        
        PurchaseOrder po = purchaseOrderRepository.findById(bill.getPurchaseOrderId())
                .orElseThrow(() -> new RuntimeException("Purchase order does not exist"));
                
        if (!po.getArtisanId().equals(bill.getArtisanId())) {
            throw new RuntimeException("Artisan does not match the purchase order's artisan");
        }

        bill.setBillDate(LocalDateTime.now());
        bill.setStatus(VendorBillStatus.PENDING);
        return vendorBillRepository.save(bill);
    }

    public List<VendorBill> getAllVendorBills() {
        return vendorBillRepository.findAll();
    }

    public Optional<VendorBill> getVendorBillById(Long id) {
        return vendorBillRepository.findById(id);
    }

    @Transactional
    public VendorBill payVendorBill(Long id) {
        VendorBill bill = vendorBillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendor bill not found"));
                
        if (bill.getStatus() == VendorBillStatus.PAID) {
            throw new RuntimeException("Bill is already paid");
        }
        
        bill.setStatus(VendorBillStatus.PAID);
        bill.setPaymentId("PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        
        return vendorBillRepository.save(bill);
    }
}
