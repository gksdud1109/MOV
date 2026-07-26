package com.movie.curator.common.openapi

/**
 * 해당 API가 반환할 수 있는 오류 코드를 OpenAPI 문서에 기록한다.
 *
 * springdoc 표준 필드가 아니므로 OpenApiConfig가 `x-error-codes` 확장으로 노출한다.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ApiErrorCode(vararg val value: String)
