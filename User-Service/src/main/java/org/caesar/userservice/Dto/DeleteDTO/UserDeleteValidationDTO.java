package org.caesar.userservice.Dto.DeleteDTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserDeleteValidationDTO {
    private List<WishlistDTO> wishlists;
    private int wishlistProduct;
    private int review;
    private int productOrder;
    private List<OrderDTO> orders;
}
