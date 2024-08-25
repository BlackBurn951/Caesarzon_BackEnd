package org.caesar.productservice.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserDeleteCompleteDTO {
    private boolean wishlists;
    private List<WishListProductDTO> wishlistProduct;
    private List<ReviewDTO> reviews;
    private List<ProductOrderDTO> productOrder;
    private boolean orders;
}
