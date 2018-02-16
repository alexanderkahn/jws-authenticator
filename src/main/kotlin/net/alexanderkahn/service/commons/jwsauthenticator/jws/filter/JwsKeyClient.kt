package net.alexanderkahn.service.commons.jwsauthenticator.jws.filter

import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate

class JwsKeyClient(private val keystoreUrl: String) {
    fun get(): ResponseEntity<String> {
        return RestTemplate().getForEntity(keystoreUrl, String::class.java)
    }
}