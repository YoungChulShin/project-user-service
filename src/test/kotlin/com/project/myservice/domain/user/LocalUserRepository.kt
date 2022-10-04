package com.project.myservice.domain.user

import java.time.Instant
import java.util.concurrent.atomic.AtomicLong

class LocalUserRepository: UserRepository {

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
}