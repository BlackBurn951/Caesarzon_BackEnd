package org.caesar.productservice.Data.Services;

import org.caesar.productservice.Data.Entities.Availability;
import org.caesar.productservice.Data.Entities.Product;
import org.caesar.productservice.Dto.AvailabilityDTO;
import org.caesar.productservice.Dto.ProductDTO;

import java.util.List;
import java.util.UUID;

public interface AvailabilityService {

    boolean validateAvailability(List<AvailabilityDTO> availability);
    boolean completeAvailability(List<AvailabilityDTO> availability);
    boolean releaseLockAvailability(List<AvailabilityDTO> availability);
    boolean rollbackAvailability(List<AvailabilityDTO> availability, boolean validate);


    boolean addOrUpdateAvailability(List<AvailabilityDTO> availabilities, ProductDTO product);
    boolean deleteAvailabilityByProduct(ProductDTO product);
    List<AvailabilityDTO> getAvailabilitiesByProduct(ProductDTO productDTO);
    AvailabilityDTO getAvailabilitieByProductId(ProductDTO productDTO, String size);
}
