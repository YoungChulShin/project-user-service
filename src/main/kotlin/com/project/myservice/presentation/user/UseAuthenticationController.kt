package com.project.myservice.presentation.user

import com.project.myservice.application.user.UserAuthenticationService
import com.project.myservice.domain.user.authentication.UserAuthenticationType
import com.project.myservice.presentation.common.CommonResponse
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users/authentication")
class UseAuthenticationController(
    val userAuthenticationService: UserAuthenticationService,
) {

    @PostMapping("/request")
    fun requestAuthentication(
        @RequestBody @Validated request: RequestUserAuthenticationRequestDto,
    ): CommonResponse<RequestUserAuthenticationResponseDto> {
        val authenticationNumber = userAuthenticationService.requestAuthentication(
            type = UserAuthenticationType.valueOf(request.type!!.uppercase()),
            phoneNumber = request.phoneNumber!!
        )

        return CommonResponse.success(
            RequestUserAuthenticationResponseDto(
                type = UserAuthenticationType.valueOf(request.type.uppercase()),
                phoneNumber = request.phoneNumber,
                authenticationNumber = authenticationNumber
            )
        )
    }

    @PostMapping("/check")
    fun checkAuthentication(
        @RequestBody @Validated request: CheckUserAuthenticationRequestDto,
    ): CommonResponse<CheckUserAuthenticationResponseDto> {
        userAuthenticationService.checkAuthentication(
            type = UserAuthenticationType.valueOf(request.type!!.uppercase()),
            phoneNumber = request.phoneNumber!!,
            authenticationNumber = request.authenticationNumber!!
        )

        return CommonResponse.success(
            CheckUserAuthenticationResponseDto(
                type = UserAuthenticationType.valueOf(request.type.uppercase()),
                phoneNumber = request.phoneNumber,
                authenticationNumber = request.authenticationNumber
            )
        )
    }
}