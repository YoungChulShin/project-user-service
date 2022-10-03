package com.project.myservice.presentation.common

import com.project.myservice.common.exception.ErrorCode

class CommonResponse<T>(
    val result: Result,
    val data: T? = null,
    val errorCode: String? = null,
    val message: String? = null,
) {

    companion object {
        fun success(): CommonResponse<Void> =
            CommonResponse(
                result = Result.SUCCESS
            )

        fun <T> success(data: T): CommonResponse<T> =
            CommonResponse(
                result = Result.SUCCESS,
                data = data
            )

        fun fail(errorCode: ErrorCode): CommonResponse<Void> =
            CommonResponse(
                result = Result.FAIL,
                errorCode = errorCode.name,
                message = errorCode.errorMessage
            )

        fun fail(errorCode: String, errorMessage: String): CommonResponse<Void> =
            CommonResponse(
                result = Result.FAIL,
                errorCode = errorCode,
                message = errorMessage
            )
    }
}

enum class Result {
    SUCCESS,
    FAIL
}