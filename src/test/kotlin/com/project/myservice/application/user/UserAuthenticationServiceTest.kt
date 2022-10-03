package com.project.myservice.application.user

import com.project.myservice.domain.user.authentication.UserAuthenticationManager
import com.project.myservice.domain.user.authentication.UserAuthenticationType
import org.junit.jupiter.api.Test
import org.mockito.Mockito

internal class UserAuthenticationServiceTest {

    @Test
    fun `인증요청을 받으면 AuthenticationManager를 호출한다`() {
        // given
        val userAuthenticationManagerMock = Mockito.mock(UserAuthenticationManager::class.java)
        val sut = UserAuthenticationService(userAuthenticationManagerMock)

        val type = UserAuthenticationType.CREATE_USER
        val phoneNumber = "01011112222"

        // when
        sut.requestAuthentication(type, phoneNumber)

        // then
        Mockito.verify(userAuthenticationManagerMock).requestAuthentication(type, phoneNumber)
    }

    @Test
    fun `인증요검사 요청을 받으면 AuthenticationManager를 호출한다`() {
        // given
        val userAuthenticationManagerMock = Mockito.mock(UserAuthenticationManager::class.java)
        val sut = UserAuthenticationService(userAuthenticationManagerMock)

        val type = UserAuthenticationType.CREATE_USER
        val phoneNumber = "01011112222"
        val authenticationNumber = "1234"

        // when
        sut.checkAuthentication(type, phoneNumber, authenticationNumber)

        // then
        Mockito.verify(userAuthenticationManagerMock).checkAuthentication(type, phoneNumber, authenticationNumber)
    }
}