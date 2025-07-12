package com.itpm.demoapi.demo.service

import com.itpm.demoapi.demo.config.LogCleanupConfig
import com.itpm.demoapi.demo.repository.ApiLogRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import java.time.LocalDateTime

class LogCleanupServiceTest {

    private lateinit var logCleanupService: LogCleanupService
    private lateinit var apiLogRepository: ApiLogRepository
    private lateinit var mongoTemplate: MongoTemplate
    private lateinit var logCleanupConfig: LogCleanupConfig

    @BeforeEach
    fun setUp() {
        apiLogRepository = mockk()
        mongoTemplate = mockk()
        logCleanupConfig = LogCleanupConfig(
            retentionDays = 7,
            batchSize = 1000,
            enabled = true,
            cronExpression = "0 2 * * *"
        )
        logCleanupService = LogCleanupService(apiLogRepository, mongoTemplate, logCleanupConfig)
    }

    @Test
    fun `getCleanupStats should return correct statistics`() {
        // Given
        val cutoffDate = LocalDateTime.now().minus(7, java.time.temporal.ChronoUnit.DAYS)
        every { mongoTemplate.count(any<Query>(), "api_logs") } returns 150L

        // When
        val stats = logCleanupService.getCleanupStats()

        // Then
        assert(stats["retentionDays"] == 7)
        assert(stats["logsToDelete"] == 150L)
        assert(stats["cleanupEnabled"] == true)
        assert(stats["batchSize"] == 1000)
    }

    @Test
    fun `manualCleanup should return deleted count`() {
        // Given
        val cutoffDate = LocalDateTime.now().minus(7, java.time.temporal.ChronoUnit.DAYS)
        every { mongoTemplate.count(any<Query>(), "api_logs") } returns 50L
        every { mongoTemplate.find(any<Query>(), any<Class<*>>(), "api_logs") } returns emptyList()
        every { mongoTemplate.remove(any<Query>(), "api_logs") } returns mockk(relaxed = true)

        // When
        val deletedCount = logCleanupService.manualCleanup()

        // Then
        assert(deletedCount == 0L)
        verify { mongoTemplate.count(any<Query>(), "api_logs") }
    }

    @Test
    fun `cleanup should be skipped when disabled`() {
        // Given
        logCleanupConfig.enabled = false

        // When
        logCleanupService.cleanupOldLogs()

        // Then
        // Should not call any mongoTemplate methods when disabled
        verify(exactly = 0) { mongoTemplate.count(any<Query>(), any<String>()) }
    }
} 