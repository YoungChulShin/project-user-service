package com.project.myservice.domain.user

class LocalUserRepository(
    val hasUser: Boolean,
    val user: User?
) : UserRepository {

    override fun findByPhoneNumber(phoneNumber: String): User? {
        return if (hasUser) return user else null
    }
}