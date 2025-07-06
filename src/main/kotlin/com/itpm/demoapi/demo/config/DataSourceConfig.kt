package com.itpm.demoapi.demo.config

import com.itpm.demoapi.demo.datasource.UserDataSource
import com.itpm.demoapi.demo.datasource.mongo.MongoUserDataSource
import com.itpm.demoapi.demo.datasource.TodoDataSource
import com.itpm.demoapi.demo.datasource.mongo.MongoTodoDataSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class DataSourceConfig {

    @Bean
    @Primary
    fun userDataSource(mongoUserDataSource: MongoUserDataSource): UserDataSource {
        return mongoUserDataSource
    }

    @Bean
    @Primary
    fun todoDataSource(mongoTodoDataSource: MongoTodoDataSource): TodoDataSource {
        return mongoTodoDataSource
    }
} 