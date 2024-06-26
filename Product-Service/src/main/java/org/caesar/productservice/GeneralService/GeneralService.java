package org.caesar.productservice.GeneralService;

import org.caesar.productservice.Dto.*;
import org.caesar.productservice.Dto.DTOOrder.BuyDTO;
import org.caesar.productservice.Data.Entities.Availability;
import org.caesar.productservice.Dto.ImageDTO;
import org.caesar.productservice.Dto.ProductDTO;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

public interface GeneralService {

    List<ProductCartDTO> getCart(String username);
    boolean addProduct(ProductDTO ProductDTO);
    boolean deleteProduct(UUID id);
    List<ImageDTO> getProductImages(UUID id);
    List<ImageDTO> getAllProductImages(UUID productID);
    boolean createCart(String username, SendProductOrderDTO sendProductOrderDTO);
    boolean createOrder(String username, BuyDTO buyDTO);
    ProductDTO getProductAndAvailabilitiesAndImages(String username, UUID id);
    List<ProductSearchDTO> searchProducts(String searchText, Double minPrice, Double maxPrice, Boolean isClothing);
    List<ProductSearchDTO> getLastView(String username);

    boolean deleteWishlist( UUID wishlistID);
}
