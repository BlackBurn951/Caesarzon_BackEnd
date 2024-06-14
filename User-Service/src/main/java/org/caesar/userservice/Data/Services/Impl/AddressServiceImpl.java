package org.caesar.userservice.Data.Services.Impl;

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

    //Repository degli indirizzi
    private final AddressRepository addressRepository;


    //I metodi CRUD delle repository hanno di base il @Transactional, ma bisogna fare il doppio passaggio
    @Override
    public UUID addAddress(AddressDTO addressDTO) {
        if(!checkRoadName(addressDTO.getRoadName()) ||
                !checkHouseNumber(addressDTO.getHouseNumber()) ||
                !checkRoadType(addressDTO.getRoadType()))
            return null;

        try{
            log.debug("Post controlli");
            Address address = modelMapper.map(addressDTO, Address.class);

            // Save ritorna l'entità appena creata con l'ID (Che è autogenerato alla creazione), in caso serva è possibile salvare l'entità in una variabile
            return addressRepository.save(address).getId();
        }catch(RuntimeException | Error e){
            log.debug("Errore nell'inserimento dell'indirizzo dell'utente");
            return null;
        }

    }

    //Metodo per ottenere il singolo indirizzo e restituirlo al client
    @Override
    public AddressDTO getAddress(UUID addressId) {
        return modelMapper.map(addressRepository.findById(addressId), AddressDTO.class);
    }

    @Override
    public boolean deleteAddress(UUID addressId) {
        try {
            addressRepository.deleteById(addressId);
            return true;
        } catch (Exception e) {
            log.debug("Errore nella cancellazione dell'indirizzo");
            return false;
        }
    }

    @Override
    public boolean deleteAllUserAddresses(List<UserAddressDTO> userAddresses) {  //DONE
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
