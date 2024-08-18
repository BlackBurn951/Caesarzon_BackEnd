package org.caesar.notificationservice.Data.Entities;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table( name = "richiesta_supporto")
@Getter
public class Support {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tipo")
    private String type;

    @Column(name = "username")
    private String username;

    @Column(name = "testo")
    private String text;

    @Column(name = "oggetto")
    private String subject;

    @Column(name = "data_richiesta")
    private LocalDate dateRequest;

}
