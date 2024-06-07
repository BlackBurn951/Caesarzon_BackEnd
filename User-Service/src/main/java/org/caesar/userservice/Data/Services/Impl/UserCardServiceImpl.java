package org.caesar.userservice.Data.Services.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.userservice.Data.Dao.KeycloakDAO.UserRepository;
import org.caesar.userservice.Data.Dao.UserCardRepository;
import org.caesar.userservice.Data.Entities.UserAddress;
import org.caesar.userservice.Data.Entities.UserCard;
import org.caesar.userservice.Data.Services.UserCardService;
import org.caesar.userservice.Dto.UserAddressDTO;
import org.caesar.userservice.Dto.UserCardDTO;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.UUID;
import java.util.Vector;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCardServiceImpl implements UserCardService {


    private final UserCardRepository userCardRepository;

    private final ModelMapper modelMapper;

    private final UserRepository userRepository;

    @Override
    public boolean addUserCards(UserCardDTO userCard) {
        //Try per gestire l'errore nell'inserimento della tupla (l'eventuale rollback sarà gestito dal @Transactional del save()
        try{
            UserCard userCardEntity = modelMapper.map(userCard, UserCard.class);
            userCardRepository.save(userCardEntity);
        } catch (RuntimeException | Error e){
            //TODO Log
            return false;
        }

        return true;
    }

    @Override
    public UserCardDTO getUserCard(String userId, int cardNum) {

        //Presa della lista delle carte associate all'utente
        List<UserCard> userCardVector = userCardRepository.findByUserId(userId);

        int count= 0;

        //Presa della carta registrata in posizione cardNum
        UserCardDTO userCardDTO = null;
        for(UserCard userCard : userCardVector){
            count+=1;
            if(count == cardNum){
                userCardDTO= modelMapper.map(userCard, UserCardDTO.class);
                break;
            }
        }

        return userCardDTO;
    }

    @Override
    public List<String> getCards() {
        String userId= userRepository.getUserIdFromToken();

        int num= userCardRepository.countByUserId(userId);

        log.debug("Numero di tuple tornato {}", num);

        List<String> result= new Vector<>();
        for(int i=0; i<num; i++)
            result.add("Carta "+ (i+1));

        if (result.isEmpty())
            result.add("");


        for (String a: result)
            log.debug("Elemento nella lista {}", a);

        return result;
    }

    @Override
    public List<UserCardDTO> getUserCards(String userId) {
        List<UserCardDTO> result= new Vector<>();

        List<UserCard> userCards = userCardRepository.findByUserId(userId);

        for(UserCard ut: userCards) {
            result.add(modelMapper.map(ut, UserCardDTO.class));
        }

        return result;
    }

    @Override
    public boolean deleteUserCard(UserCardDTO userCardDTO) {
        try {
            userCardRepository.deleteById(userCardDTO.getId());
            return true;
        } catch (Exception e) {
            log.debug("Problemi nella cancellazione della tupla  di relazione carta utente");
            return false;
        }
    }

    @Override
    public boolean deleteUserCards(List<UserCardDTO> userCards) {
        try {
            List<UUID> ids= new Vector<>();

            for(UserCardDTO userCard : userCards) {
                ids.add(userCard.getId());
            }

            userCardRepository.deleteAllById(ids);
            return true;
        } catch (Exception e) {
            log.debug("Errore nella cancellazione di tutte le tuple nella tabella di relazione utente carte");
            return false;
        }
    }
}
