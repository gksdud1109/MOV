package com.movie.curator.domain.auth.controller.api

import com.movie.curator.domain.auth.dto.AuthRequest
import com.movie.curator.domain.auth.dto.AuthResponse
import com.movie.curator.global.openapi.ApiErrorCode
import com.movie.curator.global.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody

@Tag(name = "Auth API", description = "소셜 로그인, 토큰 재발급, 로그아웃 API")
interface AuthApi {

    @Operation(summary = "소셜 로그인(토큰)", description = "프론트가 받은 프로바이더 토큰으로 로그인한다. google(ID token), kakao/naver(access token) 지원.")
    @ApiErrorCode("NOT_FOUND", "UNAUTHORIZED", "VALIDATION_FAILED")
    fun login(
        @Parameter(description = "소셜 로그인 제공자(google, kakao, naver)", example = "kakao")
        @PathVariable provider: String,
        @Valid @RequestBody req: AuthRequest.SocialLoginRequest,
    ): ApiResponse<AuthResponse.LoginResponse>

    @Operation(summary = "토큰 재발급", description = "refresh token을 검증하고 새 access/refresh token 쌍으로 회전한다.")
    @ApiErrorCode("UNAUTHORIZED", "VALIDATION_FAILED")
    fun refresh(
        @Valid @RequestBody req: AuthRequest.RefreshRequest,
    ): ApiResponse<AuthResponse.TokenResponse>

    @Operation(summary = "로그아웃", description = "refresh token을 폐기해 이후 재발급을 막는다.")
    @ApiErrorCode("VALIDATION_FAILED")
    fun logout(
        @Valid @RequestBody req: AuthRequest.LogoutRequest,
    ): ApiResponse<Unit>
}
