package org.caesar.productservice.Data.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "disponibilita")
public class Availability {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private long id;

    @Column(name= "quantita")
    private int quantity;

    @Column(name= "taglia")
    private String size;
}
