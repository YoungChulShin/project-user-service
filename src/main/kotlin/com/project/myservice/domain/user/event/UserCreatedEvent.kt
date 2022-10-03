package com.project.myservice.domain.user.event

import com.project.myservice.domain.user.UserInfo

data class UserCreatedEvent(
    val userInfo: UserInfo,
)