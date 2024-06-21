package org.caesar.notificationservice.Data.Entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "notificheadmin")
public class AdminNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "data", nullable = false)
    private LocalDate date;

    @Basic
    @Column(name = "descrizione")
    private String subject;

    @Column(name= "username_admin")
    private String admin;

    @Column(name= "letta")
    private boolean read;

    @Column(name= "id_segnalazione")
    private UUID reportId;
}

