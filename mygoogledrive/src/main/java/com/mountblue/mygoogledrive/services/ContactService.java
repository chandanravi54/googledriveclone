package com.mountblue.mygoogledrive.services;

import com.mountblue.mygoogledrive.entities.Contact;
import com.mountblue.mygoogledrive.repositories.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    public void addNewContact(String name, String number, Authentication authentication) {
        Contact contact = new Contact();
        contact.setName(name);
        contact.setContactNumber(number);
        contact.setUserName(authentication.getName());
        contactRepository.save(contact);
    }

    public List<Contact> showContacts(Authentication authentication) {
        String name= authentication.getName();
        return contactRepository.findAllByUserName(name);
    }

    public List<Contact> findContact(String name, String search) {
        return contactRepository.findAllByNameIgnoreCaseContainingAndUserName(search, name);
    }
}
