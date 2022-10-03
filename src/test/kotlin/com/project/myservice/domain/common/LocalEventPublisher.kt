package com.project.myservice.domain.common

import org.springframework.context.ApplicationEventPublisher

class LocalEventPublisher : ApplicationEventPublisher {

    val data = mutableListOf<Any>()

    override fun publishEvent(event: Any) {
        data.add(event)
    }
}