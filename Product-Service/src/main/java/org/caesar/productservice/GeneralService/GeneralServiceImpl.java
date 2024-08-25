package org.caesar.productservice.GeneralService;

import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.productservice.Data.Services.*;
import org.caesar.productservice.Dto.*;
import org.caesar.productservice.Dto.DTOOrder.BuyDTO;
import org.caesar.productservice.Dto.DTOOrder.OrderDTO;
import org.caesar.productservice.Dto.DTOOrder.UnavailableDTO;
import org.caesar.productservice.Sagas.OrderOrchestrator;
import org.caesar.productservice.Sagas.ReviewOrchestrator;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeneralServiceImpl implements GeneralService {

    private final ProductService productService;
    private final ReviewService reviewService;
    private final AvailabilityService availabilityService;

    private final ProductOrderService productOrderService;
    private final OrderService orderService;
    private final LastViewService lastViewService;

    private final WishlistService wishlistService;
    private final WishlistProductService wishlistProductService;

    private final Utils utils;
    private final RestTemplate restTemplate;
    private final PayPalService payPalService;
    private final ModelMapper modelMapper;
    private final ReviewOrchestrator reviewOrchestrator;
    private final OrderOrchestrator orderOrchestrator;

    private final static String USER_SERVICE= "userService";
    private final static String NOTIFY_SERVICE= "notifyService";

    private boolean fallbackUser(Throwable e){
        log.info("Servizio utenti non disponibile");
        return false;
    }

    private boolean fallbackNotify(Throwable e){
        log.info("Servizio per l'invio delle notifiche non disponibile");
        return false;
    }



    //SEZIONE DEI PRODOTTI E STRETTAMENTE CORRELATI
    @Override
    public List<ImageDTO> getProductImage(UUID id) {
        return List.of();
    }

    @Override
    public List<ImageDTO> getAllProductImages(UUID productID) {
        return List.of();
    }

    @Override // Restituisce il prodotto con le sue disponibilità e immagini, in più se l'utente non è guest salva la ricerca
    public ProductDTO getProductAndAvailabilitiesAndImages(String username, UUID id){
        ProductDTO productDTO = productService.getProductById(id);

        if(productDTO != null){
            List<AvailabilityDTO> availabilities = availabilityService.getAvailabilitiesByProduct(productDTO);

            for(AvailabilityDTO availabilityDTO: availabilities)
                availabilityDTO.setProduct(null);
            productDTO.setAvailabilities(availabilities);

//            if(!username.equals("guest"))
            //lastViewService.save(username, productDTO);   TODO da tornare anche le immagini
            return productDTO;
        }
        return null;
    }

    @Override
    @Transactional // Aggiunge il prodotto ricevuto da front al db dei prodotti
    public boolean addProduct(ProductDTO sendProductDTO) {

        // Aggiorna l'ID del productDTO dopo averlo salvato
        sendProductDTO.setId(productService.addOrUpdateProduct(sendProductDTO).getId());

        if(sendProductDTO.getId()==null)
            return false;

        //TODO DA FARE AGGIUNTA FOTO

        return availabilityService.addOrUpdateAvailability(sendProductDTO.getAvailabilities(), sendProductDTO);
    }

    @Override
    @Transactional //TODO AGGIUNGERE ELIMINAZIONE IMMAGINI
    public boolean deleteProduct(UUID id) {
        ProductDTO product = productService.getProductById(id);

        if(product != null)
            return availabilityService.deleteAvailabilityByProduct(product) && productService.deleteProductById(id);

        return false;
    }



    //SEZIONE RECENSIONI
    @Override
    public List<ReviewDTO> getProductReviews(UUID productID, int str) {
        ProductDTO productDTO= productService.getProductById(productID);

        if(productDTO == null)
            return null;

        return reviewService.getReviewsByProduct(productDTO, str);
    }

    @Override
    public List<Integer> getReviewScore(UUID productId) {
        ProductDTO product= productService.getProductById(productId);

        if(product==null)
            return null;

        int oneStar= reviewService.getNumberOfReview(product, 1);
        int twoStar= reviewService.getNumberOfReview(product, 2);
        int threeStar= reviewService.getNumberOfReview(product, 3);
        int fourStar= reviewService.getNumberOfReview(product, 4);
        int fiveStar= reviewService.getNumberOfReview(product, 5);

        List<Integer> result= new Vector<>();
        result.add(oneStar);
        result.add(twoStar);
        result.add(threeStar);
        result.add(fourStar);
        result.add(fiveStar);

        return result;
    }

    @Override
    public AverageDTO getReviewAverage(UUID productId) {
        ProductDTO productDTO= productService.getProductById(productId);

        if(productDTO==null)
            return null;

        return reviewService.getReviewAverage(productDTO);
    }

    @Override
    public String addReview(ReviewDTO reviewDTO, String username) {

        ProductDTO productDTO= productService.getProductById(reviewDTO.getProductID());

        if(productDTO==null)
            return "Problemi nell'aggiunta della recensione...";

        reviewDTO.setUsername(username);

        return reviewService.addReview(reviewDTO, productDTO);
    }

    @Override
    @Transactional
    @CircuitBreaker(name= NOTIFY_SERVICE, fallbackMethod = "fallbackNotify")
    public boolean deleteReviewByUser(String username, UUID productId) {
        ProductDTO productDTO= productService.getProductById(productId);

        if(productDTO==null)
            return false;

        ReviewDTO reviewDTO= reviewService.getReviewByUsernameAndProduct(username, productDTO);

        if(reviewDTO==null)
            return false;

        return reviewOrchestrator.processDeleteReview(reviewDTO);
    }



    //SEZIONE DEL CARRELLO
    @Override  //Restituisce il carrello dell'utente con la lista dei prodotti al suo interno
    public List<ProductCartDTO> getCart(String username) {
        List<ProductOrderDTO> productCart = productOrderService.getProductOrdersByUsername(username);

        if (productCart == null || productCart.isEmpty()) {
            return null;
        }

        List<ProductCartDTO> result = new Vector<>();
        ProductCartDTO prod;

        for (ProductOrderDTO p : productCart) {
            prod = new ProductCartDTO();

            ProductDTO productDTO = productService.getProductById(p.getProductDTO().getId());

            prod.setName(productDTO.getName());
            prod.setId(productDTO.getId());

            prod.setTotal(approximatedSecondDecimal(productDTO.getPrice()));

            prod.setQuantity(p.getQuantity());
            prod.setSize(p.getSize());
            System.out.println("VARIABILE: " + p.isBuyLater());
            prod.setBuyLater(p.isBuyLater());

            double discountPrice = (p.getProductDTO().getPrice() * p.getProductDTO().getDiscount())/100;
            double totalDiscount = p.getProductDTO().getPrice()-discountPrice;
            prod.setDiscountTotal(approximatedSecondDecimal(totalDiscount));

            result.add(prod);
        }
        return result;
    }

    @Override  // Aggiunge al carrello il singolo prodotto scelto dall'utente (anche in caso di acquisto rapido si passa da qui)
    public boolean createCart(String username, SendProductOrderDTO sendProductOrderDTO) {
        ProductDTO productDTO = productService.getProductById(sendProductOrderDTO.getProductID());

        if(productDTO==null)
            return false;

        ProductOrderDTO productOrderDTO = new ProductOrderDTO();

        productOrderDTO.setProductDTO(productDTO);
        productOrderDTO.setTotal(approximatedSecondDecimal(productDTO.getPrice()*sendProductOrderDTO.getQuantity()));

        if(!checkQuantity(sendProductOrderDTO.getQuantity()) || !checkSize(sendProductOrderDTO.getSize()))
            return false;

        productOrderDTO.setQuantity(sendProductOrderDTO.getQuantity());
        productOrderDTO.setSize(sendProductOrderDTO.getSize());
        productOrderDTO.setUsername(username);

        return productOrderService.save(productOrderDTO);
    }

    @Override
    public boolean saveLater(String username, UUID productID) {
        ProductDTO productDTO = modelMapper.map(productService.getProductById(productID), ProductDTO.class);
        return productOrderService.saveLater(username,productDTO);
    }

    @Override
    public boolean changeQuantity(String username, UUID productID, ChangeCartDTO changeCartDTO) {
        ProductDTO productDTO = productService.getProductById(productID);

        if(checkSize(changeCartDTO.getSize()) && checkQuantity(changeCartDTO.getQuantity()))
            return productOrderService.changeQuantity(username,productDTO,changeCartDTO.getQuantity(), changeCartDTO.getSize());
        return false;
    }

    @Override
    public boolean deleteProductCart(String username, UUID productID){
        ProductDTO productDTO = productService.getProductById(productID);
        return productOrderService.deleteProductCart(username,productDTO);
    }



    //SEZIONE DELL'ORDINE
    @Override
    public List<ProductCartDTO> getOrder(String username, UUID orderId) {
        OrderDTO orderDTO = orderService.getOrder(username, orderId);

        List<ProductOrderDTO> productsOrder= productOrderService.getProductInOrder(username, orderDTO);
        if(productsOrder==null && productsOrder.isEmpty())
            return null;

        List<ProductCartDTO> productCartDTOS = new Vector<>();
        ProductCartDTO productCartDTO;
        double discountTotal;

        for (ProductOrderDTO productOrderDTO : productsOrder) {
            productCartDTO = new ProductCartDTO();

            productCartDTO.setId(productOrderDTO.getProductDTO().getId());
            productCartDTO.setName(productOrderDTO.getProductDTO().getName());
            productCartDTO.setQuantity(productOrderDTO.getQuantity());
            productCartDTO.setTotal(productOrderDTO.getTotal());
            productCartDTO.setSize(productOrderDTO.getSize());

            discountTotal= approximatedSecondDecimal((productOrderDTO.getProductDTO().getPrice()*productOrderDTO.getProductDTO().getDiscount())/100);
            productCartDTO.setDiscountTotal(discountTotal);

            productCartDTOS.add(productCartDTO);
        }

        return productCartDTOS;
    }

    @Override
    @Transactional
    public String createOrder(String username, BuyDTO buyDTO) {
        List<ProductOrderDTO> productInOrder= getProductInOrder(username, buyDTO.getProductsIds());

        if(productInOrder==null || productInOrder.isEmpty())
            return "Errore";

        if(buyDTO.getTotal()<=0.0 || buyDTO.getAddressID()==null)
            return "Errore";

        if(orderOrchestrator.processCreateOrderWithPaypalPayment(username, productInOrder, buyDTO.getTotal(), buyDTO.getAddressID()))
            return "Ordine effettuato con successo!";

        changeAvaibility(productInOrder, true);
        return "Errore"; //☺
    }

    @Override
    @Transactional  //Metodo per eseguire il reso
    public boolean updateOrder(String username, UUID orderId) {
        LocalDate tenDaysAgo = LocalDate.now().minusDays(10);
        OrderDTO order = orderService.getOrderByIdAndUsername(orderId, username);
        if (order.getPurchaseDate().isBefore(tenDaysAgo)) {
            utils.sendNotify(username, "Reso ordine: "+order.getOrderNumber()+" rifiutato",
                    "Il reso è possibile solo entro 10 giorni dall'acquisto");
        }else{
            //Prendo tutti i prodotti nell'ordine restituito
            List<ProductOrderDTO> productOrderDTO = productOrderService.getProductInOrder(username, order);

            if(productOrderDTO==null || productOrderDTO.isEmpty())
                return false;

            //Lista di disponibilità (mi serve solo per aggiornare la disponibilità)
            List<AvailabilityDTO> availabilityDTOS= new Vector<>();

            //Oggetto singolo per restituire la disponibilità attuale del prodotto tramite taglia
            AvailabilityDTO availabilityDTO;

            for(ProductOrderDTO productOrderDTO1: productOrderDTO){

                //Inizializzo il prodotto andando a prendermi la disponibilità del prodotto passato per argomento e della taglia sempre passata come argomento
                availabilityDTO = availabilityService.getAvailabilitieByProductId(productOrderDTO1.getProductDTO(), productOrderDTO1.getSize());

                if(availabilityDTO==null)
                    return false;

                //Aggiunto la disponibilità alla lista che mi serve per aggiornare la disponibilità
                availabilityDTOS.add(availabilityDTO);
            }


            return orderOrchestrator.processReturnOrder(username, productOrderDTO, orderId, availabilityDTOS, order.getCardID(), order.getOrderTotal(), order.getOrderNumber());
        }
        return false;
    }

    @Override
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
    @Transactional   // Genera un ordine contenente gli articoli acquistati dall'utente e la notifica corrispondente
    public String checkOrder(String username, BuyDTO buyDTO, boolean payMethod) {  //PayMethod -> false carta -> true paypal

        List<ProductOrderDTO> productInOrder = getProductInOrder(username, buyDTO.getProductsIds());

        if (productInOrder == null || productInOrder.isEmpty())
            return "Errore";

        // Controllo che vengano effettivamente passati indirizzo e carta per pagare
        if (buyDTO.getAddressID() == null || (!payMethod && buyDTO.getCardID() == null)) {
            changeAvaibility(productInOrder, true);
            return "Errore";
        }

        // Chiamata per verificare che l'utente che vuole acquistare abbia quell'indirizzo
        if (!checkAddress(buyDTO.getAddressID())) {
            changeAvaibility(productInOrder, true);
            return "Errore";
        }


        double total = productInOrder.stream()
                .mapToDouble(prod -> {
                    if (prod.getProductDTO().getDiscount() == 0)
                        return prod.getTotal() * prod.getQuantity(); // CALCOLO SENZA SCONTO

                    // CALCOLO CON SCONTO
                    double discount = (prod.getTotal() * prod.getProductDTO().getDiscount()) / 100;
                    return (prod.getTotal() - discount) * prod.getQuantity();
                }).sum();

        approximatedSecondDecimal(total+= 5);
        if (!payMethod) {
            if(orderOrchestrator.processCreateOrderWithCardPayment(username, productInOrder, total, buyDTO.getAddressID(), buyDTO.getCardID()))
                return "Ordine effettuato con successo!";
            changeAvaibility(productInOrder, true);
        } else {
            try {
                Payment payment = payPalService.createPayment(
                        total, "EUR", "paypal",
                        "sale", "Pagamento ordine",
                        "http://localhost:4200/order-final",
                        "http://localhost:4200/order-final");
                for (Links link : payment.getLinks()) {
                    if (link.getRel().equals("approval_url")) {
                        return link.getHref();
                    }
                }
            } catch (PayPalRESTException e) {
                e.printStackTrace();
            }
        }
        return "Errore";
    }



    //SEZIONE RICERCA DEI PRODOTTI
    @Override // Resituisce una lista di prodotti, risultato della ricerca coi valori dei parametri passati
    public List<ProductSearchDTO> searchProducts(String searchText, Double minPrice, Double maxPrice, Boolean isClothing) {
        List<ProductDTO> productDTO = productService.searchProducts(searchText, minPrice, maxPrice, isClothing);

        if(productDTO==null || productDTO.isEmpty())
            return null;

        return makeProductSearch(productDTO);
    }

    @Override //Restituisce i prodotti visti di recente dall'utente
    public List<ProductSearchDTO> getLastView(String username){
        //Metodo per prendere tutte le tuple dei prodotti visti
        List<LastViewDTO> lastViewDTOS = lastViewService.getAllViewed(username);

        if(lastViewDTOS==null || lastViewDTOS.isEmpty())
            return null;

        //Lista degli utlimi prodotti cliccati
        List<ProductDTO> productDTOS = new Vector<>();
        ProductDTO productDTO;

        for(LastViewDTO l: lastViewDTOS){
            productDTO= productService.getProductById(l.getId());

            if(productDTO!=null)
                productDTOS.add(productDTO);
        }

        return makeProductSearch(productDTOS);
    }

    @Override
    public List<ProductSearchDTO> getNewProducts() {
        List<ProductDTO> productDTO = productService.getLastProducts();

        if(productDTO==null || productDTO.isEmpty())
            return null;

        return makeProductSearch(productDTO);
    }

    @Override
    public List<ProductSearchDTO> getOffers() {
        List<ProductDTO> products= productService.getOffer();

        if(products==null || products.isEmpty())
            return null;

        return makeProductSearch(products);
    }



    //SEZIONE DELLE WISHLIST
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
    public boolean addProductIntoWishList(String username, SendWishlistProductDTO wishlistProductDTO) {

        WishListProductDTO wishListProductDTO = getWishListProductDTO(username, wishlistProductDTO);


        if(wishListProductDTO==null)
            return false;

        return wishlistProductService.addOrUpdateWishlistProduct(wishListProductDTO);
    }

    @Override
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
    public boolean deleteProductsFromWishList(String username, UUID wishlistId) {
        WishlistDTO wishlistDTO= wishlistService.getWishlist(wishlistId, username);

        if(wishlistDTO==null)
            return false;

        return wishlistProductService.deleteAllProductsFromWishlist(wishlistDTO);
    }

    @Override // Elimina l'intera wishlist dell'utente assieme a tutti i prodotti in essa contenuti
    public boolean deleteWishlist(String username, UUID wishlistID){
        WishlistDTO wishlistDTO = wishlistService.getWishlist(wishlistID, username);

        if(wishlistDTO==null)
            return false;

        return wishlistProductService.deleteAllProductsFromWishlist(wishlistDTO) && wishlistService.deleteWishlist(wishlistID);
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

    public double approximatedSecondDecimal(double total) {
        BigDecimal bd = new BigDecimal(total);
        bd = bd.setScale(2, RoundingMode.HALF_UP); // Arrotonda alla seconda cifra decimale
        return bd.doubleValue();
    }

    private WishListProductDTO getWishListProductDTO(String username, SendWishlistProductDTO wishlistProductDTO) {
        ProductDTO productDTO= productService.getProductById(wishlistProductDTO.getProductID());

        if(productDTO==null)
            return null;

        WishlistDTO wishlistDTO= wishlistService.getWishlist(wishlistProductDTO.getWishlistID(), username);

        if(wishlistDTO==null)
            return null;

        if(wishlistProductService.thereIsProductInWishList(wishlistDTO, productDTO))
            return null;

        WishListProductDTO wishListProductDTO= new WishListProductDTO();

        wishListProductDTO.setWishlistDTO(wishlistDTO);
        wishListProductDTO.setProductDTO(productDTO);

        return wishListProductDTO;
    }

    private List<UnavailableDTO> setPossibleAvailability(List<ProductDTO> unavaibilities) {
        List<UnavailableDTO> result= new Vector<>();
        UnavailableDTO un;

        for(ProductDTO unavailableDTO: unavaibilities){
            un = new UnavailableDTO();

            List<AvailabilityDTO> ava= availabilityService.getAvailabilitiesByProduct(unavailableDTO)
                    .stream()
                    .map(a -> {
                        a.setProduct(null);
                        return a;
                    })
                    .toList();

            un.setId(unavailableDTO.getId());
            un.setName(unavailableDTO.getName());
            un.setAvailabilities(ava);

            result.add(un);
        }

        return result;
    }

    @CircuitBreaker(name= USER_SERVICE, fallbackMethod = "fallbackUser")
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

    @CircuitBreaker(name= USER_SERVICE, fallbackMethod = "fallbackUser")
    private boolean checkPayment(UUID cardId, double total, boolean refund) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", request.getHeader("Authorization"));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Boolean> response = restTemplate.exchange(
                "http://user-service/user-api/balance/"+cardId+"?total="+total+"&refund="+refund,
                HttpMethod.POST,
                entity,
                Boolean.class
        );

        if(response.getStatusCode()==HttpStatus.OK)
            return response.getBody();
        return false;
    }

    private List<ProductSearchDTO> makeProductSearch(List<ProductDTO> productDTO){
        List<ProductSearchDTO> productSearchDTO = new Vector<>();
        ProductSearchDTO productSearchDTO1;
        AverageDTO averageDTO;

        for(ProductDTO p: productDTO){
            productSearchDTO1 = new ProductSearchDTO();

            averageDTO = reviewService.getReviewAverage(p);

            if(averageDTO==null) {
                productSearchDTO1.setReviewsNumber(0);
                productSearchDTO1.setAverageReview(0.0);
            } else {
                productSearchDTO1.setReviewsNumber(averageDTO.getNumberOfReview());
                productSearchDTO1.setAverageReview(averageDTO.getAverage());
            }

            productSearchDTO1.setProductId(p.getId());

            productSearchDTO1.setProductName(p.getName());
            productSearchDTO1.setPrice(p.getPrice());
            productSearchDTO1.setDiscount(p.getDiscount());

            productSearchDTO.add(productSearchDTO1);
        }
        return productSearchDTO;
    }

    private boolean checkSize(String size) {
        if(size==null)
            return true;
        List<String> sizes = List.of(new String[]{"XS", "S", "M", "L", "XL"});
        return sizes.contains(size);
    }

    private boolean checkQuantity(int quantity) {
        return quantity >= 0;
    }
}