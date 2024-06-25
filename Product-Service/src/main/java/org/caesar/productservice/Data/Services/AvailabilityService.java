package org.caesar.productservice.Data.Services;

import org.caesar.productservice.Data.Entities.Availability;
import org.caesar.productservice.Data.Entities.Product;
import org.caesar.productservice.Dto.AvailabilityDTO;
import org.caesar.productservice.Dto.ProductDTO;

import java.util.List;
import java.util.UUID;

public interface AvailabilityService {

    boolean addOrUpdateAvailability(List<AvailabilityDTO> availabilities, ProductDTO product);
    boolean deleteAvailability(UUID id);
    boolean deleteAvailabilityByProduct(Product product);
    List<Availability> getAll();
    List<AvailabilityDTO> getAvailabilitiesByProductID(UUID productId);
}
