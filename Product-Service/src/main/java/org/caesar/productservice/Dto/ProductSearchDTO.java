package org.caesar.productservice.Dto;


import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ProductSearchDTO {

//  private image

    private String productName;

    private UUID productId;

    private double averageReview;

    private int reviewsNumber;

    private double price;

    private int discount;
}
