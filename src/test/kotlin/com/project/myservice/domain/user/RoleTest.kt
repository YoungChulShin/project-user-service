package com.project.myservice.domain.user

import org.assertj.core.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

internal class RoleTest {

    @ParameterizedTest
    @ValueSource(strings = ["ROLE_ADMIN", "ROLE_USER"])
    fun `권한이 생성된다`(type: RoleType) {
        // given

        // when
        val role = Role.create(type)

        // then
        Assertions.assertThat(role.name).isEqualTo(type.name)
    }
}