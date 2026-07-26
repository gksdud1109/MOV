package com.movie.curator.common.error

import org.springframework.http.HttpStatus

/**
 * 애플리케이션의 모든 오류 코드가 따라야 하는 공통 계약이다.
 *
 * 기능별 오류 코드는 각 기능 패키지의 exception 패키지에 enum으로 정의한다.
 */
interface ErrorCode {
    val httpStatus: HttpStatus
    val message: String
}
