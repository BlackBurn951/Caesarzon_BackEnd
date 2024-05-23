package org.caesar.userservice.Data.Entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name= "admin")
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private Long id;

    @Column(name= "nome")
    private String name;

    @Column(name= "cognome")
    private String surname;

    @Column(name= "email")
    private String email;

    @Column(name= "password")
    private String password;

    @Column(name= "username")
    private String username;

    @Column(name= "foto_profilo")
    private byte profilePic;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ban> bannedUsers = new ArrayList<>();
}
