package org.caesar.notificationservice.Data.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "notificheadmin")
@Getter
@Setter
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

    @OneToOne
    @JoinColumn(name= "id_segnalazione")
    private Report report;

    @OneToOne
    @JoinColumn(name= "id_richiesta")
    private Support support;
}

