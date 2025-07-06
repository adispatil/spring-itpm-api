package com.itpm.demoapi.demo.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.index.Indexed

@Document(collection = "users")
data class User(
    @Id
    val id: String? = null,
    @Indexed(unique = true)
    val userid: String,
    val password: String,
    val name: String,
    @Indexed(unique = true)
    val email: String,
    val isActive: Boolean = true
)

data class UserValidationRequest(
    val userid: String,
    val password: String
)

data class UserValidationResponse(
    val isValid: Boolean,
    val message: String,
    val user: User? = null
)

data class UserRegistrationRequest(
    val userid: String,
    val password: String,
    val name: String,
    val email: String
)

data class UserRegistrationResponse(
    val success: Boolean,
    val message: String,
    val user: User? = null
) 