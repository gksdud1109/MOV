package com.movie.curator.global.security

import com.nimbusds.jose.jwk.source.ImmutableSecret
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.OAuth2TokenValidator
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtValidators
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import javax.crypto.spec.SecretKeySpec

/**
 * 자체 발급하는 액세스/리프레시 토큰의 HMAC(HS256) 서명 설정.
 *
 * - [jwtEncoder] : [JwtTokenProvider]가 토큰을 발급할 때 사용한다.
 * - [jwtDecoder] : 리소스 서버가 요청을 인증할 때 사용하며, `typ` 클레임이 `access`가 아닌
 *   토큰을 추가로 거부한다. (리프레시 토큰은 [JwtTokenProvider] 내부의 별도 디코더에서 파싱)
 */
@Configuration
@EnableConfigurationProperties(JwtProperties::class)
class JwtConfig(
    private val properties: JwtProperties
) {

    init {
        require(properties.secret.toByteArray(Charsets.UTF_8).size >= MIN_SECRET_BYTES) {
            "app.jwt.secret must be at least $MIN_SECRET_BYTES bytes for HS256"
        }
    }

    private fun secretKey() =
        SecretKeySpec(properties.secret.toByteArray(Charsets.UTF_8), "HmacSHA256")

    @Bean
    fun jwtEncoder(): JwtEncoder = NimbusJwtEncoder(ImmutableSecret(secretKey()))

    @Bean
    fun jwtDecoder(): JwtDecoder {
        val decoder = NimbusJwtDecoder.withSecretKey(secretKey())
            .macAlgorithm(MacAlgorithm.HS256)
            .build()
        val accessOnly = OAuth2TokenValidator<Jwt> { jwt ->
            if (jwt.getClaimAsString("typ") == "access") {
                OAuth2TokenValidatorResult.success()
            } else {
                OAuth2TokenValidatorResult.failure(
                    OAuth2Error("invalid_token", "An access token is required", null),
                )
            }
        }
        decoder.setJwtValidator(
            DelegatingOAuth2TokenValidator(JwtValidators.createDefaultWithIssuer(properties.issuer), accessOnly),
        )
        return decoder
    }

    private companion object {
        const val MIN_SECRET_BYTES = 32
    }
}
