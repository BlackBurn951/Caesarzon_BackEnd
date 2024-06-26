package org.caesar.productservice.Data.Services.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.productservice.Data.Dao.WishlistRepository;
import org.caesar.productservice.Data.Entities.Wishlist;
import org.caesar.productservice.Data.Services.WishlistService;
import org.caesar.productservice.Dto.ReviewDTO;
import org.caesar.productservice.Dto.WishlistDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WishlistServiceImpl implements WishlistService {

    private final ModelMapper modelMapper;
    private final WishlistRepository wishlistRepository;
    private final WishlistProductServiceImpl wishlistProductServiceImpl;

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
    public List<WishlistDTO> getAllWishlists(String userUsername, String visibility) {
        //Restituisce una lista di "liste desideri" dell'utente specificato in base alla visibilità richiesta
            return wishlistRepository.findAllByUserUsernameAndVisibility(userUsername, visibility)
                           .stream()
                           .map(wishlist -> modelMapper.map(wishlist, WishlistDTO.class))
                           .toList();
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
