package org.caesar.notificationservice.Dto;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class UserNotificationDTO {
    private UUID id;
    private LocalDate date;
    private String subject;
    private String user;
    private boolean read;
    private String explanation;
}

