package org.caesar.productservice.Data.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name= "ricerche")
@Getter
@Setter
public class Search {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name= "testo_ricerca")
    private String searchText;

    @Column(name= "username")
    private String username;
}
