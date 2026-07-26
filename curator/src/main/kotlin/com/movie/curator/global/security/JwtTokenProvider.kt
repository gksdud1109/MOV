package com.movie.curator.global.security

import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.JwsHeader
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.security.oauth2.jwt.JwtValidators
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.time.Instant
import javax.crypto.spec.SecretKeySpec


/**
 * 애플리케이션 자체 JWT를 발급하고 파싱한다.
 *
 * - 액세스 토큰: 단기 유효, `typ=access`, `sub` 클레임에 사용자 id를 담는다.
 * - 리프레시 토큰: 장기 유효, `typ=refresh`, auth 도메인이 영속/로테이션/폐기할 수
 *   있도록 `jti`를 담는다.
 *
 * 여기서의 파싱은 `typ` 제한이 없는 전용 디코더를 사용하므로([JwtConfig]의 리소스
 * 서버 디코더와 달리) 리프레시 토큰도 검증할 수 있다.
 */
@Component
class JwtTokenProvider(
    private val encoder: JwtEncoder,
    private val properties: JwtProperties,
) {
    private val decoder = NimbusJwtDecoder
        .withSecretKey(SecretKeySpec(properties.secret.toByteArray(StandardCharsets.UTF_8), "HmacSHA256"))
        .macAlgorithm(MacAlgorithm.HS256)
        .build()
        .apply {
            setJwtValidator(JwtValidators.createDefaultWithIssuer(properties.issuer))
        }

    fun issueAccessToken(userId: Long): String =
        encode(userId, TYPE_ACCESS, properties.accessTokenTtl, jti = null)

    fun issueRefreshToken(userId: Long, jti: String): String =
        encode(userId, TYPE_REFRESH, properties.refreshTokenTtl, jti = jti)

    /** 토큰의 서명과 만료를 파싱하고 검증한다. 유효하지 않으면 예외를 던진다. */
    fun parse(token: String): ParsedToken {
        val jwt = decoder.decode(token)
        return ParsedToken(
            userId = jwt.subject.toLong(),
            type = jwt.getClaimAsString(CLAIM_TYPE),
            jti = jwt.id,
            expiresAt = jwt.expiresAt,
        )
    }

    private fun encode(userId: Long, type: String, ttl: Duration, jti: String?): String {
        val now = Instant.now()
        val claims = JwtClaimsSet.builder()
            .issuer(properties.issuer)
            .issuedAt(now)
            .expiresAt(now.plus(ttl))
            .subject(userId.toString())
            .claim(CLAIM_TYPE, type)
            .apply { jti?.let { id(it) } }
            .build()
        val header = JwsHeader.with(MacAlgorithm.HS256).build()
        return encoder.encode(JwtEncoderParameters.from(header, claims)).tokenValue
    }

    data class ParsedToken(
        val userId: Long,
        val type: String?,
        val jti: String?,
        val expiresAt: Instant?,
    )

    companion object {
        const val CLAIM_TYPE = "typ"
        const val TYPE_ACCESS = "access"
        const val TYPE_REFRESH = "refresh"
    }
}
