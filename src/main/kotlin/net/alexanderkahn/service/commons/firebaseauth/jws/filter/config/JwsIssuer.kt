package net.alexanderkahn.service.commons.firebaseauth.jws.filter.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Service

@Service
@Configuration
open class JwsIssuer(
        @Value("\${oauth.issuer.algorithm:RS256}") val algorithm: String,
        @Value("\${oauth.issuer.keystore.url:}") val keystoreUrl: String,

        //TODO: if I could fit these into an arbitrary claims key-value map that would be tasty
        @Value("\${oauth.issuer.claims.aud:}") val audienceClaim: String,
        @Value("\${oauth.issuer.claims.iss:}") val issuerClaim: String
)