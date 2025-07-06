package com.itpm.demoapi.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DemoApiApplication

fun main(args: Array<String>) {
	runApplication<DemoApiApplication>(*args)
}