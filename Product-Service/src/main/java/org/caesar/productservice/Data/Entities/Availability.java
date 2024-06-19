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
    private int quantity;

    @Column(name= "taglia")
    private String size;

}
