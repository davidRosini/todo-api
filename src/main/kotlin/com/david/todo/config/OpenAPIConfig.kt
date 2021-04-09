package com.david.todo.config

import org.springframework.context.annotation.Configuration

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean

@Configuration
class OpenAPIConfig {

    @Bean
    fun customOpenAPI(@Value("\${springdoc.version}") appVersion: String): OpenAPI {
        return OpenAPI()
            .info(
                Info().title("Todo API").version(appVersion)
                    .license(License().name("Apache 2.0").url("https://springdoc.orgg"))
            )
    }
}