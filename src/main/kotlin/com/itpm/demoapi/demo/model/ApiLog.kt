package com.itpm.demoapi.demo.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "api_logs")
data class ApiLog(
    @Id
    val id: String? = null,
    val endpoint: String,
    val method: String,
    val userId: String? = null,
    val requestBody: String? = null,
    val responseStatus: Int,
    val responseBody: String? = null,
    val userAgent: String? = null,
    val ipAddress: String? = null,
    val executionTime: Long? = null, // in milliseconds
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val errorMessage: String? = null,
    val requestHeaders: Map<String, String>? = null,
    val queryParams: Map<String, String>? = null,
    val pathParams: Map<String, String>? = null
) 