package com.example.springwebbug

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringWebBugApplication

fun main(args: Array<String>) {
	runApplication<SpringWebBugApplication>(*args)
}
