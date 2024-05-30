package org.caesar.userservice.Dto;

import lombok.*;

@Getter
@Setter
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private boolean enabled;
    private String credentialType;
    private String credentialValue;
}
