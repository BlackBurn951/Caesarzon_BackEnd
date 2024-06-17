package org.caesar.productservice.Data.Services;

import org.caesar.productservice.Data.Entities.Availability;
import org.caesar.productservice.Dto.AvailabilityDTO;

import java.util.UUID;

public interface AvailabilityService {

    UUID addOrUpdateAvailability(AvailabilityDTO availability);
    AvailabilityDTO getAvailability(UUID id);
    boolean deleteAvailability(UUID id);
}
