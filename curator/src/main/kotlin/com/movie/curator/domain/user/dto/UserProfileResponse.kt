package com.movie.curator.domain.user.dto

import com.movie.curator.domain.user.model.User
import java.time.Instant

/**
 * 공개 프로필 조회 응답. 현재 로그인 사용자가 이 사용자를 팔로우 중인지(`following`)와
 * 이 사용자가 참여(답변)한 질문 요약 + 작성 답변 요약을 담는다.
 */
data class UserProfileResponse(
    val id: Long,
    val username: String,
    val nickname: String,
    val email: String?,
    val createdAt: Instant,
) {
    companion object {
        fun from(
            user: User,
        ): UserProfileResponse = UserProfileResponse(
            id = user.requireId(),
            username = user.username,
            nickname = user.nickname,
            email = user.email,
            createdAt = user.createdAt,
        )
    }
}
