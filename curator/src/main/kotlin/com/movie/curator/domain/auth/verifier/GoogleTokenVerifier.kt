package com.movie.curator.domain.auth.verifier

import com.movie.curator.domain.auth.config.OAuthProperties
import com.movie.curator.domain.auth.model.AuthProvider
import org.springframework.stereotype.Component

@Component
class GoogleTokenVerifier(
    properties: OAuthProperties,
) : OidcTokenVerifier(
    issuer = properties.google.issuer,
    jwksUri = properties.google.jwksUri,
    clientId = properties.google.clientId,
) {
    override fun provider(): AuthProvider = AuthProvider.GOOGLE
}
