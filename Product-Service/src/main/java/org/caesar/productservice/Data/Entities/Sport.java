package org.caesar.productservice.Data.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "sport")
public class Sport {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private long id;

    @Column(name= "nome")
    private String name;

}
