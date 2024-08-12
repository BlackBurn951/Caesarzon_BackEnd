package org.caesar.userservice.Data.Services.Impl;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.userservice.Data.Dao.UserCardRepository;
import org.caesar.userservice.Data.Entities.Card;
import org.caesar.userservice.Data.Entities.UserCard;
import org.caesar.userservice.Data.Services.UserCardService;
import org.caesar.userservice.Dto.CardDTO;
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

    //Prende la carta dell'utente tramite id
    @Override
    public UserCardDTO getUserCard(UUID id) {

        //Presa della lista delle carte associate all'utente
        UserCard userCard = userCardRepository.findById(id).orElse(null);
        return modelMapper.map(userCard, UserCardDTO.class);
    }

    //Prende gli id di tutte le carte dell'utente
    @Override
    public List<UUID> getCards(String userUsername) {

        List<UserCard> userCards = userCardRepository.findAllByUserUsername(userUsername);

        List<UUID> result = new Vector<>();

        for (UserCard userCard: userCards) {
            result.add(userCard.getId());
        }

        return result;
    }

    @Override
    public List<UserCardDTO> getUserCards(String userUsername) {
        List<UserCardDTO> result= new Vector<>();

        List<UserCard> userCards = userCardRepository.findByUserUsername(userUsername);

        for(UserCard ut: userCards) {
            result.add(modelMapper.map(ut, UserCardDTO.class));
        }

        return result;
    }

    @Override
    public boolean checkCard(String username, UUID cardId) {
        UserCard userCard= userCardRepository.findByUserUsernameAndId(username, cardId);

        return userCard != null;
    }

    //Aggiunta della relazione carta utente
    @Override
    public boolean addUserCards(UserCardDTO userCard) {
        //Try per gestire l'errore nell'inserimento della tupla (l'eventuale rollback sarà gestito dal @Transactional del save()
        try{
            UserCard userCardEntity = modelMapper.map(userCard, UserCard.class);
            userCardRepository.save(userCardEntity);

            return true;
        } catch (RuntimeException | Error e){
            log.debug("Errore nel salvataggio nella tabella di relazione utente carte");
            return false;
        }
    }

    //Eliminazione
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

    //Eliminazione
    @Override
    public boolean deleteUserCards(String userUsername) {
        try {
            //Presa di tutte le tuple inerenti all'utente da cancellare
            List<UserCard> userCards = userCardRepository.findByUserUsername(userUsername);

            //Eliminizaione delle tuple passando direttamente la lista con al suo intenro gli ogetti entity che le rappresentano
            userCardRepository.deleteAll(userCards);
            return true;
        } catch (Exception e) {
            log.debug("Errore nella cancellazione di tutte le tuple nella tabella di relazione utente carte");
            return false;
        }
    }
}
