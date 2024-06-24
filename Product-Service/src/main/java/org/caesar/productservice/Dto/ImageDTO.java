package org.caesar.productservice.Dto;

import lombok.Getter;
import lombok.Setter;
import org.caesar.productservice.Data.Entities.Product;

@Setter
@Getter
public class ImageDTO {

    private byte[] image;
    private Product idProduct;
}
