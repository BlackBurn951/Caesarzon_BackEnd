package org.caesar.productservice.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
public class WishlistProductDTO {
    private UUID Id;
    private UUID productId;
    private UUID WishlistProductId;
}
