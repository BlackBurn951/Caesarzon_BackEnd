package org.caesar.userservice.Data.Entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name ="dispositivi_connessi")
public class ConnectedDevices {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "nome")
    private String name;

    @Column(name = "data_collegamento")
    private LocalDate connectionDate;

    @Column(name = "username_utente")
    private String userUsername;
}
