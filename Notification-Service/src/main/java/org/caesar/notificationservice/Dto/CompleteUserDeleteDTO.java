package org.caesar.notificationservice.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CompleteUserDeleteDTO {
    private boolean adminNotification;
    private boolean userNotification;
    private boolean report;
    private boolean support;

    public CompleteUserDeleteDTO() {
        support= false;
    }
}
