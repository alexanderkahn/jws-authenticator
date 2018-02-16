package net.alexanderkahn.service.commons.firebaseauth.jws.filter

import net.alexanderkahn.service.commons.firebaseauth.jws.JwsAuthentication
import net.alexanderkahn.service.commons.firebaseauth.jws.filter.config.FirebaseJwsConfig
import org.springframework.http.HttpMethod
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.GenericFilterBean
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

class JwsAuthenticationFilter(private val responseWriter: ExceptionResponseWriter, config: FirebaseJwsConfig) : GenericFilterBean() {

    private val unauthenticatedPathPatterns: Collection<Regex>

    init {
        unauthenticatedPathPatterns = config.unauthenticatedPaths.map { Regex(it) }
    }

    private val tokenAuthenticationService: TokenAuthenticationService = TokenAuthenticationService(config.issuer)
    private val bypassTokenManager: BypassTokenManager = BypassTokenManager(config.bypassToken)

    override fun doFilter(request: ServletRequest, response: ServletResponse, filterChain: FilterChain) {
        try {
            if (request is HttpServletRequest && !request.allowUnauthenticated()) {
                setUserInContext(getUserFromRequest(request))
            }
            filterChain.doFilter(request, response)
        } catch (exception: Exception) {
            responseWriter.writeExceptionResponse(exception, response)
        }
    }

    private fun getUserFromRequest(request: HttpServletRequest): JwsAuthentication {
        return if (bypassTokenManager.isUsingBypassToken(request)) {
            bypassTokenManager.tokenBypassCredentials
        } else {
            tokenAuthenticationService.getUserFromToken(request)
        }
    }

    private fun HttpServletRequest.allowUnauthenticated(): Boolean {
        this.servletPath ?: return false
        return unauthenticatedPathPatterns.any { it. matches(this.servletPath) } || HttpMethod.OPTIONS.matches(this.method)
    }

    private fun setUserInContext(jwsAuthentication: JwsAuthentication) {
        jwsAuthentication.isAuthenticated = true
        SecurityContextHolder.getContext().authentication = jwsAuthentication
    }

}