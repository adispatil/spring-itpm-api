package com.itpm.demoapi.demo.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "todos")
data class Todo(
    @Id
    val id: String? = null,
    val userId: String,
    val title: String,
    val description: String? = null,
    val completed: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)

data class TodoRequest(
    val title: String,
    val description: String? = null
)

data class TodoUpdateRequest(
    val title: String? = null,
    val description: String? = null,
    val completed: Boolean? = null
) 