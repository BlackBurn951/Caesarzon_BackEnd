package org.caesar.productservice.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class LastViewDTO {
    private UUID id;
    private ProductDTO product;
    private String username;
    private boolean onDeleting;
}
