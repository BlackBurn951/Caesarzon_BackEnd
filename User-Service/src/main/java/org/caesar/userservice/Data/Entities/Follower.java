package org.caesar.userservice.Data.Entities;

import jakarta.persistence.*;


@Entity
@Table(name ="follower")
public class Follower {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "id_utente1")
    private Long userId1;

    @Column(name = "id_utente2")
    private Long userId2;

    @Column(name = "amico")
    private boolean friend;

}
