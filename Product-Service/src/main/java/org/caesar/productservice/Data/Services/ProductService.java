package org.caesar.productservice.Data.Services;

import org.caesar.productservice.Data.Entities.Product;
import org.caesar.productservice.Dto.ProductDTO;

import java.util.List;
import java.util.UUID;

public interface ProductService {

    Product addOrUpdateProduct(ProductDTO product);

    UUID getProductIDByName(String name);

    ProductDTO getProductById(UUID id);

    List<ProductDTO> getAllProducts();

    boolean deleteProductById(UUID id);

    List<ProductDTO> searchProducts(String searchText, Double minPrice, Double maxPrice, Boolean isClothing);

}
