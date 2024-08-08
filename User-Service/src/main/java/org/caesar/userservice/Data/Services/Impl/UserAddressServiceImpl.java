package org.caesar.userservice.Data.Services.Impl;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.userservice.Data.Dao.UserAddressRepository;
import org.caesar.userservice.Data.Entities.Address;
import org.caesar.userservice.Data.Entities.Card;
import org.caesar.userservice.Data.Entities.UserAddress;
import org.caesar.userservice.Data.Entities.UserCard;
import org.caesar.userservice.Data.Services.UserAddressService;
import org.caesar.userservice.Dto.AddressDTO;
import org.caesar.userservice.Dto.UserAddressDTO;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.UUID;
import java.util.Vector;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAddressServiceImpl implements UserAddressService {

    private final UserAddressRepository userAddressRepository;
    private final ModelMapper modelMapper;
    private final static String USER_ADDRESS_SERVICE= "userAddressService";

    public String fallbackCircuitBreaker(CallNotPermittedException e){
        log.debug("Circuit breaker su userAddressService da: {}", e.getCausingCircuitBreakerName());
        return e.getMessage();
    }

    //Prende l'indirizzo dell'utente tramite id
    @Override
    public UserAddressDTO getUserAddress(UUID id) {

        UserAddress userAddress = userAddressRepository.findById(id).orElse(null);

        return modelMapper.map(userAddress, UserAddressDTO.class);
    }

    //Prende gli id di tutti gli indirizzi dell'utente
    @Override
//    @Retry(name=USER_ADDRESS_SERVICE)
    public List<UUID> getAddresses(String userUsername) {

        List<UserAddress> userAddresses = userAddressRepository.findAllByUserUsername(userUsername);

        List<UUID> result = new Vector<>();

        for (UserAddress userAddress : userAddresses) {
            result.add(userAddress.getId());
        }

        return result;
    }

    @Override
//    @Retry(name=USER_ADDRESS_SERVICE)
    public List<UserAddressDTO> getUserAddresses(String userUsername) {
        List<UserAddressDTO> result= new Vector<>();

        List<UserAddress> userAddresses = userAddressRepository.findByUserUsername(userUsername);

        for(UserAddress ut: userAddresses) {
            result.add(modelMapper.map(ut, UserAddressDTO.class));
        }

        return result;
    }

    @Override
//    @Retry(name=USER_ADDRESS_SERVICE)
    public boolean checkAddress(String username, UUID addressId) {
        UserAddress userAddress= userAddressRepository.findByUserUsernameAndId(username, addressId);
        System.out.println(userAddress.getId());
        return userAddress != null;
    }

    //Aggiunta della relazione indirizzo utente
    @Override
//    @CircuitBreaker(name=USER_ADDRESS_SERVICE, fallbackMethod = "fallbackCircuitBreaker")
//    @Retry(name=USER_ADDRESS_SERVICE)
    public boolean addUserAddreses(UserAddressDTO userAddress) {
        //Try per gestire l'errore nell'inserimento della tupla (l'eventuale rollback sarà gestito dal @Transactional del save()
        try{
            UserAddress userAddressEntity = modelMapper.map(userAddress, UserAddress.class);
            userAddressRepository.save(userAddressEntity);

            return true;
        } catch (Exception e){
            log.debug("Errore nell'inserimento nella tabella di relazione utente indirizzo");
            return false;
        }
    }

    //Eliminazione
    @Override
//    @CircuitBreaker(name=USER_ADDRESS_SERVICE, fallbackMethod = "fallbackCircuitBreaker")
//    @Retry(name=USER_ADDRESS_SERVICE)
    public boolean deleteUserAddress(UserAddressDTO userAddressDTO) {
        try {
            userAddressRepository.deleteById(userAddressDTO.getId());
            return true;
        } catch (Exception e) {
            log.debug("Problemi nella cancellazione della tupla  di relazione indirizzo utente");
            return false;
        }
    }

    //Eliminazione
    @Override
//    @CircuitBreaker(name=USER_ADDRESS_SERVICE, fallbackMethod = "fallbackCircuitBreaker")
//    @Retry(name=USER_ADDRESS_SERVICE)
    public boolean deleteUserAddresses(String userUsername) {
        try {
            //Presa di tutte le tuple inerenti all'utente da cancellare
            List<UserAddress> userAddresses = userAddressRepository.findByUserUsername(userUsername);

            //Eliminizaione delle tuple passando direttamente la lista con al suo intenro gli ogetti entity che le rappresentano
            userAddressRepository.deleteAll(userAddresses);
            return true;
        } catch (Exception e) {
            log.debug("Errore nella cancellazione di tutte le tuple nella tabella di relazione utente indirizzi");
            return false;
        }
    }
}
