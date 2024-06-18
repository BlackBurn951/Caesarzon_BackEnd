package org.caesar.productservice.Data.Dao;

import org.caesar.productservice.Data.Entities.SportProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SportProductRepository extends JpaRepository<SportProduct, UUID> {
}
