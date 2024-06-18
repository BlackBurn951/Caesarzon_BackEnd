package org.caesar.notificationservice.Dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class ReportDTO {

    private UUID id;
    private LocalDate reportDate;
    private String reason;
    private String description;
    private String usernameUser1;
    private String usernameUser2;
}
