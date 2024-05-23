package org.caesar.notificationservice.Dto;

import java.time.LocalDateTime;

public class AdminNotificationDTO {
    private Long id;
    private LocalDateTime data;
    private String descrizione;
    private Long idAdmin;

    // Costruttore vuoto
    public AdminNotificationDTO() {
    }

    // Costruttore con parametri per creare un DTO a partire da un'istanza di AdminNotification
    public AdminNotificationDTO(Long id, LocalDateTime data, String descrizione, Long idAdmin) {
        this.id = id;
        this.data = data;
        this.descrizione = descrizione;
        this.idAdmin = idAdmin;
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

    public Long getIdAdmin() {
        return idAdmin;
    }

    public void setIdAdmin(Long idAdmin) {
        this.idAdmin = idAdmin;
    }
}

