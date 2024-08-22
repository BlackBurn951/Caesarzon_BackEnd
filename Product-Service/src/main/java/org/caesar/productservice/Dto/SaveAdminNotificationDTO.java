package org.caesar.productservice.Dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;


@Getter
@Setter
public class SaveAdminNotificationDTO {

    private UUID id;
    private LocalDate date;
    private String subject;
    private String admin;
    private boolean read;
    private ReportDTO report;
    private UUID support;
    private boolean confirmed;

}

