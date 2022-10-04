package com.project.myservice.domain.user

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.NullSource
import org.junit.jupiter.params.provider.ValueSource

internal class UserTest {

    @Test
    fun `회원 엔티티를 생성한다`() {
        // given
        val username = "testusername"
        val email = "test@myservice.com"
        val phoneNumber = "01011112222"
        val password = "testpassword"
        val name = "myname"
        val nickname = "mynickname"
        val roleId = 1L

        // when
        val user = User(username, email, phoneNumber, password, name, nickname, roleId)

        // then
        Assertions.assertThat(user).isNotNull
        Assertions.assertThat(user.username).isEqualTo(username)
        Assertions.assertThat(user.email).isEqualTo(email)
        Assertions.assertThat(user.phoneNumber).isEqualTo(phoneNumber)
        Assertions.assertThat(user.phoneNumber).isEqualTo(phoneNumber)
        Assertions.assertThat(user.name).isEqualTo(name)
        Assertions.assertThat(user.nickname).isEqualTo(nickname)
        Assertions.assertThat(user.roleIds).contains(roleId)
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = [1L])
    fun `회원 엔티티를 비교한다`(id: Long?) {
        // given
        val username = "testusername"
        val email = "test@myservice.com"
        val phoneNumber = "01011112222"
        val password = "testpassword"
        val name = "myname"
        val nickname = "mynickname"
        val roleId = 1L

        // when
        val user = User(username, email, phoneNumber, password, name, nickname, roleId)
        val user2 = User(username, email, phoneNumber, password, name, nickname, roleId)

        // then
        Assertions.assertThat(user).isEqualTo(user2)
    }

    @Test
    fun `권한 정보를 추가할 수 있다`() {
        // given
        val username = "testusername"
        val email = "test@myservice.com"
        val phoneNumber = "01011112222"
        val password = "testpassword"
        val name = "myname"
        val nickname = "mynickname"
        val roleId = 1L

        val user = User(username, email, phoneNumber, password, name, nickname, roleId)

        // when
        user.addRole(2L)

        // then
        Assertions.assertThat(user.roleIds.size).isEqualTo(2)
        Assertions.assertThat(user.roleIds.containsAll(listOf(1L, 2L))).isTrue
    }

    @Test
    fun `비밀번호를 초기화할 수 있다`() {
        // given
        val username = "testusername"
        val email = "test@myservice.com"
        val phoneNumber = "01011112222"
        val password = "testpassword"
        val name = "myname"
        val nickname = "mynickname"
        val roleId = 1L

        val user = User(username, email, phoneNumber, password, name, nickname, roleId)

        val newPassword= "newpassword"

        // when
        user.resetPassword(newPassword)

        // then
        Assertions.assertThat(user.password).isEqualTo(newPassword)
    }

}