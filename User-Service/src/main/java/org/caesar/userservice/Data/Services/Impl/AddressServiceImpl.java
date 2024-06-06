package org.caesar.userservice.Data.Services.Impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.userservice.Config.JwtConverter;
import org.caesar.userservice.Data.Dao.AddressRepository;
import org.caesar.userservice.Data.Dao.KeycloakDAO.UserRepository;
import org.caesar.userservice.Data.Entities.Address;
import org.caesar.userservice.Data.Services.AddressService;
import org.caesar.userservice.Data.Services.UserAddressService;
import org.caesar.userservice.Data.Services.UserService;
import org.caesar.userservice.Dto.AddressDTO;
import org.caesar.userservice.Dto.UserAddressDTO;
import org.caesar.userservice.Dto.UserIdDTO;
import org.modelmapper.ModelMapper;

import java.util.UUID;
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

    private final UserAddressService userAddressService;

    private final UserService userService;




    //I metodi CRUD delle repository hanno di base il @Transactional, ma bisogna fare il doppio passaggio
    @Transactional
    @Override
    public boolean saveOrUpdateAddress(AddressDTO addressDTO, boolean isUpdate) {
        log.debug("Entrato nel save or update prima della convalida dei dati");
        if(!checkRoadName(addressDTO.getRoadName()) ||
                !checkHouseNumber(addressDTO.getHouseNumber()) ||
                !checkRoadType(addressDTO.getRoadType()))
            return false;

        log.debug("save or update post convalida dei dati");
        try{
            Address address = modelMapper.map(addressDTO, Address.class);


            UUID addressID = addressRepository.save(address).getId(); // Save ritorna l'entità appena creata con l'ID (Che è autogenerato alla creazione), in caso serva è possibile salvare l'entità in una variabile

            String userID = userService.getUserId().getUserId();

            if(!isUpdate) {
                UserAddressDTO userAddressDTO= new UserAddressDTO();

                userAddressDTO.setAddressId(addressID);
                userAddressDTO.setUserId(userID);

                return userAddressService.addUserAddreses(userAddressDTO);
            }
        }catch(RuntimeException | Error e){
            //LOG DA IMPLEMENTARE //TODO
            e.printStackTrace(); // You should replace this with a proper logging mechanism

            return false;
        }

        return true;
    }


    //Metodo per ottenere il singolo indirizzo e restituirlo al client
    @Override
    public AddressDTO getAddress(String addressName) {

        //Prendiamp l'id dell'utente attualmente attivo
        UserIdDTO userId = userService.getUserId();

        //Creazione della regex per prendersi il numero dell'indirizzo mandato dall'utente
        Pattern pattern = Pattern.compile("([0-9]+)");
        Matcher matcher = pattern.matcher(addressName);

        int addressNum;
        if(matcher.matches())
            addressNum = Integer.parseInt(matcher.group(1));
        else
            return null;

        log.debug("Sono dopo la presa del numero dell'indirizzo desiderato numero indirizzo {}", addressNum);
        //Prendiamo l'indirizzo in posizione addressNum nel database nella tabella della relazione utente-indirizzo
        UserAddressDTO userAddress= userAddressService.getUserAddress(userId.getUserId(), addressNum);

        //Ritorno valore null nel caso ci sia un problema nella ricerca degli indirizzi dell'utente
        if(userAddress==null)
            return null;

        //Prendiamo il singolo indirizzo in base all'id dell'indirizzo della tupla restituita sopra (mappando nel DTO)
        return modelMapper.map(addressRepository.findById(userAddress.getAddressId()), AddressDTO.class);
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
        return roadType!=null && (roadType.length()>=3 && roadType.length()<=8) &&
                roadType.matches("");
    }
}
