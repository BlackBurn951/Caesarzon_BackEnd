package org.caesar.notificationservice.Data.Entities;

import jakarta.persistence.*;

@Entity
@Table(name= "supporto_admin_utente")
public class AdminUserSupport {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "username_utente")
    private String user;

    @Column(name = "username_admin")
    private String admin;
}
