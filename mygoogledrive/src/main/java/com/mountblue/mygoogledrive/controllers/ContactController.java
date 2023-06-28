package com.mountblue.mygoogledrive.controllers;

import com.mountblue.mygoogledrive.entities.Contact;
import com.mountblue.mygoogledrive.services.ContactService;
import com.mountblue.mygoogledrive.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ContactController {

    @Autowired
    private ContactService contactService;

    @Autowired
    private FileService fileService;

    @PostMapping("/drive/create-contact")
    @PreAuthorize("authentication.name == authentication.name")
    public String createContact(@RequestParam("c-name") String name , @RequestParam("c-number") String number, Authentication authentication){
            contactService.addNewContact(name, number, authentication);
            return "redirect:/drive";
    }
    @PostMapping("/drive/search")
    @PreAuthorize("authentication.name == authentication.name")
    public String searchContact(Model model, @RequestParam("search") String search, Authentication authentication){
        List<Contact> contacts= contactService.findContact(authentication.getName(),search);
        model.addAttribute("contacts",contacts);
        return "search";
    }


}
