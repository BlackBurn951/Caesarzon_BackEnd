package org.caesar.userservice.Data.Services.Impl;

import lombok.RequiredArgsConstructor;
import org.caesar.userservice.Data.Dao.UserCardRepository;
import org.caesar.userservice.Data.Entities.UserCard;
import org.caesar.userservice.Data.Services.UserCardService;
import org.caesar.userservice.Dto.UserCardDTO;
import org.modelmapper.ModelMapper;

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
}
