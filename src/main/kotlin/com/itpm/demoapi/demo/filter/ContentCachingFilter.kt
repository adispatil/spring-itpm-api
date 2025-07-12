package com.itpm.demoapi.demo.filter

import jakarta.servlet.*
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import java.io.IOException

@Component
@Order(1)
class ContentCachingFilter : Filter {

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val wrappedRequest = ContentCachingRequestWrapper(request as HttpServletRequest)
        val wrappedResponse = ContentCachingResponseWrapper(response as HttpServletResponse)

        try {
            chain.doFilter(wrappedRequest, wrappedResponse)
        } finally {
            wrappedResponse.copyBodyToResponse()
        }
    }
} 