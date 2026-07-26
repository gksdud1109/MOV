package com.movie.curator.domain.auth.model


/**
 * 검증된 프로바이더 토큰에서 추출한 정규화된 신원 정보. `email`/`nickname`은
 * 베스트 에포트(best-effort)로 제공되며, 사용자의 동의/스코프에 따라 프로바이더가
 * 생략할 수 있다.
 */
data class SocialUserInfo(
    val provider: AuthProvider,
    val providerUserId: String,
    val email: String?,
    val nickname: String?,
)
