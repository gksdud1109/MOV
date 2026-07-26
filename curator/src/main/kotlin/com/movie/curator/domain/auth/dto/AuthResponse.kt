package com.movie.curator.domain.auth.dto

import com.movie.curator.domain.user.dto.UserResponse

sealed interface AuthResponse {

    data class LoginResponse(
        val accessToken: String,
        val refreshToken: String,
        val tokenType: String = "Bearer",
        val user: UserResponse,
    ) : AuthResponse {
        companion object {
            fun from(accessToken: String, refreshToken: String, user: UserResponse): LoginResponse {
                return LoginResponse(
                    accessToken = accessToken,
                    refreshToken = refreshToken,
                    user = user,
                )
            }
        }
    }

    data class TokenResponse(
        val accessToken: String,
        val refreshToken: String,
        val tokenType: String = "Bearer",
    ) : AuthResponse

}
