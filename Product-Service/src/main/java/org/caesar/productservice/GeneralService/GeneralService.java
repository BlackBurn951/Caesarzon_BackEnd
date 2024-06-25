package org.caesar.productservice.GeneralService;

import org.caesar.productservice.Data.Entities.Availability;
import org.caesar.productservice.Dto.ImageDTO;
import org.caesar.productservice.Dto.ProductDTO;

import java.util.List;
import java.util.UUID;

public interface GeneralService {


    boolean addProduct(ProductDTO ProductDTO);
    boolean deleteProduct(UUID id);
    List<ImageDTO> getProductImages(UUID id);
    List<Availability> getAvailabilityByProductID(UUID productID);
    List<ImageDTO> getAllProductImages(UUID productID);

}
