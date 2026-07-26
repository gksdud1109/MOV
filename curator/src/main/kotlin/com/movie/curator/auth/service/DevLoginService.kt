package com.movie.curator.auth.service

import com.movie.curator.auth.dto.AuthResponse
import com.movie.curator.user.dto.UserResponse
import com.movie.curator.user.model.User
import com.movie.curator.user.repository.UserRepository
import com.movie.curator.global.security.JwtTokenProvider
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 개발 전용 로그인. username으로 사용자를 찾거나 새로 만들고, 소셜 검증 없이 실제 JWT(access/refresh)를
 * 발급한다. 발급 이후 흐름은 운영과 100% 동일하다(Bearer 토큰).
 *
 * 소셜 검증을 우회하므로 운영에서는 절대 활성화하면 안 된다 — [com.movie.curator.auth.controller.DevLoginController]
 * 가 `app.auth.dev-login.enabled` 플래그와 시크릿으로 게이팅한다.
 */
@Service
class DevLoginService(
    private val users: UserRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val refreshTokenService: RefreshTokenService,
) {

    @Transactional
    fun login(username: String): AuthResponse.LoginResponse {
        val user = users.findByUsername(username)
            ?: users.save(User(username = username, nickname = username))
        val userId = user.requireId()
        val accessToken = jwtTokenProvider.issueAccessToken(userId)
        val refreshToken = refreshTokenService.issue(userId)
        return AuthResponse.LoginResponse.from(
            accessToken = accessToken,
            refreshToken = refreshToken,
            user = UserResponse.from(user),
        )
    }
}
