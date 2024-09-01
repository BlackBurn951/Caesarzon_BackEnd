package org.caesar.productservice.Data.Services.Impl;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.productservice.Data.Dao.WishlistRepository;
import org.caesar.productservice.Data.Entities.Wishlist;
import org.caesar.productservice.Data.Services.WishlistService;
import org.caesar.productservice.Dto.BasicWishlistDTO;
import org.caesar.productservice.Dto.ChangeVisibilityDTO;
import org.caesar.productservice.Dto.WishlistDTO;
import org.modelmapper.ModelMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.Vector;

@Service
@RequiredArgsConstructor
@Slf4j
public class WishlistServiceImpl implements WishlistService {

    private final ModelMapper modelMapper;
    private final WishlistRepository wishlistRepository;
    private final RestTemplate restTemplate;

    private final static String USER_SERVICE= "userService";

    private List<BasicWishlistDTO> fallbackCircuitBreaker(Throwable e){
        log.info("Servizio per l'invio delle notifiche non disponibile");
        return null;
    }



    @Override  //Metodo per creare una lista dei desideri per l'utente
    public UUID addOrUpdateWishlist(WishlistDTO wishlistDTO, String username) {
        try {
            wishlistDTO.setUserUsername(username);

            if(!checkName(wishlistDTO.getName()) || !checkVisibility(wishlistDTO.getVisibility()))
                return null;

            Wishlist wishlistEntity = modelMapper.map(wishlistDTO, Wishlist.class);
            wishlistEntity.setOnDeleting(false);
            return wishlistRepository.save(wishlistEntity).getId();
        }
        catch (Exception | Error e) {
            log.debug("Errore nella creazione lista desideri");
            return null;
        }
    }


    @Override  //Metodo per prendere la lista dei desideri dell'utente
    public WishlistDTO getWishlist(UUID id, String username) {
        Wishlist wish= wishlistRepository.findWishlistByIdAndUserUsername(id, username);

        if(wish==null || wish.isOnDeleting())
            return null;
        return modelMapper.map(wish, WishlistDTO.class);
    }

    @Override
    public List<WishlistDTO> getAllWishlist(UUID id, String username) {
        return wishlistRepository.findAllByIdAndUserUsername(id, username)
                .stream()
                .map(a -> modelMapper.map(a, WishlistDTO.class))
                .toList();
    }

    @Override
    @CircuitBreaker(name= USER_SERVICE, fallbackMethod = "fallbackCircuitBreaker")
    public List<BasicWishlistDTO> getAllWishlists(String ownerUsername, String accessUsername, int visibility) {
        System.out.println("username dell'utente: "+ownerUsername+"visibilità: "+visibility);
        String vs= "";
        switch (visibility) {
            case 0 -> vs= "Pubblica";
            case 1 -> vs= "Condivisa";
            case 2 -> vs= "Privata";
        }
        //Caso in cui l'utente vuole accedere alle sue liste desideri
        if(ownerUsername.equals(accessUsername)) {
            List<Wishlist> wishlists= wishlistRepository.findAllByUserUsernameAndVisibility(ownerUsername, vs);

            List<Wishlist> result= new Vector<>();
            for(Wishlist wishlist: wishlists) {
                if(wishlist.isOnDeleting())
                    continue;

                result.add(wishlist);
            }
            return result.stream()
                    .map(a -> modelMapper.map(a, BasicWishlistDTO.class))
                    .toList();
        } else {
            if(visibility==0) { //Pubbliche
                List<Wishlist> wishlists= wishlistRepository.findAllByUserUsernameAndVisibility(ownerUsername, vs);

                List<Wishlist> result= new Vector<>();
                for(Wishlist wishlist: wishlists) {
                    if(wishlist.isOnDeleting())
                        continue;

                    result.add(wishlist);
                }
                return result.stream()
                        .map(a -> modelMapper.map(a, BasicWishlistDTO.class))
                        .toList();
            } else if(visibility==1) { //Condivisa
                //Caso in cui l'utente vuole accedere alle wishlist di un altro utente
                HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
                HttpHeaders headers = new HttpHeaders();
                headers.add("Authorization", request.getHeader("Authorization"));

                HttpEntity<String> entity = new HttpEntity<>(headers);

                ResponseEntity<Boolean> response= restTemplate.exchange(
                        "http://user-service/user-api/follower/" + accessUsername+"?username="+ownerUsername,
                        HttpMethod.GET,
                        entity,
                        boolean.class
                );

                if(response.getStatusCode()== HttpStatus.OK && response.getBody()) {
                    List<Wishlist> wishlists= wishlistRepository.findAllByUserUsernameAndVisibility(ownerUsername, vs);

                    List<Wishlist> result= new Vector<>();
                    for(Wishlist wishlist: wishlists) {
                        if(wishlist.isOnDeleting())
                            continue;

                        result.add(wishlist);
                    }
                    return result.stream()
                            .map(a -> modelMapper.map(a, BasicWishlistDTO.class))
                            .toList();
                }
            }
            return null;
        }
    }

