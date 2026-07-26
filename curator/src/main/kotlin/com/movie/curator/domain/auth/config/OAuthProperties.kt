package com.movie.curator.domain.auth.config

import org.springframework.boot.context.properties.ConfigurationProperties


/**
 * `app.oauth.*` 설정을 바인딩한다. 구글이 현재 환경에 설정되지 않은 경우 `client-id`가
 * 공백일 수 있으며, 해당 verifier는 설정이 완료될 때까지 로그인을 거부한다.
 * 카카오/네이버는 access token으로 검증하므로 서버 측 client-id가 필요 없다.
 */
@ConfigurationProperties(prefix = "app.oauth")
data class OAuthProperties(
    val kakao: Kakao,
    val naver: Naver,
    val google: Oidc,
) {
    data class Kakao(
        val userInfoUri: String,
    )

    data class Naver(
        val userInfoUri: String,
    )

    data class Oidc(
        val clientId: String = "",
        val issuer: String,
        val jwksUri: String,
    )
}
