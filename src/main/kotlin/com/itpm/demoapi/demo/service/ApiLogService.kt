package com.itpm.demoapi.demo.service

import com.itpm.demoapi.demo.model.ApiLog
import com.itpm.demoapi.demo.repository.ApiLogRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ApiLogService(private val apiLogRepository: ApiLogRepository) {
    
    fun saveApiLog(apiLog: ApiLog): ApiLog {
        return apiLogRepository.save(apiLog)
    }
    
    fun getAllLogs(): List<ApiLog> {
        return apiLogRepository.findAll()
    }
    
    fun getLogsByUserId(userId: String): List<ApiLog> {
        return apiLogRepository.findByUserId(userId)
    }
    
    fun getLogsByEndpoint(endpoint: String): List<ApiLog> {
        return apiLogRepository.findByEndpoint(endpoint)
    }
    
    fun getLogsByMethod(method: String): List<ApiLog> {
        return apiLogRepository.findByMethod(method)
    }
    
    fun getLogsByResponseStatus(status: Int): List<ApiLog> {
        return apiLogRepository.findByResponseStatus(status)
    }
    
    fun getLogsByDateRange(startDate: LocalDateTime, endDate: LocalDateTime): List<ApiLog> {
        return apiLogRepository.findLogsByDateRange(startDate, endDate)
    }
    
    fun getUserLogsByDateRange(userId: String, startDate: LocalDateTime, endDate: LocalDateTime): List<ApiLog> {
        return apiLogRepository.findUserLogsByDateRange(userId, startDate, endDate)
    }
    
    fun getErrorLogs(): List<ApiLog> {
        return apiLogRepository.findErrorLogs()
    }
    
    fun getUserErrorLogs(userId: String): List<ApiLog> {
        return apiLogRepository.findUserErrorLogs(userId)
    }
    
    fun getLogsByDateRangeAndMethod(startDate: LocalDateTime, endDate: LocalDateTime, method: String): List<ApiLog> {
        return apiLogRepository.findLogsByDateRange(startDate, endDate)
            .filter { it.method.equals(method, ignoreCase = true) }
    }
    
    fun getLogsByDateRangeAndEndpoint(startDate: LocalDateTime, endDate: LocalDateTime, endpoint: String): List<ApiLog> {
        return apiLogRepository.findLogsByDateRange(startDate, endDate)
            .filter { it.endpoint.contains(endpoint, ignoreCase = true) }
    }
} 