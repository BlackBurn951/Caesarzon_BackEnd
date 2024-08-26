package org.caesar.userservice.Dto.DeleteDTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ValidateUserDeleteDTO {
    private int adminNotificationForReport;  //0 -> presenti e validate 1 -> presenti ed errore 2 -> non presenti
    private int adminNotificationForSupport;
    private List<ReportDTO> reports;
    private List<SupportDTO> supports;
    private boolean userNotification;
}
