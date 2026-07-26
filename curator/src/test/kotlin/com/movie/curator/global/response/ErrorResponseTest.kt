package com.movie.curator.global.response

import com.movie.curator.global.exception.ErrorResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ErrorResponseTest {

    @Test
    fun `오류 응답은 안정적인 코드와 메시지를 가진다`() {
        val response = ErrorResponse(
            code = "RECOMMENDATION_NOT_FOUND",
            message = "추천 결과를 찾을 수 없습니다.",
        )

        assertThat(response).isEqualTo(
            ErrorResponse(
                code = "RECOMMENDATION_NOT_FOUND",
                message = "추천 결과를 찾을 수 없습니다.",
                timestamp = response.timestamp,
            ),
        )
    }

    @Test
    fun `필드 오류 상세 정보를 포함할 수 있다`() {
        val response = ErrorResponse(
            code = "VALIDATION_FAILED",
            message = "요청 값이 올바르지 않습니다",
            details = listOf(ErrorResponse.FieldError("liked", "정확히 3편 입력해주세요.")),
        )

        assertThat(response.details)
            .containsExactly(ErrorResponse.FieldError("liked", "정확히 3편 입력해주세요."))
    }
}
