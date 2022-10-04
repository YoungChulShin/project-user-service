package com.project.myservice.infrastructure.notification

import com.project.myservice.domain.user.UserInfo
import com.project.myservice.domain.user.notification.UserNotificationMessage
import com.project.myservice.domain.user.notification.UserNotificationType
import com.project.myservice.domain.user.notification.UserNotifier

class LocalUserNotifier : UserNotifier {

    val data = mutableListOf<Triple<UserInfo, UserNotificationType, UserNotificationMessage>>()

    override fun notify(
        userInfo: UserInfo,
        type: UserNotificationType,
        message: UserNotificationMessage
    ) {
        data.add(Triple(userInfo, type, message))
    }
}