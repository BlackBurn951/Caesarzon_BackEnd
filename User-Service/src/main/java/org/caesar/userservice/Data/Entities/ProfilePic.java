package org.caesar.userservice.Data.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "foto_profilo")
@Setter
@Getter
public class ProfilePic {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "username_utente")
    private String userUsername;

    @Column(name = "foto_profilo")
    private byte[] profilePic;

    @Column(name= "in_cancellazione")
    private boolean onDeleting;
}
