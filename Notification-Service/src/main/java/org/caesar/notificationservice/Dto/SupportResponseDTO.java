package org.caesar.notificationservice.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class SupportResponseDTO {
    private UUID supportCode;
    private String explain;
}
