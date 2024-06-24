package org.caesar.productservice.Data.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "prodotto")
@Indexed
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @FullTextField
    @Column(name = "descrizione")
    private String description;

    @FullTextField
    @Column(name = "nome")
    private String name;

    @FullTextField
    @Column(name = "marca")
    private String brand;

    @Column(name = "sconto")
    private int discount;

    @Column(name = "prezzo")
    private double price;

    @FullTextField
    @Column(name = "colore_primario")
    private String primaryColor;

    @FullTextField
    @Column(name = "colore_secondario")
    private String secondaryColor;

    @Column(name = "e_abbigliamento")
    private Boolean is_clothing;
}
