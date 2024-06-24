package org.caesar.productservice.Dto;

import lombok.Getter;
import lombok.Setter;
import org.caesar.productservice.Data.Entities.Product;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
public class ImageDTO {

    private byte[] file;
    private Product idProduct;

    public ImageDTO(byte[] image, Product idProduct) {
        this.file = image;
        this.idProduct = idProduct;
    }
}
