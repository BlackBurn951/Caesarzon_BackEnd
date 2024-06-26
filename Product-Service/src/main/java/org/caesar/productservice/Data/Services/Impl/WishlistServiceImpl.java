package org.caesar.productservice.Data.Services.Impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.productservice.Data.Dao.WishlistRepository;
import org.caesar.productservice.Data.Entities.Wishlist;
import org.caesar.productservice.Data.Services.WishlistService;
import org.caesar.productservice.Dto.BasicWishlistDTO;
import org.caesar.productservice.Dto.ReviewDTO;
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
    private final WishlistProductServiceImpl wishlistProductServiceImpl;
    private final RestTemplate restTemplate;


    //Metodo per creare una lista dei desideri per l'utente
    @Override
    public UUID addOrUpdateWishlist(WishlistDTO wishlistDTO, String username) {
        try {
            wishlistDTO.setUserUsername(username);
            Wishlist wishlistEntity = modelMapper.map(wishlistDTO, Wishlist.class);
            return wishlistRepository.save(wishlistEntity).getId();
        }
        catch (RuntimeException | Error e) {
            log.debug("Errore nella creazione lista desideri");
            return null;
        }
    }

    //Metodo per prendere la lista dei desideri dell'utente
    @Override
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
    public List<BasicWishlistDTO> getAllWishlists(String ownerUsername, String accessUsername, int visibility) {
        //Caso in cui l'utente vuole accedere alle sue liste desideri
        if(ownerUsername.equals(accessUsername)) {
            return wishlistRepository.findAllByUserUsername(ownerUsername)
                    .stream()
                    .map(a -> modelMapper.map(a, BasicWishlistDTO.class))
                    .toList();
        } else {
            String vs= "";
            switch (visibility) {
                case 0 -> vs= "Pubblica";
                case 1 -> vs= "Besties";
            }

            if(visibility==0) { //Pubbliche
                return wishlistRepository.findAllByUserUsernameAndVisibility(ownerUsername, vs)
                        .stream()
                        .map(a -> modelMapper.map(a, BasicWishlistDTO.class))
                        .toList();
            } else if(visibility==2) { //Besties
                //Caso in cui l'utente vuole accedere alle wishlist di un altro utente
                HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
                HttpHeaders headers = new HttpHeaders();
                headers.add("Authorization", request.getHeader("Authorization"));

                HttpEntity<String> entity = new HttpEntity<>(headers);

                ResponseEntity<Boolean> response= restTemplate.exchange(
                        "http://user-service/user-api/follower/" + accessUsername,
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
}
