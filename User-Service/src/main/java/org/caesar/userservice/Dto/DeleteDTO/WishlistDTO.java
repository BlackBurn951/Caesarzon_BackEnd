package org.caesar.userservice.Dto.DeleteDTO;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class WishlistDTO {

    private UUID id;
    private String name;
    private String visibility;
    private String userUsername;
}
