package com.project.myservice.domain.user.authentication

import com.project.myservice.common.exception.*
import com.project.myservice.domain.user.LocalUserRepository
import com.project.myservice.domain.user.User
import com.project.myservice.infrastructure.common.LocalCacheRepository
import com.project.myservice.infrastructure.common.LocalNotificationSender
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class UserAuthenticationManagerTest {

    @Nested
    @DisplayName("인증 요청을 하면")
    inner class RequestAuthentication {

        @Test
        fun `사용자 정보가 없어야 하는데, 사용자 정보가 있다면 에러가 발생한다`() {
            // given
            val phoneNumber = "01011112222"
            val user = User("testusername", "test@test.com", phoneNumber, "testpass", "testuser", "testnick")
            val userRepository = LocalUserRepository()
            val cacheRepository = LocalCacheRepository()
            val notificationSender = LocalNotificationSender()
            val sut = UserAuthenticationManager(1800000L, userRepository, cacheRepository, notificationSender)

            userRepository.save(user)

            // when
            val thrown = catchThrowable { sut.requestAuthentication(UserAuthenticationType.CREATE_USER, phoneNumber) }

            // then
            Assertions.assertThat(thrown).isNotNull
            Assertions.assertThat(thrown).isInstanceOf(UserAlreadyExistsException::class.java)
        }

        @Test
        fun `사용자 정보가 있어야 하는데, 사용자 정보가 없다면 에러가 발생한다`() {
            // given
            val userRepository = LocalUserRepository()
            val cacheRepository = LocalCacheRepository()
            val notificationSender = LocalNotificationSender()
            val sut = UserAuthenticationManager(1800000L, userRepository, cacheRepository, notificationSender)

            val phoneNumber = "01011112222"

            // when
            val thrown = catchThrowable { sut.requestAuthentication(UserAuthenticationType.RESET_PASSWORD, phoneNumber) }

            // then
            Assertions.assertThat(thrown).isNotNull
            Assertions.assertThat(thrown).isInstanceOf(UserNotFoundException::class.java)
        }

        @Test
        fun `캐시에 데이터를 저장한다`() {
            // given
            val userRepository = LocalUserRepository()
            val cacheRepository = LocalCacheRepository()
            val notificationSender = LocalNotificationSender()
            val sut = UserAuthenticationManager(1800000L, userRepository, cacheRepository, notificationSender)

            val phoneNumber = "01011112222"
            val cacheKey = "api:authentication:create_user:$phoneNumber"

            // when
            sut.requestAuthentication(UserAuthenticationType.CREATE_USER, phoneNumber)

            // then
            Assertions.assertThat(cacheRepository.data[cacheKey]).isNotNull
        }

        @Test
        fun `알람이 전송된다`() {
            // given
            val userRepository = LocalUserRepository()
            val cacheRepository = LocalCacheRepository()
            val notificationSender = LocalNotificationSender()
            val sut = UserAuthenticationManager(1800000L, userRepository, cacheRepository, notificationSender)

            val phoneNumber = "01011112222"

            // when
            val authenticationNumber = sut.requestAuthentication(UserAuthenticationType.CREATE_USER, phoneNumber)

            // then
            Assertions.assertThat(notificationSender.data.size).isEqualTo(1)
            Assertions.assertThat(notificationSender.data[0].first).isEqualTo(phoneNumber)
            Assertions.assertThat(notificationSender.data[0].second).isEqualTo("인증번호 [$authenticationNumber]를 입력해주세요")
        }
    }

    @Nested
    @DisplayName("인증 검사를 요청하면")
    inner class RequestCheckAuthentication {

        @Test
        fun `캐시에 데이터가 없으면 예외가 발생한다`() {
            // given
            val userRepository = LocalUserRepository()
            val cacheRepository = LocalCacheRepository()
            val notificationSender = LocalNotificationSender()
            val sut = UserAuthenticationManager(1800000L, userRepository, cacheRepository, notificationSender)

            val phoneNumber = "01011112222"
            val authenticationNumber = "1234"

            // when
            val thrown = catchThrowable {
                sut.checkAuthentication(UserAuthenticationType.CREATE_USER, phoneNumber, authenticationNumber)
            }

            // then
            Assertions.assertThat(thrown).isNotNull
            Assertions.assertThat(thrown).isInstanceOf(AuthenticationFailedException::class.java)
            Assertions.assertThat((thrown as BaseException).errorCode).isEqualTo(ErrorCode.AUTHENTICATION_NUMBER_NOT_FOUND)
        }

        @Test
        fun `입력값과 캐시의 값이 다르면 예외가 발생한다`() {
            // given
            val userRepository = LocalUserRepository()
            val cacheRepository = LocalCacheRepository()
            val notificationSender = LocalNotificationSender()
            val sut = UserAuthenticationManager(1800000L, userRepository, cacheRepository, notificationSender)

            val phoneNumber = "01011112222"
            val authenticationNumber = "1234"

            cacheRepository.save("api:authentication:create_user:$phoneNumber", "5678", 10000L)

            // when
            val thrown = catchThrowable {
                sut.checkAuthentication(UserAuthenticationType.CREATE_USER, phoneNumber, authenticationNumber)
            }

            // then
            Assertions.assertThat(thrown).isNotNull
            Assertions.assertThat(thrown).isInstanceOf(AuthenticationFailedException::class.java)
            Assertions.assertThat((thrown as BaseException).errorCode).isEqualTo(ErrorCode.AUTHENTICATION_NUMBER_MISMATCHED)
        }

        @Test
        fun `입력값과 캐시의 값이 같으면 검사를 통과한다`() {
            // given
            val userRepository = LocalUserRepository()
            val cacheRepository = LocalCacheRepository()
            val notificationSender = LocalNotificationSender()
            val sut = UserAuthenticationManager(1800000L, userRepository, cacheRepository, notificationSender)

            val phoneNumber = "01011112222"
            val authenticationNumber = "1234"

            cacheRepository.save("api:authentication:create_user:$phoneNumber", authenticationNumber, 10000L)

            // when
            val thrown = catchThrowable {
                sut.checkAuthentication(UserAuthenticationType.CREATE_USER, phoneNumber, authenticationNumber)
            }

            // then
            Assertions.assertThat(thrown).isNull()
        }
    }

    @Nested
    @DisplayName("인증 초기화를 요청하면")
    inner class RequestClearAuthentication {
        @Test
        fun `캐시의 데이터를 삭제한다`() {
            // given
            val userRepository = LocalUserRepository()
            val cacheRepository = LocalCacheRepository()
            val notificationSender = LocalNotificationSender()
            val sut = UserAuthenticationManager(1800000L, userRepository, cacheRepository, notificationSender)

            val phoneNumber = "01011112222"
            val cacheKey = "api:authentication:create_user:$phoneNumber"

            cacheRepository.save(cacheKey, "1234", 10000L)

            // when
            sut.clearAuthentication(UserAuthenticationType.CREATE_USER, phoneNumber)

            // then
            Assertions.assertThat(cacheRepository.data[cacheKey]).isNull()
        }
    }
}