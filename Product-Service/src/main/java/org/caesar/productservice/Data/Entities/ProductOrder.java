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
    private Order order;

    @ManyToOne
    @JoinColumn(name= "id_prodotto")
    private Product product;

    @Column(name= "username_utente")
    private String username;

    @Column(name= "quantita")
    private int quantity;

    @Column(name= "per_dopo")
    private boolean buyLater;

    @Column(name= "taglia")
    private String size;
}
