package org.caesar.userservice.Data.Entities;

import jakarta.persistence.*;

@Entity
@Table(name= "indirizzo")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private Long id;

    @Column(name= "nome_strada")
    private String roadName;

    @Column(name= "num_civico")
    private String houseNumber;

    @Column(name= "tipo_strada")
    private String roadType;

    @ManyToOne
    @JoinColumn(name = "id_dati_comune")
    private CityData city;
}
