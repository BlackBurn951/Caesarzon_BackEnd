package org.caesar.productservice.Data.Dao;

import org.caesar.productservice.Data.Entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    List<Order> findOrderByUsername(String username);

    Order findOrderByIdAndUsername(UUID id, String username);
}
