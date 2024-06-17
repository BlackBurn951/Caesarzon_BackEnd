package org.caesar.notificationservice.Data.Entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notificheadmin")
public class AdminNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "data", nullable = false)
    private LocalDateTime data;

    @Basic
    @Column(name = "descrizione")
    private String description;

    @Column(name= "username_admin")
    private String admin;
}

