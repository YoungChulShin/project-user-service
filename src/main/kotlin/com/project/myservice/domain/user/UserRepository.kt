package com.project.myservice.domain.user

interface UserRepository {

    fun save(user: User): User

    fun findByUsername(username: String): User?

    fun findByEmail(email: String): User?

    fun findByPhoneNumber(phoneNumber: String): User?

    fun findDetail(username: String): UserDetailInfo?
}