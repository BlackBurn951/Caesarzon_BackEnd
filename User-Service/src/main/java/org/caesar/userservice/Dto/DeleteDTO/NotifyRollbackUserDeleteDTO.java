package org.caesar.userservice.Dto.DeleteDTO;

import lombok.Getter;
import lombok.Setter;
import org.caesar.userservice.Dto.DeleteDTO.SaveAdminNotificationDTO;
import org.caesar.userservice.Dto.DeleteDTO.SupportDTO;

import java.util.List;

@Getter
@Setter
public class NotifyRollbackUserDeleteDTO {
    private List<ReportDTO> reports;
    private List<SupportDTO> supports;
    private List<SaveAdminNotificationDTO> adminNotification;
    private List<UserNotificationDTO> userNotification;
}
