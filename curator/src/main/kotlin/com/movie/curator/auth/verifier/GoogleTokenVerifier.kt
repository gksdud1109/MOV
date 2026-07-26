package com.movie.curator.auth.verifier

import com.movie.curator.auth.config.OAuthProperties
import com.movie.curator.auth.model.AuthProvider
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
