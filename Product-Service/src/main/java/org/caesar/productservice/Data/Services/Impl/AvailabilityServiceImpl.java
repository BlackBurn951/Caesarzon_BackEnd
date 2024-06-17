package org.caesar.productservice.Data.Services.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.productservice.Data.Dao.AvailabilityRepository;
import org.caesar.productservice.Data.Services.AvailabilityService;
import org.caesar.productservice.Dto.AvailabilityDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j

public class AvailabilityServiceImpl implements AvailabilityService {

    private final ModelMapper modelMapper;
    private final AvailabilityRepository availabilityRepository;


    @Override
    public boolean addOrUpdateAvailability(AvailabilityDTO availability) {

        return false;
    }

    @Override
    public AvailabilityDTO getAvailability(UUID id) {

        return modelMapper.map(availabilityRepository.findById(id), AvailabilityDTO.class);
    }

    @Override
    public boolean deleteAvailability(UUID id) {
        try {
            availabilityRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            log.debug("Errore nella cancellazione della disponibilità");
            return false;
        }
    }
}
