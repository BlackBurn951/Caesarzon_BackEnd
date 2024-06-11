package org.caesar.userservice.Data.Services.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.userservice.Data.Dao.UserAddressRepository;
import org.caesar.userservice.Data.Entities.UserAddress;
import org.caesar.userservice.Data.Services.UserAddressService;
import org.caesar.userservice.Data.Services.UserService;
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

    private final UserService userService;



    @Override
    public boolean addUserAddreses(UserAddressDTO userAddress) {
        //Try per gestire l'errore nell'inserimento della tupla (l'eventuale rollback sarà gestito dal @Transactional del save()
        try{
            String userId= userService.getUserId().getUserId();

            userAddress.setUserId(userId);
            UserAddress userAddressEntity = modelMapper.map(userAddress, UserAddress.class);
            userAddressRepository.save(userAddressEntity);
        } catch (Exception e){
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

    @Override
    public List<String> getAddresses() {
        String userId= userRepository.getUserIdFromToken();

        int num= userAddressRepository.countByUserId(userId);

        List<String> result= new Vector<>();
        for(int i=0; i<num; i++)
            result.add("Indirizzo "+ (i+1));

        if (result.isEmpty())
            result.add("");

        return result;
    }

    @Override
    public List<UserAddressDTO> getUserAddresses(String userId) {
        List<UserAddressDTO> result= new Vector<>();

        List<UserAddress> userAddresses = userAddressRepository.findByUserId(userId);

        for(UserAddress ut: userAddresses) {
            result.add(modelMapper.map(ut, UserAddressDTO.class));
        }

        return result;
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
    public boolean deleteUserAddresses(String userId) { //DONE
        try {
            List<UserAddress> userAddresses = userAddressRepository.findByUserId(userId);

            List<UUID> ids= new Vector<>();

            for(UserAddress userAddress : userAddresses) {
                ids.add(userAddress.getId());
            }

            userAddressRepository.deleteAllById(ids);
            return true;
        } catch (Exception e) {
            log.debug("Errore nella cancellazione di tutte le tuple nella tabella di relazione utente indirizzi");
            return false;
        }
    }
}
