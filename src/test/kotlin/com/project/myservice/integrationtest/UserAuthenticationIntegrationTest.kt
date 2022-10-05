package com.project.myservice.integrationtest

import com.project.myservice.domain.common.CacheRepository
import com.project.myservice.integrationtest.base.BaseTestContainer
import com.project.myservice.presentation.common.CommonResponse
import com.project.myservice.presentation.common.Result
import com.project.myservice.presentation.user.CheckUserAuthenticationRequestDto
import com.project.myservice.presentation.user.RequestUserAuthenticationRequestDto
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Rollback
class UserAuthenticationIntegrationTest : BaseTestContainer() {

    @Autowired
    lateinit var cacheRepository: CacheRepository

    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

    /**
     * 회원 가입 인증 요청
     * - 요청 이후에 인증 번호가 반환되어야 한다
     * - 인증 번호가 캐시에 저장되어야 한다
     */
    @Test
    @Transactional
    @DisplayName("[통합테스트] 회원가입 인증 요청")
    fun `회원가입 인증 요청`() {
        // given
        val phoneNumber = "01055556666"
        val request = RequestUserAuthenticationRequestDto("CREATE_USER", phoneNumber)

        // when
        val requestResult = testRestTemplate.postForEntity(
            "/api/v1/users/authentication/request",
            request,
            CommonResponse::class.java)

        // then
        Assertions.assertThat(requestResult.statusCode).isEqualTo(HttpStatus.OK)
        Assertions.assertThat(requestResult.body?.result).isEqualTo(Result.SUCCESS)

        val responseMap = (requestResult.body?.data as MutableMap<*, *>)
        val authenticationNumber = responseMap["authenticationNumber"]
        val findAuthenticationNumber = cacheRepository.find("api:authentication:create_user:${phoneNumber}")
        Assertions.assertThat(findAuthenticationNumber).isEqualTo(authenticationNumber)

        // clear
        cacheRepository.delete("api:authentication:create_user:${phoneNumber}")
    }

    /**
     * 비밀번호 초기화 인증 요청
     * - 요청 이후에 인증 번호가 반환되어야 한다
     * - 인증 번호가 캐시에 저장되어야 한다
     */
    @Test
    @Transactional
    @DisplayName("[통합테스트] 비밀번호 초기화 인증 요청")
    fun `비밀번호 초기화 인증 요청`() {
        // given
        val phoneNumber = "01033334444"
        val request = RequestUserAuthenticationRequestDto("RESET_PASSWORD", phoneNumber)

        // when
        val requestResult = testRestTemplate.postForEntity(
            "/api/v1/users/authentication/request",
            request,
            CommonResponse::class.java)

        // then
        Assertions.assertThat(requestResult.statusCode).isEqualTo(HttpStatus.OK)
        Assertions.assertThat(requestResult.body?.result).isEqualTo(Result.SUCCESS)

        val responseMap = (requestResult.body?.data as MutableMap<*, *>)
        val authenticationNumber = responseMap["authenticationNumber"]
        val findAuthenticationNumber = cacheRepository.find("api:authentication:reset_password:${phoneNumber}")
        Assertions.assertThat(findAuthenticationNumber).isEqualTo(authenticationNumber)

        // clear
        cacheRepository.delete("api:authentication:reset_password:${phoneNumber}")
    }

    /**
     * 인증 요청 검사
     * - 인증 번호가 캐시에 저장되어 있어야한다
     * - 캐시에 저장된 인증번호와 요청 번호가 일치해야한다
     */
    @Test
    @Transactional
    @DisplayName("[통합테스트] 인증 요청 검사")
    fun `인증 요청 검사`() {
        // given
        val phoneNumber = "01055556666"
        val request = CheckUserAuthenticationRequestDto("CREATE_USER", "01055556666", "1234")

        cacheRepository.save("api:authentication:create_user:${phoneNumber}", "1234", 1800000L)

        // when
        val requestResult = testRestTemplate.postForEntity(
            "/api/v1/users/authentication/check",
            request,
            CommonResponse::class.java)

        // then
        Assertions.assertThat(requestResult.statusCode).isEqualTo(HttpStatus.OK)
        Assertions.assertThat(requestResult.body?.result).isEqualTo(Result.SUCCESS)

        // clear
        cacheRepository.delete("api:authentication:create_user:${phoneNumber}")
    }
}