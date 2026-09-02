package com.example.ogym.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class OgymBackendApplication

fun main(args: Array<String>) {
    runApplication<OgymBackendApplication>(*args)
}