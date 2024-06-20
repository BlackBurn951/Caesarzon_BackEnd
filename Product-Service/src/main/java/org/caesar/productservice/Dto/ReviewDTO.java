package org.caesar.productservice.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class ReviewDTO {

    private String reviewText;
    private int reviewRating;
    private String reviewAuthorUsername;
    private String nameProduct;
}
