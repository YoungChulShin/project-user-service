package com.project.myservice.presentation.user

import com.fasterxml.jackson.databind.ObjectMapper
import com.project.myservice.application.user.UserService
import com.project.myservice.common.exception.UserNotFoundException
import com.project.myservice.common.util.toLocalString
import com.project.myservice.domain.user.UserDetailInfo
import com.project.myservice.domain.user.UserInfo
import com.sun.security.auth.UserPrincipal
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EmptySource
import org.junit.jupiter.params.provider.NullSource
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.headers.HeaderDocumentation
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.time.Instant


internal class UserControllerTest {

    @Nested
    @WebMvcTest(UserController::class)
    @ExtendWith(RestDocumentationExtension::class)
    @DisplayName("회원가입 API를 호출할 때")
    inner class RequestCreateUserApi {

        lateinit var mockMvc: MockMvc

        @MockBean
        lateinit var userServiceMock: UserService

        @Autowired
        lateinit var objectMapper: ObjectMapper

        @BeforeEach
        fun setUp(webApplicationContext: WebApplicationContext, restDocumentation: RestDocumentationContextProvider) {
            this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply<DefaultMockMvcBuilder>(MockMvcRestDocumentation.documentationConfiguration(restDocumentation))
                .build()
        }

        @ParameterizedTest
        @NullSource
        @EmptySource
        fun `로그인 아이디는 공백일 수 없다`(username: String?) {
            // given
            val request = CreateUserRequestDto(
                username = username,
                email = "test@test.com",
                phoneNumber = "01011112222",
                password = "Test12345!@#$",
                name = "testname",
                nickname = "testnickname",
                authenticationNumber = "1234"
            )

            // when, then
            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/users")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andDo { MockMvcResultHandlers.print() }
                .andExpect(MockMvcResultMatchers.jsonPath("result").value("FAIL"))
                .andExpect(MockMvcResultMatchers.jsonPath("errorCode").value("COMMON_INVALID_PARAMETER"))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("message")
                    .value("요청한 'username' 값이 올바르지 않습니다. 입력 값: '$username'. 로그인 아이디는 공백일 수 없습니다"))
        }

