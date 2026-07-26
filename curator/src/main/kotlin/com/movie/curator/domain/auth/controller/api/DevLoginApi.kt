package com.movie.curator.domain.auth.controller.api

import com.movie.curator.domain.auth.dto.AuthResponse
import com.movie.curator.domain.auth.dto.DevLoginRequest
import com.movie.curator.global.openapi.ApiErrorCode
import com.movie.curator.global.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.RequestBody

@Tag(name = "Dev Login API", description = "프론트 개발용 임시 로그인 API")
interface DevLoginApi {

    @Operation(
        summary = "개발용 로그인",
        description = "소셜 SDK 연동 전 개발 환경에서 username만으로 테스트용 access/refresh token을 발급한다.",
    )
    @ApiErrorCode("NOT_FOUND", "VALIDATION_FAILED")
    fun devLogin(
        @Valid @RequestBody req: DevLoginRequest,
    ): ApiResponse<AuthResponse.LoginResponse>
}
