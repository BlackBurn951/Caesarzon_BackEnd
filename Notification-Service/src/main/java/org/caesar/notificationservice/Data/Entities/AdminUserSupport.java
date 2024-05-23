package org.caesar.notificationservice.Data.Entities;

import jakarta.persistence.*;

@Entity
@Table(name= "supporto_admin_utente")
public class AdminUserSupport {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private Long id;

    @Column(name = "id_utente")
    private Long UserId;

    @Column(name = "id_admin")
    private Long AdminId;
}
