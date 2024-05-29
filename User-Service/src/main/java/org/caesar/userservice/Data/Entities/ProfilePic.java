package org.caesar.userservice.Data.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "foto_profilo")
public class ProfilePic {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "id_utente")
    private String userId;

    @Column(name = "foto_profilo")
    private Byte profilePic;
}
