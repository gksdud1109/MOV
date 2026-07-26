package com.movie.curator.recommendation.service

import com.movie.curator.recommendation.client.GeminiClient
import com.movie.curator.recommendation.dto.RecoRequest
import com.movie.curator.recommendation.dto.RecoResponse
import org.springframework.stereotype.Service

@Service
class RecommendationService(
    private val geminiClient: GeminiClient,
) {

    fun recommend(request: RecoRequest.Personalized): RecoResponse.Personalized =
        geminiClient.recommend(request)
}
