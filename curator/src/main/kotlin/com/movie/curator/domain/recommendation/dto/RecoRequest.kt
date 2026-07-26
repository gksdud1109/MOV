package com.movie.curator.domain.recommendation.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

sealed interface RecoRequest {

    data class Personalized(
        @field:Size(min = 3, max = 3, message = "좋아한 영화는 정확히 3편 입력해주세요.")
        val liked: List<@NotBlank(message = "좋아한 영화는 비어 있을 수 없습니다.") String>,

        @field:Size(min = 1, max = 1, message = "별로였던 영화는 정확히 1편 입력해주세요.")
        val disliked: List<@NotBlank(message = "별로였던 영화는 비어 있을 수 없습니다.") String>,
    ) : RecoRequest
}
