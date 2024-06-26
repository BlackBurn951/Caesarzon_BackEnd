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

    List<ProductOrder> findAllByUsernameAndOrderIDIsNull(String username);

    ProductOrder findByUsernameAndProductID(String username, Product productID);

    List<ProductOrder> findAllByUsernameAndOrderIDIsNullAndProductID(String username, Product productID);

    @Transactional
    void deleteByUsernameAndOrderIDNullAndProductID(String username, Product productId);

    @Transactional
    void deleteAllByUsernameAndOrderIDIsNull(String username);

    List<ProductOrder> findAllByUsernameAndOrderID(String username, Order order);

}
