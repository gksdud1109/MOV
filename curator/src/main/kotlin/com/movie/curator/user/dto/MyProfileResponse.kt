package com.movie.curator.user.dto

import com.movie.curator.user.model.User
import java.time.Instant

data class MyProfileResponse(
    val id: Long,
    val username: String,
    val nickname: String,
    val email: String?,
    val createdAt: Instant,
) {
    companion object {
        fun from(
            user: User,
        ): MyProfileResponse = MyProfileResponse(
            id = user.requireId(),
            username = user.username,
            nickname = user.nickname,
            email = user.email,
            createdAt = user.createdAt,
        )
    }
}
