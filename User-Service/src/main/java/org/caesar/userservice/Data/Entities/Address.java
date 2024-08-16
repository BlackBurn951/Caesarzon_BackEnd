package org.caesar.userservice.Data.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


@Entity
@Table(name = "indirizzo")
@Getter
@Setter
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "nome_strada")
    private String roadName;

    @Column(name = "num_civico")
    private String houseNumber;

    @Column(name = "tipo_strada")
    private String roadType;

    @OneToOne
    @JoinColumn(name = "id_dati_comune")
    private CityData city;

    @Column(name= "in_utilizzo")
    private boolean onUse;
}
