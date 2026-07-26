package com.movie.curator.domain.auth.controller

import com.movie.curator.domain.auth.controller.api.DevLoginApi
import com.movie.curator.domain.auth.dto.AuthResponse
import com.movie.curator.domain.auth.dto.DevLoginRequest
import com.movie.curator.domain.auth.service.DevLoginService
import com.movie.curator.global.exception.ApiException
import com.movie.curator.global.exception.ErrorCode
import com.movie.curator.global.response.ApiResponse
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 개발 전용 로그인 엔드포인트. 소셜 SDK 연동 전 프론트 개발 기간에만 임시로 쓴다.
 *
 * - `app.auth.dev-login.enabled=false`(기본)면 404로 응답해 사실상 비활성.
 * - 활성 시 시크릿 없이 username만으로 실제 JWT를 발급한다(프로토타입 한정).
 * - 인증 경로(`/api/v1/auth` 하위)는 SecurityConfig에서 permitAll 이라 토큰 없이 호출 가능.
 *
 * 운영 배포 시 반드시 enabled=false(또는 미설정)로 둘 것.
 */
@RestController
@RequestMapping("/api/v1/auth")
class DevLoginController(
    private val devLoginService: DevLoginService,
    @Value("\${app.auth.dev-login.enabled:false}") private val enabled: Boolean,
) : DevLoginApi {

    @PostMapping("/dev-login")
    override fun devLogin(@Valid @RequestBody req: DevLoginRequest): ApiResponse<AuthResponse.LoginResponse> {
        if (!enabled) {
            throw ApiException(ErrorCode.NOT_FOUND, "not found")
        }
        return ApiResponse.ok(devLoginService.login(req.username))
    }
}
