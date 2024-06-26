package org.caesar.productservice.GeneralService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.productservice.Data.Services.*;
import org.caesar.productservice.Dto.*;
import org.caesar.productservice.Dto.DTOOrder.BuyDTO;
import org.caesar.productservice.Dto.DTOOrder.OrderDTO;
import org.caesar.productservice.Utils.Utils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.caesar.productservice.Data.Services.AvailabilityService;
import org.caesar.productservice.Data.Services.ProductService;
import org.caesar.productservice.Data.Services.WishlistProductService;
import org.caesar.productservice.Data.Services.WishlistService;
import org.caesar.productservice.Dto.ImageDTO;
import org.caesar.productservice.Dto.ProductDTO;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GeneralServiceImpl implements GeneralService {

    private final AvailabilityService availabilityService;
    private final ProductService productService;
    private final ModelMapper modelMapper;
    private final ProductOrderService productOrderService;
    private final OrderService orderService;
    private final LastViewService lastViewService;
    private final ReviewService reviewService;
    private final WishlistService wishlistService;
    private final WishlistProductService wishlistProductService;
    private final Utils utils;


    @Override
    // Aggiunge il prodotto ricevuto da front al db dei prodotti
    public boolean addProduct(ProductDTO sendProductDTO) {
        // Mappa sendProductDTO a ProductDTO
        ProductDTO productDTO = modelMapper.map(sendProductDTO, ProductDTO.class);
        // Aggiorna l'ID del productDTO dopo averlo salvato
        productDTO.setId(productService.addOrUpdateProduct(productDTO).getId());
        availabilityService.addOrUpdateAvailability(sendProductDTO.getAvailabilities(), productDTO);
        return true;

    }

    @Override
    // Restituisce il carrello dell'utente con la lista dei prodotti al suo interno
    public List<ProductCartDTO> getCart(String username) {
        List<ProductOrderDTO> productCart= productOrderService.getProductOrdersByUsername(username);

        if(productCart==null || productCart.isEmpty())
            return null;

        List<ProductCartDTO> result= new Vector<>();
        ProductCartDTO prod;
        for(ProductOrderDTO p: productCart){
            prod = new ProductCartDTO();
            prod.setName(productService.getProductById(p.getId()).getName());
            prod.setId(p.getId());
            prod.setTotal(p.getTotal());
            prod.setQuantity(p.getQuantity());

            result.add(prod);
        }
        return result;
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
    public List<ImageDTO> getAllProductImages(UUID productID) {
        return List.of();
    }


    @Override
    @Transactional
    // Genera un nuovo carrello alla scelta del primo prodotto dell'utente
    public boolean createCart(String username, SendProductOrderDTO sendProductOrderDTO) {
        ProductDTO productDTO = productService.getProductById(sendProductOrderDTO.getProductID());

        if(productDTO==null)
            return false;

        ProductOrderDTO productOrderDTO = new ProductOrderDTO();

        productOrderDTO.setProductDTO(productDTO);
        productOrderDTO.setTotal(productDTO.getPrice()*sendProductOrderDTO.getQuantity());
        productOrderDTO.setQuantity(sendProductOrderDTO.getQuantity());
        productOrderDTO.setUsername(username);

        return productOrderService.save(productOrderDTO);
    }

    @Override
    @Transactional
    // Genera un ordine contenente gli articoli acquistati dall'utente e la notifica corrispondente
    public boolean createOrder(String username, BuyDTO buyDTO) {

        if(buyDTO.getAddressID() == null || buyDTO.getCardID() == null)
            return false;

        List<ProductOrderDTO> productInOrder = productOrderService.getProductOrdersByUsername(username);

        productInOrder.forEach(a -> System.out.println("Id prodotto " + a.getProductDTO().getId()));

        if(productInOrder==null || productInOrder.isEmpty())
            return false;

        List<ProductOrderDTO> productOrderDTOs = productInOrder.stream()
                .filter(productOrderDTO -> buyDTO.getProductsIds().contains(productOrderDTO.getProductDTO().getId()))
                .toList();

        OrderDTO orderDTO= new OrderDTO();
        orderDTO.setOrderNumber(generaCodice(8));
        orderDTO.setOrderState("Ricevuto");
        orderDTO.setOrderTotal(productOrderDTOs.stream().mapToDouble(ProductOrderDTO::getTotal).sum());
        orderDTO.setExpectedDeliveryDate(LocalDate.now().plusDays(5));
        orderDTO.setPurchaseDate(LocalDate.now());
        orderDTO.setRefund(false);
        orderDTO.setAddressID(buyDTO.getAddressID());
        orderDTO.setCardID(buyDTO.getCardID());
        orderDTO.setUsername(username);


        OrderDTO savedOrder = orderService.addOrder(orderDTO);

        if(savedOrder==null)
            return false;

        for(ProductOrderDTO productOrderDTO : productOrderDTOs){
            productOrderDTO.setOrderID(savedOrder);
        }

        if(productOrderService.saveAll(productOrderDTOs)) {

            return  utils.sendNotify(username,
                    "Ordine numero "+savedOrder.getOrderNumber()+" effettuato",
                    "Il tuo ordine è in fase di elaborazione e sarà consegnato il "+ savedOrder.getExpectedDeliveryDate()
            );


        }
        return false; //☺
    }

    // Generatore di codici per gli ordini
    public static String generaCodice(int lunghezza) {
        String CHARACTERS = "5LDG8OKXCSV4EZ1YU9IR0HT7WMAJB2FN3P6Q";
        SecureRandom RANDOM = new SecureRandom();

        StringBuilder codice = new StringBuilder(lunghezza);
        for (int i = 0; i < lunghezza; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            codice.append(CHARACTERS.charAt(index));
        }
        return codice.toString();
    }

    @Override
    // Restituisce il prodotto con le sue disponibilità e immagini
    public ProductDTO getProductAndAvailabilitiesAndImages(String username, UUID id){
        ProductDTO productDTO = productService.getProductById(id);
        if(productService.getProductById(id) != null){
            List<AvailabilityDTO> availabilities = availabilityService.getAvailabilitiesByProductID(productDTO);
            productDTO.setAvailabilities(availabilities);
            lastViewService.save(username, productDTO);
            return productDTO;
        }
        return null;
    }

    // Resituisce una lista di prodotti, risultato della ricerca coi valori dei parametri passati
    public List<ProductSearchDTO> searchProducts(String searchText, Double minPrice, Double maxPrice, Boolean isClothing) {
        List<ProductDTO> productDTO = productService.searchProducts(searchText, minPrice, maxPrice, isClothing);

        List<ProductSearchDTO> productSearchDTO = new Vector<>();
        ProductSearchDTO productSearchDTO1;
        AverageDTO averageDTO;

        for(ProductDTO p: productDTO){
            productSearchDTO1 = new ProductSearchDTO();
            averageDTO = reviewService.getReviewAverage(p.getId());

            productSearchDTO1.setAverageReview(averageDTO.getAvarege());
            productSearchDTO1.setReviewsNumber(averageDTO.getNummberOfReview());

            productSearchDTO1.setProductName(p.getName());
            productSearchDTO1.setPrice(p.getPrice());

            productSearchDTO.add(productSearchDTO1);
        }

        return productSearchDTO;
    }

    // Restituisce i prodotti visti di recente dall'utente
    public List<ProductSearchDTO> getLastView(String username){
        //Metodo per prendere tutte le tuple dei prodotti visti
        List<LastViewDTO> lastViewDTOS = lastViewService.getAllViewed(username);

        //Lista degli utlimi prodotti cliccati
        List<ProductDTO> productDTOS = new Vector<>();

        List<ProductSearchDTO> productSearchDTOS = new Vector<>();

        ProductSearchDTO productSearchDTO1;

        AverageDTO averageDTO;

        for(LastViewDTO l: lastViewDTOS){
            productDTOS.add(productService.getProductById(l.getId()));
        }

        for(ProductDTO p: productDTOS){
            productSearchDTO1 = new ProductSearchDTO();
            averageDTO = reviewService.getReviewAverage(p.getId());

            productSearchDTO1.setAverageReview(averageDTO.getAvarege());
            productSearchDTO1.setReviewsNumber(averageDTO.getNummberOfReview());

            productSearchDTO1.setProductName(p.getName());
            productSearchDTO1.setPrice(p.getPrice());

            productSearchDTOS.add(productSearchDTO1);
        }

        return productSearchDTOS;
    }

    @Override
    public List<ProductCartDTO> getOrder(String username, UUID orderId) {
        OrderDTO orderDTO = orderService.getOrder(username, orderId);

        List<ProductOrderDTO> productsOrder= productOrderService.getProductInOrder(username, orderDTO);

        if(productsOrder==null && productsOrder.isEmpty())
            return null;

        List<ProductDTO> products= productService.getAllProductsById(productsOrder.stream().map(a -> a.getProductDTO().getId()).toList());

        if(products==null && products.isEmpty())
            return null;

        List<ProductCartDTO> productCartDTOS = new Vector<>();
        ProductCartDTO productCartDTO;

        for (ProductOrderDTO productOrderDTO : productsOrder) {
            for (ProductDTO product : products) {
                if (productOrderDTO.getProductDTO().getId().equals(product.getId())) {
                    productCartDTO = new ProductCartDTO();

                    productCartDTO.setId(product.getId());
                    productCartDTO.setName(product.getName());
                    productCartDTO.setQuantity(productOrderDTO.getQuantity());
                    productCartDTO.setTotal(productOrderDTO.getTotal());

                    productCartDTOS.add(productCartDTO);
                }
            }
        }

        return productCartDTOS;
    }


    @Transactional
    @Override
    // Elimina l'intera wishlist dell'utente assieme a tutti i prodotti in essa contenuti
    public boolean deleteWishlist(String username, UUID wishlistID){
        WishlistDTO wishlistDTO = wishlistService.getWishlist(wishlistID, username);

        if(wishlistDTO==null)
            return false;

        return wishlistProductService.deleteAllProductsFromWishlist(wishlistDTO) && wishlistService.deleteWishlist(wishlistID);
    }


    public boolean deleteProductCart(String username, UUID productID){
        ProductDTO productDTO = productService.getProductById(productID);
        return productOrderService.deleteProductCart(username,productDTO);
    }

    @Override
    public boolean changeQuantity(String username, UUID productID, int quantity){
        ProductDTO productDTO = productService.getProductById(productID);
        return productOrderService.changeQuantity(username,productDTO,quantity);
    }

    @Override
    public boolean saveLater(String username, UUID productID) {
        ProductDTO productDTO = modelMapper.map(productService.getProductById(productID), ProductDTO.class);
        return productOrderService.saveLater(username,productDTO);
    }

    @Override
    @Transactional
    public boolean addProductIntoWishList(String username, SendWishlistProductDTO wishlistProductDTO) {

        WishListProductDTO wishListProductDTO = getWishListProductDTO(username, wishlistProductDTO);

        System.out.println("wishlistID: "+ wishListProductDTO.getWishlistID().getId());
        System.out.println("productID: "+ wishListProductDTO.getProductID().getId());

        if(wishListProductDTO==null)
            return false;

        return wishlistProductService.addOrUpdateWishlistProduct(wishListProductDTO);
    }

    @Override
    @Transactional
    public boolean deleteProductFromWishList(String username, SendWishlistProductDTO wishlistProductDTO) {
        WishListProductDTO wishListProductDTO= getWishListProductDTO(username, wishlistProductDTO);

        System.out.println("wishlistID: "+ wishListProductDTO.getWishlistID().getId());
        System.out.println("productID: "+ wishListProductDTO.getProductID().getId());

        if(wishListProductDTO==null)
            return false;

        return wishlistProductService.deleteProductFromWishlist(wishListProductDTO);
    }

    @Override
    @Transactional
    public boolean deleteProductsFromWishList(String username, UUID wishlistId) {
        WishlistDTO wishlistDTO= wishlistService.getWishlist(wishlistId, username);

        if(wishlistDTO==null)
            return false;

        return wishlistProductService.deleteAllProductsFromWishlist(wishlistDTO);
    }


    //Metodi di servizio
    private WishListProductDTO getWishListProductDTO(String username, SendWishlistProductDTO wishlistProductDTO) {
        ProductDTO productDTO= productService.getProductById(wishlistProductDTO.getProductID());

        if(productDTO==null)
            return null;

        WishlistDTO wishlistDTO= wishlistService.getWishlist(wishlistProductDTO.getWishlistID(), username);

        if(wishlistDTO==null)
            return null;

        WishListProductDTO wishListProductDTO= new WishListProductDTO();

        wishListProductDTO.setWishlistID(wishlistDTO);
        wishListProductDTO.setProductID(productDTO);

        System.out.println("wishlistID: "+ wishlistDTO.getId());
        System.out.println("productID: "+ productDTO.getId());

        return wishListProductDTO;
    }



    @Override
    public WishProductDTO getWishlistProductsByWishlistID(UUID wishlistID, String username) {

        WishlistDTO wishlistDTO = wishlistService.getWishlist(wishlistID, username);

        if(wishlistDTO==null){
            return null;
        }

        List<WishListProductDTO> wishListProductDTOS = wishlistProductService.getWishlistProductsByWishlistID(wishlistDTO);

        if(wishListProductDTOS == null){
            System.out.println("SOno vuotas");
            return null;
        }

        WishProductDTO wishProductDTO = new WishProductDTO();

        SingleWishListProductDTO singleWishListProductDTO;

        List<SingleWishListProductDTO> singleWishListProductDTOS = new Vector<>();

        for(WishListProductDTO wishListProductDTO: wishListProductDTOS){
            System.out.println("Nome prodotto: " + wishListProductDTO.getProductID().getName());
            System.out.println("Prezzo: " + wishListProductDTO.getProductID().getPrice());
            singleWishListProductDTO = new SingleWishListProductDTO();

            singleWishListProductDTO.setProductName(wishListProductDTO.getProductID().getName());
            singleWishListProductDTO.setPrice(wishListProductDTO.getProductID().getPrice());

            singleWishListProductDTOS.add(singleWishListProductDTO);

        }

        wishProductDTO.setSingleWishListProductDTOS(singleWishListProductDTOS);
        wishProductDTO.setVisibility(wishlistDTO.getVisibility());

        return wishProductDTO;
    }

}
