package com.movie.curator.domain.user.repository

import com.movie.curator.domain.user.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByUsername(username: String): User?
    fun findByIdAndDeletedFalse(id: Long): User?
    fun findAllByIdInAndDeletedFalse(ids: Collection<Long>): List<User>
    fun existsByUsername(username: String): Boolean
    fun existsByIdAndDeletedFalse(id: Long): Boolean
}
