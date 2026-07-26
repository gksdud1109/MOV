package com.movie.curator.global.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.presigner.S3Presigner

/**
 * S3 presigned URL 발급용 설정.
 *
 * 자격증명은 AWS 기본 제공자 체인(DefaultCredentialsProvider)을 사용한다:
 *  - 로컬: `AWS_PROFILE` / `~/.aws` (SSO 로그인 상태)
 *  - EC2: instance role 또는 `AWS_ACCESS_KEY_ID` / `AWS_SECRET_ACCESS_KEY` env
 *
 * presigner 빈 생성은 오프라인(URL 서명만)이라, 자격증명이 없어도 앱 부팅에는 영향이 없다.
 * (실제 presign 호출 시점에만 자격증명이 필요하다.)
 */
@Configuration
class S3Config(
    @Value("\${app.s3.region:ap-northeast-2}") private val region: String,
) {

    @Bean
    fun s3Presigner(): S3Presigner =
        S3Presigner.builder().region(Region.of(region)).build()
}
