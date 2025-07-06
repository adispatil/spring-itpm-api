package com.itpm.demoapi.demo.service

import com.itpm.demoapi.demo.datasource.UserDataSource
import com.itpm.demoapi.demo.datasource.mongo.MongoUserDataSource
import com.itpm.demoapi.demo.model.User
import com.itpm.demoapi.demo.model.UserValidationRequest
import com.itpm.demoapi.demo.model.UserRegistrationRequest
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UserServiceTest {

    private lateinit var userDataSource: UserDataSource
    private lateinit var mongoUserDataSource: MongoUserDataSource
    private lateinit var userService: UserService

    @BeforeEach
    fun setUp() {
        userDataSource = mockk()
        mongoUserDataSource = mockk()
        userService = UserService(userDataSource, mongoUserDataSource)
    }

    @Test
    fun `validateUser should return success for valid credentials`() {
        // Given
        val request = UserValidationRequest("admin", "admin123")
        val mockUser = User(userid = "admin", password = "admin123", name = "Administrator", email = "admin@example.com", isActive = true)
        
        every { userDataSource.validateUser("admin", "admin123") } returns mockUser

        // When
        val result = userService.validateUser(request)

        // Then
        assertTrue(result.isValid)
        assertEquals("User validation successful", result.message)
        assertNotNull(result.user)
        assertEquals("admin", result.user?.userid)
        assertEquals("***", result.user?.password) // Password should be masked
    }

    @Test
    fun `validateUser should return failure for invalid credentials`() {
        // Given
        val request = UserValidationRequest("admin", "wrongpassword")
        
        every { userDataSource.validateUser("admin", "wrongpassword") } returns null

        // When
        val result = userService.validateUser(request)

        // Then
        assertFalse(result.isValid)
        assertEquals("Invalid userid or password", result.message)
        assertNull(result.user)
    }

    @Test
    fun `validateUser should return failure for inactive user`() {
        // Given
        val request = UserValidationRequest("inactive_user", "test123")
        
        every { userDataSource.validateUser("inactive_user", "test123") } returns null

        // When
        val result = userService.validateUser(request)

        // Then
        assertFalse(result.isValid)
        assertEquals("Invalid userid or password", result.message)
        assertNull(result.user)
    }

    @Test
    fun `getUserByUserid should return user with masked password`() {
        // Given
        val mockUser = User(userid = "john_doe", password = "password123", name = "John Doe", email = "john.doe@example.com", isActive = true)
        
        every { userDataSource.getUserByUserid("john_doe") } returns mockUser

        // When
        val result = userService.getUserByUserid("john_doe")

        // Then
        assertNotNull(result)
        assertEquals("john_doe", result?.userid)
        assertEquals("***", result?.password) // Password should be masked
    }

    @Test
    fun `getAllUsers should return users with masked passwords`() {
        // Given
        val mockUsers = listOf(
            User(userid = "admin", password = "admin123", name = "Administrator", email = "admin@example.com", isActive = true),
            User(userid = "john_doe", password = "password123", name = "John Doe", email = "john.doe@example.com", isActive = true)
        )
        
        every { userDataSource.getAllUsers() } returns mockUsers

        // When
        val result = userService.getAllUsers()

        // Then
        assertEquals(2, result.size)
        result.forEach { user ->
            assertEquals("***", user.password) // All passwords should be masked
        }
    }

    @Test
    fun `registerUser should return success for new user`() {
        // Given
        val request = UserRegistrationRequest(
            userid = "newuser",
            password = "password123",
            name = "New User",
            email = "newuser@example.com"
        )
        val savedUser = User(
            id = "123",
            userid = "newuser",
            password = "password123",
            name = "New User",
            email = "newuser@example.com",
            isActive = true
        )
        
        every { mongoUserDataSource.existsByUserid("newuser") } returns false
        every { mongoUserDataSource.existsByEmail("newuser@example.com") } returns false
        every { mongoUserDataSource.createUser(any()) } returns savedUser

        // When
        val result = userService.registerUser(request)

        // Then
        assertTrue(result.success)
        assertEquals("User registered successfully", result.message)
        assertNotNull(result.user)
        assertEquals("newuser", result.user?.userid)
        assertEquals("New User", result.user?.name)
        assertEquals("newuser@example.com", result.user?.email)
        assertEquals("***", result.user?.password) // Password should be masked
        assertTrue(result.user?.isActive == true)
    }

    @Test
    fun `registerUser should return failure when userid already exists`() {
        // Given
        val request = UserRegistrationRequest(
            userid = "existinguser",
            password = "password123",
            name = "Existing User",
            email = "existinguser@example.com"
        )
        
        every { mongoUserDataSource.existsByUserid("existinguser") } returns true

        // When
        val result = userService.registerUser(request)

        // Then
        assertFalse(result.success)
        assertEquals("User with userid 'existinguser' already exists", result.message)
        assertNull(result.user)
    }

    @Test
    fun `registerUser should return failure when email already exists`() {
        // Given
        val request = UserRegistrationRequest(
            userid = "newuser",
            password = "password123",
            name = "New User",
            email = "existing@example.com"
        )
        
        every { mongoUserDataSource.existsByUserid("newuser") } returns false
        every { mongoUserDataSource.existsByEmail("existing@example.com") } returns true

        // When
        val result = userService.registerUser(request)

        // Then
        assertFalse(result.success)
        assertEquals("User with email 'existing@example.com' already exists", result.message)
        assertNull(result.user)
    }
} 