    @Override
    public List<BasicWishlistDTO> getAllUserWishlists(String accessUsername) {
        List<Wishlist> wishlists= wishlistRepository.findAllByUserUsername(accessUsername);

        List<Wishlist> result= new Vector<>();
        for(Wishlist wishlist: wishlists) {
            if(wishlist.isOnDeleting())
                continue;
            result.add(wishlist);
        }
        return result.stream().map(a -> modelMapper.map(a, BasicWishlistDTO.class)).toList();
    }

    @Override
    public boolean deleteWishlist(UUID id) {
        try {
            Wishlist wishlist= wishlistRepository.findById(id).orElse(null);

            if(wishlist==null || wishlist.isOnDeleting())
                return false;

            wishlistRepository.deleteById(id);
            log.debug("Lista desideri eliminata correttamente");
            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nella cancellazione della lista desideri");
            return false;
        }
    }

    @Override
    public boolean changeVisibility(String username, ChangeVisibilityDTO changeVisibilityDTO) {
        try{
            Wishlist wishlist = wishlistRepository.findWishlistByIdAndUserUsername(changeVisibilityDTO.getWishId(), username);

            if(wishlist==null || wishlist.isOnDeleting())
                return false;

            switch (changeVisibilityDTO.getVisibility()) {
                case 0 -> wishlist.setVisibility("Pubblica");
                case 1 -> wishlist.setVisibility("Condivisa");
                case 2 -> wishlist.setVisibility("Privata");
                default -> {
                    return false;
                }
            }
            wishlistRepository.save(wishlist);
            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nella cancellazione della lista desideri");
            return false;
        }
    }



    @Override
    public List<WishlistDTO> validateOrRollbackDeleteUserWishlist(String username, boolean rollback) {
        try{
            System.out.println("Prima della presa delle wishlist");
            List<Wishlist> wishlists= wishlistRepository.findAllByUserUsername(username);

            System.out.println("Dopo la presa delle wishlist");
            if(wishlists.isEmpty())
                return new Vector<>();

            System.out.println("Ciclando le wishlist");
            for(Wishlist wishlist: wishlists) {
                if(wishlist.isOnDeleting() && !rollback)
                    continue;
                wishlist.setOnDeleting(!rollback);
            }

            System.out.println("Pre salvataggio wishlist");
            wishlistRepository.saveAll(wishlists);

            System.out.println("Post salvataggio wislist");
            return wishlists.stream()
                    .map(nt -> modelMapper.map(nt, WishlistDTO.class))
                    .toList();
        }catch(Exception | Error e){
            log.debug("Errore nell'inserimento della notifica per l'utente");
            return null;
        }
    }



    //METODI DI SERVIZIO
    private boolean checkName(String name) {
        return !name.isEmpty() && name.length()<=50;
    }

    private boolean checkVisibility(String visibility) {
        String[] vis= {"Pubblica", "Condivisa", "Privata"};

        return !visibility.isEmpty() &&
                (visibility.equals(vis[0]) || visibility.equals(vis[1]) || visibility.equals(vis[2]));
    }
}
