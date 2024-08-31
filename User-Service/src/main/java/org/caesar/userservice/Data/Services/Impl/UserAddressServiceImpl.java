package org.caesar.userservice.Data.Services.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.userservice.Data.Dao.UserAddressRepository;
import org.caesar.userservice.Data.Entities.*;
import org.caesar.userservice.Data.Services.UserAddressService;
import org.caesar.userservice.Dto.AddressDTO;
import org.caesar.userservice.Dto.CardDTO;
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

    //Prende l'indirizzo dell'utente tramite id
    @Override
    public UserAddressDTO getUserAddress(UUID id) {

        UserAddress userAddress = userAddressRepository.findById(id).orElse(null);

        return modelMapper.map(userAddress, UserAddressDTO.class);
    }

    //Prende gli id di tutti gli indirizzi dell'utente
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
    public boolean checkAddress(String username, UUID addressId) {
        System.out.println(username+" "+addressId);
        UserAddress userAddress= userAddressRepository.findByUserUsernameAndId(username, addressId);

        return userAddress != null;
    }

    //Aggiunta della relazione indirizzo utente
    @Override
    public boolean addUserAddreses(UserAddressDTO userAddress) {
        //Try per gestire l'errore nell'inserimento della tupla (l'eventuale rollback sarà gestito dal @Transactional del save()
        try{
            UserAddress userAddressEntity = modelMapper.map(userAddress, UserAddress.class);
            userAddressRepository.save(userAddressEntity);

            return true;
        } catch (Exception | Error e){
            log.debug("Errore nell'inserimento nella tabella di relazione utente indirizzo");
            return false;
        }
    }

    //Eliminazione
    @Override
    public boolean deleteUserAddress(UserAddressDTO userAddressDTO) {
        try {
            userAddressRepository.deleteById(userAddressDTO.getId());
            return true;
        } catch (Exception | Error e) {
            log.debug("Problemi nella cancellazione della tupla  di relazione indirizzo utente");
            return false;
        }
    }



    @Override
    public List<AddressDTO> validateOrRollbackUserAddressesDelete(String username, boolean rollback) {
        try {
            List<UserAddress> addresses= userAddressRepository.findAllByUserUsername(username);

            if(addresses.isEmpty())
                return new Vector<>();

            List<AddressDTO> result= new Vector<>();
            for(UserAddress userAddress: addresses) {
                result.add(modelMapper.map(userAddress.getAddress(), AddressDTO.class));
                userAddress.setOnDeleting(!rollback);
            }

            userAddressRepository.saveAll(addresses);

            return result;
        } catch (Exception | Error e) {
            log.debug("Problemi nella cancellazione della tupla  di relazione carta utente");
            return null;
        }
    }

    @Override
    public boolean completeUserAddressesDelete(String username) {
        try {
            //List<UserAddress> addresses= userAddressRepository.findAllByUserUsername(username);

//            for(UserAddress userAddress: addresses) {
//                userAddress.setAddress(null);
//            }
//
//            userAddressRepository.saveAll(addresses);

            return true;
        } catch (Exception | Error e) {
            log.debug("Problemi nella cancellazione della tupla  di relazione carta utente");
            return false;
        }
    }

    @Override
    public boolean releaseLockUserAddresses(String username) {
        try {
            userAddressRepository.deleteAllByUserUsername(username);

            return true;
        } catch (Exception | Error e) {
            log.debug("Problemi nella cancellazione della tupla  di relazione carta utente");
            return false;
        }
    }

    @Override
    public boolean rollbackUserAddresses(String username, List<AddressDTO> addresses) {
        try {
            List<UserAddress> addr= userAddressRepository.findAllByUserUsername(username);

            for(UserAddress userAddress: addr) {
                for(AddressDTO addressDTO: addresses) {
                    userAddress.setAddress(modelMapper.map(addressDTO, Address.class));
                }
            }

            userAddressRepository.saveAll(addr);

            return true;
        } catch (Exception | Error e) {
            log.debug("Problemi nella cancellazione della tupla  di relazione carta utente");
            return false;
        }
    }
}
