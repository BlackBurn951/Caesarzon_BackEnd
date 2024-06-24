package org.caesar.productservice.Data.Dao;

import org.caesar.productservice.Data.Entities.Search;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SearchRepository extends JpaRepository<Search, UUID> {


}
