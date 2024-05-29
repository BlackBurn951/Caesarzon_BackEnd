package org.caesar.userservice.Data.Entities;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name ="carte")
public class Card {
    @Id
    //@GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "num_carta")
    private String cardNumber;

    @Column(name = "titolare")
    private String owner;

    @Column(name = "cvv")
    private String cvv;

    @Column(name = "data_scadenza")
    private LocalDate expirationDate;



}
