package org.caesar.productservice.Data.Dao;

import org.caesar.productservice.Data.Entities.Availability;
import org.caesar.productservice.Data.Entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, UUID> {

    List<Availability> findAllByProduct(Product product);

    Availability findByProductAndSize(Product product, String size);

    void deleteAllByProduct(Product product);
}
