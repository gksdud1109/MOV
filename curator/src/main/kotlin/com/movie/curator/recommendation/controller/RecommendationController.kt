package com.movie.curator.recommendation.controller

import com.movie.curator.common.openapi.ApiErrorCode
import com.movie.curator.common.response.ApiResponse
import com.movie.curator.recommendation.dto.RecoRequest
import com.movie.curator.recommendation.dto.RecoResponse
import com.movie.curator.recommendation.service.RecommendationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/recommendations")
@Tag(name = "Recommendation API", description = "취향을 바탕으로 영화 5편과 추천 이유를 제공한다.")
class RecommendationController(
    private val recoService: RecommendationService,
) {

    @PostMapping
    @Operation(
        summary = "영화 추천 받기",
        description = "좋아한 영화 3편과 별로였던 영화 1편을 바탕으로 맞춤 영화 5편을 추천한다.",
    )
    @ApiErrorCode("INVALID_INPUT_VALUE", "MESSAGE_NOT_READABLE", "EXTERNAL_API_ERROR")
    fun recommend(
        @Valid @RequestBody request: RecoRequest.Personalized,
    ): ApiResponse<RecoResponse.Personalized> =
        ApiResponse.ok(recoService.recommend(request))
}
