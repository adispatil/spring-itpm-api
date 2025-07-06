package com.itpm.demoapi.demo.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.itpm.demoapi.demo.model.User
import com.itpm.demoapi.demo.model.UserValidationRequest
import com.itpm.demoapi.demo.model.UserValidationResponse
import com.itpm.demoapi.demo.model.UserRegistrationRequest
import com.itpm.demoapi.demo.model.UserRegistrationResponse
import com.itpm.demoapi.demo.service.UserService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class UserControllerTest {

    private lateinit var mockMvc: MockMvc
    private lateinit var userService: UserService
    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setUp() {
        userService = mockk()
        objectMapper = ObjectMapper()
        mockMvc = MockMvcBuilders.standaloneSetup(UserController(userService)).build()
    }

    @Test
    fun `POST api users register should return success for valid registration`() {
        // Given
        val request = UserRegistrationRequest(
            userid = "newuser",
            password = "password123",
            name = "New User",
            email = "newuser@example.com"
        )
        val response = UserRegistrationResponse(
            success = true,
            message = "User registered successfully",
            user = User(
                id = "123",
                userid = "newuser",
                password = "***",
                name = "New User",
                email = "newuser@example.com",
                isActive = true
            )
        )
        
        every { userService.registerUser(request) } returns response

        // When & Then
        mockMvc.perform(
            post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("User registered successfully"))
            .andExpect(jsonPath("$.user.userid").value("newuser"))
            .andExpect(jsonPath("$.user.name").value("New User"))
            .andExpect(jsonPath("$.user.email").value("newuser@example.com"))
            .andExpect(jsonPath("$.user.password").value("***"))
    }

    @Test
    fun `POST api users register should return bad request for duplicate userid`() {
        // Given
        val request = UserRegistrationRequest(
            userid = "existinguser",
            password = "password123",
            name = "Existing User",
            email = "existinguser@example.com"
        )
        val response = UserRegistrationResponse(
            success = false,
            message = "User with userid 'existinguser' already exists"
        )
        
        every { userService.registerUser(request) } returns response

        // When & Then
        mockMvc.perform(
            post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("User with userid 'existinguser' already exists"))
    }

    @Test
    fun `POST api users validate should return success for valid credentials`() {
        // Given
        val request = UserValidationRequest("admin", "admin123")
        val response = UserValidationResponse(
            isValid = true,
            message = "User validation successful",
            user = User(userid = "admin", password = "***", name = "Administrator", email = "admin@example.com", isActive = true)
        )
        
        every { userService.validateUser(request) } returns response

        // When & Then
        mockMvc.perform(
            post("/api/users/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.isValid").value(true))
            .andExpect(jsonPath("$.message").value("User validation successful"))
            .andExpect(jsonPath("$.user.userid").value("admin"))
            .andExpect(jsonPath("$.user.password").value("***"))
    }

    @Test
    fun `POST api users validate should return unauthorized for invalid credentials`() {
        // Given
        val request = UserValidationRequest("admin", "wrongpassword")
        val response = UserValidationResponse(
            isValid = false,
            message = "Invalid userid or password"
        )
        
        every { userService.validateUser(request) } returns response

        // When & Then
        mockMvc.perform(
            post("/api/users/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.isValid").value(false))
            .andExpect(jsonPath("$.message").value("Invalid userid or password"))
    }

    @Test
    fun `GET api users should return all users`() {
        // Given
        val users = listOf(
            User(userid = "admin", password = "***", name = "Administrator", email = "admin@example.com", isActive = true),
            User(userid = "john_doe", password = "***", name = "John Doe", email = "john.doe@example.com", isActive = true)
        )
        
        every { userService.getAllUsers() } returns users

        // When & Then
        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].userid").value("admin"))
            .andExpect(jsonPath("$[0].password").value("***"))
            .andExpect(jsonPath("$[1].userid").value("john_doe"))
            .andExpect(jsonPath("$[1].password").value("***"))
    }

    @Test
    fun `GET api users userid should return user when found`() {
        // Given
        val user = User(userid = "admin", password = "***", name = "Administrator", email = "admin@example.com", isActive = true)
        
        every { userService.getUserByUserid("admin") } returns user

        // When & Then
        mockMvc.perform(get("/api/users/admin"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.userid").value("admin"))
            .andExpect(jsonPath("$.password").value("***"))
    }

    @Test
    fun `GET api users userid should return 404 when user not found`() {
        // Given
        every { userService.getUserByUserid("nonexistent") } returns null

        // When & Then
        mockMvc.perform(get("/api/users/nonexistent"))
            .andExpect(status().isNotFound)
    }
} 