package org.caesar.productservice.Data.Services.Impl;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.productservice.Data.Dao.ProductOrderRepository;
import org.caesar.productservice.Data.Entities.Order;
import org.caesar.productservice.Data.Entities.ProductOrder;
import org.caesar.productservice.Data.Services.ProductOrderService;
import org.caesar.productservice.Dto.DTOOrder.OrderDTO;
import org.caesar.productservice.Data.Entities.Product;
import org.caesar.productservice.Dto.ProductDTO;

import org.caesar.productservice.Dto.ProductOrderDTO;
import org.caesar.productservice.Dto.SendProductOrderDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.Vector;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductOrderServiceImpl implements ProductOrderService {

    private final ProductOrderRepository productOrderRepository;
    private final ModelMapper modelMapper;
    private final static String PRODUCTORDER_SERVICE = "productOrderService";

    public String fallbackCircuitBreaker(CallNotPermittedException e){
        log.debug("Circuit breaker su productOrderService da: {}", e.getCausingCircuitBreakerName());
        return e.getMessage();
    }

    @Override
//    @CircuitBreaker(name= PRODUCTORDER_SERVICE, fallbackMethod = "fallbackCircuitBreaker")
//    @Retry(name= PRODUCTORDER_SERVICE)
    public UUID addOrUpdateProductOrder(SendProductOrderDTO productOrder) {
        return null;
    }

    @Override
//    @Retry(name= PRODUCTORDER_SERVICE)
    public SendProductOrderDTO getProductOrder(UUID id) {
        return null;
    }

    @Override
//    @Retry(name= PRODUCTORDER_SERVICE)
    public List<SendProductOrderDTO> getProductOrders() {
        return List.of();
    }

    @Override
//    @CircuitBreaker(name= PRODUCTORDER_SERVICE, fallbackMethod = "fallbackCircuitBreaker")
//    @Retry(name= PRODUCTORDER_SERVICE)
    public boolean deleteProductCarts(String username) {
        try {
            productOrderRepository.deleteAllByUsernameAndOrderIsNull(username);
            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nell'eliminazione dei prodotti dalla lista desideri");
            return false;
        }
    }

    @Override
//    @CircuitBreaker(name= PRODUCTORDER_SERVICE, fallbackMethod = "fallbackCircuitBreaker")
//    @Retry(name= PRODUCTORDER_SERVICE)
    public boolean save(ProductOrderDTO productOrderDTO) {
        if(productOrderDTO != null) {
            productOrderRepository.save(modelMapper.map(productOrderDTO, ProductOrder.class));
            return true;
        }else{
            return false;
        }
    }

    @Override
//    @Retry(name= PRODUCTORDER_SERVICE)
    public List<ProductOrderDTO> getProductOrdersByUsername(String username){
        List<ProductOrder> result= productOrderRepository.findAllByUsernameAndOrderIsNullAndBuyLaterIsFalse(username);

        List<ProductOrderDTO> productOrderDTOList= new Vector<>();
        ProductOrderDTO productOrderDTO;
        for(ProductOrder productOrder: result){
            productOrderDTO= new ProductOrderDTO();

            productOrderDTO.setId(productOrder.getId());
            productOrderDTO.setUsername(productOrder.getUsername());
            productOrderDTO.setProductDTO(modelMapper.map(productOrder.getProduct(), ProductDTO.class));
            productOrderDTO.setTotal(productOrder.getTotal());
            productOrderDTO.setQuantity(productOrder.getQuantity());
            productOrderDTO.setBuyLater(productOrder.isBuyLater());
            productOrderDTO.setSize(productOrder.getSize());

            productOrderDTOList.add(productOrderDTO);
        }

        return productOrderDTOList;
    }

    @Override
//    @CircuitBreaker(name= PRODUCTORDER_SERVICE, fallbackMethod = "fallbackCircuitBreaker")
//    @Retry(name= PRODUCTORDER_SERVICE)
    public boolean deleteProductCart(String username, ProductDTO productDTO) {
        try {
            productOrderRepository.deleteByUsernameAndOrderNullAndProduct(username, modelMapper.map(productDTO, Product.class));

            return true;
        } catch (Exception | Error e) {

            return false;
        }
    }

    @Override
//    @CircuitBreaker(name= PRODUCTORDER_SERVICE, fallbackMethod = "fallbackCircuitBreaker")
//    @Retry(name= PRODUCTORDER_SERVICE)
    public boolean saveAll(List<ProductOrderDTO> orderDTOS) {
        try {
            List<ProductOrder> productOrderList= new Vector<>();
            ProductOrder productOrder;
            System.out.println(orderDTOS.getFirst().getOrderDTO().getId());

            for(ProductOrderDTO productOrderDTO: orderDTOS){
                productOrder = new ProductOrder();

                productOrder.setId(productOrderDTO.getId());
                productOrder.setOrder(modelMapper.map(productOrderDTO.getOrderDTO(), Order.class));
                productOrder.setProduct(modelMapper.map(productOrderDTO.getProductDTO(), Product.class));
                productOrder.setTotal(productOrderDTO.getTotal());
                productOrder.setUsername(productOrderDTO.getUsername());
                productOrder.setBuyLater(productOrderDTO.isBuyLater());

                productOrderList.add(productOrder);
            }

            productOrderRepository.saveAll(productOrderList);

            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nel salvataggio degli ordini");
            return false;
        }
    }


    @Override
//    @CircuitBreaker(name= PRODUCTORDER_SERVICE, fallbackMethod = "fallbackCircuitBreaker")
//    @Retry(name= PRODUCTORDER_SERVICE)
    public boolean saveLater(String username, ProductDTO productDTO) {
        try{
            ProductOrder productOrder= productOrderRepository
            .findByUsernameAndProduct(username, modelMapper.map(productDTO, Product.class));

            if(productOrder == null)
                return false;

            productOrder.setBuyLater(true);
            productOrderRepository.save(productOrder);

            return true;
        }catch (Exception | Error e){
            log.debug("Errore nel salvataggio dell'ordine");
            return false;
        }
    }

    @Override
//    @CircuitBreaker(name= PRODUCTORDER_SERVICE, fallbackMethod = "fallbackCircuitBreaker")
//    @Retry(name= PRODUCTORDER_SERVICE)
    public boolean changeQuantity(String username, ProductDTO productDTO, int quantity, String size) {
        try{
            ProductOrder productOrder = productOrderRepository
                    .findByUsernameAndProduct(username, modelMapper.map(productDTO, Product.class));

            if(productOrder == null)
                return false;
            productOrder.setQuantity(quantity);
            productOrder.setSize(size);
            productOrderRepository.save(productOrder);

            return true;
        }catch (Exception | Error e){
            log.debug("Errore nell'aggiornamento dell'ordine");
            return false;
        }
    }

    @Override
//    @Retry(name= PRODUCTORDER_SERVICE)
    public List<ProductOrderDTO> getProductInOrder(String username, OrderDTO orderDTO) {
        try {
            return productOrderRepository.findAllByUsernameAndOrder(username, modelMapper.map(orderDTO, Order.class))
                    .stream().map(a -> modelMapper.map(a, ProductOrderDTO.class)).toList();
        } catch (Exception | Error e) {
                log.debug("Errore nella presa dei prodotti nell'ordine");
            return null;
        }
    }
}
