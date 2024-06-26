package org.caesar.productservice.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ReviewDTO {

    private UUID id;
    private String text;
    private int evaluation;
    private String username;
    private UUID productID;
}
