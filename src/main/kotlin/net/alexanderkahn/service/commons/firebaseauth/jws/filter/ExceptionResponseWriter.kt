package net.alexanderkahn.service.commons.firebaseauth.jws.filter

import javax.servlet.ServletResponse

interface ExceptionResponseWriter {
    fun writeExceptionResponse(exception: Exception, response: ServletResponse)
}