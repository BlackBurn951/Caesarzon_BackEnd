package org.caesar.productservice.Dto;

import lombok.Getter;
import lombok.Setter;
import org.caesar.productservice.Dto.DTOOrder.OrderDTO;

import java.util.List;

@Getter
@Setter
public class RollbackUserDeleteDTO {
    private List<WishlistDTO> wishlists;
    private List<WishListProductDTO> wishListProducts;
    private List<ReviewDTO> reviews;
    private List<ProductOrderDTO> productOrders;
    private List<OrderDTO> orders;
}
