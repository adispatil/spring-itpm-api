package com.itpm.demoapi.demo.repository

import com.itpm.demoapi.demo.model.Todo
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface TodoRepository : MongoRepository<Todo, String> {
    fun findAllByUserId(userId: String): List<Todo>
} 