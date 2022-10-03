package com.project.myservice.common.exception

class UserAlreadyExistsException : BaseException {

    constructor(): super(ErrorCode.USER_ALREADY_EXISTS)

    constructor(message: String): super(message, ErrorCode.USER_ALREADY_EXISTS)
}