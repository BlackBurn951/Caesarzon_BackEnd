package org.caesar.productservice.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class SingleWishListProductDTO {

    private UUID productId;
    private String productName;
    private double price;



}
