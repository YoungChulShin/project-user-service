package com.project.myservice.config.security.token

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class TokenManagerTest {

    @Test
    fun `토큰 생성`() {
        // given
        val username = "testusername"
        val uri = "localhost:8080/test"
        val roles = listOf("ROLE_ADMIN", "ROLE_USER")

        // when
        val tokenInfo = TokenManager.issueToken(
            sub = username,
            iss = uri,
            roles = roles
        )

        // then
        Assertions.assertThat(tokenInfo.accessToken).isNotNull
    }

    @Test
    fun `토큰 검증`() {
        // given
        val username = "testusername"
        val uri = "localhost:8080/test"
        val roles = listOf("ROLE_ADMIN", "ROLE_USER")
        val tokenInfo = TokenManager.issueToken(
            sub = username,
            iss = uri,
            roles = roles
        )

        // when
        val decodedToken = TokenManager.verifyToken(tokenInfo.accessToken)

        // then
        Assertions.assertThat(decodedToken).isNotNull
        Assertions.assertThat(decodedToken.subject).isEqualTo(username)
        Assertions.assertThat(decodedToken.issuer).isEqualTo(uri)
        val tokenRoles = decodedToken.claims["roles"]?.asArray(String::class.java)
        Assertions.assertThat(tokenRoles).isNotNull
        Assertions.assertThat(tokenRoles?.contains("ROLE_ADMIN")).isTrue
        Assertions.assertThat(tokenRoles?.contains("ROLE_USER")).isTrue
    }
}