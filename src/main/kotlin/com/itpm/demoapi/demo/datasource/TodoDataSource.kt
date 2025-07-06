package com.itpm.demoapi.demo.datasource

import com.itpm.demoapi.demo.model.Todo
import com.itpm.demoapi.demo.model.TodoRequest
import com.itpm.demoapi.demo.model.TodoUpdateRequest

interface TodoDataSource {
    fun createTodo(userId: String, request: TodoRequest): Todo
    fun updateTodo(userId: String, todoId: String, request: TodoUpdateRequest): Todo?
    fun deleteTodo(userId: String, todoId: String): Boolean
    fun getTodosByUserId(userId: String): List<Todo>
} 