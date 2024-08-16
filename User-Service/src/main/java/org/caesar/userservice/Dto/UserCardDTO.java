package org.caesar.userservice.Dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
public class UserCardDTO {

    private UUID id;
    private String userUsername;
    private UUID cardId;
    private boolean onDeleting;
}
