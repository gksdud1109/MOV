package com.movie.curator.auth.controller

import com.movie.curator.auth.controller.api.AuthApi
import com.movie.curator.auth.dto.AuthRequest
import com.movie.curator.auth.dto.AuthResponse
import com.movie.curator.auth.model.AuthProvider
import com.movie.curator.auth.service.OAuthLoginService
import com.movie.curator.auth.service.RefreshTokenService
import com.movie.curator.global.response.ApiResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val oAuthLoginService: OAuthLoginService,
    private val refreshTokenService: RefreshTokenService,
) : AuthApi {

    @PostMapping("/{provider}/login")
    override fun login(
        @PathVariable provider: String,
        @Valid @RequestBody req: AuthRequest.SocialLoginRequest,
    ): ApiResponse<AuthResponse.LoginResponse> {
        val result = oAuthLoginService.login(AuthProvider.fromPath(provider), req.token)
        return ApiResponse.ok(result)
    }

    @PostMapping("/refresh")
    override fun refresh(
        @Valid @RequestBody req: AuthRequest.RefreshRequest,
    ): ApiResponse<AuthResponse.TokenResponse> {
        val result = refreshTokenService.rotate(req.refreshToken)
        return ApiResponse.ok(result)
    }

    @PostMapping("/logout")
    override fun logout(
        @Valid @RequestBody req: AuthRequest.LogoutRequest,
    ): ApiResponse<Unit> {
        refreshTokenService.logout(req.refreshToken)
        return ApiResponse.ok<Unit>(null)
    }
}
