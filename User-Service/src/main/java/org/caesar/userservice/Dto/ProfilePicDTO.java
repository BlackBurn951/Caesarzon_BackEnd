package org.caesar.userservice.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ProfilePicDTO {

    private UUID id;
    private String userId;
    private byte[] profilePic;

}
