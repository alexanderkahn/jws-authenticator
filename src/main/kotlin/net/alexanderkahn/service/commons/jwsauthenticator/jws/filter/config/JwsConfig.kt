package net.alexanderkahn.service.commons.jwsauthenticator.jws.filter.config

interface JwsConfig {

    val issuer: JwsIssuerConfig
    val unauthenticatedPaths: MutableCollection<String>
    val bypassToken: BypassTokenConfig?


    interface JwsIssuerConfig {
        val algorithm: String
        val keystore: Keystore
        val claims: Claims

        interface Keystore {
            val url: String
        }

        interface Claims {
            //TODO: if I could fit these into an arbitrary claims key-value map that would be tasty
            val audience: String
            val issuer: String
        }
    }

    interface BypassTokenConfig {
        val token: String
    }
}