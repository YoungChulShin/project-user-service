package com.project.myservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class MyserviceApplication

fun main(args: Array<String>) {
    runApplication<MyserviceApplication>(*args)
}
