package com.itpm.demoapi.demo.model

data class QuoteResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null
) 