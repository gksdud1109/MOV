package com.movie.curator.common.handler

import com.movie.curator.common.error.BusinessException
import com.movie.curator.common.error.CommonErrorCode
import com.movie.curator.common.error.ErrorCode
import com.movie.curator.common.error.ExternalApiException
import com.movie.curator.common.response.ErrorResponse
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.resource.NoResourceFoundException

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(exception: BusinessException): ResponseEntity<ErrorResponse> {
        val errorCode = exception.errorCode
        val codeName = errorCode.codeName()

        if (errorCode.httpStatus.is5xxServerError) {
            log.error("BusinessException: {} - {}", codeName, exception.message)
        } else {
            log.warn("BusinessException: {} - {}", codeName, exception.message)
        }

        return errorResponse(errorCode.httpStatus.value(), ErrorResponse.from(exception))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(exception: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val message = exception.bindingResult.fieldErrors
            .joinToString(", ") { "${it.field}: ${it.defaultMessage}" }
            .ifBlank { CommonErrorCode.INVALID_INPUT_VALUE.message }

        log.warn("Validation failed: {}", message)

        return errorResponse(
            CommonErrorCode.INVALID_INPUT_VALUE.httpStatus.value(),
            ErrorResponse(
                status = CommonErrorCode.INVALID_INPUT_VALUE.httpStatus.value(),
                code = CommonErrorCode.INVALID_INPUT_VALUE.name,
                message = message,
            ),
        )
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatch(exception: MethodArgumentTypeMismatchException): ResponseEntity<ErrorResponse> {
        log.warn("Request type mismatch: {}", exception.message)
        return errorResponse(CommonErrorCode.TYPE_MISMATCH)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleMessageNotReadable(exception: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> {
        log.warn("Request body is not readable: {}", exception.message)
        return errorResponse(CommonErrorCode.MESSAGE_NOT_READABLE)
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingRequestParameter(
        exception: MissingServletRequestParameterException,
    ): ResponseEntity<ErrorResponse> {
        log.warn("Missing request parameter: {}", exception.message)
        return errorResponse(CommonErrorCode.MISSING_REQUEST_PARAMETER)
    }

    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNotFound(exception: NoResourceFoundException): ResponseEntity<ErrorResponse> {
        log.warn("Resource not found: {}", exception.resourcePath)
        return errorResponse(CommonErrorCode.NOT_FOUND)
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleMethodNotAllowed(
        exception: HttpRequestMethodNotSupportedException,
    ): ResponseEntity<ErrorResponse> {
        log.warn("Method not allowed: {}", exception.message)
        return errorResponse(CommonErrorCode.METHOD_NOT_ALLOWED)
    }

    @ExceptionHandler(ExternalApiException::class)
    fun handleExternalApi(exception: ExternalApiException): ResponseEntity<ErrorResponse> {
        log.error(
            "ExternalApiException: {} - {}",
            exception.code,
            exception.message,
            exception,
        )

        return errorResponse(
            exception.httpStatus.value(),
            ErrorResponse(
                status = exception.httpStatus.value(),
                code = exception.code,
                message = exception.message ?: CommonErrorCode.EXTERNAL_API_ERROR.message,
            ),
        )
    }

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrityViolation(
        exception: DataIntegrityViolationException,
    ): ResponseEntity<ErrorResponse> {
        log.warn("Data integrity violation: {}", exception.message)
        return errorResponse(CommonErrorCode.DUPLICATE_KEY)
    }

    @ExceptionHandler(OptimisticLockingFailureException::class)
    fun handleOptimisticLockingFailure(
        exception: OptimisticLockingFailureException,
    ): ResponseEntity<ErrorResponse> {
        log.warn("Optimistic locking failure: {}", exception.message)
        return errorResponse(CommonErrorCode.OPTIMISTIC_LOCK_FAILURE)
    }

    @ExceptionHandler(Exception::class)
    fun handleUnknown(exception: Exception): ResponseEntity<ErrorResponse> {
        log.error("Unexpected error: {}", exception.message, exception)
        return errorResponse(CommonErrorCode.INTERNAL_SERVER_ERROR)
    }

    private fun errorResponse(errorCode: ErrorCode): ResponseEntity<ErrorResponse> =
        errorResponse(errorCode.httpStatus.value(), ErrorResponse.from(errorCode))

    private fun errorResponse(
        status: Int,
        body: ErrorResponse,
    ): ResponseEntity<ErrorResponse> =
        ResponseEntity
            .status(status)
            .contentType(MediaType.APPLICATION_JSON)
            .body(body)

    private fun ErrorCode.codeName(): String =
        when (this) {
            is Enum<*> -> name
            else -> this::class.simpleName ?: "UNKNOWN_ERROR"
        }

    companion object {
        private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
    }
}
