package com.movie.curator.global.exception

import java.time.Instant

data class ErrorResponse(
    val code: String,
    val message: String,
    val timestamp: Instant = Instant.now(),
    val details: List<FieldError> = emptyList(),
) {
    data class FieldError(val field: String, val message: String)
}
