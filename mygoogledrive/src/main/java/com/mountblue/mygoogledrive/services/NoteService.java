package com.mountblue.mygoogledrive.services;

import com.mountblue.mygoogledrive.entities.Note;
import com.mountblue.mygoogledrive.repositories.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService {

    @Autowired
    private NoteRepository noteRepository;
    public void addNewNote(String note , Authentication authentication) {
        Note newNote = new Note();
        newNote.setNote(note);
        newNote.setUserName(authentication.getName());
        noteRepository.save(newNote);
    }

    public List<Note> showNotes(Authentication authentication) {
        return noteRepository.findAllByUserName(authentication.getName());
    }
}
