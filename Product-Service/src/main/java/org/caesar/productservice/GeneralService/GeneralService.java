package org.caesar.productservice.GeneralService;

import org.caesar.productservice.Data.Entities.Availability;
import org.caesar.productservice.Dto.ImageDTO;
import org.caesar.productservice.Dto.SendProductDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface GeneralService {


    boolean addProduct(SendProductDTO sendProductDTO);
    boolean deleteProduct(UUID id);
    List<ImageDTO> getProductImages(UUID id);
    List<Availability> getAvailabilityByProductID(UUID productID);
    List<ImageDTO> getAllProductImages(UUID productID);

}
