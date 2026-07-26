package com.movie.curator.global.response

import org.springframework.http.HttpStatus

data class ApiResponse<T>(
    val code: String,
    val message: String,
    val data: T? = null,
) {

    class Builder<T> {
        var code: HttpStatus = HttpStatus.OK
        var message: String by DelegateUtil.notNullWithDefault { code.reasonPhrase }
        var data: T? = null

        fun build(): ApiResponse<T>{
            return ApiResponse(
                code = code.value().toString(),
                message = message,
                data = data
            )
        }
    }

    constructor(
        status: HttpStatus,
        message: String = status.reasonPhrase,
    ) : this(
        code = status.value().toString(),
        message = message,
    )

    companion object {
        fun <T> ok(data: T?): ApiResponse<T> = ApiResponse(code = "200", message = "OK", data = data)

        fun <T> badRequest(message: String = HttpStatus.BAD_REQUEST.reasonPhrase): ApiResponse<T> =
            ApiResponse(status = HttpStatus.BAD_REQUEST, message = message)

        fun <T> response(status: HttpStatus, message: String): ApiResponse<T> =
            ApiResponse(status = status, message = message)
    }
}
