package com.project.myservice.domain.user

import java.time.Instant

data class UserDetailInfo(
    val id: Long,
    val username: String,
    val email: String,
    val phoneNumber: String,
    val name: String,
    val nickname: String,
    val roles: List<String>,
    val createdAt: Instant,
    val updatedAt: Instant?,
    val deletedAt: Instant?,
)