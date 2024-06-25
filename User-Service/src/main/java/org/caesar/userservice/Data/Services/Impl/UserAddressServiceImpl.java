package org.caesar.userservice.Data.Services.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.userservice.Data.Dao.UserAddressRepository;
import org.caesar.userservice.Data.Entities.UserAddress;
import org.caesar.userservice.Data.Services.UserAddressService;
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


    @Override
    public UserAddressDTO getUserAddress(UUID id) {

        UserAddress userAddress = userAddressRepository.findById(id).orElse(null);

        return modelMapper.map(userAddress, UserAddressDTO.class);
    }

    @Override
    public List<UUID> getAddresses(String userUsername) {

        List<UserAddress> userAddresses = userAddressRepository.findAllByUserUsername(userUsername);

        List<UUID> result = new Vector<>();

        for (UserAddress userAddress : userAddresses) {
            result.add(userAddress.getId());
        }

        return result;
    }

    @Override
    public List<UserAddressDTO> getUserAddresses(String userUsername) {
        List<UserAddressDTO> result= new Vector<>();

        List<UserAddress> userAddresses = userAddressRepository.findByUserUsername(userUsername);

        for(UserAddress ut: userAddresses) {
            result.add(modelMapper.map(ut, UserAddressDTO.class));
        }

        return result;
    }

    @Override
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

    @Override
    public boolean deleteUserAddress(UserAddressDTO userAddressDTO) {
        try {
            userAddressRepository.deleteById(userAddressDTO.getId());
            return true;
        } catch (Exception e) {
            log.debug("Problemi nella cancellazione della tupla  di relazione indirizzo utente");
            return false;
        }
    }

    @Override
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
