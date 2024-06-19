package org.caesar.notificationservice.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BanDTO {

    private String motivo;
    private String dataInizio;
    private String dataFine;
    private String usernameUtenteBannato;
    private String usernameAdmin;
}
