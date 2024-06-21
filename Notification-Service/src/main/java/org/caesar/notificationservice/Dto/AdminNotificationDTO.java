package org.caesar.notificationservice.Dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class AdminNotificationDTO {
    private Long id;
    private LocalDate date;
    private String description;
    private String admin;
    private boolean read;
    private UUID reportId;
}

