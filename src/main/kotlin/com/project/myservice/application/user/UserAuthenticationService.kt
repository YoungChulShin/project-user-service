package com.project.myservice.application.user

import com.project.myservice.domain.user.authentication.UserAuthenticationManager
import com.project.myservice.domain.user.authentication.UserAuthenticationType
import org.springframework.stereotype.Service

@Service
class UserAuthenticationService(
    val userAuthenticationManager: UserAuthenticationManager,
) {

    fun requestAuthentication(type: UserAuthenticationType, phoneNumber: String) =
        userAuthenticationManager.requestAuthentication(type, phoneNumber)

    fun checkAuthentication(
        type: UserAuthenticationType,
        phoneNumber: String,
        authenticationNumber: String
    ) = userAuthenticationManager.checkAuthentication(type, phoneNumber, authenticationNumber)
}