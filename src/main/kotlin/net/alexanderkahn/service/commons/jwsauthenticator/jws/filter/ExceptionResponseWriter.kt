package net.alexanderkahn.service.commons.jwsauthenticator.jws.filter

import javax.servlet.ServletResponse

interface ExceptionResponseWriter {
    fun writeExceptionResponse(exception: Exception, response: ServletResponse)
}