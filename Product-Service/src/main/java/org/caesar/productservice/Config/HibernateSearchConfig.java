package org.caesar.productservice.Config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateSearchConfig {

    @Bean
    public SearchSession searchSession(EntityManager entityManager) {
        return Search.session(entityManager);
    }

    @Bean
    public boolean initializeHibernateSearch(EntityManagerFactory entityManagerFactory) {
        Search.mapping(entityManagerFactory).scope(Object.class).massIndexer().start();
        return true;
    }
}
