package com.movie.curator.global.openapi

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest
@AutoConfigureMockMvc
class OpenApiDocumentTest(
    @Autowired private val mockMvc: MockMvc,
) {

    @Test
    fun `추천 API의 OpenAPI 문서와 오류 코드 확장을 노출한다`() {
        mockMvc
            .get("/v3/api-docs")
            .andExpect {
                status { isOk() }
                jsonPath("$.info.title") { value("Movie Curator API") }
                jsonPath("$.paths['/api/recommendations'].post.summary") { value("영화 추천 받기") }
                jsonPath("$.paths['/api/recommendations'].post['x-error-codes'][0]") {
                    value("INVALID_INPUT_VALUE")
                }
            }
    }
}
