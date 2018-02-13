package net.alexanderkahn.service.commons.firebaseauth.jws.filter

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.SigningKeyResolver
import io.jsonwebtoken.impl.DefaultJwtParser
import net.alexanderkahn.service.commons.model.exception.UnauthenticatedException

class ExtendedJwsParser(signingKeyResolver: SigningKeyResolver, private val algorithm: String) : DefaultJwtParser() {
    init {
        setSigningKeyResolver(signingKeyResolver)
    }

    override fun parseClaimsJws(claimsJws: String): Jws<Claims> {
        val parsed = super.parseClaimsJws(claimsJws)
        verifyClaims(parsed)
        return parsed
    }

    private fun verifyClaims(parsed: Jws<Claims>) {
        if (parsed.header.getAlgorithm() != algorithm) {
            throw UnauthenticatedException("JWT token contained invalid signing algorithm")
        }
    }
}