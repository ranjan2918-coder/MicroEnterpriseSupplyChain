package com.example.supplychain.service;

import com.example.supplychain.model.Contact;
import com.example.supplychain.model.ContactType;
import com.example.supplychain.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    public Contact createContact(Contact contact) {
        return contactRepository.save(contact);
    }

    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    public Optional<Contact> getContactById(Long id) {
        return contactRepository.findById(id);
    }

    public Contact updateContact(Long id, Contact contactDetails) {
        return contactRepository.findById(id).map(contact -> {
            contact.setName(contactDetails.getName());
            contact.setContactType(contactDetails.getContactType());
            contact.setPhone(contactDetails.getPhone());
            contact.setEmail(contactDetails.getEmail());
            contact.setAddress(contactDetails.getAddress());
            contact.setActive(contactDetails.getActive());
            return contactRepository.save(contact);
        }).orElseThrow(() -> new RuntimeException("Contact not found with id " + id));
    }

    public void deleteContact(Long id) {
        contactRepository.deleteById(id);
    }

    public List<Contact> getContactsByType(ContactType type) {
        return contactRepository.findByContactType(type);
    }
}
