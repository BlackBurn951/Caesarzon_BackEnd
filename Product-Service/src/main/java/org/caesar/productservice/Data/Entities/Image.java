package org.caesar.productservice.Data.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "immagine")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private long id;

    @Column(name= "file")
    private byte file;

    @ManyToOne
    @JoinColumn(name= "id_prodotto")
    private Product idProduct;
}
