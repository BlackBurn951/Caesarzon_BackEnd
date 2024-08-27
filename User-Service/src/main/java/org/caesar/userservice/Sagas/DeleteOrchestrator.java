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

        if(profilePic==null)
            profilePicService.validateDeleteUser(user.getUsername(), true);

        //Validazione Tabelle di relazione carte e indirizzi
        List<CardDTO> validationUserCard= userCardService.validateOrRollbackUserCardsDelete(user.getUsername(), false);
        List<AddressDTO> validationUserAddress= userAddressService.validateOrRollbackUserAddressesDelete(user.getUsername(), false);

        int validateAddressAndCard= -1;  //0 -> true entrambi 1 -> true solo carte 2-> true solo indirizzi 3 -> false

        if(validationUserCard==null || validationUserAddress==null) {
            rollbackUserCardAndAddress(user.getUsername());

            return false;
        }


        //Controllo che l'utente abbia delle carte/indirizzi a suo nome
        if(!validationUserCard.isEmpty() || !validationUserAddress.isEmpty())
            validateAddressAndCard= validateCardAndAddress(validationUserCard.stream().map(CardDTO::getId).toList(), validationUserAddress.stream().map(AddressDTO::getId).toList(), false);

        //Validazione follower
        List<FollowerDTO> validateFollower= followerService.validateOrRollbackDeleteFollowers(user.getUsername(), false);  //0 -> true 1-> false 2 -> non avente

        //Controllo che non ci siano stati errori a prescindere dai dati presenti o no
        if(validateFollower!=null && validateAddressAndCard!=3 && profilePic!=null) {

            //Fase di validazione sul servizio delle notifiche
            ValidateUserDeleteDTO validateNotification= callCenter.validateNotificationService(false);

            //Controllo che non ci siano stati errori sul servizio notifiche
            if(validateNotification!=null) {

                //Fase di validazione sul servizio prodotti
                UserDeleteValidationDTO validateProduct= callCenter.validateProductService(false);

                //Controllo che non ci siano stati errori sul servizio prodotti
                if(validateProduct!=null) {


                    //Fase di completamento in locale

                    //Controllo che l'utente abbia follower
                    boolean completeFollower= true;
                    if(!validateFollower.isEmpty()) {
                        completeFollower= followerService.completeDeleteFollowers(user.getUsername());

                        if(!completeFollower) {
                            //Rollback in locale
                            followerService.rollbackDeleteFollowers(validateFollower);

                            if(validateAddressAndCard!=-1) {
                                validateCardAndAddress(validationUserCard.stream().map(CardDTO::getId).toList(), validationUserAddress.stream().map(AddressDTO::getId).toList(), true);
                                rollbackUserCardAndAddress(user.getUsername());
                            }

                            profilePicService.validateDeleteUser(user.getUsername(), true);
                            userService.validateOrRollbackDeleteUser(user.getUsername(), true);

                            //Rollback sui servizi esterni
                            callCenter.validateNotificationService(true);
                            callCenter.validateProductService(true);

                            return false;
                        }
                    }

                    boolean completePic= profilePicService.completeDeleteUser(user.getUsername());
                    if(!completePic) {
                        profilePicService.rollbackDeleteUser(user.getUsername(), profilePic);
                        userService.validateOrRollbackDeleteUser(user.getUsername(), true);

                        if(validateAddressAndCard!=-1) {
                            validateCardAndAddress(validationUserCard.stream().map(CardDTO::getId).toList(), validationUserAddress.stream().map(AddressDTO::getId).toList(), true);
                            rollbackUserCardAndAddress(user.getUsername());
                        }

                        if(!validateFollower.isEmpty())
                            followerService.rollbackDeleteFollowers(validateFollower);

                        //Rollback sui servizi esterni
                        callCenter.validateNotificationService(true);
                        callCenter.validateProductService(true);

                        return false;
                    }

                    //Controllo che l'utente abbia indirizzi e carte
                    boolean completeUserAddress= true;
                    boolean completeUserCard= true;
                    switch (validateAddressAndCard) {
                        case 0:
                            completeUserAddress= userAddressService.completeUserAddressesDelete(user.getUsername());
                            completeUserCard= userCardService.completeUserCardsDelete(user.getUsername());

                            if(!completeUserAddress || !completeUserCard) {
                                //Rollback locale
                                userAddressService.rollbackUserAddresses(user.getUsername(), validationUserAddress);
                                userCardService.rollbackUserCards(user.getUsername(), validationUserCard);
                                validateCardAndAddress(validationUserCard.stream().map(CardDTO::getId).toList(), validationUserAddress.stream().map(AddressDTO::getId).toList(), true);

                                if(!validateFollower.isEmpty())
                                    followerService.rollbackDeleteFollowers(validateFollower);

                                profilePicService.rollbackDeleteUser(user.getUsername(), profilePic);
                                userService.validateOrRollbackDeleteUser(user.getUsername(), true);

                                //Rollback sui servizi esterni
                                callCenter.validateNotificationService(true);
                                callCenter.validateProductService(true);

                                return false;
                            }

                            boolean completeCard= cardService.completeCards(validationUserCard.stream().map(CardDTO::getId).toList()),
                                    completeAddress= addressService.completeAddresses(validationUserAddress.stream().map(AddressDTO::getId).toList());

                            if(!completeAddress && !completeCard) {
                                //Rollback in locale
                                userAddressService.rollbackUserAddresses(user.getUsername(), validationUserAddress);
                                userCardService.rollbackUserCards(user.getUsername(), validationUserCard);
                                cardService.rollbackCards(validationUserCard);
                                addressService.rollbackAddresses(validationUserAddress);

                                if(!validateFollower.isEmpty())
                                    followerService.rollbackDeleteFollowers(validateFollower);

                                profilePicService.rollbackDeleteUser(user.getUsername(), profilePic);
                                userService.validateOrRollbackDeleteUser(user.getUsername(), true);

                                //Rollback sui servizi esterni
                                callCenter.validateNotificationService(true);
                                callCenter.validateProductService(true);

                                return false;
                            }
                            break;

                        case 1:
                            completeUserCard= userCardService.completeUserCardsDelete(user.getUsername());

                            if(!completeUserCard) {
                                userCardService.rollbackUserCards(user.getUsername(), validationUserCard);

                                if(!validateFollower.isEmpty())
                                    followerService.rollbackDeleteFollowers(validateFollower);

                                profilePicService.rollbackDeleteUser(user.getUsername(), profilePic);
                                userService.validateOrRollbackDeleteUser(user.getUsername(), true);

                                //Rollback sui servizi esterni
                                callCenter.validateNotificationService(true);
                                callCenter.validateProductService(true);

                                return false;
                            }

                            if(!cardService.completeCards(validationUserCard.stream().map(CardDTO::getId).toList())) {
                                userCardService.rollbackUserCards(user.getUsername(), validationUserCard);
                                cardService.rollbackCards(validationUserCard);

                                if(!validateFollower.isEmpty())
                                    followerService.rollbackDeleteFollowers(validateFollower);

                                profilePicService.rollbackDeleteUser(user.getUsername(), profilePic);
                                userService.validateOrRollbackDeleteUser(user.getUsername(), true);

                                //Rollback sui servizi esterni
                                callCenter.validateNotificationService(true);
                                callCenter.validateProductService(true);

                                return false;
                            }

                            break;

                        case 2:
                            completeUserAddress= userAddressService.completeUserAddressesDelete(user.getUsername());

                            if(!completeUserAddress) {
                                userAddressService.rollbackUserAddresses(user.getUsername(), validationUserAddress);

                                if(!validateFollower.isEmpty())
                                    followerService.rollbackDeleteFollowers(validateFollower);

                                profilePicService.rollbackDeleteUser(user.getUsername(), profilePic);
                                userService.validateOrRollbackDeleteUser(user.getUsername(), true);

                                //Rollback sui servizi esterni
                                callCenter.validateNotificationService(true);
                                callCenter.validateProductService(true);

                                return false;
                            }

                            if(!addressService.completeAddresses(validationUserAddress.stream().map(AddressDTO::getId).toList())) {
                                userAddressService.rollbackUserAddresses(user.getUsername(), validationUserAddress);
                                addressService.rollbackAddresses(validationUserAddress);

                                if(!validateFollower.isEmpty())
                                    followerService.rollbackDeleteFollowers(validateFollower);

                                profilePicService.rollbackDeleteUser(user.getUsername(), profilePic);
                                userService.validateOrRollbackDeleteUser(user.getUsername(), true);

                                //Rollback sui servizi esterni
                                callCenter.validateNotificationService(true);
                                callCenter.validateProductService(true);

                                return false;
                            }

                            break;
                    }

                    if(!userService.completeDeleteUser(user.getUsername())) {
                        if(!validateFollower.isEmpty())
                            followerService.rollbackDeleteFollowers(validateFollower);

                        if(validateAddressAndCard==0) {
                            userAddressService.rollbackUserAddresses(user.getUsername(), validationUserAddress);
                            userCardService.rollbackUserCards(user.getUsername(), validationUserCard);
                            cardService.rollbackCards(validationUserCard);
                            addressService.rollbackAddresses(validationUserAddress);
                        }
                        else if(validateAddressAndCard==1) {
                            userCardService.rollbackUserCards(user.getUsername(), validationUserCard);
                            cardService.rollbackCards(validationUserCard);
                        }
                        else if(validateAddressAndCard==2) {
                            userAddressService.rollbackUserAddresses(user.getUsername(), validationUserAddress);
                            addressService.rollbackAddresses(validationUserAddress);
                        }
                        profilePicService.rollbackDeleteUser(user.getUsername(), profilePic);
                        userService.updateUser(user);

                        callCenter.validateNotificationService(true);
                        callCenter.validateProductService(true);

                        return false;
                    }

                    //Fase di completamento sul servizio notifiche con controllo che l'utente abbia almeno qualcosa da completare
                    CompleteUserDeleteDTO completeNotification= new CompleteUserDeleteDTO();
                    if(!validateNotification.getReports().isEmpty() || !validateNotification.getSupports().isEmpty()
                            || !validateNotification.getUserNotification().isEmpty()) {

                        completeNotification= callCenter.completeNotificationService(validateNotification);

                        if(completeNotification==null) {
                            NotifyRollbackUserDeleteDTO rollbackNotification= new NotifyRollbackUserDeleteDTO();
                            rollbackNotification.setReports(validateNotification.getReports());
                            rollbackNotification.setSupports(validateNotification.getSupports());
                            rollbackNotification.setUserNotification(validateNotification.getUserNotification());
                            validateNotification.getAdminNotificationForReport().addAll(validateNotification.getAdminNotificationForSupport());
                            rollbackNotification.setAdminNotification(validateNotification.getAdminNotificationForReport());
                            callCenter.rollbackNotificationService(rollbackNotification);

                            if(!validateFollower.isEmpty())
                                followerService.rollbackDeleteFollowers(validateFollower);

                            if(validateAddressAndCard==0) {
                                userAddressService.rollbackUserAddresses(user.getUsername(), validationUserAddress);
                                userCardService.rollbackUserCards(user.getUsername(), validationUserCard);
                                cardService.rollbackCards(validationUserCard);
                                addressService.rollbackAddresses(validationUserAddress);
                            }
                            else if(validateAddressAndCard==1) {
                                userCardService.rollbackUserCards(user.getUsername(), validationUserCard);
                                cardService.rollbackCards(validationUserCard);
                            }
                            else if(validateAddressAndCard==2) {
                                userAddressService.rollbackUserAddresses(user.getUsername(), validationUserAddress);
                                addressService.rollbackAddresses(validationUserAddress);
                            }
                            profilePicService.rollbackDeleteUser(user.getUsername(), profilePic);
                            userService.updateUser(user);

                            callCenter.validateProductService(true);

                            return false;
                        }
                    }

                    //Fase di completamento sul servizio prodotti con controllo che l'utente abbia almeno qualcosa da completare
                    UserDeleteCompleteDTO completeProduct= new UserDeleteCompleteDTO();
                    if(!validateProduct.getWishlists().isEmpty() || !validateProduct.getOrders().isEmpty()
                        || !validateProduct.getProductOrder().isEmpty() | !validateProduct.getWishlistProduct().isEmpty() || !validateProduct.getReview().isEmpty()) {

                        boolean review= !validateProduct.getReview().isEmpty(), product= !validateProduct.getProductOrder().isEmpty(),
                                order= !validateProduct.getOrders().isEmpty(), wishProd= !validateProduct.getWishlistProduct().isEmpty();
                        completeProduct= callCenter.completeProductService(review, product, order, wishProd, validateProduct.getWishlists());

                        if(completeProduct==null) {
                            ProductRollbackUserDeleteDTO rollbackProduct= new ProductRollbackUserDeleteDTO();

                            rollbackProduct.setWishListProducts(validateProduct.getWishlistProduct());
                            rollbackProduct.setWishlists(validateProduct.getWishlists());
                            rollbackProduct.setOrders(validateProduct.getOrders());
                            rollbackProduct.setProductOrders(validateProduct.getProductOrder());
                            rollbackProduct.setReviews(validateProduct.getReview());

                            callCenter.rollbackProductService(rollbackProduct);

                            if(!validateNotification.getReports().isEmpty() || !validateNotification.getSupports().isEmpty()
                                    || !validateNotification.getUserNotification().isEmpty()) {
                                NotifyRollbackUserDeleteDTO rollbackNotification= new NotifyRollbackUserDeleteDTO();
                                rollbackNotification.setReports(validateNotification.getReports());
                                rollbackNotification.setSupports(validateNotification.getSupports());
                                rollbackNotification.setUserNotification(validateNotification.getUserNotification());
                                validateNotification.getAdminNotificationForReport().addAll(validateNotification.getAdminNotificationForSupport());
                                rollbackNotification.setAdminNotification(validateNotification.getAdminNotificationForReport());
                                callCenter.rollbackNotificationService(rollbackNotification);
                            }
                            if(!validateFollower.isEmpty())
                                followerService.rollbackDeleteFollowers(validateFollower);

                            if(validateAddressAndCard==0) {
                                userAddressService.rollbackUserAddresses(user.getUsername(), validationUserAddress);
                                userCardService.rollbackUserCards(user.getUsername(), validationUserCard);
                                cardService.rollbackCards(validationUserCard);
                                addressService.rollbackAddresses(validationUserAddress);
                            }
                            else if(validateAddressAndCard==1) {
                                userCardService.rollbackUserCards(user.getUsername(), validationUserCard);
                                cardService.rollbackCards(validationUserCard);
                            }
                            else if(validateAddressAndCard==2) {
                                userAddressService.rollbackUserAddresses(user.getUsername(), validationUserAddress);
                                addressService.rollbackAddresses(validationUserAddress);
                            }
                            profilePicService.rollbackDeleteUser(user.getUsername(), profilePic);
                            userService.updateUser(user);

                            return false;
                        }
                    }

                    //Fase di rilascio lock su tutti i servizi
                    if(!validateFollower.isEmpty())
                        followerService.releaseOrDeleteFollowers(user.getUsername());
                    switch (validateAddressAndCard) {
                        case 0:
                            userCardService.releaseLockUserCards(user.getUsername());
                            userAddressService.releaseLockUserAddresses(user.getUsername());
                            addressService.releaseLockAddresses(validationUserAddress.stream().map(AddressDTO::getId).toList());
                            cardService.releaseLockCards(validationUserCard.stream().map(CardDTO::getId).toList());
                            break;

                        case 1:
                            userCardService.releaseLockUserCards(user.getUsername());
                            cardService.releaseLockCards(validationUserCard.stream().map(CardDTO::getId).toList());
                            break;

                        case 2:
                            userAddressService.releaseLockUserAddresses(user.getUsername());
                            addressService.releaseLockAddresses(validationUserAddress.stream().map(AddressDTO::getId).toList());
                            break;
                    }

                    ReleaseLockUserDeleteDTO releaseNotification= new ReleaseLockUserDeleteDTO();
                    boolean support= false;

                    if(!completeNotification.getAdminNotification().isEmpty())
                        releaseNotification.setAdminNotification(completeNotification.getAdminNotification().stream().map(SaveAdminNotificationDTO::getId).toList());
                    if(!completeNotification.getUserNotification().isEmpty())
                        releaseNotification.setUserNotification(new Vector<>());
                    if(!validateNotification.getReports().isEmpty())
                        releaseNotification.setReportId(validateNotification.getReports().stream().map(ReportDTO::getId).toList());
                    if(completeNotification.isSupport())
                        support= true;
                    callCenter.releaseNotificationService(releaseNotification, support);

                    ProductRollbackUserDeleteDTO releaseProduct= new ProductRollbackUserDeleteDTO();

                    if(!validateProduct.getProductOrder().isEmpty())
                        releaseProduct.setProductOrders(validateProduct.getProductOrder());
                    if(!validateProduct.getOrders().isEmpty())
                        releaseProduct.setOrders(validateProduct.getOrders());
                    if(!validateProduct.getReview().isEmpty())
                        releaseProduct.setReviews(validateProduct.getReview());
                    if(!validateProduct.getWishlists().isEmpty())
                        releaseProduct.setWishlists(validateProduct.getWishlists());
                    if(!validateProduct.getWishlistProduct().isEmpty())
                        releaseProduct.setWishListProducts(validateProduct.getWishlistProduct());
                    callCenter.releaseProductService(releaseProduct);
                    profilePicService.releaseDeleteUser(user.getUsername());
                    userService.releaseLockDeleteUser(user.getUsername());

                    return true;
                }

                //Fase di rollback pre completamento in locale e sui servizi esterni
                callCenter.validateProductService(true);
            }

            //Fase di rollback pre completamento in locale e sul servizio notifiche
            callCenter.validateNotificationService(true);
        }

        //Fase di rollback pre completamento in locale
        validateCardAndAddress(validationUserCard.stream().map(CardDTO::getId).toList(), validationUserAddress.stream().map(AddressDTO::getId).toList(), true);
        followerService.validateOrRollbackDeleteFollowers(user.getUsername(), true);
        rollbackUserCardAndAddress(user.getUsername());
        profilePicService.validateDeleteUser(user.getUsername(), true);
        userService.validateOrRollbackDeleteUser(user.getUsername(), true);

        return false;
    }

    //Metodi di servizio
    private int validateCardAndAddress(List<UUID> cardId, List<UUID> addressId, boolean rollback) {
        boolean card= false,
                address= false;

        if(!cardId.isEmpty())
            card= cardService.validateOrRollbackCards(cardId, rollback);
        if(!addressId.isEmpty())
            address = addressService.validateOrRollbackAddresses(addressId, rollback);

        if(!cardId.isEmpty() && !addressId.isEmpty())
            return card && address? 0: 3;
        else if(!cardId.isEmpty())
            return card? 1: 3;
        else if(!addressId.isEmpty())
            return address? 2: 3;

        return 3;
    }

    //Metodi per il rollback
    private void rollbackUserCardAndAddress(String username) {
        userCardService.validateOrRollbackUserCardsDelete(username, true);
        userAddressService.validateOrRollbackUserAddressesDelete(username, true);
    }
}