package com.movie.curator.recommendation.dto

sealed interface RecoResponse {

    data class Personalized(
        val recommendations: List<MovieRecommendation>
    ): RecoResponse {
        data class MovieRecommendation(
            val title: String,
            val description: String,
        )
    }
}