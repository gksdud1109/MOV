package com.movie.curator.domain.recommendation.service

import com.movie.curator.domain.recommendation.client.GeminiClient
import com.movie.curator.domain.recommendation.dto.RecoRequest
import com.movie.curator.domain.recommendation.dto.RecoResponse
import org.springframework.stereotype.Service

@Service
class RecommendationService(
    private val geminiClient: GeminiClient,
) {

    fun recommend(request: RecoRequest.Personalized): RecoResponse.Personalized =
        geminiClient.recommend(request)
}
