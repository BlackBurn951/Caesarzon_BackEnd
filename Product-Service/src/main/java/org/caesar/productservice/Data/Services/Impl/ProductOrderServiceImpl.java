package org.caesar.productservice.Data.Services.Impl;

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
import org.springframework.dao.DataAccessException;
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

    @Override
    public UUID addOrUpdateProductOrder(SendProductOrderDTO productOrder) {
        return null;
    }

    @Override
    public SendProductOrderDTO getProductOrder(UUID id) {
        return null;
    }

    @Override
    public List<SendProductOrderDTO> getProductOrders() {
        return List.of();
    }

    @Override
    public boolean deleteProductCarts(String username) {
        try {
            productOrderRepository.deleteAllByUsernameAndOrderIDIsNull(username);
            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nell'eliminazione dei prodotti dalla lista desideri");
            return false;
        }
    }

    @Override
    public boolean save(ProductOrderDTO productOrderDTO) {
        if(productOrderDTO != null) {
            productOrderRepository.save(modelMapper.map(productOrderDTO, ProductOrder.class));
            return true;
        }else{
            return false;
        }
    }

    @Override
    public List<ProductOrderDTO> getProductOrdersByUsername(String username){
        List<ProductOrder> result= productOrderRepository.findAllByUsernameAndOrderIDIsNull(username);

        List<ProductOrderDTO> productOrderDTOList= new Vector<>();
        ProductOrderDTO productOrderDTO;
        for(ProductOrder productOrder: result){
            productOrderDTO= new ProductOrderDTO();

            productOrderDTO.setId(productOrder.getId());
            productOrderDTO.setUsername(productOrder.getUsername());
            productOrderDTO.setProductDTO(modelMapper.map(productOrder, ProductDTO.class));
            productOrderDTO.setTotal(productOrder.getTotal());
            productOrderDTO.setQuantity(productOrder.getQuantity());
            productOrderDTO.setBuyLater(productOrder.isBuyLater());

            productOrderDTOList.add(productOrderDTO);
        }

        return productOrderDTOList;
    }

    @Override
    public boolean deleteProductCart(String username, ProductDTO productDTO) {
        try {
            productOrderRepository.deleteByUsernameAndOrderIDNullAndProductID(username, modelMapper.map(productDTO, Product.class));

            return true;
        } catch (Exception | Error e) {

            return false;
        }
    }

    @Override
    public boolean saveAll(List<ProductOrderDTO> orderDTOS) {
        try {
            List<ProductOrder> productOrderList= new Vector<>();
            ProductOrder productOrder;
            for(ProductOrderDTO productOrderDTO: orderDTOS){
                productOrder = new ProductOrder();

                productOrder.setId(productOrderDTO.getId());
                productOrder.setOrderID(modelMapper.map(productOrderDTO.getOrderID(), Order.class));
                productOrder.setProductID(modelMapper.map(productOrderDTO.getProductDTO(), Product.class));
                productOrder.setTotal(productOrderDTO.getTotal());
                productOrder.setUsername(productOrder.getUsername());
                productOrder.setBuyLater(productOrder.isBuyLater());

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
    public boolean saveLater(String username, ProductDTO productDTO) {
        try{
            ProductOrder productOrder= productOrderRepository
            .findByUsernameAndProductID(username, modelMapper.map(productDTO, Product.class));

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
    public boolean changeQuantity(String username, ProductDTO productDTO, int quantity) {
        try{
            ProductOrder productOrder = productOrderRepository
                    .findByUsernameAndProductID(username, modelMapper.map(productDTO, Product.class));

            if(productOrder == null)
                return false;
            productOrder.setQuantity(quantity);
            productOrderRepository.save(productOrder);

            return true;
        }catch (Exception | Error e){
            log.debug("Errore nell'aggiornamento dell'ordine");
            return false;
        }
    }

    @Override
    public List<ProductOrderDTO> getProductInOrder(String username, OrderDTO orderDTO) {
        try {
            return productOrderRepository.findAllByUsernameAndOrderID(username, modelMapper.map(orderDTO, Order.class))
                    .stream().map(a -> modelMapper.map(a, ProductOrderDTO.class)).toList();
        } catch (Exception | Error e) {
                log.debug("Errore nella presa dei prodotti nell'ordine");
            return null;
        }
    }
}
