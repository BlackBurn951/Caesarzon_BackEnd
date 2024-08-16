package org.caesar.userservice.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class FollowerDTO {
    private UUID id;
    private String userUsername1;
    private String userUsername2;
    private boolean friend;
    private boolean onDeleting;
}
