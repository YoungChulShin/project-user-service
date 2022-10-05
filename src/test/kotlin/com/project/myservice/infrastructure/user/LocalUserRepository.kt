package com.project.myservice.infrastructure.user

import com.project.myservice.domain.user.User
import com.project.myservice.domain.user.UserDetailInfo
import com.project.myservice.domain.user.UserRepository
import java.time.Instant
import java.util.concurrent.atomic.AtomicLong

class LocalUserRepository(
    val localRoleRepository: LocalRoleRepository? = null
): UserRepository {

    val data = mutableListOf<User>()
    var lastId = AtomicLong(1)

    override fun save(user: User): User {
        if (user.id == null) {
            val userClass = User::class.java
            val idField = userClass.getDeclaredField("id")
            idField.isAccessible = true
            idField.set(user, lastId.getAndIncrement())

            val createdAtField = userClass.superclass.getDeclaredField("createdAt")
            createdAtField.isAccessible = true
            createdAtField.set(user, Instant.now())
        }
        data.add(user)
        return user
    }

    override fun findByUsername(username: String): User? {
        val findUsers = data.filter { it.username == username }.toList()
        return if (findUsers.isNotEmpty()) findUsers[0] else null
    }

    override fun findByEmail(email: String): User? {
        val findUsers = data.filter { it.email == email }.toList()
        return if (findUsers.isNotEmpty()) findUsers[0] else null
    }

    override fun findByPhoneNumber(phoneNumber: String): User? {
        val findUsers = data.filter { it.phoneNumber == phoneNumber }.toList()
        return if (findUsers.isNotEmpty()) findUsers[0] else null
    }

    override fun findDetail(username: String): UserDetailInfo? {
        val findUsers = data.filter { it.username == username }.toList()
        if (findUsers.isEmpty()) return null

        val findUser = findUsers[0]
        val userRoles = localRoleRepository?.findByIds(findUser.roleIds)

        return UserDetailInfo(
            findUser.id!!,
            findUser.username,
            findUser.email,
            findUser.phoneNumber,
            findUser.name,
            findUser.nickname,
            userRoles?.map { it.name }?.toList() ?: listOf(),
            findUser.createdAt!!,
            findUser.updatedAt,
            findUser.deletedAt
        )
    }
}