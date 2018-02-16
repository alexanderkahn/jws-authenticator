package net.alexanderkahn.service.commons.jwsauthenticator.jws.filter

import io.jsonwebtoken.Claims
import net.alexanderkahn.service.commons.jwsauthenticator.jws.JwsAuthentication
import net.alexanderkahn.service.commons.jwsauthenticator.jws.JwsCredentials
import net.alexanderkahn.service.commons.jwsauthenticator.jws.JwsUserDetails
import net.alexanderkahn.service.commons.jwsauthenticator.jws.filter.config.JwsConfig
import javax.servlet.http.HttpServletRequest

class TokenAuthenticationService(jwsIssuer: JwsConfig.JwsIssuerConfig) {

    private val parser = JwsParserFactory(jwsIssuer).tokenParser

    fun getUserFromToken(request: HttpServletRequest): JwsAuthentication {
        val tokenString = request.getBearerToken()
        val requestAuth = getTokenClaims(tokenString)
        return buildTokenUser(requestAuth)
    }

    private fun getTokenClaims(tokenString: String): Claims {
        val parsed = parser.parseClaimsJws(tokenString)
        return parsed.body
    }

    private fun buildTokenUser(requestAuth: Claims): JwsAuthentication {
        val credentials = JwsCredentials(requestAuth["iss"] as String, requestAuth["user_id"] as String)
        val details = JwsUserDetails(requestAuth["name"] as String)
        return JwsAuthentication(credentials, details)
    }
}