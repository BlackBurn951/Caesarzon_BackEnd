package org.caesar.productservice.Data.Services.Impl;

import lombok.RequiredArgsConstructor;
import org.caesar.productservice.Data.Dao.ProductOrderRepository;
import org.caesar.productservice.Data.Entities.ProductOrder;
import org.caesar.productservice.Data.Services.ProductOrderService;
import org.caesar.productservice.Dto.ProductOrderDTO;
import org.caesar.productservice.Dto.SendProductOrderDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
@Service
@RequiredArgsConstructor
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
    public boolean deleteProductCart(String username, UUID id) {
        try {
            productOrderRepository.deleteByUsernameAndOrderIDNullAndProductID(username, id);

            return true;
        } catch (Exception | Error e) {
//            log.debug("Errore nell'eliminazione del singolo prodotto dalla lista desideri");
            return false;
        }
    }

    @Override
    public boolean deleteProductOrders(List<UUID> ids) {
        return false;
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
        return productOrderRepository.findAllByUsernameAAndOrderIDNull(username).stream().map(a -> modelMapper.map(a, ProductOrderDTO.class)).toList();

    }

    @Override
    public boolean saveAll(List<ProductOrderDTO> orderDTOS) {
        try {
            productOrderRepository.saveAll(orderDTOS.stream().map(a -> modelMapper.map(a, ProductOrder.class)).toList());

            return true;
        } catch (Exception | Error e) {
            //log.debug("Errore nel salvataggio degli ordini");
            return false;
        }
    }
    @Override
    public boolean updateOrder(String username, UUID productId){
        try{
            ProductOrderDTO productOrderDTO = modelMapper.map(productOrderRepository.findAllByUsernameAAndOrderIDNullAAndProductID(username, productId).getFirst(), ProductOrderDTO.class);
            if(productOrderDTO == null)
                return false;
            productOrderDTO.setBuyLater(true);
            productOrderRepository.save(modelMapper.map(productOrderDTO, ProductOrder.class));
            return true;

        }catch (Exception | Error e){
//            log.debug("Errore nell'aggiornamento dell'ordine'");
            return false;
        }
    }

}
