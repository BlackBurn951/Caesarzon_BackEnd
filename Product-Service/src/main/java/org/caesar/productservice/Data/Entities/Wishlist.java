package org.caesar.productservice.Data.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "lista_desideri")
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name= "nome")
    private String name;

    @Column(name= "visibilita")
    private String visibility;

    @Column(name= "username_utente")
    private String userUsername;

    @Column(name= "in_cancellazione")
    private boolean onDeleting;
}
