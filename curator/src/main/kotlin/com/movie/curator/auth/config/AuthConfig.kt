package com.movie.curator.auth.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

/**
 * auth 도메인의 [OAuthProperties] 바인딩을 활성화한다.
 */
@Configuration
@EnableConfigurationProperties(OAuthProperties::class)
class AuthConfig {
}