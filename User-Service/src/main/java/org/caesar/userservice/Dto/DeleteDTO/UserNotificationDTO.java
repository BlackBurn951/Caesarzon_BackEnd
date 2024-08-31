package org.caesar.userservice.Dto.DeleteDTO;


import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UserNotificationDTO {
    private UUID id;
    private String date;
    private String subject;
    private String user;
    private boolean read;
    private String explanation;
}

