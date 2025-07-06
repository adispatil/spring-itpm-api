package com.itpm.demoapi.demo.controller

import com.itpm.demoapi.demo.model.Todo
import com.itpm.demoapi.demo.model.TodoRequest
import com.itpm.demoapi.demo.model.TodoUpdateRequest
import com.itpm.demoapi.demo.model.TodoResponse
import com.itpm.demoapi.demo.service.TodoService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/todos")
class TodoController(private val todoService: TodoService) {

    @PostMapping
    fun createTodo(
        @RequestHeader("userId") userId: String,
        @RequestBody request: TodoRequest
    ): ResponseEntity<TodoResponse<Todo>> {
        val todo = todoService.createTodo(userId, request)
        return ResponseEntity(
            TodoResponse(success = true, message = "Todo created successfully", data = todo),
            HttpStatus.CREATED
        )
    }

    @PutMapping
    fun updateTodo(
        @RequestHeader("userId") userId: String,
        @RequestParam todoId: String,
        @RequestBody request: TodoUpdateRequest
    ): ResponseEntity<TodoResponse<Todo>> {
        val updated = todoService.updateTodo(userId, todoId, request)
        return if (updated != null)
            ResponseEntity(TodoResponse(true, "Todo updated successfully", updated), HttpStatus.OK)
        else
            ResponseEntity(TodoResponse(false, "Todo not found or not owned by user", null), HttpStatus.NOT_FOUND)
    }

    @DeleteMapping
    fun deleteTodo(
        @RequestHeader("userId") userId: String,
        @RequestParam todoId: String
    ): ResponseEntity<TodoResponse<Void>> {
        val deleted = todoService.deleteTodo(userId, todoId)
        return if (deleted)
            ResponseEntity(TodoResponse(true, "Todo deleted successfully", null), HttpStatus.OK)
        else
            ResponseEntity(TodoResponse(false, "Todo not found or not owned by user", null), HttpStatus.NOT_FOUND)
    }

    @GetMapping
    fun getTodosByUserId(@RequestHeader("userId") userId: String): ResponseEntity<TodoResponse<List<Todo>>> {
        val todos = todoService.getTodosByUserId(userId)
        return ResponseEntity(
            TodoResponse(success = true, message = "Todos fetched successfully", data = todos),
            HttpStatus.OK
        )
    }
} 