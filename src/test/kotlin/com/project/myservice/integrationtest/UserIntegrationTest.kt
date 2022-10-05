package com.project.myservice.integrationtest

import com.project.myservice.domain.common.CacheRepository
import com.project.myservice.domain.user.UserRepository
import com.project.myservice.integrationtest.base.BaseTestContainer
import com.project.myservice.presentation.common.CommonResponse
import com.project.myservice.presentation.common.Result
import com.project.myservice.presentation.user.CreateUserRequestDto
import com.project.myservice.presentation.user.ResetPasswordRequestDto
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Rollback
class UserIntegrationTest : BaseTestContainer() {

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var cacheRepository: CacheRepository

    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

    /**
     * 회원 가입
     * - 인증 번호가 캐시에 저장되어 있어야 한다
     * - 기존에 가입된 회원 정보가 없어야한다
     * - 회원 정보가 DB에 저장되어야 한다
     * - 회원 정보 완료 이후에 캐시를 삭제한다
     */
    @Test
    @Transactional
    @DisplayName("[통합테스트] 회원가입")
    fun `회원가입`() {
        // given
        val phoneNumber = "01055556666"
        val authenticationNumber = "1234"
        val username = "testusername"
        val request = CreateUserRequestDto(
            username = username,
            email = "test@test.com",
            phoneNumber = phoneNumber,
            password = "Test12345!@#$",
            name = "testname",
            nickname = "testnickname",
            authenticationNumber = authenticationNumber
        )

        cacheRepository.save("api:authentication:create_user:${phoneNumber}", authenticationNumber, 1800000)

        // when
        val requestResult = testRestTemplate.postForEntity(
            "/api/v1/users",
            request,
            CommonResponse::class.java)

        // then
        Assertions.assertThat(requestResult.statusCode).isEqualTo(HttpStatus.OK)
        Assertions.assertThat(requestResult.body?.result).isEqualTo(Result.SUCCESS)

        val findUser = userRepository.findByUsername(username)
        Assertions.assertThat(findUser).isNotNull
        Assertions.assertThat(findUser?.username).isEqualTo(username)

        val authorizationNumber = cacheRepository.find("api:authentication:create_user:${phoneNumber}")
        Assertions.assertThat(authorizationNumber).isNull()
    }

    /**
     * 비밀번호 초기화
     * - 인증 번호가 캐시에 저장되어 있어야 한다
     * - 비밀번호 정보를 변경하고 변경된 정보가 DB에 저장된다
     * - 비밀번호 변경 이후에 캐시를 삭제한다
     */
    @Test
    @Transactional
    @DisplayName("[통합테스트] 비밀번호 초기화")
    fun `비밀번호 초기화`() {
        // given
        val phoneNumber = "01033334444"
        val authenticationNumber = "1234"
        val request = ResetPasswordRequestDto(
            phoneNumber = phoneNumber,
            newPassword = "newTest12345!@#$",
            authenticationNumber = authenticationNumber
        )

        cacheRepository.save("api:authentication:reset_password:${phoneNumber}", authenticationNumber, 1800000)

        // when
        val requestResult = testRestTemplate.postForEntity(
            "/api/v1/users/reset-password",
            request,
            CommonResponse::class.java)

        // then
        Assertions.assertThat(requestResult.statusCode).isEqualTo(HttpStatus.OK)
        Assertions.assertThat(requestResult.body?.result).isEqualTo(Result.SUCCESS)

        val findUser = userRepository.findByPhoneNumber(phoneNumber)
        Assertions.assertThat(findUser).isNotNull
        Assertions.assertThat(findUser?.password).isNotEqualTo("\$2a\$10\$IMj2tLz5UHw1mB66/a5TTOSKljS1bHYqLtgxC0qVGIl39iL7SRMsK")

        val authorizationNumber = cacheRepository.find("api:authentication:reset_password:${phoneNumber}")
        Assertions.assertThat(authorizationNumber).isNull()
    }

    /**
     * 로그인
     * - 토큰 정보가 생성된다
     */
    @Test
    @Transactional
    @DisplayName("[통합테스트] 로그인")
    fun `로그인`() {
        // when
        val requestResult = testRestTemplate.postForEntity(
            "/api/v1/login?loginType=USERNAME&loginId=testuser&password=Secret1323",
            null,
            CommonResponse::class.java)

        // then
        Assertions.assertThat(requestResult.statusCode).isEqualTo(HttpStatus.OK)
        Assertions.assertThat(requestResult.body?.result).isEqualTo(Result.SUCCESS)

        val responseMap = (requestResult.body?.data as MutableMap<*, *>)
        val accessToken = responseMap["accessToken"]
        Assertions.assertThat(accessToken).isNotNull
    }

    /**
     * 내 정보 조회
     * - DB에 내 정보가 있어야한다
     * - 로그인 토큰을 기반으로 사용자 정보를 찾아서 조회한다
     * - DB 조회 결과와 API 응답 결과가 일치해야한다
     */
    @Test
    @Transactional
    @DisplayName("[통합테스트] 내 정보 조회")
    fun `내 정보 조회`() {
        // given
        val loginRequestResult = testRestTemplate.postForEntity(
            "/api/v1/login?loginType=USERNAME&loginId=testuser&password=Secret1323",
            null,
            CommonResponse::class.java)

        val loginResponseMap = (loginRequestResult.body?.data as MutableMap<*, *>)
        val accessToken = loginResponseMap["accessToken"]

        val httpHeaders = HttpHeaders()
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "bearer $accessToken")

        val user = userRepository.findByUsername("testuser")

        // when
        val requestResult = testRestTemplate.exchange(
            "/api/v1/users/my",
            HttpMethod.GET,
            HttpEntity<Any>(httpHeaders),
            CommonResponse::class.java
        )

        // when
        Assertions.assertThat(requestResult.statusCode).isEqualTo(HttpStatus.OK)
        Assertions.assertThat(requestResult.body?.result).isEqualTo(Result.SUCCESS)

        val responseMap = (requestResult.body?.data as MutableMap<*, *>)
        Assertions.assertThat(responseMap["username"]).isEqualTo(user?.username)
        Assertions.assertThat(responseMap["email"]).isEqualTo(user?.email)
        Assertions.assertThat(responseMap["phoneNumber"]).isEqualTo(user?.phoneNumber)
        Assertions.assertThat(responseMap["name"]).isEqualTo(user?.name)
        Assertions.assertThat(responseMap["nickname"]).isEqualTo(user?.nickname)
    }
}