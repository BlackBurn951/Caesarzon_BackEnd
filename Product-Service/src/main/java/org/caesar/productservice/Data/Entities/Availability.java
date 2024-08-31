package org.caesar.productservice.Data.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "disponibilita")
@Getter
@Setter
public class Availability {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name= "quantita")
    private int amount;

    @Column(name= "taglia")
    private String size;

    @OneToOne
    @JoinColumn(name = "id_prodotto")
    private Product product;

    @Column(name= "in_modifica")
    private boolean onChanges;
}
