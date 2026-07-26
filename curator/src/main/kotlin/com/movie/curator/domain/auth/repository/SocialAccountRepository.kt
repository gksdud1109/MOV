package com.movie.curator.domain.auth.repository

import com.movie.curator.domain.auth.model.AuthProvider
import com.movie.curator.domain.auth.model.SocialAccount
import org.springframework.data.jpa.repository.JpaRepository

interface SocialAccountRepository : JpaRepository<SocialAccount, Long> {
    fun findByProviderAndProviderUserId(provider: AuthProvider, providerUserId: String): SocialAccount?
}
