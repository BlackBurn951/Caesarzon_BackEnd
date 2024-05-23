package org.caesar.productservice.Data.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "ordine_prodotto")
public class ProductOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private long id;

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
