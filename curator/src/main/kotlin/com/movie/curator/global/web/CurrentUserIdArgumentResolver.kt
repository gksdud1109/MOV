package com.movie.curator.global.web

import com.movie.curator.global.exception.ApiException
import com.movie.curator.global.exception.ErrorCode
import org.springframework.core.MethodParameter
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

/**
 * 인증된 주체에서 [CurrentUserId]를 리졸브한다. 액세스 토큰의 `sub` 클레임이
 * 사용자 id이며, Spring Security는 이를 `authentication.name`으로 노출한다.
 */
@Component
class CurrentUserIdArgumentResolver : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean {
        if (!parameter.hasParameterAnnotation(CurrentUserId::class.java)) return false
        // Kotlin non-null `Long`(primitive `long`으로 컴파일됨)과 boxed `Long` 모두 허용한다.
        val type = parameter.parameterType
        return type == java.lang.Long::class.java || type == java.lang.Long.TYPE
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): Any {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication == null ||
            !authentication.isAuthenticated ||
            authentication is AnonymousAuthenticationToken
        ) {
            throw ApiException(ErrorCode.UNAUTHORIZED, "Authentication required")
        }
        return authentication.name.toLongOrNull()
            ?: throw ApiException(ErrorCode.UNAUTHORIZED, "Invalid authentication principal")
    }
}
