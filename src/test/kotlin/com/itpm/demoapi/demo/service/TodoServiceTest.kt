package com.itpm.demoapi.demo.service

import com.itpm.demoapi.demo.datasource.TodoDataSource
import com.itpm.demoapi.demo.datasource.mongo.MongoTodoDataSource
import com.itpm.demoapi.demo.model.Todo
import com.itpm.demoapi.demo.model.TodoRequest
import com.itpm.demoapi.demo.model.TodoUpdateRequest
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class TodoServiceTest {

    private lateinit var todoDataSource: TodoDataSource
    private lateinit var mongoTodoDataSource: MongoTodoDataSource
    private lateinit var todoService: TodoService

    @BeforeEach
    fun setUp() {
        todoDataSource = mockk()
        mongoTodoDataSource = mockk()
        todoService = TodoService(todoDataSource, mongoTodoDataSource)
    }

    @Test
    fun `createTodo should return created todo`() {
        val userId = "user1"
        val request = TodoRequest(title = "Test Todo", description = "Test Desc")
        val todo = Todo(
            id = "1",
            userId = userId,
            title = "Test Todo",
            description = "Test Desc",
            completed = false,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        every { todoDataSource.createTodo(userId, request) } returns todo

        val result = todoService.createTodo(userId, request)
        assertEquals(todo, result)
    }

    @Test
    fun `updateTodo should return updated todo`() {
        val userId = "user1"
        val todoId = "1"
        val request = TodoUpdateRequest(title = "Updated", description = "Updated Desc", completed = true)
        val updatedTodo = Todo(
            id = todoId,
            userId = userId,
            title = "Updated",
            description = "Updated Desc",
            completed = true,
            createdAt = LocalDateTime.now().minusDays(1),
            updatedAt = LocalDateTime.now()
        )
        every { todoDataSource.updateTodo(userId, todoId, request) } returns updatedTodo

        val result = todoService.updateTodo(userId, todoId, request)
        assertEquals(updatedTodo, result)
    }

    @Test
    fun `updateTodo should return null if not found or not owned`() {
        val userId = "user1"
        val todoId = "1"
        val request = TodoUpdateRequest(title = "Updated")
        every { todoDataSource.updateTodo(userId, todoId, request) } returns null

        val result = todoService.updateTodo(userId, todoId, request)
        assertNull(result)
    }

    @Test
    fun `deleteTodo should return true if deleted`() {
        val userId = "user1"
        val todoId = "1"
        every { todoDataSource.deleteTodo(userId, todoId) } returns true

        val result = todoService.deleteTodo(userId, todoId)
        assertTrue(result)
    }

    @Test
    fun `deleteTodo should return false if not found or not owned`() {
        val userId = "user1"
        val todoId = "1"
        every { todoDataSource.deleteTodo(userId, todoId) } returns false

        val result = todoService.deleteTodo(userId, todoId)
        assertFalse(result)
    }

    @Test
    fun `getTodosByUserId should return list of todos`() {
        val userId = "user1"
        val todos = listOf(
            Todo(
                id = "1",
                userId = userId,
                title = "Todo 1",
                description = "Desc 1",
                completed = false,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            ),
            Todo(
                id = "2",
                userId = userId,
                title = "Todo 2",
                description = "Desc 2",
                completed = true,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
        )
        every { todoDataSource.getTodosByUserId(userId) } returns todos

        val result = todoService.getTodosByUserId(userId)
        assertEquals(todos, result)
    }
} 