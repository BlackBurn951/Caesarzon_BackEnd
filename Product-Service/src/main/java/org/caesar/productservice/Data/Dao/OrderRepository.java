package org.caesar.productservice.Data.Dao;

import org.caesar.productservice.Data.Entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
