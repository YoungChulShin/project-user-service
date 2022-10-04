package com.project.myservice.common.exception

class RoleNotFoundException : BaseException {

    constructor(): super(ErrorCode.ROLE_NOT_FOUND)

    constructor(message: String): super(message, ErrorCode.ROLE_NOT_FOUND)
}