package org.caesar.notificationservice.Data.Entities;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "notificheutente")
public class UserNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "Id", nullable = false)
    private Long id;

    @Basic
    @Column(name = "Data", nullable = false)
    private LocalDate date;

    @Basic
    @Column(name = "Descrizione")
    private String description;

    @ManyToOne
    @JoinColumn(name = "idUtente", referencedColumnName = "id", nullable = false)
    private AdminNotification admin;
}
