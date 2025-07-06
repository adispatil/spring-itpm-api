package com.itpm.demoapi.demo.model

import java.time.LocalDate

data class QuoteWithDate(
    val id: String?,
    val text: String,
    val date: LocalDate
) 