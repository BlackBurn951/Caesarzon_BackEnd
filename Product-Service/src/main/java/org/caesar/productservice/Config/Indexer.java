package org.caesar.productservice.Config;

import jakarta.persistence.EntityManagerFactory;
import org.caesar.productservice.Data.Entities.Product;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.massindexing.MassIndexer;
import org.springframework.stereotype.Component;

@Component
public class Indexer {
    private final EntityManagerFactory entityManagerFactory;

    public Indexer(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public void reindex() {
        MassIndexer indexer = Search.mapping(entityManagerFactory)
                .scope(Product.class)
                .massIndexer();
        try {
            indexer.startAndWait();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Indexing interrupted", e);
        }
    }
}
