package com.mountblue.mygoogledrive.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Note {
    @Id
    private int id;
    private String userName;
    @Column(columnDefinition = "varchar(3500)")
    private String note;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
