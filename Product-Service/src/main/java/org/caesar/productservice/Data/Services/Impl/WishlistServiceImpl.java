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
            return wishlistRepository.save(wishlistEntity).getId();
        }
        catch (RuntimeException | Error e) {
            log.debug("Errore nella creazione lista desideri");
            return null;
        }
    }


    @Override  //Metodo per prendere la lista dei desideri dell'utente
    public WishlistDTO getWishlist(UUID id, String username) {
        return modelMapper.map(wishlistRepository.findWishlistByIdAndUserUsername(id, username), WishlistDTO.class);
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
        String vs= "";
        switch (visibility) {
            case 0 -> vs= "Pubblica";
            case 1 -> vs= "Condivisa";
            case 2 -> vs= "Privata";
        }
        //Caso in cui l'utente vuole accedere alle sue liste desideri
        if(ownerUsername.equals(accessUsername)) {
            return wishlistRepository.findAllByUserUsernameAndVisibility(ownerUsername, vs)
                    .stream()
                    .map(a -> modelMapper.map(a, BasicWishlistDTO.class))
                    .toList();
        } else {
            if(visibility==0) { //Pubbliche
                return wishlistRepository.findAllByUserUsernameAndVisibility(ownerUsername, vs)
                        .stream()
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
                    return wishlistRepository.findAllByUserUsernameAndVisibility(ownerUsername, vs)
                            .stream()
                            .map(a -> modelMapper.map(a, BasicWishlistDTO.class))
                            .toList();
                }
            }
            return null;
        }
    }

    @Override
    public List<BasicWishlistDTO> getAllUserWishlists(String accessUsername) {
        return wishlistRepository.findAllByUserUsername(accessUsername).stream().map(a -> modelMapper.map(a, BasicWishlistDTO.class)).toList();
    }

    @Override
    public boolean deleteWishlist(UUID id) {
        try {
            wishlistRepository.deleteById(id);
            log.debug("Lista desideri eliminata correttamente");
            return true;
        } catch (Exception e) {
            log.debug("Errore nella cancellazione della lista desideri");
            return false;
        }
    }

    @Override
    public boolean changeVisibility(String username, ChangeVisibilityDTO changeVisibilityDTO) {
        try{
            Wishlist wishlist = wishlistRepository.findWishlistByIdAndUserUsername(changeVisibilityDTO.getWishId(), username);

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
        } catch (Exception e) {
            log.debug("Errore nella cancellazione della lista desideri");
            return false;
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
