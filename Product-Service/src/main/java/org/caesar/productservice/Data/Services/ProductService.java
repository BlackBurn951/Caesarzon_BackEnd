package org.caesar.productservice.Data.Services;

import org.caesar.productservice.Data.Entities.Product;
import org.caesar.productservice.Dto.ProductDTO;
import org.caesar.productservice.Dto.SendProductDTO;

import java.util.List;
import java.util.UUID;

public interface ProductService {

    Product addOrUpdateProduct(ProductDTO product);

    SendProductDTO getProductByName(String name);

    SendProductDTO getProductById(UUID id);

    List<SendProductDTO> getAllProducts();

    boolean deleteProductById(UUID id);
}
