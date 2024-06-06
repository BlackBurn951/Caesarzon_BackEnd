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
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardServiceImpl implements CardService {

    private final ModelMapper modelMapper;

    //Repository delle carte
    private final CardRepository cardRepository;

    private final UserCardService userCardService;

    private final UserService userService;


    //I metodi CRUD delle repository hanno di base il @Transactional, ma bisogna fare il doppio passaggio
    @Transactional
    @Override
    public boolean saveCard(CardDTO cardDTO) {
        if(!checkCardNumber(cardDTO.getCardNumber()) || !checkOwner(cardDTO.getOwner()) ||
            !checkCvv(cardDTO.getCvv()) || !checkExpiryDate(cardDTO.getExpiryDate()))
            return false;

        try{
            Card card = modelMapper.map(cardDTO, Card.class);

            UUID cardID = cardRepository.save(card).getId(); // Save ritorna l'entità appena creata con l'ID (Che è autogenerato alla creazione), in caso serva è possibile salvare l'entità in una variabile

            String userID = userService.getUserId().getUserId();



            UserCardDTO userCardDTO = new UserCardDTO();

            userCardDTO.setCardId(cardID);
            userCardDTO.setUserId(userID);

            return userCardService.addUserCards(userCardDTO);
        }catch(RuntimeException | Error e){
            //LOG DA IMPLEMENTARE //TODO
            e.printStackTrace(); // You should replace this with a proper logging mechanism

            return false;
        }
    }

    @Override
    public CardDTO getCard(String cardName) {

        UserIdDTO userId = userService.getUserId();

        //Scrittura della regex per prendere il numero della carta desiderata
        Pattern pattern = Pattern.compile(".*([0-9]+)");
        Matcher matcher = pattern.matcher(cardName);

        int cardNumber;
        if(matcher.matches())
            cardNumber = Integer.parseInt(matcher.group(1));
        else
            return null;

        log.debug("Sono dopo la presa del numero della carta desiderata numero carta {}", cardNumber);

        UserCardDTO userCard= userCardService.getUserCard(userId.getUserId(), cardNumber);

        //Ritorno di un valore null in caso di problemi nella presa della carta desiderata
        if(userCard==null)
            return null;

        return modelMapper.map(cardRepository.findById(userCard.getCardId()), CardDTO.class);
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
