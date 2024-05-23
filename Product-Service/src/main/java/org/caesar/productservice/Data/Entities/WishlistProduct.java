package org.caesar.productservice.Data.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "prodotti_lista_desideri")
public class WishlistProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private long id;

    @ManyToOne
    @JoinColumn(name= "id_lista_desideri")
    private Wishlist wishilistID;

    @ManyToOne
    @JoinColumn(name= "id_prodotto")
    private Product productID;
}
