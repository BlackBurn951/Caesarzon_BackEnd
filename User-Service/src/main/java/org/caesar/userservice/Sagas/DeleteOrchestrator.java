package org.caesar.userservice.Sagas;

import lombok.RequiredArgsConstructor;
import org.caesar.userservice.Data.Services.*;
import org.caesar.userservice.Dto.*;
import org.caesar.userservice.Dto.DeleteDTO.*;
import org.caesar.userservice.Utils.CallCenter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.Vector;

@Service
@RequiredArgsConstructor
public class DeleteOrchestrator {

    private final UserAddressService userAddressService;
    private final UserCardService userCardService;
    private final AddressService addressService;
    private final CardService cardService;
    private final FollowerService followerService;
    private final CallCenter callCenter;
    private final ProfilePicService profilePicService;
    private final UserService userService;

    public boolean processUserDelete(UserDTO user) {

        //Fase di validazione in locale

        //Validazione foto profilo
        ProfilePicDTO profilePic= profilePicService.validateDeleteUser(user.getUsername(), false);

        //Validazione carte
        List<CardDTO> validationUserCard= userCardService.validateOrRollbackUserCardsDelete(user.getUsername(), false);

        boolean validateCard= true,
                validateAddress= true;

        if(validationUserCard!=null && !validationUserCard.isEmpty())
            validateCard= cardService.validateOrRollbackCards(validationUserCard.stream().map(CardDTO::getId).toList(), false);


        //Validazione indirizi
        List<AddressDTO> validationUserAddress= userAddressService.validateOrRollbackUserAddressesDelete(user.getUsername(), false);

        if(validationUserAddress!=null && !validationUserAddress.isEmpty())
             validateAddress= addressService.validateOrRollbackAddresses(validationUserAddress.stream().map(AddressDTO::getId).toList(), false);


        //Validazione follower
        List<FollowerDTO> validateFollower= followerService.validateOrRollbackDeleteFollowers(user.getUsername(), false);

        boolean checkCard= validationUserCard!=null && !validationUserCard.isEmpty() && validateCard;
        boolean checkAddress= validationUserAddress!=null && !validationUserAddress.isEmpty() && validateAddress;
        if(profilePic!=null && validationUserCard!=null && checkCard && validationUserAddress !=null && checkAddress && validateFollower!=null) {

            //Fase di validazione (completamento) sui servizi esterni
            if(callCenter.validateNotificationService(false) && callCenter.validateProductService(false)) {

                //Completamento (release) in locale

                //Release della foto profilo
                profilePicService.releaseDeleteUser(user.getUsername());

                //Release delle carte
                if(!validationUserCard.isEmpty()) {
                    userCardService.releaseLockUserCards(user.getUsername());
                    cardService.releaseLockCards(validationUserCard.stream().map(CardDTO::getId).toList());
                }

                //Release degli indirizzi
                if(!validationUserAddress.isEmpty()) {
                    userAddressService.releaseLockUserAddresses(user.getUsername());
                    addressService.releaseLockAddresses(validationUserAddress.stream().map(AddressDTO::getId).toList());
                }

                //Release dei follower
                if(!validateFollower.isEmpty())
                    followerService.releaseOrDeleteFollowers(user.getUsername());

                userService.releaseLockDeleteUser(user.getUsername());

                return true;
            }

            //Fase di rollback sui servizi esterni
            callCenter.validateNotificationService(true);
            callCenter.validateProductService(true);
        }

        //Fase di rollback in locale
        profilePicService.validateDeleteUser(user.getUsername(), true);

        if(validationUserCard!=null && !validationUserCard.isEmpty()) {
            userCardService.validateOrRollbackUserCardsDelete(user.getUsername(), true);
            cardService.validateOrRollbackCards(validationUserCard.stream().map(CardDTO::getId).toList(), true);
        }

        if(validationUserAddress!=null && !validationUserAddress.isEmpty()) {
            userAddressService.validateOrRollbackUserAddressesDelete(user.getUsername(), true);
            addressService.validateOrRollbackAddresses(validationUserAddress.stream().map(AddressDTO::getId).toList(), true);
        }

        if(validateFollower!=null && !validateFollower.isEmpty())
            followerService.validateOrRollbackDeleteFollowers(user.getUsername(), true);

        userService.validateOrRollbackDeleteUser(user.getUsername(), true);

        return false;
    }
}