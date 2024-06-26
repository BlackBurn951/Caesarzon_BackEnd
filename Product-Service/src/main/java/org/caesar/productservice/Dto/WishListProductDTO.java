package org.caesar.productservice.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class WishListProductDTO {

    private UUID id;
    private WishlistDTO wishlistDTO;
    private ProductDTO productDTO;
}