        @Test
        fun `로그인 아이디는 25자까지 가능하다`() {
            // given
            val request = CreateUserRequestDto(
                username = "12345678901234567890123456",
                email = "test@test.com",
                phoneNumber = "01011112222",
                password = "Test12345!@#$",
                name = "testname",
                nickname = "testnickname",
                authenticationNumber = "1234"
            )

            // when, then
            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/users")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andDo { MockMvcResultHandlers.print() }
                .andExpect(MockMvcResultMatchers.jsonPath("result").value("FAIL"))
                .andExpect(MockMvcResultMatchers.jsonPath("errorCode").value("COMMON_INVALID_PARAMETER"))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("message")
                        .value("요청한 'username' 값이 올바르지 않습니다. 입력 값: '12345678901234567890123456'. 로그인 아이디는 최대 25자까지 가능합니다"))
        }

        @ParameterizedTest
        @NullSource
        @EmptySource
        fun `이메일은 공백일 수 없다`(email: String?) {
            // given
            val request = CreateUserRequestDto(
                username = "testusername",
                email = email,
                phoneNumber = "01011112222",
                password = "Test12345!@#$",
                name = "testname",
                nickname = "testnickname",
                authenticationNumber = "1234"
            )

            // when, then
            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/users")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andDo { MockMvcResultHandlers.print() }
                .andExpect(MockMvcResultMatchers.jsonPath("result").value("FAIL"))
                .andExpect(MockMvcResultMatchers.jsonPath("errorCode").value("COMMON_INVALID_PARAMETER"))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("message")
                        .value("요청한 'email' 값이 올바르지 않습니다. 입력 값: '$email'. 이메일은 공백일 수 없습니다"))
        }

        @ParameterizedTest
        @ValueSource(strings = ["test.com", "test@test@test.com"])
        fun `올바른 이메일 양식을 지켜야 한다`(email: String) {
            // given
            val request = CreateUserRequestDto(
                username = "testusername",
                email = email,
                phoneNumber = "01011112222",
                password = "Test12345!@#$",
                name = "testname",
                nickname = "testnickname",
                authenticationNumber = "1234"
            )

            // when, then
            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/users")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andDo { MockMvcResultHandlers.print() }
                .andExpect(MockMvcResultMatchers.jsonPath("result").value("FAIL"))
                .andExpect(MockMvcResultMatchers.jsonPath("errorCode").value("COMMON_INVALID_PARAMETER"))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("message")
                        .value("요청한 'email' 값이 올바르지 않습니다. 입력 값: '$email'. 올바른 이메일 양식이 아닙니다"))
        }

        @Test
        fun `이메일은 최대 50자까지 가능하다`() {
            // given
            val email = "123456789012345678901234567890123456789012@test.com"
            val request = CreateUserRequestDto(
                username = "testusername",
                email = email,
                phoneNumber = "01011112222",
                password = "Test12345!@#$",
                name = "testname",
                nickname = "testnickname",
                authenticationNumber = "1234"
            )

            // when, then
            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/users")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andDo { MockMvcResultHandlers.print() }
                .andExpect(MockMvcResultMatchers.jsonPath("result").value("FAIL"))
                .andExpect(MockMvcResultMatchers.jsonPath("errorCode").value("COMMON_INVALID_PARAMETER"))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("message")
                        .value("요청한 'email' 값이 올바르지 않습니다. 입력 값: '$email'. 이메일은 최대 50자까지 가능합니다"))
        }

        @ParameterizedTest
        @NullSource
        fun `전화번호는 공백일 수 없다`(phoneNumber: String?) {
            // given
            val request = CreateUserRequestDto(
                username = "testusername",
                email = "test@test.com",
                phoneNumber = phoneNumber,
                password = "Test12345!@#$",
                name = "testname",
                nickname = "testnickname",
                authenticationNumber = "1234"
            )

            // when, then
            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/users")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andDo { MockMvcResultHandlers.print() }
                .andExpect(MockMvcResultMatchers.jsonPath("result").value("FAIL"))
                .andExpect(MockMvcResultMatchers.jsonPath("errorCode").value("COMMON_INVALID_PARAMETER"))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("message")
                        .value("요청한 'phoneNumber' 값이 올바르지 않습니다. 입력 값: '$phoneNumber'. 전화번호는 공백일 수 없습니다"))
        }

        @ParameterizedTest
        @ValueSource(strings = ["010", "010111122223", "0101111222a"])
        fun `전화번호는 10~11자리 숫자만 입력 가능하다`(phoneNumber: String?) {
            // given
            val request = CreateUserRequestDto(
                username = "testusername",
                email = "test@test.com",
                phoneNumber = phoneNumber,
                password = "Test12345!@#$",
                name = "testname",
                nickname = "testnickname",
                authenticationNumber = "1234"
            )

            // when, then
            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/users")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andDo { MockMvcResultHandlers.print() }
                .andExpect(MockMvcResultMatchers.jsonPath("result").value("FAIL"))
                .andExpect(MockMvcResultMatchers.jsonPath("errorCode").value("COMMON_INVALID_PARAMETER"))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("message")
                        .value("요청한 'phoneNumber' 값이 올바르지 않습니다. 입력 값: '$phoneNumber'. 전화번호는 10~11자리 숫자만 입력 가능합니다"))
        }

        @ParameterizedTest
        @NullSource
        fun `비밀번호는 공백일 수 없다`(password: String?) {
            // given
            val request = CreateUserRequestDto(
                username = "testusername",
                email = "test@test.com",
                phoneNumber = "01011112222",
                password = password,
                name = "testname",
                nickname = "testnickname",
                authenticationNumber = "1234"
            )

            // when, then
            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/users")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andDo { MockMvcResultHandlers.print() }
                .andExpect(MockMvcResultMatchers.jsonPath("result").value("FAIL"))
                .andExpect(MockMvcResultMatchers.jsonPath("errorCode").value("COMMON_INVALID_PARAMETER"))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("message")
                        .value("요청한 'password' 값이 올바르지 않습니다. 입력 값: '$password'. 비밀번호는 공백일 수 없습니다"))
        }

        @ParameterizedTest
        @ValueSource(strings = ["7digit0", "21digit00000000000000", "Testpassword01", "Testpassword!#", "testpassword0!#", "TESTPASSWORD0!#"])
        fun `비밀번호는 8~20자리이고, 최소한 하나의 숫자,소문자,대문자,특수문자를 포함해야한다`(password: String?) {
            // given
            val request = CreateUserRequestDto(
                username = "testusername",
                email = "test@test.com",
                phoneNumber = "01011112222",
                password = password,
                name = "testname",
                nickname = "testnickname",
                authenticationNumber = "1234"
            )

            // when, then
            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/users")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andDo { MockMvcResultHandlers.print() }
                .andExpect(MockMvcResultMatchers.jsonPath("result").value("FAIL"))
                .andExpect(MockMvcResultMatchers.jsonPath("errorCode").value("COMMON_INVALID_PARAMETER"))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("message")
                        .value("요청한 'password' 값이 올바르지 않습니다. 입력 값: '$password'. 비밀번호는 8~20자리이고, 최소한 하나의 숫자,소문자,대문자,특수문자를 포함해야합니다"))
        }

        @ParameterizedTest
        @NullSource
        fun `사용자 이름은 공백일 수 없다`(name: String?) {
            // given
            val request = CreateUserRequestDto(
                username = "testusername",
                email = "test@test.com",
                phoneNumber = "01011112222",
                password = "Test12345!@#$",
                name = name,
                nickname = "testnickname",
                authenticationNumber = "1234"
            )

            // when, then
            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/users")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andDo { MockMvcResultHandlers.print() }
                .andExpect(MockMvcResultMatchers.jsonPath("result").value("FAIL"))
                .andExpect(MockMvcResultMatchers.jsonPath("errorCode").value("COMMON_INVALID_PARAMETER"))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("message")
                        .value("요청한 'name' 값이 올바르지 않습니다. 입력 값: '$name'. 사용자 이름은 공백일 수 없습니다"))
        }

        @Test
        fun `사용자 이름은 최대 50자까지 가능하다`() {
            // given
            val name = "123456789012345678901234567890123456789012345678901"
            val request = CreateUserRequestDto(
                username = "testusername",
                email = "test@test.com",
                phoneNumber = "01011112222",
                password = "Test12345!@#$",
                name = name,
                nickname = "testnickname",
                authenticationNumber = "1234"
            )

            // when, then
            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/users")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andDo { MockMvcResultHandlers.print() }
                .andExpect(MockMvcResultMatchers.jsonPath("result").value("FAIL"))
                .andExpect(MockMvcResultMatchers.jsonPath("errorCode").value("COMMON_INVALID_PARAMETER"))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("message")
                        .value("요청한 'name' 값이 올바르지 않습니다. 입력 값: '$name'. 사용자 최대 50자까지 가능합니다"))
        }

        @ParameterizedTest
        @NullSource
        @EmptySource
        fun `별명은 공백일 수 없다`(nickname: String?) {
            // given
            val request = CreateUserRequestDto(
                username = "testusername",
                email = "test@test.com",
                phoneNumber = "01011112222",
                password = "Test12345!@#$",
                name = "testname",
                nickname = nickname,
                authenticationNumber = "1234"
            )

            // when, then
            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/users")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andDo { MockMvcResultHandlers.print() }
                .andExpect(MockMvcResultMatchers.jsonPath("result").value("FAIL"))
                .andExpect(MockMvcResultMatchers.jsonPath("errorCode").value("COMMON_INVALID_PARAMETER"))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("message")
                        .value("요청한 'nickname' 값이 올바르지 않습니다. 입력 값: '$nickname'. 별명은 공백일 수 없습니다"))
        }

        @Test
        fun `별명은 최대 50자까지 가능하다`() {
            // given
            val nickname = "123456789012345678901234567890123456789012345678901"
            val request = CreateUserRequestDto(
                username = "testusername",
                email = "test@test.com",
                phoneNumber = "01011112222",
                password = "Test12345!@#$",
                name = "testname",
                nickname = nickname,
                authenticationNumber = "1234"
            )

            // when, then
            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/users")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andDo { MockMvcResultHandlers.print() }
                .andExpect(MockMvcResultMatchers.jsonPath("result").value("FAIL"))
                .andExpect(MockMvcResultMatchers.jsonPath("errorCode").value("COMMON_INVALID_PARAMETER"))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("message")
                        .value("요청한 'nickname' 값이 올바르지 않습니다. 입력 값: '$nickname'. 별명은 최대 50자까지 가능합니다"))
        }

        @ParameterizedTest
        @NullSource
        fun `인증번호는 공백일 수 없다`(authenticationNumber: String?) {
            // given
            val request = CreateUserRequestDto(
                username = "testusername",
                email = "test@test.com",
                phoneNumber = "01011112222",
                password = "Test12345!@#$",
                name = "testname",
                nickname = "testnickname",
                authenticationNumber = authenticationNumber
            )

            // when, then
            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/users")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andDo { MockMvcResultHandlers.print() }
                .andExpect(MockMvcResultMatchers.jsonPath("result").value("FAIL"))
                .andExpect(MockMvcResultMatchers.jsonPath("errorCode").value("COMMON_INVALID_PARAMETER"))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("message")
                        .value("요청한 'authenticationNumber' 값이 올바르지 않습니다. 입력 값: '$authenticationNumber'. 인증번호는 공백일 수 없습니다"))
        }

        @ParameterizedTest
        @ValueSource(strings = ["123", "12345", "123a", "123A"])
        fun `인증번호는 4자리 숫자만 가능하다`(authenticationNumber: String?) {
            // given
            val request = CreateUserRequestDto(
                username = "testusername",
                email = "test@test.com",
                phoneNumber = "01011112222",
                password = "Test12345!@#$",
                name = "testname",
                nickname = "testnickname",
                authenticationNumber = authenticationNumber
            )

            // when, then
            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/users")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andDo { MockMvcResultHandlers.print() }
                .andExpect(MockMvcResultMatchers.jsonPath("result").value("FAIL"))
                .andExpect(MockMvcResultMatchers.jsonPath("errorCode").value("COMMON_INVALID_PARAMETER"))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("message")
                        .value("요청한 'authenticationNumber' 값이 올바르지 않습니다. 입력 값: '$authenticationNumber'. 인증번호는 4자리 숫자만 입력 가능합니다"))
        }

        @Test
        fun `입력 데이터가 올바르면 ApplicationService를 호출하고, 호출이 완료되면 UserInfo를 반환한다`() {
            // given
            val currentTime = Instant.ofEpochMilli(1640999420000) // 2022-01-01 01:10:20 GMT
            val request = CreateUserRequestDto(
                username = "testusername",
                email = "test@test.com",
                phoneNumber = "01011112222",
                password = "Test12345!@#$",
                name = "testname",
                nickname = "testnickname",
                authenticationNumber = "1234"
            )

            val userInfo = UserInfo(
                id = 1L,
                username = "testusername",
                email = "test@test.com",
                phoneNumber = "01011112222",
                name = "testname",
                nickname = "testnickname",
                createdAt = currentTime,
                updatedAt = null,
                deletedAt = null
            )

            Mockito.`when`(userServiceMock.createUser(request.toCommand()))
                .thenReturn(userInfo)

            // when, then
            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/users")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andDo { MockMvcResultHandlers.print() }
                .andExpect(MockMvcResultMatchers.jsonPath("result").value("SUCCESS"))
                .andExpect(MockMvcResultMatchers.jsonPath("data.email").value("test@test.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("data.phoneNumber").value("01011112222"))
                .andExpect(MockMvcResultMatchers.jsonPath("data.username").value("testusername"))
                .andExpect(MockMvcResultMatchers.jsonPath("data.nickname").value("testnickname"))
                .andExpect(MockMvcResultMatchers.jsonPath("data.createdAt").value("2022-01-01 10:10:20"))
                .andExpect(MockMvcResultMatchers.jsonPath("data.updatedAt").value(null))
                .andExpect(MockMvcResultMatchers.jsonPath("data.deletedAt").value(null))
                .andDo(
                    MockMvcRestDocumentation.document(
                        "users/create",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        PayloadDocumentation.requestFields(
                            PayloadDocumentation.fieldWithPath("username").type(JsonFieldType.STRING).description("로그인 아이디"),
                            PayloadDocumentation.fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                            PayloadDocumentation.fieldWithPath("phoneNumber").type(JsonFieldType.STRING).description("전화번호"),
                            PayloadDocumentation.fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호"),
                            PayloadDocumentation.fieldWithPath("name").type(JsonFieldType.STRING).description("회원 이름"),
                            PayloadDocumentation.fieldWithPath("nickname").type(JsonFieldType.STRING).description("회원 별명"),
                            PayloadDocumentation.fieldWithPath("authenticationNumber").type(JsonFieldType.STRING).description("인증번호"),
                        ),
                        PayloadDocumentation.responseFields(
                            PayloadDocumentation.fieldWithPath("result").type(JsonFieldType.STRING).description("성공 여부"),
                            PayloadDocumentation.fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("고유 아이디"),
                            PayloadDocumentation.fieldWithPath("data.username").type(JsonFieldType.STRING).description("로그인 아이디"),
                            PayloadDocumentation.fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                            PayloadDocumentation.fieldWithPath("data.phoneNumber").type(JsonFieldType.STRING).description("전화번호"),
                            PayloadDocumentation.fieldWithPath("data.name").type(JsonFieldType.STRING).description("회원 이름"),
                            PayloadDocumentation.fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("회원 별명"),
                            PayloadDocumentation.fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성 날짜"),
                            PayloadDocumentation.fieldWithPath("data.updatedAt").type(JsonFieldType.NULL).description("마지막 변경 날짜"),
                            PayloadDocumentation.fieldWithPath("data.deletedAt").type(JsonFieldType.NULL).description("삭제 날짜"),
                            PayloadDocumentation.fieldWithPath("errorCode").type(JsonFieldType.NULL).description("에러 코드"),
                            PayloadDocumentation.fieldWithPath("message").type(JsonFieldType.NULL).description("에러 메시지"),

                        )
                    )
                )

            Mockito.verify(userServiceMock).createUser(request.toCommand())
        }
    }

    @Nested
    @WebMvcTest(UserController::class)
    @ExtendWith(RestDocumentationExtension::class)
    @DisplayName("비밀번호 변경 API를 호출할 때")
    inner class RequestResetPasswordApi {

        lateinit var mockMvc: MockMvc

        @MockBean
        lateinit var userServiceMock: UserService

        @Autowired
        lateinit var objectMapper: ObjectMapper

        @BeforeEach
        fun setUp(webApplicationContext: WebApplicationContext, restDocumentation: RestDocumentationContextProvider) {
            this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply<DefaultMockMvcBuilder>(MockMvcRestDocumentation.documentationConfiguration(restDocumentation))
                .build()
        }

        @ParameterizedTest
        @NullSource
        fun `전화번호는 공백일 수 없다`(phoneNumber: String?) {
            // given
            val request = ResetPasswordRequestDto(phoneNumber, "Secret1323!", "1234")

            // when, then
            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/users/reset-password")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andDo { MockMvcResultHandlers.print() }
                .andExpect(MockMvcResultMatchers.jsonPath("result").value("FAIL"))
                .andExpect(MockMvcResultMatchers.jsonPath("errorCode").value("COMMON_INVALID_PARAMETER"))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("message")
                        .value("요청한 'phoneNumber' 값이 올바르지 않습니다. 입력 값: '$phoneNumber'. 전화번호는 공백일 수 없습니다"))
        }

        @ParameterizedTest
        @ValueSource(strings = ["010", "010111122223", "0101111222a"])
        fun `전화번호는 10~11자리 숫자만 입력 가능하다`(phoneNumber: String?) {
            // given
            val request = ResetPasswordRequestDto(phoneNumber, "Secret1323!", "1234")

            // when, then
            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/users/reset-password")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andDo { MockMvcResultHandlers.print() }
                .andExpect(MockMvcResultMatchers.jsonPath("result").value("FAIL"))
                .andExpect(MockMvcResultMatchers.jsonPath("errorCode").value("COMMON_INVALID_PARAMETER"))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("message")
                        .value("요청한 'phoneNumber' 값이 올바르지 않습니다. 입력 값: '$phoneNumber'. 전화번호는 10~11자리 숫자만 입력 가능합니다"))
        }

        @ParameterizedTest
        @NullSource
        fun `비밀번호는 공백일 수 없다`(newPassword: String?) {
            // given
            val request = ResetPasswordRequestDto("01011112222", newPassword, "1234")

            // when, then
            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/users/reset-password")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andDo { MockMvcResultHandlers.print() }
                .andExpect(MockMvcResultMatchers.jsonPath("result").value("FAIL"))
                .andExpect(MockMvcResultMatchers.jsonPath("errorCode").value("COMMON_INVALID_PARAMETER"))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("message")
                        .value("요청한 'newPassword' 값이 올바르지 않습니다. 입력 값: '$newPassword'. 비밀번호는 공백일 수 없습니다"))
        }

        @ParameterizedTest
        @ValueSource(strings = ["7digit0", "21digit00000000000000", "Testpassword01", "Testpassword!#", "testpassword0!#", "TESTPASSWORD0!#"])
        fun `비밀번호는 8~20자리이고, 최소한 하나의 숫자,소문자,대문자,특수문자를 포함해야한다`(newPassword: String?) {
            // given
            val request = ResetPasswordRequestDto("01011112222", newPassword, "1234")

            // when, then
            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/users/reset-password")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andDo { MockMvcResultHandlers.print() }
                .andExpect(MockMvcResultMatchers.jsonPath("result").value("FAIL"))
                .andExpect(MockMvcResultMatchers.jsonPath("errorCode").value("COMMON_INVALID_PARAMETER"))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("message")
                        .value("요청한 'newPassword' 값이 올바르지 않습니다. 입력 값: '$newPassword'. 비밀번호는 8~20자리이고, 최소한 하나의 숫자,소문자,대문자,특수문자를 포함해야합니다"))
        }

        @ParameterizedTest
        @NullSource
        fun `인증번호는 공백일 수 없다`(authenticationNumber: String?) {
            // given
            val request = ResetPasswordRequestDto("01011112222", "Secret1323!", authenticationNumber)

            // when, then
            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/users/reset-password")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andDo { MockMvcResultHandlers.print() }
                .andExpect(MockMvcResultMatchers.jsonPath("result").value("FAIL"))
                .andExpect(MockMvcResultMatchers.jsonPath("errorCode").value("COMMON_INVALID_PARAMETER"))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("message")
                        .value("요청한 'authenticationNumber' 값이 올바르지 않습니다. 입력 값: '$authenticationNumber'. 인증번호는 공백일 수 없습니다"))
        }

        @ParameterizedTest
        @ValueSource(strings = ["123", "12345", "123a", "123A"])
        fun `인증번호는 4자리 숫자만 입력 가능하다`(authenticationNumber: String?) {
            // given
            val request = ResetPasswordRequestDto("01011112222", "Secret1323!", authenticationNumber)

            // when, then
            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/users/reset-password")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
                .andDo { MockMvcResultHandlers.print() }
                .andExpect(MockMvcResultMatchers.jsonPath("result").value("FAIL"))
                .andExpect(MockMvcResultMatchers.jsonPath("errorCode").value("COMMON_INVALID_PARAMETER"))
                .andExpect(
                    MockMvcResultMatchers.jsonPath("message")
                        .value("요청한 'authenticationNumber' 값이 올바르지 않습니다. 입력 값: '$authenticationNumber'. 인증번호는 4자리 숫자만 입력 가능합니다"))
        }

        @Test
        fun `입력 데이터가 올바르면 ApplicationService를 호출하고, 호출이 완료되면 성공 응답을 반환한다`() {
            // given
            val request = ResetPasswordRequestDto("01011112222", "Secret1323!", "1234")

            // when, then
            mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/users/reset-password")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andDo { MockMvcResultHandlers.print() }
                .andExpect(MockMvcResultMatchers.jsonPath("result").value("SUCCESS"))
                .andDo(
                    MockMvcRestDocumentation.document(
                        "users/reset-password",
                        preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        PayloadDocumentation.requestFields(
                            PayloadDocumentation.fieldWithPath("phoneNumber").type(JsonFieldType.STRING).description("전화번호"),
                            PayloadDocumentation.fieldWithPath("newPassword").type(JsonFieldType.STRING).description("신규 비밀번호"),
                            PayloadDocumentation.fieldWithPath("authenticationNumber").type(JsonFieldType.STRING).description("인증번호"),
                        ),
                        PayloadDocumentation.responseFields(
                            PayloadDocumentation.fieldWithPath("result").type(JsonFieldType.STRING).description("성공 여부"),
                            PayloadDocumentation.fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터"),
                            PayloadDocumentation.fieldWithPath("errorCode").type(JsonFieldType.NULL).description("에러 코드"),
                            PayloadDocumentation.fieldWithPath("message").type(JsonFieldType.NULL).description("에러 메시지"),
                        )
                    )
                )

            Mockito.verify(userServiceMock)
                .resetPassword(request.toCommand())
        }
    }

    @Nested
    @WebMvcTest(UserController::class)
    @ExtendWith(RestDocumentationExtension::class)
    @DisplayName("내 정보 조회를 호출할 때")
    inner class FindMyApi {

        lateinit var mockMvc: MockMvc

        @MockBean
        lateinit var userServiceMock: UserService

        @BeforeEach
        fun setUp(webApplicationContext: WebApplicationContext, restDocumentation: RestDocumentationContextProvider) {
            this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply<DefaultMockMvcBuilder>(MockMvcRestDocumentation.documentationConfiguration(restDocumentation))
                .build()

            val user: UserDetails = org.springframework.security.core.userdetails.User(
                "username",
                "Secret1323!",
                listOf(SimpleGrantedAuthority("ROLE_USER"))
            )

            val context = SecurityContextHolder.getContext()
            context.authentication = UsernamePasswordAuthenticationToken(user, user.password, user.authorities)
        }

        @Test
        fun `사용자 정보가 없다면 에러를 응답한다`() {
            // given
            val principal = UserPrincipal("testusername")

            Mockito.`when`(userServiceMock.findUserDetail(principal.name))
                .thenThrow(UserNotFoundException())

            // when, then
            mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/users/my")
                    .principal(principal))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andDo { MockMvcResultHandlers.print() }
                .andExpect(MockMvcResultMatchers.jsonPath("result").value("FAIL"))
                .andExpect(MockMvcResultMatchers.jsonPath("errorCode").value("USER_NOT_FOUND"))
        }

        @Test
        fun `정보가 올바르다면, 상세 정보를 응답한다`() {
            // given
            val principal = UserPrincipal("testusername")
            val currentTime = Instant.now()

            val userDetailInfo = UserDetailInfo(
                1L,
                "testusername",
                "test@test.com",
                "01011112222",
                "testname",
                "testnickname",
                listOf("ROLE_USER"),
                currentTime,
                currentTime,
                null
            )

            Mockito.`when`(userServiceMock.findUserDetail(principal.name))
                .thenReturn(userDetailInfo)

            // when, then
            mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/users/my")
                    .header("Authorization", "bearer accessToken")
                    .principal(principal))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andDo { MockMvcResultHandlers.print() }
                .andExpect(MockMvcResultMatchers.jsonPath("result").value("SUCCESS"))
                .andExpect(MockMvcResultMatchers.jsonPath("data.username").value("testusername"))
                .andExpect(MockMvcResultMatchers.jsonPath("data.email").value("test@test.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("data.phoneNumber").value("01011112222"))
                .andExpect(MockMvcResultMatchers.jsonPath("data.name").value("testname"))
                .andExpect(MockMvcResultMatchers.jsonPath("data.nickname").value("testnickname"))
                .andExpect(MockMvcResultMatchers.jsonPath("data.roles[0]").value("ROLE_USER"))
                .andExpect(MockMvcResultMatchers.jsonPath("data.createdAt").value(currentTime.toLocalString()))
                .andExpect(MockMvcResultMatchers.jsonPath("data.updatedAt").value(currentTime.toLocalString()))
                .andExpect(MockMvcResultMatchers.jsonPath("data.deletedAt").value(null))
                .andDo(
                    MockMvcRestDocumentation.document(
                        "users/my",
                        preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        HeaderDocumentation.requestHeaders(headerWithName("Authorization").description("bearer token")),
                        PayloadDocumentation.responseFields(
                            PayloadDocumentation.fieldWithPath("result").type(JsonFieldType.STRING).description("성공 여부"),
                            PayloadDocumentation.fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("고유 아이디"),
                            PayloadDocumentation.fieldWithPath("data.username").type(JsonFieldType.STRING).description("로그인 아이디"),
                            PayloadDocumentation.fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                            PayloadDocumentation.fieldWithPath("data.phoneNumber").type(JsonFieldType.STRING).description("전화번호"),
                            PayloadDocumentation.fieldWithPath("data.name").type(JsonFieldType.STRING).description("회원 이름"),
                            PayloadDocumentation.fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("회원 별명"),
                            PayloadDocumentation.fieldWithPath("data.roles").type(JsonFieldType.ARRAY).description("회원 권한"),
                            PayloadDocumentation.fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성 날짜"),
                            PayloadDocumentation.fieldWithPath("data.updatedAt").type(JsonFieldType.STRING).description("마지막 변경 날짜"),
                            PayloadDocumentation.fieldWithPath("data.deletedAt").type(JsonFieldType.NULL).description("삭제 날짜"),
                            PayloadDocumentation.fieldWithPath("errorCode").type(JsonFieldType.NULL).description("에러 코드"),
                            PayloadDocumentation.fieldWithPath("message").type(JsonFieldType.NULL).description("에러 메시지"),
                        )
                    )
                )
        }
    }
}