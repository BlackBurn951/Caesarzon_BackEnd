package org.caesar.productservice.Dto;

import lombok.Getter;
import lombok.Setter;
import org.caesar.productservice.Dto.DTOOrder.OrderDTO;

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
