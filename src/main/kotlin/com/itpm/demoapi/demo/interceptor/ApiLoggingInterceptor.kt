package com.itpm.demoapi.demo.interceptor

import com.fasterxml.jackson.databind.ObjectMapper
import com.itpm.demoapi.demo.model.ApiLog
import com.itpm.demoapi.demo.service.ApiLogService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture

@Component
class ApiLoggingInterceptor(
    private val apiLogService: ApiLogService,
    private val objectMapper: ObjectMapper
) : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val startTime = System.currentTimeMillis()
        request.setAttribute("startTime", startTime)
        return true
    }

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?
    ) {
        try {
            val startTime = request.getAttribute("startTime") as Long?
            val endTime = System.currentTimeMillis()
            val executionTime = startTime?.let { endTime - it }

            val userId = extractUserId(request)
            val requestBody = extractRequestBody(request)
            val responseBody = extractResponseBody(response)
            val headers = extractHeaders(request)
            val queryParams = extractQueryParams(request)
            val pathParams = extractPathParams(request)

            val apiLog = ApiLog(
                endpoint = request.requestURI,
                method = request.method,
                userId = userId,
                requestBody = requestBody,
                responseStatus = response.status,
                responseBody = responseBody,
                userAgent = request.getHeader("User-Agent"),
                ipAddress = getClientIpAddress(request),
                executionTime = executionTime,
                timestamp = LocalDateTime.now(),
                errorMessage = ex?.message,
                requestHeaders = headers,
                queryParams = queryParams,
                pathParams = pathParams
            )

            // Log asynchronously to avoid blocking the response
            CompletableFuture.runAsync {
                try {
                    apiLogService.saveApiLog(apiLog)
                } catch (e: Exception) {
                    // Log the error but don't fail the request
                    println("Failed to save API log: ${e.message}")
                }
            }
        } catch (e: Exception) {
            // Don't let logging errors affect the main request
            println("Error in API logging interceptor: ${e.message}")
        }
    }

    private fun extractUserId(request: HttpServletRequest): String? {
        return request.getHeader("userId") ?: request.getHeader("X-User-ID")
    }

    private fun extractRequestBody(request: HttpServletRequest): String? {
        return try {
            if (request is ContentCachingRequestWrapper) {
                val content = request.contentAsByteArray
                if (content.isNotEmpty()) {
                    String(content)
                } else null
            } else null
        } catch (e: Exception) {
            null
        }
    }

    private fun extractResponseBody(response: HttpServletResponse): String? {
        return try {
            if (response is ContentCachingResponseWrapper) {
                val content = response.contentAsByteArray
                if (content.isNotEmpty()) {
                    String(content)
                } else null
            } else null
        } catch (e: Exception) {
            null
        }
    }

    private fun extractHeaders(request: HttpServletRequest): Map<String, String> {
        val headers = mutableMapOf<String, String>()
        val headerNames = request.headerNames
        while (headerNames.hasMoreElements()) {
            val headerName = headerNames.nextElement()
            val headerValue = request.getHeader(headerName)
            if (headerValue != null) {
                headers[headerName] = headerValue
            }
        }
        return headers
    }

    private fun extractQueryParams(request: HttpServletRequest): Map<String, String> {
        val queryParams = mutableMapOf<String, String>()
        val queryString = request.queryString
        if (!queryString.isNullOrEmpty()) {
            queryString.split("&").forEach { param ->
                val keyValue = param.split("=")
                if (keyValue.size == 2) {
                    queryParams[keyValue[0]] = keyValue[1]
                }
            }
        }
        return queryParams
    }

    private fun extractPathParams(request: HttpServletRequest): Map<String, String> {
        // This is a simplified version. In a real application, you might want to use
        // Spring's PathVariable extraction or pattern matching
        return emptyMap()
    }

    private fun getClientIpAddress(request: HttpServletRequest): String? {
        var ip = request.getHeader("X-Forwarded-For")
        if (ip.isNullOrEmpty() || ip == "unknown") {
            ip = request.getHeader("X-Real-IP")
        }
        if (ip.isNullOrEmpty() || ip == "unknown") {
            ip = request.getHeader("Proxy-Client-IP")
        }
        if (ip.isNullOrEmpty() || ip == "unknown") {
            ip = request.getHeader("WL-Proxy-Client-IP")
        }
        if (ip.isNullOrEmpty() || ip == "unknown") {
            ip = request.getHeader("HTTP_CLIENT_IP")
        }
        if (ip.isNullOrEmpty() || ip == "unknown") {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR")
        }
        if (ip.isNullOrEmpty() || ip == "unknown") {
            ip = request.remoteAddr
        }
        return ip
    }
} 