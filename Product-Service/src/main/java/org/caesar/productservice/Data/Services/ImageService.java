package org.caesar.productservice.Data.Services;

import org.caesar.productservice.Data.Entities.Image;
import org.caesar.productservice.Data.Entities.Product;
import org.caesar.productservice.Dto.ImageDTO;
import org.caesar.productservice.Dto.ImagesDTO;
import org.caesar.productservice.Dto.ProductDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ImageService {


    byte[] getImage(ProductDTO prod);
    ImageDTO findImage(ProductDTO product);
    boolean saveImage(ImageDTO image);
    boolean deleteImage(ProductDTO product);
}
