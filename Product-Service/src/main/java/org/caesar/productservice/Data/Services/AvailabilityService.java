package org.caesar.productservice.Data.Services;

import org.caesar.productservice.Data.Entities.Availability;
import org.caesar.productservice.Dto.AvailabilityDTO;

import java.util.List;
import java.util.UUID;

public interface AvailabilityService {

    List<Availability> addOrUpdateAvailability(List<AvailabilityDTO> availability);
    boolean deleteAvailability(UUID id);
    //List<AvailabilityDTO> getAvailabilitiesByProductID(UUID productId);
}
