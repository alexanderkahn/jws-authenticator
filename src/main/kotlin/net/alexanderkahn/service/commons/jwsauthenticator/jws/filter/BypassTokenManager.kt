package net.alexanderkahn.service.commons.jwsauthenticator.jws.filter

import net.alexanderkahn.service.commons.jwsauthenticator.jws.InvalidJwsTokenException
import net.alexanderkahn.service.commons.jwsauthenticator.jws.JwsAuthentication
import net.alexanderkahn.service.commons.jwsauthenticator.jws.JwsCredentials
import net.alexanderkahn.service.commons.jwsauthenticator.jws.JwsUserDetails
import net.alexanderkahn.service.commons.jwsauthenticator.jws.filter.config.JwsConfig
import javax.servlet.http.HttpServletRequest

open class BypassTokenManager(private val config: JwsConfig.BypassTokenConfig?) {

    fun isUsingBypassToken(request: HttpServletRequest): Boolean {
        return config != null && !config.token.isEmpty() && request.getBearerToken() == config.token
    }

    val tokenBypassCredentials = JwsAuthentication(
            JwsCredentials("testIssuer", "tokenBypassCredentials"),
            JwsUserDetails("Test User"),
            true
    )
        get() {
            if (config?.token.isNullOrBlank()) {
                throw InvalidJwsTokenException("Bypass token is disabled or not configured.")
            }
            return field
        }

}