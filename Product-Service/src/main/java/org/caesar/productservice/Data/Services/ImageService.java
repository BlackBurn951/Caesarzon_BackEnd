package org.caesar.productservice.Data.Services;

import org.caesar.productservice.Dto.ImageDTO;
import org.caesar.productservice.Dto.ProductDTO;

public interface ImageService {


    byte[] getImage(ProductDTO prod);
    ImageDTO findImage(ProductDTO product);
    boolean createImage(ProductDTO prod);
    boolean updateImage(ImageDTO image, boolean isNew);
    boolean deleteImage(ProductDTO product);
}
