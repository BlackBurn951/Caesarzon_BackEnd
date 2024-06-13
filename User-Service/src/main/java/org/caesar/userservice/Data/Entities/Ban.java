package org.caesar.userservice.Data.Entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name ="ban")
public class Ban {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "motivo")
    private String reason;

    @Column(name = "data_inizio")
    private LocalDate startDate;

    @Column(name = "data_fine")
    private LocalDate endDate;

    @Column(name= "username_utente")
    private String userUsername;

    @Column(name= "username_admin")
    private String adminUsername;

}
