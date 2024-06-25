package org.caesar.productservice.GeneralService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.productservice.Data.Entities.Availability;
import org.caesar.productservice.Data.Services.AvailabilityService;
import org.caesar.productservice.Data.Services.ProductService;
import org.caesar.productservice.Data.Services.WishlistProductService;
import org.caesar.productservice.Data.Services.WishlistService;
import org.caesar.productservice.Dto.ImageDTO;
import org.caesar.productservice.Dto.ProductDTO;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GeneralServiceImpl implements GeneralService {

    private final AvailabilityService availabilityService;
    private final ProductService productService;
    private final ModelMapper modelMapper;
    private final WishlistService wishlistService;
    private final WishlistProductService wishlistProductService;


    @Override
    public boolean addProduct(ProductDTO sendProductDTO) {

        // Mappa sendProductDTO a ProductDTO
        ProductDTO productDTO = modelMapper.map(sendProductDTO, ProductDTO.class);

        // Aggiorna l'ID del productDTO dopo averlo salvato
        productDTO.setId(productService.addOrUpdateProduct(productDTO).getId());

        availabilityService.addOrUpdateAvailability(sendProductDTO.getAvailabilities(), productDTO);


        return true;

    }

    @Override
    public boolean deleteProduct(UUID id) {
        return false;
    }

    @Override
    public List<ImageDTO> getProductImages(UUID id) {
        return List.of();
    }

    @Override
    public List<Availability> getAvailabilityByProductID(UUID productID) {
        List<Availability> availabilities = new ArrayList<>();
        System.out.println("Sono in getAvailabilityByProductID");
        for(Availability availability : availabilityService.getAll()) {
            if(availability.getProduct().getId().equals(productID)) {
                availabilities.add(availability);

            }
        }
        return availabilities;
    }

    @Override
    public List<ImageDTO> getAllProductImages(UUID productID) {
        return List.of();
    }

    @Transactional
    @Override
    public boolean deleteWishlist(UUID wishlistID){
        return wishlistProductService.deleteAllWishlistProductsByWishlistID(wishlistID) && wishlistService.deleteWishlist(wishlistID);
    }

}
