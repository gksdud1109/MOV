package com.movie.curator.auth.dto

import jakarta.validation.constraints.NotBlank

/**
 * 개발 전용 로그인 요청. 소셜 SDK 연동 전, 프론트 개발 기간에만 임시로 사용한다.
 * 시크릿 없이 username만 받는다(프로토타입 한정). `app.auth.dev-login.enabled=true` 일 때만 동작한다.
 */
data class DevLoginRequest(
    @field:NotBlank
    val username: String,
)
