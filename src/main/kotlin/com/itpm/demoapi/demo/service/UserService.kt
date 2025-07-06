package com.itpm.demoapi.demo.service

import com.itpm.demoapi.demo.datasource.UserDataSource
import com.itpm.demoapi.demo.datasource.mongo.MongoUserDataSource
import com.itpm.demoapi.demo.model.User
import com.itpm.demoapi.demo.model.UserValidationRequest
import com.itpm.demoapi.demo.model.UserValidationResponse
import com.itpm.demoapi.demo.model.UserRegistrationRequest
import com.itpm.demoapi.demo.model.UserRegistrationResponse
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userDataSource: UserDataSource,
    private val mongoUserDataSource: MongoUserDataSource
) {

    fun validateUser(request: UserValidationRequest): UserValidationResponse {
        val user = userDataSource.validateUser(request.userid, request.password)
        
        return if (user != null) {
            UserValidationResponse(
                isValid = true,
                message = "User validation successful",
                user = user.copy(password = "***") // Don't return password in response
            )
        } else {
            UserValidationResponse(
                isValid = false,
                message = "Invalid userid or password"
            )
        }
    }

    fun getUserByUserid(userid: String): User? {
        val user = userDataSource.getUserByUserid(userid)
        return user?.copy(password = "***") // Don't return password
    }

    fun getAllUsers(): List<User> {
        return userDataSource.getAllUsers().map { it.copy(password = "***") }
    }

    fun registerUser(request: UserRegistrationRequest): UserRegistrationResponse {
        // Check if userid already exists
        if (mongoUserDataSource.existsByUserid(request.userid)) {
            return UserRegistrationResponse(
                success = false,
                message = "User with userid '${request.userid}' already exists"
            )
        }

        // Check if email already exists
        if (mongoUserDataSource.existsByEmail(request.email)) {
            return UserRegistrationResponse(
                success = false,
                message = "User with email '${request.email}' already exists"
            )
        }

        // Create new user
        val newUser = User(
            userid = request.userid,
            password = request.password,
            name = request.name,
            email = request.email,
            isActive = true
        )

        val savedUser = mongoUserDataSource.createUser(newUser)
        
        return UserRegistrationResponse(
            success = true,
            message = "User registered successfully",
            user = savedUser.copy(password = "***") // Don't return password in response
        )
    }
} 