package org.caesar.userservice.Dto.DeleteDTO;

import lombok.Getter;
import lombok.Setter;
import org.caesar.notificationservice.Dto.SupportDTO;

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
    private SupportDTO support;
    private boolean confirmed;

}

