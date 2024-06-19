package org.caesar.notificationservice.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportResponseDTO {
    private String reportCode;
    private boolean accept;
    private String explain;
}
