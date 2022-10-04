package com.project.myservice.application.user

data class CreateUserCommand(
    val username: String,
    val email: String,
    val phoneNumber: String,
    val password: String,
    val name: String,
    val nickname: String,
    val authenticationNumber: String,
)

data class ResetPasswordCommand(
    val phoneNumber: String,
    val newPassword: String,
    val authenticationNumber: String,
)