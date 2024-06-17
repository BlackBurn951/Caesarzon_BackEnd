package org.caesar.notificationservice.Data.Entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "notificheutente")
public class UserNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "data", nullable = false)
    private LocalDate date;

    @Column(name = "descrizione")
    private String description;

    @Column(name= "username_utente")
    private String user;

    @Column(name= "letta")
    private boolean read;
}
