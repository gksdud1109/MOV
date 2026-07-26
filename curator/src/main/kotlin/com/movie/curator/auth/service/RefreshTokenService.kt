package com.movie.curator.auth.service

import com.movie.curator.auth.dto.AuthResponse
import com.movie.curator.auth.model.RefreshToken
import com.movie.curator.auth.repository.RefreshTokenRepository
import com.movie.curator.user.repository.UserRepository
import com.movie.curator.global.exception.ApiException
import com.movie.curator.global.exception.ErrorCode
import com.movie.curator.global.security.JwtProperties
import com.movie.curator.global.security.JwtTokenProvider
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

/**
 * 리프레시 토큰을 영속/회전/폐기한다. 각 토큰은 [RefreshToken] 행과 매핑되는 `jti`를 담으며,
 * 회전 시 기존 토큰을 폐기하고 새 토큰 쌍을 발급한다.
 */
@Service
@Transactional
class RefreshTokenService(
    private val refreshTokens: RefreshTokenRepository,
    private val users: UserRepository,
    private val jwtTokenProvider: JwtTokenProvider,
    private val jwtProperties: JwtProperties,
) {

    /** [userId]에 대한 리프레시 토큰을 발급·영속하고 인코딩된 JWT를 반환한다. */
    fun issue(userId: Long): String {
        requireActiveUser(userId)
        val jti = UUID.randomUUID().toString()
        val expiresAt = Instant.now().plus(jwtProperties.refreshTokenTtl)
        refreshTokens.save(RefreshToken(userId = userId, jti = jti, expiresAt = expiresAt))
        return jwtTokenProvider.issueRefreshToken(userId, jti)
    }

    /**
     * 리프레시 토큰을 검증·회전한다: 제시된 토큰을 폐기하고 새 토큰 쌍을 발급한다.
     *
     * noRollbackFor: 재사용 탐지(폐기된 토큰 재제시 = 탈취 신호) 시 revokeAllByUserId로
     * 패밀리 전체를 폐기한 뒤 401을 던지는데, 기본 롤백 정책이면 그 폐기까지 롤백되어
     * 방어가 무력화된다. ApiException은 "의도된 거부"이므로 커밋한다(거부 경로에서
     * 이 폐기 외의 쓰기는 발생하지 않는다).
     */
    @Transactional(noRollbackFor = [ApiException::class])
    fun rotate(refreshTokenValue: String): AuthResponse.TokenResponse {
        val parsed = parseRefresh(refreshTokenValue)
        val jti = parsed.jti ?: throw ApiException(ErrorCode.UNAUTHORIZED, "invalid refresh token")
        val stored = refreshTokens.findByJtiForUpdate(jti)
            ?: throw ApiException(ErrorCode.UNAUTHORIZED, "invalid refresh token")
        if (stored.userId != parsed.userId ||
            stored.revoked ||
            stored.expiresAt.isBefore(Instant.now())
        ) {
            refreshTokens.revokeAllByUserId(stored.userId)
            throw ApiException(ErrorCode.UNAUTHORIZED, "invalid refresh token")
        }
        requireActiveUser(parsed.userId)
        stored.revoke()
        val accessToken = jwtTokenProvider.issueAccessToken(parsed.userId)
        val refreshToken = issue(parsed.userId)
        return AuthResponse.TokenResponse(accessToken = accessToken, refreshToken = refreshToken)
    }

    /** 알려진 행으로 해석되면 리프레시 토큰을 폐기한다. 멱등하다. */
    fun logout(refreshTokenValue: String) {
        val parsed = try {
            jwtTokenProvider.parse(refreshTokenValue)
        } catch (ex: Exception) {
            return
        }
        val jti = parsed.jti ?: return
        refreshTokens.findByJti(jti)?.revoke()
    }

    private fun parseRefresh(refreshTokenValue: String): JwtTokenProvider.ParsedToken {
        val parsed = try {
            jwtTokenProvider.parse(refreshTokenValue)
        } catch (ex: Exception) {
            throw ApiException(ErrorCode.UNAUTHORIZED, "invalid refresh token", ex)
        }
        if (parsed.type != JwtTokenProvider.TYPE_REFRESH) {
            throw ApiException(ErrorCode.UNAUTHORIZED, "invalid refresh token")
        }
        return parsed
    }

    private fun requireActiveUser(userId: Long) {
        if (!users.existsByIdAndDeletedFalse(userId)) {
            throw ApiException(ErrorCode.UNAUTHORIZED, "invalid refresh token")
        }
    }
}
