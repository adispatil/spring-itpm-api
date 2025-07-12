package com.itpm.demoapi.demo.config

import com.itpm.demoapi.demo.interceptor.ApiLoggingInterceptor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer

@Configuration
class WebConfig(private val apiLoggingInterceptor: ApiLoggingInterceptor) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(apiLoggingInterceptor)
            .addPathPatterns("/api/**") // Apply to all API endpoints
            .excludePathPatterns("/api/logs/**") // Exclude logs endpoint to avoid infinite loops
    }

    override fun configureContentNegotiation(configurer: ContentNegotiationConfigurer) {
        configurer.defaultContentType(org.springframework.http.MediaType.APPLICATION_JSON)
    }
} 