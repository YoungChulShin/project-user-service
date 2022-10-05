package com.project.myservice.integrationtest.base

import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.utility.DockerImageName

abstract class BaseTestContainer {

    companion object {
        private val MY_SQL_CONTAINER = MySQLContainer(DockerImageName.parse("mysql:5.7.32"))
            .withInitScript("db/init.sql")
            .withDatabaseName("myservice")
            .withExposedPorts(3306)
            .withUsername("root")

        private val REDIS_CONTAINER = GenericContainer(DockerImageName.parse("redis:6.0.2"))
            .withExposedPorts(6379)

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", MY_SQL_CONTAINER::getJdbcUrl)
            registry.add("spring.redis.port", REDIS_CONTAINER::getFirstMappedPort)
        }
    }

    init {
        MY_SQL_CONTAINER.start()
        REDIS_CONTAINER.start()
    }
}