package com.movie.curator.global.handler

import com.movie.curator.global.exception.ApiException
import com.movie.curator.global.exception.ErrorCode
import com.movie.curator.global.exception.GlobalExceptionHandler
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
                jsonPath("$.code") { value("CONFLICT") }
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
                jsonPath("$.code") { value("VALIDATION_FAILED") }
                jsonPath("$.message") { value("요청 값이 올바르지 않습니다") }
                jsonPath("$.details[0].field") { value("title") }
                jsonPath("$.details[0].message") { value("비어 있을 수 없습니다.") }
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
                jsonPath("$.code") { value("VALIDATION_FAILED") }
                jsonPath("$.message") { value("요청 값이 올바르지 않습니다") }
            }
    }

    @RestController
    private class TestController {

        @GetMapping("/test/business")
        fun business(): Nothing =
            throw ApiException(ErrorCode.CONFLICT, "추천이 이미 생성되었습니다.")

        @PostMapping("/test/validation")
        fun validation(
            @Valid @RequestBody request: TestRequest,
        ): TestRequest = request
    }

    private data class TestRequest(
        @field:NotBlank(message = "비어 있을 수 없습니다.")
        val title: String,
    )

}
