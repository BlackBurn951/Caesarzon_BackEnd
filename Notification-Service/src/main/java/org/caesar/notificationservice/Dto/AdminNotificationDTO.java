package org.caesar.notificationservice.Dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class AdminNotificationDTO {
    private UUID id;
    private LocalDate date;
    private String subject;
    private String admin;
    private boolean read;
    private UUID reportId;
}

