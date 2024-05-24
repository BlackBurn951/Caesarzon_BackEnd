package org.caesar.userservice.Data.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "carte_utente")
public class UserCard {

    @Id
    //@GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_utente")
    private User user;

    @ManyToOne
    @JoinColumn(name = "id_carta")
    private Card card;
}
