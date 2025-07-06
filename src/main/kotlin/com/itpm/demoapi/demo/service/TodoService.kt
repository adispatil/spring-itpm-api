package com.itpm.demoapi.demo.service

import com.itpm.demoapi.demo.datasource.TodoDataSource
import com.itpm.demoapi.demo.datasource.mongo.MongoTodoDataSource
import com.itpm.demoapi.demo.model.Todo
import com.itpm.demoapi.demo.model.TodoRequest
import com.itpm.demoapi.demo.model.TodoUpdateRequest
import org.springframework.stereotype.Service

@Service
class TodoService(
    private val todoDataSource: TodoDataSource,
    private val mongoTodoDataSource: MongoTodoDataSource
) {
    fun createTodo(userId: String, request: TodoRequest): Todo {
        return todoDataSource.createTodo(userId, request)
    }

    fun updateTodo(userId: String, todoId: String, request: TodoUpdateRequest): Todo? {
        return todoDataSource.updateTodo(userId, todoId, request)
    }

    fun deleteTodo(userId: String, todoId: String): Boolean {
        return todoDataSource.deleteTodo(userId, todoId)
    }

    fun getTodosByUserId(userId: String): List<Todo> {
        return todoDataSource.getTodosByUserId(userId)
    }
} 