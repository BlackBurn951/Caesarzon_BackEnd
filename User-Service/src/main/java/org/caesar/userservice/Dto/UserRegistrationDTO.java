package org.caesar.userservice.Dto;

import lombok.*;

@Getter
@Setter
public class UserRegistrationDTO {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String credentialValue;
}
