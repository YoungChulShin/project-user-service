package com.project.myservice.common.exception

class InvalidParameterException : BaseException {

    constructor(): super(ErrorCode.COMMON_INVALID_PARAMETER)

    constructor(message: String): super(message, ErrorCode.COMMON_INVALID_PARAMETER)
}