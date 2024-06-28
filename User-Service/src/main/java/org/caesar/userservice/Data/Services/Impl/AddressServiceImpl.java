package org.caesar.userservice.Data.Services.Impl;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.userservice.Data.Dao.AddressRepository;
import org.caesar.userservice.Data.Entities.Address;
import org.caesar.userservice.Data.Services.AddressService;
import org.caesar.userservice.Dto.AddressDTO;
import org.caesar.userservice.Dto.UserAddressDTO;
import org.modelmapper.ModelMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressServiceImpl implements AddressService {

    private final ModelMapper modelMapper;
    private final AddressRepository addressRepository;
    private final static String ADDRESS_SERVICE = "addressService";

    public String fallbackCircuitBreaker(CallNotPermittedException e){
        log.debug("Circuit breaker su addressService da: {}", e.getCausingCircuitBreakerName());
        return e.getMessage();
    }

    //Metodo per prendere un indirizzo
    @Override
    @Retry(name=ADDRESS_SERVICE)
    public AddressDTO getAddress(UUID addressId) {
        AddressDTO addressDTO = modelMapper.map(addressRepository.findById(addressId), AddressDTO.class);
        addressDTO.setId(null);
        return addressDTO;
    }

    //Metodo per aggiungere un indirizzo
    @Override
    @CircuitBreaker(name=ADDRESS_SERVICE, fallbackMethod = "fallbackCircuitBreaker")
    @Retry(name=ADDRESS_SERVICE)
    public UUID addAddress(AddressDTO addressDTO) {
        //Controllo che i campi inviati rispettino i criteri
        if(!checkRoadName(addressDTO.getRoadName()) ||
                !checkHouseNumber(addressDTO.getHouseNumber()) ||
                !checkRoadType(addressDTO.getRoadType()))
            return null;
        try{
            Address address = modelMapper.map(addressDTO, Address.class);
            // Save ritorna l'entità appena creata con l'ID (Che è autogenerato alla creazione), in caso serva è possibile salvare l'entità in una variabile
            return addressRepository.save(address).getId();
        }catch(RuntimeException | Error e){
            log.debug("Errore nell'inserimento dell'indirizzo dell'utente");
            return null;
        }
    }

    //Metodo per eliminare un indirizzo tramite id
    @Override
    @CircuitBreaker(name=ADDRESS_SERVICE, fallbackMethod = "fallbackCircuitBreaker")
    @Retry(name=ADDRESS_SERVICE)
    public boolean deleteAddress(UUID addressId) {
        try {
            addressRepository.deleteById(addressId);
            return true;
        } catch (Exception e) {
            log.debug("Errore nella cancellazione dell'indirizzo");
            return false;
        }
    }

    //Metodo per eliminare tutti gli indirizzi associati ad un utente
    @Override
    @CircuitBreaker(name=ADDRESS_SERVICE, fallbackMethod = "fallbackCircuitBreaker")
    @Retry(name=ADDRESS_SERVICE)
    public boolean deleteAllUserAddresses(List<UserAddressDTO> userAddresses) {
        //Presa degli id dei indirizzi dalle tuple di relazione
        List<UUID> addressId= new Vector<>();
        for(UserAddressDTO userAddress: userAddresses) {
            addressId.add(userAddress.getAddressId());
        }

        try {
            addressRepository.deleteAllById(addressId);
            return true;
        } catch (Exception e) {
            log.debug("Problemi nell'eliminazione di tutti gli indirizzi");
            return false;
        }
    }

    //Metodi per la convalida
    private boolean checkRoadName(String roadName) {
        return roadName!=null && (roadName.length()>=2 && roadName.length()<=30) &&
                roadName.matches("^(?=(?:.*[a-zA-Z]){2,})[a-zA-Z0-9 ]{2,30}$");
    }

    private boolean checkHouseNumber(String houseNumber) {
        return houseNumber!=null && (!houseNumber.isEmpty() && houseNumber.length()<=8) &&
                houseNumber.matches("^[0-9a-zA-Z]{1,8}$");
    }

    private boolean checkRoadType(String roadType) {
        if(roadType == null)
            return false;

        try {
            List<String> roadTypes = Files.readAllLines(Path.of("User-Service/src/main/resources/static/road-types.txt"));

            for(String types: roadTypes) {
                if(roadType.equals(types))
                    return true;
            }

            return false;
        } catch (IOException e) {
            //TODO LOG GESTIONE ERRORE
        }

        return false;
    }
}
