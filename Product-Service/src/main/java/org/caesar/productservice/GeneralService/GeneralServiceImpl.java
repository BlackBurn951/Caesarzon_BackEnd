package org.caesar.productservice.GeneralService;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.productservice.Data.Services.*;
import org.caesar.productservice.Dto.*;
import org.caesar.productservice.Dto.DTOOrder.BuyDTO;
import org.caesar.productservice.Dto.DTOOrder.OrderDTO;
import org.caesar.productservice.Dto.DTOOrder.UnavailableDTO;
import org.caesar.productservice.Utils.Utils;
import org.modelmapper.ModelMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.caesar.productservice.Data.Services.AvailabilityService;
import org.caesar.productservice.Data.Services.ProductService;
import org.caesar.productservice.Data.Services.WishlistProductService;
import org.caesar.productservice.Data.Services.WishlistService;
import org.caesar.productservice.Dto.ImageDTO;
import org.caesar.productservice.Dto.ProductDTO;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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
    private final RestTemplate restTemplate;
    private final PayPalService payPalService;


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
            prod.setSize(p.getSize());

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
    @Transactional   // Genera un nuovo carrello alla scelta del primo prodotto dell'utente
    public boolean createCart(String username, SendProductOrderDTO sendProductOrderDTO) {
        ProductDTO productDTO = productService.getProductById(sendProductOrderDTO.getProductID());
        //TODO CHECK DELLA DISPONIBILITà
        if(productDTO==null)
            return false;

        ProductOrderDTO productOrderDTO = new ProductOrderDTO();

        productOrderDTO.setProductDTO(productDTO);
        productOrderDTO.setTotal(productDTO.getPrice()*sendProductOrderDTO.getQuantity());
        productOrderDTO.setQuantity(sendProductOrderDTO.getQuantity());
        productOrderDTO.setUsername(username);
        productOrderDTO.setSize(sendProductOrderDTO.getSize());

        return productOrderService.save(productOrderDTO);
    }

    @Override
    @Transactional   // Genera un ordine contenente gli articoli acquistati dall'utente e la notifica corrispondente
    public String checkOrder(String username, BuyDTO buyDTO, boolean payMethod) {  //PayMethod -> true carta -> false paypal

        List<ProductOrderDTO> productInOrder= getProductInOrder(username, buyDTO.getProductsIds());

        if(productInOrder==null || productInOrder.isEmpty()) {
            changeAvaibility(productInOrder, true);
            return "Errore";
        }

        //Controllo che vengano effettivamente passati indirizzo e carta per pagare
        if(buyDTO.getAddressID() == null || (payMethod && buyDTO.getCardID() == null) ) {
            changeAvaibility(productInOrder, true);
            return "Errore";
        }

        //Chiamata per veificare che l'utente che vuole acquistare abbia quell'indirizzo e quella carta
        if(!checkAddress(buyDTO.getAddressID())) {
            changeAvaibility(productInOrder, true);
            return "Errore";
        }

        double total= productInOrder.stream().mapToDouble(ProductOrderDTO::getTotal).sum();

        if(payMethod) {
            if(!checkPayment(buyDTO.getCardID(), total)) {
                changeAvaibility(productInOrder, true);
                return "Errore";
            }
            buyDTO.setTotal(total);
            return createOrder(username, buyDTO);
        } else {
            try {
                Payment payment = payPalService.createPayment(
                        total, "EUR", "paypal",
                        "sale", "Pagamento ordine",
                        "http://localhost:4200/pagamento", //TODO REDIRECT SUL FRONT ANCHE
                        "http://localhost:4200/personal-data?total="+total);
                for (Links link : payment.getLinks()) {
                    if (link.getRel().equals("approval_url")) {
                        return "redirect:" + link.getHref();
                    }
                }
            } catch (PayPalRESTException e) {
                e.printStackTrace();
            }
            return "Errore";
        }
    }

    @Override
    public List<ProductSearchDTO> newProducts() {
        List<ProductDTO> productDTO = productService.getLastProducts();

        return metodoAusiliario(productDTO);
    }

    @Override
    public List<ProductSearchDTO> getOffers() {
        List<ProductDTO> products= productService.getOffer();

        if(products==null || products.isEmpty())
            return null;

        List<ProductSearchDTO> result= new Vector<>();
        ProductSearchDTO prod;
        AverageDTO average;
        for(ProductDTO p: products){
            average= reviewService.getReviewAverage(p.getId());

            prod= new ProductSearchDTO();
            if(average==null) {
                prod.setReviewsNumber(0);
                prod.setAverageReview(0.0);
            } else {
                prod.setReviewsNumber(average.getNummberOfReview());
                prod.setAverageReview(average.getAvarege());
            }
            prod.setProductId(p.getId());
            prod.setProductName(p.getName());
            prod.setPrice(p.getPrice());
            prod.setDiscount(p.getDiscount());

            result.add(prod);
        }

        return result;
    }

    @Override
    @Transactional
    public String createOrder(String username, BuyDTO buyDTO) {
        List<ProductOrderDTO> productInOrder= getProductInOrder(username, buyDTO.getProductsIds());

        if(productInOrder==null || productInOrder.isEmpty()) {
            changeAvaibility(productInOrder, true);
            return "Errore";
        }

        OrderDTO orderDTO= new OrderDTO();
        orderDTO.setOrderNumber(generaCodice(8));
        orderDTO.setOrderState("Ricevuto");
        orderDTO.setOrderTotal(buyDTO.getTotal());
        orderDTO.setExpectedDeliveryDate(LocalDate.now().plusDays(5));
        orderDTO.setPurchaseDate(LocalDate.now());
        orderDTO.setRefund(false);
        orderDTO.setAddressID(buyDTO.getAddressID());
        orderDTO.setCardID(buyDTO.getCardID());
        orderDTO.setUsername(username);


        OrderDTO savedOrder = orderService.addOrder(orderDTO);

        if(savedOrder==null) {
            changeAvaibility(productInOrder, true);
            return "Errore";
        }

        for(ProductOrderDTO productOrderDTO : productInOrder)
            productOrderDTO.setOrderDTO(savedOrder);

        if(productOrderService.saveAll(productInOrder) &&
                utils.sendNotify(username,
                "Ordine numero "+savedOrder.getOrderNumber()+" effettuato",
                "Il tuo ordine è in fase di elaborazione e sarà consegnato il "+ savedOrder.getExpectedDeliveryDate()))
           return "Ordine effettuatop con successo!";
        else {
            changeAvaibility(productInOrder, true);
            return "Errore"; //☺
        }
    }

    @Override
    @Transactional
    public List<UnavailableDTO> checkAvailability(String username, List<UUID> productIds) {

        //Presa di tutti i prodotti presenti nel carello dell'utente
        List<ProductOrderDTO> productInOrder= getProductInOrder(username, productIds);

        if(productInOrder==null || productInOrder.isEmpty())
            return null;

        //Controllo delle disponibilità
        List<ProductDTO> productWithoutAvailability= new Vector<>();

        for(ProductOrderDTO p: productInOrder){
            AvailabilityDTO availabilityDTO= availabilityService.getAvailabilitieByProductId(p.getProductDTO(), p.getSize());

            if(availabilityDTO==null || availabilityDTO.getAmount()<p.getQuantity())
                productWithoutAvailability.add(p.getProductDTO());
        }

        if(!productWithoutAvailability.isEmpty()) {
            return setPossibleAvailability(productWithoutAvailability);
        }

        if(changeAvaibility(productInOrder, false)) {
            List<UnavailableDTO> unavailableDTOS = new Vector<>();
            unavailableDTOS.add(null);

            return unavailableDTOS;
        }
        return null;
    }

    @Override
    // Restituisce il prodotto con le sue disponibilità e immagini
    public ProductDTO getProductAndAvailabilitiesAndImages(String username, UUID id){
        ProductDTO productDTO = productService.getProductById(id);
        if(productDTO != null){
            List<AvailabilityDTO> availabilities = availabilityService.getAvailabilitiesByProductID(productDTO);
            for(AvailabilityDTO availabilityDTO: availabilities)
                availabilityDTO.setProduct(null);
            productDTO.setAvailabilities(availabilities);
            //lastViewService.save(username, productDTO);
            return productDTO;
        }
        return null;
    }

    // Resituisce una lista di prodotti, risultato della ricerca coi valori dei parametri passati
    public List<ProductSearchDTO> searchProducts(String searchText, Double minPrice, Double maxPrice, Boolean isClothing) {
        List<ProductDTO> productDTO = productService.searchProducts(searchText, minPrice, maxPrice, isClothing);

        return metodoAusiliario(productDTO);
    }


    List<ProductSearchDTO> metodoAusiliario(List<ProductDTO> productDTO){
        List<ProductSearchDTO> productSearchDTO = new Vector<>();
        ProductSearchDTO productSearchDTO1;
        AverageDTO averageDTO;

        for(ProductDTO p: productDTO){
            productSearchDTO1 = new ProductSearchDTO();
            averageDTO = reviewService.getReviewAverage(p.getId());

            productSearchDTO1.setProductId(p.getId());
            productSearchDTO1.setAverageReview(averageDTO.getAvarege());
            productSearchDTO1.setReviewsNumber(averageDTO.getNummberOfReview());

            productSearchDTO1.setProductName(p.getName());
            productSearchDTO1.setPrice(p.getPrice());

            productSearchDTO1.setAverageReview(averageDTO.getAvarege());
            productSearchDTO1.setReviewsNumber(averageDTO.getNummberOfReview());

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
                    productCartDTO.setSize(productCartDTO.getSize());

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
    public boolean changeQuantity(String username, UUID productID, int quantity, String size){
        ProductDTO productDTO = productService.getProductById(productID);
        return productOrderService.changeQuantity(username,productDTO,quantity, size);
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

        System.out.println("wishlistID: "+ wishListProductDTO.getWishlistDTO().getId());
        System.out.println("productID: "+ wishListProductDTO.getProductDTO().getId());

        if(wishListProductDTO==null)
            return false;

        return wishlistProductService.addOrUpdateWishlistProduct(wishListProductDTO);
    }

    @Override
    @Transactional
    public boolean deleteProductFromWishList(String username, UUID wishId, UUID productId) {
        SendWishlistProductDTO sendWishlistProductDTO = new SendWishlistProductDTO();
        sendWishlistProductDTO.setProductID(productId);
        sendWishlistProductDTO.setWishlistID(wishId);

        WishListProductDTO wishListProductDTO= getWishListProductDTO(username, sendWishlistProductDTO);

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

    @Override
    public WishProductDTO getWishlistProductsByWishlistID(UUID wishlistID, String username) {

        WishlistDTO wishlistDTO = wishlistService.getWishlist(wishlistID, username);

        if(wishlistDTO==null){
            return null;
        }

        List<WishListProductDTO> wishListProductDTOS = wishlistProductService.getWishlistProductsByWishlistID(wishlistDTO);

        if(wishListProductDTOS == null || wishListProductDTOS.isEmpty())
            return null;

        WishProductDTO wishProductDTO = new WishProductDTO();

        SingleWishListProductDTO singleWishListProductDTO;

        List<SingleWishListProductDTO> singleWishListProductDTOS = new Vector<>();

        for(WishListProductDTO wishListProductDTO: wishListProductDTOS){
            singleWishListProductDTO = new SingleWishListProductDTO();

            singleWishListProductDTO.setProductName(wishListProductDTO.getProductDTO().getName());
            singleWishListProductDTO.setPrice(wishListProductDTO.getProductDTO().getPrice());
            singleWishListProductDTO.setProductId(wishListProductDTO.getProductDTO().getId());

            singleWishListProductDTOS.add(singleWishListProductDTO);

        }

        wishProductDTO.setSingleWishListProductDTOS(singleWishListProductDTOS);
        wishProductDTO.setVisibility(wishlistDTO.getVisibility());

        return wishProductDTO;
    }

    @Override
    public boolean updateOrder(String username, UUID orderId) {
        try {
            LocalDate tenDaysAgo = LocalDate.now().minusDays(10);
            OrderDTO order = orderService.getOrderByIdAndUsername(orderId, username);
            if (order.getPurchaseDate().isBefore(tenDaysAgo)) {
                utils.sendNotify(username, "Reso ordine: "+order.getOrderNumber()+" rifiutato",
                        "Il reso è possibile solo entro 10 giorni dall'acquisto");
                return false;
            }else{
                //Prendo tutti i prodotti nell'ordine restituito
                List<ProductOrderDTO> productOrderDTO = productOrderService.getProductInOrder(username, order);

                //Lista di disponibilità (mi serve solo per aggiornare la disponibilità)
                List<AvailabilityDTO> availabilityDTOS;

                //Oggetto singolo per restituire la disponibilità attuale del prodotto tramite taglia
                AvailabilityDTO availabilityDTO;

                for(ProductOrderDTO productOrderDTO1: productOrderDTO){
                    //Inizializzo qui la lista perchè mi serve sempre vuota
                    availabilityDTOS = new Vector<>();

                    //Inizializzo il prodotto andando a prendermi la disponibilità del prodotto passato per argomento e della taglia sempre passata come argomento
                    availabilityDTO = availabilityService.getAvailabilitieByProductId(productOrderDTO1.getProductDTO(), productOrderDTO1.getSize());

                    //Alla disponibilità restituita aggiungo di nuovo quella precedentemente sottratta e se nel DB non esisteva più viene ricreata
                    availabilityDTO.setSize(productOrderDTO1.getSize());
                    availabilityDTO.setAmount(productOrderDTO1.getQuantity());

                    //Aggiunto la disponibilità alla lista che mi serve per aggiornare la disponibilità
                    availabilityDTOS.add(availabilityDTO);

                    //Aggiorno effettivamente la disponibilità
                    availabilityService.addOrUpdateAvailability(availabilityDTOS, productOrderDTO1.getProductDTO());
                }

                order.setRefundDate(LocalDate.now());
                order.setOrderState("Rimborsato");
                order.setRefund(true);
                orderService.save(order);
                return utils.sendNotify(username, "Reso ordine: "+order.getOrderNumber()+" accettato",
                        "Il rimborso sarà effettuato sulla carta utilizzata al momento del pagamento");

            }
        }catch (Exception | Error e) {
            log.debug("Errore nell'update dell'ordine");
            return false;
        }
    }

    @Override
    public boolean updateNotifyOrder() {
        try {
            List<OrderDTO> ordersToUpdate = orderService.getOrdersByState("Ricevuto");
            for (OrderDTO order : ordersToUpdate) {
                order.setOrderState("In consegna");
                orderService.addOrder(order);

                utils.sendNotify(order.getUsername(),
                        "Aggiornamento ordine numero " + order.getOrderNumber(),
                        "Il tuo ordine è in consegna e arriverà presto."
                );
            }
            return true;
        }catch (Exception | Error e) {
            log.debug("Errore nell'update delle notifiche");
            return false;
        }

    }


    //Metodi di servizio

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

    private WishListProductDTO getWishListProductDTO(String username, SendWishlistProductDTO wishlistProductDTO) {
        ProductDTO productDTO= productService.getProductById(wishlistProductDTO.getProductID());

        if(productDTO==null)
            return null;

        WishlistDTO wishlistDTO= wishlistService.getWishlist(wishlistProductDTO.getWishlistID(), username);

        if(wishlistDTO==null)
            return null;

        WishListProductDTO wishListProductDTO= new WishListProductDTO();

        wishListProductDTO.setWishlistDTO(wishlistDTO);
        wishListProductDTO.setProductDTO(productDTO);

        System.out.println("wishlistID: "+ wishlistDTO.getId());
        System.out.println("productID: "+ productDTO.getId());

        return wishListProductDTO;
    }

    private List<UnavailableDTO> setPossibleAvailability(List<ProductDTO> unavaibilities) {
        List<UnavailableDTO> result= new Vector<>();
        UnavailableDTO un;

        for(ProductDTO unavailableDTO: unavaibilities){
            un = new UnavailableDTO();

            un.setId(unavailableDTO.getId());
            un.setName(unavailableDTO.getName());
            un.setAvailabilities(availabilityService.getAvailabilitiesByProductID(unavailableDTO));

            result.add(un);
        }

        return result;
    }

    private boolean checkAddress(UUID addressId) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Boolean> response = restTemplate.exchange(
                "http://user-service/user-api/user/address/"+addressId,
                HttpMethod.GET,
                entity,
                Boolean.class
        );

        if(response.getStatusCode()==HttpStatus.OK)
            return response.getBody();
        return false;
    }

    private boolean changeAvaibility(List<ProductOrderDTO> products, boolean rollback) {
        List<AvailabilityDTO> availabilities= new Vector<>();
        AvailabilityDTO availability;
        int newAmount;

        for(ProductOrderDTO productOrderDTO: products) {
            availability= availabilityService.getAvailabilitieByProductId(productOrderDTO.getProductDTO(), productOrderDTO.getSize());

            newAmount= !rollback? availability.getAmount()-productOrderDTO.getQuantity(): availability.getAmount()+productOrderDTO.getQuantity();
            availability.setAmount(newAmount);

            availabilities.add(availability);

            if(!availabilityService.addOrUpdateAvailability(availabilities, productOrderDTO.getProductDTO()))
                return false;
            newAmount= 0;
            availabilities.clear();
        }

        return true;
    }

    private List<ProductOrderDTO> getProductInOrder(String username, List<UUID> productIds) {
        List<ProductOrderDTO> productInCart = productOrderService.getProductOrdersByUsername(username);

        if(productInCart.isEmpty())
            return null;

        return productInCart.stream()
                .filter(productOrderDTO -> productIds.contains(productOrderDTO.getProductDTO().getId()))
                .toList();
    }

    private boolean checkPayment(UUID cardId, double total) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Boolean> response = restTemplate.exchange(
                "http://user-service/user-api/balance/"+cardId+"?total="+total,
                HttpMethod.POST,
                entity,
                Boolean.class
        );

        if(response.getStatusCode()==HttpStatus.OK)
            return response.getBody();
        return false;
    }


}
