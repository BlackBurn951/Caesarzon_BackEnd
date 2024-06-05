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
    public boolean saveOrUpdateCard(CardDTO cardDTO, boolean isUpdate) {
        log.debug("Dentro il save or update prima della convalida");
        if(!checkCardNumber(cardDTO.getCardNumber()) || !checkOwner(cardDTO.getOwner()) ||
            !checkCvv(cardDTO.getCvv()) || !checkExpiryDate(cardDTO.getExpiryDate()))
            return false;

        try{
            Card card = modelMapper.map(cardDTO, Card.class);

            UUID cardID = cardRepository.save(card).getId(); // Save ritorna l'entità appena creata con l'ID (Che è autogenerato alla creazione), in caso serva è possibile salvare l'entità in una variabile

            String userID = userService.getUserId().getUserId();


            if(!isUpdate) {
                UserCardDTO userCardDTO = new UserCardDTO();

                userCardDTO.setCardId(cardID);
                userCardDTO.setUserId(userID);

                return userCardService.addUserCards(userCardDTO);
            }
        }catch(RuntimeException | Error e){
            //LOG DA IMPLEMENTARE //TODO
            e.printStackTrace(); // You should replace this with a proper logging mechanism

            return false;
        }

        return true;
    }

    @Override
    public CardDTO getCard(String cardName) {

        UserIdDTO userId = userService.getUserId();

        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(cardName);
        StringBuilder numbers = new StringBuilder();
        while (matcher.find()) {
            numbers.append(matcher.group());
        }

        int cardNum = Integer.parseInt(numbers.toString());


        UserCardDTO userCard= userCardService.getUserCard(userId.getUserId(), cardNum);

        return modelMapper.map(cardRepository.findById(userCard.getCardId()), CardDTO.class);
    }


    //Metodi per la convalida
    private boolean checkCardNumber(String cardNumber) {
        return cardNumber!=null && cardNumber.length()==10 && cardNumber.matches("[0-9]{10}");
    }

    private boolean checkOwner(String owner) {
        return owner!= null && owner.length()>5 && owner.length()<=40 &&
                owner.matches("^(?=.{5,40}$)[a-zA-Z]+( [a-zA-Z]+){0,3}$");
    }

    private boolean checkCvv(String cvv) {
        return cvv!=null && cvv.matches("[0-9]{3}$");
    }

    private boolean checkExpiryDate(LocalDate expiryDate) {
        log.debug("Data mandata da front: "+expiryDate+"\nData del local date"+LocalDate.now());
        return expiryDate!=null && expiryDate.isAfter(LocalDate.now());
    }
}
