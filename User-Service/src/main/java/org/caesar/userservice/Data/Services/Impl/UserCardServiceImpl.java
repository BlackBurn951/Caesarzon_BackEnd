package org.caesar.userservice.Data.Services.Impl;

import lombok.RequiredArgsConstructor;
import org.caesar.userservice.Data.Dao.UserCardRepository;
import org.caesar.userservice.Data.Entities.UserAddress;
import org.caesar.userservice.Data.Entities.UserCard;
import org.caesar.userservice.Data.Services.UserCardService;
import org.caesar.userservice.Dto.UserCardDTO;
import org.modelmapper.ModelMapper;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCardServiceImpl implements UserCardService {


    private final UserCardRepository userCardRepository;

    private final ModelMapper modelMapper;

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
}
