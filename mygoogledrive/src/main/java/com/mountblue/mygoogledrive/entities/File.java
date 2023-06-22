package com.mountblue.mygoogledrive.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;


import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

@Entity
@Table(name="file")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class File {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String fileName;
    private byte[] content;
    private long size;
    private boolean isTrashed;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private String fileType;
    private String userName;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @PrePersist
    public void prePersist() {
        this.setCreatedAt(LocalDate.now());
        this.setUpdatedAt(LocalDate.now());
        this.isTrashed= false;
    }
    public boolean getTrashed(){
        return this.isTrashed;
    }
}
