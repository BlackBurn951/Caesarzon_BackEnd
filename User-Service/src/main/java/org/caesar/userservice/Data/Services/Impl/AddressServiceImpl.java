package org.caesar.userservice.Data.Services.Impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.userservice.Data.Dao.AddressRepository;
import org.caesar.userservice.Data.Entities.Address;
import org.caesar.userservice.Data.Services.AddressService;
import org.caesar.userservice.Data.Services.UserAddressService;
import org.caesar.userservice.Data.Services.UserService;
import org.caesar.userservice.Dto.AddressDTO;
import org.caesar.userservice.Dto.UserAddressDTO;
import org.caesar.userservice.Dto.UserIdDTO;
import org.modelmapper.ModelMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressServiceImpl implements AddressService {

    private final ModelMapper modelMapper;

    //Repository degli indirizzi
    private final AddressRepository addressRepository;


    //I metodi CRUD delle repository hanno di base il @Transactional, ma bisogna fare il doppio passaggio
    @Transactional
    @Override
    public boolean saveAddress(AddressDTO addressDTO) {
        if(!checkRoadName(addressDTO.getRoadName()) ||
                !checkHouseNumber(addressDTO.getHouseNumber()) ||
                !checkRoadType(addressDTO.getRoadType()))
            return false;

        try{
            Address address = modelMapper.map(addressDTO, Address.class);


            UUID addressID = addressRepository.save(address).getId(); // Save ritorna l'entità appena creata con l'ID (Che è autogenerato alla creazione), in caso serva è possibile salvare l'entità in una variabile


            UserAddressDTO userAddressDTO= new UserAddressDTO();

            userAddressDTO.setAddressId(addressID);

            return userAddressService.addUserAddreses(userAddressDTO);
        }catch(RuntimeException | Error e){
            //LOG DA IMPLEMENTARE //TODO
            e.printStackTrace(); // You should replace this with a proper logging mechanism

            return false;
        }

    }

    //Metodo per ottenere il singolo indirizzo e restituirlo al client
    @Override
    public AddressDTO getAddress(String addressName) {

        int addressNum= getAddressNumber(addressName);

        if(addressNum == 0)
            return null;

        //Prendiamo l'indirizzo in posizione addressNum nel database nella tabella della relazione utente-indirizzo
        UserAddressDTO userAddress= userAddressService.getUserAddress(addressNum);

        //Ritorno valore null nel caso ci sia un problema nella ricerca degli indirizzi dell'utente
        if(userAddress==null)
            return null;

        //Prendiamo il singolo indirizzo in base all'id dell'indirizzo della tupla restituita sopra (mappando nel DTO)
        return modelMapper.map(addressRepository.findById(userAddress.getAddressId()), AddressDTO.class);
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
    public boolean deleteAllUserAddresses(String userId, List<UserAddressDTO> userAddresses) {  //DONE
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
