package com.example.supplychain.repository;

import com.example.supplychain.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    boolean existsBySalesOrderId(Long salesOrderId);
}
