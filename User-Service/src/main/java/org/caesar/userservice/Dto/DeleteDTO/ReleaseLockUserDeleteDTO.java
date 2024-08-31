package org.caesar.userservice.Dto.DeleteDTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ReleaseLockUserDeleteDTO {
    private List<UUID> adminNotification;
    private List<UUID> userNotification;
    private List<UUID> reportId;
}
