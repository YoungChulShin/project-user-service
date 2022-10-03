package com.project.myservice.application.user.eventlistener

import com.project.myservice.domain.user.UserInfo
import com.project.myservice.domain.user.authentication.UserAuthenticationManager
import com.project.myservice.domain.user.authentication.UserAuthenticationType
import com.project.myservice.domain.user.event.UserCreatedEvent
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.time.Instant

internal class UserCreatedEventListenerTest {

    @Test
    fun `사용자가 생성되면 전화번호 인증 캐시 삭제 기능을 호출한다`() {
        // given
        val authenticationManagerMock = Mockito.mock(UserAuthenticationManager::class.java)
        val sut = UserCreatedEventListener(authenticationManagerMock)

        val userInfo = UserInfo(
            1L,
            "testusername",
            "test@test.com",
            "01011112222",
            "testname",
            "testnickname",
            Instant.now(),
            null,
            null
        )
        val event = UserCreatedEvent(userInfo)

        // when
        sut.handleUserCreatedEvent(event)

        // then
        Mockito.verify(authenticationManagerMock)
            .clearAuthentication(UserAuthenticationType.CREATE_USER, userInfo.phoneNumber)
    }
}