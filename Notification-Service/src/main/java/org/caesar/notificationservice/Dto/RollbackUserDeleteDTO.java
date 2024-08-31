package org.caesar.notificationservice.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RollbackUserDeleteDTO {
    private List<ReportDTO> reports;
    private List<SupportDTO> supports;
    private List<SaveAdminNotificationDTO> adminNotification;
    private List<UserNotificationDTO> userNotification;
}
