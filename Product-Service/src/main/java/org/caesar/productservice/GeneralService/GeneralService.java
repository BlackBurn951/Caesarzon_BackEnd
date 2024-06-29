package org.caesar.productservice.GeneralService;

import org.caesar.productservice.Dto.*;
import org.caesar.productservice.Dto.DTOOrder.BuyDTO;
import org.caesar.productservice.Dto.DTOOrder.UnavailableDTO;
import org.caesar.productservice.Dto.ImageDTO;
import org.caesar.productservice.Dto.ProductDTO;

import java.util.List;
import java.util.UUID;

public interface GeneralService {

    List<ProductCartDTO> getCart(String username);
    boolean addProduct(ProductDTO ProductDTO);
    boolean deleteProduct(UUID id);
    List<ImageDTO> getProductImages(UUID id);
    List<ImageDTO> getAllProductImages(UUID productID);
    boolean createCart(String username, SendProductOrderDTO sendProductOrderDTO);
    String createOrder(String username, BuyDTO buyDTO);
    ProductDTO getProductAndAvailabilitiesAndImages(String username, UUID id);
    List<ProductSearchDTO> searchProducts(String searchText, Double minPrice, Double maxPrice, Boolean isClothing);
    List<ProductSearchDTO> getLastView(String username);
    List<ProductCartDTO> getOrder(String username, UUID orderId);
    boolean deleteWishlist( String username, UUID wishlistID);
    boolean deleteProductCart(String username, UUID productID);
    boolean changeQuantity(String username, UUID productID, int quantity, String size);
    boolean saveLater(String username, UUID productDTO);
    boolean addProductIntoWishList(String username, SendWishlistProductDTO sendWishlistProductDTO);
    boolean deleteProductFromWishList(String username, UUID wishId, UUID productId);
    boolean deleteProductsFromWishList(String username, UUID wishlistId);
    WishProductDTO getWishlistProductsByWishlistID(UUID wishlistId, String username);
    boolean updateOrder(String username, UUID orderId);
    boolean updateNotifyOrder();
    List<UnavailableDTO> checkAvailability(String username, List<UUID> productIds);
    String checkOrder(String username, BuyDTO buyDTO, boolean payMethod);
    List<ProductSearchDTO>newProducts();
    List<ProductSearchDTO>offer();


}
