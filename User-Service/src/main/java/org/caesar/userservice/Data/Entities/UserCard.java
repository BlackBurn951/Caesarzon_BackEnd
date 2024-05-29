package org.caesar.userservice.Data.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "carte_utente")
public class UserCard {

    @Id
    //@GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name= "id_utente")
    private String userId;

    @ManyToOne
    @JoinColumn(name = "id_carta")
    private Card card;
}
