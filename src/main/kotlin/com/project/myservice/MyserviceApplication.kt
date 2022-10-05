package com.project.myservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class MyserviceApplication

fun main(args: Array<String>) {
    runApplication<MyserviceApplication>(*args)
}
