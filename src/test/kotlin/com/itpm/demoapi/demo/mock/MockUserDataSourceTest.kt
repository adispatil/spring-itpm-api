package com.itpm.demoapi.demo.mock

import com.itpm.demoapi.demo.datasource.mock.MockUserDataSource
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MockUserDataSourceTest {

    private lateinit var mockUserDataSource: MockUserDataSource

    @BeforeEach
    fun setUp() {
        mockUserDataSource = MockUserDataSource()
    }

    @Test
    fun `validateUser should return user for valid credentials`() {
        // When
        val result = mockUserDataSource.validateUser("admin", "admin123")

        // Then
        assertNotNull(result)
        assertEquals("admin", result?.userid)
        assertEquals("admin123", result?.password)
        assertEquals("Administrator", result?.name)
        assertTrue(result?.isActive == true)
    }

    @Test
    fun `validateUser should return null for invalid password`() {
        // When
        val result = mockUserDataSource.validateUser("admin", "wrongpassword")

        // Then
        assertNull(result)
    }

    @Test
    fun `validateUser should return null for non-existent userid`() {
        // When
        val result = mockUserDataSource.validateUser("nonexistent", "password")

        // Then
        assertNull(result)
    }

    @Test
    fun `validateUser should return null for inactive user`() {
        // When
        val result = mockUserDataSource.validateUser("inactive_user", "test123")

        // Then
        assertNull(result)
    }

    @Test
    fun `getUserByUserid should return user for existing userid`() {
        // When
        val result = mockUserDataSource.getUserByUserid("john_doe")

        // Then
        assertNotNull(result)
        assertEquals("john_doe", result?.userid)
        assertEquals("password123", result?.password)
        assertEquals("John Doe", result?.name)
    }

    @Test
    fun `getUserByUserid should return null for non-existent userid`() {
        // When
        val result = mockUserDataSource.getUserByUserid("nonexistent")

        // Then
        assertNull(result)
    }

    @Test
    fun `getAllUsers should return all users including inactive ones`() {
        // When
        val result = mockUserDataSource.getAllUsers()

        // Then
        assertEquals(4, result.size)
        
        val userids = result.map { it.userid }
        assertTrue(userids.contains("admin"))
        assertTrue(userids.contains("john_doe"))
        assertTrue(userids.contains("jane_smith"))
        assertTrue(userids.contains("inactive_user"))
        
        // Check that inactive user is included in getAllUsers
        val inactiveUser = result.find { it.userid == "inactive_user" }
        assertNotNull(inactiveUser)
        assertFalse(inactiveUser?.isActive == true)
    }
} 