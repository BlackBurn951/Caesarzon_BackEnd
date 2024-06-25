package org.caesar.productservice.Data.Services;

import org.caesar.productservice.Data.Entities.Product;
import org.caesar.productservice.Dto.SendProductDTO;

import java.util.List;
import java.util.UUID;

public interface ProductService {

    Product addOrUpdateProduct(SendProductDTO product);

    UUID getProductIDByName(String name);

    Product getProductById(UUID id);


    public List<SendProductDTO> getProductByPrice(double priceMin, double priceMax);

    List<SendProductDTO> getAllProducts();

    boolean deleteProductById(UUID id);

    List<Product> searchProducts(String searchText, Double minPrice, Double maxPrice, Boolean isClothing);

}
