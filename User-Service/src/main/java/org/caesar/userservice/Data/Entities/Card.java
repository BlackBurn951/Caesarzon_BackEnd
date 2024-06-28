package org.caesar.userservice.Data.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name ="carte")
@Getter
@Setter
@ToString
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "num_carta")
    private String cardNumber;

    @Column(name = "titolare")
    private String owner;

    @Column(name = "cvv")
    private String cvv;

    @Column(name = "data_scadenza")
    private String expiryDate;

    @Column(name= "saldo")
    private double balance;
}
