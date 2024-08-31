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

    //Metodo per restituire una carta tramite id
    @Override
    public CardDTO getCard(UUID cardId) {
        return modelMapper.map(cardRepository.findById(cardId), CardDTO.class);
    }

    //Metodo per aggiungere una carta
    @Override
    public UUID addCard(CardDTO cardDTO) {
        System.out.println("Aggiornamento della carta");
        //Controllo che i campi mandati rispettino i criteri
        if(!checkCardNumber(cardDTO.getCardNumber()) || !checkOwner(cardDTO.getOwner()) ||
            !checkCvv(cardDTO.getCvv()) || !checkExpiryDate(cardDTO.getExpiryDate())) {
            System.out.println("sono nel check di tutti i controlli");
            return null;

        try{
            System.out.println("ho passato i controlli");
            Card card = modelMapper.map(cardDTO, Card.class);

            return cardRepository.save(card).getId();
        }catch(Exception | Error e){
            log.debug("Errore nel salvataggio della carta dell'utente");
            return null;
        }
    }

    //Metodo per eliminare una carta
    @Override
    public boolean deleteCard(UUID cardId) {
        try {
            cardRepository.deleteById(cardId);
            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nella cancellazione della carta");
            return false;
        }
    }



    @Override
    public boolean validateOrRollbackCards(List<UUID> cardsId, boolean rollback) {
        try {
            List<Card> cards= cardRepository.findAllById(cardsId);

            for(Card card : cards){
                card.setOnChanges(!rollback);
            }

            cardRepository.saveAll(cards);
            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nella cancellazione della carta");
            return false;
        }
    }

    @Override
    public boolean completeCards(List<UUID> cardsId) {
        try {
            List<Card> cards= cardRepository.findAllById(cardsId);

            List<CardDTO> result= new Vector<>();
            for(Card card : cards){
                result.add(modelMapper.map(card, CardDTO.class));

                card.setCvv(null);
                card.setBalance(0.0);
                card.setExpiryDate(null);
                card.setOwner(null);
                card.setCardNumber(null);
            }

            cardRepository.saveAll(cards);
            return true;
        } catch (Exception | Error e) {
            System.out.println(e);
            log.debug("Errore nella cancellazione della carta");
            return false;
        }
    }

    @Override
    public boolean releaseLockCards(List<UUID> cardsId) {
        try {
            cardRepository.deleteAllById(cardsId);

            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nella cancellazione della carta");
            return false;
        }
    }

    @Override
    public boolean rollbackCards(List<CardDTO> cards) {
        try {
            cardRepository.saveAll(cards.stream().map(cd -> modelMapper.map(cd, Card.class)).toList());

            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nella cancellazione della carta");
            return false;
        }
    }




    //Metodi per la convalida
    private boolean checkCardNumber(String cardNumber) {
        System.out.println("checkCardNumber: " + cardNumber);
        return cardNumber!=null && cardNumber.matches("[0-9]{16}");
    }

    private boolean checkOwner(String owner) {
        System.out.println("check owner carta: "+ owner.matches("^(?=.{5,40}$)[a-zA-Z]+( [a-zA-Z]+){0,3}$"));
        return owner!= null && owner.length()>5 && owner.length()<=40 &&
                owner.matches("^(?=.{5,40}$)[a-zA-Z]+( [a-zA-Z]+){0,3}$");
    }

    private boolean checkCvv(String cvv) {
        System.out.println("cvv: "+cvv);
        return cvv!=null && cvv.matches("[0-9]{3}$");
    }

    private boolean checkExpiryDate(String expiryDate) {

        //Controllo che la variabile della data non sia nulla o di lunghezza 0
        if(expiryDate==null || expiryDate.isEmpty())
            return false;

        //Regex per separare l'anno dal mese
        Pattern pattern = Pattern.compile("([0-9]+)-([0-9]+)");
        Matcher matcher = pattern.matcher(expiryDate);

        System.out.println("check data: prima regex");
        int month=0, year=0;
        System.out.println("sono prima del match: "+expiryDate);
        if(matcher.matches()) {
            System.out.println("check data: dopo regex "+"\n"+month+"\n"+year);
            month = Integer.parseInt(matcher.group(2));
            year = Integer.parseInt(matcher.group(1));
            System.out.println("Month: "+month+ " Year: "+year);
        }

        System.out.println("check data: ");
        //Presa della data attuale e separazione tra mese e anno
        LocalDate date = LocalDate.now();

        int actualMonth = date.getMonthValue(), actualYear = date.getYear();

        return month>=actualMonth && year>=actualYear;
    }
}
