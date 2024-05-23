package org.caesar.productservice.Data.Dao;

import org.caesar.productservice.Data.Entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
