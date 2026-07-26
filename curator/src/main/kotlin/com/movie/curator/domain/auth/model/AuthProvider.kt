package com.movie.curator.domain.auth.model

import com.movie.curator.global.exception.ApiException
import com.movie.curator.global.exception.ErrorCode


/**
 * 지원하는 소셜 로그인 프로바이더. `/api/auth/{provider}/login`의 경로 세그먼트는
 * [fromPath]를 통해 이 값 중 하나로 변환된다.
 */
enum class AuthProvider {
    KAKAO,
    GOOGLE,
    NAVER,
    ;

    companion object {
        fun fromPath(s: String): AuthProvider =
            entries.firstOrNull { it.name.equals(s, ignoreCase = true) }
                ?: throw ApiException(ErrorCode.NOT_FOUND, "unknown provider")
    }
}
