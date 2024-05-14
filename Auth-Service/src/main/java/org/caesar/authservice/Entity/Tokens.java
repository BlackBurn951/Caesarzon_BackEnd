package org.caesar.authservice.Entity;

import lombok.*;

@Getter
@Setter
public class Tokens {

    String access;
    String refresh;
    String _csrf;
}
