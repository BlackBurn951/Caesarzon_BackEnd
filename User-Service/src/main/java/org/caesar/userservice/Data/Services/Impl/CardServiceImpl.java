package org.caesar.userservice.Data.Services.Impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.userservice.Data.Dao.CardRepository;
import org.caesar.userservice.Data.Entities.Card;
import org.caesar.userservice.Data.Services.CardService;
import org.caesar.userservice.Data.Services.UserCardService;
import org.caesar.userservice.Data.Services.UserService;
import org.caesar.userservice.Dto.*;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardServiceImpl implements CardService {

    private final ModelMapper modelMapper;
    private final CardRepository cardRepository;


    //I metodi CRUD delle repository hanno di base il @Transactional, ma bisogna fare il doppio passaggio
    @Override
    public UUID addCard(CardDTO cardDTO) {
        if(!checkCardNumber(cardDTO.getCardNumber()) || !checkOwner(cardDTO.getOwner()) ||
            !checkCvv(cardDTO.getCvv()) || !checkExpiryDate(cardDTO.getExpiryDate()))
            return null;

        try{
            Card card = modelMapper.map(cardDTO, Card.class);

             // Save ritorna l'entità appena creata con l'ID (Che è autogenerato alla creazione), in caso serva è possibile salvare l'entità in una variabile
            return cardRepository.save(card).getId();
        }catch(RuntimeException | Error e){
            log.debug("Errore nel salvataggio della carta dell'utente");
            return null;
        }
    }

    @Override
    public CardDTO getCard(UUID cardId) {
        return modelMapper.map(cardRepository.findById(cardId), CardDTO.class);
    }

    @Override
    public boolean deleteCard(UUID cardId) {
        try {
            cardRepository.deleteById(cardId);
            return true;
        } catch (Exception e) {
            log.debug("Errore nella cancellazione della carta");
            return false;
        }
    }

    @Override
    public boolean deleteUserCards(List<UserCardDTO> userCards) {  //DONE
        List<UUID> cardId= new Vector<>();
        for(UserCardDTO userCard: userCards) {
            cardId.add(userCard.getCardId());
        }

        try {
            cardRepository.deleteAllById(cardId);
            return true;
        } catch (Exception e) {
            log.debug("Problemi nell'eliminazione di tutti le carte");
            return false;
        }
    }


    //Metodi per la convalida
    private boolean checkCardNumber(String cardNumber) {
        return cardNumber!=null && cardNumber.matches("[0-9]{16}");
    }

    private boolean checkOwner(String owner) {
        return owner!= null && owner.length()>5 && owner.length()<=40 &&
                owner.matches("^(?=.{5,40}$)[a-zA-Z]+( [a-zA-Z]+){0,3}$");
    }

    private boolean checkCvv(String cvv) {
        return cvv!=null && cvv.matches("[0-9]{3}$");
    }

    private boolean checkExpiryDate(String expiryDate) {

        //Controllo che la variabile della data non sia nulla o di lunghezza 0
        if(expiryDate==null || expiryDate.isEmpty())
            return false;

        //Regex per separare l'anno dal mese
        Pattern pattern = Pattern.compile("([0-9]+)-([0-9]+)");
        Matcher matcher = pattern.matcher(expiryDate);

        int month=0, year=0;
        if(matcher.matches()) {
            month = Integer.parseInt(matcher.group(2));
            year = Integer.parseInt(matcher.group(1));
        }

        //Presa della data attuale e separazione tra mese e anno
        LocalDate date = LocalDate.now();

        int actualMonth = date.getMonthValue(), actualYear = date.getYear();

        return month>=actualMonth && year>=actualYear;
    }
}
