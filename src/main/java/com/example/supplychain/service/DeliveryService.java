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
public class DeliveryService {

    @Autowired
    private DeliveryRepository deliveryRepository;
    
    @Autowired
    private SalesOrderRepository salesOrderRepository;
    
    @Autowired
    private ContactRepository contactRepository;

    @Transactional
    public Delivery createDelivery(Delivery delivery) {
        if (!salesOrderRepository.existsById(delivery.getSalesOrderId())) {
            throw new RuntimeException("Sales order does not exist");
        }
        
        Contact partner = contactRepository.findById(delivery.getDeliveryPartnerId())
            .orElseThrow(() -> new RuntimeException("Delivery partner does not exist"));
            
        if (partner.getContactType() != ContactType.DELIVERY_PARTNER) {
            throw new RuntimeException("Contact is not a DELIVERY_PARTNER");
        }

        delivery.setStatus(DeliveryStatus.PENDING);
        delivery.setTrackingNumber("TRK-" + UUID.randomUUID().toString().substring(0,8).toUpperCase());
        
        return deliveryRepository.save(delivery);
    }

    public List<Delivery> getAllDeliveries() {
        return deliveryRepository.findAll();
    }

    public Optional<Delivery> getDeliveryById(Long id) {
        return deliveryRepository.findById(id);
    }

    @Transactional
    public Delivery updateStatus(Long id, DeliveryStatus newStatus) {
        Delivery delivery = deliveryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Delivery not found"));
            
        DeliveryStatus currentStatus = delivery.getStatus();
        
        if (!isValidTransition(currentStatus, newStatus)) {
            throw new RuntimeException("Invalid status transition from " + currentStatus + " to " + newStatus);
        }
        
        if (newStatus == DeliveryStatus.DISPATCHED) {
            delivery.setDispatchDate(LocalDateTime.now());
        } else if (newStatus == DeliveryStatus.DELIVERED) {
            delivery.setDeliveryDate(LocalDateTime.now());
        }
        
        delivery.setStatus(newStatus);
        return deliveryRepository.save(delivery);
    }
    
    private boolean isValidTransition(DeliveryStatus current, DeliveryStatus next) {
        if (next == DeliveryStatus.CANCELLED && current != DeliveryStatus.DELIVERED && current != DeliveryStatus.CANCELLED) {
            return true; 
        }
        
        switch (current) {
            case PENDING: return next == DeliveryStatus.ASSIGNED;
            case ASSIGNED: return next == DeliveryStatus.DISPATCHED;
            case DISPATCHED: return next == DeliveryStatus.IN_TRANSIT;
            case IN_TRANSIT: return next == DeliveryStatus.DELIVERED;
            default: return false;
        }
    }
}
