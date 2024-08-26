package org.caesar.userservice.Dto.DeleteDTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductRollbackUserDeleteDTO {
    private List<WishlistDTO> wishlists;
    private List<WishListProductDTO> wishListProducts;
    private List<ReviewDTO> reviews;
    private List<ProductOrderDTO> productOrders;
    private List<OrderDTO> orders;
}
