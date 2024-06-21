package org.caesar.notificationservice.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class NotificationDTO {
    private UUID id;
    private String date;
    private String subject;
    private String explanation;
    private boolean read;
}
