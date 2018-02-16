package net.alexanderkahn.service.commons.firebaseauth.jws

import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority

class JwsAuthentication(
        private val jwsCredentials: JwsCredentials,
        private val userDetails: JwsUserDetails,
        private var authenticated: Boolean = false) : Authentication {

    override fun getName(): String {
        return jwsCredentials.username
    }

    override fun setAuthenticated(isAuthenticated: Boolean) {
        this.authenticated = isAuthenticated
    }

    override fun isAuthenticated(): Boolean {
        return authenticated
    }

    override fun getCredentials(): Any {
        throw NotImplementedError()
    }

    override fun getDetails(): JwsUserDetails {
        return userDetails
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        throw NotImplementedError()
    }

    override fun getPrincipal(): JwsCredentials {
        return jwsCredentials
    }

}