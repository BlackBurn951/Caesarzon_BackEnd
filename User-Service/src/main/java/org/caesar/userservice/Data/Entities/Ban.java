package org.caesar.userservice.Data.Entities;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name ="ban")
public class Ban {

    @Id
    //@GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "motivo")
    private String reason;

    @Column(name = "data_inizio")
    private LocalDate startDate;

    @Column(name = "data_fine")
    private LocalDate endDate;

    @Column(name= "id_utente")
    private String userId;

    @Column(name= "id_admin")
    private String adminId;

}
