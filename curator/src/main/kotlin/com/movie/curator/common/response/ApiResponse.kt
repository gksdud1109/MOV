package com.movie.curator.common.response

import org.springframework.http.HttpStatus

/**
 * 성공한 API 응답의 공통 형식이다.
 *
 * 오류 응답은 GlobalExceptionHandler가 ErrorResponse 형식으로 별도 반환한다.
 */
data class ApiResponse<T>(
    val code: String,
    val message: String,
    val data: T? = null,
) {
    companion object {
        fun <T> ok(data: T): ApiResponse<T> =
            ApiResponse(
                code = HttpStatus.OK.value().toString(),
                message = HttpStatus.OK.reasonPhrase,
                data = data,
            )
    }
}
