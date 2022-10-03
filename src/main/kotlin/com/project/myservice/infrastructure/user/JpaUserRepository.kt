package com.project.myservice.infrastructure.user

import com.project.myservice.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository

interface JpaUserRepository : JpaRepository<User, Long> {

    fun findByPhoneNumber(phoneNumber: String): User?
}