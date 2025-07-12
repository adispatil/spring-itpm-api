package com.itpm.demoapi.demo.repository

import com.itpm.demoapi.demo.model.ApiLog
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface ApiLogRepository : MongoRepository<ApiLog, String> {
    
    fun findByUserId(userId: String): List<ApiLog>
    
    fun findByEndpoint(endpoint: String): List<ApiLog>
    
    fun findByMethod(method: String): List<ApiLog>
    
    fun findByResponseStatus(status: Int): List<ApiLog>
    
    fun findByTimestampBetween(startDate: LocalDateTime, endDate: LocalDateTime): List<ApiLog>
    
    @Query("{'timestamp': {\$gte: ?0, \$lte: ?1}}")
    fun findLogsByDateRange(startDate: LocalDateTime, endDate: LocalDateTime): List<ApiLog>
    
    @Query("{'userId': ?0, 'timestamp': {\$gte: ?1, \$lte: ?2}}")
    fun findUserLogsByDateRange(userId: String, startDate: LocalDateTime, endDate: LocalDateTime): List<ApiLog>
    
    @Query("{'responseStatus': {\$gte: 400}}")
    fun findErrorLogs(): List<ApiLog>
    
    @Query("{'userId': ?0, 'responseStatus': {\$gte: 400}}")
    fun findUserErrorLogs(userId: String): List<ApiLog>
} 