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

//        if(cardId==null || addressId==null)
//            //TODO ROLLBACK

        //Controllo che l'utente abbia delle carte/indirizzi a suo nome
        if(!cardId.isEmpty() || !addressId.isEmpty())
            validateAddressAndCard= validateCardAndAddress(cardId, addressId, false);

        int validateFollower= followerService.validateOrRollbackDeleteFollowers(username, false);  //0 -> true 1-> false 2 -> non avente

        //TODO VALIDAZIONE FOTO PROFILO (SICURA)

//        if(validateFollower==1)
//            //TODO ROLLBACK

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
                    List<FollowerDTO> rollbackFollower;
                    if(validateFollower!=2) {
                        rollbackFollower= followerService.completeDeleteFollowers(username);

//                        if(rollbackFollower!=null) {
//
//                            //TOdo ROLLBACK
//                        }
                    }

                    //Controllo che l'utente abbia indirizzi e carte
                    List<AddressDTO> completeUserAddress= new Vector<>();
                    List<CardDTO> completeUserCard= new Vector<>();
                    switch (validateAddressAndCard) {
                        case 0:
                            completeUserAddress= userAddressService.completeUserAddressesDelete(username);
                            completeUserCard= userCardService.completeUserCardsDelete(username);

//                            if(completeUserAddress==null || completeUserCard==null) {
//                                //TODO ROLLBACK
//                            }

                            boolean completeCard= cardService.completeCards(completeUserCard.stream().map(CardDTO::getId).toList()),
                                    completeAddress= addressService.completeAddresses(completeUserAddress.stream().map(AddressDTO::getId).toList());

//                            if(!completeAddress && !completeCard)
//                                //TODO ROLLBACK
                            break;

                        case 1:
                            completeUserCard= userCardService.completeUserCardsDelete(username);

//                            if(completeUserCard==null) {
//                                //TODO ROLLBACK
//                            }

//                            if(!cardService.completeCards(completeUserCard.stream().map(CardDTO::getId).toList()))
//                                //TODO ROLLBACK
                            break;

                        case 2:
                            completeUserAddress= userAddressService.completeUserAddressesDelete(username);

//                            if(completeUserAddress==null) {
//                                //TODO ROLLBACK
//                            }

//                            if(!addressService.completeAddresses(completeUserAddress.stream().map(AddressDTO::getId).toList()))
////                                //TODO ROLLBACK
                            break;
                    }

                    //Fase di completamento sul servizio notifiche con controllo che l'utente abbia almeno qualcosa da completare
                    CompleteUserDeleteDTO completeNotification= new CompleteUserDeleteDTO();
                    if(!validateNotification.getReports().isEmpty() || !validateNotification.getSupports().isEmpty()
                            || validateNotification.getUserNotification()==0) {

                        completeNotification= callCenter.completeNotificationService(validateNotification);

//                        if(completeNotification==null)
//                            //TODO ROLLBACK
                    }

                    //Fase di completamento sul servizio prodotti con controllo che l'utente abbia almeno qualcosa da completare
                    UserDeleteCompleteDTO completeProduct= new UserDeleteCompleteDTO();
                    if(!validateProduct.getWishlists().isEmpty() || !validateProduct.getOrders().isEmpty()
                        || validateProduct.getProductOrder()!=2 | validateProduct.getWishlistProduct()!=2 || validateProduct.getReview()!=2) {

                        boolean review= validateProduct.getReview()==0, product= validateProduct.getProductOrder()==0,
                                order= !validateProduct.getOrders().isEmpty(), wishProd= validateProduct.getWishlistProduct()==0;
                        completeProduct= callCenter.completeProductService(review, product, order, wishProd, validateProduct.getWishlists());

//                        if(completeProduct==null)
//                            //TODO ROLLBACK
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

                    if(!completeProduct.getProductOrder().isEmpty())
                        releaseProduct.setProductOrders(completeProduct.getProductOrder());
                    if(!validateProduct.getOrders().isEmpty())
                        releaseProduct.setOrders(validateProduct.getOrders());
                    if(!completeProduct.getReviews().isEmpty())
                        releaseProduct.setReviews(completeProduct.getReviews());
                    if(!validateProduct.getWishlists().isEmpty())
                        releaseProduct.setWishlists(validateProduct.getWishlists());
                    if(!completeProduct.getWishlistProduct().isEmpty())
                        releaseProduct.setWishListProducts(completeProduct.getWishlistProduct());
                    callCenter.releaseProductService(releaseProduct);
                }
            }
        }
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
}
