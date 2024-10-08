package org.caesar.notificationservice.Data.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table( name = "richiesta_supporto")
@Getter
@Setter
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

    @Column(name= "in_cancellazione")
    private boolean onDeleting;
}
