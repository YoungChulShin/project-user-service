package com.project.myservice.common.util

import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.Instant

class TimeUtilTest {

    @Test
    @DisplayName("toLocalString 확장함수는 Instant를 yyyy-MM-dd HH:mm:ss 문자열로 변환한다")
    fun `toLocalString 확장함수는 Instant를 String으로 변환한다`() {
        // given
        // 2022-01-01 01:10:20 GMT
        val currentTime = Instant.ofEpochMilli(1640999420000)

        // when
        val localTime = currentTime.toLocalString()
        println(localTime)

        // when
        Assertions.assertThat(localTime).isEqualTo("2022-01-01 10:10:20")
    }
}