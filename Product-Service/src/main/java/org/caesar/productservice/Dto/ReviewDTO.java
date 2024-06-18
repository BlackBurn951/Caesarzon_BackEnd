package org.caesar.productservice.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class ReviewDTO {

    private UUID id;
    private UUID productId;
    private String reviewText;
    private Date reviewDate;
    private int reviewRating;
    private String reviewAuthorUsername;
}
