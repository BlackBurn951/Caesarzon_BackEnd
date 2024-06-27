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

    @Override
    // Aggiunge tuple o modifica la tabella delle disponibilità
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
    // Elimina una disponibilità dal db tramite il suo id
    public boolean deleteAvailability(UUID availabilityId) {
        try {
            availabilityRepository.deleteById(availabilityId);
            return true;
        } catch (Exception e) {
            log.debug("Errore nella cancellazione della disponibilità");
            return false;
        }
    }

    @Override
    // Elimina tutte le disponibilità di un determinato prodotto
    public boolean deleteAvailabilityByProduct(Product product) {
        List<Availability> availabilitiesToDelete = new ArrayList<>();
        for (Availability availability : availabilityRepository.findAll()) {
            if (availability.getProduct().equals(product)) {
                availabilitiesToDelete.add(availability);
            }
        }
        if (!availabilitiesToDelete.isEmpty()) {
            availabilityRepository.deleteAll(availabilitiesToDelete);
            return true;
        } else
            return false;

    }

    @Override
    // Restituisce tutte le disponibilità registrate nel db
    public List<Availability> getAll() {
        return availabilityRepository.findAll();
    }

    @Override
    // Resituisce tutte le disponibilità di un determinato prodotto
    public List<AvailabilityDTO> getAvailabilitiesByProductID(ProductDTO productDTO) {
        return availabilityRepository.findAllByProduct(modelMapper.map(productDTO, Product.class))
                .stream().map(a -> modelMapper.map(a, AvailabilityDTO.class)).toList();

    }

    @Override
    public AvailabilityDTO getAvailabilitieByProductId(ProductDTO productDTO, String size) {
        AvailabilityDTO availabilityDTO1 = new AvailabilityDTO();

        AvailabilityDTO availabilityDTO = modelMapper.map(availabilityRepository.findByProductAndSize
                (modelMapper.map(productDTO, Product.class), size), AvailabilityDTO.class);

        availabilityDTO1.setAmount(availabilityDTO.getAmount());
        availabilityDTO1.setSize(availabilityDTO.getSize());

        return availabilityDTO1;
    }

    // Controllo della taglia del prodotto
    private boolean checkSize(String size) {
        if(size==null)
            return true;
        List<String> sizes = List.of(new String[]{"XS", "S", "M", "L", "XL"});
        return sizes.contains(size);
    }

    // Controllo della quantità del prodotto
    private boolean checkQuantity(int quantity) {
        return quantity >= 0;
    }
}
