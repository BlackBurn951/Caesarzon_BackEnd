package org.caesar.productservice.GeneralService;

import org.caesar.productservice.Dto.*;
import org.caesar.productservice.Dto.DTOOrder.BuyDTO;
import org.caesar.productservice.Dto.DTOOrder.UnavailableDTO;
import org.caesar.productservice.Dto.ImageDTO;
import org.caesar.productservice.Dto.ProductDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface GeneralService {

    //SEZIONE DEI PRODOTTI E STRETTAMENTE CORRELATI
    byte[] getProductImage(UUID id);
    List<ImageDTO> getAllProductImages(UUID productID);
    ProductDTO getProductAndAvailabilitiesAndImages(String username, UUID id);
    UUID addProduct(ProductDTO ProductDTO, boolean isNew);
    boolean deleteProduct(UUID id);
    boolean saveImage(UUID productId, MultipartFile file, boolean isNew);

    //SEZIONE RECENSIONI
    List<ReviewDTO> getProductReviews(UUID productID, int str);
    List<Integer> getReviewScore(UUID productId);
    AverageDTO getReviewAverage(UUID productId);
    String addReview(ReviewDTO reviewDTO, String username);
    boolean deleteReviewByUser(String username, UUID productId);



    //SEZIONE DEL CARRELLO
    List<ProductCartDTO> getCart(String username);
    boolean createCart(String username, SendProductOrderDTO sendProductOrderDTO);
    boolean saveLater(String username, UUID productDTO);
    boolean changeQuantity(String username, UUID productID, ChangeCartDTO changeCartDTO);
    boolean deleteProductCart(String username, UUID productID);


    //SEZIONE DELL'ORDINE
    List<ProductCartDTO> getOrder(String username, UUID orderId);
    String createOrder(String username, BuyDTO buyDTO);
    boolean updateOrder(String username, UUID orderId);
    List<UnavailableDTO> checkAvailability(String username, List<UUID> productIds);
    boolean rollbackCheckAvailability(String username, List<UUID> productIds);
    String checkOrder(String username, BuyDTO buyDTO, boolean payMethod, boolean platform);


    //SEZIONE RICERCA DEI PRODOTTI
    List<ProductSearchDTO> searchProducts(String searchText, Double minPrice, Double maxPrice, Boolean isClothing);
    List<ProductSearchDTO> getLastView(String username);
    List<ProductSearchDTO> getNewProducts();
    List<ProductSearchDTO> getOffers();


    //SEZIONE DELLE WISHLIST
    WishProductDTO getWishlistProductsByWishlistID(UUID wishlistID, String ownerUsername, String accessUsername);
    boolean addProductIntoWishList(String username, SendWishlistProductDTO sendWishlistProductDTO);
    boolean deleteProductFromWishList(String username, UUID wishId, UUID productId);
    boolean deleteProductsFromWishList(String username, UUID wishlistId);
    boolean deleteWishlist( String username, UUID wishlistID);
}
