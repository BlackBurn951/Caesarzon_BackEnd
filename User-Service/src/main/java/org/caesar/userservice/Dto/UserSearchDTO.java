package org.caesar.userservice.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSearchDTO {
    private String username;
    private byte[] profilePic;
    private boolean follower;
    private boolean friend;
}
