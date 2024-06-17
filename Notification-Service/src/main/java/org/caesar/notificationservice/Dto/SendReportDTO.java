package org.caesar.notificationservice.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendReportDTO {

    private String motivo;
    private String descrizione;
    private String dataSegnalazione;
    private String usernameUser2;
}