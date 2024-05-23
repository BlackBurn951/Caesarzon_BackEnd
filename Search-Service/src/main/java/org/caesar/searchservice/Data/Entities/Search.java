package org.caesar.searchservice.Data.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "ricerca")
public class Search {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Basic
    @Column(name = "testo", nullable = false)
    private String text;

    @ManyToOne
    @JoinColumn(name = "id_utente", referencedColumnName = "id", nullable = false)
    private int IdUser;
}

