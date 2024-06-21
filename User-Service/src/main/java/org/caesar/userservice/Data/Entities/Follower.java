package org.caesar.userservice.Data.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


@Entity
@Table(name ="follower")
@Getter
@Setter
public class Follower {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name= "username_utente1")
    private String userUsername1;

    @Column(name= "username_utente2")
    private String userUsername2;

    @Column(name = "amico")
    private boolean friend;

}
