package net.alexanderkahn.service.commons.firebaseauth.jws.filter

import net.alexanderkahn.service.commons.firebaseauth.jws.JwsAuthentication
import net.alexanderkahn.service.commons.firebaseauth.jws.JwsCredentials
import net.alexanderkahn.service.commons.firebaseauth.jws.JwsUserDetails
import net.alexanderkahn.service.commons.firebaseauth.jws.filter.config.FirebaseJwsConfig
import net.alexanderkahn.service.commons.model.exception.BadRequestException
import org.slf4j.LoggerFactory
import javax.servlet.http.HttpServletRequest

open class BypassTokenManager(private val config: FirebaseJwsConfig.BypassTokenConfig?) {

    private val logger = LoggerFactory.getLogger(BypassTokenManager::class.java)

    fun isUsingBypassToken(request: HttpServletRequest): Boolean {
        return config != null && !config.token.isNullOrEmpty() && request.getBearerToken() == config.token
    }

    val tokenBypassCredentials = JwsAuthentication(
            JwsCredentials("testIssuer", "tokenBypassCredentials"),
            JwsUserDetails("Test User"),
            true
    )
        get() {
            if (config?.token.isNullOrBlank()) {
                throw BadRequestException("Bypass token is disabled or not configured.")
            }
            return field
        }

}