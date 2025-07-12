package com.itpm.demoapi.demo.service

import com.itpm.demoapi.demo.model.ApiLog
import com.itpm.demoapi.demo.repository.ApiLogRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class ApiLogServiceTest {

    private lateinit var apiLogService: ApiLogService
    private lateinit var apiLogRepository: ApiLogRepository

    @BeforeEach
    fun setUp() {
        apiLogRepository = mockk()
        apiLogService = ApiLogService(apiLogRepository)
    }

    @Test
    fun `saveApiLog should save log to repository`() {
        // Given
        val apiLog = ApiLog(
            endpoint = "/api/users",
            method = "GET",
            userId = "user1",
            responseStatus = 200,
            timestamp = LocalDateTime.now()
        )
        every { apiLogRepository.save(any()) } returns apiLog

        // When
        val result = apiLogService.saveApiLog(apiLog)

        // Then
        verify { apiLogRepository.save(apiLog) }
        assert(result == apiLog)
    }

    @Test
    fun `getAllLogs should return all logs from repository`() {
        // Given
        val logs = listOf(
            ApiLog(endpoint = "/api/users", method = "GET", responseStatus = 200),
            ApiLog(endpoint = "/api/todos", method = "POST", responseStatus = 201)
        )
        every { apiLogRepository.findAll() } returns logs

        // When
        val result = apiLogService.getAllLogs()

        // Then
        verify { apiLogRepository.findAll() }
        assert(result == logs)
    }

    @Test
    fun `getLogsByUserId should return user logs from repository`() {
        // Given
        val userId = "user1"
        val logs = listOf(
            ApiLog(endpoint = "/api/users", method = "GET", userId = userId, responseStatus = 200)
        )
        every { apiLogRepository.findByUserId(userId) } returns logs

        // When
        val result = apiLogService.getLogsByUserId(userId)

        // Then
        verify { apiLogRepository.findByUserId(userId) }
        assert(result == logs)
    }

    @Test
    fun `getErrorLogs should return error logs from repository`() {
        // Given
        val logs = listOf(
            ApiLog(endpoint = "/api/users", method = "GET", responseStatus = 404),
            ApiLog(endpoint = "/api/todos", method = "POST", responseStatus = 500)
        )
        every { apiLogRepository.findErrorLogs() } returns logs

        // When
        val result = apiLogService.getErrorLogs()

        // Then
        verify { apiLogRepository.findErrorLogs() }
        assert(result == logs)
    }
} 