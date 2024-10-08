package org.caesar.userservice.Data.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "carte_utente")
@Getter
@Setter
public class UserCard {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name= "username_utente")
    private String userUsername;

    @ManyToOne
    @JoinColumn(name = "id_carta")
    private Card card;

    @Column(name= "in_cancellazione")
    private boolean onDeleting;
}
