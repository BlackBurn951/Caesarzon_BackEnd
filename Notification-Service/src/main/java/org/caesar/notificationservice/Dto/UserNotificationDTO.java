package org.caesar.notificationservice.Dto;

import java.time.LocalDateTime;

public class UserNotificationDTO {
    private Long id;
    private LocalDateTime data;
    private String descrizione;
    private Long idUtente;

    // Costruttore vuoto
    public UserNotificationDTO() {
    }

    // Costruttore con parametri per creare un DTO a partire da un'istanza di AdminNotification
    public UserNotificationDTO(Long id, LocalDateTime data, String descrizione, Long idUtente) {
        this.id = id;
        this.data = data;
        this.descrizione = descrizione;
        this.idUtente = idUtente;
    }

    // Metodi getter e setter per accedere ai campi privati
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public Long getIdUtente() {
        return idUtente;
    }

    public void setIdUtente(Long idUtente) {
        this.idUtente = idUtente;
    }
}

