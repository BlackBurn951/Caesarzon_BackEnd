package org.caesar.productservice.Data.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "prodotti_lista_desideri")
public class WishlistProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID Id;

    @ManyToOne
    @JoinColumn(name= "id_lista_desideri")
    private Wishlist wishlist;

    @ManyToOne
    @JoinColumn(name= "id_prodotto")
    private Product product;
}
