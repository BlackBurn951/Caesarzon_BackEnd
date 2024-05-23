package org.caesar.productservice.Data.Dao;

import org.caesar.productservice.Data.Entities.ProductOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductOrderRepository extends JpaRepository<ProductOrder, Long> {
}
