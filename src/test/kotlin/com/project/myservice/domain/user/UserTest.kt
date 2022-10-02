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

        // when
        val user = User(username, email, phoneNumber, password, name, nickname)

        // then
        Assertions.assertThat(user).isNotNull
        Assertions.assertThat(user.username).isEqualTo(username)
        Assertions.assertThat(user.email).isEqualTo(email)
        Assertions.assertThat(user.phoneNumber).isEqualTo(phoneNumber)
        Assertions.assertThat(user.phoneNumber).isEqualTo(phoneNumber)
        Assertions.assertThat(user.name).isEqualTo(name)
        Assertions.assertThat(user.nickname).isEqualTo(nickname)
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

        // when
        val user = User(username, email, phoneNumber, password, name, nickname)
        val user2 = User(username, email, phoneNumber, password, name, nickname)

        // then
        Assertions.assertThat(user).isEqualTo(user2)
    }
}