package org.caesar.userservice.Dto.DeleteDTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserDeleteCompleteDTO {
    private boolean wishlists;
    private boolean wishlistProduct;
    private boolean reviews;
    private boolean productOrder;
    private boolean orders;
}
