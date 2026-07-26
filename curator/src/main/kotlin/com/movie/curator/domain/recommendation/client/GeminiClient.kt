package com.movie.curator.domain.recommendation.client

import com.movie.curator.global.exception.ApiException
import com.movie.curator.global.exception.ErrorCode
import com.movie.curator.domain.recommendation.dto.RecoRequest
import com.movie.curator.domain.recommendation.dto.RecoResponse
import org.springframework.ai.chat.client.ChatClient
import org.springframework.stereotype.Component

/** Gemini 호출과 프롬프트/응답 형식을 추천 도메인 안에 가둔다. */
@Component
class GeminiClient(chatClientBuilder: ChatClient.Builder) {

    private val chatClient = chatClientBuilder.build()

    fun recommend(request: RecoRequest.Personalized): RecoResponse.Personalized =
        try {
            chatClient
                .prompt()
                .system(SYSTEM_PROMPT)
                .user { user ->
                    user
                        .text(USER_PROMPT)
                        .param("liked", request.liked.joinToString(", "))
                        .param("disliked", request.disliked.joinToString(", "))
                }
                .call()
                .entity(RecoResponse.Personalized::class.java)
                ?: throw IllegalStateException("Gemini returned an empty recommendation response")
        } catch (exception: Exception) {
            throw ApiException(ErrorCode.EXTERNAL_API_ERROR, cause = exception)
        }

    private companion object {
        const val SYSTEM_PROMPT = """
            당신은 영화 큐레이터입니다.
            사용자가 좋아한 영화와 싫어한 영화를 분석해 정확히 5편의 영화를 추천하세요.
            각 추천에는 title과 description이 있어야 합니다.
            description에는 사용자의 취향과 연결된 추천 이유를 한국어로 2~3문장 작성하세요.
            사용자가 좋아하거나 싫어한 영화와 동일한 제목은 추천하지 마세요.
            존재하지 않는 영화나 확인할 수 없는 정보를 만들지 마세요.
        """

        const val USER_PROMPT = """
            좋아한 영화: {liked}
            싫어한 영화: {disliked}
        """
    }
}
