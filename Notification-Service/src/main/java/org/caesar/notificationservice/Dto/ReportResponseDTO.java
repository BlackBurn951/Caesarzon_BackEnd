package org.caesar.notificationservice.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ReportResponseDTO {
    private UUID reportCode;
    private boolean accept;
    private String explain;
    private UUID reviewId;
}
