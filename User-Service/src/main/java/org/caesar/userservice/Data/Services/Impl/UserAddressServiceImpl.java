package org.caesar.userservice.Data.Services.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.userservice.Config.JwtConverter;
import org.caesar.userservice.Data.Dao.KeycloakDAO.UserRepository;
import org.caesar.userservice.Data.Dao.UserAddressRepository;
import org.caesar.userservice.Data.Entities.UserAddress;
import org.caesar.userservice.Data.Services.UserAddressService;
import org.caesar.userservice.Dto.UserAddressDTO;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAddressServiceImpl implements UserAddressService {

    private final UserAddressRepository userAddressRepository;

    private final ModelMapper modelMapper;

    @Override
    public boolean addUserAddreses(UserAddressDTO userAddress) {
        //Try per gestire l'errore nell'inserimento della tupla (l'eventuale rollback sarà gestito dal @Transactional del save()
        try{
            UserAddress userAddressEntity = modelMapper.map(userAddress, UserAddress.class);
            userAddressRepository.save(userAddressEntity);
        } catch (RuntimeException | Error e){
            //TODO Log
            return false;
        }

        return true;
    }

    @Override
    public UserAddressDTO getUserAddress(String userId, int addressNum) {

        //Presa della lista degli inidirizzi del singolo utente
        List<UserAddress> userAddresses = userAddressRepository.findByUserId(userId);

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
}
