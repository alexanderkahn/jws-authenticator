package net.alexanderkahn.service.commons.firebaseauth.jws.filter

import com.fasterxml.jackson.databind.ObjectMapper
import net.alexanderkahn.service.commons.firebaseauth.jws.JwsAuthentication
import net.alexanderkahn.service.commons.firebaseauth.jws.filter.config.FirebaseJwsConfig
import net.alexanderkahn.service.commons.model.exception.UnauthenticatedException
import net.alexanderkahn.service.commons.model.response.body.ErrorResponse
import net.alexanderkahn.service.commons.model.response.body.error.ResponseError
import net.alexanderkahn.service.commons.model.response.body.meta.ObjectResponseMeta
import net.alexanderkahn.service.commons.model.response.body.meta.ResponseStatus
import org.springframework.http.HttpMethod
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.GenericFilterBean
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwsAuthenticationFilter(
        private val jsonObjectMapper: ObjectMapper,
        private val config: FirebaseJwsConfig
) : GenericFilterBean() {

    val unauthenticatedPathPatterns: Collection<Regex>

    init {
        unauthenticatedPathPatterns = config.unauthenticatedPaths.map { Regex(it) }
    }

    private val tokenAuthenticationService: TokenAuthenticationService = TokenAuthenticationService(config.issuer)
    private val bypassTokenManager: BypassTokenManager = BypassTokenManager(config.bypassToken)

    //TODO: the assumption that we are using the json-api model schema pops up in here. Should be decoupled.
    override fun doFilter(request: ServletRequest, response: ServletResponse, filterChain: FilterChain) {
        try {
            if (request is HttpServletRequest && !request.allowUnauthenticated()) {
                setUserInContext(getUserFromRequest(request))
            }
            filterChain.doFilter(request, response)
        } catch (exception: Exception) {
            val status = ResponseStatus.UNAUTHORIZED
            val payload = ErrorResponse(ObjectResponseMeta(status), ResponseError(UnauthenticatedException(exception.message.orEmpty())))
            (response as? HttpServletResponse)?.apply {
                setStatus(status.statusCode)
                contentType = "application/json"
                characterEncoding = "UTF-8"
                writer.write(jsonObjectMapper.writeValueAsString(payload))
            }
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