package org.caesar.userservice.Data.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "indirizzi_utente")
@Getter
@Setter
public class UserAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "username_utente")
    private String userUsername;

    @ManyToOne
    @JoinColumn(name = "id_indirizzo")
    private Address address;

    @Column(name= "in_cancellazione")
    private boolean onDeleting;
}
