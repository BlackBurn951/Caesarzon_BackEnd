package org.caesar.productservice.Data.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "prodotto")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name= "descrizione")
    private String description;

    @Column(name= "nome")
    private String name;

    @Column(name= "marca")
    private String brand;

    @Column(name= "sconto")
    private int discount;

    @Column(name= "prezzo")
    private double price;

    @Column(name= "colore_primario")
    private String primaryColor;

    @Column(name= "colore_secondario")
    private String secondaryColor;

    @Column(name= "e_abbigliamento")
    private Boolean is_clothing;

    @OneToMany
    @JoinColumn(name= "id_disponibilita")
    private List<Availability> availabilityID;
}
