package com.itpm.demoapi.demo.repository

import com.itpm.demoapi.demo.model.User
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : MongoRepository<User, String> {
    fun findByUserid(userid: String): User?
    fun findByEmail(email: String): User?
    fun existsByUserid(userid: String): Boolean
    fun existsByEmail(email: String): Boolean
} 