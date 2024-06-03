package org.caesar.userservice.Data.Entities;

import jakarta.persistence.*;
import lombok.Getter;


@Entity
@Table(name = "dati_comune")
@Getter
public class CityData {

    @Id
    @Column(name = "id_comune")
    private Long id;

    @Column(name = "nome_comune")
    private String city;

    @Column(name = "cap")
    private String cap;

    @Column(name = "regione")
    private String region;

    @Column(name = "provincia")
    private String province;


}
