package org.caesar.productservice.Data.Dao;

import org.caesar.productservice.Data.Entities.Product;
import org.caesar.productservice.Data.Entities.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    List<Review>findByproductID(Product product);

    Review findByuserIDAndProductID(String username, Product productID);

}
