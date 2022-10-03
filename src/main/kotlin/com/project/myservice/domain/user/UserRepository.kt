package com.project.myservice.domain.user

interface UserRepository {

    fun findByPhoneNumber(phoneNumber: String): User?
}