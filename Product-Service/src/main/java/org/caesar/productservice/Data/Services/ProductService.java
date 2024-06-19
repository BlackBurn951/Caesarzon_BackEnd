package org.caesar.productservice.Data.Services;

import org.caesar.productservice.Data.Entities.Availability;
import org.caesar.productservice.Dto.ProductDTO;

import java.util.List;
import java.util.UUID;

public interface ProductService {

    UUID addOrUpdateProduct(ProductDTO product, List<Availability> availabilities);

    ProductDTO getProductByName(String name);

    ProductDTO getProductById(UUID id);

    List<ProductDTO> getAllProducts();

    boolean deleteProductById(UUID id);
}
