package org.caesar.productservice.Data.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
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
