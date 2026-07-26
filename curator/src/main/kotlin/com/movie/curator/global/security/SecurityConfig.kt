package com.movie.curator.global.security

import com.movie.curator.global.exception.ErrorCode
import com.movie.curator.global.exception.ErrorResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import com.fasterxml.jackson.databind.ObjectMapper


/**
 * 무상태(Stateless) JWT 보안 설정. 인증 엔드포인트와 헬스 체크만 공개되며,
 * 그 외 모든 요청은 유효한 액세스 토큰(Bearer)이 필요하다. 기본적으로 읽기 요청도
 * 인증이 필요하며, 공개 조회를 허용하려면 여기서 특정 GET 요청을 완화한다.
 */
@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun filterChain(http: HttpSecurity, entryPoint: AuthenticationEntryPoint): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it.requestMatchers("/api/v1/auth/**").permitAll()
				it.requestMatchers("/api/recommendations").permitAll()
                it.requestMatchers("/actuator/health", "/actuator/health/**").permitAll()
                it.requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()
                // 공개 읽기(GET만): 오늘의 질문·질문 상세·질문별 답변 목록·아카이브는 비로그인 허용.
                // 쓰기(POST/PATCH/DELETE)는 메서드가 달라 매칭되지 않으므로 인증이 그대로 유지된다.
                it.requestMatchers(
                    HttpMethod.GET,
                    "/api/v1/questions/today",
                    "/api/v1/questions/*",
                    "/api/v1/questions/*/answers",
                    "/api/v1/archives/questions",
                ).permitAll()
                it.anyRequest().authenticated()
            }
            .oauth2ResourceServer { rs ->
                rs.authenticationEntryPoint(entryPoint)
                rs.jwt { }
            }
            .exceptionHandling { it.authenticationEntryPoint(entryPoint) }
        return http.build()
    }

    /** 401 응답 시 GlobalExceptionHandler와 동일한 JSON 오류 형식을 반환한다. */
    @Bean
    fun authenticationEntryPoint(objectMapper: ObjectMapper): AuthenticationEntryPoint =
        AuthenticationEntryPoint { _, response, _ ->
            response.status = HttpStatus.UNAUTHORIZED.value()
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            response.characterEncoding = "UTF-8"
            val body = ErrorResponse(
                code = ErrorCode.UNAUTHORIZED.name,
                message = ErrorCode.UNAUTHORIZED.defaultMessage,
            )
            response.writer.write(objectMapper.writeValueAsString(body))
        }
}
