package com.movie.curator.domain.auth.model

import com.movie.curator.global.jpa.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Index
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.Instant

/**
 * 발급된 리프레시 토큰의 영속 레코드. `jti`로 토큰을 식별하며, 존재하고 폐기되지 않았으며
 * 만료되지 않은 행만 회전(rotate)에 사용된다.
 */
@Entity
@Table(
    name = "refresh_tokens",
    uniqueConstraints = [UniqueConstraint(name = "uk_refresh_jti", columnNames = ["jti"])],
    indexes = [Index(name = "ix_refresh_user_id", columnList = "userId")],
)
class RefreshToken(
    @Column(nullable = false)
    var userId: Long,

    @Column(nullable = false, length = 36)
    var jti: String,

    @Column(nullable = false)
    var expiresAt: Instant,
) : BaseEntity() {

    @Column(nullable = false)
    var revoked: Boolean = false

    fun revoke() {
        this.revoked = true
    }
}
