package org.caesar.userservice.Data.Services.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.userservice.Data.Dao.UserAddressRepository;
import org.caesar.userservice.Data.Entities.UserAddress;
import org.caesar.userservice.Data.Services.UserAddressService;
import org.caesar.userservice.Dto.UserAddressDTO;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Vector;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAddressServiceImpl implements UserAddressService {

    private final UserAddressRepository userAddressRepository;
    private final ModelMapper modelMapper;


    @Override
    public UserAddressDTO getUserAddress(String userUsername, int addressNum) {

        //Presa della lista degli inidirizzi del singolo utente
        List<UserAddress> userAddresses = userAddressRepository.findByUserUsername(userUsername);

        int count= 0;

        //Scorrimento della lista degli indirizzi ricercando quello in posizione addressNum
        UserAddressDTO userAddressDTO = null;
        for(UserAddress userAddress : userAddresses){
            count+=1;
            if(count == addressNum){
                userAddressDTO= modelMapper.map(userAddress, UserAddressDTO.class);
                break;
            }
        }

        return userAddressDTO;
    }

    @Override
    public List<String> getAddresses(String userUsername) {
        //Conta degli indirizzi associati all'utente
        int num= userAddressRepository.countByUserUsername(userUsername);

        //Creazione delle stringe da restituire al client
        List<String> result= new Vector<>();
        for(int i=0; i<num; i++)
            result.add("Indirizzo "+ (i+1));

        //Invio di una stringa vuota nel caso l'utente non abbia indirizzi
        if (result.isEmpty())
            result.add("");

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
