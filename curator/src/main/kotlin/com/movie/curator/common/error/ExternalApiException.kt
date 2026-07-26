package com.movie.curator.common.error

import org.springframework.http.HttpStatus

/**
 * LLM 등 외부 시스템의 오류를 내부 오류 계약으로 변환할 때 사용한다.
 */
class ExternalApiException(
    val code: String,
    val httpStatus: HttpStatus,
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause) {

    constructor(
        code: String,
        errorCode: ErrorCode,
        cause: Throwable? = null,
    ) : this(code, errorCode.httpStatus, errorCode.message, cause)
}
