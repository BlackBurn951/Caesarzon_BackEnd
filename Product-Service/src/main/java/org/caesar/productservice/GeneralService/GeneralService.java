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
    List<ProductCartDTO> getOrder(String username, UUID orderId);
    boolean deleteWishlist( UUID wishlistID);
    boolean deleteProductCart(String username, UUID productID);
    boolean changeQuantity(String username, UUID productID, int quantity);
    boolean saveLater(String username, UUID productDTO);
}
