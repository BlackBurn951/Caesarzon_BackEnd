package org.caesar.productservice.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
@Getter
@Setter
public class ProductSportDTO {
    private UUID id;
    private ProductDTO productId;
    private SportDTO sportID;
}
