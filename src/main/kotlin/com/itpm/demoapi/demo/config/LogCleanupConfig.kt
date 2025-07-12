package com.itpm.demoapi.demo.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "app.log-cleanup")
data class LogCleanupConfig(
    var retentionDays: Int = 7,
    var batchSize: Int = 1000,
    var enabled: Boolean = true,
    var cronExpression: String = "0 2 * * *" // Daily at 2 AM
) 