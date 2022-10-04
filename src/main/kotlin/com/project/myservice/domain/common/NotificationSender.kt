package com.project.myservice.domain.common

interface NotificationSender {

    fun send(target: String, message: String)
}