package com.itpm.demoapi.demo.datasource.mongo

import com.itpm.demoapi.demo.datasource.TodoDataSource
import com.itpm.demoapi.demo.model.Todo
import com.itpm.demoapi.demo.model.TodoRequest
import com.itpm.demoapi.demo.model.TodoUpdateRequest
import com.itpm.demoapi.demo.repository.TodoRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class MongoTodoDataSource(private val todoRepository: TodoRepository) : TodoDataSource {
    override fun createTodo(userId: String, request: TodoRequest): Todo {
        val todo = Todo(
            userId = userId,
            title = request.title,
            description = request.description
        )
        return todoRepository.save(todo)
    }

    override fun updateTodo(userId: String, todoId: String, request: TodoUpdateRequest): Todo? {
        val todo = todoRepository.findById(todoId).orElse(null)
        if (todo == null || todo.userId != userId) return null
        val updated = todo.copy(
            title = request.title ?: todo.title,
            description = request.description ?: todo.description,
            completed = request.completed ?: todo.completed,
            updatedAt = LocalDateTime.now()
        )
        return todoRepository.save(updated)
    }

    override fun deleteTodo(userId: String, todoId: String): Boolean {
        val todo = todoRepository.findById(todoId).orElse(null)
        if (todo == null || todo.userId != userId) return false
        todoRepository.deleteById(todoId)
        return true
    }

    override fun getTodosByUserId(userId: String): List<Todo> {
        return todoRepository.findAllByUserId(userId)
    }
} 