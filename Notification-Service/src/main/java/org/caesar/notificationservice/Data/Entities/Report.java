package org.caesar.notificationservice.Data.Entities;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "segnala")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private Long id;

    @Column(name = "data_segnalazione")
    private LocalDate reportDate;

    @Column(name = "motivo")
    private String reason;

    @Column(name = "id_utente1")
    private Long userIdOne;

    @Column(name = "id_utente2")
    private Long userIdTwo;
}
