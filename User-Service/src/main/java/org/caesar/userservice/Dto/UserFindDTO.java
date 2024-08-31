package org.caesar.userservice.Dto;

import lombok.Getter;
import lombok.Setter;
import org.bouncycastle.oer.its.etsi102941.Url;

@Getter
@Setter
public class UserFindDTO {
    private String username;
    private byte[] profilePic;
}
