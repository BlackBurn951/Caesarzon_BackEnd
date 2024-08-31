package org.caesar.productservice.Data.Dao;

import jakarta.transaction.Transactional;
import org.caesar.productservice.Data.Entities.Order;
import org.caesar.productservice.Data.Entities.Product;
import org.caesar.productservice.Data.Entities.ProductOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductOrderRepository extends JpaRepository<ProductOrder, UUID> {

    List<ProductOrder> findAllByUsernameAndOrderIsNull(String username);

    ProductOrder findByUsernameAndProductAndOrderIsNull(String username, Product productID);

    @Transactional
    void deleteByUsernameAndOrderNullAndProduct(String username, Product productId);

    @Transactional
    void deleteAllByUsernameAndOrderIsNull(String username);

    List<ProductOrder> findAllByUsernameAndOrder(String username, Order order);

    List<ProductOrder> findAllByUsername(String username);

    List<ProductOrder> findAllByUsernameAndOrderIsNotNullAndProduct(String username, Product product);
}
