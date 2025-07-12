package com.itpm.demoapi.demo.controller

import com.itpm.demoapi.demo.model.ApiLog
import com.itpm.demoapi.demo.service.ApiLogService
import com.itpm.demoapi.demo.service.LogCleanupService
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/logs")
class ApiLogController(
    private val apiLogService: ApiLogService,
    private val logCleanupService: LogCleanupService
) {

    @GetMapping
    fun getAllLogs(
        @RequestParam(required = false) userId: String?,
        @RequestParam(required = false) endpoint: String?,
        @RequestParam(required = false) method: String?,
        @RequestParam(required = false) status: Int?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startDate: LocalDateTime?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endDate: LocalDateTime?,
        @RequestParam(defaultValue = "100") limit: Int
    ): ResponseEntity<List<ApiLog>> {
        val logs = when {
            userId != null && startDate != null && endDate != null -> {
                apiLogService.getUserLogsByDateRange(userId, startDate, endDate)
            }
            startDate != null && endDate != null -> {
                apiLogService.getLogsByDateRange(startDate, endDate)
            }
            userId != null -> {
                apiLogService.getLogsByUserId(userId)
            }
            endpoint != null -> {
                apiLogService.getLogsByEndpoint(endpoint)
            }
            method != null -> {
                apiLogService.getLogsByMethod(method)
            }
            status != null -> {
                apiLogService.getLogsByResponseStatus(status)
            }
            else -> {
                apiLogService.getAllLogs()
            }
        }
        
        return ResponseEntity(logs.take(limit), HttpStatus.OK)
    }

    @GetMapping("/errors")
    fun getErrorLogs(
        @RequestParam(required = false) userId: String?
    ): ResponseEntity<List<ApiLog>> {
        val logs = if (userId != null) {
            apiLogService.getUserErrorLogs(userId)
        } else {
            apiLogService.getErrorLogs()
        }
        return ResponseEntity(logs, HttpStatus.OK)
    }

    @GetMapping("/users/{userId}")
    fun getUserLogs(@PathVariable userId: String): ResponseEntity<List<ApiLog>> {
        val logs = apiLogService.getLogsByUserId(userId)
        return ResponseEntity(logs, HttpStatus.OK)
    }

    @GetMapping("/endpoints/{endpoint}")
    fun getEndpointLogs(@PathVariable endpoint: String): ResponseEntity<List<ApiLog>> {
        val logs = apiLogService.getLogsByEndpoint(endpoint)
        return ResponseEntity(logs, HttpStatus.OK)
    }

    @GetMapping("/methods/{method}")
    fun getMethodLogs(@PathVariable method: String): ResponseEntity<List<ApiLog>> {
        val logs = apiLogService.getLogsByMethod(method)
        return ResponseEntity(logs, HttpStatus.OK)
    }

    @GetMapping("/status/{status}")
    fun getStatusLogs(@PathVariable status: Int): ResponseEntity<List<ApiLog>> {
        val logs = apiLogService.getLogsByResponseStatus(status)
        return ResponseEntity(logs, HttpStatus.OK)
    }

    @GetMapping("/date-range")
    fun getLogsByDateRange(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startDate: LocalDateTime,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endDate: LocalDateTime,
        @RequestParam(required = false) method: String?,
        @RequestParam(required = false) endpoint: String?
    ): ResponseEntity<List<ApiLog>> {
        val logs = when {
            method != null -> {
                apiLogService.getLogsByDateRangeAndMethod(startDate, endDate, method)
            }
            endpoint != null -> {
                apiLogService.getLogsByDateRangeAndEndpoint(startDate, endDate, endpoint)
            }
            else -> {
                apiLogService.getLogsByDateRange(startDate, endDate)
            }
        }
        return ResponseEntity(logs, HttpStatus.OK)
    }

    @GetMapping("/stats")
    fun getLogStats(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) startDate: LocalDateTime?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) endDate: LocalDateTime?
    ): ResponseEntity<Map<String, Any>> {
        val logs = if (startDate != null && endDate != null) {
            apiLogService.getLogsByDateRange(startDate, endDate)
        } else {
            apiLogService.getAllLogs()
        }

        val stats = mapOf(
            "totalRequests" to logs.size,
            "successfulRequests" to logs.count { it.responseStatus < 400 },
            "errorRequests" to logs.count { it.responseStatus >= 400 },
            "averageResponseTime" to logs.mapNotNull { it.executionTime }.average(),
            "uniqueUsers" to logs.mapNotNull { it.userId }.distinct().size,
            "uniqueEndpoints" to logs.map { it.endpoint }.distinct().size,
            "methods" to logs.groupBy { it.method }.mapValues { it.value.size },
            "statusCodes" to logs.groupBy { it.responseStatus }.mapValues { it.value.size }
        )

        return ResponseEntity(stats, HttpStatus.OK)
    }

    @PostMapping("/cleanup/manual")
    fun triggerManualCleanup(): ResponseEntity<Map<String, Any>> {
        val deletedCount = logCleanupService.manualCleanup()
        val response = mapOf(
            "success" to true,
            "message" to "Manual cleanup completed",
            "deletedCount" to deletedCount
        )
        return ResponseEntity(response, HttpStatus.OK)
    }

    @GetMapping("/cleanup/stats")
    fun getCleanupStats(): ResponseEntity<Map<String, Any>> {
        val stats = logCleanupService.getCleanupStats()
        return ResponseEntity(stats, HttpStatus.OK)
    }
} 