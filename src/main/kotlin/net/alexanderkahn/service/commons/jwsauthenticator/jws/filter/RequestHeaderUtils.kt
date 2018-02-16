package net.alexanderkahn.service.commons.jwsauthenticator.jws.filter

import net.alexanderkahn.service.commons.jwsauthenticator.jws.InvalidJwsTokenException
import javax.servlet.http.HttpServletRequest

private const val TOKEN_HEADER_NAME = "Authorization"
private const val TOKEN_HEADER_PREFIX = "Bearer "

fun HttpServletRequest.getBearerToken(): String {
    val authHeader = this.getHeader(TOKEN_HEADER_NAME)
    if (authHeader == null || !authHeader.startsWith(TOKEN_HEADER_PREFIX)) {
        throw InvalidJwsTokenException("Unable to find authorization token. Please include a token in your request.")
    }
    return authHeader.drop(TOKEN_HEADER_PREFIX.length)
}