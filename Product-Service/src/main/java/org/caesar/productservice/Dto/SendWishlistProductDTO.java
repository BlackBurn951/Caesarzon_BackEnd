package org.caesar.productservice.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class SendWishlistProductDTO {

    private UUID productID;
    private UUID wishlistID  ;
}
