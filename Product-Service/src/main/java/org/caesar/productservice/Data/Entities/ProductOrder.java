package org.caesar.productservice.Data.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "ordine_prodotto")
public class ProductOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name= "totale")
    private double total;

    @ManyToOne
    @JoinColumn(name= "id_ordine")
    private Order orderID;

    @ManyToOne
    @JoinColumn(name= "id_prodotto")
    private Product productID;

    @Column(name= "id_utente")
    private long userID;

    @Column(name= "quantita")
    private int quantity;
}
