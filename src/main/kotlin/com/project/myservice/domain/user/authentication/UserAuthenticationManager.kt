package com.project.myservice.domain.user.authentication

import com.project.myservice.common.exception.AuthenticationFailedException
import com.project.myservice.common.exception.ErrorCode
import com.project.myservice.common.exception.UserAlreadyExistsException
import com.project.myservice.common.exception.UserNotFoundException
import com.project.myservice.domain.common.CacheRepository
import com.project.myservice.domain.common.NotificationSender
import com.project.myservice.domain.user.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class UserAuthenticationManager(
    @Value("\${application.user.authentication.authentication-number-ttl-ms}")
    val authenticationNumberTtl: Long,
    val userRepository: UserRepository,
    val cacheRepository: CacheRepository,
    val notificationSender: NotificationSender,
) {

    /**
     * 전화번호 인증 요청
     */
    fun requestAuthentication(type: UserAuthenticationType, phoneNumber: String): String {
        checkUserOption(type, phoneNumber)

        val authenticationNumber = UserAuthenticationNumberGenerator.generate()
        cacheRepository.save(
            key = generateKey(type, phoneNumber),
            value = authenticationNumber,
            ttl = authenticationNumberTtl
        )
        notificationSender.send(
            target = phoneNumber,
            message = "인증번호 [${authenticationNumber}]를 입력해주세요")

        return authenticationNumber
    }

    fun checkAuthentication(
        type: UserAuthenticationType,
        phoneNumber: String,
        authenticationNumber: String,
    ) {
        val findAuthenticationNumber = cacheRepository.find(generateKey(type, phoneNumber))
            ?: throw AuthenticationFailedException(ErrorCode.AUTHENTICATION_NUMBER_NOT_FOUND)

        if (authenticationNumber != findAuthenticationNumber) {
            throw AuthenticationFailedException(ErrorCode.AUTHENTICATION_NUMBER_MISMATCHED)
        }
    }

    fun clearAuthentication(type: UserAuthenticationType, phoneNumber: String) {
        cacheRepository.delete(generateKey(type, phoneNumber))
    }

    private fun generateKey(type: UserAuthenticationType, phoneNumber: String): String {
        return "api:authentication:${type.name.lowercase()}:${phoneNumber}"
    }

    private fun checkUserOption(type: UserAuthenticationType, phoneNumber: String) {
        when(type.userOption) {
            UserExistsOption.EXISTS -> if (userRepository.findByPhoneNumber(phoneNumber) == null)
                throw UserNotFoundException()
            UserExistsOption.NOT_EXISTS -> if (userRepository.findByPhoneNumber(phoneNumber) != null)
                throw UserAlreadyExistsException()
            else -> return
        }
    }
}