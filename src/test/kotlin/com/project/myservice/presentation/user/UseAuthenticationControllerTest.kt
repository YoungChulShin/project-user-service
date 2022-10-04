package com.project.myservice.presentation.user

import com.fasterxml.jackson.databind.ObjectMapper
import com.project.myservice.application.user.UserAuthenticationService
import com.project.myservice.domain.user.authentication.UserAuthenticationType
import com.project.myservice.presentation.common.CommonExceptionTranslator
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.Mockito
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

internal class UseAuthenticationControllerTest {

    @Nested
    @DisplayName("전화번호 인증을 요청할 때")
    inner class RequestAuthentication {

        lateinit var mockMvc: MockMvc
        lateinit var authenticationServiceMock: UserAuthenticationService
        private val objectMapper = ObjectMapper()

        @BeforeEach
        fun setup() {
            authenticationServiceMock = Mockito.mock(UserAuthenticationService::class.java)

            mockMvc = MockMvcBuilders
                .standaloneSetup(UseAuthenticationController(authenticationServiceMock))
                .setControllerAdvice(CommonExceptionTranslator())
                .build()
        }

        @Test
        fun `요청타입은 공백일 수 없다`() {
            // given
            val request = RequestUserAuthenticationRequestDto(null, "01011112222")

            // when, then
            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/users/authentication/request")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest)
                .andDo { MockMvcResultHandlers.print() }
                .andExpect(jsonPath("result").value("FAIL"))
                .andExpect(jsonPath("errorCode").value("COMMON_INVALID_PARAMETER"))
                .andExpect(jsonPath("message")
                    .value("요청한 'type' 값이 올바르지 않습니다. 입력 값: 'null'. 요청타입은 공백일 수 없습니다"))
        }

        @Test
        fun `전화번호는 공백일 수 없다`() {
            // given
            val request = RequestUserAuthenticationRequestDto("CREATE_USER", "")

            // when, then
            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/users/authentication/request")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest)
                .andDo { MockMvcResultHandlers.print() }
                .andExpect(jsonPath("result").value("FAIL"))
                .andExpect(jsonPath("errorCode").value("COMMON_INVALID_PARAMETER"))
                .andExpect(jsonPath("message")
                    .value("요청한 'phoneNumber' 값이 올바르지 않습니다. 입력 값: ''. 전화번호는 공백일 수 없습니다"))
        }

        @ParameterizedTest
        @ValueSource(strings = ["010", "0101111222233333", "0101111abcd"])
        fun `전화번호는 10-11자리 숫자만 입력 가능하다`(phoneNumber: String) {
            // given
            val request = RequestUserAuthenticationRequestDto("CREATE_USER", phoneNumber)

            // when, then
            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/users/authentication/request")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest)
                .andDo { MockMvcResultHandlers.print() }
                .andExpect(jsonPath("result").value("FAIL"))
                .andExpect(jsonPath("errorCode").value("COMMON_INVALID_PARAMETER"))
                .andExpect(jsonPath("message")
                    .value("요청한 'phoneNumber' 값이 올바르지 않습니다. 입력 값: '$phoneNumber'. 전화번호는 10~11 자리 숫자만 입력 가능합니다"))
        }

        @Test
        fun `정상 인증 요청이 되면, 인증 번호를 반환한다`() {
            // given
            val authenticationNumber = "1234"
            val phoneNumber = "01011112222"
            val request = RequestUserAuthenticationRequestDto("CREATE_USER", phoneNumber)


            Mockito.`when`(authenticationServiceMock.requestAuthentication(
                UserAuthenticationType.CREATE_USER,
                phoneNumber))
                .thenReturn(authenticationNumber)

            // when, then
            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/users/authentication/request")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk)
                .andDo { MockMvcResultHandlers.print() }
                .andExpect(jsonPath("result").value("SUCCESS"))
                .andExpect(jsonPath("data.type").value("CREATE_USER"))
                .andExpect(jsonPath("data.phoneNumber").value(phoneNumber))
                .andExpect(jsonPath("data.authenticationNumber").value(authenticationNumber))
        }
    }

