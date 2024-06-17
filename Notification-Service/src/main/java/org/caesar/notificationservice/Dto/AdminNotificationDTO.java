package org.caesar.notificationservice.Dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AdminNotificationDTO {
    private Long id;
    private LocalDate data;
    private String description;
    private String admin;
    private boolean read;
}

