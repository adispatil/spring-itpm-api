package com.itpm.demoapi.demo.datasource

import com.itpm.demoapi.demo.model.User

interface UserDataSource {
    fun getUserByUserid(userid: String): User?
    fun validateUser(userid: String, password: String): User?
    fun getAllUsers(): List<User>
} 