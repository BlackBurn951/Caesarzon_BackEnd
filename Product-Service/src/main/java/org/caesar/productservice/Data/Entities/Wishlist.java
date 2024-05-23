package org.caesar.productservice.Data.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "lista_desideri")
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private long id;

    @Column(name= "nome")
    private String name;

    @Column(name= "visibilita")
    private String visibility;

    @Column(name= "id_utente")
    private long userID;
}
