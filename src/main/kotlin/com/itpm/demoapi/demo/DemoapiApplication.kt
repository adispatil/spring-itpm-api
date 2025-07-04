package com.itpm.demoapi.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class DemoapiApplication

fun main(args: Array<String>) {
	runApplication<DemoapiApplication>(*args)
}

@RestController
@RequestMapping("api/hello")
class HelloWorldController {

    @GetMapping
    fun helloWorld(): String = "Hello, this is a REST endpoint!"
}