package com.itpm.demoapi.demo.service

import com.itpm.demoapi.demo.config.LogCleanupConfig
import com.itpm.demoapi.demo.repository.ApiLogRepository
import org.slf4j.LoggerFactory
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Service
class LogCleanupService(
    private val apiLogRepository: ApiLogRepository,
    private val mongoTemplate: MongoTemplate,
    private val logCleanupConfig: LogCleanupConfig
) {
    
    private val logger = LoggerFactory.getLogger(LogCleanupService::class.java)
    
    /**
     * Scheduled cleanup task that runs daily at 2 AM
     * Only executes if cleanup is enabled and there are logs older than retention period
     */
    @Scheduled(cron = "\${app.log-cleanup.cron-expression}")
    @Transactional
    fun cleanupOldLogs() {
        if (!logCleanupConfig.enabled) {
            logger.info("Log cleanup is disabled. Skipping cleanup task.")
            return
        }
        
        try {
            val cutoffDate = LocalDateTime.now().minus(logCleanupConfig.retentionDays.toLong(), ChronoUnit.DAYS)
            logger.info("Starting log cleanup for logs older than: $cutoffDate")
            
            // Check if there are any logs to clean up
            val countToDelete = countLogsOlderThan(cutoffDate)
            if (countToDelete == 0L) {
                logger.info("No logs found older than $cutoffDate. Skipping cleanup.")
                return
            }
            
            logger.info("Found $countToDelete logs to delete. Starting batch cleanup...")
            
            // Perform batch deletion
            val deletedCount = deleteLogsInBatches(cutoffDate)
            
            logger.info("Log cleanup completed. Deleted $deletedCount logs older than $cutoffDate")
            
        } catch (e: Exception) {
            logger.error("Error during log cleanup: ${e.message}", e)
        }
    }
    
    /**
     * Count logs older than the specified date
     */
    private fun countLogsOlderThan(cutoffDate: LocalDateTime): Long {
        val query = Query(Criteria.where("timestamp").lt(cutoffDate))
        return mongoTemplate.count(query, "api_logs")
    }
    
    /**
     * Delete logs in batches to avoid memory issues
     */
    private fun deleteLogsInBatches(cutoffDate: LocalDateTime): Long {
        var totalDeleted = 0L
        var hasMore = true
        
        while (hasMore) {
            val query = Query(Criteria.where("timestamp").lt(cutoffDate))
            query.limit(logCleanupConfig.batchSize)
            
            val batchToDelete = mongoTemplate.find(query, com.itpm.demoapi.demo.model.ApiLog::class.java, "api_logs")
            
            if (batchToDelete.isEmpty()) {
                hasMore = false
            } else {
                val idsToDelete = batchToDelete.mapNotNull { it.id }
                if (idsToDelete.isNotEmpty()) {
                    val deleteQuery = Query(Criteria.where("_id").`in`(idsToDelete))
                    val deleteResult = mongoTemplate.remove(deleteQuery, "api_logs")
                    totalDeleted += deleteResult.deletedCount
                    
                    logger.debug("Deleted batch of ${deleteResult.deletedCount} logs")
                }
                
                // If we got less than batch size, we're done
                if (batchToDelete.size < logCleanupConfig.batchSize) {
                    hasMore = false
                }
            }
        }
        
        return totalDeleted
    }
    
    /**
     * Manual cleanup method for testing or immediate cleanup
     */
    fun manualCleanup(): Long {
        logger.info("Manual cleanup triggered")
        val cutoffDate = LocalDateTime.now().minus(logCleanupConfig.retentionDays.toLong(), ChronoUnit.DAYS)
        return deleteLogsInBatches(cutoffDate)
    }
    
    /**
     * Get cleanup statistics
     */
    fun getCleanupStats(): Map<String, Any> {
        val cutoffDate = LocalDateTime.now().minus(logCleanupConfig.retentionDays.toLong(), ChronoUnit.DAYS)
        val countToDelete = countLogsOlderThan(cutoffDate)
        
        return mapOf(
            "retentionDays" to logCleanupConfig.retentionDays,
            "cutoffDate" to cutoffDate.toString(),
            "logsToDelete" to countToDelete,
            "cleanupEnabled" to logCleanupConfig.enabled,
            "batchSize" to logCleanupConfig.batchSize
        )
    }
} 