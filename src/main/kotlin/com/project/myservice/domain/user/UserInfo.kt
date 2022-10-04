package com.project.myservice.domain.user

import com.project.myservice.common.exception.InvalidParameterException
import java.time.Instant

data class UserInfo(
    val id: Long,
    val username: String,
    val email: String,
    val phoneNumber: String,
    val name: String,
    val nickname: String,
    val createdAt: Instant,
    val updatedAt: Instant?,
    val deletedAt: Instant?,
) {
    companion object {
        fun of(user: User): UserInfo {
            return UserInfo(
                id = user.id ?: throw InvalidParameterException("id는 null일 수 없습니다"),
                username = user.username,
                email = user.email,
                phoneNumber = user.phoneNumber,
                name = user.name,
                nickname = user.nickname,
                createdAt = user.createdAt ?: throw InvalidParameterException("생성시간은 null일 수 없습니다"),
                updatedAt = user.updatedAt,
                deletedAt = user.deletedAt
            )
        }
    }
}