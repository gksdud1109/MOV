package com.movie.curator.user.model

import com.movie.curator.global.jpa.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

/**
 * 회원. 소셜 로그인 최초 연동 시 자동 생성된다. `username`은 내부 식별용으로 유니크하며,
 * `nickname`은 화면에 노출되는 표시 이름이다.
 *
 * NOTE: auth 플로우가 동작하도록 추가한 최소 골격이다. role 등 필요한 필드는 자유롭게 확장하라.
 */
@Entity
@Table(
    name = "users",
    uniqueConstraints = [UniqueConstraint(name = "uk_users_username", columnNames = ["username"])],
)
class User(
    @Column(nullable = false, length = 50)
    var username: String,

    @Column(nullable = false, length = 100)
    var nickname: String,

    @Column(length = 320)
    var email: String? = null,
) : BaseEntity() {

    fun update(nickname: String?, email: String?) {
        nickname?.let { this.nickname = it }
        email?.let { this.email = it }
    }
}
