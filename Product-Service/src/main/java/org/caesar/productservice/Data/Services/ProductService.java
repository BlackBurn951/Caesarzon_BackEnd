package org.caesar.productservice.Data.Services;

import org.caesar.productservice.Data.Entities.Product;
import org.caesar.productservice.Dto.ProductDTO;

import java.util.List;
import java.util.UUID;

public interface ProductService {

    UUID addOrUpdateProduct(ProductDTO product);
    ProductDTO getProductById(UUID id);
    List<ProductDTO> getAllProducts();
    boolean deleteProductById(UUID id);
}
