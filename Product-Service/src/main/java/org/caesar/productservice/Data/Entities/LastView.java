package org.caesar.productservice.Data.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name= "ultimi_visti")
@Getter
@Setter
public class LastView {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JoinColumn(name = "id_prodotto")
    @OneToOne
    private Product idProduct;

    @Column(name = "username")
    private String username;
}
