package org.caesar.userservice.Sagas;

import lombok.RequiredArgsConstructor;
import org.caesar.userservice.Data.Services.*;
import org.caesar.userservice.Dto.AddressDTO;
import org.caesar.userservice.Dto.CardDTO;
import org.caesar.userservice.Dto.DeleteDTO.*;
import org.caesar.userservice.Dto.FollowerDTO;
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

    public boolean processUserDelete(String username) {

        //Fase di validazione in locale
        List<UUID> cardId= userCardService.validateOrRollbackUserCardsDelete(username, false),
                addressId= userAddressService.validateOrRollbackUserAddressesDelete(username, false);

        int validateAddressAndCard= -1;  //0 -> true entrambi 1 -> true solo carte 2-> true solo indirizzi 3 -> false

        if(cardId==null || addressId==null) {
            rollbackUserCardAndAddress(username);

            return false;
        }


        //Controllo che l'utente abbia delle carte/indirizzi a suo nome
        if(!cardId.isEmpty() || !addressId.isEmpty())
            validateAddressAndCard= validateCardAndAddress(cardId, addressId, false);

        int validateFollower= followerService.validateOrRollbackDeleteFollowers(username, false);  //0 -> true 1-> false 2 -> non avente

        //TODO VALIDAZIONE FOTO PROFILO (SICURA)


        //Controllo che non ci siano stati errori a prescindere dai dati presenti o no
        if(validateFollower!=1 && validateAddressAndCard!=3) {

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
                    List<FollowerDTO> rollbackFollower= new Vector<>();
                    if(validateFollower!=2) {
                        rollbackFollower= followerService.completeDeleteFollowers(username);

                        if(rollbackFollower==null) {
                            //Rollback in locale
                            followerService.rollbackDeleteFollowers(rollbackFollower);

                            if(validateAddressAndCard!=-1) {
                                validateCardAndAddress(cardId, addressId, true);
                                rollbackUserCardAndAddress(username);
                            }

                            //Rollback sui servizi esterni
                            callCenter.validateNotificationService(true);
                            callCenter.validateProductService(true);

                            return false;
                        }
                    }

                    //Controllo che l'utente abbia indirizzi e carte
                    List<AddressDTO> completeUserAddress= new Vector<>();
                    List<CardDTO> completeUserCard= new Vector<>();
                    switch (validateAddressAndCard) {
                        case 0:
                            completeUserAddress= userAddressService.completeUserAddressesDelete(username);
                            completeUserCard= userCardService.completeUserCardsDelete(username);

                            if(completeUserAddress==null || completeUserCard==null) {
                                //Rollback locale
                                userAddressService.rollbackUserAddresses(username, completeUserAddress);
                                userCardService.rollbackUserCards(username, completeUserCard);
                                validateCardAndAddress(cardId, addressId, true);

                                if(!rollbackFollower.isEmpty())
                                    followerService.rollbackDeleteFollowers(rollbackFollower);

                                //Rollback sui servizi esterni
                                callCenter.validateNotificationService(true);
                                callCenter.validateProductService(true);

                                return false;
                            }

                            boolean completeCard= cardService.completeCards(completeUserCard.stream().map(CardDTO::getId).toList()),
                                    completeAddress= addressService.completeAddresses(completeUserAddress.stream().map(AddressDTO::getId).toList());

                            if(!completeAddress && !completeCard) {
                                //Rollback in locale
                                userAddressService.rollbackUserAddresses(username, completeUserAddress);
                                userCardService.rollbackUserCards(username, completeUserCard);
                                cardService.rollbackCards(completeUserCard);
                                addressService.rollbackAddresses(completeUserAddress);

                                if(!rollbackFollower.isEmpty())
                                    followerService.rollbackDeleteFollowers(rollbackFollower);

                                //Rollback sui servizi esterni
                                callCenter.validateNotificationService(true);
                                callCenter.validateProductService(true);

                                return false;
                            }
                            break;

                        case 1:
                            completeUserCard= userCardService.completeUserCardsDelete(username);

                            if(completeUserCard==null) {
                                userCardService.rollbackUserCards(username, completeUserCard);

                                if(!rollbackFollower.isEmpty())
                                    followerService.rollbackDeleteFollowers(rollbackFollower);

                                //Rollback sui servizi esterni
                                callCenter.validateNotificationService(true);
                                callCenter.validateProductService(true);

                                return false;
                            }

                            if(!cardService.completeCards(completeUserCard.stream().map(CardDTO::getId).toList())) {
                                userCardService.rollbackUserCards(username, completeUserCard);
                                cardService.rollbackCards(completeUserCard);

                                if(!rollbackFollower.isEmpty())
                                    followerService.rollbackDeleteFollowers(rollbackFollower);

                                //Rollback sui servizi esterni
                                callCenter.validateNotificationService(true);
                                callCenter.validateProductService(true);

                                return false;
                            }

                            break;

                        case 2:
                            completeUserAddress= userAddressService.completeUserAddressesDelete(username);

                            if(completeUserAddress==null) {
                                userAddressService.rollbackUserAddresses(username, completeUserAddress);

                                if(!rollbackFollower.isEmpty())
                                    followerService.rollbackDeleteFollowers(rollbackFollower);

                                //Rollback sui servizi esterni
                                callCenter.validateNotificationService(true);
                                callCenter.validateProductService(true);

                                return false;
                            }

                            if(!addressService.completeAddresses(completeUserAddress.stream().map(AddressDTO::getId).toList())) {
                                userAddressService.rollbackUserAddresses(username, completeUserAddress);
                                addressService.rollbackAddresses(completeUserAddress);

                                if(!rollbackFollower.isEmpty())
                                    followerService.rollbackDeleteFollowers(rollbackFollower);

                                //Rollback sui servizi esterni
                                callCenter.validateNotificationService(true);
                                callCenter.validateProductService(true);

                                return false;
                            }

                            break;
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

                            if(!rollbackFollower.isEmpty())
                                followerService.rollbackDeleteFollowers(rollbackFollower);

                            if(validateAddressAndCard==0) {
                                userAddressService.rollbackUserAddresses(username, completeUserAddress);
                                userCardService.rollbackUserCards(username, completeUserCard);
                                cardService.rollbackCards(completeUserCard);
                                addressService.rollbackAddresses(completeUserAddress);
                            }
                            else if(validateAddressAndCard==1) {
                                userCardService.rollbackUserCards(username, completeUserCard);
                                cardService.rollbackCards(completeUserCard);
                            }
                            else if(validateAddressAndCard==2) {
                                userAddressService.rollbackUserAddresses(username, completeUserAddress);
                                addressService.rollbackAddresses(completeUserAddress);
                            }
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
                            if(!rollbackFollower.isEmpty())
                                followerService.rollbackDeleteFollowers(rollbackFollower);

                            if(validateAddressAndCard==0) {
                                userAddressService.rollbackUserAddresses(username, completeUserAddress);
                                userCardService.rollbackUserCards(username, completeUserCard);
                                cardService.rollbackCards(completeUserCard);
                                addressService.rollbackAddresses(completeUserAddress);
                            }
                            else if(validateAddressAndCard==1) {
                                userCardService.rollbackUserCards(username, completeUserCard);
                                cardService.rollbackCards(completeUserCard);
                            }
                            else if(validateAddressAndCard==2) {
                                userAddressService.rollbackUserAddresses(username, completeUserAddress);
                                addressService.rollbackAddresses(completeUserAddress);
                            }

                            return false;
                        }
                    }

                    //Fase di rilascio lock su tutti i servizi
                    if(validateFollower!=2)
                        followerService.releaseOrDeleteFollowers(username);
                    switch (validateAddressAndCard) {
                        case 0:
                            userCardService.releaseLockUserCards(username);
                            userAddressService.releaseLockUserAddresses(username);
                            addressService.releaseLockAddresses(completeUserAddress.stream().map(AddressDTO::getId).toList());
                            cardService.releaseLockCards(completeUserCard.stream().map(CardDTO::getId).toList());
                            break;

                        case 1:
                            userCardService.releaseLockUserCards(username);
                            cardService.releaseLockCards(completeUserCard.stream().map(CardDTO::getId).toList());
                            break;

                        case 2:
                            userAddressService.releaseLockUserAddresses(username);
                            addressService.releaseLockAddresses(completeUserAddress.stream().map(AddressDTO::getId).toList());
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

                    return true;
                }

                //Fase di rollback pre completamento in locale e sui servizi esterni
                callCenter.validateProductService(true);
            }

            //Fase di rollback pre completamento in locale e sul servizio notifiche
            callCenter.validateNotificationService(true);
        }

        //Fase di rollback pre completamento in locale
        validateCardAndAddress(cardId, addressId, true);
        followerService.validateOrRollbackDeleteFollowers(username, true);
        rollbackUserCardAndAddress(username);
        //TODO ROLLBACK FOTO PROFILO (SICURA)


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
