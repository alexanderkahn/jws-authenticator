package net.alexanderkahn.service.commons.jwsauthenticator.jws.filter


import net.alexanderkahn.service.commons.jwsauthenticator.jws.UnableToVerifyJwsTokenException
import net.alexanderkahn.service.commons.jwsauthenticator.jws.filter.config.JwsConfig
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class JwsParserFactory(private val jwsIssuer: JwsConfig.JwsIssuerConfig) {

    private val logger = LoggerFactory.getLogger(JwsParserFactory::class.java)

    val tokenParser: ExtendedJwsParser by lazy {
        if (jwsIssuer.keystore.url.isEmpty()) {
            throw UnableToVerifyJwsTokenException("Unable to process tokens: no keystoreUrl configured")
        }
        val parser = ExtendedJwsParser(RemoteKeyResolver(JwsKeyClient(jwsIssuer.keystore.url)), jwsIssuer.algorithm)

        if (jwsIssuer.claims.audience.isNotEmpty()) {
            parser.requireAudience(jwsIssuer.claims.audience)
        } else {
            logger.warn("Token Parser initialized without audience claim verification!")
        }

        if (jwsIssuer.claims.issuer.isNotEmpty()) {
            parser.requireIssuer(jwsIssuer.claims.issuer)
        } else {
            logger.warn("Token Parser initialized without issuer claim verification!")
        }
        parser
    }
}

