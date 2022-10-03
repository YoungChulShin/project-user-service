package com.project.myservice.common.exception

enum class ErrorCode(
    val errorMessage: String,
) {

    USER_NOT_FOUND("회원정보를 찾을 수 없습니다"),
}