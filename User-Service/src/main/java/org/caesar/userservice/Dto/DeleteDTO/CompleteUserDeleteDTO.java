package org.caesar.userservice.Dto.DeleteDTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CompleteUserDeleteDTO {
    private List<SaveAdminNotificationDTO> adminNotification;
    private List<UserNotificationDTO> userNotification;
    private boolean report;
    private boolean support;
}
