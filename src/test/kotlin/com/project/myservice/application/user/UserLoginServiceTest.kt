package com.project.myservice.application.user

import com.project.myservice.common.exception.BaseException
import com.project.myservice.common.exception.InvalidParameterException
import com.project.myservice.common.exception.UserNotFoundException
import com.project.myservice.domain.user.*
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EmptySource
import org.junit.jupiter.params.provider.NullSource
import org.junit.jupiter.params.provider.ValueSource

internal class UserLoginServiceTest {

    @ParameterizedTest
    @NullSource
    @EmptySource
    @ValueSource(strings = ["testuser", "testuser:testpass:testresult"])
    fun `잘못된 인증정보가 있으면 에러가 발생한다`(username: String?) {
        // given
        val userRepository = LocalUserRepository()
        val roleRepository = LocalRoleRepository()
        val sut = UserLoginService(userRepository, roleRepository)

        // when
        val thrown = catchThrowable { sut.loadUserByUsername(null) }

        // then
        Assertions.assertThat(thrown).isNotNull
        Assertions.assertThat(thrown).isInstanceOf(InvalidParameterException::class.java)
        Assertions.assertThat((thrown as BaseException).message).isEqualTo("로그인 정보 생성 중 오류가 발생했습니다")
    }

    @Test
    fun `정의되지 않은 로그인 타입은 에러가 발생한다`() {
        // given
        val userRepository = LocalUserRepository()
        val roleRepository = LocalRoleRepository()
        val sut = UserLoginService(userRepository, roleRepository)

        // when
        val thrown = catchThrowable { sut.loadUserByUsername("testtype:testuser") }

        // then
        Assertions.assertThat(thrown).isNotNull
        Assertions.assertThat(thrown).isInstanceOf(InvalidParameterException::class.java)
        Assertions.assertThat((thrown as BaseException).message).isEqualTo("'loginType' 파라미터 처리 중 에러가 발생했습니다")
    }

    @Test
    fun `로그인 아이디가 공백이면 에러가 발생한다`() {
        // given
        val userRepository = LocalUserRepository()
        val roleRepository = LocalRoleRepository()
        val sut = UserLoginService(userRepository, roleRepository)

        // when
        val thrown = catchThrowable { sut.loadUserByUsername("USERNAME:") }

        // then
        Assertions.assertThat(thrown).isNotNull
        Assertions.assertThat(thrown).isInstanceOf(InvalidParameterException::class.java)
        Assertions.assertThat((thrown as BaseException).message).isEqualTo("'username' 파라미터 처리 중 에러가 발생했습니다")
    }

    @ParameterizedTest
    @ValueSource(strings = ["USERNAME:testusername", "EMAIL:test@test.com", "PHONE_NUMBER:01011112222"])
    fun `회원 정보가 없으면 에러가 발생한다`(username: String) {
        // given
        val userRepository = LocalUserRepository()
        val roleRepository = LocalRoleRepository()
        val sut = UserLoginService(userRepository, roleRepository)

        // when
        val thrown = catchThrowable { sut.loadUserByUsername(username) }

        // then
        Assertions.assertThat(thrown).isNotNull
        Assertions.assertThat(thrown).isInstanceOf(UserNotFoundException::class.java)
        Assertions.assertThat((thrown as BaseException).message).isEqualTo("회원정보를 찾을 수 없습니다")
    }

    @Test
    fun `인증정보가 올바르면 UserDetail 정보를 리턴한다`() {
        // given
        val userRepository = LocalUserRepository()
        val roleRepository = LocalRoleRepository()
        val sut = UserLoginService(userRepository, roleRepository)

        roleRepository.save(Role.create(RoleType.ROLE_ADMIN))
        roleRepository.save(Role.create(RoleType.ROLE_USER))

        val user =  User(
            "testusername",
            "test2@test.com",
            "01033334444",
            "testpassword2",
            "testname2",
            "testnickname2",
            2L
        )
        userRepository.save(user)

        // when
        val userDetail = sut.loadUserByUsername("USERNAME:testusername")

        // then
        Assertions.assertThat(userDetail).isNotNull
        Assertions.assertThat(userDetail.username).isEqualTo("testusername")
        Assertions.assertThat(userDetail.password).isEqualTo("testpassword2")
        Assertions.assertThat(userDetail.authorities.size).isEqualTo(1)
        Assertions.assertThat(userDetail.authorities.first().authority).isEqualTo("ROLE_USER")
    }
}