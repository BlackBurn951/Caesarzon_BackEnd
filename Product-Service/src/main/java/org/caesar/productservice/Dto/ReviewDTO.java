package org.caesar.productservice.Dto;

import lombok.Getter;
import lombok.Setter;
import org.caesar.productservice.Data.Entities.Product;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class ReviewDTO {

    private UUID id;
    private String text;
    private int evaluation;
    private String userID;
    private UUID productID;
}
