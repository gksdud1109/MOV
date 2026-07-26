package com.movie.curator.global.exception

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.resource.NoResourceFoundException


@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(ApiException::class)
    fun handleApi(ex: ApiException): ResponseEntity<ErrorResponse> {
        val body = ErrorResponse(code = ex.code.name, message = ex.message ?: ex.code.defaultMessage)
        return ResponseEntity.status(ex.code.status).body(body)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val fieldErrors = ex.bindingResult.fieldErrors.map {
            ErrorResponse.FieldError(field = it.field, message = it.defaultMessage ?: "invalid")
        }
        val body = ErrorResponse(
            code = ErrorCode.VALIDATION_FAILED.name,
            message = ErrorCode.VALIDATION_FAILED.defaultMessage,
            details = fieldErrors,
        )
        return ResponseEntity.status(ErrorCode.VALIDATION_FAILED.status).body(body)
    }

    /**
     * 잘못된 요청 형태를 400으로 매핑한다: 깨진/빈 JSON 본문(HttpMessageNotReadableException),
     * 경로·쿼리 파라미터 타입 불일치(MethodArgumentTypeMismatchException), 필수 파라미터 누락
     * (MissingServletRequestParameterException). 처리하지 않으면 catch-all로 떨어져 500이 된다.
     */
    @ExceptionHandler(
        HttpMessageNotReadableException::class,
        MethodArgumentTypeMismatchException::class,
        MissingServletRequestParameterException::class,
    )
    fun handleBadRequest(ex: Exception): ResponseEntity<ErrorResponse> {
        val body = ErrorResponse(
            code = ErrorCode.VALIDATION_FAILED.name,
            message = ErrorCode.VALIDATION_FAILED.defaultMessage,
        )
        return ResponseEntity.status(ErrorCode.VALIDATION_FAILED.status).body(body)
    }

    /**
     * 매칭되는 핸들러/정적 리소스가 없을 때(잘못된 경로, swagger-ui 의 `*.map` 같은 부수 요청 등).
     * 일상적인 404이므로 ERROR 로그를 남기지 않고 깔끔한 404로 응답한다(catch-all이 500으로 잡지 않게).
     */
    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNoResource(ex: NoResourceFoundException): ResponseEntity<ErrorResponse> {
        val body = ErrorResponse(
            code = ErrorCode.NOT_FOUND.name,
            message = ErrorCode.NOT_FOUND.defaultMessage,
        )
        return ResponseEntity.status(ErrorCode.NOT_FOUND.status).body(body)
    }

    @ExceptionHandler(Exception::class)
    fun handleUnknown(ex: Exception): ResponseEntity<ErrorResponse> {
        log.error("Unhandled exception", ex)
        val body = ErrorResponse(
            code = ErrorCode.INTERNAL.name,
            message = ErrorCode.INTERNAL.defaultMessage,
        )
        return ResponseEntity.status(ErrorCode.INTERNAL.status).body(body)
    }
}
