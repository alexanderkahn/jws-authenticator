package net.alexanderkahn.service.commons.firebaseauth.jws.filter


import net.alexanderkahn.service.commons.firebaseauth.jws.filter.config.JwsIssuer
import net.alexanderkahn.service.commons.model.exception.InvalidStateException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class JwsParserFactory(
        @Autowired private val jwsIssuer: JwsIssuer) {

    private val logger = LoggerFactory.getLogger(JwsParserFactory::class.java)

    //TODO: this could all be much simpler with some verification on the jwsIssuer object
    val tokenParser: ExtendedJwsParser by lazy {
        if (jwsIssuer.keystoreUrl.isEmpty()) {
            throw InvalidStateException("Unable to process tokens: no keystoreUrl configured")
        }
        val parser = ExtendedJwsParser(RemoteKeyResolver(JwsKeyClient(jwsIssuer.keystoreUrl)), jwsIssuer.algorithm)

        if (jwsIssuer.audienceClaim.isNotEmpty()) {
            parser.requireAudience(jwsIssuer.audienceClaim)
        } else {
            logger.warn("Token Parser initialized without audience claim verification!")
        }

        if (jwsIssuer.issuerClaim.isNotEmpty()) {
            parser.requireIssuer(jwsIssuer.issuerClaim)
        } else {
            logger.warn("Token Parser initialized without issuer claim verification!")
        }
        parser
    }
}

