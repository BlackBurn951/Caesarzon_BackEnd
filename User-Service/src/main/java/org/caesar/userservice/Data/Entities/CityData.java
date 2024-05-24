package org.caesar.userservice.Data.Entities;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name= "dati_comune")
public class CityData {

    @Id
//  @GeneratedValue(strategy = GenerationType.UUID)
    private Long id;


    @Column(name= "citta")
    private String city;

    @Column(name= "cap")
    private String cap;

    @Column(name= "regione")
    private String region;

    @Column(name= "provincia")
    private String province;
}
