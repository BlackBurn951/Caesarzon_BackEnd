package org.caesar.authservice.Config;

import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;

import jakarta.servlet.http.HttpServletRequest;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

public abstract class JwtUtil {

    //public static final long EXPIRATION_TIME = 864_000; // 10 days
    //public static final long EXPIRATION_TIME = 36_000; // 10 hours
    //public static final long EXPIRATION_TIME = 3_600;// 1 hour
    //public static final long EXPIRATION_TIME = 600; // 10 minutes

    public static final long EXPIRATION_TIME = 3_600; // 1 minutes
    public static final long EXPIRATION_REFRESH_TOKEN_TIME = 36_000; // 1 minutes
    public static final String BEARER_TOKEN_PREFIX = "Bearer ";
    public static final String BASIC_TOKEN_PREFIX =  "Basic ";
    public static final String LOGIN_URI_ENDING = "/login";
    public static final String REFRESH_TOKEN_URI_ENDING = "/refreshToken";

    public static final String JWT_SECRET = "t3pCSx2wx1ExbQ5z43XXB8my/KR24aon4EH/niU9iZi1I3S69rk1QhlMFFsTrZIY";
    private static SecretKey SECRET = new SecretKeySpec(Base64.getDecoder().decode(JWT_SECRET), "HmacSHA256");
    public static final String AUTHORIZATION = "Authorization";



//    public static LoggedUserToken extractToken(HttpServletRequest request) throws Exception {
//        return parseToken(request.getHeader(AUTHORIZATION).replace(BEARER_TOKEN_PREFIX, ""));
//    }

    public static String parseToken(String token) throws Exception {
        JwtConsumer consumer = new JwtConsumerBuilder()
                .setSkipAllValidators()
                .setDisableRequireSignature()
                .setSkipSignatureVerification()
                .build();
        JwtClaims claims = consumer.processToClaims(token);
        List<String> tokens = new ArrayList<>();
        tokens.add(claims.getRawJson());

        System.out.println("* Parsed token: "+ claims.getRawJson() );
        System.out.println("* Roles: "+ claims.getClaimValue("roles").toString());
        System.out.println("* Expiration date: " + new Date(claims.getExpirationTime().getValueInMillis()) );

        return claims.getRawJson();
    }

    public static String[] decodedBase64(String token) {

        byte[] decodedBytes = Base64.getDecoder().decode(token);
        String pairedCredentials = new String(decodedBytes);
        String[] credentials = pairedCredentials.split(":", 2);

        return credentials;
    }
}