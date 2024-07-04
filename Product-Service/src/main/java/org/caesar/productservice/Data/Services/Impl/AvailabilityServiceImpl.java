package org.caesar.productservice.Data.Services.Impl;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
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
    private final static String AVAILABILITY_SERVICE = "availabilityService";

    public String fallbackCircuitBreaker(CallNotPermittedException e){
        log.debug("Circuit breaker su availabilityService da: {}", e.getCausingCircuitBreakerName());
        return e.getMessage();
    }

    @Override // Aggiunge tuple o modifica la tabella delle disponibilità
//    @CircuitBreaker(name=AVAILABILITY_SERVICE, fallbackMethod = "fallbackCircuitBreaker")
//    @Retry(name=AVAILABILITY_SERVICE)
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
//    @CircuitBreaker(name=AVAILABILITY_SERVICE, fallbackMethod = "fallbackCircuitBreaker")
//    @Retry(name=AVAILABILITY_SERVICE)
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
//    @CircuitBreaker(name=AVAILABILITY_SERVICE, fallbackMethod = "fallbackCircuitBreaker")
//    @Retry(name=AVAILABILITY_SERVICE)
    public boolean deleteAvailabilityByProduct(Product product) {
        System.out.println("Sono nella funzione per eliminare le disponibilità");
        List<Availability> availabilitiesToDelete = new ArrayList<>();
        for (Availability availability : availabilityRepository.findAll()) {
            if (availability.getProduct().getId().equals(product.getId())) {
                System.out.println("Trovata la disponibilità: "+availability.getSize());
                availabilitiesToDelete.add(availability);
            }
        }
        if (!availabilitiesToDelete.isEmpty()) {
            System.out.println("Ho eliminato tutte le disponibilità");
            availabilityRepository.deleteAll(availabilitiesToDelete);
            return true;
        } else {
            System.out.println("Non sono riuscito ad eliminare le disponibilità");
            return false;
        }

    }

    @Override
    // Restituisce tutte le disponibilità registrate nel db
//    @Retry(name=AVAILABILITY_SERVICE)
    public List<Availability> getAll() {
        return availabilityRepository.findAll();
    }

    @Override
    // Resituisce tutte le disponibilità di un determinato prodotto
//    @Retry(name=AVAILABILITY_SERVICE)
    public List<AvailabilityDTO> getAvailabilitiesByProductID(ProductDTO productDTO) {
        return availabilityRepository.findAllByProduct(modelMapper.map(productDTO, Product.class))
                .stream()
                .map(a ->modelMapper.map(a, AvailabilityDTO.class))
                .toList();
    }
    @Override
//    @Retry(name=AVAILABILITY_SERVICE)
    public AvailabilityDTO getAvailabilitieByProductId(ProductDTO productDTO, String size) {
        Availability availability= availabilityRepository.findByProductAndSize(modelMapper.map(productDTO, Product.class), size);

        if (availability == null)
            return null;
        return modelMapper.map(availability, AvailabilityDTO.class);
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
