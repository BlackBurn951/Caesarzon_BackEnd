package org.caesar.notificationservice.Data.Entities;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table( name = "richiesta_supporto")
public class SupportRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tipo")
    private String type;

    @Column(name = "testo")
    private String text;

    @Column(name = "oggetto")
    private String subject;

    @Column(name = "data_richiesta")
    private LocalDate dateRequest;

}
