package org.caesar.productservice.Data.Dao;

import org.caesar.productservice.Data.Entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    List<Order> findAllOrdersByUsername(String username);

    Order findOrdersByUsername(String username);

    Order findOrderByIdAndUsername(UUID id, String username);

    List<Order> findByOrderStateAndExpectedDeliveryDate(String state, LocalDate date);

    List<Order> findAllByOrderState(String state);

}
