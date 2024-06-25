package org.caesar.productservice.GeneralService;

import org.caesar.productservice.Data.Entities.Availability;
import org.caesar.productservice.Dto.DTOOrder.BuyDTO;
import org.caesar.productservice.Dto.ImageDTO;
import org.caesar.productservice.Dto.ProductDTO;
import org.caesar.productservice.Dto.SendProductOrderDTO;

import java.util.List;
import java.util.UUID;

public interface GeneralService {


    boolean addProduct(ProductDTO ProductDTO);
    boolean deleteProduct(UUID id);
    List<ImageDTO> getProductImages(UUID id);
    List<ImageDTO> getAllProductImages(UUID productID);
    boolean createOrder(String username, SendProductOrderDTO sendProductOrderDTO);
    boolean updateOrder(String username, BuyDTO buyDTO);
    ProductDTO getProductAndAvailabilitiesAndImages(UUID id);

}
