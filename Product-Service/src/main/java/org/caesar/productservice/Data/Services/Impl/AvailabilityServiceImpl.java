package org.caesar.productservice.Data.Services.Impl;

import org.caesar.productservice.Data.Services.AvailabilityService;
import org.caesar.productservice.Dto.AvailabilityDTO;

import java.util.UUID;

public class AvailabilityServiceImpl implements AvailabilityService {

    @Override
    public boolean addOrUpdateAvailability(AvailabilityDTO availability) {
        return false;
    }

    @Override
    public AvailabilityDTO getAvailability(UUID id) {
        return null;
    }

    @Override
    public boolean deleteAvailability(UUID id) {
        return false;
    }
}
