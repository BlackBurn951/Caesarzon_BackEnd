package org.caesar.productservice.GeneralService;

import org.caesar.productservice.Data.Entities.Product;
import org.caesar.productservice.Dto.*;
import org.caesar.productservice.Dto.DTOOrder.BuyDTO;
import org.caesar.productservice.Dto.DTOOrder.UnavailableDTO;
import org.caesar.productservice.Dto.ImageDTO;
import org.caesar.productservice.Dto.ProductDTO;

import java.util.List;
import java.util.UUID;

public interface GeneralService {

    //SEZIONE DEI PRODOTTI E STRETTAMENTE CORRELATI
    List<ImageDTO> getProductImages(UUID id);
    List<ImageDTO> getAllProductImages(UUID productID);
    ProductDTO getProductAndAvailabilitiesAndImages(String username, UUID id);
    boolean addProduct(ProductDTO ProductDTO);
    boolean deleteProduct(UUID id);
    boolean deleteAvailabilityByProduct(Product product);

    //SEZIONE RECENSIONI
    List<ReviewDTO> getProductReviews(UUID productID);
    List<Integer> getReviewScore(UUID productId);
    AverageDTO getReviewAverage(UUID productId);
    boolean addReview(ReviewDTO reviewDTO, String username);
    boolean deleteReviewByUser(String username, UUID productId);
    boolean deleteReviewByAdmin(String username, UUID productId);


    //SEZIONE DEL CARRELLO
    List<ProductCartDTO> getCart(String username);
    boolean createCart(String username, SendProductOrderDTO sendProductOrderDTO);
    boolean saveLater(String username, UUID productDTO);
    boolean changeQuantity(String username, UUID productID, int quantity, String size);
    boolean deleteProductCart(String username, UUID productID);


    //SEZIONE DELL'ORDINE
    List<ProductCartDTO> getOrder(String username, UUID orderId);
    String createOrder(String username, BuyDTO buyDTO);
    boolean updateOrder(String username, UUID orderId);
    boolean updateNotifyOrder();
    List<UnavailableDTO> checkAvailability(String username, List<UUID> productIds);
    String checkOrder(String username, BuyDTO buyDTO, boolean payMethod);


    //SEZIONE RICERCA DEI PRODOTTI
    List<ProductSearchDTO> searchProducts(String searchText, Double minPrice, Double maxPrice, Boolean isClothing);
    List<ProductSearchDTO> getLastView(String username);
    List<ProductSearchDTO> getNewProducts();
    List<ProductSearchDTO> getOffers();


    //SEZIONE DELLE WISHLIST
    WishProductDTO getWishlistProductsByWishlistID(UUID wishlistId, String username);
    boolean addProductIntoWishList(String username, SendWishlistProductDTO sendWishlistProductDTO);
    boolean deleteProductFromWishList(String username, UUID wishId, UUID productId);
    boolean deleteProductsFromWishList(String username, UUID wishlistId);
    boolean deleteWishlist( String username, UUID wishlistID);
}
