package com.project.myservice.infrastructure.user

import com.project.myservice.domain.user.UserRepository
import org.springframework.stereotype.Repository

@Repository
class DefaultUserRepository(
    val jpaUserRepository: JpaUserRepository,
) : UserRepository {

    override fun findByPhoneNumber(phoneNumber: String) =
        jpaUserRepository.findByPhoneNumber(phoneNumber)
}