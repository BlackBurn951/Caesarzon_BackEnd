package org.caesar.userservice.Data.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "indirizzi_utente")
@Getter
@Setter
public class UserAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Assuming auto-generation
    private Long id;

    @Column(name = "id_utente")
    private String userId;

    @ManyToOne
    @JoinColumn(name = "id_indirizzo")
    private Address address;
}
