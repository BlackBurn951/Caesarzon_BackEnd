package org.caesar.productservice.Data.Dao;

import org.caesar.productservice.Data.Entities.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
