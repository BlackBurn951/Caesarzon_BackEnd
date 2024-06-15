package org.caesar.searchservice.Data.Entities;

import jakarta.persistence.*;

import java.util.UUID;


@Entity
@Table(name = "ricerca")
public class Search {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Basic
    @Column(name = "testo", nullable = false)
    private String text;

    @Column(name = "id_utente", nullable = false)
    private String utente;
}

