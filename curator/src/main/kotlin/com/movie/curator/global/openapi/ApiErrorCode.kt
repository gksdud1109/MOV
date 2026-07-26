package com.movie.curator.global.openapi

/**
 * OpenAPI Operation 에 노출할 API 오류 코드 목록.
 *
 * springdoc 표준 필드는 아니므로 [OpenApiConfig] 에서 `x-error-codes` 확장으로 변환한다.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ApiErrorCode(vararg val value: String)
