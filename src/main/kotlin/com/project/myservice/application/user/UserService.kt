package com.project.myservice.application.user

import com.project.myservice.common.exception.UserAlreadyExistsException
import com.project.myservice.domain.user.User
import com.project.myservice.domain.user.UserInfo
import com.project.myservice.domain.user.UserRepository
import com.project.myservice.domain.user.authentication.UserAuthenticationManager
import com.project.myservice.domain.user.authentication.UserAuthenticationType
import com.project.myservice.domain.user.event.UserCreatedEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    val userAuthenticationManager: UserAuthenticationManager,
    val userRepository: UserRepository,
    val applicationEventPublisher: ApplicationEventPublisher,
) {

    @Transactional
    fun createUser(command: CreateUserCommand): UserInfo {
        if (userRepository.findByUsername(command.username) != null) {
            throw UserAlreadyExistsException("동일한 아이디를 사용하는 회원정보가 존재합니다")
        }
        if (userRepository.findByEmail(command.email) != null) {
            throw UserAlreadyExistsException("동일한 이메일을 사용하는 회원정보가 존재합니다")
        }
        if (userRepository.findByPhoneNumber(command.phoneNumber) != null) {
            throw UserAlreadyExistsException("동일한 연락처를 사용하는 회원정보가 존재합니다")
        }

        userAuthenticationManager.checkAuthentication(
            UserAuthenticationType.CREATE_USER,
            command.phoneNumber,
            command.authenticationNumber
        )

        val initUser = User(
            command.username,
            command.email,
            command.phoneNumber,
            command.password,
            command.name,
            command.nickname,
        )

        return userRepository.save(initUser)
            .run { UserInfo.of(this) }
            .also { applicationEventPublisher.publishEvent(UserCreatedEvent(it)) }
    }
}