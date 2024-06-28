package org.caesar.userservice.Data.Services.Impl;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.userservice.Data.Dao.CardRepository;
import org.caesar.userservice.Data.Entities.Card;
import org.caesar.userservice.Data.Services.CardService;
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
    private final static String CARD_SERVICE = "cardService";

    public String fallbackCircuitBreaker(CallNotPermittedException e){
        log.debug("Circuit breaker su cardService da: {}", e.getCausingCircuitBreakerName());
        return e.getMessage();
    }

    //Metodo per restituire una carta tramite id
    @Override
    @Retry(name=CARD_SERVICE)
    public CardDTO getCard(UUID cardId) {
        return modelMapper.map(cardRepository.findById(cardId), CardDTO.class);
    }

    //Metodo per aggiungere una carta
    @Override
    @CircuitBreaker(name=CARD_SERVICE, fallbackMethod = "fallbackCircuitBreaker")
    @Retry(name=CARD_SERVICE)
    public UUID addCard(CardDTO cardDTO) {
        //Controllo che i campi mandati rispettino i criteri
        if(!checkCardNumber(cardDTO.getCardNumber()) || !checkOwner(cardDTO.getOwner()) ||
            !checkCvv(cardDTO.getCvv()) || !checkExpiryDate(cardDTO.getExpiryDate()))
            return null;
        //TODO DA DECIDRE SE ABILITARE LA MODIFICA (DUPLICAZIONE OBBLIGATORIA)
        try{
            Card card = modelMapper.map(cardDTO, Card.class);

            return cardRepository.save(card).getId();
        }catch(RuntimeException | Error e){
            log.debug("Errore nel salvataggio della carta dell'utente");
            return null;
        }
    }

    //Metodo per eliminare una carta
    @Override
    @CircuitBreaker(name=CARD_SERVICE, fallbackMethod = "fallbackCircuitBreaker")
    @Retry(name=CARD_SERVICE)
    public boolean deleteCard(UUID cardId) {
        try {
            cardRepository.deleteById(cardId);
            return true;
        } catch (Exception e) {
            log.debug("Errore nella cancellazione della carta");
            return false;
        }
    }

    //Metodo per eliminare le carte dell'utente
    @Override
    @CircuitBreaker(name=CARD_SERVICE, fallbackMethod = "fallbackCircuitBreaker")
    @Retry(name=CARD_SERVICE)
    public boolean deleteUserCards(List<UserCardDTO> userCards) {
        //Presa degli id dei indirizzi dalle tuple di relazione
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
