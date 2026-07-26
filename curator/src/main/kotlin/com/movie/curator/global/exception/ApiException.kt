package com.movie.curator.global.exception

/**
 * [GlobalExceptionHandler]를 통해 안정적인 HTTP 응답으로 변환되는 도메인 예외.
 */
class ApiException(
    val code: ErrorCode,
    message: String? = null,
    cause: Throwable? = null,
): RuntimeException(message ?: code.defaultMessage, cause)