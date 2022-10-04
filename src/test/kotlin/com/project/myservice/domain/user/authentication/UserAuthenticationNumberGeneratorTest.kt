package com.project.myservice.domain.user.authentication

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class UserAuthenticationNumberGeneratorTest {

    @Test
    fun `4자리의 난수가 생성된다`() {
        // when
        val authenticationNumber = UserAuthenticationNumberGenerator.generate()

        // then
        val authenticationNumberInt = authenticationNumber.toInt()
        Assertions.assertThat(authenticationNumberInt).isGreaterThanOrEqualTo(0)
        Assertions.assertThat(authenticationNumberInt).isLessThanOrEqualTo(9999)
    }

    @Test
    fun `인증 번호는 중복되지 않는다`() {
        // when
        val authenticationNumber = UserAuthenticationNumberGenerator.generate()
        val authenticationNumber2 = UserAuthenticationNumberGenerator.generate()
        val authenticationNumber3 = UserAuthenticationNumberGenerator.generate()

        // then
        Assertions.assertThat(authenticationNumber).isNotEqualTo(authenticationNumber2)
        Assertions.assertThat(authenticationNumber).isNotEqualTo(authenticationNumber3)
        Assertions.assertThat(authenticationNumber2).isNotEqualTo(authenticationNumber3)
    }
}