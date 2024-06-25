package org.caesar.productservice.Dto;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductSearchDTO {

//    private image
    private String productName;

    private double averageReview;

    private int reviewsNumber;

    private double price;

}
