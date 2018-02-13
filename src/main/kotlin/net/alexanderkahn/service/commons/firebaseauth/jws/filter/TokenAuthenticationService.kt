package net.alexanderkahn.service.commons.firebaseauth.jws.filter

import io.jsonwebtoken.Claims
import net.alexanderkahn.service.commons.firebaseauth.jws.JwsAuthentication
import net.alexanderkahn.service.commons.firebaseauth.jws.JwsCredentials
import net.alexanderkahn.service.commons.firebaseauth.jws.JwsUserDetails
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest

@Component
@Configuration
open class TokenAuthenticationService(@Autowired jwsParserFactory: JwsParserFactory) {

    private val parser = jwsParserFactory.tokenParser

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