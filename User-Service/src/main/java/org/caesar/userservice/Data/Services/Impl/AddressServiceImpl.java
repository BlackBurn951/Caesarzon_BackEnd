package org.caesar.userservice.Data.Services.Impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.caesar.userservice.Data.Dao.AddressRepository;
import org.caesar.userservice.Data.Entities.Address;
import org.caesar.userservice.Data.Services.AddressService;
import org.caesar.userservice.Data.Services.UserAddressService;
import org.caesar.userservice.Data.Services.UserService;
import org.caesar.userservice.Dto.AddressDTO;
import org.caesar.userservice.Dto.UserAddressDTO;
import org.modelmapper.ModelMapper;

@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final ModelMapper modelMapper;

    //Repository degli indirizzi
    private final AddressRepository addressRepository;

    private final UserAddressService userAddressService;

    //I metodi CRUD delle repository hanno di base il @Transactional, ma bisogna fare il doppio passaggio
    @Transactional
    @Override
    public boolean saveOrUpdateAddress(AddressDTO addressDTO, boolean isUpdate) {
        try{
            Address address = modelMapper.map(addressDTO, Address.class);

            addressRepository.save(address); // Save ritorna l'entità appena creata con l'ID (Che è autogenerato alla creazione), in caso serva è possibile salvare l'entità in una variabile

            if(!isUpdate)
                return userAddressService.addUserAddreses(new UserAddressDTO());

        }catch(RuntimeException | Error e){
            //LOG DA IMPLEMENTARE //TODO
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
