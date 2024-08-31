package org.caesar.productservice.Data.Services.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.caesar.productservice.Data.Dao.ProductOrderRepository;
import org.caesar.productservice.Data.Entities.*;
import org.caesar.productservice.Data.Services.ProductOrderService;
import org.caesar.productservice.Dto.*;
import org.caesar.productservice.Dto.DTOOrder.OrderDTO;

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


    @Override
    public boolean validateAndCompleteAndReleaseProductInOrder(List<ProductOrderDTO> products, boolean release) {
        try {
            List<ProductOrder> productOrderList= new Vector<>();
            ProductOrder productOrder;

            System.out.println("Sono prima del for");
            for(ProductOrderDTO productOrderDTO: products){
                System.out.println("Sono nel for");
                productOrder = new ProductOrder();

                productOrder.setId(productOrderDTO.getId());
                if(productOrderDTO.getOrderDTO()!=null)
                    productOrder.setOrder(modelMapper.map(productOrderDTO.getOrderDTO(), Order.class));
                else
                    productOrder.setOrder(null);
                productOrder.setProduct(modelMapper.map(productOrderDTO.getProductDTO(), Product.class));
                productOrder.setTotal(productOrderDTO.getTotal());
                productOrder.setUsername(productOrderDTO.getUsername());
                productOrder.setBuyLater(productOrderDTO.isBuyLater());
                productOrder.setSize(productOrderDTO.getSize());
                productOrder.setQuantity(productOrderDTO.getQuantity());
                productOrder.setOnChanges(!release);

                productOrderList.add(productOrder);
            }

            productOrderRepository.saveAll(productOrderList);

            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nella validazione, completamento o rollback dell'ordine");
            return false;
        }
    }

    @Override
    public boolean rollbackProductInOrder(List<ProductOrderDTO> products) {
        try {
            List<ProductOrder> productOrderList= new Vector<>();
            ProductOrder productOrder;

            for(ProductOrderDTO productOrderDTO: products){
                productOrder = new ProductOrder();

                productOrder.setId(productOrderDTO.getId());
                productOrder.setOrder(null);
                productOrder.setProduct(modelMapper.map(productOrderDTO.getProductDTO(), Product.class));
                productOrder.setTotal(productOrderDTO.getTotal());
                productOrder.setUsername(productOrderDTO.getUsername());
                productOrder.setBuyLater(productOrderDTO.isBuyLater());
                productOrder.setSize(productOrderDTO.getSize());
                productOrder.setQuantity(productOrderDTO.getQuantity());
                productOrder.setOnChanges(false);

                productOrderList.add(productOrder);
            }

            productOrderRepository.saveAll(productOrderList);

            return true;
        } catch (Exception | Error e) {
            log.debug("Errore nel rollback dell'ordine");
            return false;
        }
    }

    @Override
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
    public boolean save(ProductOrderDTO productOrderDTO) {
        if(productOrderDTO != null) {
            productOrderDTO.setOnChanges(false);
            productOrderRepository.save(modelMapper.map(productOrderDTO, ProductOrder.class));
            return true;
        }else{
            return false;
        }
    }

    @Override
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
    public boolean deleteProductCart(String username, ProductDTO productDTO) {
        try {
            productOrderRepository.deleteByUsernameAndOrderNullAndProduct(username, modelMapper.map(productDTO, Product.class));

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
                productOrder.setOrder(modelMapper.map(productOrderDTO.getOrderDTO(), Order.class));
                productOrder.setProduct(modelMapper.map(productOrderDTO.getProductDTO(), Product.class));
                productOrder.setTotal(productOrderDTO.getTotal());
                productOrder.setUsername(productOrderDTO.getUsername());
                productOrder.setBuyLater(productOrderDTO.isBuyLater());
                productOrder.setSize(productOrderDTO.getSize());
                productOrder.setQuantity(productOrderDTO.getQuantity());

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
            ProductOrder productOrder= productOrderRepository.findByUsernameAndProduct(username, modelMapper.map(productDTO, Product.class));

            if(productOrder == null)
                return false;

            productOrder.setBuyLater(!productOrder.isBuyLater());
            productOrderRepository.save(productOrder);

            return true;
        }catch (Exception | Error e){
            log.debug("Errore nel salvataggio dell'ordine");
            return false;
        }
    }

    @Override
    public boolean changeQuantity(String username, ProductDTO productDTO, int quantity, String size) {
        try{
            System.out.println(productDTO.getName()+" "+quantity+" "+size);
            ProductOrder productOrder = productOrderRepository
                    .findByUsernameAndProduct(username, modelMapper.map(productDTO, Product.class));

            if(productOrder == null)
                return false;
            productOrder.setQuantity(quantity);
            productOrder.setSize(size);
            productOrderRepository.save(productOrder);

            return true;
        }catch (Exception | Error e){
            System.out.println(e);
            log.debug("Errore nell'aggiornamento dell'ordine");
            return false;
        }
    }

    @Override
    public List<ProductOrderDTO> getProductInOrder(String username, OrderDTO orderDTO) {
        try {
            List<ProductOrder> productOrders= productOrderRepository.findAllByUsernameAndOrder(username, modelMapper.map(orderDTO, Order.class));

            return productOrders.stream()
                    .map(prod -> {
                        ProductOrderDTO productOrderDTO= new ProductOrderDTO();
                        productOrderDTO.setId(prod.getId());
                        productOrderDTO.setTotal(prod.getTotal());
                        productOrderDTO.setProductDTO(modelMapper.map(prod.getProduct(), ProductDTO.class));
                        productOrderDTO.setSize(prod.getSize());
                        productOrderDTO.setQuantity(prod.getQuantity());

                        return productOrderDTO;
                    })
                    .toList();
        } catch (Exception | Error e) {
                log.debug("Errore nella presa dei prodotti nell'ordine");
            return null;
        }
    }



    @Override
    public boolean validateOrRollbackDeleteUserCart(String username, boolean rollback) {
        try {
            System.out.println("Pre presa dei prodotti negli ordini");
            List<ProductOrder> products= productOrderRepository.findAllByUsername(username);

            System.out.println("Post presa dei prodotti negli ordini");
            if(products.isEmpty())
                return true;

            for(ProductOrder productOrder: products){
                System.out.println(productOrder.getUsername());
                productOrder.setOnChanges(!rollback);
            }

            System.out.println("pre salvataggio");
            productOrderRepository.saveAll(products);
            System.out.println("Post salvataggio");
            return true;
        }catch(Exception | Error e) {
            System.out.println(e);
            log.debug("Errore nella presa dei prodotti della lista");
            return false;
        }
    }
}
