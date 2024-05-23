package org.caesar.notificationservice.Data.Entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notificheadmin")
public class AdminNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "Id", nullable = false)
    private Long id;

    @Basic
    @Column(name = "Data", nullable = false)
    private LocalDateTime data;

    @Basic
    @Column(name = "Descrizione")
    private String description;

    @ManyToOne
    @JoinColumn(name = "idAdmin", referencedColumnName = "id", nullable = false)
    private AdminNotification admin;
}

