package org.caesar.productservice.Data.Services.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.productservice.Data.Dao.AvailabilityRepository;
import org.caesar.productservice.Data.Entities.Availability;
import org.caesar.productservice.Data.Entities.Product;
import org.caesar.productservice.Data.Services.AvailabilityService;
import org.caesar.productservice.Dto.AvailabilityDTO;
import org.caesar.productservice.Dto.ProductDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AvailabilityServiceImpl implements AvailabilityService {

    private final ModelMapper modelMapper;
    private final AvailabilityRepository availabilityRepository;

    //fare la modifica delle disponibilità e eventuale eliminazione
    @Override
    public boolean addOrUpdateAvailability(List<AvailabilityDTO> availabilities, ProductDTO product) {
        if (availabilities.isEmpty()) {
            return false;
        }
        for (AvailabilityDTO availability : availabilities) {
            if(checkQuantity(availability.getAmount()) && checkSize(availability.getSize())) {
                Availability myAvailability = modelMapper.map(availability, Availability.class);
                myAvailability.setProduct(modelMapper.map(product, Product.class));
                availabilityRepository.save(myAvailability);
            }
        }
        return true;
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

    @Override
    public boolean deleteAvailabilityByProduct(Product product) {
        System.out.println("Sono nell'elimina della disponibilità");
        List<Availability> availabilitiesToDelete = new ArrayList<>();
        for(Availability availability : availabilityRepository.findAll()) {
            if(availability.getProduct().equals(product)){
                System.out.println("Disponibilità trovata");
                availabilitiesToDelete.add(availability);
            }
        }
        if(!availabilitiesToDelete.isEmpty()) {
            availabilityRepository.deleteAll(availabilitiesToDelete);
            return true;
        }else
            return false;
		
    public List<Availability> getAll() {
        return availabilityRepository.findAll();
    }


    private boolean checkSize(String size) {
        if(size==null)
            return true;
        List<String> sizes = List.of(new String[]{"XS", "S", "M", "L", "XL"});
        return sizes.contains(size);
    }

    private boolean checkQuantity(int quantity) {
        return quantity >= 0;
    }
}
