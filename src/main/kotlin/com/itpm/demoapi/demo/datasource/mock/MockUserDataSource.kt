package com.itpm.demoapi.demo.datasource.mock

import com.itpm.demoapi.demo.datasource.UserDataSource
import com.itpm.demoapi.demo.model.User
import org.springframework.stereotype.Repository

@Repository
class MockUserDataSource : UserDataSource {
    
    private val users = listOf(
        User(
            userid = "admin",
            password = "admin123",
            name = "Administrator",
            email = "admin@example.com",
            isActive = true
        ),
        User(
            userid = "john_doe",
            password = "password123",
            name = "John Doe",
            email = "john.doe@example.com",
            isActive = true
        ),
        User(
            userid = "jane_smith",
            password = "secure456",
            name = "Jane Smith",
            email = "jane.smith@example.com",
            isActive = true
        ),
        User(
            userid = "inactive_user",
            password = "test123",
            name = "Inactive User",
            email = "inactive@example.com",
            isActive = false
        )
    )

    override fun getUserByUserid(userid: String): User? {
        return users.find { it.userid == userid }
    }

    override fun validateUser(userid: String, password: String): User? {
        return users.find { 
            it.userid == userid && 
            it.password == password && 
            it.isActive 
        }
    }

    override fun getAllUsers(): List<User> {
        return users
    }
} 