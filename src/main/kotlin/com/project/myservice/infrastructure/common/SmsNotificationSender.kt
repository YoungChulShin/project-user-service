package com.project.myservice.infrastructure.common

import com.project.myservice.domain.common.NotificationSender
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class SmsNotificationSender : NotificationSender {

    companion object {
        private val logger = LoggerFactory.getLogger(SmsNotificationSender::class.java)
    }

    override fun send(target: String, message: String) {
        logger.info("sms 알람을 전달합니다. target='$target', message='$message'")
    }
}