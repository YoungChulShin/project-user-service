package com.project.myservice.presentation.user

import com.project.myservice.application.user.UserService
import com.project.myservice.presentation.common.CommonResponse
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    val userService: UserService
) {

    @PostMapping
    fun createUser(
        @RequestBody @Validated request: CreateUserRequestDto,
    ): CommonResponse<UserInfoDto> {
        val userInfo = userService.createUser(request.toCommand())

        return CommonResponse.success(UserInfoDto.of(userInfo))
    }

    @PostMapping("/reset-password")
    fun resetPassword(
        @RequestBody @Validated request: ResetPasswordRequestDto,
    ): CommonResponse<Void> {
        userService.resetPassword(request.toCommand())

        return CommonResponse.success()
    }

    @GetMapping("/my")
    fun findMyInfo(principal: Principal): CommonResponse<UserDetailInfoDto> {
        val userDetailInfo = userService.findUserDetail(principal.name)

        return CommonResponse.success(UserDetailInfoDto.of(userDetailInfo))
    }
}