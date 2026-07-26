package com.movie.curator.auth.verifier

import com.movie.curator.auth.model.AuthProvider
import com.movie.curator.auth.model.SocialUserInfo


/**
 * 프로바이더가 발급한 토큰을 검증하고 그 토큰이 주장하는 신원을 추출한다.
 * [AuthProvider]별로 구현체가 하나씩 존재하며, [com.example.board.auth.service.OAuthLoginService]가
 * [provider]에 따라 디스패치한다.
 */
interface SocialTokenVerifier {
    fun provider(): AuthProvider

    /** [token]을 검증하고 주장된 신원을 반환한다. 유효하지 않으면 ApiException(UNAUTHORIZED)을 던진다. */
    fun verify(token: String): SocialUserInfo
}