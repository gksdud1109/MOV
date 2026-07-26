package com.movie.curator.global.response

/**
 * 카운트만 반환하는 엔드포인트(반응 수 등)를 위한 간단한 래퍼.
 * 타입 있는 레코드를 사용해 클라이언트에 안정적인 필드명을 제공한다.
 */
data class CountResponse(val count: Long)
