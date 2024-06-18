package org.caesar.productservice.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ImageDTO {

    private UUID id;
    private UUID productId;
    private String image;
}
