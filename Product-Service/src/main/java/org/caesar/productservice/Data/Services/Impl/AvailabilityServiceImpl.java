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

    @Override // Aggiunge tuple o modifica la tabella delle disponibilità
    public boolean addOrUpdateAvailability(List<AvailabilityDTO> availabilitiesDTO, ProductDTO product) {
        if (availabilitiesDTO.isEmpty()) {
            return false;
        }

        //Presa della lista delle disppnibilità già inserite per il prodotto
        List<Availability> availabilities= availabilityRepository.findAllByProduct(modelMapper.map(product, Product.class));
        boolean insert= false;  //Variabile che identifica l'aggiornamento o meno

        //Ciclo sulle disponibilità arrivate dal front
        for (AvailabilityDTO availabilityDTO : availabilitiesDTO) {

            //Validazione sui dati in ingresso
            if((checkQuantity(availabilityDTO.getAmount()) && checkSize(availabilityDTO.getSize()))) {

                //Ciclio sull'eventuali disponibilità già presenti per il prodotto
                for(Availability availability : availabilities) {

                    //Se la taglia in arrivo combacia con una già presente si fa l'aggiornamento
                    if(availabilityDTO.getSize() != null && availabilityDTO.getSize().equals(availability.getSize())) {
                        availability.setAmount(availabilityDTO.getAmount());
                        insert= true;
                        availabilityRepository.save(availability);
                        break;
                    }else if(availabilityDTO.getSize() == null && availability.getSize() == null){
                        availability.setAmount(availabilityDTO.getAmount());
                        insert= true;
                        availabilityRepository.save(availability);
                        break;
                    }


                }

                //Controllo che l'aggiornamento non sia stato fatto + aggiunta della nuova disponibilità
                if(!insert) {
                    Availability myAvailability = modelMapper.map(availabilityDTO, Availability.class);
                    myAvailability.setProduct(modelMapper.map(product, Product.class));
                    availabilityRepository.save(myAvailability);
                } else
                    insert= false;
            }
        }
        return true;
    }

    @Override //Elimina una disponibilità dal db tramite il suo id
    public boolean deleteAvailability(UUID availabilityId) {
        try {
            availabilityRepository.deleteById(availabilityId);
            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nella cancellazione della disponibilità");
            return false;
        }
    }

    @Override //Elimina tutte le disponibilità di un determinato prodotto
    public boolean deleteAvailabilityByProduct(ProductDTO product) {
        try{
            availabilityRepository.deleteAllByProduct(modelMapper.map(product, Product.class));

            return true;
        } catch(Exception | Error e) {
            log.debug("Errore nella cancellazione delle disponibilità per singolo prodotto");
            return false;
        }
    }

    @Override
    public List<Availability> getAll() {
        return availabilityRepository.findAll();
    }

    @Override //Resituisce tutte le disponibilità di un determinato prodotto
    public List<AvailabilityDTO> getAvailabilitiesByProduct(ProductDTO productDTO) {
        return availabilityRepository.findAllByProduct(modelMapper.map(productDTO, Product.class))
                .stream()
                .map(a ->modelMapper.map(a, AvailabilityDTO.class))
                .toList();
    }

    @Override
    public AvailabilityDTO getAvailabilitieByProductId(ProductDTO productDTO, String size) {
        Availability availability= availabilityRepository.findByProductAndSize(modelMapper.map(productDTO, Product.class), size);

        if (availability == null)
            return null;
        return modelMapper.map(availability, AvailabilityDTO.class);
    }


    //METODI DI SERVIZIO
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
