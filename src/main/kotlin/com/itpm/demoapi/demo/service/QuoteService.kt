package com.itpm.demoapi.demo.service

import com.itpm.demoapi.demo.model.Quote
import com.itpm.demoapi.demo.repository.QuoteRepository
import org.springframework.stereotype.Service
import kotlin.random.Random

@Service
class QuoteService(private val quoteRepository: QuoteRepository) {
    fun getRandomQuote(): Quote? {
        val count = quoteRepository.count()
        if (count == 0L) return null
        val allIds = quoteRepository.findAll().mapNotNull { it.id }
        if (allIds.isEmpty()) return null
        val randomId = allIds[Random.nextInt(allIds.size)]
        return quoteRepository.findById(randomId).orElse(null)
    }
} 