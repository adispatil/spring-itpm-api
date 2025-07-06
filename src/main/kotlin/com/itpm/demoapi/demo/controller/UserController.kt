package com.itpm.demoapi.demo.controller
import com.itpm.demoapi.demo.model.User
import com.itpm.demoapi.demo.model.UserValidationRequest
import com.itpm.demoapi.demo.model.UserValidationResponse
import com.itpm.demoapi.demo.model.UserRegistrationRequest
import com.itpm.demoapi.demo.model.UserRegistrationResponse
import com.itpm.demoapi.demo.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {

    @PostMapping("/register")
    fun registerUser(@RequestBody request: UserRegistrationRequest): ResponseEntity<UserRegistrationResponse> {
        val response = userService.registerUser(request)
        val status = if (response.success) HttpStatus.CREATED else HttpStatus.BAD_REQUEST
        return ResponseEntity(response, status)
    }

    @PostMapping("/validate")
    fun validateUser(@RequestBody request: UserValidationRequest): ResponseEntity<UserValidationResponse> {
        val response = userService.validateUser(request)
        val status = if (response.isValid) HttpStatus.OK else HttpStatus.UNAUTHORIZED
        return ResponseEntity(response, status)
    }

    @GetMapping
    fun getAllUsers(): ResponseEntity<List<User>> {
        val users = userService.getAllUsers()
        return ResponseEntity(users, HttpStatus.OK)
    }

    @GetMapping("/{userid}")
    fun getUserByUserid(@PathVariable userid: String): ResponseEntity<User> {
        val user = userService.getUserByUserid(userid)
        return if (user != null) {
            ResponseEntity(user, HttpStatus.OK)
        } else {
            ResponseEntity(HttpStatus.NOT_FOUND)
        }
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<String> {
        return ResponseEntity("An error occurred: ${e.message}", HttpStatus.INTERNAL_SERVER_ERROR)
    }
} 