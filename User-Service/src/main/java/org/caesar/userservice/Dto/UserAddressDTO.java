package org.caesar.userservice.Dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
public class UserAddressDTO {

    private UUID id;
    private String userUsername;
    private UUID addressId;
    private boolean onDeleting;
}
