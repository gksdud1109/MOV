package com.movie.curator.domain.auth.repository

import com.movie.curator.domain.auth.model.RefreshToken
import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface RefreshTokenRepository : JpaRepository<RefreshToken, Long> {
    fun findByJti(jti: String): RefreshToken?

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from RefreshToken r where r.jti = :jti")
    fun findByJtiForUpdate(@Param("jti") jti: String): RefreshToken?

    @Modifying
    @Query("update RefreshToken r set r.revoked = true where r.userId = :userId and r.revoked = false")
    fun revokeAllByUserId(@Param("userId") userId: Long): Int
}
