package net.alexanderkahn.service.commons.firebaseauth.jws.filter

import net.alexanderkahn.service.commons.firebaseauth.jws.InvalidJwsTokenException
import net.alexanderkahn.service.commons.firebaseauth.jws.JwsAuthentication
import net.alexanderkahn.service.commons.firebaseauth.jws.JwsCredentials
import net.alexanderkahn.service.commons.firebaseauth.jws.JwsUserDetails
import net.alexanderkahn.service.commons.firebaseauth.jws.filter.config.FirebaseJwsConfig
import javax.servlet.http.HttpServletRequest

open class BypassTokenManager(private val config: FirebaseJwsConfig.BypassTokenConfig?) {

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