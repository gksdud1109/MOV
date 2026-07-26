package com.movie.curator.common.response

import com.movie.curator.common.error.BusinessException
import com.movie.curator.common.error.ErrorCode
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

class ErrorResponseTest {

    @Test
    fun `enum 오류 코드는 enum 이름을 응답 코드로 사용한다`() {
        val response = ErrorResponse.from(TestErrorCode.RECOMMENDATION_NOT_FOUND)

        assertThat(response).isEqualTo(
            ErrorResponse(
                status = 404,
                code = "RECOMMENDATION_NOT_FOUND",
                message = "추천 결과를 찾을 수 없습니다.",
            ),
        )
    }

    @Test
    fun `비즈니스 예외를 공통 오류 응답으로 변환한다`() {
        val exception = BusinessException(TestErrorCode.RECOMMENDATION_NOT_FOUND)

        assertThat(ErrorResponse.from(exception).message)
            .isEqualTo("추천 결과를 찾을 수 없습니다.")
    }

    private enum class TestErrorCode(
        override val httpStatus: HttpStatus,
        override val message: String,
    ) : ErrorCode {
        RECOMMENDATION_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "추천 결과를 찾을 수 없습니다.",
        ),
    }
}
