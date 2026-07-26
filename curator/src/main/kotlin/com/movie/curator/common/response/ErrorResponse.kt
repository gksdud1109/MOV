package com.movie.curator.common.response

import com.movie.curator.common.error.BusinessException
import com.movie.curator.common.error.ErrorCode

data class ErrorResponse(
    val status: Int,
    val code: String,
    val message: String,
) {
    companion object {
        fun from(exception: BusinessException): ErrorResponse =
            from(exception.errorCode, exception.message ?: exception.errorCode.message)

        fun from(errorCode: ErrorCode): ErrorResponse =
            from(errorCode, errorCode.message)

        private fun from(
            errorCode: ErrorCode,
            message: String,
        ): ErrorResponse =
            ErrorResponse(
                status = errorCode.httpStatus.value(),
                code = errorCode.codeName(),
                message = message,
            )

        private fun ErrorCode.codeName(): String =
            when (this) {
                is Enum<*> -> name
                else -> this::class.simpleName ?: "UNKNOWN_ERROR"
            }
    }
}
