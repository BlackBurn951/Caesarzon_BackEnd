package org.caesar.notificationservice.Data.Entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "segnala")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "data_segnalazione")
    private LocalDate reportDate;

    @Column(name = "motivo")
    private String reason;

    @Column(name = "username_utente1")
    private String usernameUser1;

    @Column(name = "username_utente2")
    private String usernameUser2;
}
