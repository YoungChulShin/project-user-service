package com.project.myservice.common.exception

enum class ErrorCode(
    val errorMessage: String,
) {

    USER_ALREADY_EXISTS("회원정보가 존재합니다"),
    USER_NOT_FOUND("회원정보를 찾을 수 없습니다"),

    AUTHENTICATION_NUMBER_NOT_FOUND("인증정보를 찾을 수 없습니다. 정보가 만료되었거나, 잘못된 값을 입력하셨습니다"),
    AUTHENTICATION_NUMBER_MISMATCHED("인증정보가 불일치합니다"),
}