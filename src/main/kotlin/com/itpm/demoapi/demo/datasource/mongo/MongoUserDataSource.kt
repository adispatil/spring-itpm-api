package com.itpm.demoapi.demo.datasource.mongo

import com.itpm.demoapi.demo.datasource.UserDataSource
import com.itpm.demoapi.demo.model.User
import com.itpm.demoapi.demo.repository.UserRepository
import org.springframework.stereotype.Repository

@Repository
class MongoUserDataSource(private val userRepository: UserRepository) : UserDataSource {

    override fun getUserByUserid(userid: String): User? {
        return userRepository.findByUserid(userid)
    }

    override fun validateUser(userid: String, password: String): User? {
        val user = userRepository.findByUserid(userid)
        return if (user != null && user.password == password && user.isActive) {
            user
        } else {
            null
        }
    }

    override fun getAllUsers(): List<User> {
        return userRepository.findAll()
    }

    fun createUser(user: User): User {
        return userRepository.save(user)
    }

    fun existsByUserid(userid: String): Boolean {
        return userRepository.existsByUserid(userid)
    }

    fun existsByEmail(email: String): Boolean {
        return userRepository.existsByEmail(email)
    }
} 