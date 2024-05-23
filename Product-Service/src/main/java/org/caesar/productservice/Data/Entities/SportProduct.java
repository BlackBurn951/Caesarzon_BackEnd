package org.caesar.productservice.Data.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "prodotto_sport")
public class SportProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private long id;

    @Column(name= "id_prodotto")
    private int quantity;

    @ManyToOne
    @JoinColumn(name= "id_sport")
    private Sport sportID;
}
