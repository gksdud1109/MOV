package com.movie.curator.domain.auth.verifier

import com.movie.curator.domain.auth.config.OAuthProperties
import com.movie.curator.domain.auth.model.AuthProvider
import com.movie.curator.domain.auth.model.SocialUserInfo
import com.movie.curator.global.exception.ApiException
import com.movie.curator.global.exception.ErrorCode
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException

/**
 * 네이버 access 토큰은 불투명(opaque)하므로 네이버 사용자 API(/v1/nid/me)를
 * Bearer로 호출해 검증하고, 응답에서 신원을 추출한다(카카오와 동일한 방식).
 */
@Component
class NaverTokenVerifier(
    private val restClient: RestClient,
    private val properties: OAuthProperties,
) : SocialTokenVerifier {

    override fun provider(): AuthProvider = AuthProvider.NAVER

    override fun verify(token: String): SocialUserInfo {
        val body = try {
            restClient.get()
                .uri(properties.naver.userInfoUri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .retrieve()
                .body(NaverUserResponse::class.java)
        } catch (ex: RestClientException) {
            throw ApiException(ErrorCode.UNAUTHORIZED, "invalid naver token", ex)
        } ?: throw ApiException(ErrorCode.UNAUTHORIZED, "invalid naver token")

        if (body.resultcode != "00") {
            throw ApiException(ErrorCode.UNAUTHORIZED, "invalid naver token")
        }
        val profile = body.response ?: throw ApiException(ErrorCode.UNAUTHORIZED, "invalid naver token")
        val id = profile.id ?: throw ApiException(ErrorCode.UNAUTHORIZED, "invalid naver token")
        return SocialUserInfo(
            provider = AuthProvider.NAVER,
            providerUserId = id,
            email = profile.email,
            nickname = profile.nickname ?: profile.name,
        )
    }

    /** 네이버 응답에서 필요한 일부만 매핑한다. */
    data class NaverUserResponse(
        val resultcode: String?,
        val message: String?,
        val response: NaverProfile?,
    ) {
        data class NaverProfile(
            val id: String?,
            val nickname: String?,
            val name: String?,
            val email: String?,
        )
    }
}
