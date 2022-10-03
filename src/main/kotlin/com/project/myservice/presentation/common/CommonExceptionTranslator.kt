package com.project.myservice.presentation.common

import com.project.myservice.common.exception.BaseException
import com.project.myservice.common.exception.ErrorCode
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class CommonExceptionTranslator {

    companion object {
        private val logger = LoggerFactory.getLogger(CommonExceptionTranslator::class.java)
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = [MethodArgumentNotValidException::class])
    fun onMethodArgumentNotValidException(e: MethodArgumentNotValidException): CommonResponse<Void> {
        val errorMessage = e.fieldError?.let {
            "요청한 '${it.field}' 값이 올바르지 않습니다. 입력 값: '${it.rejectedValue}'. ${it.defaultMessage}"
        } ?: ErrorCode.COMMON_INVALID_PARAMETER.errorMessage
        logger.warn(errorMessage, e)

        return CommonResponse.fail(errorMessage, ErrorCode.COMMON_INVALID_PARAMETER.name)
    }

    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(value = [BaseException::class])
    fun onBaseException(e: BaseException): CommonResponse<Void> {
        logger.warn(e.message, e)

        return CommonResponse.fail(e.message ?: e.errorCode.errorMessage, e.errorCode.name)
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = [Exception::class])
    fun onException(e: Exception): CommonResponse<Void> {
        logger.error(e.message, e)

        return CommonResponse.fail(ErrorCode.COMMON_SYSTEM_ERROR)
    }
}