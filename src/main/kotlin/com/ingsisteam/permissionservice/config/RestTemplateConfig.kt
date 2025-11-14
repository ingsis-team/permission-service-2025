package com.ingsisteam.permissionservice.config

import org.slf4j.MDC
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.web.client.RestTemplate

@Configuration
class RestTemplateConfig {
    companion object {
        const val REQUEST_ID_HEADER = "X-Request-ID"
        const val REQUEST_ID_MDC_KEY = "requestId"
    }

    @Bean
    fun restTemplate(restTemplateBuilder: RestTemplateBuilder): RestTemplate {
        return restTemplateBuilder
            .interceptors(requestIdPropagationInterceptor())
            .build()
    }

    // Interceptor para propagar el Request ID a las llamadas HTTP salientes
    private fun requestIdPropagationInterceptor(): ClientHttpRequestInterceptor {
        return ClientHttpRequestInterceptor { request, body, execution ->
            val requestId = MDC.get(REQUEST_ID_MDC_KEY)
            if (requestId != null) {
                request.headers.add(REQUEST_ID_HEADER, requestId)
            }
            execution.execute(request, body)
        }
    }
}
