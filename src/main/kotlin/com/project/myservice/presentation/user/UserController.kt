package com.project.myservice.presentation.user

import com.project.myservice.application.user.UserService
import com.project.myservice.presentation.common.CommonResponse
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
}