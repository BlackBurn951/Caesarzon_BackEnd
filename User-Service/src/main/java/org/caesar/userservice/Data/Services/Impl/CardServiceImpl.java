package org.caesar.userservice.Data.Services.Impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.caesar.userservice.Data.Dao.CardRepository;
import org.caesar.userservice.Data.Entities.Card;
import org.caesar.userservice.Data.Services.CardService;
import org.caesar.userservice.Data.Services.UserCardService;
import org.caesar.userservice.Data.Services.UserService;
import org.caesar.userservice.Dto.*;
import org.modelmapper.ModelMapper;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
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

}
