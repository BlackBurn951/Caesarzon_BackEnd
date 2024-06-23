package org.caesar.productservice.Data.Services;

import org.caesar.productservice.Data.Entities.Image;
import org.caesar.productservice.Data.Entities.Product;
import org.caesar.productservice.Dto.ImageDTO;

import java.util.List;

public interface ImageService {

    boolean addOrUpdateImage(Product product, List<String> image);
    ImageDTO getImage(Product product);
    List<Image> getAllProductImages(Product product);
    boolean deleteImage(Product product);
}
