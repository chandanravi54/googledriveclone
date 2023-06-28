package com.mountblue.mygoogledrive.repositories;

import com.mountblue.mygoogledrive.entities.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Integer> {
    List<Contact> findAllByUserName(String name);

    List<Contact> findAllByNameIgnoreCaseContainingAndUserName(String search, String name);
}
