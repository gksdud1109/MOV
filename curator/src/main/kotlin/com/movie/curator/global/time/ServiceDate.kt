package com.movie.curator.global.time

import java.time.LocalDate
import java.time.ZoneId

/**
 * 서비스의 "오늘" 기준 날짜를 한 곳에서 정의한다.
 *
 * 오늘의 질문(활성화 날짜) 조회와 시드가 동일한 기준을 쓰도록 보장한다.
 * 한국 사용자 기준이므로 KST(Asia/Seoul) — 매일 KST 00:00에 그날의 질문이 today로 전환된다.
 */
object ServiceDate {
    val ZONE: ZoneId = ZoneId.of("Asia/Seoul")

    fun today(): LocalDate = LocalDate.now(ZONE)
}
