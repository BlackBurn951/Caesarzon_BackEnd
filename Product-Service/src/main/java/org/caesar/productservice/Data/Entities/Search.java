package org.caesar.productservice.Data.Entities;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name= "ricerche")
public class Search {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name= "testo_ricerca")
    private String searchText;

    @Column(name= "username")
    private String username;
}
