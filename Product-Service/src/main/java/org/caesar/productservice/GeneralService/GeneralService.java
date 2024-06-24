package org.caesar.productservice.GeneralService;

import org.caesar.productservice.Data.Entities.Availability;
import org.caesar.productservice.Dto.SendProductDTO;

import java.util.List;
import java.util.UUID;

public interface GeneralService {


    boolean addProduct(SendProductDTO sendProductDTO);
    boolean deleteProduct(UUID id);
    List<Availability> getAvailabilityByProductID(UUID productID);
    List<String> getAllProductImages(UUID productID);

}
