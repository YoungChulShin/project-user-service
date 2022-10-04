package com.project.myservice.application.user.eventlistener

import com.project.myservice.domain.user.UserInfo
import com.project.myservice.domain.user.authentication.UserAuthenticationManager
import com.project.myservice.domain.user.authentication.UserAuthenticationType
import com.project.myservice.domain.user.event.UserPasswordResetEvent
import com.project.myservice.domain.user.notification.UserNotificationType
import com.project.myservice.domain.user.notification.UserPasswordResetMessage
import com.project.myservice.infrastructure.notification.LocalUserNotifier
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.time.Instant

internal class UserPasswordResetEventListenerTest {

    @Nested
    @DisplayName("비밀번호 초기화가 완료되면")
    inner class HandleUserPasswordResetEvent {

        @Test
        fun `인증번호 삭제를 호출한다`() {
            // given
            val authenticationManagerMock = Mockito.mock(UserAuthenticationManager::class.java)
            val userNotifier = LocalUserNotifier()
            val sut = UserPasswordResetEventListener(authenticationManagerMock, userNotifier)

            val userInfo = getDummyUserInfo()
            val event = UserPasswordResetEvent(userInfo, "01011112222")

            // when
            sut.handleUserPasswordResetEvent(event)

            // then
            Mockito.verify(authenticationManagerMock)
                .clearAuthentication(UserAuthenticationType.RESET_PASSWORD, event.userInfo.phoneNumber)
        }

        @Test
        fun `사용자 푸시 알람을 호출한다`() {
            // given
            val authenticationManagerMock = Mockito.mock(UserAuthenticationManager::class.java)
            val userNotifier = LocalUserNotifier()
            val sut = UserPasswordResetEventListener(authenticationManagerMock, userNotifier)

            val userInfo = getDummyUserInfo()
            val event = UserPasswordResetEvent(userInfo, "01011112222")

            // when
            sut.handleUserPasswordResetEvent(event)

            // then
            Assertions.assertThat(userNotifier.data.size).isEqualTo(1)
            Assertions.assertThat(userNotifier.data[0].first).isEqualTo(userInfo)
            Assertions.assertThat(userNotifier.data[0].second).isEqualTo(UserNotificationType.RESET_PASSWORD)
            Assertions.assertThat(userNotifier.data[0].third).isEqualTo(UserPasswordResetMessage(event.newPassword))
        }

        private fun getDummyUserInfo() = UserInfo(
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
    }
}