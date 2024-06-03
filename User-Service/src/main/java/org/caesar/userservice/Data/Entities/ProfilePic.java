package org.caesar.userservice.Data.Entities;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "foto_profilo")
public class ProfilePic {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "id_utente")
    private String userId;

    @Column(name = "foto_profilo")
    private byte[] profilePic;
}
