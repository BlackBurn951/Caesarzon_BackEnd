package org.caesar.productservice.Data.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name= "ultimi_visti")
@Getter
@Setter
public class LastView {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "id_prodotto")
    @OneToMany
    private List<Product> idProduct;

    @Column(name = "username")
    private String username;
}
