package com.mountblue.mygoogledrive.controllers;

import com.mountblue.mygoogledrive.entities.Note;
import com.mountblue.mygoogledrive.services.FileService;
import com.mountblue.mygoogledrive.services.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class NoteController {

    @Autowired
    private NoteService noteService;
    @Autowired
    private FileService fileService;

    @PostMapping("/drive/save-note")
   @PreAuthorize("authentication.name == authentication.name")
   public String createNote(@RequestParam  ("note") String note , Authentication authentication){
        noteService.addNewNote(note, authentication);
       return "redirect:/drive";
    }

    @PostMapping("/drive/my-notes")
    @PreAuthorize("authentication.name == authentication.name")
    public String showNotes(Authentication authentication, Model model){
        List<Note> notes = noteService.showNotes(authentication);
        model.addAttribute("notes", notes);
        return "search";
    }
}
