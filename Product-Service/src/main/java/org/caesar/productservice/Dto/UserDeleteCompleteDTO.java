package org.caesar.productservice.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDeleteCompleteDTO {
    private boolean wishlists;
    private boolean wishlistProduct;
    private boolean reviews;
    private boolean productOrder;
    private boolean orders;
}
