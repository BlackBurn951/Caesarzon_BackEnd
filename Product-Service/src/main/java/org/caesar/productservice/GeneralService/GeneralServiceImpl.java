package org.caesar.productservice.GeneralService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.productservice.Data.Services.*;
import org.caesar.productservice.Dto.*;
import org.caesar.productservice.Dto.DTOOrder.BuyDTO;
import org.caesar.productservice.Dto.DTOOrder.OrderDTO;
import org.modelmapper.ModelMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.caesar.productservice.Data.Services.AvailabilityService;
import org.caesar.productservice.Data.Services.ProductService;
import org.caesar.productservice.Data.Services.WishlistProductService;
import org.caesar.productservice.Data.Services.WishlistService;
import org.caesar.productservice.Dto.ImageDTO;
import org.caesar.productservice.Dto.ProductDTO;
import org.springframework.http.HttpStatus;

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
    private final RestTemplate restTemplate;
    private final LastViewService lastViewService;
    private final ReviewService reviewService;
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
    public boolean createOrder(String username, BuyDTO buyDTO) {

        if(buyDTO.getAddressID() == null || buyDTO.getCardID() == null)
            return false;

        List<ProductOrderDTO> productInOrder = productOrderService.getProductOrdersByUsername(username);

        if(productInOrder==null || productInOrder.isEmpty())
            return false;

        List<ProductOrderDTO> productOrderDTOs = productInOrder.stream()
                .filter(productOrderDTO -> buyDTO.getProductsIds().contains(productOrderDTO.getProductDTO().getId()))
                .toList();

        OrderDTO orderDTO= new OrderDTO();
        orderDTO.setOrderNumber(generaCodice(8));
        orderDTO.setOrderState("Ricevuto");
        orderDTO.setExpectedDeliveryDate(LocalDate.now().plusDays(5));
        orderDTO.setPurchaseDate(LocalDate.now());
        orderDTO.setRefund(false);
        orderDTO.setAddressID(buyDTO.getAddressID());
        orderDTO.setCardID(buyDTO.getCardID());
        orderDTO.setUsername(username);

        orderDTO.setTotalOrder(productOrderDTOs.stream().mapToDouble(ProductOrderDTO::getTotal).sum());

        OrderDTO savedOrder = orderService.addOrder(orderDTO);

        if(savedOrder==null)
            return false;

        productInOrder.forEach(productOrderDTO -> productOrderDTO.setOrderID(savedOrder));

        if(productOrderService.saveAll(productOrderDTOs)) {
            HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", request.getHeader("Authorization"));

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("date", String.valueOf(LocalDate.now()));
            requestBody.put("subject", "Ordine numero "+savedOrder.getOrderNumber()+" effettuato");
            requestBody.put("user", username);
            requestBody.put("read", "false");
            requestBody.put("explaination", "Il tuo ordine è in fase di elaborazione e sarà il "+ String.valueOf(savedOrder.getExpectedDeliveryDate()));

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);
            return restTemplate.exchange(
                    "http://notification-service/notification",
                    HttpMethod.POST,
                    entity,
                    String.class
            ).getStatusCode()== HttpStatus.OK;
        }
        return false; //☺
    }



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


    public List<ProductSearchDTO> searchProducts(String searchText, Double minPrice, Double maxPrice, Boolean isClothing) {
        List<ProductDTO> productDTO = productService.searchProducts(searchText, minPrice, maxPrice, isClothing);

        List<ProductSearchDTO> productSearchDTO = new Vector<>();
        ProductSearchDTO productSearchDTO1;
        AverageDTO averageDTO;

        for(ProductDTO p: productDTO){
            productSearchDTO1 = new ProductSearchDTO();
            averageDTO = reviewService.getReviewAverage(p.getId());

            productSearchDTO1.setAverageReview(averageDTO.getAverage());
            productSearchDTO1.setReviewsNumber(averageDTO.getNumberOfReviews());

            productSearchDTO1.setProductName(p.getName());
            productSearchDTO1.setPrice(p.getPrice());

            productSearchDTO.add(productSearchDTO1);
        }

        return productSearchDTO;
    }

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

            productSearchDTO1.setAverageReview(averageDTO.getAverage());
            productSearchDTO1.setReviewsNumber(averageDTO.getNumberOfReviews());

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
    public boolean deleteWishlist(UUID wishlistID){
        return wishlistProductService.deleteAllWishlistProductsByWishlistID(wishlistID) && wishlistService.deleteWishlist(wishlistID);
    }

}
