package com.project.myservice.infrastructure.user.notification

import com.project.myservice.domain.user.UserInfo
import com.project.myservice.domain.user.notification.UserNotificationMessage
import com.project.myservice.domain.user.notification.UserNotificationType
import com.project.myservice.domain.user.notification.UserNotifier
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class FcmUserNotifier : UserNotifier {

    companion object {
        private val logger = LoggerFactory.getLogger(FcmUserNotifier::class.java)
    }

    override fun notify(
        userInfo: UserInfo,
        type: UserNotificationType,
        message: UserNotificationMessage
    ) {
        when (type) {
            UserNotificationType.RESET_PASSWORD
                -> logger.info("'${userInfo.username}' 사용자에게 비밀번호 초기화 푸쉬를 전달합니다")
        }
    }
}