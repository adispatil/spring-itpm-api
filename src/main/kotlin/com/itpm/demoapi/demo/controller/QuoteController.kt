package com.itpm.demoapi.demo.controller

import com.itpm.demoapi.demo.model.Quote
import com.itpm.demoapi.demo.model.QuoteResponse
import com.itpm.demoapi.demo.model.QuoteWithDate
import com.itpm.demoapi.demo.service.QuoteService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/api/quotes")
class QuoteController(private val quoteService: QuoteService) {
    @GetMapping("/random")
    fun getRandomQuote(): ResponseEntity<QuoteResponse<QuoteWithDate>> {
        val quote = quoteService.getRandomQuote()
        val today = LocalDate.now()
        return if (quote != null) {
            val quoteWithDate = QuoteWithDate(
                id = quote.id,
                text = quote.text,
                date = today
            )
            ResponseEntity(QuoteResponse(true, "Random quote fetched successfully", quoteWithDate), HttpStatus.OK)
        } else {
            ResponseEntity(QuoteResponse(false, "No quotes available", null), HttpStatus.NOT_FOUND)
        }
    }
} 