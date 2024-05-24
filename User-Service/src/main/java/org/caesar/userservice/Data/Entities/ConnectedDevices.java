package org.caesar.userservice.Data.Entities;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name ="dispositivi_connessi")
public class ConnectedDevices {

    @Id
    //@GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "nome")
    private String name;

    @Column(name = "data_collegamento")
    private LocalDate connectionDate;

    @ManyToOne
    @JoinColumn(name = "id_utente")
    private User user;
}
