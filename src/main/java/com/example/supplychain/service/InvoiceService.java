package com.example.supplychain.service;

import com.example.supplychain.model.Invoice;
import com.example.supplychain.model.InvoiceStatus;
import com.example.supplychain.model.SalesOrder;
import com.example.supplychain.model.SalesOrderStatus;
import com.example.supplychain.repository.InvoiceRepository;
import com.example.supplychain.repository.SalesOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;
    
    @Autowired
    private SalesOrderRepository salesOrderRepository;

    @Transactional
    public Invoice generateInvoiceFromOrder(Long orderId) {
        SalesOrder order = salesOrderRepository.findById(orderId)
            .orElseThrow(() -> new IllegalArgumentException("Sales order does not exist"));
            
        if (order.getStatus() == SalesOrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot generate invoice for a cancelled order");
        }
        
        if (invoiceRepository.existsBySalesOrderId(orderId)) {
            throw new IllegalStateException("Invoice already exists for this sales order");
        }
        
        Invoice invoice = new Invoice();
        invoice.setSalesOrderId(orderId);
        invoice.setMerchantId(order.getMerchantId());
        invoice.setAmount(order.getTotalAmount());
        invoice.setInvoiceDate(LocalDateTime.now());
        invoice.setStatus(InvoiceStatus.ISSUED);
        
        return invoiceRepository.save(invoice);
    }
    
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }
    
    public Optional<Invoice> getInvoiceById(Long id) {
        return invoiceRepository.findById(id);
    }
}
