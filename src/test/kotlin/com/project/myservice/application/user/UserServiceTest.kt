package com.project.myservice.application.user

import com.project.myservice.common.exception.BaseException
import com.project.myservice.common.exception.UserAlreadyExistsException
import com.project.myservice.domain.common.LocalEventPublisher
import com.project.myservice.domain.user.LocalUserRepository
import com.project.myservice.domain.user.User
import com.project.myservice.domain.user.authentication.UserAuthenticationManager
import com.project.myservice.domain.user.authentication.UserAuthenticationType
import com.project.myservice.domain.user.event.UserCreatedEvent
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito

internal class UserServiceTest {

    @Nested
    @DisplayName("회원 정보를 생성할 때")
    inner class CreateUser {

        private lateinit var authenticationManagerMock: UserAuthenticationManager
        private lateinit var userRepository: LocalUserRepository
        private lateinit var eventPublisher: LocalEventPublisher
        private lateinit var sut: UserService

        @BeforeEach
        fun setup() {
            authenticationManagerMock = Mockito.mock(UserAuthenticationManager::class.java)
            userRepository = LocalUserRepository()
            eventPublisher = LocalEventPublisher()
            sut = UserService(authenticationManagerMock, userRepository, eventPublisher)
        }

        @Test
        fun `동일한 아이디가 있으면 에러`() {
            // given
            val command = createDummyCommand()
            val user =  User(
                "testusername",
                "test2@test.com",
                "01033334444",
                "testpassword2",
                "testname2",
                "testnickname2"
            )

            userRepository.save(user)

            // when
            val thrown = catchThrowable { sut.createUser(command) }

            // then
            Assertions.assertThat(thrown).isNotNull
            Assertions.assertThat(thrown).isInstanceOf(UserAlreadyExistsException::class.java)
            Assertions.assertThat((thrown as BaseException).message).isEqualTo("동일한 아이디를 사용하는 회원정보가 존재합니다")
        }

        @Test
        fun `동일한 이메일이 있으면 에러`() {
            // given
            val command = createDummyCommand()
            val user =  User(
                "testusername2",
                "test@test.com",
                "01033334444",
                "testpassword2",
                "testname2",
                "testnickname2"
            )

            userRepository.save(user)

            // when
            val thrown = catchThrowable { sut.createUser(command) }

            // then
            Assertions.assertThat(thrown).isNotNull
            Assertions.assertThat(thrown).isInstanceOf(UserAlreadyExistsException::class.java)
            Assertions.assertThat((thrown as BaseException).message).isEqualTo("동일한 이메일을 사용하는 회원정보가 존재합니다")
        }

        @Test
        fun `동일한 연락처가 있으면 에러`() {
            // given
            val command = createDummyCommand()
            val user =  User(
                "testusername2",
                "test2@test.com",
                "01011112222",
                "testpassword2",
                "testname2",
                "testnickname2"
            )

            userRepository.save(user)

            // when
            val thrown = catchThrowable { sut.createUser(command) }

            // then
            Assertions.assertThat(thrown).isNotNull
            Assertions.assertThat(thrown).isInstanceOf(UserAlreadyExistsException::class.java)
            Assertions.assertThat((thrown as BaseException).message).isEqualTo("동일한 연락처를 사용하는 회원정보가 존재합니다")
        }

        @Test
        fun `checkAuthentication를 호출한다`() {
            // given
            val command = createDummyCommand()

            // when
            sut.createUser(command)

            // then
            Mockito
                .verify(authenticationManagerMock)
                .checkAuthentication(
                    UserAuthenticationType.CREATE_USER,
                    command.phoneNumber,
                    command.authenticationNumber
                )
        }

        @Test
        fun `사용자 생성 이벤트를 발행한다`() {
            // given
            val command = createDummyCommand()

            // when
            sut.createUser(command)

            // then
            Assertions.assertThat(eventPublisher.data.size).isEqualTo(1)
            Assertions.assertThat((eventPublisher.data[0] as UserCreatedEvent).userInfo.username).isEqualTo("testusername")
        }

        @Test
        fun `정상 생성되면 사용자 정보를 반환한다`() {
            // given
            val command = createDummyCommand()

            // when
            val userInfo = sut.createUser(command)

            // then
            Assertions.assertThat(userInfo).isNotNull
            Assertions.assertThat(userInfo.username).isEqualTo("testusername")
            Assertions.assertThat(userInfo.email).isEqualTo("test@test.com")
            Assertions.assertThat(userInfo.phoneNumber).isEqualTo("01011112222")
            Assertions.assertThat(userInfo.name).isEqualTo("testname")
            Assertions.assertThat(userInfo.nickname).isEqualTo("testnickname")
        }

        private fun createDummyCommand() = CreateUserCommand(
            "testusername",
            "test@test.com",
            "01011112222",
            "testpassword",
            "testname",
            "testnickname",
            "1234"
        )
    }
}