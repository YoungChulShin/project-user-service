package com.project.myservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MyserviceApplication

fun main(args: Array<String>) {
    runApplication<MyserviceApplication>(*args)
}
