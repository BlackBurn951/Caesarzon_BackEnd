package org.caesar.notificationservice.Dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BanDTO {

    private String reason;
    private LocalDate startDate;
    private LocalDate endDate;
    private String userUsername;
    private String adminUsername;
    private boolean confirmed;
}
