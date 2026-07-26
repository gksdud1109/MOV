package com.movie.curator.domain.user.controller.api

import com.movie.curator.domain.user.dto.UpdateUserRequest
import com.movie.curator.domain.user.dto.MyProfileResponse
import com.movie.curator.domain.user.dto.UserProfileResponse
import com.movie.curator.domain.user.dto.UserResponse
import com.movie.curator.global.openapi.ApiErrorCode
import com.movie.curator.global.response.ApiResponse
import com.movie.curator.global.web.CurrentUserId
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody

@Tag(name = "User API", description = "내 계정과 사용자 공개 프로필 조회 API")
@SecurityRequirement(name = "bearerAuth")
interface UserApi {

    @Operation(summary = "내 프로필 조회", description = "현재 access token의 사용자 프로필과 참여한 질문·작성 답변/댓글 요약을 조회한다.")
    @ApiErrorCode("UNAUTHORIZED", "NOT_FOUND")
    fun getMe(
        @Parameter(hidden = true) @CurrentUserId currentUserId: Long,
    ): ApiResponse<MyProfileResponse>

    @Operation(summary = "내 프로필 수정", description = "현재 access token의 사용자 닉네임을 수정한다.")
    @ApiErrorCode("UNAUTHORIZED", "NOT_FOUND", "VALIDATION_FAILED")
    fun updateMe(
        @Parameter(hidden = true) @CurrentUserId currentUserId: Long,
        @Valid @RequestBody req: UpdateUserRequest,
    ): ApiResponse<UserResponse>

    @Operation(summary = "내 계정 탈퇴", description = "현재 access token의 사용자를 탈퇴 처리하고 refresh token을 폐기한다.")
    @ApiErrorCode("UNAUTHORIZED", "NOT_FOUND")
    fun deleteMe(
        @Parameter(hidden = true) @CurrentUserId currentUserId: Long,
    ): ApiResponse<Unit>

    @Operation(
        summary = "사용자 프로필 조회",
        description = "특정 사용자 id의 공개 프로필을 조회한다. 현재 로그인 사용자의 팔로우 여부(following)와 참여한 질문·작성 답변 요약을 포함한다.",
    )
    @ApiErrorCode("UNAUTHORIZED", "NOT_FOUND")
    fun getById(
        @Parameter(hidden = true) @CurrentUserId currentUserId: Long,
        @Parameter(description = "사용자 ID", example = "7")
        @PathVariable id: Long,
    ): ApiResponse<UserProfileResponse>

    @Operation(
        summary = "username으로 사용자 조회",
        description = "특정 username의 공개 프로필을 조회한다. 현재 로그인 사용자의 팔로우 여부(following)와 참여한 질문·작성 답변 요약을 포함한다.",
    )
    @ApiErrorCode("UNAUTHORIZED", "NOT_FOUND")
    fun getByUsername(
        @Parameter(hidden = true) @CurrentUserId currentUserId: Long,
        @Parameter(description = "사용자 username", example = "kakao_4567")
        @PathVariable username: String,
    ): ApiResponse<UserProfileResponse>
}
