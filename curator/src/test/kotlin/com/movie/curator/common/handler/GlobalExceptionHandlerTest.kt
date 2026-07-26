package com.movie.curator.common.handler

import com.movie.curator.common.error.BusinessException
import com.movie.curator.common.error.ErrorCode
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

class GlobalExceptionHandlerTest {

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(TestController())
            .setControllerAdvice(GlobalExceptionHandler())
            .build()
    }

    @Test
    fun `비즈니스 예외는 공통 오류 형식으로 응답한다`() {
        mockMvc
            .get("/test/business")
            .andExpect {
                status { isConflict() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.status") { value(409) }
                jsonPath("$.code") { value("RECOMMENDATION_ALREADY_CREATED") }
                jsonPath("$.message") { value("추천이 이미 생성되었습니다.") }
            }
    }

    @Test
    fun `요청 검증 실패는 필드 정보를 포함한 공통 오류 형식으로 응답한다`() {
        mockMvc
            .post("/test/validation") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"title":""}"""
            }
            .andExpect {
                status { isBadRequest() }
                content { contentType(MediaType.APPLICATION_JSON) }
                jsonPath("$.status") { value(400) }
                jsonPath("$.code") { value("INVALID_INPUT_VALUE") }
                jsonPath("$.message") { value("title: 비어 있을 수 없습니다.") }
            }
    }

    @Test
    fun `읽을 수 없는 JSON은 세부 구현을 노출하지 않는 공통 오류로 응답한다`() {
        mockMvc
            .post("/test/validation") {
                contentType = MediaType.APPLICATION_JSON
                content = """{"title":}"""
            }
            .andExpect {
                status { isBadRequest() }
                jsonPath("$.status") { value(400) }
                jsonPath("$.code") { value("MESSAGE_NOT_READABLE") }
                jsonPath("$.message") { value("요청 본문을 읽을 수 없습니다.") }
            }
    }

    @RestController
    private class TestController {

        @GetMapping("/test/business")
        fun business(): Nothing =
            throw BusinessException(TestErrorCode.RECOMMENDATION_ALREADY_CREATED)

        @PostMapping("/test/validation")
        fun validation(
            @Valid @RequestBody request: TestRequest,
        ): TestRequest = request
    }

    private data class TestRequest(
        @field:NotBlank(message = "비어 있을 수 없습니다.")
        val title: String,
    )

    private enum class TestErrorCode(
        override val httpStatus: HttpStatus,
        override val message: String,
    ) : ErrorCode {
        RECOMMENDATION_ALREADY_CREATED(
            HttpStatus.CONFLICT,
            "추천이 이미 생성되었습니다.",
        ),
    }
}
