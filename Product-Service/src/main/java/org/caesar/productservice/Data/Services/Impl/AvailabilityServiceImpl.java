package org.caesar.productservice.Data.Services.Impl;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.productservice.Data.Dao.AvailabilityRepository;
import org.caesar.productservice.Data.Entities.Availability;
import org.caesar.productservice.Data.Services.AvailabilityService;
import org.caesar.productservice.Dto.AvailabilityDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AvailabilityServiceImpl implements AvailabilityService {

    private final ModelMapper modelMapper;
    private final AvailabilityRepository availabilityRepository;


    //Ritorna le liste di disponibilità del prodotto
    @Override
    public boolean addOrUpdateAvailability(List<AvailabilityDTO> availabilitiesDTO) {
        System.out.println("Sono nell'add or update della disponibilità");
        try{
            for(AvailabilityDTO availabilityDTO : availabilitiesDTO)
                if(checkQuantity(availabilityDTO.getAmount()) && checkSize(availabilityDTO.getSize())) {
                    availabilityRepository.save(modelMapper.map(availabilityDTO, Availability.class));
                    log.debug("Disponibilità del prodotto aggiunta");
                }
                else
                    log.debug("Errore nell'inserimento della disponibilità");
            return true;

        }catch (RuntimeException e){
            log.debug("Errore nell'inserimento della disponibilità: {}", e.getMessage(), e);
            return false;
        }

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



    private boolean checkSize(String size) {
        List<String> sizes = List.of(new String[]{"XS", "S", "M", "L", "XL", "XXL"});
        return sizes.contains(size);
    }

    private boolean checkQuantity(int quantity) {
        return quantity > 0;
    }
}
