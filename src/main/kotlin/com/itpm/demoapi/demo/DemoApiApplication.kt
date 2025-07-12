package com.itpm.demoapi.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class DemoApiApplication

fun main(args: Array<String>) {
	runApplication<DemoApiApplication>(*args)
}