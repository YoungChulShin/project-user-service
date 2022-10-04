package com.project.myservice.common.exception

abstract class BaseException : RuntimeException {

    val errorCode: ErrorCode

    constructor(errorCode: ErrorCode) : super(errorCode.errorMessage) {
        this.errorCode = errorCode
    }

    constructor(message: String, errorCode: ErrorCode) : super(message) {
        this.errorCode = errorCode
    }

    constructor(message: String, errorCode: ErrorCode, cause: Throwable)
            : super(message, cause) {
        this.errorCode = errorCode
    }
}