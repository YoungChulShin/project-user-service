package com.project.myservice.domain.user.event

import com.project.myservice.domain.user.UserInfo

data class UserPasswordResetEvent(
    val userInfo: UserInfo,
    val newPassword: String,
)