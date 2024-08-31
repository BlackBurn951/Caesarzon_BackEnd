package org.caesar.notificationservice.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ValidateUserDeleteDTO {
    private List<SaveAdminNotificationDTO> adminNotificationForReport;  //0 -> presenti e validate 1 -> presenti ed errore 2 -> non presenti
    private List<SaveAdminNotificationDTO> adminNotificationForSupport;
    private List<ReportDTO> reports;
    private List<SupportDTO> supports;
    private List<UserNotificationDTO> userNotification;
}
