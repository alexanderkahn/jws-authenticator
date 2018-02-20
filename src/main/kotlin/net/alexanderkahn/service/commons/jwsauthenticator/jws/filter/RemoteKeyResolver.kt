package net.alexanderkahn.service.commons.jwsauthenticator.jws.filter

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwsHeader
import io.jsonwebtoken.SigningKeyResolver
import net.alexanderkahn.service.commons.jwsauthenticator.jws.UnableToVerifyJwsTokenException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import java.io.ByteArrayInputStream
import java.security.Key
import java.security.PublicKey
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.time.OffsetDateTime
import java.util.*

private const val CACHE_CONTROL_HEADER = "Cache-Control"
private const val MAX_AGE_DELIMITER_START = "max-age="
private const val MAX_AGE_DELIMITER_END = ","

class RemoteKeyResolver(private val keyClient: JwsKeyClient) : SigningKeyResolver {

    private val certFactory = CertificateFactory.getInstance("X.509")
    private val logger = LoggerFactory.getLogger(RemoteKeyResolver::class.java)

    private var cachedKeys: CachedKeys? = null

    override fun resolveSigningKey(header: JwsHeader<out JwsHeader<*>>?, claims: Claims?): Key {
        header ?: throw UnableToVerifyJwsTokenException("Unable to read token header")
        val headerKeyId: String = header.getKeyId()
        val publicKey = getRemoteKeys()?.get(headerKeyId)
        return publicKey ?: throw UnableToVerifyJwsTokenException("Unable to authenticate token signing key")
    }

    override fun resolveSigningKey(header: JwsHeader<out JwsHeader<*>>?, plaintext: String?): Key {
        throw NotImplementedError()
    }

    private fun getRemoteKeys(): Map<String, PublicKey>? {
        if (OffsetDateTime.now().isAfter(cachedKeys?.updateAfter)) {
            updateKeys()
        }
        return cachedKeys?.keyIds
    }

    private fun updateKeys() {
        logger.info("Updating JWT public keys")
        val response = keyClient.get()
        cachedKeys = CachedKeys(updateAfter = getNextRefresh(response.headers), keyIds = convertToKeys(response.body.orEmpty()))
    }

    private fun getNextRefresh(headers: HttpHeaders): OffsetDateTime {
        val maxAge = headers[CACHE_CONTROL_HEADER]?.get(0)?.substringAfter(MAX_AGE_DELIMITER_START)?.substringBefore(MAX_AGE_DELIMITER_END)?.toLongOrNull()
        return if (maxAge == null) {
            logger.warn("Certificates cached with null max-age value. Will refresh on next request.")
            OffsetDateTime.now()
        } else {
            OffsetDateTime.now().plusSeconds(maxAge)
        }
    }

    private fun convertToKeys(responseBody: String): Map<String, PublicKey> {
        val certMap: HashMap<String, String> = Gson().fromJson(responseBody, object : TypeToken<HashMap<String, String>>() {}.type)
        return certMap.mapValues { convertToKey(it.value) }
    }

    private fun convertToKey(certText: String): PublicKey {
        val certStream = ByteArrayInputStream(certText.toByteArray())
        certStream.use {
            val certificate = certFactory.generateCertificate(certStream) as X509Certificate
            return certificate.publicKey
        }
    }

    private data class CachedKeys(
            val updateAfter: OffsetDateTime,
            val keyIds: Map<String, PublicKey>
    )
}