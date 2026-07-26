package com.movie.curator.domain.user.dto

import com.movie.curator.domain.user.model.User
import java.time.Instant

data class UserResponse(
    val id: Long,
    val username: String,
    val nickname: String,
    val email: String?,
    val createdAt: Instant,
) {
    companion object {
        fun from(user: User): UserResponse = UserResponse(
            id = user.requireId(),
            username = user.username,
            nickname = user.nickname,
            email = user.email,
            createdAt = user.createdAt,
        )
    }
}
