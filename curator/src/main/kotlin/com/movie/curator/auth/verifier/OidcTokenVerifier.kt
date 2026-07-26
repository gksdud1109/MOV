package com.movie.curator.auth.verifier

import com.movie.curator.auth.model.SocialUserInfo
import com.movie.curator.global.exception.ApiException
import com.movie.curator.global.exception.ErrorCode
import org.springframework.security.oauth2.jwt.JwtException
import org.springframework.security.oauth2.jwt.JwtValidators
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder

/**
 * OIDC ID 토큰(JWT)을 발급하는 프로바이더(구글/애플)의 공통 검증 로직.
 * provider JWKS로 서명을 검증하고 issuer/audience를 확인한 뒤 신원을 추출한다.
 *
 * 디코더는 빈 생성 시 만들어지지만 JWKS는 첫 검증 때 lazy로 가져오므로, 컨텍스트 로딩에는
 * 네트워크가 필요 없다.
 */
abstract class OidcTokenVerifier(
    issuer: String,
    jwksUri: String,
    private val clientId: String,
) : SocialTokenVerifier {

    private val decoder: NimbusJwtDecoder = NimbusJwtDecoder.withJwkSetUri(jwksUri).build().apply {
        setJwtValidator(JwtValidators.createDefaultWithIssuer(issuer))
    }

    override fun verify(token: String): SocialUserInfo {
        if (clientId.isBlank()) {
            throw ApiException(ErrorCode.UNAUTHORIZED, "${provider().name.lowercase()} login is not configured")
        }
        val jwt = try {
            decoder.decode(token)
        } catch (ex: JwtException) {
            throw ApiException(ErrorCode.UNAUTHORIZED, "invalid id token", ex)
        }
        if (clientId !in jwt.audience) {
            throw ApiException(ErrorCode.UNAUTHORIZED, "unexpected audience")
        }
        return SocialUserInfo(
            provider = provider(),
            providerUserId = jwt.subject,
            email = jwt.getClaimAsString("email"),
            nickname = jwt.getClaimAsString("name"),
        )
    }
}
