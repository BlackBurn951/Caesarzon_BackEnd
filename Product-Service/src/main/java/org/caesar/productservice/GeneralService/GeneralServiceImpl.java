package org.caesar.productservice.GeneralService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.productservice.Data.Dao.ProductRepository;
import org.caesar.productservice.Data.Entities.Availability;
import org.caesar.productservice.Data.Entities.Product;
import org.caesar.productservice.Data.Entities.ProductOrder;
import org.caesar.productservice.Data.Services.AvailabilityService;
import org.caesar.productservice.Data.Services.OrderService;
import org.caesar.productservice.Data.Services.ProductOrderService;
import org.caesar.productservice.Data.Services.ProductService;
import org.caesar.productservice.Dto.*;
import org.caesar.productservice.Dto.DTOOrder.BuyDTO;
import org.caesar.productservice.Dto.DTOOrder.OrderDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GeneralServiceImpl implements GeneralService {

    private final AvailabilityService availabilityService;
    private final ProductService productService;
    private final ModelMapper modelMapper;
    private final ProductRepository productRepository;
    private final ProductOrderService productOrderService;
    private final OrderService orderService;
    private final RestTemplate restTemplate;


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

    @Override
    public boolean createOrder(String username, SendProductOrderDTO sendProductOrderDTO) {

        ProductDTO productDTO = productService.getProductById(sendProductOrderDTO.getProductID());

        ProductOrderDTO productOrderDTO = new ProductOrderDTO();

        productOrderDTO.setProductDTO(productDTO);
        productOrderDTO.setTotal(productDTO.getPrice()*sendProductOrderDTO.getQuantity());
        productOrderDTO.setQuantity(sendProductOrderDTO.getQuantity());
        productOrderDTO.setUsername(username);

        return productOrderService.save(productOrderDTO);


    }

    @Override
    @Transactional
    public boolean updateOrder(String username, BuyDTO buyDTO) {

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

        orderDTO.setTotalOrder(productOrderDTOs.stream().mapToDouble(ProductOrderDTO::getTotal).sum());

        OrderDTO orderDTO1 = orderService.addOrder(orderDTO);

        productInOrder.forEach(productOrderDTO -> productOrderDTO.setOrderID(orderDTO1));

        if(productOrderService.saveAll(productOrderDTOs)) {
            HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization", request.getHeader("Authorization"));

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("date", String.valueOf(LocalDate.now()));
            requestBody.put("subject", "Ordine numero "+orderDTO1.getOrderNumber()+" effettuato");
            requestBody.put("user", username);
            requestBody.put("read", "false");
            requestBody.put("explaination", "Il tuo ordine è in fase di elaborazione e sarà il "+ String.valueOf(orderDTO1.getExpectedDeliveryDate()));

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);
            return restTemplate.exchange(
                    "http://notification-service/notification",
                    HttpMethod.POST,
                    entity,
                    String.class
            ).getStatusCode()== HttpStatus.OK;
        }
        return false;
        //☺
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
    public ProductDTO getProductAndAvailabilitiesAndImages(UUID id){
        ProductDTO productDTO = productService.getProductById(id);
        if(productService.getProductById(id) != null){
            List<AvailabilityDTO> availabilities = availabilitySe rvice.getAvailabilitiesByProductID(productDTO);
            productDTO.setAvailabilities(availabilities);
            return productDTO;
        }
        return null;
    }


}
