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
import org.caesar.userservice.Dto.UserDTO;
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

        List<UserCard> userCards = userCardRepository.findAllByUserUsername(userUsername);

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
        } catch (Exception | Error e){
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
        } catch (Exception | Error e) {
            log.debug("Problemi nella cancellazione della tupla  di relazione carta utente");
            return false;
        }
    }




    @Override
    public List<CardDTO> validateOrRollbackUserCardsDelete(String username, boolean rollback) {
        try {
            List<UserCard> cards= userCardRepository.findAllByUserUsername(username);

            if(cards.isEmpty())
                return new Vector<>();

            List<CardDTO> result= new Vector<>();
            for(UserCard userCard: cards) {
                result.add(modelMapper.map(userCard.getCard(), CardDTO.class));
                userCard.setOnDeleting(!rollback);
            }

            userCardRepository.saveAll(cards);

            return result;
        } catch (Exception | Error e) {
            log.debug("Problemi nella cancellazione della tupla  di relazione carta utente");
            return null;
        }
    }

    @Override
    public boolean completeUserCardsDelete(String username) {
        try {
            List<UserCard> cards= userCardRepository.findAllByUserUsername(username);

            for(UserCard userCard: cards) {
                userCard.setCard(null);
            }

            userCardRepository.saveAll(cards);

            return true;
        } catch (Exception | Error e) {
            log.debug("Problemi nella cancellazione della tupla  di relazione carta utente");
            return false;
        }
    }

    @Override
    public boolean releaseLockUserCards(String username) {
        try {
            userCardRepository.deleteAllByUserUsername(username);

            return true;
        } catch (Exception | Error e) {
            log.debug("Problemi nella cancellazione della tupla  di relazione carta utente");
            return false;
        }
    }

    @Override
    public boolean rollbackUserCards(String username, List<CardDTO> userCards) {
        try {
            List<UserCard> cards= userCardRepository.findAllByUserUsername(username);

            for(UserCard userCard: cards) {
                for(CardDTO card: userCards) {
                    userCard.setCard(modelMapper.map(card, Card.class));
                }
            }

            userCardRepository.saveAll(cards);

            return true;
        } catch (Exception | Error e) {
            log.debug("Problemi nella cancellazione della tupla  di relazione carta utente");
            return false;
        }
    }
}
