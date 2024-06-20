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

    @Override
    public UUID addOrUpdateWishlist(WishlistDTO wishlistDTO) {

        if (!wishlistRepository.existsById(wishlistDTO.getId())) {
            return null;
        }
        try {
            Wishlist wishlistEntity = modelMapper.map(wishlistDTO, Wishlist.class);
            log.debug("Lista desideri creata");
            return wishlistRepository.save(wishlistEntity).getId();
        }
        catch (RuntimeException | Error e) {
            log.debug("Errore nella creazione lista desideri");
            return null;
        }
    }

    @Override
    public WishlistDTO getWishlist(UUID id) {
        return modelMapper.map(wishlistRepository.findById(id), WishlistDTO.class);
    }

    @Override
    //Restituisce tutte le liste desideri con la visibilità specificata (privata, pubblica, condivisa)
    public List<WishlistDTO> getAllWishlists(String userUsername, String visibility) {
        return wishlistRepository.findAllByUserUsernameAndVisibility(userUsername, visibility)
                .stream()
                .map(wishlist -> modelMapper.map(wishlist, WishlistDTO.class))
                .toList();
    }

    @Override
    public boolean deleteWishlist(UUID id) {
        try {
            wishlistRepository.deleteById(id);
            log.debug("Wishlist id: {} cancellata \n", id);
            return true;
        } catch (Exception e) {
            log.debug("Errore nella cancellazione della lista desideri \n");
            return false;
        }
    }
}
