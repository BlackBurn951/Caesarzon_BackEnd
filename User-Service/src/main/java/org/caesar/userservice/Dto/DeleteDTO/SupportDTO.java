package org.caesar.userservice.Dto.DeleteDTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class SupportDTO {

    private UUID id;
    private String username;
    private String type;
    private String subject;
    private String text;
    private LocalDate dateRequest;
}