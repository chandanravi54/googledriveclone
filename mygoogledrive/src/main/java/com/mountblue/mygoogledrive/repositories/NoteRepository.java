package com.mountblue.mygoogledrive.repositories;

import com.mountblue.mygoogledrive.entities.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Integer> {


    List<Note> findAllByUserName(String name);
}
