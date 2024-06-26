package org.caesar.productservice.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class WishProductDTO {

    private String visibility;
    private List<SingleWishListProductDTO> singleWishListProductDTOS;
}
