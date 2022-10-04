package com.project.myservice.domain.user.notification

import com.project.myservice.domain.user.UserInfo

interface UserNotifier {

    fun notify(
        userInfo: UserInfo,
        type: UserNotificationType,
        message: UserNotificationMessage,
    )
}