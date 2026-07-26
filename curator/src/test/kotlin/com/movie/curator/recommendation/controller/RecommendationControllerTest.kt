package com.movie.curator.recommendation.controller

import com.movie.curator.recommendation.client.GeminiClient
import com.movie.curator.recommendation.dto.RecoRequest
import com.movie.curator.recommendation.dto.RecoResponse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.mockito.BDDMockito.given

@SpringBootTest
@AutoConfigureMockMvc
class RecommendationControllerTest(
    @Autowired private val mockMvc: MockMvc,
) {

    @MockBean
    private lateinit var geminiClient: GeminiClient

    @BeforeEach
    fun setUp() {
        given(
            geminiClient.recommend(
                RecoRequest.Personalized(
                    liked = listOf("인셉션", "매드 맥스: 분노의 도로", "기생충"),
                    disliked = listOf("트와일라잇"),
                ),
            ),
        ).willReturn(
            RecoResponse.Personalized(
                recommendations = listOf(
                    RecoResponse.Personalized.MovieRecommendation(
                        title = "나이브스 아웃",
                        description = "취향에 맞는 긴장감 있는 전개를 제공합니다.",
                    ),
                ),
            ),
        )
    }

    @Test
    fun `추천 결과를 공통 성공 응답으로 반환한다`() {
        mockMvc
            .post("/api/recommendations") {
                contentType = MediaType.APPLICATION_JSON
                content = """
                    {
                      "liked": ["인셉션", "매드 맥스: 분노의 도로", "기생충"],
                      "disliked": ["트와일라잇"]
                    }
                """.trimIndent()
            }
            .andExpect {
                status { isOk() }
                jsonPath("$.code") { value("200") }
                jsonPath("$.message") { value("OK") }
                jsonPath("$.data.recommendations") { isArray() }
            }
    }
}
