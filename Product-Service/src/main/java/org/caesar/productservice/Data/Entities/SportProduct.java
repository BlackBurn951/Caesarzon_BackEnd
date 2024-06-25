package org.caesar.productservice.Data.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "prodotto_sport")
public class SportProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name= "id_prodotto")
    private Product productId;

    @ManyToOne
    @JoinColumn(name= "id_sport")
    private Sport sportID;
}
