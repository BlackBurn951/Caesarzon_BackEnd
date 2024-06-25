package org.caesar.productservice.Data.Services;

import org.caesar.productservice.Data.Entities.Product;
import org.caesar.productservice.Dto.ProductDTO;

import java.util.List;
import java.util.UUID;

public interface ProductService {

    Product addOrUpdateProduct(ProductDTO product);

    UUID getProductIDByName(String name);

    ProductDTO getProductById(UUID id);


    public List<ProductDTO> getProductByPrice(double priceMin, double priceMax);

    List<ProductDTO> getAllProducts();

    boolean deleteProductById(UUID id);

    List<Product> searchProducts(String searchText, Double minPrice, Double maxPrice, Boolean isClothing);

}
