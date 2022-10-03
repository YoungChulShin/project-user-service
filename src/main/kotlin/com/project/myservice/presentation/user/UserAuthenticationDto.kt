package com.project.myservice.presentation.user

import com.project.myservice.domain.user.authentication.UserAuthenticationType
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern

data class RequestUserAuthenticationRequestDto(
    @field:NotBlank(message = "요청타입은 공백일 수 없습니다")
    val type: String?,

    @field:NotBlank(message = "전화번호는 공백일 수 없습니다")
    @field:Pattern(regexp = "[0-9]{10,11}", message = "전화번호는 10~11 자리 숫자만 입력 가능합니다")
    val phoneNumber: String?,
)

data class RequestUserAuthenticationResponseDto(
    val type: UserAuthenticationType,
    val phoneNumber: String,
    val authenticationNumber: String,
)

data class CheckUserAuthenticationRequestDto(
    @field:NotBlank(message = "요청타입은 공백일 수 없습니다")
    val type: String?,

    @field:NotBlank(message = "전화번호는 공백일 수 없습니다")
    @field:Pattern(regexp = "[0-9]{10,11}", message = "전화번호는 10~11자리 숫자만 입력 가능합니다")
    val phoneNumber: String?,

    @field:NotBlank(message = "인증번호는 공백일 수 없습니다")
    @field:Pattern(regexp = "[0-9]{4}", message = "인증번호는 4자리 숫자만 입력 가능합니다")
    val authenticationNumber: String?,
)

data class CheckUserAuthenticationResponseDto(
    val type: UserAuthenticationType,
    val phoneNumber: String,
    val authenticationNumber: String,
)