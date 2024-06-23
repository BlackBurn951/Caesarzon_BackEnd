package org.caesar.productservice.Data.Dao;

import org.caesar.productservice.Data.Entities.Image;
import org.caesar.productservice.Data.Entities.Product;
import org.caesar.productservice.Dto.ImageDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ImageRepository extends JpaRepository<Image, UUID> {

    ImageDTO findImageByidProduct(Product productID);
}
