package com.movie.curator.global.web

/**
 * 인증된 사용자 id를 컨트롤러 메서드 파라미터에 주입한다.
 *
 * 현재는 JWT / Spring Security 기반으로 동작하며, 인증 방식이 변경될 경우
 * [CurrentUserIdArgumentResolver]를 SecurityContext 기반 구현으로 교체하면
 * 컨트롤러와 서비스는 수정하지 않아도 된다.
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class CurrentUserId
