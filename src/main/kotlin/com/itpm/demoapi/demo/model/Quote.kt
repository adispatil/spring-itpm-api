package com.itpm.demoapi.demo.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "quotes")
data class Quote(
    @Id
    val id: String? = null,
    val text: String
) 