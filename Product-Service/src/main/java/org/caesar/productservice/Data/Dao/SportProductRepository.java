package org.caesar.productservice.Data.Dao;

import org.caesar.productservice.Data.Entities.SportProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SportProductRepository extends JpaRepository<SportProduct, Long> {
}
