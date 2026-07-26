package com.movie.curator.global.response

import org.springframework.data.domain.Page

/**
 * 목록 엔드포인트에서 공유하는 최소한의 페이지 래퍼. Spring 업그레이드에도 와이어 포맷을
 * 안정적으로 유지하며(Spring 기본 `Page` 직렬화는 불안정하다고 문서화됨), 컨트롤러가
 * 자체 `T` 형태를 자유롭게 선택할 수 있도록 한다.
 */
data class PageResponse<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
    val hasNext: Boolean,
) {
    companion object {
        fun <S : Any, T : Any> from(page: Page<S>, transform: (S) -> T): PageResponse<T> = PageResponse(
            content = page.content.map(transform),
            page = page.number,
            size = page.size,
            totalElements = page.totalElements,
            totalPages = page.totalPages,
            hasNext = page.hasNext(),
        )
    }
}
