package com.itpm.demoapi.demo.repository

import com.itpm.demoapi.demo.model.Quote
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface QuoteRepository : MongoRepository<Quote, String> 