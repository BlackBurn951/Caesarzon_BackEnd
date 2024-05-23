package org.caesar.productservice.Data.Entities;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "recensione")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private long id;

    @Column(name= "testo")
    private String text;

    @Column(name= "data")
    private LocalDate date;

    @Column(name= "valutazione")
    private int evaluation;

    @Column(name= "id_utente")
    private long userID;

    @ManyToOne
    @JoinColumn(name= "id_prodotto")
    private Product productID;
}
