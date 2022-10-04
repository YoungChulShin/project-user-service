package com.project.myservice.presentation.user

import com.project.myservice.application.user.CreateUserCommand
import com.project.myservice.application.user.ResetPasswordCommand
import com.project.myservice.common.util.toLocalString
import com.project.myservice.domain.user.UserDetailInfo
import com.project.myservice.domain.user.UserInfo
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size


data class CreateUserRequestDto(
    @field:NotBlank(message = "로그인 아이디는 공백일 수 없습니다")
    @field:Size(max = 25, message = "로그인 아이디는 최대 25자까지 가능합니다")
    val username: String?,

    @field:NotBlank(message = "이메일은 공백일 수 없습니다")
    @field:Email(message = "올바른 이메일 양식이 아닙니다")
    @field:Size(max = 50, message = "이메일은 최대 50자까지 가능합니다")
    val email: String?,

    @field:NotBlank(message = "전화번호는 공백일 수 없습니다")
    @field:Pattern(regexp = "[0-9]{10,11}", message = "전화번호는 10~11자리 숫자만 입력 가능합니다")
    val phoneNumber: String?,

    @field:NotBlank(message = "비밀번호는 공백일 수 없습니다")
    @field:Pattern(
        regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~\$^+=<>]).{8,20}$",
        message = "비밀번호는 8~20자리이고, 최소한 하나의 숫자,소문자,대문자,특수문자를 포함해야합니다"
    )
    val password: String?,

    @field:NotBlank(message = "사용자 이름은 공백일 수 없습니다")
    @field:Size(max = 50, message = "사용자 최대 50자까지 가능합니다")
    val name: String?,

    @field:NotBlank(message = "별명은 공백일 수 없습니다")
    @field:Size(max = 50, message = "별명은 최대 50자까지 가능합니다")
    val nickname: String?,

    @field:NotBlank(message = "인증번호는 공백일 수 없습니다")
    @field:Pattern(regexp = "[0-9]{4}", message = "인증번호는 4자리 숫자만 입력 가능합니다")
    val authenticationNumber: String?,
) {
    fun toCommand(): CreateUserCommand {
        return CreateUserCommand(
            username = username!!,
            email = email!!,
            phoneNumber = phoneNumber!!,
            password = password!!,
            name = name!!,
            nickname = nickname!!,
            authenticationNumber = authenticationNumber!!
        )
    }
}

data class ResetPasswordRequestDto(
    @field:NotBlank(message = "전화번호는 공백일 수 없습니다")
    @field:Pattern(regexp = "[0-9]{10,11}", message = "전화번호는 10~11자리 숫자만 입력 가능합니다")
    val phoneNumber: String?,

    @field:NotBlank(message = "비밀번호는 공백일 수 없습니다")
    @field:Pattern(
        regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~\$^+=<>]).{8,20}$",
        message = "비밀번호는 8~20자리이고, 최소한 하나의 숫자,소문자,대문자,특수문자를 포함해야합니다"
    )
    val newPassword: String?,

    @field:NotBlank(message = "인증번호는 공백일 수 없습니다")
    @field:Pattern(regexp = "[0-9]{4}", message = "인증번호는 4자리 숫자만 입력 가능합니다")
    val authenticationNumber: String?,
) {
    fun toCommand(): ResetPasswordCommand {
        return ResetPasswordCommand(
            phoneNumber = phoneNumber!!,
            newPassword = newPassword!!,
            authenticationNumber = authenticationNumber!!
        )
    }
}

data class UserInfoDto(
    val id: Long,
    val email: String,
    val phoneNumber: String,
    val username: String,
    val nickname: String,
    val createdAt: String,
    val updatedAt: String?,
    val deletedAt: String?
) {
    companion object {
        fun of(userInfo: UserInfo): UserInfoDto {
            return UserInfoDto(
                id = userInfo.id,
                email = userInfo.email,
                phoneNumber = userInfo.phoneNumber,
                username = userInfo.username,
                nickname = userInfo.nickname,
                createdAt = userInfo.createdAt.toLocalString(),
                updatedAt = userInfo.updatedAt?.toLocalString(),
                deletedAt = userInfo.deletedAt?.toLocalString()
            )
        }
    }
}

data class UserDetailInfoDto(
    val id: Long,
    val username: String,
    val email: String,
    val phoneNumber: String,
    val name: String,
    val nickname: String,
    val roles: List<String>,
    val createdAt: String,
    val updatedAt: String?,
    val deletedAt: String?,
) {
    companion object {
        fun of(userDetailInfo: UserDetailInfo): UserDetailInfoDto {
            return UserDetailInfoDto(
                id = userDetailInfo.id,
                username = userDetailInfo.username,
                email = userDetailInfo.email,
                phoneNumber = userDetailInfo.phoneNumber,
                name = userDetailInfo.name,
                nickname = userDetailInfo.nickname,
                roles = userDetailInfo.roles,
                createdAt = userDetailInfo.createdAt.toLocalString(),
                updatedAt = userDetailInfo.updatedAt?.toLocalString(),
                deletedAt = userDetailInfo.deletedAt?.toLocalString()
            )
        }
    }
}