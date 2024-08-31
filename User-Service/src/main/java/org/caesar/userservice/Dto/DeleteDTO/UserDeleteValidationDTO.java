package org.caesar.userservice.Dto.DeleteDTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserDeleteValidationDTO {
    private List<WishlistDTO> wishlists;
    private List<WishListProductDTO> wishlistProduct;
    private List<ReviewDTO> review;
    private List<ProductOrderDTO>  productOrder;
    private List<OrderDTO> orders;
}
