package org.caesar.userservice.Dto;

import lombok.*;

@Getter
@Setter
public class TokenDTO {

    private String access_token;
    private String refresh_token;


}
