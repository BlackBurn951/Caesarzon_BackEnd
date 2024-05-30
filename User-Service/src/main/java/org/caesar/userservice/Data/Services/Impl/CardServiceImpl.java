package org.caesar.userservice.Data.Services.Impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.caesar.userservice.Config.JwtConverter;
import org.caesar.userservice.Data.Dao.CardRepository;
import org.caesar.userservice.Data.Entities.Card;
import org.caesar.userservice.Data.Services.CardService;
import org.caesar.userservice.Data.Services.UserCardService;
import org.caesar.userservice.Dto.CardDTO;
import org.caesar.userservice.Dto.UserCardDTO;
import org.modelmapper.ModelMapper;

@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final ModelMapper modelMapper;

    //Converter per il token
    private final JwtConverter jwtConverter = new JwtConverter();

    //Repository delle carte
    private final CardRepository cardRepository;

    private final UserCardService userCardService;

    //I metodi CRUD delle repository hanno di base il @Transactional, ma bisogna fare il doppio passaggio
    @Transactional
    @Override
    public boolean saveOrUpdateCard(CardDTO cardDTO, boolean isUpdate) {
        try{
            Card card = modelMapper.map(cardDTO, Card.class);

            cardRepository.save(card); // Save ritorna l'entità appena creata con l'ID (Che è autogenerato alla creazione), in caso serva è possibile salvare l'entità in una variabile

            if(!isUpdate)
                return userCardService.addUserCards(new UserCardDTO());

        }catch(RuntimeException | Error e){
            //LOG DA IMPLEMENTARE //TODO
            return false;
        }

        return true;
    }
}
