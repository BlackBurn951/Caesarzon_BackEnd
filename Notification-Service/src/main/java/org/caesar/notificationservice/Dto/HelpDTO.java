package org.caesar.notificationservice.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HelpDTO {

    private String motivo;
    private String oggetto;
    private String descrizione;
    private String dataRichiesta;
}