package org.caesar.productservice.Data.Dao;

import org.caesar.productservice.Data.Entities.ProductOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductOrderRepository extends JpaRepository<ProductOrder, UUID> {

    List<ProductOrder> findAllByUsernameAndOrderIDNull(String username);

    List<ProductOrder> findAllByUsernameAndOrderIDNullAndProductID(String username, UUID productId);

    void deleteByUsernameAndOrderIDNullAndProductID(String username, UUID productId);

    void deleteAllByUsernameAndOrderIDNull(String username);

}
