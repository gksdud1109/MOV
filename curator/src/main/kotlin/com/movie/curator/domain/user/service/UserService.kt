package com.movie.curator.domain.user.service

import com.movie.curator.domain.auth.repository.RefreshTokenRepository
import com.movie.curator.domain.user.dto.MyProfileResponse
import com.movie.curator.domain.user.dto.UpdateUserRequest
import com.movie.curator.domain.user.dto.UserProfileResponse
import com.movie.curator.domain.user.model.User
import com.movie.curator.domain.user.repository.UserRepository
import com.movie.curator.global.exception.ApiException
import com.movie.curator.global.exception.ErrorCode
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(
    private val users: UserRepository,
    private val refreshTokens: RefreshTokenRepository,
) {

    fun getById(id: Long): User {
        return users.findByIdAndDeletedFalse(id)
            ?: throw ApiException(ErrorCode.NOT_FOUND, "User $id not found")
    }

    fun requireActive(id: Long): User = getById(id)

    private fun getAnyById(id: Long): User {
        return users.findById(id).orElseThrow {
            ApiException(ErrorCode.NOT_FOUND, "User $id not found")
        }
    }

    fun getByUsername(username: String): User {
        val user = users.findByUsername(username)
            ?: throw ApiException(ErrorCode.NOT_FOUND, "User '$username' not found")
        if (user.deleted) throw ApiException(ErrorCode.NOT_FOUND, "User '$username' not found")
        return user
    }

    /** 현재 로그인 사용자의 프로필 + 작성 답변/댓글 요약을 조회한다. */
    fun getMyProfile(currentUserId: Long): MyProfileResponse {
        val user = getById(currentUserId)
        return MyProfileResponse.from(
            user = user,
        )
    }

    /** id로 공개 프로필 + 현재 사용자의 팔로우 여부를 조회한다. */
    fun getProfile(viewerId: Long, targetId: Long): UserProfileResponse =
        toProfile(viewerId, getById(targetId))

    /** username으로 공개 프로필 + 현재 사용자의 팔로우 여부를 조회한다. */
    fun getProfileByUsername(viewerId: Long, username: String): UserProfileResponse =
        toProfile(viewerId, getByUsername(username))

    private fun toProfile(viewerId: Long, target: User): UserProfileResponse {
        val targetId = target.requireId()
        return UserProfileResponse.from(
            user = target,
        )
    }

    @Transactional
    fun updateMe(currentUserId: Long, req: UpdateUserRequest): User {
        val user = getById(currentUserId)
        user.update(nickname = req.nickname, email = null)
        return user
    }

    @Transactional
    fun deleteMe(currentUserId: Long) {
        val user = getAnyById(currentUserId)
        if (user.deleted) return
        user.markDeleted()
        refreshTokens.revokeAllByUserId(currentUserId)
    }

    /** 작성자 닉네임 조회. 사용자가 없어도 throw하지 않고 플레이스홀더를 반환한다(응답 보강용). */
    fun nicknameOf(userId: Long): String =
        users.findByIdAndDeletedFalse(userId)?.nickname ?: "(알 수 없음)"

    /** 여러 작성자 닉네임을 한 번에 조회한다(목록 응답의 N+1 방지용). 없는 id는 결과에서 누락된다. */
    fun nicknamesOf(userIds: Collection<Long>): Map<Long, String> {
        val ids = userIds.toSet()
        if (ids.isEmpty()) return emptyMap()
        return users.findAllByIdInAndDeletedFalse(ids).associate { it.requireId() to it.nickname }
    }

    companion object {
        private const val PROFILE_CONTENT_LIMIT = 20
    }
}
