package com.movie.curator.auth.model

import com.movie.curator.global.jpa.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Index
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

/**
 * 소셜 신원과 로컬 [com.movie.curator.user.model.User]를 잇는 연결 행.
 * (provider, providerUserId) 한 쌍이 하나의 회원에 매핑된다.
 */
@Entity
@Table(
    name = "social_accounts",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_social_provider_user", columnNames = ["provider", "providerUserId"]),
    ],
    indexes = [Index(name = "ix_social_user_id", columnList = "userId")],
)
class SocialAccount(
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var provider: AuthProvider,

    @Column(nullable = false, length = 100)
    var providerUserId: String,

    @Column(nullable = false)
    var userId: Long,
) : BaseEntity() {
}
