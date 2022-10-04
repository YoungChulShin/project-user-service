package com.project.myservice.infrastructure.user

import com.project.myservice.domain.user.User
import com.project.myservice.domain.user.UserRepository
import org.springframework.stereotype.Repository

@Repository
class DefaultUserRepository(
    val jpaUserRepository: JpaUserRepository,
) : UserRepository {

    override fun save(user: User) = jpaUserRepository.save(user)

    override fun findByUsername(username: String) = jpaUserRepository.findByUsername(username)

    override fun findByEmail(email: String) = jpaUserRepository.findByEmail(email)

    override fun findByPhoneNumber(phoneNumber: String) =
        jpaUserRepository.findByPhoneNumber(phoneNumber)
}