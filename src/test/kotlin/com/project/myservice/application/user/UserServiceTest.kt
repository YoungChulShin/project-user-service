package com.project.myservice.application.user

import com.project.myservice.common.exception.BaseException
import com.project.myservice.common.exception.ErrorCode
import com.project.myservice.common.exception.UserAlreadyExistsException
import com.project.myservice.common.exception.UserNotFoundException
import com.project.myservice.domain.common.LocalEventPublisher
import com.project.myservice.domain.user.*
import com.project.myservice.domain.user.authentication.UserAuthenticationManager
import com.project.myservice.domain.user.authentication.UserAuthenticationType
import com.project.myservice.domain.user.event.UserCreatedEvent
import com.project.myservice.domain.user.event.UserPasswordResetEvent
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

internal class UserServiceTest {

    @Nested
    @DisplayName("회원 정보를 생성할 때")
    inner class CreateUser {

        private val passwordEncoder = BCryptPasswordEncoder()
        private lateinit var authenticationManagerMock: UserAuthenticationManager
        private lateinit var userRepository: LocalUserRepository
        private lateinit var roleRepository: LocalRoleRepository
        private lateinit var eventPublisher: LocalEventPublisher
        private lateinit var sut: UserService

        @BeforeEach
        fun setup() {
            authenticationManagerMock = Mockito.mock(UserAuthenticationManager::class.java)
            userRepository = LocalUserRepository()
            roleRepository = LocalRoleRepository()
            eventPublisher = LocalEventPublisher()
            sut = UserService(authenticationManagerMock, passwordEncoder, userRepository, roleRepository, eventPublisher)

            roleRepository.save(Role.create(RoleType.ROLE_USER))
        }

        @Test
        fun `동일한 아이디가 있으면 에러가 발생한다`() {
            // given
            val command = createDummyCommand()
            val user =  User(
                "testusername",
                "test2@test.com",
                "01033334444",
                "testpassword2",
                "testname2",
                "testnickname2",
                1L
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
        fun `동일한 이메일이 있으면 에러가 발생한다`() {
            // given
            val command = createDummyCommand()
            val user =  User(
                "testusername2",
                "test@test.com",
                "01033334444",
                "testpassword2",
                "testname2",
                "testnickname2",
                1L
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
        fun `동일한 연락처가 있으면 에러가 발생한다`() {
            // given
            val command = createDummyCommand()
            val user =  User(
                "testusername2",
                "test2@test.com",
                "01011112222",
                "testpassword2",
                "testname2",
                "testnickname2",
                1L
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

        @Test
        fun `비밀번호를 암호화해서 저장한다`() {
            // given
            val command = createDummyCommand()
            val userInfo = sut.createUser(command)

            // when
            val findUser = userRepository.findByUsername(userInfo.username)

            // then
            Assertions.assertThat(findUser).isNotNull
            Assertions.assertThat(findUser!!.password).isNotEqualTo(command.password)
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

    @Nested
    @DisplayName("비밀번호 초기화를 요청할 때")
    inner class ResetPassword {

        private val passwordEncoder = BCryptPasswordEncoder()
        private lateinit var authenticationManagerMock: UserAuthenticationManager
        private lateinit var userRepository: LocalUserRepository
        private lateinit var roleRepository: LocalRoleRepository
        private lateinit var eventPublisher: LocalEventPublisher
        private lateinit var sut: UserService

        @BeforeEach
        fun setup() {
            authenticationManagerMock = Mockito.mock(UserAuthenticationManager::class.java)
            userRepository = LocalUserRepository()
            roleRepository = LocalRoleRepository()
            eventPublisher = LocalEventPublisher()
            sut = UserService(authenticationManagerMock, passwordEncoder, userRepository, roleRepository, eventPublisher)

            roleRepository.save(Role.create(RoleType.ROLE_USER))
        }

        @Test
        fun `인증정보를 검사한다`() {
            // given
            val command = ResetPasswordCommand("01011112222", "01033334444", "1234")
            val user =  User(
                "testusername",
                "test@test.com",
                "01011112222",
                "testpassword",
                "testname",
                "testnickname",
                1L
            )
            userRepository.save(user)

            // when
            sut.resetPassword(command)

            // then
            Mockito.verify(authenticationManagerMock)
                .checkAuthentication(
                    UserAuthenticationType.RESET_PASSWORD,
                    command.phoneNumber,
                    command.authenticationNumber
                )
        }

        @Test
        fun `회원 정보가 없으면 에러가 발생한다`() {
            // given
            val command = ResetPasswordCommand("01011112222", "01033334444", "1234")

            // when
            val thrown = catchThrowable { sut.resetPassword(command) }


            // then
            Assertions.assertThat(thrown).isNotNull
            Assertions.assertThat(thrown).isInstanceOf(UserNotFoundException::class.java)
            Assertions.assertThat((thrown as BaseException).message).isEqualTo("회원정보를 찾을 수 없습니다")
            Assertions.assertThat((thrown as BaseException).errorCode).isEqualTo(ErrorCode.USER_NOT_FOUND)
        }

        @Test
        fun `비밀번호가 변경된다`() {
            // given
            val command = ResetPasswordCommand("01011112222", "01033334444", "1234")
            val user =  User(
                "testusername",
                "test@test.com",
                "01011112222",
                "testpassword",
                "testname",
                "testnickname",
                1L
            )
            userRepository.save(user)

            // when
            sut.resetPassword(command)

            // then
            val findUser = userRepository.findByPhoneNumber(command.phoneNumber)
            Assertions.assertThat(findUser?.password).isNotEqualTo("01011112222")
        }

        @Test
        fun `비밀번호 초기화 이벤트를 발행한다`() {
            // given
            val command = ResetPasswordCommand("01011112222", "01033334444", "1234")
            val user =  User(
                "testusername",
                "test@test.com",
                "01011112222",
                "testpassword",
                "testname",
                "testnickname",
                1L
            )
            userRepository.save(user)

            // when
            sut.resetPassword(command)

            // then
            Assertions.assertThat(eventPublisher.data.size).isEqualTo(1)
            Assertions.assertThat(eventPublisher.data[0]).isInstanceOf(UserPasswordResetEvent::class.java)
            Assertions.assertThat((eventPublisher.data[0] as UserPasswordResetEvent).userInfo.username).isEqualTo("testusername")
        }
    }

    @Nested
    @DisplayName("회원 상세 정보를 호출할 때")
    inner class FindUserDetail {

        private val passwordEncoder = BCryptPasswordEncoder()
        private lateinit var authenticationManagerMock: UserAuthenticationManager
        private lateinit var userRepository: LocalUserRepository
        private lateinit var roleRepository: LocalRoleRepository
        private lateinit var eventPublisher: LocalEventPublisher
        private lateinit var sut: UserService

        @BeforeEach
        fun setup() {
            authenticationManagerMock = Mockito.mock(UserAuthenticationManager::class.java)
            roleRepository = LocalRoleRepository()
            userRepository = LocalUserRepository(roleRepository)
            eventPublisher = LocalEventPublisher()
            sut = UserService(authenticationManagerMock, passwordEncoder, userRepository, roleRepository, eventPublisher)

            roleRepository.save(Role.create(RoleType.ROLE_USER))
        }

        @Test
        fun `사용자 정보가 없으면 에러가 발생한다`() {
            // given
            val username = "testusername"

            // when
            val thrown = catchThrowable { sut.findUserDetail(username) }

            // then
            Assertions.assertThat(thrown).isNotNull
            Assertions.assertThat(thrown).isInstanceOf(UserNotFoundException::class.java)
            Assertions.assertThat((thrown as BaseException).message).isEqualTo("회원정보를 찾을 수 없습니다")
            Assertions.assertThat((thrown as BaseException).errorCode).isEqualTo(ErrorCode.USER_NOT_FOUND)
        }

        @Test
        fun `사용자 상세 정보를 리턴한다`() {
            // given
            roleRepository.save(Role.create(RoleType.ROLE_ADMIN))
            roleRepository.save(Role.create(RoleType.ROLE_USER))
            val userRole = roleRepository.find(RoleType.ROLE_USER)

            val username = "testusername"
            val user =  User(
                username,
                "test@test.com",
                "01011112222",
                "testpassword",
                "testname",
                "testnickname",
                userRole?.id!!
            )

            userRepository.save(user)

            // when
            val userDetailInfo = sut.findUserDetail(username)

            // then
            Assertions.assertThat(userDetailInfo).isNotNull
            Assertions.assertThat(userDetailInfo.username).isEqualTo(username)
            Assertions.assertThat(userDetailInfo.email).isEqualTo("test@test.com")
            Assertions.assertThat(userDetailInfo.phoneNumber).isEqualTo("01011112222")
            Assertions.assertThat(userDetailInfo.name).isEqualTo("testname")
            Assertions.assertThat(userDetailInfo.nickname).isEqualTo("testnickname")
            Assertions.assertThat(userDetailInfo.roles.size).isEqualTo(1)
            Assertions.assertThat(userDetailInfo.roles[0]).isEqualTo("ROLE_USER")
        }
    }
}