package org.caesar.userservice.Dto;

import lombok.Getter;
import lombok.Setter;

import java.net.URL;

@Getter
@Setter
public class UserSearchDTO {
    private String username;
    private boolean follower;
    private boolean friend;
}
