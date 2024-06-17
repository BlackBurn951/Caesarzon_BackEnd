package org.caesar.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {


    String message;

    public AuthResponse(String message) {
        this.message = message;
    }


    private String accessToken;
    private String refreshToken;

}


