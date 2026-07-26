package com.movie.curator.auth.dto

import jakarta.validation.constraints.NotBlank

sealed interface AuthRequest {

    data class SocialLoginRequest(
        @field:NotBlank
        val token: String,
    )

    data class RefreshRequest(
        @field:NotBlank
        val refreshToken: String,
    )

    data class LogoutRequest(
        @field:NotBlank
        val refreshToken: String,
    )
}