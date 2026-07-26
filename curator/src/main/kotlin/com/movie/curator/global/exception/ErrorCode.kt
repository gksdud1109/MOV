package com.movie.curator.global.exception

import org.springframework.http.HttpStatus

/**
 * 안정적인 API 오류 코드. 기능 추가 시 항목을 추가하되, 클라이언트가 이 값으로
 * 분기 처리할 수 있으므로 기존 코드는 변경하지 않는다.
 */
enum class ErrorCode(val status: HttpStatus, val defaultMessage: String) {
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "이 작업을 수행할 권한이 없습니다"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다"),
    CONFLICT(HttpStatus.CONFLICT, "리소스 상태가 충돌했습니다"),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "요청 값이 올바르지 않습니다"),
    EXTERNAL_API_ERROR(HttpStatus.BAD_GATEWAY, "AI 추천 생성에 실패했습니다. 잠시 후 다시 시도해주세요"),
    INTERNAL(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다"),
}
