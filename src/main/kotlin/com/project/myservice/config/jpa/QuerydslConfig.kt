package com.project.myservice.config.jpa

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

@Configuration
class QuerydslConfig(
    @PersistenceContext val entityManager: EntityManager,
) {

    @Bean
    fun queryFactory() = JPAQueryFactory(entityManager)
}