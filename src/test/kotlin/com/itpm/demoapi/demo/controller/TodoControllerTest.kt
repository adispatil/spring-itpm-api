package com.itpm.demoapi.demo.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.itpm.demoapi.demo.model.Todo
import com.itpm.demoapi.demo.model.TodoRequest
import com.itpm.demoapi.demo.model.TodoUpdateRequest
import com.itpm.demoapi.demo.model.TodoResponse
import com.itpm.demoapi.demo.service.TodoService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.LocalDateTime

class TodoControllerTest {

    private lateinit var mockMvc: MockMvc
    private lateinit var todoService: TodoService
    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setUp() {
        todoService = mockk()
        objectMapper = ObjectMapper()
        mockMvc = MockMvcBuilders.standaloneSetup(TodoController(todoService)).build()
    }

    @Test
    fun `POST api todos should create todo`() {
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
        every { todoService.createTodo(userId, request) } returns todo

        mockMvc.perform(
            post("/api/todos")
                .header("userId", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Todo created successfully"))
            .andExpect(jsonPath("$.data.id").value("1"))
            .andExpect(jsonPath("$.data.userId").value(userId))
            .andExpect(jsonPath("$.data.title").value("Test Todo"))
            .andExpect(jsonPath("$.data.description").value("Test Desc"))
            .andExpect(jsonPath("$.data.completed").value(false))
    }

    @Test
    fun `PUT api todos should update todo`() {
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
        every { todoService.updateTodo(userId, todoId, request) } returns updatedTodo

        mockMvc.perform(
            put("/api/todos")
                .header("userId", userId)
                .param("todoId", todoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Todo updated successfully"))
            .andExpect(jsonPath("$.data.id").value(todoId))
            .andExpect(jsonPath("$.data.title").value("Updated"))
            .andExpect(jsonPath("$.data.description").value("Updated Desc"))
            .andExpect(jsonPath("$.data.completed").value(true))
    }

    @Test
    fun `PUT api todos should return 404 if not found`() {
        val userId = "user1"
        val todoId = "1"
        val request = TodoUpdateRequest(title = "Updated")
        every { todoService.updateTodo(userId, todoId, request) } returns null

        mockMvc.perform(
            put("/api/todos")
                .header("userId", userId)
                .param("todoId", todoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Todo not found or not owned by user"))
            .andExpect(jsonPath("$.data").doesNotExist())
    }

    @Test
    fun `DELETE api todos should delete todo`() {
        val userId = "user1"
        val todoId = "1"
        every { todoService.deleteTodo(userId, todoId) } returns true

        mockMvc.perform(
            delete("/api/todos")
                .header("userId", userId)
                .param("todoId", todoId)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Todo deleted successfully"))
            .andExpect(jsonPath("$.data").doesNotExist())
    }

    @Test
    fun `DELETE api todos should return 404 if not found`() {
        val userId = "user1"
        val todoId = "1"
        every { todoService.deleteTodo(userId, todoId) } returns false

        mockMvc.perform(
            delete("/api/todos")
                .header("userId", userId)
                .param("todoId", todoId)
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("Todo not found or not owned by user"))
            .andExpect(jsonPath("$.data").doesNotExist())
    }

    @Test
    fun `GET api todos should return todos for user`() {
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
        every { todoService.getTodosByUserId(userId) } returns todos

        mockMvc.perform(
            get("/api/todos")
                .header("userId", userId)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("Todos fetched successfully"))
            .andExpect(jsonPath("$.data[0].id").value("1"))
            .andExpect(jsonPath("$.data[0].userId").value(userId))
            .andExpect(jsonPath("$.data[1].id").value("2"))
            .andExpect(jsonPath("$.data[1].userId").value(userId))
    }
} 