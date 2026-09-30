package com.example.supplychain.repository;

import com.example.supplychain.model.Contact;
import com.example.supplychain.model.ContactType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    List<Contact> findByContactType(ContactType contactType);
}
