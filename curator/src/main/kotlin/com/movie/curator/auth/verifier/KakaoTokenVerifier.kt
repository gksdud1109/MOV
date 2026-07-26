package com.movie.curator.auth.verifier

import com.fasterxml.jackson.annotation.JsonProperty
import com.movie.curator.auth.config.OAuthProperties
import com.movie.curator.auth.model.AuthProvider
import com.movie.curator.auth.model.SocialUserInfo
import com.movie.curator.global.exception.ApiException
import com.movie.curator.global.exception.ErrorCode
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException

/**
 * 카카오 access 토큰은 불투명(opaque)하므로 카카오 사용자 API(/v2/user/me)를
 * Bearer로 호출해 검증하고, 응답에서 신원을 추출한다.
 */
@Component
class KakaoTokenVerifier(
    private val restClient: RestClient,
    private val properties: OAuthProperties,
) : SocialTokenVerifier {

    override fun provider(): AuthProvider = AuthProvider.KAKAO

    override fun verify(token: String): SocialUserInfo {
        val response = try {
            restClient.get()
                .uri(properties.kakao.userInfoUri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .retrieve()
                .body(KakaoUserResponse::class.java)
        } catch (ex: RestClientException) {
            throw ApiException(ErrorCode.UNAUTHORIZED, "invalid kakao token", ex)
        } ?: throw ApiException(ErrorCode.UNAUTHORIZED, "invalid kakao token")

        val id = response.id ?: throw ApiException(ErrorCode.UNAUTHORIZED, "invalid kakao token")
        val account = response.kakaoAccount
        return SocialUserInfo(
            provider = AuthProvider.KAKAO,
            providerUserId = id.toString(),
            email = account?.email,
            nickname = account?.profile?.nickname,
        )
    }

    /** 카카오 응답에서 필요한 일부만 매핑한다. */
    data class KakaoUserResponse(
        val id: Long?,
        @JsonProperty("kakao_account") val kakaoAccount: KakaoAccount?,
    ) {
        data class KakaoAccount(
            val email: String?,
            val profile: Profile?,
        ) {
            data class Profile(val nickname: String?)
        }
    }
}
