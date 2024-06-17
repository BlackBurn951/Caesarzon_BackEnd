package org.caesar.productservice.Data.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "recensione")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

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
