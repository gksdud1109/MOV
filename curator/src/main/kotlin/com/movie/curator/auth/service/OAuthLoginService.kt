package com.movie.curator.auth.service

import com.movie.curator.auth.dto.AuthResponse
import com.movie.curator.auth.model.AuthProvider
import com.movie.curator.auth.model.SocialAccount
import com.movie.curator.auth.model.SocialUserInfo
import com.movie.curator.auth.repository.SocialAccountRepository
import com.movie.curator.auth.verifier.SocialTokenVerifier
import com.movie.curator.user.dto.UserResponse
import com.movie.curator.user.model.User
import com.movie.curator.user.repository.UserRepository
import com.movie.curator.global.exception.ApiException
import com.movie.curator.global.exception.ErrorCode
import com.movie.curator.global.security.JwtTokenProvider
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 소셜 로그인을 조율한다. 프로바이더 토큰을 검증하고, 소셜 신원에 대한 로컬 사용자를
 * 연결(또는 자동 생성)한 뒤, 애플리케이션 자체의 액세스 토큰과 리프레시 토큰을 발급한다.
 *
 * 프론트가 받은 프로바이더 토큰(Google ID token, Kakao/Naver access token)을 [login]으로 검증한다.
 */
@Service
@Transactional(readOnly = true)
class OAuthLoginService(
    private val verifiers: List<SocialTokenVerifier>,
    private val socialAccounts: SocialAccountRepository,
    private val users: UserRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val refreshTokenService: RefreshTokenService,
) {

    // 생성자가 아닌 lazy로 초기화하여, 모든 verifier 빈이 완전히 초기화된 후 provider()를
    // 읽도록 한다. Verifier 빈은 싱글톤으로 provider()가 고정값이므로 한 번만 평가된다.
    private val verifierByProvider: Map<AuthProvider, SocialTokenVerifier> by lazy {
        verifiers.associateBy { it.provider() }
    }

    /** 토큰 기반 로그인(Google ID token, Kakao/Naver access token). */
    @Transactional
    fun login(provider: AuthProvider, providerToken: String): AuthResponse.LoginResponse {
        val verifier = verifierByProvider[provider]
            ?: throw ApiException(ErrorCode.NOT_FOUND, "unknown provider")
        val info = verifier.verify(providerToken)

        val user = resolveUser(provider, info)
        val userId = user.requireId()

        return AuthResponse.LoginResponse.from(
            accessToken = jwtTokenProvider.issueAccessToken(userId),
            refreshToken = refreshTokenService.issue(userId),
            user = UserResponse.from(user),
        )
    }

    /** 소셜 신원에 연결된 사용자를 찾고, 없으면 새로 생성(자동 프로비저닝)한다. */
    private fun resolveUser(provider: AuthProvider, info: SocialUserInfo): User {
        val existing = socialAccounts.findByProviderAndProviderUserId(provider, info.providerUserId)
        if (existing != null) {
            val user = users.findById(existing.userId).orElseThrow {
                ApiException(ErrorCode.NOT_FOUND, "User ${existing.userId} not found")
            }
            if (user.deleted) {
                throw ApiException(ErrorCode.UNAUTHORIZED, "User ${existing.userId} is inactive")
            }
            return user
        }
        val user = users.save(
            User(
                username = uniqueUsername(provider, info.providerUserId),
                nickname = info.nickname ?: "${provider.name.lowercase()} user",
                email = info.email,
            ),
        )
        val userId = user.requireId()
        socialAccounts.save(SocialAccount(provider = provider, providerUserId = info.providerUserId, userId = userId))
        return user
    }

    private fun uniqueUsername(provider: AuthProvider, providerUserId: String): String {
        val base = "${provider.name.lowercase()}_$providerUserId".take(MAX_USERNAME_LENGTH)
        if (!users.existsByUsername(base)) return base
        var suffix = 1
        while (true) {
            val tag = suffix.toString()
            val candidate = base.take(MAX_USERNAME_LENGTH - tag.length) + tag
            if (!users.existsByUsername(candidate)) return candidate
            suffix++
        }
    }

    private companion object {
        const val MAX_USERNAME_LENGTH = 50
    }
}
