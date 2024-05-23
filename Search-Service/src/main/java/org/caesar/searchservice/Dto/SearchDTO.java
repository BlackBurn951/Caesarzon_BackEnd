package org.caesar.searchservice.Dto;

public class SearchDTO {
    private Long id;
    private String testo;
    private Long idUser;

    // Costruttore vuoto
    public SearchDTO() {
    }

    // Costruttore con parametri per creare un DTO a partire da un'istanza di Search
    public SearchDTO(Long id, String testo, Long idUser) {
        this.id = id;
        this.testo = testo;
        this.idUser = idUser;
    }

    // Metodi getter e setter per accedere ai campi privati
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }
}
