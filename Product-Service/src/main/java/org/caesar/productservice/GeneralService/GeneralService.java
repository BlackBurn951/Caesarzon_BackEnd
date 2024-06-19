package org.caesar.productservice.GeneralService;

import org.caesar.productservice.Data.Entities.Product;
import org.caesar.productservice.Dto.ProductDTO;

public interface GeneralService {

    Product addProduct(ProductDTO product);
}
