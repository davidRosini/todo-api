package com.david.todo.config

import io.r2dbc.spi.ConnectionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator

@Configuration
class DataBaseConfig {

    companion object {
        const val RESOURCE_DB_PATH = "\\db"
        const val RESOURCE_SCHEMA_PATH = "\\schema.sql"
        const val RESOURCE_MIGRATION_PATH = RESOURCE_DB_PATH + RESOURCE_SCHEMA_PATH
    }

    @Bean
    fun initializer(connectionFactory: ConnectionFactory): ConnectionFactoryInitializer {
        val initializer = ConnectionFactoryInitializer()
        initializer.setConnectionFactory(connectionFactory)
        initializer.setDatabasePopulator(ResourceDatabasePopulator(ClassPathResource(RESOURCE_MIGRATION_PATH)))
        return initializer
    }
}