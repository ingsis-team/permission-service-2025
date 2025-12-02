package com.ingsisteam.permissionservice.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {
    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("Permission Service API")
                    .version("1.0.0")
                    .description("API para gestión de permisos de snippets"),
            )
            .servers(
                listOf(
                    Server()
                        .url("http://localhost:8081")
                        .description("Servidor de desarrollo"),
                ),
            )
    }
}
