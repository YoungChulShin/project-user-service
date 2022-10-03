package com.project.myservice.infrastructure.common

import com.project.myservice.domain.common.NotificationSender

class LocalNotificationSender : NotificationSender {

    val data = mutableListOf<Pair<String, String>>()

    override fun send(target: String, message: String) {
        data.add(Pair(target, message))
    }
}