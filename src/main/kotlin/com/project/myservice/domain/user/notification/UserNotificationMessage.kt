package com.project.myservice.domain.user.notification

interface UserNotificationMessage

data class UserPasswordResetMessage(
    val newPassword: String
) : UserNotificationMessage