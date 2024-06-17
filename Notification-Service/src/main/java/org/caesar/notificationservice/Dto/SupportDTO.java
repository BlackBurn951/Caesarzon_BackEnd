package org.caesar.notificationservice.Dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SupportDTO {

    private String username;
    private String type;
    private String subject;
    private String text;
    private LocalDate dateRequest;
}