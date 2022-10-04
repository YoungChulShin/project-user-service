package com.project.myservice.common.exception

class UserNotFoundException : BaseException {

    constructor(): super(ErrorCode.USER_NOT_FOUND)

    constructor(message: String): super(message, ErrorCode.USER_NOT_FOUND)
}