package org.caesar.productservice.Dto;

import lombok.Getter;
import lombok.Setter;
import org.caesar.productservice.Dto.DTOOrder.OrderDTO;

import java.util.List;

@Getter
@Setter
public class UserDeleteValidationDTO {
    private List<WishlistDTO> wishlists;
    private boolean wishlistProduct;
    private boolean review;
    private boolean productOrder;
    private List<OrderDTO> orders;
}
