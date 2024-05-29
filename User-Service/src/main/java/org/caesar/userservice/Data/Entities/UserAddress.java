package org.caesar.userservice.Data.Entities;

import jakarta.persistence.*;

@Entity
@Table(name= "indirizzi_utente")
public class UserAddress {

    @Id
    //@GeneratedValue(strategy = GenerationType.UUID)
    private Long id;

    @Column(name= "id_utente")
    private String userId;

    @ManyToOne
    @JoinColumn(name= "id_indirizzo")
    private Address address;
}
