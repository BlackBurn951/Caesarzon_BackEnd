package org.caesar.userservice.Data.Services.Impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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

        //Conversione della stringa mandata dal client in INT
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(addressName);
        StringBuilder numbers = new StringBuilder();
        while (matcher.find()) {
            numbers.append(matcher.group());
        }

        int addressNum = Integer.parseInt(numbers.toString());

        //Prendiamo l'indirizzo in posizione addressNum nel database nella tabella della relazione utente-indirizzo
        UserAddressDTO userAddress= userAddressService.getUserAddress(userId.getUserId(), addressNum);

        //Prendiamo il singolo indirizzo in base all'id dell'indirizzo della tupla restituita sopra (mappando nel DTO)
        return modelMapper.map(addressRepository.findById(userAddress.getAddressId()), AddressDTO.class);
    }

}
