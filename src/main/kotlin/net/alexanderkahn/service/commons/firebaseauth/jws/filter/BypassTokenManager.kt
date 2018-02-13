package net.alexanderkahn.service.commons.firebaseauth.jws.filter

import net.alexanderkahn.service.commons.firebaseauth.isBypassTokenProfileActive
import net.alexanderkahn.service.commons.firebaseauth.jws.JwsAuthentication
import net.alexanderkahn.service.commons.firebaseauth.jws.JwsCredentials
import net.alexanderkahn.service.commons.firebaseauth.jws.JwsUserDetails
import net.alexanderkahn.service.commons.model.exception.InvalidStateException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest

@Component
open class BypassTokenManager(
        @Autowired private val environment: Environment,
        @Value("\${oauth.test.bypassToken:}") configuredToken: String) {

    private val bypassToken: String?

    private val logger = LoggerFactory.getLogger(BypassTokenManager::class.java)

    init {
        bypassToken = if (configuredToken.isNotEmpty() && environment.isBypassTokenProfileActive()) {
            logger.info("Configuring token authentication service with bypass token: $configuredToken")
            configuredToken
        } else {
            null
        }
    }

    fun isUsingBypassToken(request: HttpServletRequest): Boolean {
        return !bypassToken.isNullOrEmpty() && request.getBearerToken() == bypassToken
    }

    val tokenBypassCredentials = JwsAuthentication(
            JwsCredentials("testIssuer", "tokenBypassCredentials"),
            JwsUserDetails("Test User"),
            true
    )
        get() {
            if (!environment.isBypassTokenProfileActive()) {
                throw InvalidStateException("Cannot access test user from the current active profiles")
            }
            return field
        }

}