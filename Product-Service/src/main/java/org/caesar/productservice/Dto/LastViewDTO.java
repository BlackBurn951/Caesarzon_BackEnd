package org.caesar.productservice.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class LastViewDTO {
    UUID id;
    ProductDTO product;
    String username;
}
