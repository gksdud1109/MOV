package com.movie.curator.global.openapi

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springdoc.core.customizers.OperationCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.HandlerMethod
import java.lang.reflect.Method

@Configuration
class OpenApiConfig {

    @Bean
    fun openAPI(): OpenAPI =
        OpenAPI()
            .info(
                Info()
                    .title("Movie Curator API")
                    .version("v1")
                    .description("취향 기반 영화 추천 API"),
            )
            .components(
                Components().addSecuritySchemes(
                    BEARER_AUTH,
                    SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"),
                ),
            )

    @Bean
    fun apiErrorCodeOperationCustomizer(): OperationCustomizer =
        OperationCustomizer { operation, handlerMethod ->
            findApiErrorCode(handlerMethod)?.let {
                operation.addExtension("x-error-codes", it.value.toList())
            }
            operation
        }

    private fun findApiErrorCode(handlerMethod: HandlerMethod): ApiErrorCode? {
        val method = handlerMethod.method
        method.getAnnotation(ApiErrorCode::class.java)?.let { return it }
        return handlerMethod.beanType.interfaces
            .asSequence()
            .flatMap { it.allMethods().asSequence() }
            .firstOrNull { it.hasSameSignature(method) }
            ?.getAnnotation(ApiErrorCode::class.java)
    }

    private fun Class<*>.allMethods(): Set<Method> =
        interfaces.flatMapTo(methods.toMutableSet()) { it.allMethods() }

    private fun Method.hasSameSignature(other: Method): Boolean =
        name == other.name && parameterCount == other.parameterCount

    companion object {
        const val BEARER_AUTH = "bearerAuth"
    }
}