    @Nested
    @DisplayName("전화번호 인증 검증을 요청할 때")
    inner class RequestCheckAuthentication {

        lateinit var mockMvc: MockMvc
        lateinit var authenticationServiceMock: UserAuthenticationService
        private val objectMapper = ObjectMapper()

        @BeforeEach
        fun setup() {
            authenticationServiceMock = Mockito.mock(UserAuthenticationService::class.java)

            mockMvc = MockMvcBuilders
                .standaloneSetup(UseAuthenticationController(authenticationServiceMock))
                .setControllerAdvice(CommonExceptionTranslator())
                .build()
        }

        @Test
        fun `요청타입은 공백일 수 없다`() {
            // given
            val request = CheckUserAuthenticationRequestDto(null, "01011112222", "1234")

            // when, then
            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/users/authentication/check")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest)
                .andDo { MockMvcResultHandlers.print() }
                .andExpect(jsonPath("result").value("FAIL"))
                .andExpect(jsonPath("errorCode").value("COMMON_INVALID_PARAMETER"))
                .andExpect(jsonPath("message")
                    .value("요청한 'type' 값이 올바르지 않습니다. 입력 값: 'null'. 요청타입은 공백일 수 없습니다"))
        }

        @Test
        fun `전화번호는 공백일 수 없다`() {
            // given
            val request = CheckUserAuthenticationRequestDto("CREATE_USER", null, "1234")

            // when, then
            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/users/authentication/check")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest)
                .andDo { MockMvcResultHandlers.print() }
                .andExpect(jsonPath("result").value("FAIL"))
                .andExpect(jsonPath("errorCode").value("COMMON_INVALID_PARAMETER"))
                .andExpect(jsonPath("message")
                    .value("요청한 'phoneNumber' 값이 올바르지 않습니다. 입력 값: 'null'. 전화번호는 공백일 수 없습니다"))
        }

        @ParameterizedTest
        @ValueSource(strings = ["010", "0101111222233333", "0101111abcd"])
        fun `전화번호는 10-11자리 숫자만 입력 가능하다`(phoneNumber: String) {
            // given
            val request = CheckUserAuthenticationRequestDto("CREATE_USER", phoneNumber, "1234")

            // when, then
            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/users/authentication/check")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest)
                .andDo { MockMvcResultHandlers.print() }
                .andExpect(jsonPath("result").value("FAIL"))
                .andExpect(jsonPath("errorCode").value("COMMON_INVALID_PARAMETER"))
                .andExpect(jsonPath("message")
                    .value("요청한 'phoneNumber' 값이 올바르지 않습니다. 입력 값: '$phoneNumber'. 전화번호는 10~11자리 숫자만 입력 가능합니다"))
        }

        @Test
        fun `인증번호는 공백일 수 없다`() {
            // given
            val request = CheckUserAuthenticationRequestDto("CREATE_USER", "01011112222", null)

            // when, then
            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/users/authentication/check")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest)
                .andDo { MockMvcResultHandlers.print() }
                .andExpect(jsonPath("result").value("FAIL"))
                .andExpect(jsonPath("errorCode").value("COMMON_INVALID_PARAMETER"))
                .andExpect(jsonPath("message")
                    .value("요청한 'authenticationNumber' 값이 올바르지 않습니다. 입력 값: 'null'. 인증번호는 공백일 수 없습니다"))
        }

        @ParameterizedTest
        @ValueSource(strings = ["123", "12345", "123b"])
        fun `인증번호는 4자리 숫자만 입력 가능하다`(authenticationNumber: String) {
            // given
            val request = CheckUserAuthenticationRequestDto("CREATE_USER", "01011112222", authenticationNumber)

            // when, then
            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/users/authentication/check")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest)
                .andDo { MockMvcResultHandlers.print() }
                .andExpect(jsonPath("result").value("FAIL"))
                .andExpect(jsonPath("errorCode").value("COMMON_INVALID_PARAMETER"))
                .andExpect(jsonPath("message")
                    .value("요청한 'authenticationNumber' 값이 올바르지 않습니다. 입력 값: '$authenticationNumber'. 인증번호는 4자리 숫자만 입력 가능합니다"))
        }

        @Test
        fun `인증 검증이 완료되면 예정된 응답을 반환한다`() {
            // given
            val authenticationNumber = "1234"
            val phoneNumber = "01011112222"
            val request = CheckUserAuthenticationRequestDto("CREATE_USER", phoneNumber, authenticationNumber)

            // when, then
            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/users/authentication/check")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk)
                .andDo { MockMvcResultHandlers.print() }
                .andExpect(jsonPath("result").value("SUCCESS"))
                .andExpect(jsonPath("data.type").value("CREATE_USER"))
                .andExpect(jsonPath("data.phoneNumber").value(phoneNumber))
                .andExpect(jsonPath("data.authenticationNumber").value(authenticationNumber))
        }
    }
}