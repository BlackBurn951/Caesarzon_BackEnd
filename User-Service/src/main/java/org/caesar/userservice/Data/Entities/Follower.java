package org.caesar.userservice.Data.Entities;

import jakarta.persistence.*;

import java.util.UUID;


@Entity
@Table(name ="follower")
public class Follower {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name= "id_utente1")
    private String userId1;

    @Column(name= "id_utente2")
    private String userId2;

    @Column(name = "amico")
    private boolean friend;

}
