package org.caesar.userservice.Dto.DeleteDTO;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class WishListProductDTO {

    private UUID Id;
    private WishlistDTO wishlistDTO;
    private ProductDTO productDTO;
}
