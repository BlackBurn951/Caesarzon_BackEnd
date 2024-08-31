package org.caesar.productservice.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Vector;

@Getter
@Setter
public class DeleteReviewDTO {
    private List<ReportDTO> reports;
    private List<SaveAdminNotificationDTO> adminNotify;

    public DeleteReviewDTO() {
        reports= new Vector<>();
        adminNotify= new Vector<>();
    }
}
