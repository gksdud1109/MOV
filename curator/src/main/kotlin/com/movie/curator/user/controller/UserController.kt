package com.movie.curator.user.controller

import com.movie.curator.global.response.ApiResponse
import com.movie.curator.user.controller.api.UserApi
import com.movie.curator.user.dto.MyProfileResponse
import com.movie.curator.user.dto.UpdateUserRequest
import com.movie.curator.user.dto.UserProfileResponse
import com.movie.curator.user.dto.UserResponse
import com.movie.curator.user.service.UserService
import com.movie.curator.global.web.CurrentUserId
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 사용자 API.
 *
 * - `/me`: 인증된 현재 사용자의 내 계정 조회/수정/탈퇴
 * - `/{id}`, `/by-username/{username}`: 다른 사용자 공개 프로필 조회(현재 사용자의 팔로우 여부 포함)
 *
 * 회원 생성은 로그인(소셜/dev)이 자동 처리하므로 생성 엔드포인트는 없다.
 */
@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService,
) : UserApi {

    /** 현재 로그인 사용자의 프로필을 조회한다. */
    @GetMapping("/me")
    override fun getMe(@CurrentUserId currentUserId: Long): ApiResponse<MyProfileResponse> =
        ApiResponse.ok(userService.getMyProfile(currentUserId))

    /** 현재 로그인 사용자의 프로필을 수정한다. */
    @PatchMapping("/me")
    override fun updateMe(
        @CurrentUserId currentUserId: Long,
        @Valid @RequestBody req: UpdateUserRequest,
    ): ApiResponse<UserResponse> = ApiResponse.ok(UserResponse.from(userService.updateMe(currentUserId, req)))

    /** 현재 로그인 사용자를 탈퇴 처리하고 발급된 refresh token을 폐기한다. */
    @DeleteMapping("/me")
    override fun deleteMe(@CurrentUserId currentUserId: Long): ApiResponse<Unit> {
        userService.deleteMe(currentUserId)
        return ApiResponse.ok<Unit>(null)
    }

    /** id로 특정 사용자의 공개 프로필을 조회한다(현재 사용자의 팔로우 여부 포함). */
    @GetMapping("/{id}")
    override fun getById(
        @CurrentUserId currentUserId: Long,
        @PathVariable id: Long,
    ): ApiResponse<UserProfileResponse> = ApiResponse.ok(userService.getProfile(currentUserId, id))

    /** username으로 특정 사용자의 공개 프로필을 조회한다(현재 사용자의 팔로우 여부 포함). */
    @GetMapping("/by-username/{username}")
    override fun getByUsername(
        @CurrentUserId currentUserId: Long,
        @PathVariable username: String,
    ): ApiResponse<UserProfileResponse> = ApiResponse.ok(userService.getProfileByUsername(currentUserId, username))
}
