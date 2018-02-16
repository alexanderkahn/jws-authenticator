package net.alexanderkahn.service.commons.firebaseauth.jws.filter


import net.alexanderkahn.service.commons.firebaseauth.jws.UnableToVerifyJwsTokenException
import net.alexanderkahn.service.commons.firebaseauth.jws.filter.config.FirebaseJwsConfig
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class JwsParserFactory(private val jwsIssuer: FirebaseJwsConfig.JwsIssuerConfig) {

    private val logger = LoggerFactory.getLogger(JwsParserFactory::class.java)

    //TODO: this could all be much simpler with some verification on the jwsIssuer object
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

