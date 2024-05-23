package org.caesar.userservice.Data.Entities;

import jakarta.persistence.*;

@Entity
@Table(name= "indirizzi_utente")
public class UserAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private Long id;

    @ManyToOne
    @JoinColumn(name= "id_utente")
    private User utente;

    @ManyToOne
    @JoinColumn(name= "id_utente")
    private User user;

    @ManyToOne
    @JoinColumn(name= "id_indirizzo")
    private Address address;
}
