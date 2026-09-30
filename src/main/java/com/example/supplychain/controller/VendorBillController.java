package com.example.supplychain.controller;

import com.example.supplychain.model.VendorBill;
import com.example.supplychain.service.VendorBillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendor-bills")
public class VendorBillController {

    @Autowired
    private VendorBillService vendorBillService;

    @PostMapping
    public ResponseEntity<?> createVendorBill(@RequestBody VendorBill vendorBill) { return ResponseEntity.ok(vendorBillService.createVendorBill(vendorBill));
    }

    @GetMapping
    public List<VendorBill> getAllVendorBills() {
        return vendorBillService.getAllVendorBills();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VendorBill> getVendorBillById(@PathVariable Long id) {
        return vendorBillService.getVendorBillById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/pay")
    public ResponseEntity<?> payVendorBill(@PathVariable Long id) { VendorBill paidBill = vendorBillService.payVendorBill(id);
            return ResponseEntity.ok(paidBill);
    }
}

