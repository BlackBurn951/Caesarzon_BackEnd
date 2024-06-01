package org.caesar.userservice.Data.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "indirizzo")
@Getter
@Setter
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome_strada")
    private String roadName;

    @Column(name = "num_civico")
    private String houseNumber;

    @Column(name = "tipo_strada")
    private String roadType;

    @OneToOne
    @JoinColumn(name = "id_dati_comune")
    private CityData city;


}
