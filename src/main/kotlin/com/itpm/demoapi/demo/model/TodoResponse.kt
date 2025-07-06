package com.itpm.demoapi.demo.model

data class TodoResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null
) 